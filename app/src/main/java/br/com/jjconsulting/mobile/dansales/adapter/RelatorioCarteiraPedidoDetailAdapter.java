package br.com.jjconsulting.mobile.dansales.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import br.com.jjconsulting.mobile.dansales.R;
import br.com.jjconsulting.mobile.dansales.model.RelatorioCarteiraPedidoDetail;
import br.com.jjconsulting.mobile.dansales.util.Config;
import br.com.jjconsulting.mobile.jjlib.util.FormatUtils;
import br.com.jjconsulting.mobile.jjlib.util.LogUser;
import br.com.jjconsulting.mobile.jjlib.util.TextUtils;

public class RelatorioCarteiraPedidoDetailAdapter extends RecyclerView.Adapter<RelatorioCarteiraPedidoDetailAdapter.ViewHolder> {

    private static final int ITEM = 0;
    private static final int LOADING = 1;

    private Context mContext;
    private List<RelatorioCarteiraPedidoDetail> mItens;
    private boolean finishPagination;
    private boolean isLoadingAdded = false;

    public RelatorioCarteiraPedidoDetailAdapter(Context context, List<RelatorioCarteiraPedidoDetail> itens) {
        mContext = context;
        this.mItens = itens;

        if (mItens.size() < Config.SIZE_PAGE) {
            finishPagination = true;
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View skuView;

        switch (viewType) {
            case ITEM:
                skuView = inflater.inflate(R.layout.item_relatorio_carteira_pedidos_detail, parent, false);
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
                RelatorioCarteiraPedidoDetail item = mItens.get(position);
                holder.mNomeTextView.setText(String.format("%s - %s", TextUtils.trimLeadingZeros(item.getCodigo()), item.getNome()));
                holder.mStatusTextView.setText(item.getStatus());

                try {
                    holder.mPesoTextView.setText(mContext.getString(R.string.unidade_peso_kg,
                            FormatUtils.toStringDoubleFormat(item.getPeso())));
                } catch (Exception ex) {
                    LogUser.log(Config.TAG, ex.toString());
                }

                break;
            case LOADING:

                break;
        }
    }

    @Override
    public int getItemViewType(int position) {
        return (position == mItens.size() - 1 && isLoadingAdded) ? LOADING : ITEM;
    }

    @Override
    public int getItemCount() {
        return mItens.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView mNomeTextView;
        private TextView mStatusTextView;
        private TextView mPesoTextView;


        public ViewHolder(View itemView) {
            super(itemView);

            switch (itemView.getId()) {
                case ITEM:
                    mNomeTextView = itemView.findViewById(R.id.item_rel_cart_ped_detail_nome_text_view);
                    mStatusTextView = itemView.findViewById(R.id.item_rel_cart_ped_detail_status_text_view);
                    mPesoTextView = itemView.findViewById(R.id.item_rel_cart_ped_detail_peso_text_view);
                    break;
            }
        }
    }

    public void updateData(List<RelatorioCarteiraPedidoDetail> listSKU) {
        if (listSKU.size() == 0 || listSKU.size() < Config.SIZE_PAGE) {
            finishPagination = true;
        } else {
            finishPagination = false;

        }
        mItens.addAll(listSKU);

        notifyDataSetChanged();
        LogUser.log(Config.TAG, "Pagination - listSKU size: " + mItens.size() + " - page size: " + Config.SIZE_PAGE + " - finishPagination: " + finishPagination);
    }

    public void resetData() {
        mItens.clear();
        finishPagination = false;
    }

    public boolean isFinishPagination() {
        return finishPagination;
    }

    public void setFinishPagination(boolean finishPagination) {
        this.finishPagination = finishPagination;
    }

    public List<RelatorioCarteiraPedidoDetail> getRelatorioCarteiraPedidoDetail() {
        return mItens;
    }

    public void add(RelatorioCarteiraPedidoDetail itens) {
        mItens.add(itens);
        notifyItemInserted(mItens.size() - 1);
    }

    public void addLoadingFooter() {
        isLoadingAdded = true;
        add(new RelatorioCarteiraPedidoDetail());
    }

    public void removeLoadingFooter() {
        isLoadingAdded = false;
        int position = mItens.size() - 1;
        RelatorioCarteiraPedidoDetail item = getItem(position);
        if (item != null) {
            mItens.remove(position);
            notifyItemRemoved(position);
        }
    }

    public RelatorioCarteiraPedidoDetail getItem(int position) {
        return mItens.get(position);
    }
}
