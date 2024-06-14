package br.com.jjconsulting.mobile.dansales.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import br.com.jjconsulting.mobile.dansales.R;
import br.com.jjconsulting.mobile.dansales.model.EstoqueDAT;
import br.com.jjconsulting.mobile.dansales.model.ItemPedido;
import br.com.jjconsulting.mobile.dansales.model.PrecoVenda;
import br.com.jjconsulting.mobile.dansales.model.Produto;
import br.com.jjconsulting.mobile.dansales.model.TipoVenda;
import br.com.jjconsulting.mobile.dansales.util.TSortimento;
import br.com.jjconsulting.mobile.jjlib.util.FormatUtils;
import br.com.jjconsulting.mobile.jjlib.util.TextUtils;

public class ItensPedidoAdapter extends RecyclerView.Adapter<ItensPedidoAdapter.ViewHolder> {

    private Context mContext;
    private OnAddRemove onAddRemove;
    private List<ItemPedido> mItens;
    private String mCodigoTipoVenda;
    private boolean mShowSortimento;
    private boolean mIsAllowedToEdit;
    public boolean isReb;


    public ItensPedidoAdapter(Context context, OnAddRemove onAddRemove, List<ItemPedido> itens, String codigoTipoVenda,
                              boolean showSortimento, boolean isAllowedToEdit) {
        this.mContext = context;
        this.mItens = itens;
        this.mCodigoTipoVenda = codigoTipoVenda;
        this.mIsAllowedToEdit = isAllowedToEdit;
        this.isReb = TipoVenda.REB.equals(codigoTipoVenda);
        this.onAddRemove = onAddRemove;

        if(isReb){
            mShowSortimento = false;
        } else {
            mShowSortimento = showSortimento;
        }
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_pedido_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ItemPedido item = mItens.get(position);

        if (item == null) {
            return;
        }

        holder.mQtdViewGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        Produto produto = item.getProduto();
        boolean ehDAT = TipoVenda.DAT.equals(mCodigoTipoVenda);

        holder.mNomeProdutoTextView.setText(produto.getNome());

        if(mIsAllowedToEdit && mShowSortimento && item.getQtdPedAnt() <= 0 && (item.getProduto().getTipoSortimento() != null && item.getProduto().getTipoSortimento() == Produto.SORTIMENTO_TIPO_OBRIGATORIO)){
            holder.mNomeProdutoTextView.setTextColor(mContext.getResources().getColor(R.color.listItemSecondarySugTextColor));
        } else {
            holder.mNomeProdutoTextView.setTextColor(mContext.getResources().getColor(R.color.listItemSecondaryTextColor));
        }

        holder.mIdTextView.setText(produto.getCodigoSimplificado() == null ?
                "-" : produto.getCodigoSimplificado());
        holder.mSkuTextView.setText(TextUtils.removeAllLeftOccurrencies(
                produto.getCodigo(), '0'));

        if (item.getQtdSug() == -1 || (item.getQtdSug() > 0 && item.getQuantidade() != item.getQtdSug())) {
            holder.mQuantidadeTextView.setTextColor(mContext.getResources().getColor(R.color.listItemSecondarySugTextColor));
            holder.mTitleQuantidadeTextView.setTextColor(mContext.getResources().getColor(R.color.listItemSecondarySugTextColor));
        } else {
            holder.mQuantidadeTextView.setTextColor(mContext.getResources().getColor(R.color.listItemSecondaryTextColor));
            holder.mTitleQuantidadeTextView.setTextColor(mContext.getResources().getColor(R.color.listItemSecondaryTextColor));

        }

        holder.mValorTotalTextView.setText(FormatUtils.toBrazilianRealCurrency(
                item.getValorTotal()).replace("R$", ""));

        if(isReb){
            holder.mViewGroupDATReb.setVisibility(View.VISIBLE);
            holder.mViewGroupDATValReb.setVisibility(View.VISIBLE);
            holder.mExpirationDateTextView.setText(item.getLote());
            holder.mVLGondulaTextView.setText(FormatUtils.toBrazilianRealCurrency(
                    item.getPrecoVenda()));
        } else {
            holder.mExpirationDateTextView.setText(item.getLote());
            holder.mViewGroupDATReb.setVisibility(View.GONE);
            holder.mViewGroupDATValReb.setVisibility(View.GONE);
        }

        if (ehDAT) {
            holder.mLoteTextView.setText(item.getLote());
            EstoqueDAT estoqueDAT = produto.getEstoqueDAT();
            holder.mEstoqueTextView.setText(estoqueDAT == null ?
                    "-" : String.valueOf(estoqueDAT.getQuantidadeDisponivel()));
        }

        holder.mViewGroupDAT.setVisibility(ehDAT ? View.VISIBLE : View.GONE);

        int tipoSortimento = (produto.getTipoSortimento() == null ? -1 : produto.getTipoSortimento());

        holder.mQuantidadeTextView.setVisibility(View.GONE);
        holder.mTitleQuantidadeTextView.setVisibility(View.GONE);

        holder.mSortimentoTextView.setVisibility(View.GONE);
        holder.mViewGroupSortimento.setVisibility(View.GONE);


        if(mShowSortimento  && tipoSortimento != -1){
            holder.mSortimentoItemTextView.setBackgroundResource(R.drawable.textview_rounded_corners);
            GradientDrawable drawable = (GradientDrawable) holder.mSortimentoItemTextView.getBackground();

            if(item.getQuantidade() > 0){
                drawable.setColor(mContext.getResources().getColor(R.color.sucessCollor));
            } else {
                drawable.setColor(Color.BLUE);
            }

            holder.mSortimentoItemTextView.setBackground(drawable);
            holder.mSortimentoItemTextView.setText(TSortimento.getDescriptionSortimento(mContext, tipoSortimento));
            holder.mSortimentoItemTextView.setVisibility(View.VISIBLE);

        } else {
            holder.mSortimentoItemTextView.setVisibility(View.GONE);
        }

        boolean showStatus = mIsAllowedToEdit && produto.isPrecoAvailable();
        if (showStatus) {
            boolean disponivel = produto.getPrecoVenda().getBloq()
                    == PrecoVenda.BLOQ_PRODUTO_DISPONIVEL;
            int statusColor = mContext.getResources().getColor(
                    disponivel ? R.color.statusProdutoOK : R.color.statusProdutoError);
            holder.mStatusView.setBackgroundColor(statusColor);
        }
        holder.mStatusView.setVisibility(showStatus ? View.VISIBLE : View.INVISIBLE);

        if(item.getQuantidade() >= 0){
            holder.mQuantidadeItemTextView.setText(String.valueOf(item.getQuantidade()));
            holder.mPesoTextView.setText(FormatUtils.toKilogram(
                    item.getPesoLiquidoProduto() * item.getQuantidade(), 2));

        } else {
            holder.mQuantidadeItemTextView.setText("");
            holder.mPesoTextView.setText("");
        }

        if(!mIsAllowedToEdit){
            holder.mArrowLeftQtDImageView.setVisibility(View.INVISIBLE);
            holder.mArrowRightQtDImageView.setVisibility(View.INVISIBLE);

        } else {
            holder.mArrowLeftQtDImageView.setTag(position);
            holder.mArrowRightQtDImageView.setTag(item.getId());
            holder.mArrowLeftQtDImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onAddRemove.onClick(item.getId(),false);
                }
            });

            holder.mArrowRightQtDImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onAddRemove.onClick(item.getId(),true);
                }
            });
        }

        //holder.mArrowLeftQtDImageView.setVisibility(View.INVISIBLE);
        //holder.mArrowRightQtDImageView.setVisibility(View.INVISIBLE);

    }

    @Override
    public int getItemCount() {
        return mItens.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ViewGroup mViewGroupDAT;
        private ViewGroup mViewGroupDATReb;
        private ViewGroup mViewGroupDATValReb;
        private ViewGroup mViewGroupSortimento;
        private ViewGroup mQtdViewGroup;

        private TextView mNomeProdutoTextView;
        private TextView mIdTextView;
        private TextView mSkuTextView;
        private TextView mQuantidadeTextView;
        private TextView mTitleQuantidadeTextView;
        private TextView mPesoTextView;
        private TextView mValorTotalTextView;
        private TextView mLoteTextView;
        private TextView mEstoqueTextView;
        private TextView mSortimentoTextView;
        private TextView mExpirationDateTextView;
        private TextView mVLGondulaTextView;
        private TextView mSortimentoItemTextView;
        private TextView mQuantidadeItemTextView;

        private ImageView mArrowLeftQtDImageView;
        private ImageView mArrowRightQtDImageView;



        private View mStatusView;

        public ViewHolder(View itemView) {
            super(itemView);

            mQtdViewGroup = itemView.findViewById(R.id.item_qtd_linear_layout);
            mViewGroupDAT = itemView.findViewById(R.id.view_group_dat);
            mViewGroupDATReb = itemView.findViewById(R.id.view_group_dat_reb);
            mViewGroupDATValReb = itemView.findViewById(R.id.view_group_dat_val_reb);
            mViewGroupSortimento = itemView.findViewById(R.id.view_group_sortimento);
            mNomeProdutoTextView = itemView.findViewById(R.id.nome_produto_text_view);
            mIdTextView = itemView.findViewById(R.id.id_text_view);
            mSkuTextView = itemView.findViewById(R.id.sku_produto_text_view);
            mQuantidadeTextView = itemView.findViewById(R.id.quantidade_text_view);
            mTitleQuantidadeTextView = itemView.findViewById(R.id.title_quantidade_text_view);
            mPesoTextView = itemView.findViewById(R.id.item_prod_peso_text_view);
            mValorTotalTextView = itemView.findViewById(R.id.valor_total_text_view);
            mLoteTextView = itemView.findViewById(R.id.item_prod_lote_text_view);
            mEstoqueTextView = itemView.findViewById(R.id.item_prod_estoque_text_view);
            mSortimentoTextView = itemView.findViewById(R.id.item_prod_sortimento_text_view);
            mStatusView = itemView.findViewById(R.id.produto_status_view);
            mExpirationDateTextView =itemView.findViewById(R.id.item_prod_expiration_date_text_view);
            mVLGondulaTextView = itemView.findViewById(R.id.item_prod_valor_gondula_text_view);
            mSortimentoItemTextView = itemView.findViewById(R.id.item_sortimento_text_view);
            mQuantidadeItemTextView = itemView.findViewById(R.id.quantidade_item_text_view);

            mArrowLeftQtDImageView = itemView.findViewById(R.id.arrow_left_qtd_image_view);
            mArrowRightQtDImageView = itemView.findViewById(R.id.arrow_right_qtd_image_view);
        }
    }

    public String getCodigoTipoVenda() {
        return mCodigoTipoVenda;
    }

    public void setCodigoTipoVenda(String codigoTipoVenda) {
        this.mCodigoTipoVenda = codigoTipoVenda;
        isReb = TipoVenda.REB.equals(codigoTipoVenda);
    }

    public interface OnAddRemove{
        public void onClick(int position, boolean add);
    }
}
