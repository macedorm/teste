package br.com.jjconsulting.mobile.dansales.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import br.com.jjconsulting.mobile.dansales.R;
import br.com.jjconsulting.mobile.dansales.model.Anexo;
import br.com.jjconsulting.mobile.dansales.util.Config;
import br.com.jjconsulting.mobile.jjlib.util.LogUser;

public class TapItemAnexosAdapter extends RecyclerView.Adapter<TapItemAnexosAdapter.ViewHolder> {

    private static final int ITEM = 0;
    private static final int LOADING = 1;
    private ArrayList<Anexo> mAnexos;
    private Context mContext;
    private boolean finishPagination;
    private boolean isLoadingAdded = false;

    public TapItemAnexosAdapter(Context context, ArrayList<Anexo> anexos) {
        mAnexos = anexos;
        mContext = context;

        if (mAnexos.size() < Config.SIZE_PAGE) {
            finishPagination = true;
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View anexoView;

        switch (viewType) {
            case ITEM:
                anexoView = inflater.inflate(R.layout.item_anexo, parent, false);
                anexoView.setId(viewType);
                break;
            case LOADING:
                anexoView = inflater.inflate(R.layout.item_progress, parent, false);
                anexoView.setId(viewType);
                break;
            default:
                anexoView = null;
                break;
        }

        return new ViewHolder(anexoView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        switch (getItemViewType(position)) {

            case ITEM:
                Anexo anexo = mAnexos.get(position);
                holder.mNomeTextView.setText(mContext.getString(R.string.tap_anexo_label, anexo.getName()));
                break;
            case LOADING:

                break;
        }
    }

    @Override
    public int getItemViewType(int position) {
        return (position == mAnexos.size() - 1 && isLoadingAdded) ? LOADING : ITEM;
    }

    @Override
    public int getItemCount() {
        return mAnexos.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView mNomeTextView;

        public ViewHolder(View itemView) {
            super(itemView);

            switch (itemView.getId()) {
                case ITEM:
                    mNomeTextView = itemView.findViewById(R.id.anexo_nome_text_view);
                    break;
            }
        }
    }

    public void updateData(ArrayList<Anexo> listAnexo) {
        if (listAnexo.size() == 0 || listAnexo.size() < Config.SIZE_PAGE) {
            finishPagination = true;
        } else {
            finishPagination = false;
        }

        mAnexos.addAll(listAnexo);

        notifyDataSetChanged();
        LogUser.log(Config.TAG, "Pagination - listAnexos size: " + mAnexos.size() + " - page size: " + Config.SIZE_PAGE + " - finishPagination: " + finishPagination);
    }

    public void resetData() {
        mAnexos.clear();
        finishPagination = false;
    }

    public boolean isFinishPagination() {
        return finishPagination;
    }

    public void setFinishPagination(boolean finishPagination) {
        this.finishPagination = finishPagination;
    }

    public ArrayList<Anexo> getAnexos() {
        return mAnexos;
    }

    public void add(Anexo anexo) {
        mAnexos.add(anexo);
        notifyItemInserted(mAnexos.size() - 1);
    }

    public void addLoadingFooter() {
        isLoadingAdded = true;
        add(new Anexo());
    }

    public void removeLoadingFooter() {
        isLoadingAdded = false;
        int position = mAnexos.size() - 1;
        Anexo anexo = getItem(position);
        if (anexo != null) {
            mAnexos.remove(position);
            notifyItemRemoved(position);
        }
    }

    public Anexo getItem(int position) {
        return mAnexos.get(position);
    }
}
