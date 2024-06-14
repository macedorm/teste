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
import br.com.jjconsulting.mobile.dansales.model.OrigemPedido;
import br.com.jjconsulting.mobile.dansales.model.RelatorioCarteiraPedido;
import br.com.jjconsulting.mobile.dansales.model.RelatorioCarteiraPedidoStatus;
import br.com.jjconsulting.mobile.dansales.util.Config;
import br.com.jjconsulting.mobile.jjlib.util.FormatUtils;
import br.com.jjconsulting.mobile.jjlib.util.LogUser;
import br.com.jjconsulting.mobile.jjlib.util.TextUtils;

public class RelatorioCarteiraPedidosAdapter extends RecyclerView.Adapter<RelatorioCarteiraPedidosAdapter.ViewHolder> {

    private static final int ITEM = 0;
    private static final int LOADING = 1;

    private Context mContext;
    private List<RelatorioCarteiraPedido> mPedidos;
    private boolean finishPagination;
    private boolean isLoadingAdded = false;

    public RelatorioCarteiraPedidosAdapter(Context context, List<RelatorioCarteiraPedido> pedidos) {
        mContext = context;
        mPedidos = pedidos;

        if (mPedidos.size() < Config.SIZE_PAGE) {
            finishPagination = true;
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View pedidoView;
        switch (viewType) {
            case ITEM:
                pedidoView = inflater.inflate(R.layout.item_carteira_pedido, parent,
                        false);
                pedidoView.setId(viewType);
                break;
            case LOADING:
                pedidoView = inflater.inflate(R.layout.item_progress, parent,
                        false);
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
                RelatorioCarteiraPedido pedido = mPedidos.get(position);
                int image = getCarteiraPedidoIconImageResourceId(
                        pedido.getStatus(), pedido.getCodigoOrigem());

                if (image != -1) {
                    holder.mOrigemStatusImageView.setImageResource(image);
                } else {
                    holder.mOrigemStatusImageView.setVisibility(View.GONE);
                }

                holder.mNomeTextView.setText(String.format("%s - %s", pedido.getCodigoCliente(), pedido.getCliente()));
                holder.mRegionalTextView.setText(mContext.getString(R.string.pedido_ped_reg_label, pedido.getRegional() == null ? "N/A" : TextUtils.firstLetterUpperCase(pedido.getRegional())));
                holder.mTipoTextView.setText(mContext.getString(R.string.pedido_tipo_label,
                        TextUtils.firstLetterUpperCase(pedido.getCodigoTipoVenda())));
                holder.mCodigoPedidoTextView.setText(mContext.getString(R.string.pedido_ped_cli_label,
                        pedido.getCodigo()));

                holder.mStatusTextView.setText(pedido.getCodigoSap() == null ? mContext.getString(R.string.pedido_codigo_sap_label, mContext.getString(R.string.null_field)) : mContext.getString(R.string.pedido_codigo_sap_label, pedido.getCodigoSap()));

                try {
                    String cDate = FormatUtils.toDefaultDateFormat(mContext, pedido.getDataCadastro());
                    holder.mDataCadastroTextView.setText(mContext.getString(R.string.pedido_emissao_label, cDate));
                } catch (Exception ex) {
                    holder.mDataCadastroTextView.setText(mContext.getString(R.string.pedido_emissao_label, mContext.getString(R.string.null_field)));
                    LogUser.log(Config.TAG, ex.toString());
                }

                try {
                    holder.mPesoTextView.setText(mContext.getString(R.string.pedido_peso_label,
                            FormatUtils.toStringDoubleFormat(pedido.getPeso())));
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
        return (position == mPedidos.size() - 1 && isLoadingAdded) ? LOADING : ITEM;
    }

    @Override
    public int getItemCount() {
        return mPedidos.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView mOrigemStatusImageView;
        private TextView mNomeTextView;
        private TextView mRegionalTextView;
        private TextView mTipoTextView;
        private TextView mCodigoPedidoTextView;
        private TextView mStatusTextView;
        private TextView mDataCadastroTextView;
        private TextView mPesoTextView;


        public ViewHolder(View itemView) {
            super(itemView);

            switch (itemView.getId()) {
                case ITEM:
                    mOrigemStatusImageView = itemView.findViewById(R.id.rel_cart_ped_origem_status_image_view);
                    mNomeTextView = itemView.findViewById(R.id.rel_cart_ped_cliente_nome_text_view);
                    mRegionalTextView = itemView.findViewById(R.id.rel_cart_ped_regional_text_view);
                    mTipoTextView = itemView.findViewById(R.id.rel_cart_ped_tipo_text_view);
                    mCodigoPedidoTextView = itemView.findViewById(R.id.rel_cart_ped_codigo_text_view);
                    mStatusTextView = itemView.findViewById(R.id.rel_cart_ped_status_total_text_view);
                    mDataCadastroTextView = itemView.findViewById(R.id.rel_cart_ped_data_cadastro_text_view);
                    mPesoTextView = itemView.findViewById(R.id.rel_cart_ped_peso_text_view);
                    break;
            }
        }
    }

    public void updateData(List<RelatorioCarteiraPedido> listPedidos) {
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

    public List<RelatorioCarteiraPedido> getPedidos() {
        return mPedidos;
    }

    public void add(RelatorioCarteiraPedido pedido) {
        mPedidos.add(pedido);
        notifyItemInserted(mPedidos.size() - 1);
    }

    public void addLoadingFooter() {
        isLoadingAdded = true;
        add(new RelatorioCarteiraPedido());
    }

    public void removeLoadingFooter() {
        isLoadingAdded = false;
        int position = mPedidos.size() - 1;
        RelatorioCarteiraPedido item = getItem(position);
        if (item != null) {
            mPedidos.remove(position);
            notifyItemRemoved(position);
        }
    }

    public RelatorioCarteiraPedido getItem(int position) {
        return mPedidos.get(position);
    }

    public static int getCarteiraPedidoIconImageResourceId(RelatorioCarteiraPedidoStatus status, int codigoOrigem) {
        int iconImageResourceId = -1;
        if (codigoOrigem == OrigemPedido.WEB || codigoOrigem == OrigemPedido.SUPORTE) {
            switch (status){
                case FATURADO_TOTAL:
                    iconImageResourceId = R.drawable.ic_desk_purple;
                    break;
                case CANCELADO:
                    iconImageResourceId = R.drawable.ic_desk_red;
                    break;
                case CORTE_TOTAL:
                    iconImageResourceId = R.drawable.ic_desk_pink;
                    break;
                case FATURADO_PARCIAL:
                    iconImageResourceId = R.drawable.ic_desk_light_green;
                    break;
                default:
                    iconImageResourceId = R.drawable.ic_desk_yellow;
                    break;
            }
        } else if (codigoOrigem == OrigemPedido.MOBILE) {
            switch (status){
                case FATURADO_TOTAL:
                    iconImageResourceId = R.drawable.ic_mob_purple;
                break;
                case CANCELADO:
                    iconImageResourceId = R.drawable.ic_mob_red;
                break;
                case CORTE_TOTAL:
                    iconImageResourceId = R.drawable.ic_mob_pink;
                    break;
                case FATURADO_PARCIAL:
                    iconImageResourceId = R.drawable.ic_mob_light_green;
                break;
                default:
                    iconImageResourceId= R.drawable.ic_mob_yellow;
                break;
            }

        } else if (codigoOrigem == OrigemPedido.CAC) {
            switch (status){
                case FATURADO_TOTAL:
                    iconImageResourceId = R.drawable.ic_cac_purple;
                break;
                case CANCELADO:
                    iconImageResourceId = R.drawable.ic_cac_red;
                break;
                case CORTE_TOTAL:
                    iconImageResourceId = R.drawable.ic_cac_pink;
                    break;
                case FATURADO_PARCIAL:
                    iconImageResourceId = R.drawable.ic_cac_light_green;
                break;
                default:
                    iconImageResourceId = R.drawable.ic_cac_yellow;
                break;
            }
        } else if (codigoOrigem == OrigemPedido.EDI) {
            switch (status){
                case FATURADO_TOTAL:
                    iconImageResourceId = R.drawable.ic_edi_purple;
                    break;
                case CANCELADO:
                    iconImageResourceId = R.drawable.ic_edi_red;
                    break;
                case CORTE_TOTAL:
                    iconImageResourceId = R.drawable.ic_edi_pink;
                    break;
                case FATURADO_PARCIAL:
                    iconImageResourceId = R.drawable.ic_edi_light_green;
                    break;
                default:
                    iconImageResourceId = R.drawable.ic_edi_yellow;
                    break;
            }
        } else if (codigoOrigem == OrigemPedido.TEVEC) {
            switch (status){
                case FATURADO_TOTAL:
                    iconImageResourceId = R.drawable.ic_suj_purple;
                    break;
                case CANCELADO:
                    iconImageResourceId = R.drawable.ic_suj_red;
                    break;
                case CORTE_TOTAL:
                    iconImageResourceId = R.drawable.ic_suj_pink;
                    break;
                case FATURADO_PARCIAL:
                    iconImageResourceId = R.drawable.ic_suj_light_green;
                    break;
                default:
                    iconImageResourceId = R.drawable.ic_suj_yellow;
                    break;
            }
        }
        return iconImageResourceId;
    }

}
