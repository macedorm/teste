package br.com.jjconsulting.mobile.dansales.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import br.com.jjconsulting.mobile.dansales.R;
import br.com.jjconsulting.mobile.dansales.model.RelatorioNotas;
import br.com.jjconsulting.mobile.dansales.util.Config;
import br.com.jjconsulting.mobile.jjlib.util.LogUser;

public class RelatorioNotasAdapter extends RecyclerView.Adapter<RelatorioNotasAdapter.ViewHolder> {

    private static final int ITEM = 0;
    private static final int LOADING = 1;

    private Context mContext;
    private List<RelatorioNotas> mNotas;
    private boolean finishPagination;
    private boolean isLoadingAdded = false;

    public RelatorioNotasAdapter(Context context, List<RelatorioNotas> notas) {
        mContext = context;
        mNotas = notas;

        if (mNotas.size() < Config.SIZE_PAGE) {
            finishPagination = true;
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View notasView;

        switch (viewType) {
            case ITEM:
                notasView = inflater.inflate(R.layout.item_relatorio_notas, parent, false);
                notasView.setId(viewType);
                break;
            case LOADING:
                notasView = inflater.inflate(R.layout.item_progress, parent, false);
                notasView.setId(viewType);
                break;
            default:
                notasView = null;
                break;
        }

        return new ViewHolder(notasView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        switch (getItemViewType(position)) {
            case ITEM:
                RelatorioNotas relatorioNotas = mNotas.get(position);
                String nome = String.format("%s - %s", relatorioNotas.getCodigoCliente(), relatorioNotas.getNome());
                holder.mNomeTextView.setText(nome);
                holder.mDataTextView.setText(relatorioNotas.getData());
                holder.mSAP.setText(relatorioNotas.getSap() == null ? mContext.getString(R.string.null_field) : relatorioNotas.getSap());
                holder.mNota.setText(relatorioNotas.getNumero());
                holder.mTipo.setText(relatorioNotas.getCodigoTipoVenda());
                break;
            case LOADING:

                break;
        }
    }

    @Override
    public int getItemViewType(int position) {
        return (position == mNotas.size() - 1 && isLoadingAdded) ? LOADING : ITEM;
    }

    @Override
    public int getItemCount() {
        return mNotas.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView mNomeTextView;
        private TextView mDataTextView;
        private TextView mSAP;
        private TextView mNota;
        private TextView mTipo;

        public ViewHolder(View itemView) {
            super(itemView);

            switch (itemView.getId()) {
                case ITEM:
                    mNomeTextView = itemView.findViewById(R.id.relatorio_notas_nome_text_view);
                    mDataTextView = itemView.findViewById(R.id.relatorio_notas_data_text_view);
                    mSAP = itemView.findViewById(R.id.relatorio_notas_sap_text_view);
                    mNota = itemView.findViewById(R.id.relatorio_notas_numero_text_view);
                    mTipo = itemView.findViewById(R.id.relatorio_notas_tipo_text_view);
                    break;
            }
        }
    }

    public void updateData(List<RelatorioNotas> listNotas) {
        if (listNotas.size() == 0 || listNotas.size() < Config.SIZE_PAGE) {
            finishPagination = true;
        } else {
            finishPagination = false;

        }
        mNotas.addAll(listNotas);

        notifyDataSetChanged();
        LogUser.log(Config.TAG, "Pagination - listNotas size: " + mNotas.size() + " - page size: " + Config.SIZE_PAGE + " - finishPagination: " + finishPagination);
    }

    public void resetData() {
        mNotas.clear();
        finishPagination = false;
    }

    public boolean isFinishPagination() {
        return finishPagination;
    }

    public void setFinishPagination(boolean finishPagination) {
        this.finishPagination = finishPagination;
    }

    public List<RelatorioNotas> getNotas() {
        return mNotas;
    }

    public void add(RelatorioNotas relatorioNotas) {
        mNotas.add(relatorioNotas);
        notifyItemInserted(mNotas.size() - 1);
    }

    public void addLoadingFooter() {
        isLoadingAdded = true;
        add(new RelatorioNotas());
    }

    public void removeLoadingFooter() {
        isLoadingAdded = false;
        int position = mNotas.size() - 1;
        RelatorioNotas item = getItem(position);
        if (item != null) {
            mNotas.remove(position);
            notifyItemRemoved(position);
        }
    }

    public RelatorioNotas getItem(int position) {
        return mNotas.get(position);
    }
}
