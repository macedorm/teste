package br.com.jjconsulting.mobile.jjlib.view.calendarEventView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import br.com.jjconsulting.mobile.jjlib.R;

public class JJCalendarEventRecyclerAdapter extends RecyclerView.Adapter<JJCalendarEventRecyclerAdapter.MyViewHolder>  {

    private Context context;
    private ArrayList<JJCalendarEvent> calendarEventArrayList;

    public JJCalendarEventRecyclerAdapter(Context context, ArrayList<JJCalendarEvent> calendarEventArrayList) {
        this.context = context;
        this.calendarEventArrayList = calendarEventArrayList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ViewGroup view = (ViewGroup) LayoutInflater.from(parent.getContext()).inflate(R.layout.jj_calendar_events_rows_layout,parent,false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        JJCalendarEvent events = calendarEventArrayList.get(position);
        holder.calendarEvent.setText(events.getEvent());
        holder.date.setText(events.getDate());

    }

    @Override
    public int getItemCount() {
        return calendarEventArrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        private TextView calendarEvent;
        private TextView date;
        private TextView time;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            calendarEvent = itemView.findViewById(R.id.eventname);
            date = itemView.findViewById(R.id.current_Date);
            time   = itemView.findViewById(R.id.eventtime);
        }
    }
}
