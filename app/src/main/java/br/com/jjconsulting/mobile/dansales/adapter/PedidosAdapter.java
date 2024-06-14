package br.com.jjconsulting.mobile.dansales.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import br.com.jjconsulting.mobile.dansales.R;
import br.com.jjconsulting.mobile.dansales.model.Pedido;
import br.com.jjconsulting.mobile.dansales.util.Config;
import br.com.jjconsulting.mobile.dansales.util.PedidoUtils;
import br.com.jjconsulting.mobile.jjlib.util.FormatUtils;
import br.com.jjconsulting.mobile.jjlib.util.LogUser;

public class PedidosAdapter extends RecyclerView.Adapter<PedidosAdapter.ViewHolder> {

    private static final int ITEM = 0;
    private static final int LOADING = 1;

    private Context mContext;
    private List<Pedido> mPedidos;
    private boolean finishPagination;
    private boolean isLoadingAdded = false;

    public PedidosAdapter(Context context, List<Pedido> pedidos) {
        mContext = context;
        mPedidos = pedidos;

        if (mPedidos == null || mPedidos.size() < Config.SIZE_PAGE) {
            finishPagination = true;
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View pedidoView;
        switch (viewType) {
            case ITEM:
                pedidoView = inflater.inflate(R.layout.item_pedido, parent, false);
                pedidoView.setId(viewType);
                break;
            case LOADING:
                pedidoView = inflater.inflate(R.layout.item_progress, parent, false);
                pedidoView.setId(viewType);
                break;
            default:
                pedidoView = null;
                break;
        }

        return new ViewHolder(pedidoView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        switch (getItemViewType(position)) {
            case ITEM:
                Pedido pedido = mPedidos.get(position);

                PedidoUtils.getPedidoIconImageResourceId( mContext, holder.mOrigemStatusImageView,
                        pedido.getCodigoStatus(), pedido.getCodigoOrigem());

                if (pedido.getCliente() != null) {
                    holder.mNomeTextView.setText(String.format("%s %s", pedido.getCodigoCliente(),
                            pedido.getCliente().getNome()));
                } else {
                    holder.mNomeTextView.setText(mContext.getString(R.string.cliente_indisponivel));
                }

                holder.mTipoTextView.setText(mContext.getString(R.string.pedido_tipo_label,
                        pedido.getCodigoTipoVenda() == null ? "N/A" : pedido.getCodigoTipoVenda()));
                holder.mCodigoTextView.setText(mContext.getString(R.string.pedido_ped_label,
                        pedido.getCodigo()));
                holder.mCodigoSapTextView.setText(mContext.getString(R.string.pedido_codigo_sap_label,
                        pedido.getCodigoSap() == null ? mContext.getString(R.string.null_field) : pedido.getCodigoSap()));
                holder.mDataCadastroTextView.setText(mContext.getString(R.string.pedido_emissao_label, FormatUtils.toDefaultDateAndHourFormat(mContext,
                        pedido.getDataCadastro())));
                holder.mValorTotalTextView.setText(mContext.getString(R.string.pedido_valor_total_label,
                        FormatUtils.toBrazilianRealCurrency(pedido.getValorTotal())));

                break;
            case LOADING:
                break;
        }
    }

    @Override
    public int getItemViewType(int position) {
        return (position == mPedidos.size() - 1 && isLoadingAdded) ? LOADING : ITEM;
    }

    @Override
    public int getItemCount() {
        return mPedidos.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView mOrigemStatusImageView;
        private TextView mNomeTextView;
        private TextView mDataCadastroTextView;
        private TextView mTipoTextView;
        private TextView mCodigoTextView;
        private TextView mCodigoSapTextView;
        private TextView mValorTotalTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            switch (itemView.getId()) {
                case ITEM:
                    mOrigemStatusImageView = itemView.findViewById(R.id.pedido_origem_status_image_view);
                    mNomeTextView = itemView.findViewById(R.id.pedido_cliente_nome_text_view);
                    mDataCadastroTextView = itemView.findViewById(R.id.pedido_data_cadastro_text_view);
                    mTipoTextView = itemView.findViewById(R.id.pedido_tipo_text_view);
                    mCodigoTextView = itemView.findViewById(R.id.pedido_codigo_text_view);
                    mCodigoSapTextView = itemView.findViewById(R.id.pedido_codigo_sap_text_view);
                    mValorTotalTextView = itemView.findViewById(R.id.pedido_valor_total_text_view);
                    break;
            }
        }
    }

    public void updateData(List<Pedido> listPedidos) {
        if (listPedidos.size() == 0 || listPedidos.size() < Config.SIZE_PAGE) {
            finishPagination = true;
        } else {
            finishPagination = false;

        }
        mPedidos.addAll(listPedidos);

        notifyDataSetChanged();
        LogUser.log(Config.TAG, "Pagination - listClientes size: " + mPedidos.size() + " - page size: " + Config.SIZE_PAGE + " - finishPagination: " + finishPagination);
    }

    public void resetData() {
        mPedidos.clear();
        finishPagination = false;
    }

    public boolean isFinishPagination() {
        return finishPagination;
    }

    public void setFinishPagination(boolean finishPagination) {
        this.finishPagination = finishPagination;
    }

    public List<Pedido> getPedidos() {
        return mPedidos;
    }

    public void add(Pedido pedido) {
        mPedidos.add(pedido);
        notifyItemInserted(mPedidos.size() - 1);
    }

    public void addLoadingFooter() {
        isLoadingAdded = true;
        add(new Pedido());
    }

    public void removeLoadingFooter() {
        isLoadingAdded = false;
        int position = mPedidos.size() - 1;
        Pedido item = getItem(position);
        if (item != null) {
            mPedidos.remove(position);
            notifyItemRemoved(position);
        }
    }

    public Pedido getItem(int position) {
        return mPedidos.get(position);
    }
}
