package br.com.jjconsulting.mobile.dansales.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import br.com.jjconsulting.mobile.dansales.R;
import br.com.jjconsulting.mobile.dansales.model.MessageChat;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {

    private Context mContext;
    private ArrayList<MessageChat> mMessageChat;

    public ChatAdapter(Context context, ArrayList<MessageChat> messageChat) {
        mContext = context;
        mMessageChat = messageChat;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_chat, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        MessageChat messageChat = mMessageChat.get(position);

        if (messageChat.isTypeUser()) {
            holder.mUserleftTextView.setText(messageChat.getMessage());
            holder.mUserDataleftTextView.setText(messageChat.getDate());
        } else {
            holder.mBotRightTextView.setText(messageChat.getMessage());
            holder.mBotDataRightTextView.setText(messageChat.getDate());
        }

        holder.mUserLeftViewGroup.setVisibility(messageChat.isTypeUser() ? View.VISIBLE : View.GONE);
        holder.mBotRightViewGroup.setVisibility(messageChat.isTypeUser() ? View.GONE : View.VISIBLE);

    }

    @Override
    public int getItemCount() {
        return mMessageChat.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView mUserleftTextView;
        private TextView mBotRightTextView;
        private TextView mUserDataleftTextView;
        private TextView mBotDataRightTextView;
        private ViewGroup mBotRightViewGroup;
        private ViewGroup mUserLeftViewGroup;

        public ViewHolder(View itemView) {
            super(itemView);

            mUserleftTextView = itemView.findViewById(R.id.user_left_text_view);
            mBotRightTextView = itemView.findViewById(R.id.bot_right_text_view);
            mUserDataleftTextView = itemView.findViewById(R.id.data_user_left_text_view);
            mBotDataRightTextView = itemView.findViewById(R.id.data_bot_right_text_view);
            mBotRightViewGroup = itemView.findViewById(R.id.bot_right_linear_layout);
            mUserLeftViewGroup = itemView.findViewById(R.id.user_left_linear_layout);

        }
    }
}
