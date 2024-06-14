package br.com.jjconsulting.mobile.dansales.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import br.com.jjconsulting.mobile.dansales.R;
import br.com.jjconsulting.mobile.dansales.model.Pedido;
import br.com.jjconsulting.mobile.dansales.model.Pesquisa;
import br.com.jjconsulting.mobile.dansales.model.PesquisaPergunta;
import br.com.jjconsulting.mobile.dansales.model.RotaTarefas;
import br.com.jjconsulting.mobile.dansales.model.RotaGuiadaTaskType;
import br.com.jjconsulting.mobile.dansales.util.Config;
import br.com.jjconsulting.mobile.dansales.util.RotaGuiadaUtils;
import br.com.jjconsulting.mobile.jjlib.util.FormatUtils;
import br.com.jjconsulting.mobile.jjlib.util.LogUser;

public class RotaGuiadaResumoAdapter extends RecyclerView.Adapter<RotaGuiadaResumoAdapter.ViewHolder> {

    private static final int ITEM = 0;
    private List<RotaTarefas> mRotasResumo;

    private Context mContext;

    public RotaGuiadaResumoAdapter(List<RotaTarefas> rotas, Context context) {
        mContext = context;
        mRotasResumo = rotas;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_rota_guiada_resumo, parent, false);
        view.setId(viewType);
        return new ViewHolder(view);
    }   

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        try {

            RotaTarefas rotas = mRotasResumo.get(position);
            holder.mStatusImageView.setImageResource(
                    RotaGuiadaUtils.getStatusRGImageResourceId(true, rotas.getStatus().getValue(), false));

            if (rotas.getStatus() == RotaGuiadaTaskType.PEDIDO) {
                Pedido pedido = rotas.getPedido();

                if (pedido != null) {
                    holder.mStatusImageView.setImageResource(
                            RotaGuiadaUtils.getStatusRGTAKSImageResourceId(rotas.getStatus().getValue(), pedido.getStatus().getCodigo(), rotas.getAtividJust()));

                    holder.mTitleTextView.setText(pedido.getCodigo());
                    holder.mDescriptionTextView.setText("" + FormatUtils.toBrazilianRealCurrency(pedido.getValorTotal()));
                }

            } else if (rotas.getStatus() == RotaGuiadaTaskType.PESQUISA) {
                Pesquisa pesquisa = rotas.getPesquisa();

                if (pesquisa != null) {
                    holder.mStatusImageView.setImageResource(
                            RotaGuiadaUtils.getStatusRGTAKSImageResourceId(rotas.getStatus().getValue(), pesquisa.getStatusResposta(), rotas.getAtividJust()));
                    holder.mTitleTextView.setText(pesquisa.getNome() + (pesquisa.isAtividadeObrigatoria() ? "*":""));
                    holder.mDescriptionTextView.setText(mContext.getString(R.string.date_pesquisa,
                            FormatUtils.toDefaultDateFormat(mContext, pesquisa.getDataInicio()),
                            FormatUtils.toDefaultDateFormat(mContext, pesquisa.getDataFim())) );
                }

            }


        }catch (Exception ex){
            LogUser.log("verificar "  + ex.toString());
        }
    }

    @Override
    public int getItemViewType(int position) {
        return ITEM;
    }

    @Override
    public int getItemCount() {
        return mRotasResumo.size();
    }

    public void updateItem(RotaTarefas rotaTarefas, int index) {
        mRotasResumo.set(index, rotaTarefas);
        notifyItemChanged(index);
    }

    public void updateData(List<RotaTarefas> listRotas) {
        mRotasResumo.addAll(listRotas);
        notifyDataSetChanged();
        LogUser.log(Config.TAG, "Pagination - RotaResumo size: " + mRotasResumo.size() + " - page size: " + Config.SIZE_PAGE);
    }

    public void resetData() {
        mRotasResumo.clear();
    }

    public RotaTarefas getItem(int position) {
        return mRotasResumo.get(position);
    }

    public List<RotaTarefas> getRotasResumo() {
        return mRotasResumo;
    }

    public void setRotasResumo(List<RotaTarefas> mRotasResumo) {
        this.mRotasResumo = mRotasResumo;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView mStatusImageView;
        private TextView mTitleTextView;
        private TextView mDescriptionTextView;
        private RelativeLayout mCointainerRelativeLayout;

        public ViewHolder(View itemView) {
            super(itemView);

            switch (itemView.getId()) {
                case ITEM:
                    mStatusImageView = itemView.findViewById(R.id.rg_resume_status_image_view);
                    mTitleTextView = itemView.findViewById(R.id.rg_resume_title_text_view);
                    mDescriptionTextView = itemView.findViewById(R.id.rg_resume_description_text_view);
                    mCointainerRelativeLayout = itemView.findViewById(R.id.rg_resume_cointainer_relative_layout);
                    break;
            }
        }
    }

    public int getSizeItem(){
        if(mContext != null){
            return (int)mContext.getResources().getDimension(R.dimen.rg_task_small_list_size);
        } else {
            return 20;
        }

    }

    public Context getContext() {
        return mContext;
    }

    public void setContext(Context mContext) {
        this.mContext = mContext;
    }
}
