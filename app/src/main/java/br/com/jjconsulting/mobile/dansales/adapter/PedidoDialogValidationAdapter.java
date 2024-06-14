package br.com.jjconsulting.mobile.dansales.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import br.com.jjconsulting.mobile.dansales.R;
import br.com.jjconsulting.mobile.dansales.data.ValidationDan;
import br.com.jjconsulting.mobile.dansales.data.ValidationMessage;
import br.com.jjconsulting.mobile.dansales.data.ValidationMessageType;

public class PedidoDialogValidationAdapter extends RecyclerView.Adapter<PedidoDialogValidationAdapter.ViewHolder> {

    private static final int ITEM = 0;
    private static final int LOADING = 1;

    private Context mContext;
    private ValidationDan mValidation;
    private boolean isLoadingAdded = false;

    public PedidoDialogValidationAdapter(Context context, ValidationDan validation) {
        mContext = context;
        mValidation = validation;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view;
        switch (viewType) {
            case ITEM:
                view = inflater.inflate(R.layout.item_ped_dialog_validation, parent,
                        false);
                view.setId(viewType);
                break;
            case LOADING:
                view = inflater.inflate(R.layout.item_progress, parent,
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
                holder.mDescricaoErroTextView.setText(mValidation.getMessage().get(position).getMessage());
                setTypeIconImageResourceId(mValidation.getMessage().get(position).getType(), holder.mIconImageView);

                break;
        }
    }

    @Override
    public int getItemCount() {
        return mValidation.getMessage().size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView mDescricaoErroTextView;
        private ImageView mIconImageView;

        public ViewHolder(View itemView) {
            super(itemView);
            mDescricaoErroTextView = itemView.findViewById(R.id.item_ped_dialog_descricao_text_view);
            mIconImageView = itemView.findViewById(R.id.item_ped_dialog_icon_image_view);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return (position == mValidation.getMessage().size() - 1 && isLoadingAdded) ? LOADING : ITEM;
    }

    public void add(ValidationMessage message) {
        mValidation.getMessage().add(message);
        notifyItemInserted(mValidation.getMessage().size() - 1);
    }

    public void addLoadingFooter() {
        isLoadingAdded = true;
        add(new ValidationMessage());
    }

    public void removeLoadingFooter() {
        isLoadingAdded = false;
        int position = mValidation.getMessage().size() - 1;
        ValidationMessage item = getItem(position);
        if (item != null) {
            mValidation.getMessage().remove(position);
            notifyItemRemoved(position);
        }
    }

    public ValidationMessage getItem(int position) {
        return mValidation.getMessage().get(position);
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
