package br.com.jjconsulting.mobile.dansales.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.os.Bundle;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import br.com.jjconsulting.mobile.dansales.R;
import br.com.jjconsulting.mobile.dansales.database.ChatDao;
import br.com.jjconsulting.mobile.dansales.database.MessageDao;
import br.com.jjconsulting.mobile.dansales.kotlin.PedidoTrackingDetailActivity;
import br.com.jjconsulting.mobile.dansales.kotlin.model.PedidoTrackingMessage;
import br.com.jjconsulting.mobile.dansales.model.Message;
import br.com.jjconsulting.mobile.dansales.model.MessageAccess;
import br.com.jjconsulting.mobile.dansales.util.TMessageType;
import br.com.jjconsulting.mobile.jjlib.util.FormatUtils;
import br.com.jjconsulting.mobile.jjlib.util.LogUser;


    public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";

    private NotificationCompat.Builder notificationBuilder;

    public Context context;

    public static Map<String, String> pushChat;

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d(TAG, "Notification Message Data: " + remoteMessage.getData());

        String messageBody = remoteMessage.getNotification().getBody();
        String messageTille = remoteMessage.getNotification().getTitle();
        String messagesound = remoteMessage.getNotification().getSound();

        Log.d(TAG, "Notification Message From: " + remoteMessage.getFrom());
        Log.d(TAG, "Notification Message Title: " + messageTille);
        Log.d(TAG, "Notification Message Body: " + messageBody);
        Log.d(TAG, "Notification Message Sound: " + messagesound);

        createMessage(this, remoteMessage);

        if (remoteMessage.getData() != null && remoteMessage.getData().toString().contains("NumNF")) {
            MyFirebaseMessagingService.pushChat = remoteMessage.getData();
            PendingIntent contentIntent;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
                contentIntent = PendingIntent.getActivity(this, 0,
                        PedidoTrackingDetailActivity.Companion.newIntentPush(this, remoteMessage.getData().get("NumNF"),
                                remoteMessage.getData().get("Serie"), remoteMessage.getData().get("CnpjEmitente")),
                        PendingIntent.FLAG_UPDATE_CURRENT| PendingIntent.FLAG_IMMUTABLE);
            } else {
                contentIntent = PendingIntent.getActivity(this, 0,
                        PedidoTrackingDetailActivity.Companion.newIntentPush(this, remoteMessage.getData().get("NumNF"),
                                remoteMessage.getData().get("Serie"), remoteMessage.getData().get("CnpjEmitente")),
                        PendingIntent.FLAG_UPDATE_CURRENT);
            }

            sendNotification(messageTille, messageBody, contentIntent);
            Intent push = new Intent("push");
            LocalBroadcastManager.getInstance(this).sendBroadcast(push);
        } else {
            sendNotification(messageTille, messageBody, null);
        }
    }

    public void sendNotification(String messageTille, String messageBody, PendingIntent intent) {
        PendingIntent notifyPIntent;

        if (intent == null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                notifyPIntent = PendingIntent.getActivity(getApplicationContext(), 0, new Intent(), PendingIntent.FLAG_IMMUTABLE);
            } else {
                notifyPIntent = PendingIntent.getActivity(getApplicationContext(), 0, new Intent(), 0);
            }
        } else {
            notifyPIntent = intent;
        }

        String channelId = "Dansales";

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.drawable.ic_notification)
                        .setColor(getApplicationContext().getResources().getColor(R.color.colorAccent))
                        .setContentTitle(messageTille)
                        .setContentText(messageBody)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(notifyPIntent)
                        .setWhen(Calendar.getInstance().getTimeInMillis())
                        .setPriority(Notification.PRIORITY_MAX)
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(messageBody));

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(0, notificationBuilder.build());
    }


    static public void createMessage(Context context, RemoteMessage remoteMessage) {
        try {

            MessageDao messageDao = new MessageDao(context);

            Message message = new Message();
            message.setUnidNeg(Current.getInstance(context).getUnidadeNegocio().getCodigo());
            message.setStartDate(FormatUtils.toTextToCompareDateInSQlite(FormatUtils.getDateTimeNow(-1, 0, 0)));
            message.setEndDate(FormatUtils.toTextToCompareDateInSQlite(FormatUtils.getDateTimeNow(2, 0, 0)));
            message.setIdMessage(messageDao.getIDNewMessage());
            message.setObrig(1);
            message.setDtUltAlt(FormatUtils.toTextToCompareDateInSQlite(new Date()));
            message.setDate(FormatUtils.toTextToCompareDateInSQlite(new Date()));
            message.setSender(context.getString(R.string.app_name));
            message.setCodRegFunc(Current.getInstance(context).getUsuario().getCodigo());
            message.setTitle(remoteMessage.getNotification().getTitle());

            if (remoteMessage.getData() != null && remoteMessage.getData().containsKey("Mensagem")) {
                message.setBody(remoteMessage.getData().get("Mensagem"));
            } else {
                message.setTitle(remoteMessage.getNotification().getBody());
            }

            if (remoteMessage.getData() != null && remoteMessage.getData().toString().contains("ID_MENSAGEM")) {
                JSONObject jsonMessage = new JSONObject();
                Set<String> keys = remoteMessage.getData().keySet();

                for (String key : keys) {
                    try {
                        jsonMessage.put(key, JSONObject.wrap(remoteMessage.getData().get(key)));
                        Gson gson = new Gson();
                        message = gson.fromJson(jsonMessage.toString(), Message.class);
                        message.setType(TMessageType.PUSH);
                    } catch (JSONException e) {
                        LogUser.log(e.getMessage());
                    }
                }
            }

            message.setTitle(remoteMessage.getNotification().getTitle());
            MessageAccess messageAccess = new MessageAccess();
            messageAccess.setIdFuncao(message.getCodRegFunc());
            messageAccess.setIdMessage(message.getIdMessage());
            messageAccess.setIdFiltro("1");
            messageAccess.setDtUltAlt(message.getDtUltAlt());
            messageAccess.setDelFlag(message.getDelFlag());

            messageDao.insert(message);
            messageDao.insertAccess(messageAccess);

            if (remoteMessage.getData() != null && remoteMessage.getData().containsKey("NumNF")) {
                message.setBody(remoteMessage.getData().get("Mensagem"));
                message.setObrig(1);

                ChatDao chatDao = new ChatDao(context);
                PedidoTrackingMessage pedidoTrackingMessage = chatDao.convertPedidoTrackingMessage(remoteMessage.getData());
                pedidoTrackingMessage.setId(message.getIdMessage());
                chatDao.insert(pedidoTrackingMessage);
            } else {
                message.setBody(remoteMessage.getNotification().getBody());
            }
        } catch (Exception ex){
            LogUser.log(ex.getMessage());
        }
    }

    static public void pushBackground(Context context, Bundle bundle) {
        Map<String, String> push = new HashMap<>();

        if (bundle != null && bundle.containsKey("NumNF")) {
            push.put("NumNF", bundle.getString("NumNF"));
            push.put("Serie", bundle.getString("Serie"));
            push.put("TipoUsuario", bundle.getString("TipoUsuario"));
            push.put("NomeUsuario", bundle.getString("NomeUsuario"));
            push.put("Mensagem", bundle.getString(" "));
            push.put("CnpjEmitente", bundle.getString("CnpjEmitente"));

            MessageDao messageDao = new MessageDao(context);
            Message message = new Message();
            message.setUnidNeg(Current.getInstance(context).getUnidadeNegocio().getCodigo());
            message.setStartDate(FormatUtils.toTextToCompareDateInSQlite(FormatUtils.getDateTimeNow(-1, 0, 0)));
            message.setEndDate(FormatUtils.toTextToCompareDateInSQlite(FormatUtils.getDateTimeNow(2, 0, 0)));
            message.setIdMessage(messageDao.getIDNewMessage());
            message.setObrig(1);
            message.setDtUltAlt(FormatUtils.toTextToCompareDateInSQlite(new Date()));
            message.setDate(FormatUtils.toTextToCompareDateInSQlite(new Date()));
            message.setSender(context.getString(R.string.app_name));
            message.setCodRegFunc(Current.getInstance(context).getUsuario().getCodigo());
            message.setTitle(push.get("Title"));
            message.setBody(push.get("Mensagem"));

            MessageAccess messageAccess = new MessageAccess();
            messageAccess.setIdFuncao(message.getCodRegFunc());
            messageAccess.setIdMessage(message.getIdMessage());
            messageAccess.setIdFiltro("1");
            messageAccess.setDtUltAlt(message.getDtUltAlt());
            messageAccess.setDelFlag(message.getDelFlag());

            messageDao.insert(message);
            messageDao.insertAccess(messageAccess);

            message.setBody(push.get("Messagem"));
            message.setObrig(1);

            ChatDao chatDao = new ChatDao(context);

            PedidoTrackingMessage pedidoTrackingMessage = chatDao.convertPedidoTrackingMessage(push);
            pedidoTrackingMessage.setId(message.getIdMessage());
            chatDao.insert(pedidoTrackingMessage);

            MyFirebaseMessagingService.pushChat = push;
        }
    }
}
