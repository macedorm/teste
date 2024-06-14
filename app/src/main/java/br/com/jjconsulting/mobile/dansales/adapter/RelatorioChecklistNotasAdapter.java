package br.com.jjconsulting.mobile.dansales.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import br.com.jjconsulting.mobile.dansales.R;
import br.com.jjconsulting.mobile.dansales.model.RelatorioChecklistNotas;
import br.com.jjconsulting.mobile.dansales.model.RelatorioNotas;
import br.com.jjconsulting.mobile.dansales.util.Config;
import br.com.jjconsulting.mobile.jjlib.util.FormatUtils;
import br.com.jjconsulting.mobile.jjlib.util.LogUser;

public class RelatorioChecklistNotasAdapter extends RecyclerView.Adapter<RelatorioChecklistNotasAdapter.ViewHolder> {

    private static final int ITEM = 0;
    private static final int LOADING = 1;

    private Context mContext;
    private List<RelatorioChecklistNotas> mNotas;
    private boolean finishPagination;
    private boolean isLoadingAdded = false;

    public RelatorioChecklistNotasAdapter(Context context, List<RelatorioChecklistNotas> notas) {
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
                notasView = inflater.inflate(R.layout.item_relatorio_check_list_notas, parent, false);
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
                RelatorioChecklistNotas relatorioNotas = mNotas.get(position);
                String nome = String.format("%s - %s", relatorioNotas.getCodigoCliente(), relatorioNotas.getNomePesquisa());
                holder.mNomeTextView.setText(relatorioNotas.getNomePesquisa());
                holder.mClienteTextView.setText(nome);
                holder.mNota.setText(relatorioNotas.getNota());

                try {
                    holder.mDataTextView.setText(FormatUtils.toDefaultDateBrazilianFormat(FormatUtils.toDate(relatorioNotas.getDataResposta())));
                }catch (Exception ex){
                    holder.mDataTextView.setVisibility(View.GONE);
                }


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
        private TextView mClienteTextView;
        private TextView mDataTextView;
        private TextView mNota;

        public ViewHolder(View itemView) {
            super(itemView);

            switch (itemView.getId()) {
                case ITEM:
                    mNomeTextView = itemView.findViewById(R.id.relatorio_checklist_notas_nome_text_view);
                    mDataTextView = itemView.findViewById(R.id.relatorio_checklist_data_text_view);
                    mNota = itemView.findViewById(R.id.relatorio_checklist_notas_text_view);
                    mClienteTextView = itemView.findViewById(R.id.relatorio_checklist_notas_cliente_text_view);
                    break;
            }
        }
    }

    public void updateData(List<RelatorioChecklistNotas> listNotas) {
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

    public List<RelatorioChecklistNotas> getNotas() {
        return mNotas;
    }

    public void add(RelatorioChecklistNotas relatorioNotas) {
        mNotas.add(relatorioNotas);
        notifyItemInserted(mNotas.size() - 1);
    }

    public void addLoadingFooter() {
        isLoadingAdded = true;
        add(new RelatorioChecklistNotas());
    }

    public void removeLoadingFooter() {
        isLoadingAdded = false;
        int position = mNotas.size() - 1;
        RelatorioChecklistNotas item = getItem(position);
        if (item != null) {
            mNotas.remove(position);
            notifyItemRemoved(position);
        }
    }

    public RelatorioChecklistNotas getItem(int position) {
        return mNotas.get(position);
    }
}
