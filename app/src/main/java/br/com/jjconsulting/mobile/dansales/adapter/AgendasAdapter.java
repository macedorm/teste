package br.com.jjconsulting.mobile.dansales.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import br.com.jjconsulting.mobile.dansales.R;
import br.com.jjconsulting.mobile.dansales.model.Agenda;
import br.com.jjconsulting.mobile.jjlib.util.FormatUtils;

public class AgendasAdapter extends RecyclerView.Adapter<AgendasAdapter.ViewHolder> {

    private Context mContext;
    private List<Agenda> mAgendas;

    public AgendasAdapter(Context context, List<Agenda> agendas) {
        mContext = context;
        mAgendas = agendas;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_agenda, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Agenda agenda = mAgendas.get(position);
        holder.mCodigoTextView.setText(mContext.getString(R.string.agenda_item_codigo,
                agenda.getCodigo()));
        holder.mValidadeTextView.setText(mContext.getString(R.string.agenda_item_validade,
                FormatUtils.toDefaultDateAndHourFormat(mContext, agenda.getValidade())));
        holder.mEditalTextView.setText(mContext.getString(R.string.agenda_item_edital,
                agenda.getEdital()));
    }

    @Override
    public int getItemCount() {
        return mAgendas.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView mCodigoTextView;
        private TextView mValidadeTextView;
        private TextView mEditalTextView;

        public ViewHolder(View itemView) {
            super(itemView);

            mCodigoTextView = itemView.findViewById(R.id.agenda_codigo);
            mValidadeTextView = itemView.findViewById(R.id.agenda_validade);
            mEditalTextView = itemView.findViewById(R.id.agenda_edital);
        }
    }
}
