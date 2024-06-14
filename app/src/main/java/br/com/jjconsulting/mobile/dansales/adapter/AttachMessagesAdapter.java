package br.com.jjconsulting.mobile.dansales.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.ibm.watson.developer_cloud.assistant.v2.model.MessageInput;

import java.util.ArrayList;
import java.util.List;

import br.com.jjconsulting.mobile.dansales.R;
import br.com.jjconsulting.mobile.dansales.model.AttachMessage;
import br.com.jjconsulting.mobile.dansales.model.Message;
import br.com.jjconsulting.mobile.dansales.util.Config;
import br.com.jjconsulting.mobile.dansales.util.ManagerAttachment;
import br.com.jjconsulting.mobile.dansales.util.MessageUtils;
import br.com.jjconsulting.mobile.dansales.util.TMessageType;
import br.com.jjconsulting.mobile.jjlib.util.FormatUtils;
import br.com.jjconsulting.mobile.jjlib.util.LogUser;

public class AttachMessagesAdapter extends RecyclerView.Adapter<AttachMessagesAdapter.ViewHolder> {

    private static final int ITEM = 0;
    private List<AttachMessage> mAttachMessage;
    private Context mContext;

    public AttachMessagesAdapter(List<AttachMessage> attachMessage, Context context) {
        mAttachMessage = attachMessage;
        mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View messageView;

        switch (viewType) {
            case ITEM:
                messageView = inflater.inflate(R.layout.item_attach_message, parent, false);
                messageView.setId(viewType);
                break;
            default:
                messageView = null;
                break;
        }

        return new ViewHolder(messageView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        switch (getItemViewType(position)) {

            case ITEM:
                AttachMessage message = mAttachMessage.get(position);
                holder.mTitleTextView.setText(message.getName());


                Drawable backgroundAttachment = ManagerAttachment.getBackgroundFileExtension(mContext, message.getName());

                if(backgroundAttachment == null){
                    holder.mVideoLinearLayout.setVisibility(View.GONE);
                } else {
                    holder.mVideoLinearLayout.setVisibility(View.VISIBLE);
                    holder.mVideoLinearLayout.setImageDrawable(backgroundAttachment);
                }

                break;
        }
    }

    @Override
    public int getItemViewType(int position) {
        return ITEM;
    }

    @Override
    public int getItemCount() {
        return mAttachMessage.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView mTitleTextView;
        private ImageView mVideoLinearLayout;

        public ViewHolder(View itemView) {
            super(itemView);

            switch (itemView.getId()) {
                case ITEM:
                    mTitleTextView = itemView.findViewById(R.id.attach_message_text_view);
                    mVideoLinearLayout = itemView.findViewById(R.id.attach_message_video_image_view);
                    break;
            }
        }
    }

    public void updateData(List<AttachMessage> listMessages) {
        if(listMessages != null) {
            mAttachMessage = new ArrayList<>();
            mAttachMessage.addAll(listMessages);
            notifyDataSetChanged();
        }
    }

    public void resetData() {
        mAttachMessage.clear();
    }

    public List<AttachMessage> getMessages() {
        return mAttachMessage;
    }

    public void add(AttachMessage message) {
        mAttachMessage.add(message);
        notifyItemInserted(mAttachMessage.size() - 1);
    }
    public AttachMessage getItem(int position) {
        return mAttachMessage.get(position);
    }


}
