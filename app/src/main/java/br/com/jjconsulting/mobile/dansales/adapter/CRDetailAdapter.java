package br.com.jjconsulting.mobile.dansales.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import br.com.jjconsulting.mobile.dansales.R;
import br.com.jjconsulting.mobile.dansales.model.ItensListSortimento;
import br.com.jjconsulting.mobile.dansales.util.Config;
import br.com.jjconsulting.mobile.dansales.util.TSortimento;
import br.com.jjconsulting.mobile.jjlib.util.FormatUtils;
import br.com.jjconsulting.mobile.jjlib.util.LogUser;
import br.com.jjconsulting.mobile.jjlib.util.TextUtils;

public class CRDetailAdapter extends RecyclerView.Adapter<CRDetailAdapter.ViewHolder> {

    private static final int ITEM = 0;
    private static final int LOADING = 1;
    private List<ItensListSortimento> mItensListSortimento;
    private boolean finishPagination;
    private boolean isLoadingAdded = false;
    private Context mContext;

    public CRDetailAdapter(List<ItensListSortimento> itensListSortimento, Context context) {
        mItensListSortimento = itensListSortimento;

        if (mItensListSortimento.size() < Config.SIZE_PAGE) {
            finishPagination = true;
        }

        mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View layoutView;

        switch (viewType) {
            case ITEM:
                layoutView = inflater.inflate(R.layout.item_cr_detail, parent, false);
                layoutView.setId(viewType);
                break;
            case LOADING:
                layoutView = inflater.inflate(R.layout.item_progress, parent, false);
                layoutView.setId(viewType);
                break;
            default:
                layoutView = null;
                break;
        }

        return new ViewHolder(layoutView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        switch (getItemViewType(position)) {

            case ITEM:
                ItensListSortimento itensListSortimento = mItensListSortimento.get(position);
                holder.mNomeTextView.setText(itensListSortimento.getName());
                holder.mCodigoTextView.setText(String.valueOf(itensListSortimento.getCod()));
                holder.mSKUTextView.setText(String.valueOf(Integer.parseInt(itensListSortimento.getSKU())));
                holder.mPesoTextView.setText(itensListSortimento.getPeso() + " kg");

                holder.mSortimentoTextView.setBackgroundResource(R.drawable.textview_rounded_corners);
                GradientDrawable drawable = (GradientDrawable) holder.mSortimentoTextView.getBackground();
                drawable.setColor(TSortimento.getColorSortimento(mContext, itensListSortimento.getStatus()));

                holder.mSortimentoTextView.setBackground(drawable);
                holder.mSortimentoTextView.setText(TSortimento.getDescriptionSortimento(mContext, itensListSortimento.getStatus()));
                holder.mSortimentoTextView.setVisibility(View.VISIBLE);

                break;
            case LOADING:

                break;
        }
    }

    @Override
    public int getItemViewType(int position) {
        return (position == mItensListSortimento.size() - 1 && isLoadingAdded) ? LOADING : ITEM;
    }

    @Override
    public int getItemCount() {
        return mItensListSortimento.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView mNomeTextView;
        private TextView mCodigoTextView;
        private TextView mSKUTextView;
        private TextView mPesoTextView;
        private TextView mSortimentoTextView;

        public ViewHolder(View itemView) {
            super(itemView);

            switch (itemView.getId()) {
                case ITEM:
                    mNomeTextView = itemView.findViewById(R.id.nome_produto_text_view);
                    mCodigoTextView = itemView.findViewById(R.id.id_text_view);
                    mSKUTextView = itemView.findViewById(R.id.sku_produto_text_view);
                    mPesoTextView = itemView.findViewById(R.id.item_prod_peso_text_view);
                    mSortimentoTextView = itemView.findViewById(R.id.item_sortimento_text_view);
                    break;
            }
        }
    }

    public void updateData(List<ItensListSortimento> listLayouts) {
        if (listLayouts.size() == 0 || listLayouts.size() < Config.SIZE_PAGE) {
            finishPagination = true;
        } else {
            finishPagination = false;
        }

        mItensListSortimento.addAll(listLayouts);
        notifyDataSetChanged();

        LogUser.log(Config.TAG, "Pagination - listLayout size: " + listLayouts.size() + " - page size: " + Config.SIZE_PAGE + " - finishPagination: " + finishPagination);
    }

    public void resetData() {
        mItensListSortimento.clear();
        finishPagination = false;
    }

    public boolean isFinishPagination() {
        return finishPagination;
    }

    public void setFinishPagination(boolean finishPagination) {
        this.finishPagination = finishPagination;
    }

    public List<ItensListSortimento> getLayouts() {
        return mItensListSortimento;
    }

    public void add(ItensListSortimento itensListSortimento) {
        mItensListSortimento.add(itensListSortimento);
        notifyItemInserted(mItensListSortimento.size() - 1);
    }

    public void addLoadingFooter() {
        isLoadingAdded = true;
        add(new ItensListSortimento());
    }

    public void removeLoadingFooter() {
        isLoadingAdded = false;
        int position = mItensListSortimento.size() - 1;
        ItensListSortimento item = getItem(position);
        if (item != null) {
            mItensListSortimento.remove(position);
            notifyItemRemoved(position);
        }
    }

    public ItensListSortimento getItem(int position) {
        return mItensListSortimento.get(position);
    }

}
