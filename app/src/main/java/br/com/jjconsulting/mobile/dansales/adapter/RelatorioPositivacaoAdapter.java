package br.com.jjconsulting.mobile.dansales.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import br.com.jjconsulting.mobile.dansales.R;
import br.com.jjconsulting.mobile.dansales.model.RelatorioPositivacao;
import br.com.jjconsulting.mobile.dansales.util.Config;
import br.com.jjconsulting.mobile.jjlib.util.LogUser;

public class RelatorioPositivacaoAdapter extends RecyclerView.Adapter<RelatorioPositivacaoAdapter.ViewHolder> {

    private static final int ITEM = 0;
    private static final int LOADING = 1;

    private Context mContext;
    private List<RelatorioPositivacao> mRelatorioPositivacao;
    private boolean finishPagination;
    private boolean isLoadingAdded = false;

    public RelatorioPositivacaoAdapter(Context context, List<RelatorioPositivacao> relatorioPositivacao) {
        mContext = context;
        mRelatorioPositivacao = relatorioPositivacao;

        if (relatorioPositivacao.size() < Config.SIZE_PAGE) {
            finishPagination = true;
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View objetivoView;

        switch (viewType) {
            case ITEM:
                objetivoView = inflater.inflate(R.layout.item_relatorio_positivacao, parent, false);
                objetivoView.setId(viewType);
                break;
            case LOADING:
                objetivoView = inflater.inflate(R.layout.item_progress, parent, false);
                objetivoView.setId(viewType);
                break;
            default:
                objetivoView = null;
                break;
        }

        return new ViewHolder(objetivoView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        switch (getItemViewType(position)) {
            case ITEM:
                RelatorioPositivacao relatorioPositivacao = mRelatorioPositivacao.get(position);

                if(position == 0){
                    holder.mTitleTextView.setText(mContext.getString(R.string.relatorio_resumo_positivacao));
                } else {
                    holder.mTitleTextView.setText(relatorioPositivacao.getNome());
                }

                StringBuilder item = new StringBuilder();
              /*  item.append(mContext.getString(R.string.relatorio_planejado) + ": " + relatorioPositivacao.getPlanejadoQtd() + "\n");
                item.append(mContext.getString(R.string.relatorio_aderencia) + ": " +  relatorioPositivacao.getAderenciaQtd() + " - AD(%): " + relatorioPositivacao.getAderenciaPerc() + "%\n");
                item.append(mContext.getString(R.string.relatorio_fora) + ": " +  relatorioPositivacao.getForaPlanoQtd() + " - FP(%): " + relatorioPositivacao.getForaPlanoPerc() + "%\n");
                item.append(mContext.getString(R.string.relatorio_produtividade) + ": " +  relatorioPositivacao.getProdutivoPerc() + "%\n");
                item.append(mContext.getString(R.string.relatorio_perdido) + ": " +  relatorioPositivacao.getPerdidoPerc() + "%\n");
*/

                item.append(mContext.getString(R.string.relatorio_planejado) + "\n");
                item.append(mContext.getString(R.string.relatorio_aderencia) + "\n");
                item.append(mContext.getString(R.string.relatorio_fora) + " \n");
                item.append(mContext.getString(R.string.relatorio_produtividade) + "\n");
                holder.mItemTextView.setText(item.toString());

                StringBuilder columnOne = new StringBuilder();
                columnOne.append(relatorioPositivacao.getPlanejadoQtd() + " \n");
                columnOne.append(relatorioPositivacao.getAderenciaQtd() + " \n");
                columnOne.append(relatorioPositivacao.getForaPlanoQtd() + " \n");
                columnOne.append(relatorioPositivacao.getProdutivoPerc() + "%\n");
                holder.mColumnOnePositivacaoTextView.setText(columnOne.toString());

                StringBuilder columnTwo = new StringBuilder();
                columnTwo.append(" \n");
                columnTwo.append("Ad(%) \n");
                columnTwo.append("FP(%) \n");
                columnTwo.append(mContext.getString(R.string.relatorio_perdido) + "\n");
                holder.mColumnTwoPositivacaoTextView.setText(columnTwo.toString());

                StringBuilder columnThree = new StringBuilder();
                columnThree.append(" \n");
                columnThree.append(relatorioPositivacao.getAderenciaPerc() + "%\n");
                columnThree.append(relatorioPositivacao.getForaPlanoPerc() + "%\n");
                columnThree.append(relatorioPositivacao.getPerdidoPerc() + "%\n");
                holder.mColumnThreePositivacaoTextView.setText(columnThree.toString());

                break;
            case LOADING:

                break;
        }
    }

    @Override
    public int getItemViewType(int position) {
        return (position == mRelatorioPositivacao.size() - 1 && isLoadingAdded) ? LOADING : ITEM;
    }

    @Override
    public int getItemCount() {
        return mRelatorioPositivacao.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView mTitleTextView;
        private TextView mItemTextView;
        private TextView mColumnOnePositivacaoTextView;
        private TextView mColumnTwoPositivacaoTextView;
        private TextView mColumnThreePositivacaoTextView;

        public ViewHolder(View itemView) {
            super(itemView);

            switch (itemView.getId()) {
                case ITEM:
                    mTitleTextView = itemView.findViewById(R.id.tile_positivacao_text_view);
                    mItemTextView = itemView.findViewById(R.id.item_positivacao_text_view);
                    mColumnOnePositivacaoTextView = itemView.findViewById(R.id.column_one_positivacao_text_view);
                    mColumnTwoPositivacaoTextView = itemView.findViewById(R.id.column_two_positivacao_text_view);
                    mColumnThreePositivacaoTextView = itemView.findViewById(R.id.column_three_positivacao_text_view);
                    break;
            }
        }
    }

    public void updateData(List<RelatorioPositivacao> listPositivacao) {
        if (listPositivacao.size() == 0 || listPositivacao.size() < Config.SIZE_PAGE) {
            finishPagination = true;
        } else {
            finishPagination = false;

        }
        mRelatorioPositivacao.addAll(listPositivacao);

        notifyDataSetChanged();
        LogUser.log(Config.TAG, "Pagination - listObjetivo size: " + listPositivacao.size() + " - page size: " + Config.SIZE_PAGE + " - finishPagination: " + finishPagination);
    }

    public void resetData() {
        mRelatorioPositivacao.clear();
        finishPagination = false;
    }

    public boolean isFinishPagination() {
        return finishPagination;
    }

    public void setFinishPagination(boolean finishPagination) {
        this.finishPagination = finishPagination;
    }

    public List<RelatorioPositivacao> getOjetivos() {
        return mRelatorioPositivacao;
    }

    public void add(RelatorioPositivacao relatorioPositivacao) {
        mRelatorioPositivacao.add(relatorioPositivacao);
        notifyItemInserted(mRelatorioPositivacao.size() - 1);
    }

    public void addLoadingFooter() {
        isLoadingAdded = true;
        add(new RelatorioPositivacao());
    }

    public void removeLoadingFooter() {
        isLoadingAdded = false;
        int position = mRelatorioPositivacao.size() - 1;
        RelatorioPositivacao item = getItem(position);
        if (item != null) {
            mRelatorioPositivacao.remove(position);
            notifyItemRemoved(position);
        }
    }

    public RelatorioPositivacao getItem(int position) {
        return mRelatorioPositivacao.get(position);
    }

}
