package br.com.jjconsulting.mobile.dansales.adapter;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import br.com.jjconsulting.mobile.dansales.R;
import br.com.jjconsulting.mobile.dansales.util.Config;
import br.com.jjconsulting.mobile.jjlib.util.LogUser;

public class ContratoAdapter extends RecyclerView.Adapter<ContratoAdapter.ViewHolder> {

    private static final int ITEM = 0;
    private static final int LOADING = 1;
    private List<String> mContratos;
    private boolean finishPagination;
    private boolean isLoadingAdded = false;

    public ContratoAdapter(List<String> contratos) {
        mContratos = contratos;

        if (mContratos.size() < Config.SIZE_PAGE) {
            finishPagination = true;
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View contratoView;

        switch (viewType) {
            case ITEM:
                contratoView = inflater.inflate(R.layout.item_contrato, parent, false);
                contratoView.setId(viewType);
                break;
            case LOADING:
                contratoView = inflater.inflate(R.layout.item_progress, parent, false);
                contratoView.setId(viewType);
                break;
            default:
                contratoView = null;
                break;
        }

        return new ViewHolder(contratoView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        switch (getItemViewType(position)) {

            case ITEM:
                String item = mContratos.get(position);
                break;
            case LOADING:

                break;
        }
    }

    @Override
    public int getItemViewType(int position) {
        return (position == mContratos.size() - 1 && isLoadingAdded) ? LOADING : ITEM;
    }

    @Override
    public int getItemCount() {
        return mContratos.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView mTitleTextView;
        private TextView mContentTextView;

        public ViewHolder(View itemView) {
            super(itemView);

            switch (itemView.getId()) {
                case ITEM:
                    mTitleTextView = itemView.findViewById(R.id.contrato_title_text_view);
                    mContentTextView = itemView.findViewById(R.id.contrato_content_text_view);
                    break;
            }
        }
    }

    public void updateData(List<String> listClientes) {
        if (listClientes.size() == 0 || listClientes.size() < Config.SIZE_PAGE) {
            finishPagination = true;
        } else {
            finishPagination = false;

        }
        mContratos.addAll(listClientes);

        notifyDataSetChanged();
        LogUser.log(Config.TAG, "Pagination - listClientes size: " + mContratos.size() + " - page size: " + Config.SIZE_PAGE + " - finishPagination: " + finishPagination);
    }

    public void resetData() {
        mContratos.clear();
        finishPagination = false;
    }

    public boolean isFinishPagination() {
        return finishPagination;
    }

    public void setFinishPagination(boolean finishPagination) {
        this.finishPagination = finishPagination;
    }

    public List<String> getContratos() {
        return mContratos;
    }

    public void add(String contrato) {
        mContratos.add(contrato);
        notifyItemInserted(mContratos.size() - 1);
    }

    public void addLoadingFooter() {
        isLoadingAdded = true;
        add(new String());
    }

    public void removeLoadingFooter() {
        isLoadingAdded = false;
        int position = mContratos.size() - 1;
        String item = getItem(position);
        if (item != null) {
            mContratos.remove(position);
            notifyItemRemoved(position);
        }
    }

    public String getItem(int position) {
        return mContratos.get(position);
    }
}
