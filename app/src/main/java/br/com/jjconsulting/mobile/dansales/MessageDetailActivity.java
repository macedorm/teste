package br.com.jjconsulting.mobile.dansales;

import android.content.Context;
import android.content.Intent;

import androidx.fragment.app.Fragment;

import com.google.gson.Gson;

import br.com.jjconsulting.mobile.dansales.database.MessageDao;
import br.com.jjconsulting.mobile.dansales.model.Message;
import br.com.jjconsulting.mobile.jjlib.SingleFragmentActivity;

public class MessageDetailActivity extends SingleFragmentActivity {

    private static final String ARG_MESSAGE = "objeto_message";

    private  Message message;

    public static Intent newIntent(Context context, Message message) {
        Intent intent = new Intent(context, MessageDetailActivity.class);
        Gson gson = new Gson();
        intent.putExtra(ARG_MESSAGE, gson.toJson(message));
        return intent;
    }

    @Override
    protected Fragment createFragment() {

        getSupportActionBar().setTitle(getString(R.string.title_screen_message));

        Intent intent = getIntent();
        if (intent == null) {
            throw new RuntimeException("Must provide arguments");
        }

        Gson gson = new Gson();
        message =  gson.fromJson(getIntent().getStringExtra(ARG_MESSAGE), Message.class);

        if(!message.isRead()){
            MessageDao messageDao = new MessageDao(this);
            messageDao.setMessagemLida(message);
        }

        return MessageDetailFragment.newInstance(message);
    }

    @Override
    protected boolean useOnBackPressedInUpNavigation() {
        return true;
    }
}




