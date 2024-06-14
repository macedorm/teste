package br.com.jjconsulting.mobile.dansales;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Browser;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


import java.util.ArrayList;

import br.com.jjconsulting.mobile.dansales.adapter.AttachMessagesAdapter;
import br.com.jjconsulting.mobile.dansales.base.BaseFragment;
import br.com.jjconsulting.mobile.dansales.model.AttachMessage;
import br.com.jjconsulting.mobile.dansales.model.Message;
import br.com.jjconsulting.mobile.dansales.util.CustomAPI;
import br.com.jjconsulting.mobile.dansales.util.ManagerAttachment;
import br.com.jjconsulting.mobile.dansales.util.UserInfo;
import br.com.jjconsulting.mobile.jjlib.base.ItemClickSupport;
import br.com.jjconsulting.mobile.jjlib.util.Cript;
import br.com.jjconsulting.mobile.jjlib.util.FormatUtils;
import br.com.jjconsulting.mobile.jjlib.util.LogUser;


public class MessageDetailFragment extends BaseFragment {


    private RecyclerView mAttachMessageRecyclerView;

    private TextView mSenderMessageTextView;
    private TextView mTitleMessageTextView;
    private TextView mDateMessageTextView;
    private TextView mBodyMessageTextView;

    private  Message message;


    public static MessageDetailFragment newInstance(Message message) {
      MessageDetailFragment messageDetailFragment = new MessageDetailFragment();
        messageDetailFragment.setMessage(message);
        return messageDetailFragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.activity_message_detail, container, false);
        super.onCreate(savedInstanceState);

        try {

            mSenderMessageTextView = view.findViewById(R.id.sender_message_text_view);
            mSenderMessageTextView.setText(message.getSender());

            mTitleMessageTextView = view.findViewById(R.id.message_title_text_view);
            mTitleMessageTextView.setText(message.getTitle());

            mDateMessageTextView = view.findViewById(R.id.message_date_text_view);

            try {
                mDateMessageTextView.setText(FormatUtils.toDefaultDateHoursBrazilianFormat(FormatUtils.toDate(message.getDate())));
            } catch (Exception ex) {
                LogUser.log(ex.toString());
            }

            mBodyMessageTextView = view.findViewById(R.id.body_message_text_view);
            mBodyMessageTextView.setText(message.getBody());

            mAttachMessageRecyclerView = view.findViewById(R.id.attach_message_recycler_view);
            mAttachMessageRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            mAttachMessageRecyclerView.setHasFixedSize(true);

            AttachMessagesAdapter attachMessageAdapter = new AttachMessagesAdapter(new ArrayList<>(), getContext());
            mAttachMessageRecyclerView.setAdapter(attachMessageAdapter);

            attachMessageAdapter.updateData(message.getAttachMessage());

            ItemClickSupport.addTo(mAttachMessageRecyclerView).setOnItemClickListener(
                    (recyclerView, position, v) -> {
                        try {

                            AttachMessage attachMessage = attachMessageAdapter.getItem(position);
                            String IdMessage = Cript.getEncryptReportPortal("KEY" + attachMessage.getIdAttach());
                            String URL = BuildConfig.URL_SITE + "/" +  CustomAPI.API_ATTACH + IdMessage;

                            ManagerAttachment managerAttachment = new ManagerAttachment(getContext());
                            managerAttachment.checkAndOpenFile(URL, attachMessage.getName(), IdMessage, getContext());

                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    });

        } catch (Exception ex){
            LogUser.log(ex.toString());
            getActivity().finish();
        }
        return view;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }


}

