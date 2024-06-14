package br.com.jjconsulting.mobile.dansales.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import br.com.jjconsulting.mobile.dansales.R;
import br.com.jjconsulting.mobile.dansales.model.PlanejamentoRotaAtividadeType;
import br.com.jjconsulting.mobile.dansales.util.PlanejamentoRotaUtils;
import br.com.jjconsulting.mobile.dansales.util.RotaGuiadaUtils;
import br.com.jjconsulting.mobile.jjlib.util.TextUtils;
import br.com.jjconsulting.mobile.jjlib.view.calendarEventView.JJCalendarEvent;

public class PlanejamentoRotaAdapter extends RecyclerView.Adapter<PlanejamentoRotaAdapter.ViewHolder> {

    private static final int ITEM = 0;
    private static final int LOADING = 1;

    private OnClickAddEvent onClickAddEvent;

    private List<JJCalendarEvent> mCalendarEvent;

    private Context context;

    private boolean isLoadingAdded = false;

    public PlanejamentoRotaAdapter(Context context, List<JJCalendarEvent> calendarEvent, OnClickAddEvent onClickAddEvent) {
        this.onClickAddEvent = onClickAddEvent;
        this.mCalendarEvent = calendarEvent;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view;

        view = inflater.inflate(R.layout.item_planejamento_rota_guiada, parent, false);
        view.setId(viewType);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        JJCalendarEvent calendarEvent = mCalendarEvent.get(position);

        if(calendarEvent.isAdd()){
            holder.mAddEventButton.setVisibility(View.VISIBLE);
        } else {
            holder.mAddEventButton.setVisibility(View.GONE);
        }

        holder.mAddEventButton.setTag(position);
        holder.mAddEventButton.setOnClickListener((view)-> {
            if(onClickAddEvent != null)
                onClickAddEvent.onClickAdd(mCalendarEvent.get(Integer.parseInt(view.getTag().toString())));
        });

        holder.mUserTextView.setVisibility(View.GONE);

        String note = "";

        holder.mDeleteImageView.setVisibility(View.VISIBLE);
        holder.mDeleteImageView.setTag(position);
        holder.mDeleteImageView.setOnClickListener((view)-> {
            onClickAddEvent.onClickRemove(mCalendarEvent.get(Integer.parseInt(view.getTag().toString())));
        });

        switch (PlanejamentoRotaUtils.fromInteger(calendarEvent.getType())){
            default:
                holder.mNomeTextView.setText(calendarEvent.getEvent().toUpperCase());
                holder.mEndereco1TextView.setText(context.getString(R.string.planejamneto_rota_start_hours) + " " + calendarEvent.getHoursStart());
                holder.mEndereco2TextView.setText(context.getString(R.string.planejamneto_rota_finish_hours) + " " + calendarEvent.getHoursEnd());
                note = calendarEvent.getNote();
                break;
            case VISTA:
            case VISITAPROMOTOR:
            case COACHINGPORMOTOR:
                if(calendarEvent.getType() == PlanejamentoRotaUtils.COACHINGPORMOTOR.getValue()){
                    holder.mDeleteImageView.setVisibility(View.GONE);
                    holder.mNomeTextView.setText(calendarEvent.getEvent().toUpperCase());
                    note = calendarEvent.getPromotorName();

                    holder.mEndereco1TextView.setVisibility(View.GONE);
                    holder.mEndereco2TextView.setVisibility(View.GONE);

                } else {
                    if(!TextUtils.isNullOrEmpty(calendarEvent.getCodCliente())){
                        holder.mObsTextView.setVisibility(View.GONE);
                        holder.mEndereco1TextView.setVisibility(View.VISIBLE);
                        holder.mEndereco2TextView.setVisibility(View.VISIBLE);

                        holder.mNomeTextView.setText(calendarEvent.getName());
                        holder.mEndereco1TextView.setText(calendarEvent.getAdresss());
                        holder.mEndereco2TextView.setText(String.format("%1$s, %2$s", calendarEvent.getCity(),
                                calendarEvent.getUf()));
                    }

                    if(!TextUtils.isNullOrEmpty(calendarEvent.getPromotor())){
                        note =  calendarEvent.getPromotorName();
                    }

                    if(!TextUtils.isNullOrEmpty(calendarEvent.getNote())){
                        if(!TextUtils.isNullOrEmpty(note)){
                            note += "\n";
                        }

                        note += calendarEvent.getNote();
                    }
                }

                break;

        }

        if(TextUtils.isNullOrEmpty(note)){
            holder.mObsTextView.setVisibility(View.GONE);
        } else {
            holder.mObsTextView.setVisibility(View.VISIBLE);
            holder.mObsTextView.setText(note);
        }

        holder.mStatusImageView.setImageResource(
                RotaGuiadaUtils.getStatusRGImageResourceId(calendarEvent.isRoute(), calendarEvent.getStatus(), false));

        holder.mForaRotaLinearLayout.setVisibility(View.GONE);
    }

    @Override
    public int getItemViewType(int position) {
        return (position == mCalendarEvent.size() - 1 && isLoadingAdded) ? LOADING : ITEM;
    }

    @Override
    public int getItemCount() {
        return mCalendarEvent.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView mStatusImageView;
        private ImageView mDeleteImageView;
        private TextView mUserTextView;
        private TextView mNomeTextView;
        private TextView mEndereco1TextView;
        private TextView mEndereco2TextView;
        private TextView mObsTextView;
        private Button mAddEventButton;
        private LinearLayout mForaRotaLinearLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            mUserTextView = itemView.findViewById(R.id.rg_user_text_view);
            mDeleteImageView = itemView.findViewById(R.id.delete_image_view);
            mStatusImageView = itemView.findViewById(R.id.rg_status_image_view);
            mNomeTextView = itemView.findViewById(R.id.rg_nome_text_view);
            mEndereco1TextView = itemView.findViewById(R.id.rg_endereco1_text_view);
            mEndereco2TextView = itemView.findViewById(R.id.rg_endereco2_text_view);
            mForaRotaLinearLayout = itemView.findViewById(R.id.fora_rota_linear_layout);
            mObsTextView = itemView.findViewById(R.id.rg_obs_text_view);
            mAddEventButton = itemView.findViewById(R.id.add_event_button);
        }
    }

    public void resetData() {
        mCalendarEvent.clear();
    }

    public List<JJCalendarEvent> getRotas() {
        return mCalendarEvent;
    }
    public JJCalendarEvent getItem(int position) {
        return mCalendarEvent.get(position);
    }

    public boolean isEnableCoaching(int index){
        boolean isFinish = true;

        JJCalendarEvent event = mCalendarEvent.get(index);

        for(JJCalendarEvent calendarEvent: mCalendarEvent){
            if(calendarEvent.getType() == PlanejamentoRotaUtils.VISITAPROMOTOR .getValue() &&
                    calendarEvent.getStatus() != RotaGuiadaUtils.STATUS_RG_FINALIZADO){

                if(event.getPromotor().equals(calendarEvent.getPromotor())){
                    isFinish = false;
                }
            }
        }

        return isFinish;
    }

    public interface OnClickAddEvent{
        void onClickAdd(JJCalendarEvent calendarEvent);
        void onClickRemove(JJCalendarEvent calendarEvent);

    }
}
