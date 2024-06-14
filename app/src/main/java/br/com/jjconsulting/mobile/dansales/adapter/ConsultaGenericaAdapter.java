package br.com.jjconsulting.mobile.dansales.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import br.com.jjconsulting.mobile.dansales.R;
import br.com.jjconsulting.mobile.dansales.model.ConsultaGenerica;
import br.com.jjconsulting.mobile.dansales.util.Config;
import br.com.jjconsulting.mobile.jjlib.util.FormatUtils;
import br.com.jjconsulting.mobile.jjlib.util.LogUser;

public class ConsultaGenericaAdapter extends RecyclerView.Adapter<ConsultaGenericaAdapter.ViewHolder> {

    private static final int ITEM = 0;
    private static final int LOADING = 1;

    private Context mContext;
    private List<ConsultaGenerica> mConsultaGenerica;
    private boolean finishPagination;
    private boolean isLoadingAdded = false;

    public ConsultaGenericaAdapter(Context context, List<ConsultaGenerica> consulta) {
        mContext = context;
        mConsultaGenerica = consulta;

        if (mConsultaGenerica.size() < Config.SIZE_PAGE) {
            finishPagination = true;
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View consultaGenericaView;

        switch (viewType) {
            case ITEM:
                consultaGenericaView = inflater.inflate(R.layout.item_requisicao, parent, false);
                consultaGenericaView.setId(viewType);
                break;
            case LOADING:
                consultaGenericaView = inflater.inflate(R.layout.item_progress, parent, false);
                consultaGenericaView.setId(viewType);
                break;
            default:
                consultaGenericaView = null;
                break;
        }

        return new ViewHolder(consultaGenericaView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        switch (getItemViewType(position)) {
            case ITEM:
                ConsultaGenerica consultaGenerica = mConsultaGenerica.get(position);
                String nome = String.format("%s - %s", consultaGenerica.getId(), consultaGenerica.getNome());

                holder.mNomeTextView.setText(nome);

                holder.mTipoCadastroTextView.setText(consultaGenerica.getDescTipoCadastro() + "");
                if(consultaGenerica.getCnpj() != null){
                    holder.mCNPJTextView.setText(FormatUtils.maskCNPJ(consultaGenerica.getCnpj()));
                }
                holder.mInclusaoTextView.setText(consultaGenerica.getDataInclusao());
                holder.mStatusTextView.setText(consultaGenerica.getDescStatus() + "");
                holder.mBandeiraTextView.setText(consultaGenerica.getBandeira());
                holder.mCidadeTextView.setText(consultaGenerica.getCidade());
                holder.mUFTextView.setText(consultaGenerica.getUf());
                holder.mSolicitanteTextView.setText(consultaGenerica.getNomeSolicitante());

                try {
                   holder.mInclusaoTextView.setText(FormatUtils.toDefaultDateHoursBrazilianFormat(FormatUtils.toDate(consultaGenerica.getDataInclusao())));
                }catch (Exception ex){
                    LogUser.log(ex.getMessage());
                }

                break;
            case LOADING:

                break;
        }
    }

    @Override
    public int getItemViewType(int position) {
        return (position == mConsultaGenerica.size() - 1 && isLoadingAdded) ? LOADING : ITEM;
    }

    @Override
    public int getItemCount() {
        return mConsultaGenerica.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView mNomeTextView;
        private TextView mTipoCadastroTextView;
        private TextView mCNPJTextView;
        private TextView mSolicitanteTextView;
        private TextView mStatusTextView;
        private TextView mInclusaoTextView;
        private TextView mBandeiraTextView;
        private TextView mCidadeTextView;
        private TextView mUFTextView;

        public ViewHolder(View itemView) {
            super(itemView);

            switch (itemView.getId()) {
                case ITEM:
                    mNomeTextView = itemView.findViewById(R.id.cg_nome_text_view);
                    mTipoCadastroTextView = itemView.findViewById(R.id.cg_tipo_cadastro_text_view);
                    mCNPJTextView = itemView.findViewById(R.id.cg_cnpj_text_view);
                    mStatusTextView = itemView.findViewById(R.id.cg_status_text_view);
                    mInclusaoTextView = itemView.findViewById(R.id.cg_inclusao_text_view);
                    mBandeiraTextView = itemView.findViewById(R.id.cg_bandeira_text_view);
                    mCidadeTextView = itemView.findViewById(R.id.cg_cidade_text_view);
                    mUFTextView = itemView.findViewById(R.id.cg_uf_text_view);
                    mSolicitanteTextView = itemView.findViewById(R.id.cg_solicitante_text_view);
                    break;
            }
        }
    }

    public void updateData(List<ConsultaGenerica> listRequisao) {
        if (listRequisao.size() == 0 || listRequisao.size() < Config.SIZE_PAGE) {
            finishPagination = true;
        } else {
            finishPagination = false;

        }
        mConsultaGenerica.addAll(listRequisao);

        notifyDataSetChanged();
        LogUser.log(Config.TAG, "Pagination - requisao size: " + mConsultaGenerica.size() + " - page size: " + Config.SIZE_PAGE + " - finishPagination: " + finishPagination);
    }

    public void resetData() {
        mConsultaGenerica.clear();
        finishPagination = false;
    }

    public boolean isFinishPagination() {
        return finishPagination;
    }

    public void setFinishPagination(boolean finishPagination) {
        this.finishPagination = finishPagination;
    }

    public List<ConsultaGenerica> getRequisao() {
        return mConsultaGenerica;
    }

    public void add(ConsultaGenerica requisicao) {
        mConsultaGenerica.add(requisicao);
        notifyItemInserted(mConsultaGenerica.size() - 1);
    }

    public void addLoadingFooter() {
        isLoadingAdded = true;
        add(new ConsultaGenerica());
    }

    public void removeLoadingFooter() {
        isLoadingAdded = false;
        int position = mConsultaGenerica.size() - 1;
        ConsultaGenerica item = getItem(position);
        if (item != null) {
            mConsultaGenerica.remove(position);
            notifyItemRemoved(position);
        }
    }

    public ConsultaGenerica getItem(int position) {
        return mConsultaGenerica.get(position);
    }
}
