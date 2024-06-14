package br.com.jjconsulting.mobile.dansales.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import br.com.jjconsulting.mobile.dansales.R;
import br.com.jjconsulting.mobile.dansales.model.LogPedido;
import br.com.jjconsulting.mobile.jjlib.util.FormatUtils;

public class LogPedidoAdapter extends RecyclerView.Adapter<LogPedidoAdapter.ViewHolder> {

    private Context mContext;
    private List<LogPedido> mLogPedido;

    public LogPedidoAdapter(Context context, List<LogPedido> logPedido) {
        mContext = context;
        mLogPedido = logPedido;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_log_pedido, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        LogPedido logPedido = mLogPedido.get(position);
        holder.mDescricao.setText(logPedido.getDescricao().replace(" S", "S"));
        holder.mData.setText(mContext.getString(R.string.log_item_date,
                FormatUtils.toDefaultDateAndHourFormat(mContext, logPedido.getData())));

        holder.mLineTop.setVisibility(View.VISIBLE);
        holder.mLineBottom.setVisibility(View.VISIBLE);

        if(position == 0){
            holder.mLineTop.setVisibility(View.INVISIBLE);
        }

        if((position + 1) == mLogPedido.size()){
            holder.mLineBottom.setVisibility(View.INVISIBLE);
        }

    }

    @Override
    public int getItemCount() {
        return mLogPedido.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView mDescricao;
        private TextView mData;
        private LinearLayout mLineTop;
        private LinearLayout mLineBottom;

        public ViewHolder(View itemView) {
            super(itemView);
            mDescricao = itemView.findViewById(R.id.log_descricao);
            mData = itemView.findViewById(R.id.log_date);
            mLineTop = itemView.findViewById(R.id.line_top_linear_layout);
            mLineBottom = itemView.findViewById(R.id.line_bottom_linear_layout);

        }
    }
}
