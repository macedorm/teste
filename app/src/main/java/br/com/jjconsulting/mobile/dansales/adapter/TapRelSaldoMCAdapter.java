package br.com.jjconsulting.mobile.dansales.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import br.com.jjconsulting.mobile.dansales.R;
import br.com.jjconsulting.mobile.dansales.model.TapRelSaldo;
import br.com.jjconsulting.mobile.dansales.util.Config;
import br.com.jjconsulting.mobile.jjlib.util.FormatUtils;
import br.com.jjconsulting.mobile.jjlib.util.TextUtils;

public class TapRelSaldoMCAdapter extends RecyclerView.Adapter<TapRelSaldoMCAdapter.ViewHolder> {

    private static final int ITEM = 0;
    private static final int LOADING = 1;

    private Context mContext;
    private List<TapRelSaldo> mSaldoMCListList;
    private boolean finishPagination;
    private boolean isLoadingAdded = false;

    public TapRelSaldoMCAdapter(Context context, List<TapRelSaldo> tapList) {
        mContext = context;
        mSaldoMCListList = tapList;

        if (mSaldoMCListList == null || mSaldoMCListList.size() < Config.SIZE_PAGE) {
            finishPagination = true;
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View pedidoView;
        switch (viewType) {
            case ITEM:
                pedidoView = inflater.inflate(R.layout.item_rel_saldo_mc_etap, parent, false);
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
                TapRelSaldo tapList = mSaldoMCListList.get(position);

                holder.mRelMasterContratoTextView.setText(
                        tapList.getMasterContrato());
                holder.mRelCodMasterContratoTextView.setText(mContext.getString(R.string.tap_rel_cod_tap_label,
                        tapList.getCodMaster()));

                holder.mRelDataTextView.setText(mContext.getString(R.string.tap_rel_date_label,
                        tapList.getData()));

                holder.mRelCodCliTextView.setText(mContext.getString(R.string.tap_rel_cod_cli_label,
                        tapList.getCodCliente()));

                if(!TextUtils.isNullOrEmpty(tapList.getNomeCliente())){
                    holder.mRelNomeCliTextView.setText(mContext.getString(R.string.tap_rel_name_cli_label,
                            tapList.getNomeCliente()));
                } else {
                    holder.mRelNomeCliTextView.setVisibility(View.GONE);
                }

                if(!TextUtils.isNullOrEmpty(tapList.getNomeStatus())){
                    holder.mRelNomeStatusTextView.setText(mContext.getString(R.string.tap_rel_name_status_label,
                            tapList.getNomeStatus()));
                } else {
                    holder.mRelNomeStatusTextView.setVisibility(View.GONE);
                }

                holder.mRelVlTapTextView.setText(mContext.getString(R.string.tap_rel_vl_tap_status_label,
                        FormatUtils.toBrazilianRealCurrency(tapList.getValorTap())));

                holder.mRelVlMasterTextView.setText(mContext.getString(R.string.tap_rel_vl_master_status_label,
                        FormatUtils.toBrazilianRealCurrency(tapList.getValorMaster())));

                holder.mRelSaldoMasterTextView.setText(mContext.getString(R.string.tap_rel_name_status_label,
                        FormatUtils.toBrazilianRealCurrency(tapList.getSaldo())));
                break;
            case LOADING:
                break;
        }
    }

    @Override
    public int getItemViewType(int position) {
        return (position == mSaldoMCListList.size() - 1 && isLoadingAdded) ? LOADING : ITEM;
    }

    @Override
    public int getItemCount() {
        return mSaldoMCListList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView mRelMasterContratoTextView;
        private TextView mRelCodMasterContratoTextView;
        private TextView mRelDataTextView;
        private TextView mRelCodCliTextView;
        private TextView mRelNomeCliTextView;
        private TextView mRelNomeStatusTextView;
        private TextView mRelVlTapTextView;
        private TextView mRelVlMasterTextView;
        private TextView mRelSaldoMasterTextView;


        public ViewHolder(View itemView) {
            super(itemView);
            switch (itemView.getId()) {
                case ITEM:
                    mRelMasterContratoTextView = itemView.findViewById(R.id.etap_rel_mc_text_view);
                    mRelCodMasterContratoTextView = itemView.findViewById(R.id.etap_rel_cod_master_tap_text_view);
                    mRelDataTextView = itemView.findViewById(R.id.etap_rel_data_text_view);
                    mRelCodCliTextView = itemView.findViewById(R.id.etap_rel_nome_cli_text_view);
                    mRelNomeCliTextView = itemView.findViewById(R.id.etap_rel_nome_cli_text_view);
                    mRelDataTextView = itemView.findViewById(R.id.etap_rel_data_text_view);
                    mRelNomeStatusTextView = itemView.findViewById(R.id.etap_rel_nome_status_text_view);
                    mRelVlTapTextView = itemView.findViewById(R.id.etap_rel_vl_text_view);
                    mRelVlMasterTextView = itemView.findViewById(R.id.etap_rel_vl_master_text_view);
                    mRelSaldoMasterTextView = itemView.findViewById(R.id.etap_rel_saldo_master_text_view);

                    break;
            }
        }
    }


    public void updateDataItem(TapRelSaldo tapList, int position) {
        mSaldoMCListList.set(position, tapList);
        notifyDataSetChanged();
    }

    public void updateData(List<TapRelSaldo> tapList) {
        if (tapList.size() == 0 || tapList.size() < Config.SIZE_PAGE) {
            finishPagination = true;
        } else {
            finishPagination = false;

        }
        mSaldoMCListList = tapList;

        notifyDataSetChanged();
    }

    public void resetData() {
        mSaldoMCListList.clear();
        finishPagination = false;
    }

    public boolean isFinishPagination() {
        return finishPagination;
    }

    public void setFinishPagination(boolean finishPagination) {
        this.finishPagination = finishPagination;
    }

    public List<TapRelSaldo> getListETap() {
        return mSaldoMCListList;
    }

    public void add(TapRelSaldo tapList) {
        mSaldoMCListList.add(tapList);
        notifyItemInserted(mSaldoMCListList.size() - 1);
    }

    public void addLoadingFooter() {
        isLoadingAdded = true;
        add(new TapRelSaldo());
    }

    public void removeLoadingFooter() {
        isLoadingAdded = false;
        int position = mSaldoMCListList.size() - 1;
        TapRelSaldo item = getItem(position);
        if (item != null) {
            mSaldoMCListList.remove(position);
            notifyItemRemoved(position);
        }
    }

    public TapRelSaldo getItem(int position) {
        return mSaldoMCListList.get(position);
    }
}
