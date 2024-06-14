package br.com.jjconsulting.mobile.dansales.adapter;

import android.content.Context;
import android.graphics.PorterDuff;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import br.com.jjconsulting.mobile.dansales.R;
import br.com.jjconsulting.mobile.dansales.model.TapActionType;
import br.com.jjconsulting.mobile.dansales.model.TapList;
import br.com.jjconsulting.mobile.dansales.util.Config;
import br.com.jjconsulting.mobile.dansales.util.ETapUtils;
import br.com.jjconsulting.mobile.jjlib.util.FormatUtils;
import br.com.jjconsulting.mobile.jjlib.util.LogUser;

public class TapListAdapter extends RecyclerView.Adapter<TapListAdapter.ViewHolder> {

    private static final int ITEM = 0;
    private static final int LOADING = 1;

    private Context mContext;
    private List<TapList> mListEtapList;
    private boolean finishPagination;
    private boolean isLoadingAdded = false;
    private TapActionType mType;

    public TapListAdapter(Context context, List<TapList> tapList, TapActionType type) {
        mContext = context;
        mListEtapList = tapList;
        mType = type;

        if (mListEtapList == null || mListEtapList.size() < Config.SIZE_PAGE) {
            finishPagination = true;
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View pedidoView;
        switch (viewType) {
            case ITEM:
                pedidoView = inflater.inflate(R.layout.item_list_etap, parent, false);
                pedidoView.setId(viewType);
                break;
            case LOADING:
                pedidoView = inflater.inflate(R.layout.item_progress, parent, false);
                pedidoView.setId(viewType);
                break;
            default:
                pedidoView = null;
                break;
        }

        return new ViewHolder(pedidoView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        switch (getItemViewType(position)) {
            case ITEM:
                TapList tapList = mListEtapList.get(position);
                holder.mStatusImageView.setImageResource(ETapUtils.getColorStatus(tapList.getStatus()));

                if (tapList.getCliCod() != null && tapList.getCliCod().trim().length() > 0) {
                    holder.mClienteETapTextView.setText(String.format("%s %s", tapList.getCliCod(),
                            tapList.getCliNome()));
                } else {
                    holder.mClienteETapTextView.setText(mContext.getString(R.string.cliente_indisponivel));
                }

                holder.mCodigoETapTextView.setText(mContext.getString(R.string.tap_cod_label,
                        tapList.getCodigo()));

                try {
                    holder.mDataCadastroETapTextView.setText(mContext.getString(R.string.pedido_emissao_label, FormatUtils.toDefaultDateAndHourFormat(mContext,
                            FormatUtils.toJsonDateConvert(tapList.getDataInc()))));

                } catch (Exception ex) {
                    holder.mDataCadastroETapTextView.setVisibility(View.GONE);
                    LogUser.log(Config.TAG, ex.toString());
                }

                if(tapList.getMasterContrato() != null){
                    holder.mMasterContratoETapTextView.setText(mContext.getString(R.string.tap_master_contrato_label,
                            tapList.getMasterContrato()));
                } else {
                    holder.mMasterContratoETapTextView.setVisibility(View.GONE);
                }

                holder.mMesAcaoETapTextView.setText(mContext.getString(R.string.tap_mes_acao_label,
                        tapList.getAnoMesAcao()));
                holder.mValorEstimadoETapTextView.setText(mContext.getString(R.string.tap_vl_estimado_label,
                        FormatUtils.toBrazilianRealCurrency(tapList.getVlEst())));
                holder.mValorApuradoETapTextView.setText(mContext.getString(R.string.tap_vl_apurado_label,
                        FormatUtils.toBrazilianRealCurrency(tapList.getVlApur())));

                if(mType == TapActionType.TAP_CONSULTA ){
                    holder.mCheckBoxLote.setVisibility(View.GONE);
                } else {
                    if(tapList.isCheckdLote()){
                        holder.mCheckBoxLote.setVisibility(View.VISIBLE);
                        holder.mCheckBoxLote.setColorFilter(mContext.getResources().getColor(R.color.colorPrimary), android.graphics.PorterDuff.Mode.SRC_IN);

                        holder.mBackgroundRelativeLinaer.setBackgroundColor(mContext.getResources().getColor(R.color.backgroundColorDark));
                    } else {
                        if(tapList.isAprov()){
                            holder.mCheckBoxLote.setColorFilter(mContext.getResources().getColor(R.color.icon_lote_enable), PorterDuff.Mode.SRC_IN);
                            holder.mCheckBoxLote.setVisibility(View.VISIBLE);
                        } else{
                            holder.mCheckBoxLote.setVisibility(View.GONE);
                        }

                        holder.mBackgroundRelativeLinaer.setBackgroundColor(mContext.getResources().getColor(R.color.backgroundColor));

                    }
                }

                break;
            case LOADING:
                break;
        }
    }

    @Override
    public int getItemViewType(int position) {
        return (position == mListEtapList.size() - 1 && isLoadingAdded) ? LOADING : ITEM;
    }

    @Override
    public int getItemCount() {
        return mListEtapList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private RelativeLayout mBackgroundRelativeLinaer;
        private ImageView mStatusImageView;
        private TextView mClienteETapTextView;
        private TextView mCodigoETapTextView;
        private TextView mDataCadastroETapTextView;
        private TextView mMasterContratoETapTextView;
        private TextView mMesAcaoETapTextView;
        private TextView mValorEstimadoETapTextView;
        private TextView mValorApuradoETapTextView;
        private ImageView mCheckBoxLote;


        public ViewHolder(View itemView) {
            super(itemView);
            switch (itemView.getId()) {
                case ITEM:
                    mStatusImageView = itemView.findViewById(R.id.etap_status_image_view);
                    mClienteETapTextView = itemView.findViewById(R.id.etap_cliente_nome_text_view);
                    mCodigoETapTextView = itemView.findViewById(R.id.etap_codigo_tap_text_view);
                    mDataCadastroETapTextView = itemView.findViewById(R.id.etap_data_cad_text_view);
                    mMasterContratoETapTextView = itemView.findViewById(R.id.etap_master_contrato_text_view);
                    mMesAcaoETapTextView = itemView.findViewById(R.id.etap_mes_acao_text_view);
                    mValorEstimadoETapTextView = itemView.findViewById(R.id.etap_valor_estimado_tap_text_view);
                    mValorApuradoETapTextView = itemView.findViewById(R.id.etap_valor_apurado_tap_text_view);
                    mCheckBoxLote = itemView.findViewById(R.id.etap_status_check_box);
                    mBackgroundRelativeLinaer = itemView.findViewById(R.id.background_relative_linear);

                    break;
            }
        }
    }


    public void updateDataItem(TapList tapList, int position) {
        tapList.setCheckdLote(!tapList.isCheckdLote());

        mListEtapList.set(position, tapList);
        notifyDataSetChanged();
    }

    public void updateData(List<TapList> tapList) {
        if (tapList.size() == 0 || tapList.size() < Config.SIZE_PAGE) {
            finishPagination = true;
        } else {
            finishPagination = false;

        }
        mListEtapList = tapList;

        notifyDataSetChanged();
    }

    public void resetData() {
        mListEtapList.clear();
        finishPagination = false;
    }

    public boolean isFinishPagination() {
        return finishPagination;
    }

    public void setFinishPagination(boolean finishPagination) {
        this.finishPagination = finishPagination;
    }

    public List<TapList> getListETap() {
        return mListEtapList;
    }

    public void add(TapList tapList) {
        mListEtapList.add(tapList);
        notifyItemInserted(mListEtapList.size() - 1);
    }

    public void addLoadingFooter() {
        isLoadingAdded = true;
        add(new TapList());
    }

    public void removeLoadingFooter() {
        isLoadingAdded = false;
        int position = mListEtapList.size() - 1;
        TapList item = getItem(position);
        if (item != null) {
            mListEtapList.remove(position);
            notifyItemRemoved(position);
        }
    }

    public TapList getItem(int position) {
        return mListEtapList.get(position);
    }
}
