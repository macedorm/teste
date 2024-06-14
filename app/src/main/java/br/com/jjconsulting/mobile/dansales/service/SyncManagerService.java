package br.com.jjconsulting.mobile.dansales.service;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import br.com.jjconsulting.mobile.dansales.BuildConfig;
import br.com.jjconsulting.mobile.dansales.R;
import br.com.jjconsulting.mobile.dansales.database.UsuarioDao;
import br.com.jjconsulting.mobile.dansales.model.Login;
import br.com.jjconsulting.mobile.dansales.model.Usuario;
import br.com.jjconsulting.mobile.dansales.util.FirebaseUtils;
import br.com.jjconsulting.mobile.dansales.util.UserInfo;
import br.com.jjconsulting.mobile.jjlib.OnUpdateChangeScreen;
import br.com.jjconsulting.mobile.jjlib.syncData.SyncDataManager;
import br.com.jjconsulting.mobile.jjlib.syncData.model.ConfigUserSync;
import br.com.jjconsulting.mobile.jjlib.util.JJSDK;
import br.com.jjconsulting.mobile.jjlib.util.LogUser;
import br.com.jjconsulting.mobile.jjlib.util.TextUtils;

@TargetApi(23)
public class SyncManagerService extends JobService {

    private final static String CHANNEL_ID = "NOTIFICATION_SYNC";
    private final static int NOTIFICATION_ID = 998987;

    private SyncDataManager mManagerSyncData;
    private NotificationCompat.Builder mNotificationBuilder;
    private NotificationManager mNotificationManager;

