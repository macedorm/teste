package br.com.jjconsulting.mobile.dansales.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import br.com.jjconsulting.mobile.dansales.R;
import br.com.jjconsulting.mobile.dansales.model.RelatorioObjetivo;
import br.com.jjconsulting.mobile.dansales.util.Config;
import br.com.jjconsulting.mobile.jjlib.util.FormatUtils;
import br.com.jjconsulting.mobile.jjlib.util.LogUser;

public class RelatorioObjetivoAdapter extends RecyclerView.Adapter<RelatorioObjetivoAdapter.ViewHolder> {

    private static final int ITEM = 0;
    private static final int LOADING = 1;

    private Context mContext;
    private List<RelatorioObjetivo> mRelatorioObjetivo;
    private boolean finishPagination;
    private boolean isLoadingAdded = false;

    public RelatorioObjetivoAdapter(Context context, List<RelatorioObjetivo> relatorioObjetivo) {
        mContext = context;
        mRelatorioObjetivo = relatorioObjetivo;

        if (mRelatorioObjetivo.size() < Config.SIZE_PAGE) {
            finishPagination = true;
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View objetivoView;

        switch (viewType) {
            case ITEM:
                objetivoView = inflater.inflate(R.layout.item_relatorio_objetivo, parent, false);
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
                RelatorioObjetivo relatorioObjetivo = mRelatorioObjetivo.get(position);

                holder.mNomeTextView.setText(relatorioObjetivo.getNome());
                holder.mPerdaTextView.setText(FormatUtils.toDoubleFormat(relatorioObjetivo.getPerda()));
                holder.mObjTextView.setText(FormatUtils.toDoubleFormat(relatorioObjetivo.getObj()));
                holder.mRealTextView.setText(FormatUtils.toDoubleFormat(relatorioObjetivo.getReal()));
                holder.mCobTextView.setText(FormatUtils.toDoubleFormat(relatorioObjetivo.getCob()));
                holder.mRefTextView.setText(FormatUtils.toDoubleFormat(relatorioObjetivo.getRaf()));

                if (relatorioObjetivo.getRaf() < 0) {
                    holder.mRefTextView.setTextColor(mContext.getResources().getColor(R.color.statusNegativo));
                } else {
                    holder.mRefTextView.setTextColor(mContext.getResources().getColor(R.color.statusPositivo));
                }

                break;
            case LOADING:

                break;
        }
    }

    @Override
    public int getItemViewType(int position) {
        return (position == mRelatorioObjetivo.size() - 1 && isLoadingAdded) ? LOADING : ITEM;
    }

    @Override
    public int getItemCount() {
        return mRelatorioObjetivo.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView mNomeTextView;
        private TextView mPerdaTextView;
        private TextView mObjTextView;
        private TextView mRealTextView;
        private TextView mCobTextView;
        private TextView mRefTextView;

        public ViewHolder(View itemView) {
            super(itemView);

            switch (itemView.getId()) {
                case ITEM:
                    mNomeTextView = itemView.findViewById(R.id.relatorio_objetivos_nome_text_view);
                    mPerdaTextView = itemView.findViewById(R.id.relatorio_objetivos_perda_text_view);
                    mObjTextView = itemView.findViewById(R.id.relatorio_objetivos_obj_text_view);
                    mRealTextView = itemView.findViewById(R.id.relatorio_objetivos_real_text_view);
                    mCobTextView = itemView.findViewById(R.id.relatorio_objetivos_cob_text_view);
                    mRefTextView = itemView.findViewById(R.id.relatorio_objetivos_ref_text_view);
                    break;
            }
        }
    }

    public void updateData(List<RelatorioObjetivo> listNotas) {
        if (listNotas.size() == 0 || listNotas.size() < Config.SIZE_PAGE) {
            finishPagination = true;
        } else {
            finishPagination = false;

        }
        mRelatorioObjetivo.addAll(listNotas);

        notifyDataSetChanged();
        LogUser.log(Config.TAG, "Pagination - listObjetivo size: " + mRelatorioObjetivo.size() + " - page size: " + Config.SIZE_PAGE + " - finishPagination: " + finishPagination);
    }

    public void resetData() {
        mRelatorioObjetivo.clear();
        finishPagination = false;
    }

    public boolean isFinishPagination() {
        return finishPagination;
    }

    public void setFinishPagination(boolean finishPagination) {
        this.finishPagination = finishPagination;
    }

    public List<RelatorioObjetivo> getOjetivos() {
        return mRelatorioObjetivo;
    }

    public void add(RelatorioObjetivo relatorioObjetivo) {
        mRelatorioObjetivo.add(relatorioObjetivo);
        notifyItemInserted(mRelatorioObjetivo.size() - 1);
    }

    public void addLoadingFooter() {
        isLoadingAdded = true;
        add(new RelatorioObjetivo());
    }

    public void removeLoadingFooter() {
        isLoadingAdded = false;
        int position = mRelatorioObjetivo.size() - 1;
        RelatorioObjetivo item = getItem(position);
        if (item != null) {
            mRelatorioObjetivo.remove(position);
            notifyItemRemoved(position);
        }
    }

    public RelatorioObjetivo getItem(int position) {
        return mRelatorioObjetivo.get(position);
    }

}
