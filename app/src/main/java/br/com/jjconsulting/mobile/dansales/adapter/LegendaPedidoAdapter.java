package br.com.jjconsulting.mobile.dansales.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import br.com.jjconsulting.mobile.dansales.R;
import br.com.jjconsulting.mobile.dansales.model.Agenda;
import br.com.jjconsulting.mobile.dansales.model.StatusPedido;
import br.com.jjconsulting.mobile.jjlib.util.FormatUtils;

public class LegendaPedidoAdapter extends RecyclerView.Adapter<LegendaPedidoAdapter.ViewHolder> {

    private Context mContext;
    private List<StatusPedido> mStatusPedido;

    public LegendaPedidoAdapter(Context context, List<StatusPedido> statusPedido) {
        mContext = context;
        mStatusPedido = statusPedido;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_legenda_pedido, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        StatusPedido statusPedido = mStatusPedido.get(position);
        holder.mTitleTextView.setText(statusPedido.getNome());
        holder.mIconImageView.setColorFilter(Color.parseColor(statusPedido.getColor()), PorterDuff.Mode.SRC_ATOP);
    }

    @Override
    public int getItemCount() {
        return mStatusPedido.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView mIconImageView;
        private TextView mTitleTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            mIconImageView = itemView.findViewById(R.id.icon_legenda_image_view);
            mTitleTextView = itemView.findViewById(R.id.title_legenda_text_view);
        }
    }
}
