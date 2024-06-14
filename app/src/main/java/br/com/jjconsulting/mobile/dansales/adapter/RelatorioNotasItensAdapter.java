package br.com.jjconsulting.mobile.dansales.adapter;

import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import br.com.jjconsulting.mobile.dansales.R;
import br.com.jjconsulting.mobile.dansales.model.RelatorioNotasItem;
import br.com.jjconsulting.mobile.dansales.util.Config;
import br.com.jjconsulting.mobile.jjlib.util.FormatUtils;
import br.com.jjconsulting.mobile.jjlib.util.LogUser;
import br.com.jjconsulting.mobile.jjlib.util.TextUtils;

public class RelatorioNotasItensAdapter extends RecyclerView.Adapter<RelatorioNotasItensAdapter.ViewHolder> {

    private static final int ITEM = 0;
    private static final int LOADING = 1;
    private List<RelatorioNotasItem> mSKU;
    private boolean finishPagination;
    private boolean isLoadingAdded = false;

    public RelatorioNotasItensAdapter(List<RelatorioNotasItem> sku) {
        mSKU = sku;

        if (mSKU.size() < Config.SIZE_PAGE) {
            finishPagination = true;
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View skuView;

        switch (viewType) {
            case ITEM:
                skuView = inflater.inflate(R.layout.item_relatorio_nota_detail_itens, parent, false);
                skuView.setId(viewType);
                break;
            case LOADING:
                skuView = inflater.inflate(R.layout.item_progress, parent, false);
                skuView.setId(viewType);
                break;
            default:
                skuView = null;
                break;
        }

        return new ViewHolder(skuView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        switch (getItemViewType(position)) {
            case ITEM:
                RelatorioNotasItem sku = mSKU.get(position);
                holder.mNomeTextView.setText(sku.getNome());
                holder.mSKUTextView.setText(TextUtils.trimLeadingZeros(sku.getId()));
                holder.mQTDTextView.setText(FormatUtils.toStringDoubleFormat(String.valueOf(sku.getQtd())));
                holder.mICMSSTTextView.setText(FormatUtils.toBrazilianRealCurrency(sku.getIcmsSt()));
                holder.mVLRICMSTextView.setText(FormatUtils.toBrazilianRealCurrency(sku.getVlrIcms()));
                holder.mVLRIPITextView.setText(FormatUtils.toBrazilianRealCurrency(sku.getVlrIpi()));
                holder.mVLRDESCTextView.setText(FormatUtils.toBrazilianRealCurrency(sku.getVlrDesc()));
                holder.mVLRUniTextView.setText(FormatUtils.toBrazilianRealCurrency(sku.getVlrUnitario()));
                holder.mVLRtotalTextView.setText(FormatUtils.toBrazilianRealCurrency(sku.getValorTotal()));
                break;
            case LOADING:

                break;
        }
    }

    @Override
    public int getItemViewType(int position) {
        return (position == mSKU.size() - 1 && isLoadingAdded) ? LOADING : ITEM;
    }

    @Override
    public int getItemCount() {
        return mSKU.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView mNomeTextView;
        private TextView mSKUTextView;
        private TextView mQTDTextView;
        private TextView mICMSSTTextView;
        private TextView mVLRICMSTextView;
        private TextView mVLRIPITextView;
        private TextView mVLRDESCTextView;
        private TextView mVLRUniTextView;
        private TextView mVLRtotalTextView;

        public ViewHolder(View itemView) {
            super(itemView);

            switch (itemView.getId()) {
                case ITEM:
                    mNomeTextView = itemView.findViewById(R.id.item_relatorio_nota_detail_nome_text_view);
                    mSKUTextView = itemView.findViewById(R.id.item_relatorio_nota_detail_sku_text_view);
                    mQTDTextView = itemView.findViewById(R.id.item_relatorio_nota_detail_qtd_text_view);
                    mICMSSTTextView = itemView.findViewById(R.id.item_relatorio_nota_detail_icmsst_text_view);
                    mVLRICMSTextView = itemView.findViewById(R.id.item_relatorio_nota_detail_vlricms_text_view);
                    mVLRIPITextView = itemView.findViewById(R.id.item_relatorio_nota_detail_vlripi_text_view);
                    mVLRDESCTextView = itemView.findViewById(R.id.item_relatorio_nota_detail_vlrdesc_text_view);
                    mVLRUniTextView = itemView.findViewById(R.id.item_relatorio_nota_detail_vlruni_text_view);
                    mVLRtotalTextView = itemView.findViewById(R.id.item_relatorio_nota_detail_vlrtotal_text_view);

                    break;
            }
        }
    }

    public void updateData(List<RelatorioNotasItem> listSKU) {
        if (listSKU.size() == 0 || listSKU.size() < Config.SIZE_PAGE) {
            finishPagination = true;
        } else {
            finishPagination = false;

        }
        mSKU.addAll(listSKU);

        notifyDataSetChanged();
        LogUser.log(Config.TAG, "Pagination - listSKU size: " + mSKU.size() + " - page size: " + Config.SIZE_PAGE + " - finishPagination: " + finishPagination);
    }

    public void resetData() {
        mSKU.clear();
        finishPagination = false;
    }

    public boolean isFinishPagination() {
        return finishPagination;
    }

    public void setFinishPagination(boolean finishPagination) {
        this.finishPagination = finishPagination;
    }

    public List<RelatorioNotasItem> getSKU() {
        return mSKU;
    }

    public void add(RelatorioNotasItem sku) {
        mSKU.add(sku);
        notifyItemInserted(mSKU.size() - 1);
    }

    public void addLoadingFooter() {
        isLoadingAdded = true;
        add(new RelatorioNotasItem());
    }

    public void removeLoadingFooter() {
        isLoadingAdded = false;
        int position = mSKU.size() - 1;
        RelatorioNotasItem item = getItem(position);
        if (item != null) {
            mSKU.remove(position);
            notifyItemRemoved(position);
        }
    }

    public RelatorioNotasItem getItem(int position) {
        return mSKU.get(position);
    }
}
