package br.com.jjconsulting.mobile.dansales.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import br.com.jjconsulting.mobile.dansales.R;
import br.com.jjconsulting.mobile.dansales.model.TapStep;
import br.com.jjconsulting.mobile.jjlib.util.FormatUtils;
import br.com.jjconsulting.mobile.jjlib.util.TextUtils;

public class LogTapAdapter extends RecyclerView.Adapter<LogTapAdapter.ViewHolder> {

    private Context mContext;
    private List<TapStep> mLogTap;

    public LogTapAdapter(Context context, List<TapStep> logTap) {
        mContext = context;
        mLogTap = logTap;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_log_tap, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        TapStep tapStep = mLogTap.get(position);

        if (TextUtils.isNullOrEmpty(tapStep.getUser())) {
            holder.mNomeVendedor.setVisibility(View.GONE);
        } else {
            holder.mNomeVendedor.setText(tapStep.getUser());
        }

        if (TextUtils.isNullOrEmpty(tapStep.getAction())) {
            holder.mDescricao.setVisibility(View.GONE);
        } else {
            holder.mDescricao.setText(mContext.getString(R.string.log_item_acao,
                    tapStep.getAction()));
        }

        if (TextUtils.isNullOrEmpty(tapStep.getDateCad())) {
            holder.mData.setVisibility(View.GONE);
        } else {
            try {
                holder.mData.setText(mContext.getString(R.string.log_item_date,
                        FormatUtils.toDefaultDateAndHourFormat(mContext, FormatUtils.toDate(tapStep.getDateCad().replace("T", " ")))));
            }catch (Exception ex){

            }
        }

        if (TextUtils.isNullOrEmpty(tapStep.getObs())) {
            holder.mObs.setVisibility(View.GONE);
        } else {
            holder.mObs.setText(mContext.getString(R.string.log_item_obs,
                    tapStep.getObs()));
        }
    }

    @Override
    public int getItemCount() {
        return mLogTap.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView mNomeVendedor;
        private TextView mDescricao;
        private TextView mData;
        private TextView mObs;

        public ViewHolder(View itemView) {
            super(itemView);
            mNomeVendedor = itemView.findViewById(R.id.log_nome);
            mDescricao = itemView.findViewById(R.id.log_descricao);
            mData = itemView.findViewById(R.id.log_date);
            mObs = itemView.findViewById(R.id.log_obs);
        }
    }
}
