package br.com.jjconsulting.mobile.dansales.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import br.com.jjconsulting.mobile.dansales.R;
import br.com.jjconsulting.mobile.dansales.model.PrecoVenda;
import br.com.jjconsulting.mobile.dansales.model.Produto;
import br.com.jjconsulting.mobile.dansales.util.PedidoUtils;
import br.com.jjconsulting.mobile.dansales.util.TSortimento;
import br.com.jjconsulting.mobile.jjlib.util.FormatUtils;
import br.com.jjconsulting.mobile.jjlib.util.TextUtils;

public class ProdutosAdapter extends RecyclerView.Adapter<ProdutosAdapter.ViewHolder> {

    private static final int ITEM = 0;
    private static final int LOADING = 1;

    private Context mContext;
    private List<Produto> mProdutos;
    private boolean mShowSortimento;

    private boolean mLoadingAdded;

    public ProdutosAdapter(Context context, List<Produto> produtos, boolean showSortimento) {
        mContext = context;
        mProdutos = produtos;
        mShowSortimento = showSortimento;
        mLoadingAdded = false;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View produtoView;
        switch (viewType) {
            case ITEM:
                produtoView = inflater.inflate(R.layout.item_produto, parent, false);
                produtoView.setId(viewType);
                break;
            case LOADING:
                produtoView = inflater.inflate(R.layout.item_progress, parent, false);
                produtoView.setId(viewType);
                break;
            default:
                produtoView = null;
                break;
        }

        return new ViewHolder(produtoView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        switch (getItemViewType(position)) {
            case ITEM:
                Produto produto = mProdutos.get(position);
                holder.mNomeTextView.setText(produto.getNome());
                holder.mSkuTextView.setText(
                        TextUtils.removeAllLeftOccurrencies(produto.getCodigo(), '0'));
                holder.mPesoTextView.setText(FormatUtils.toKilogram(produto.getPesoLiquido(), 2));

                if (produto.isPrecoAvailable()) {
                    holder.mPrecoTextView.setText(FormatUtils.toBrazilianRealCurrency(
                            produto.getPrecoVenda().getPreco()));
                    holder.mStatusView.setBackgroundColor(mContext.getResources().getColor(
                            produto.getPrecoVenda().getBloq() == PrecoVenda.BLOQ_PRODUTO_DISPONIVEL ?
                                    R.color.statusProdutoOK : R.color.statusProdutoError));
                } else {
                    holder.mPrecoTextView.setText(FormatUtils.toBrazilianRealCurrency(0));
                }


                /* Sortimento Antigo
                if (mShowSortimento) {
                    boolean sortimento = produto.getTipoSortimento() != null;

                    if (sortimento) {
                        int sortimentoColor = PedidoUtils.getSortimentoColorResourceId(sortimento ?
                                produto.getTipoSortimento().intValue() : -1);
                        holder.mSortimentoTextView.setText(sortimento ?
                                produto.getDescricaoSortimento() : "-");
                        holder.mSortimentoTextView.setTextColor(
                                mContext.getResources().getColor(sortimentoColor));
                    }

                    holder.mSortimentoTextView.setVisibility(sortimento ? View.VISIBLE : View.GONE);
                } else {
                    holder.mSortimentoTextView.setVisibility(View.GONE);
                }*/

                int tipoSortimento = (produto.getTipoSortimento() == null ? -1 : produto.getTipoSortimento());

                if(mShowSortimento  && tipoSortimento != -1){
                    holder.mSortimentoTextView.setBackgroundResource(R.drawable.textview_rounded_corners);
                    GradientDrawable drawable = (GradientDrawable) holder.mSortimentoTextView.getBackground();
                    drawable.setColor(Color.BLUE);

                    holder.mSortimentoTextView.setBackground(drawable);
                    holder.mSortimentoTextView.setText(TSortimento.getDescriptionSortimento(mContext, tipoSortimento));
                    holder.mSortimentoTextView.setVisibility(View.VISIBLE);

                } else {
                    holder.mSortimentoTextView.setVisibility(View.GONE);
                }

                holder.mStatusView.setVisibility(produto.isPrecoAvailable() ?
                        View.VISIBLE : View.INVISIBLE);
                break;
            case LOADING:
            default:
                break;
        }
    }

    @Override
    public int getItemCount() {
        return mProdutos.size();
    }

    @Override
    public int getItemViewType(int position) {
        return (position == mProdutos.size() - 1 && mLoadingAdded) ? LOADING : ITEM;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private View mStatusView;
        private TextView mNomeTextView;
        private TextView mSkuTextView;
        private TextView mPesoTextView;
        private TextView mPrecoTextView;
        private TextView mSortimentoTextView;

        public ViewHolder(View itemView) {
            super(itemView);

            switch (itemView.getId()) {
                case ITEM:
                    mStatusView = itemView.findViewById(R.id.status_view);
                    mNomeTextView = itemView.findViewById(R.id.nome_text_view);
                    mSkuTextView = itemView.findViewById(R.id.sku_text_view);
                    mPesoTextView = itemView.findViewById(R.id.peso_text_view);
                    mPrecoTextView = itemView.findViewById(R.id.preco_text_view);
                    mSortimentoTextView = itemView.findViewById(R.id.sortimento_text_view);
                    break;
                case LOADING:
                default:
                    break;
            }
        }
    }

    public void addLoadingFooter() {
        mLoadingAdded = true;
        mProdutos.add(new Produto());
        notifyItemInserted(mProdutos.size() - 1);
    }

    public void removeLoadingFooter() {
        mLoadingAdded = false;

        if (mProdutos.size() == 0) {
            return;
        }

        int position = mProdutos.size() - 1;
        mProdutos.remove(position);
        notifyItemRemoved(position);
    }
}
