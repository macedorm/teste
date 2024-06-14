package br.com.jjconsulting.mobile.dansales.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import br.com.jjconsulting.mobile.dansales.R;
import br.com.jjconsulting.mobile.dansales.model.ItemPedido;
import br.com.jjconsulting.mobile.dansales.model.Produto;
import br.com.jjconsulting.mobile.jjlib.util.TextUtils;

public class ItensPedidoJustAdapter extends RecyclerView.Adapter<ItensPedidoJustAdapter.ViewHolder> {

    private Context mContext;
    private List<ItemPedido> mItens;

    public ItensPedidoJustAdapter(Context context, List<ItemPedido> itens) {
        mContext = context;
        mItens = itens;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_pedido_item_just, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ItemPedido item = mItens.get(position);

        if (item == null) {
            return;
        }

        Produto produto = item.getProduto();
        holder.mNomeProdutoTextView.setText(produto.getNome());
        holder.mIdTextView.setText(produto.getCodigoSimplificado() == null ?
                "-" : produto.getCodigoSimplificado());
        holder.mSkuTextView.setText(TextUtils.removeAllLeftOccurrencies(
                produto.getCodigo(), '0'));

        if(item.getMultiValues() != null){
            holder.mJustificativaTextView.setText(item.getMultiValues().getDesc());
            holder.mJustificativaTextView.setTextColor(mContext.getResources().getColor(R.color.listItemSecondaryTextColor));
        } else {
            holder.mJustificativaTextView.setText(mContext.getString(R.string.justificativa_n_preenchida));
            holder.mJustificativaTextView.setTextColor(mContext.getResources().getColor(R.color.listItemSecondarySugTextColor));

        }

    }

    @Override
    public int getItemCount() {
        return mItens.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView mNomeProdutoTextView;
        private TextView mJustificativaTextView;
        private TextView mIdTextView;
        private TextView mSkuTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            mNomeProdutoTextView = itemView.findViewById(R.id.nome_produto_text_view);
            mIdTextView = itemView.findViewById(R.id.id_text_view);
            mSkuTextView = itemView.findViewById(R.id.sku_produto_text_view);
            mJustificativaTextView = itemView.findViewById(R.id.just_produto_text_view);
        }
    }
}
