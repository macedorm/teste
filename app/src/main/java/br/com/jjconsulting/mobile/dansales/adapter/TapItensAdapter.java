package br.com.jjconsulting.mobile.dansales.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import br.com.jjconsulting.mobile.dansales.R;
import br.com.jjconsulting.mobile.dansales.model.TapItem;
import br.com.jjconsulting.mobile.dansales.util.ETapUtils;
import br.com.jjconsulting.mobile.jjlib.util.FormatUtils;

public class TapItensAdapter extends RecyclerView.Adapter<TapItensAdapter.ViewHolder> {

    private Context mContext;
    private List<TapItem> mItens;

    public TapItensAdapter(Context context, List<TapItem> itens) {
        mContext = context;
        mItens = itens;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_tap_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        TapItem item = mItens.get(position);

        if (item == null) {
            return;
        }

        holder.mStatusView.setBackgroundColor(ETapUtils.getItemColorStatus(mContext, item));

        if (item.getTapTipoInvest() != null) {
            holder.mTapTipoTextView.setText(mContext.getString(R.string.tap_tipo_label,
                    item.getTapTipoInvest().getDescricao()));
        } else {
            holder.mTapTipoTextView.setVisibility(View.GONE);
        }

        if (item.getTapAcao() != null) {
            holder.mTapAcaoTextView.setText(mContext.getString(R.string.tap_acao_label,
                    item.getTapAcao().getDescricao()));
        } else {
            holder.mTapAcaoTextView.setVisibility(View.GONE);
        }

        if (item.getTapProdCateg() != null) {
            holder.mCategoriaProdutoTextView.setText(mContext.getString(R.string.tap_categoria_produto_label,
                    item.getTapProdCateg().getDescricao()));
        } else {
            holder.mCategoriaProdutoTextView.setVisibility(View.GONE);
        }

        holder.mVlEstimadoTextView.setText(mContext.getString(R.string.tap_vl_estimado_label,
                FormatUtils.toDoubleFormat(item.getVlEst())));
        holder.mVlApuradoTextView.setText(mContext.getString(R.string.tap_vl_apurado_label,
                FormatUtils.toDoubleFormat(item.getVlApur())));

        holder.mTapAnexoImageView.setTag(item.getUrlAnexo());
        holder.mTapAnexoImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse(v.getTag().toString());
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, uri);
                mContext.startActivity(browserIntent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mItens.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private View mStatusView;


        private RelativeLayout mBackgroundRelativeLayout;
        private TextView mTapTipoTextView;
        private TextView mTapAcaoTextView;
        private TextView mCategoriaProdutoTextView;
        private TextView mVlApuradoTextView;
        private TextView mVlEstimadoTextView;
        private ImageView mTapAnexoImageView;

        public ViewHolder(View itemView) {
            super(itemView);

            mStatusView = itemView.findViewById(R.id.tap_status_view);
            mTapTipoTextView = itemView.findViewById(R.id.tap_tipo_text_view);
            mTapAcaoTextView = itemView.findViewById(R.id.tap_acao_text_view);
            mCategoriaProdutoTextView = itemView.findViewById(R.id.tap_categoria_prod_text_view);
            mVlApuradoTextView = itemView.findViewById(R.id.tap_vl_apurado_text_view);
            mVlEstimadoTextView = itemView.findViewById(R.id.tap_vl_estimado_text_view);

            mStatusView = itemView.findViewById(R.id.tap_status_view);
            mTapAnexoImageView = itemView.findViewById(R.id.tap_anexo_iamge_view);

        }
    }
}
