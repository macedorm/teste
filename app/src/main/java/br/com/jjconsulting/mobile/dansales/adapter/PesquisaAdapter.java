package br.com.jjconsulting.mobile.dansales.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import br.com.jjconsulting.mobile.dansales.R;
import br.com.jjconsulting.mobile.dansales.model.Pesquisa;
import br.com.jjconsulting.mobile.dansales.util.Config;
import br.com.jjconsulting.mobile.dansales.util.PesquisaUtils;
import br.com.jjconsulting.mobile.jjlib.util.FormatUtils;
import br.com.jjconsulting.mobile.jjlib.util.LogUser;

public class PesquisaAdapter extends RecyclerView.Adapter<PesquisaAdapter.ViewHolder> {

    private static final int ITEM = 0;
    private static final int LOADING = 1;

    private Context mContext;
    private List<Pesquisa> mPesquisas;
    private boolean finishPagination;
    private boolean isLoadingAdded = false;

    public PesquisaAdapter(Context context, List<Pesquisa> pesquisas) {
        mContext = context;
        mPesquisas = pesquisas;

        if (mPesquisas == null || mPesquisas.size() < Config.SIZE_PAGE) {
            finishPagination = true;
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View pesquisaView;
        switch (viewType) {
            case ITEM:
                pesquisaView = inflater.inflate(R.layout.item_pesquisa, parent,
                        false);
                pesquisaView.setId(viewType);
                break;
            case LOADING:
                pesquisaView = inflater.inflate(R.layout.item_progress, parent,
                        false);
                pesquisaView.setId(viewType);
                break;
            default:
                pesquisaView = null;
                break;
        }

        return new ViewHolder(pesquisaView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        switch (getItemViewType(position)) {
            case ITEM:
                Pesquisa pesquisa = mPesquisas.get(position);

                holder.mNomePesquisaTextView.setText(pesquisa.getNome());
                holder.mStatusImageView.setImageResource(
                        PesquisaUtils.getStatusResposta(pesquisa.getStatusResposta()));

                try {
                    holder.mDataTextView.setText(mContext.getString(R.string.date_pesquisa,
                            FormatUtils.toDefaultDateFormat(mContext, pesquisa.getDataInicio()),
                            FormatUtils.toDefaultDateFormat(mContext, pesquisa.getDataFim())));
                } catch (Exception ex) {
                    holder.mDataTextView.setVisibility(View.GONE);
                }
                break;
            case LOADING:
                break;
        }
    }

    @Override
    public int getItemViewType(int position) {
        return (position == mPesquisas.size() - 1 && isLoadingAdded) ? LOADING : ITEM;
    }

    @Override
    public int getItemCount() {
        return mPesquisas.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView mStatusImageView;
        private TextView mNomePesquisaTextView;
        private TextView mDataTextView;

        public ViewHolder(View itemView) {
            super(itemView);

            switch (itemView.getId()) {
                case ITEM:
                    mStatusImageView = itemView.findViewById(R.id.pesquisa_status_image_view);
                    mNomePesquisaTextView = itemView.findViewById(R.id.pesquisa_nome_text_view);
                    mDataTextView = itemView.findViewById(R.id.pesquisa_data_text_view);
                    break;
            }
        }
    }

    public void updateData(List<Pesquisa> listPesquisas) {
        finishPagination = listPesquisas.size() == 0 || listPesquisas.size() < Config.SIZE_PAGE;
        mPesquisas.addAll(listPesquisas);
        notifyDataSetChanged();

        LogUser.log(Config.TAG, "Pagination - Lista Pesquisa size: " +
                mPesquisas.size() + " - page size: " + Config.SIZE_PAGE +
                " - finishPagination: " + finishPagination);
    }

    public void resetData() {
        mPesquisas.clear();
        finishPagination = false;
    }

    public boolean isFinishPagination() {
        return finishPagination;
    }

    public void setFinishPagination(boolean finishPagination) {
        this.finishPagination = finishPagination;
    }

    public List<Pesquisa> getPesquisas() {
        return mPesquisas;
    }

    public void addLoadingFooter() {
        isLoadingAdded = true;
        mPesquisas.add(new Pesquisa());
        notifyItemInserted(mPesquisas.size() - 1);
    }

    public void removeLoadingFooter() {
        isLoadingAdded = false;

        int position = mPesquisas.size() - 1;
        Pesquisa item = mPesquisas.get(position);

        if (item != null) {
            mPesquisas.remove(position);
            notifyItemRemoved(position);
        }
    }
}
