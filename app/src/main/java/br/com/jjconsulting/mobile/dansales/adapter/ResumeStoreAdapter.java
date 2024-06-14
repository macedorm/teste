package br.com.jjconsulting.mobile.dansales.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.lang.reflect.Type;
import java.util.List;

import br.com.jjconsulting.mobile.dansales.R;
import br.com.jjconsulting.mobile.dansales.model.ResumeStore;
import br.com.jjconsulting.mobile.jjlib.util.FormatUtils;

public class ResumeStoreAdapter extends RecyclerView.Adapter<ResumeStoreAdapter.ViewHolder> {

    private List<ResumeStore.ResumePilar> mResumeStorePilares;

    private Context context;


    public ResumeStoreAdapter(Context context, List<ResumeStore.ResumePilar> resumoStorePilares) {
        this.mResumeStorePilares = resumoStorePilares;
        this.context = context;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_resume_pilar, parent, false);
        view.setId(viewType);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if(position == 0){
            holder.pilarTexView.setTypeface(Typeface.DEFAULT_BOLD);
            holder.averageDayTextView.setTypeface(Typeface.DEFAULT_BOLD);
            holder.averageXDayTextView.setTypeface(Typeface.DEFAULT_BOLD);
            holder.averagePromotorTextView.setTypeface(Typeface.DEFAULT_BOLD);
        } else {
            holder.pilarTexView.setTypeface(Typeface.DEFAULT);
            holder.averageDayTextView.setTypeface(Typeface.DEFAULT);
            holder.averageXDayTextView.setTypeface(Typeface.DEFAULT);
            holder.averagePromotorTextView.setTypeface(Typeface.DEFAULT);
        }

        holder.pilarTexView.setText(mResumeStorePilares.get(position).getPilar());
        holder.averageDayTextView.setText(FormatUtils.toDoubleFormat(mResumeStorePilares.get(position).getMedia_dia()) + "");
        holder.averageXDayTextView.setText(FormatUtils.toDoubleFormat(mResumeStorePilares.get(position).getMedia_x_dias()) + "");
        holder.averagePromotorTextView.setText(FormatUtils.toDoubleFormat(mResumeStorePilares.get(position).getMedia_promotor()) + "");
    }

    @Override
    public int getItemViewType(int position) {
        return mResumeStorePilares.size();
    }

    @Override
    public int getItemCount() {
        return mResumeStorePilares.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView pilarTexView;
        private TextView averageDayTextView;
        private TextView averageXDayTextView;
        private TextView averagePromotorTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            pilarTexView = itemView.findViewById(R.id.pilar_text_view);
            averageDayTextView = itemView.findViewById(R.id.average_day_text_view);
            averageXDayTextView = itemView.findViewById(R.id.average_x_day_text_view);
            averagePromotorTextView = itemView.findViewById(R.id.average_promotor_text_view);
        }
    }



    public void resetData() {
        mResumeStorePilares.clear();
    }

    public List<ResumeStore.ResumePilar> getResumeStore() {
        return mResumeStorePilares;
    }

    public ResumeStore.ResumePilar getItem(int position) {
        return mResumeStorePilares.get(position);
    }


}