    @SuppressLint("SpecifyJobSchedulerIdRange")
    @Override
    public boolean onStartJob(JobParameters params) {
            LogUser.log("AUTO-SYNC: inicio");

            AutoStartSyncService.scheduleAutoSync(this, true);

            JJSDK.initializeAuthConnection(this, BuildConfig.URL_API, BuildConfig.USER, BuildConfig.USERKEY, BuildConfig.DATABASE_NAME, BuildConfig.VERSION_CODE);

            UserInfo userInfo = new UserInfo();

            if(userInfo.getUserInfo(this) == null || TextUtils.isNullOrEmpty(userInfo.getUserInfo(this).getUserId())){
                LogUser.log("AUTO-SYNC: Interrompido, usuário não logado");
            } else {

                ConfigUserSync configUserSync = new ConfigUserSync(BuildConfig.DATABASE_VERSION, BuildConfig.DATABASE_NAME);
                configUserSync.setIdUser(userInfo.getUserInfo(this).getUserId());
                configUserSync.setVersion(BuildConfig.VERSION_NAME);
                configUserSync.setToken(userInfo.getUserInfo(this).getToken());
                configUserSync.setUrl(JJSDK.getHost(this));

                mManagerSyncData = new SyncDataManager(this, configUserSync, new OnUpdateChangeScreen() {
                    @Override
                    public void onPreparation() {
                        LogUser.log("AUTO-SYNC: onPreparation");
                    }

                    @Override
                    public void onStart() {
                        LogUser.log("AUTO-SYNC: onStart");
                    }

                    @Override
                    public void onUpdateNotAvailabe() {
                        LogUser.log("AUTO-SYNC: onUpdateNotAvailabe");
                        if (mNotificationManager != null)
                            mNotificationManager.cancel(NOTIFICATION_ID);
                    }

                    @Override
                    public void onFinish(int totalRow, long timeTotalUpdate) {
                        LogUser.log(totalRow + " - " + timeTotalUpdate);
                        String title = getString(R.string.sync_result) + " " + totalRow + " / " + totalRow;
                        mNotificationBuilder.setContentTitle(title);
                        mNotificationBuilder.setContentText("");
                        mNotificationBuilder.setProgress(100, 100, false);

                        mNotificationManager.notify(NOTIFICATION_ID, mNotificationBuilder.build());
                        Bundle params = new Bundle();
                        params.putString("Result", "Success");
                        params.putString("TotalRecords", String.valueOf(totalRow));
                        params.putString("TotalSeconds", String.valueOf(timeTotalUpdate));
                        params.putBoolean("AutoSync", false);

                        FirebaseUtils.sendEvent(SyncManagerService.this, "sync_data", params);

                    }

                    @Override
                    public void onError(String message) {
                        LogUser.log(message);
                        if (mNotificationManager != null)
                            mNotificationManager.cancel(NOTIFICATION_ID);

                        Bundle params = new Bundle();
                        params.putString("Result", "Error");
                        params.putString("Message", message);
                        params.putBoolean("AutoSync", false);

                        FirebaseUtils.sendEvent(SyncManagerService.this, "sync_data", params);
                    }

                    @Override
                    public void onErrorConnection() {
                        LogUser.log("AUTO-SYNC: onErrorConnection");
                        if (mNotificationManager != null)
                            mNotificationManager.cancel(NOTIFICATION_ID);
                    }

                    @Override
                    public void onCancel() {
                        LogUser.log("AUTO-SYNC: onCancel");
                        if (mNotificationManager != null)
                            mNotificationManager.cancel(NOTIFICATION_ID);
                    }

                    @Override
                    public void onProgress(int totalRow, int currentRow, int currentProgress) {
                        LogUser.log(totalRow + " - " + currentRow + " - " + currentProgress);

                        if (currentProgress > 0) {

                            if (mNotificationBuilder == null) {
                                createNotification();
                            }

                            mNotificationBuilder.setContentText("" + currentProgress + "%");
                            mNotificationBuilder.setProgress(100, currentProgress, false);
                            mNotificationManager.notify(NOTIFICATION_ID, mNotificationBuilder.build());
                        }
                    }

                    @Override
                    public void onProgressStatus(String message) {
                        LogUser.log(message);
                    }
                });
                userInfo = new UserInfo();
                Login login = userInfo.getUserInfo(this);

                if (!SyncDataManager.isProgress()) {
                    if (login != null) {

                        SyncDataManager syncDataManager = new SyncDataManager(this, configUserSync);
                        String syncInfo = syncDataManager.loadInfoSync(this);

                        if (TextUtils.isNullOrEmpty(syncInfo)) {
                            LogUser.log("AUTO-SYNC: Primeiro Sincronismo nao realizado");
                            SyncDataManager.setIsProgress(false);
                        } else {

                            UsuarioDao usuarioDao = new UsuarioDao(this);
                            Usuario usuario = usuarioDao.get(login.getUserId());

                            if (usuario != null && usuario.getPerfil() != null && usuario.getPerfil().isPermiteAutoSync()) {
                                LogUser.log("AUTO-SYNC: inicio sincronismo");

                                mManagerSyncData.setCancel(false);
                                mManagerSyncData.connectionStart();
                            } else {
                                LogUser.log("AUTO-SYNC: isPermiteAutoSync false or user null");
                                SyncDataManager.setIsProgress(false);
                            }
                        }
                    } else {
                        LogUser.log("AUTO-SYNC: Usuario não logado");
                        SyncDataManager.setIsProgress(false);
                    }
                } else {
                    LogUser.log("AUTO-SYNC: Not initialize (Progress true) ");
                }

            }


        return true;
    }

    @Override
        public boolean onStopJob(JobParameters params) {
        return true;
    }

    public static void clearNotification(Context context){
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(NOTIFICATION_ID);
    }

    public void createNotification(){
        mNotificationBuilder = new NotificationCompat.Builder(SyncManagerService.this, CHANNEL_ID);
        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        mNotificationBuilder.setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.ic_notification)
                .setProgress(100,0,false)
                .setPriority(Notification.PRIORITY_MAX)
                .setContentTitle(getString(R.string.title_auto_sync_data))
                .setVibrate(new long[]{ 0 })
                .setSound(null)
                .setContentText("");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_ID, NotificationManager.IMPORTANCE_HIGH);
            channel.setSound(null, null);
            channel.enableLights(false);
            channel.enableVibration(true);
            channel.setVibrationPattern(new long[]{ 0 });
            
            mNotificationManager.createNotificationChannel(channel);
            mNotificationBuilder.setChannelId(CHANNEL_ID);
        }

        mNotificationManager.notify(NOTIFICATION_ID, mNotificationBuilder.build());
    }
}
