package br.com.jjconsulting.mobile.jjlib.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import br.com.jjconsulting.mobile.jjlib.R;
import br.com.jjconsulting.mobile.jjlib.data.ValidationInfo;
import br.com.jjconsulting.mobile.jjlib.data.ValidationMessage;
import br.com.jjconsulting.mobile.jjlib.data.ValidationMessageType;

public class JJDialogValidationAdapter extends RecyclerView.Adapter<JJDialogValidationAdapter.ViewHolder> {

    private static final int ITEM = 0;
    private static final int LOADING = 1;

    private Context mContext;
    private ValidationInfo mValidation;
    private boolean isLoadingAdded = false;

    public JJDialogValidationAdapter(Context context, ValidationInfo validation) {
        mContext = context;
        mValidation = validation;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view;
        switch (viewType) {
            case ITEM:
                view = inflater.inflate(R.layout.jj_item_dialog_validation, parent,
                        false);
                view.setId(viewType);
                break;
            case LOADING:
                view = inflater.inflate(R.layout.jj_item_progress, parent,
                        false);
                view.setId(viewType);
                break;
            default:
                view = null;
                break;
        }

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        switch (getItemViewType(position)) {
            case ITEM:
                holder.mDescricaoErroTextView.setText(mValidation.getMessages().get(position).getMessage());
                setTypeIconImageResourceId(mValidation.getMessages().get(position).getType(), holder.mIconImageView);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return mValidation.getMessages().size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView mDescricaoErroTextView;
        private ImageView mIconImageView;

        public ViewHolder(View itemView) {
            super(itemView);
            mDescricaoErroTextView = itemView.findViewById(R.id.jj_item_dialog_desc_text_view);
            mIconImageView = itemView.findViewById(R.id.jj_item_dialog_icon_image_view);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return (position == mValidation.getMessages().size() - 1 && isLoadingAdded) ? LOADING : ITEM;
    }

    public void add(ValidationMessage message) {
        mValidation.getMessages().add(message);
        notifyItemInserted(mValidation.getMessages().size() - 1);
    }

    public void addLoadingFooter() {
        isLoadingAdded = true;
        add(new ValidationMessage());
    }

    public void removeLoadingFooter() {
        isLoadingAdded = false;
        int position = mValidation.getMessages().size() - 1;
        ValidationMessage item = getItem(position);
        if (item != null) {
            mValidation.getMessages().remove(position);
            notifyItemRemoved(position);
        }
    }

    public ValidationMessage getItem(int position) {
        return mValidation.getMessages().get(position);
    }

    private void setTypeIconImageResourceId(ValidationMessageType type, ImageView icon) {
        switch (type) {
            case ALERT:
                icon.setColorFilter(mContext.getResources().getColor(R.color.alertCollor), android.graphics.PorterDuff.Mode.SRC_ATOP);
                icon.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_error_black_24dp));
                break;
            case ERROR:
                icon.setColorFilter(mContext.getResources().getColor(R.color.errorCollor), android.graphics.PorterDuff.Mode.SRC_ATOP);
                icon.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_cancel_black_24dp));
                break;
            case INFO:
                icon.setColorFilter(mContext.getResources().getColor(R.color.infoCollor), android.graphics.PorterDuff.Mode.SRC_ATOP);
                icon.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_error_black_24dp));
                break;
            case SUCCESS:
                icon.setColorFilter(mContext.getResources().getColor(R.color.sucessCollor), android.graphics.PorterDuff.Mode.SRC_ATOP);
                icon.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_check_circle_black_24dp));
                break;
        }

    }
}
