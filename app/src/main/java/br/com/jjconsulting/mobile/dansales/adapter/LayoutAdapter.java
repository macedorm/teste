package br.com.jjconsulting.mobile.dansales.adapter;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import br.com.jjconsulting.mobile.dansales.R;
import br.com.jjconsulting.mobile.dansales.model.Cliente;
import br.com.jjconsulting.mobile.dansales.model.Layout;
import br.com.jjconsulting.mobile.dansales.util.ClienteUtils;
import br.com.jjconsulting.mobile.dansales.util.Config;
import br.com.jjconsulting.mobile.dansales.util.PlanoCampoUtils;
import br.com.jjconsulting.mobile.jjlib.util.LogUser;

public class LayoutAdapter extends RecyclerView.Adapter<LayoutAdapter.ViewHolder> {

    private static final int ITEM = 0;
    private static final int LOADING = 1;
    private List<Layout> mLayouts;
    private boolean finishPagination;
    private boolean isLoadingAdded = false;
    private Context mContext;

    public LayoutAdapter(List<Layout> layout, Context context) {
        mLayouts = layout;

        if (mLayouts.size() < Config.SIZE_PAGE) {
            finishPagination = true;
        }

        mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View layoutView;

        switch (viewType) {
            case ITEM:
                layoutView = inflater.inflate(R.layout.item_layout, parent, false);
                layoutView.setId(viewType);
                break;
            case LOADING:
                layoutView = inflater.inflate(R.layout.item_progress, parent, false);
                layoutView.setId(viewType);
                break;
            default:
                layoutView = null;
                break;
        }

        return new ViewHolder(layoutView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        switch (getItemViewType(position)) {

            case ITEM:
                Layout layout = mLayouts.get(position);

                holder.mNomeTextView.setText(layout.getNome());
                holder.mCodigoTextView.setText(layout.getCodigo());

                break;
            case LOADING:

                break;
        }
    }

    @Override
    public int getItemViewType(int position) {
        return (position == mLayouts.size() - 1 && isLoadingAdded) ? LOADING : ITEM;
    }

    @Override
    public int getItemCount() {
        return mLayouts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView mNomeTextView;
        private TextView mCodigoTextView;

        public ViewHolder(View itemView) {
            super(itemView);

            switch (itemView.getId()) {
                case ITEM:
                    mNomeTextView = itemView.findViewById(R.id.layout_nome_text_view);
                    mCodigoTextView = itemView.findViewById(R.id.layout_codigo_text_view);
                    break;
            }
        }
    }

    public void updateData(List<Layout> listLayouts) {
        if (listLayouts.size() == 0 || listLayouts.size() < Config.SIZE_PAGE) {
            finishPagination = true;
        } else {
            finishPagination = false;

        }
        mLayouts.addAll(listLayouts);

        notifyDataSetChanged();
        LogUser.log(Config.TAG, "Pagination - listLayout size: " + listLayouts.size() + " - page size: " + Config.SIZE_PAGE + " - finishPagination: " + finishPagination);
    }

    public void resetData() {
        mLayouts.clear();
        finishPagination = false;
    }

    public boolean isFinishPagination() {
        return finishPagination;
    }

    public void setFinishPagination(boolean finishPagination) {
        this.finishPagination = finishPagination;
    }

    public List<Layout> getLayouts() {
        return mLayouts;
    }

    public void add(Layout layout) {
        mLayouts.add(layout);
        notifyItemInserted(mLayouts.size() - 1);
    }

    public void addLoadingFooter() {
        isLoadingAdded = true;
        add(new Layout());
    }

    public void removeLoadingFooter() {
        isLoadingAdded = false;
        int position = mLayouts.size() - 1;
        Layout item = getItem(position);
        if (item != null) {
            mLayouts.remove(position);
            notifyItemRemoved(position);
        }
    }

    public Layout getItem(int position) {
        return mLayouts.get(position);
    }


}
