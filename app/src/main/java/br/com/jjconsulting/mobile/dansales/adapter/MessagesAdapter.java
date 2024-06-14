package br.com.jjconsulting.mobile.dansales.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import br.com.jjconsulting.mobile.dansales.R;
import br.com.jjconsulting.mobile.dansales.database.MessageDao;
import br.com.jjconsulting.mobile.dansales.model.Cliente;
import br.com.jjconsulting.mobile.dansales.model.Message;
import br.com.jjconsulting.mobile.dansales.util.ClienteUtils;
import br.com.jjconsulting.mobile.dansales.util.Config;
import br.com.jjconsulting.mobile.dansales.util.MessageUtils;
import br.com.jjconsulting.mobile.dansales.util.PlanoCampoUtils;
import br.com.jjconsulting.mobile.jjlib.util.FormatUtils;
import br.com.jjconsulting.mobile.jjlib.util.LogUser;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.ViewHolder> {

    private static final int ITEM = 0;
    private static final int LOADING = 1;
    private List<Message> mMessages;
    private boolean finishPagination;
    private boolean isLoadingAdded = false;
    private Context mContext;

    public MessagesAdapter(List<Message> messages, Context context) {
        mMessages = messages;

        if (mMessages.size() < Config.SIZE_PAGE) {
            finishPagination = true;
        }

        mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View messageView;

        switch (viewType) {
            case ITEM:
                messageView = inflater.inflate(R.layout.item_message, parent, false);
                messageView.setId(viewType);
                break;
            case LOADING:
                messageView = inflater.inflate(R.layout.item_progress, parent, false);
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
                Message message = mMessages.get(position);
                holder.mTitleTextView.setText(message.getTitle());

                if(message.isRead()){
                    holder.mTitleTextView.setTypeface(null, Typeface.NORMAL);
                } else {
                    holder.mTitleTextView.setTypeface(null, Typeface.BOLD);
                }

                try{
                    holder.mVigenciaTextView.setText(FormatUtils.toDefaultDateHoursBrazilianFormat(FormatUtils.toDate(message.getDate())));
                }catch (Exception ex){
                    LogUser.log(ex.toString());
                }

                holder.mIconMessageImageView.setImageDrawable(mContext.
                        getResources().getDrawable(MessageUtils.getIcon(message.getType(), message.isRead())));

                holder.mBackgroundIconMessageLinearLayout.setBackground(
                        mContext.getResources().getDrawable(MessageUtils.getBackgroundIcon(message.isRead())));

                if(message.getAttachMessage() != null && getMessages().get(position).getAttachMessage().size() > 0){
                    holder.mAttachImageView.setVisibility(View.VISIBLE);
                } else {
                    holder.mAttachImageView.setVisibility(View.GONE);
                }


                break;
            case LOADING:

                break;
        }
    }

    @Override
    public int getItemViewType(int position) {
        return (position == mMessages.size() - 1 && isLoadingAdded) ? LOADING : ITEM;
    }

    @Override
    public int getItemCount() {
        return mMessages.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView mTitleTextView;
        private TextView mVigenciaTextView;
        private ImageView mAttachImageView;
        private ImageView mIconMessageImageView;
        private LinearLayout mBackgroundIconMessageLinearLayout;

        public ViewHolder(View itemView) {
            super(itemView);

            switch (itemView.getId()) {
                case ITEM:
                    mTitleTextView = itemView.findViewById(R.id.title_message_text_view);
                    mVigenciaTextView = itemView.findViewById(R.id.date_message_text_view);
                    mAttachImageView = itemView.findViewById(R.id.attach_image_view);
                    mIconMessageImageView = itemView.findViewById(R.id.message_type_image_view);
                    mBackgroundIconMessageLinearLayout = itemView.findViewById(R.id.message_type_linear_layout);
                    break;
            }
        }
    }

    public void updateItem(Message message, int index){
        mMessages.set(index, message);
        notifyDataSetChanged();
    }

    public void updateData(List<Message> listMessages) {
        if (listMessages.size() == 0 || listMessages.size() < Config.SIZE_PAGE) {
            finishPagination = true;
        } else {
            finishPagination = false;
        }

        mMessages.addAll(listMessages);

        notifyDataSetChanged();
        LogUser.log(Config.TAG, "Pagination - listMessages size: " + mMessages.size() + " - page size: " + Config.SIZE_PAGE + " - finishPagination: " + finishPagination);
    }

    public void resetData() {
        mMessages.clear();
        finishPagination = false;
    }

    public boolean isFinishPagination() {
        return finishPagination;
    }

    public void setFinishPagination(boolean finishPagination) {
        this.finishPagination = finishPagination;
    }

    public List<Message> getMessages() {
        return mMessages;
    }

    public void add(Message message) {
        mMessages.add(message);
        notifyItemInserted(mMessages.size() - 1);
    }

    public void addLoadingFooter() {
        isLoadingAdded = true;
        add(new Message());
    }

    public void removeLoadingFooter() {
        isLoadingAdded = false;
        int position = mMessages.size() - 1;
        Message item = getItem(position);
        if (item != null) {
            mMessages.remove(position);
            notifyItemRemoved(position);
        }
    }

    public Message getItem(int position) {
        return mMessages.get(position);
    }


}
