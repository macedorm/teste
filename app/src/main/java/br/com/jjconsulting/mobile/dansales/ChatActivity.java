package br.com.jjconsulting.mobile.dansales;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.VolleyError;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;
import com.ibm.watson.developer_cloud.assistant.v1.Assistant;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;

import br.com.jjconsulting.mobile.dansales.adapter.ChatAdapter;
import br.com.jjconsulting.mobile.dansales.base.BaseActivity;
import br.com.jjconsulting.mobile.dansales.connectionController.BaseConnection;
import br.com.jjconsulting.mobile.dansales.connectionController.ChatConnection;
import br.com.jjconsulting.mobile.dansales.database.ChatDao;
import br.com.jjconsulting.mobile.dansales.model.MessageChat;
import br.com.jjconsulting.mobile.dansales.service.Current;
import br.com.jjconsulting.mobile.dansales.util.Config;
import br.com.jjconsulting.mobile.dansales.util.ManagerSystemUpdate;
import br.com.jjconsulting.mobile.jjlib.Connection;
import br.com.jjconsulting.mobile.jjlib.model.ValidationLetter;
import br.com.jjconsulting.mobile.jjlib.util.DialogsCustom;
import br.com.jjconsulting.mobile.jjlib.util.FormatUtils;
import br.com.jjconsulting.mobile.jjlib.util.SavePref;
import br.com.jjconsulting.mobile.jjlib.util.TextUtils;

public class ChatActivity extends BaseActivity {

    private ChatDao chatDao;

    private ArrayList<MessageChat> mMessageChat;

    private RecyclerView mChatRecyclerView;
    private ChatAdapter mChatAdpater;
    private LinearLayoutManager mManager;

    private ImageView mSendImageView;

    private EditText mMessageEditText;

    private com.ibm.watson.developer_cloud.assistant.v1.model.Context context = null;

    private MessageChat messageChatLastSend;

    private ChatConnection chatConnection;

    private SavePref savePref;

    public static Intent newIntent(Context context) {
        Intent intent = new Intent(context, ChatActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        setTitle(getString(R.string.title_chat));

        chatDao = new ChatDao(this);

        savePref = new SavePref();
        String messageNotification = savePref.getPref(Config.TAG_MESSAGE_CHAT, Config.TAG, this);

        if(!TextUtils.isNullOrEmpty(messageNotification)){
            savePref.saveSharedPreferences(Config.TAG_MESSAGE_CHAT, Config.TAG, "", this);
        }

        mSendImageView = findViewById(R.id.fab_img);

        mMessageEditText = findViewById(R.id.message_edit_text);
        mChatRecyclerView = findViewById(R.id.list_message_recycler_view);
        mManager = new LinearLayoutManager(this);
        mManager.setReverseLayout(true);
        mChatRecyclerView.setHasFixedSize(true);
        mChatRecyclerView.setLayoutManager(mManager);

        mMessageChat = new ArrayList<>();

        //mMessageChat = chatDao.getAll(Current.getInstance(this).getUsuario().getCodigo());

        mChatAdpater = new ChatAdapter(this, mMessageChat);
        mChatRecyclerView.setAdapter(mChatAdpater);

        mChatAdpater.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                mManager.smoothScrollToPosition(mChatRecyclerView, null, mChatAdpater.getItemCount());
            }
        });

        mSendImageView.setOnClickListener((v) -> {
            if(!TextUtils.isNullOrEmpty(mMessageEditText.getText().toString())){
                String id = getID();
                createNewMessage(true, mMessageEditText.getText().toString());
                sendMessage(id, messageChatLastSend.getMessage());
                mMessageEditText.setText("");
            }
        });

        createConnectionListenerMessage();
    }

    private String getID(){
        String id = "";

        id = savePref.getPref(Config.TAG_MESSAGE_CHAT_ID, Config.TAG, ChatActivity.this);

        if(TextUtils.isNullOrEmpty(id)){
            id = "";
        }
        return id;
    }

    private void saveMessageNotification(String message) {
        MessageChat messageChatNotification = new MessageChat(message, FormatUtils.toDefaultDateAndHourFormat(this, new Date()), false);
        //chatDao.insert(messageChatNotification);
    }

    private void createNewMessage(boolean typeUser, String message) {
        messageChatLastSend = new MessageChat(message, FormatUtils.toDefaultDateAndHourFormat(this, new Date()), typeUser);
        mMessageChat.add(0, messageChatLastSend);
        //chatDao.insert(messageChatLastSend);
        mChatAdpater.notifyItemInserted(0);
        mChatRecyclerView.scrollToPosition(0);
    }

    private void sendMessage(String id, String message) {
       chatConnection.sendMessage(id, message);
    }

    private void createConnectionListenerMessage(){
         chatConnection = new ChatConnection(this, new BaseConnection.ConnectionListener() {
            @Override
            public void onSucess(String response, int typeConnection, InputStreamReader reader, ArrayList<String[]> responseList) {
                try{

                    JSONObject json= (JSONObject) new JSONTokener(response).nextValue();

                    String id = json.getJSONObject("context").getString("conversation_id");

                    json = json.getJSONObject("output");
                    JSONArray jsonArray = json.getJSONArray("text");
                    String[] messages = gson.fromJson(jsonArray.toString(), String[].class);

                    if(!TextUtils.isNullOrEmpty(id)){
                        savePref.saveSharedPreferences(Config.TAG_MESSAGE_CHAT_ID, Config.TAG, id, ChatActivity.this );
                    }

                    for (String messageChat : messages) {
                        runOnUiThread(() -> {

                            String message = messageChat;

                            if (!TextUtils.isNullOrEmpty(message) && message.length() > 2) {
                                message = message.replace("$user.name", Current.getInstance(getBaseContext()).getUsuario().getNome());
                                createNewMessage(false, message);

                            } else {
                                createNewMessage(false, getString(R.string.message_default));
                            }
                        });
                    }

                }catch (Exception ex){
                    createNewMessage(false, getString(R.string.message_default));
                }
            }

            @Override
            public void onError(VolleyError volleyError, int code,  int typeConnection, String response) {
                runOnUiThread(()-> {
                    if(code == Connection.AUTH_FAILURE || code == Connection.SERVER_ERROR){
                        ValidationLetter errorConnection = gson.fromJson(response, ValidationLetter.class);
                        if(ManagerSystemUpdate.isRequiredUpadate(ChatActivity.this, errorConnection.getMessage())){
                            return;
                        }

                        showMessageError(errorConnection.getMessage());
                        return;
                    }

                    dialogsDefault.showDialogQuestion(getString(R.string.message_chat_error), dialogsDefault.DIALOG_TYPE_ERROR,
                        new DialogsCustom.OnClickDialogQuestion() {
                            @Override
                            public void onClickPositive() {
                                sendMessage(getID(), messageChatLastSend.getMessage());
                            }

                            @Override
                            public void onClickNegative() {

                                if(mMessageChat.size() > 0){
                                    chatDao.deleteLastMessage();
                                    mMessageChat.remove(0);
                                    mChatAdpater.notifyItemRemoved(0);
                                    mChatRecyclerView.scrollToPosition(0);
                                }
                            }
                        });
                });
            }
        });
    }
}
