package br.com.jjconsulting.mobile.jjlib.view.calendarEventView;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import br.com.jjconsulting.mobile.jjlib.R;
import br.com.jjconsulting.mobile.jjlib.util.LogUser;

public class JJCalendarEventGridAdapter extends ArrayAdapter {


    private List<Date> dates ;
    private List<JJCalendarEvent> calendarEventList;
    private Object organaizeCalendarEventList[];
    private int heightLinesSize[];

    private boolean expandable;
    private boolean isChangeColorDate;

    private Calendar currentDate;
    private Calendar calendarDateNow;

    private LayoutInflater inflater;

    private int lineVisible;

    private OnLongClickListener onLongClickListener;
    private OnItemClickListener onItemClickListener;


    public JJCalendarEventGridAdapter(Context context, List<Date> dates, Calendar currentDate, List<JJCalendarEvent> eventsList, boolean expandable) {
        super(context, R.layout.jj_calendar_events_rows_layout);
        inflater = LayoutInflater.from(context);
        this.dates = dates;
        this.currentDate = currentDate;
        this.calendarEventList = eventsList;
        this.expandable = expandable;

        calendarDateNow = Calendar.getInstance();
        Date dateNow = new Date();
        calendarDateNow.setTime(dateNow);

        organaizeCalendarEventList = new Object[dates.size() + 1];
        heightLinesSize = new int[dates.size() / 7];

        int count = 0;

        for(Date date: dates){
            Calendar dateCalendar = Calendar.getInstance();
            dateCalendar.setTime(date);

            int dayNo = dateCalendar.get(Calendar.DAY_OF_MONTH);
            int displayMonth = dateCalendar.get(Calendar.MONTH)+1;
            int displayYear = dateCalendar.get(Calendar.YEAR);

            if(expandable && dayNo == calendarDateNow.get(Calendar.DAY_OF_MONTH)){
                lineVisible = getIndexHeight(count);
            }

            Calendar eventCalendar = Calendar.getInstance();
            List<JJCalendarEvent> eventList = new ArrayList<>();

            for (int i = 0; i < calendarEventList.size(); i++){
                eventCalendar.setTime(convertStringToDate(calendarEventList.get(i).getDate()));
                if(dayNo == eventCalendar.get(Calendar.DAY_OF_MONTH) && displayMonth == eventCalendar.get(Calendar.MONTH)+1
                        && displayYear == eventCalendar.get(Calendar.YEAR)){
                    eventList.add(calendarEventList.get(i));
                }

                if(heightLinesSize[getIndexHeight(count)] < eventList.size()){
                    heightLinesSize[getIndexHeight(count)] = eventList.size();
                }
            }

            organaizeCalendarEventList[count] = eventList;
            count++;

        }

    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View view = inflater.inflate(R.layout.jj_calendar_events_rows_layout,parent,false);

        Date monthDate = dates.get(position);
        Calendar dateCalendar = Calendar.getInstance();
        dateCalendar.setTime(monthDate);

        int dayNo = dateCalendar.get(Calendar.DAY_OF_MONTH);
        int displayMonth = dateCalendar.get(Calendar.MONTH)+1;
        int displayYear = dateCalendar.get(Calendar.YEAR);
        int currentYear = currentDate.get(Calendar.YEAR);
        int currentMonth = currentDate.get(Calendar.MONTH)+1;

        boolean isDateNow = false;
        boolean isDateSelected = false;


        if(dayNo == calendarDateNow.get(Calendar.DAY_OF_MONTH) &&
                displayMonth == calendarDateNow.get(Calendar.MONTH)+1 &&
                displayYear == calendarDateNow.get(Calendar.YEAR)){
            isDateNow = true;
        }

        if(isChangeColorDate && dayNo == currentDate.get(Calendar.DAY_OF_MONTH) &&
                displayMonth == currentDate.get(Calendar.MONTH)+1 &&
                displayYear == currentDate.get(Calendar.YEAR)){
            isDateSelected = true;
        }


        view.setBackgroundColor(getContext().getResources().getColor(R.color.colorWhite));
        TextView cellNumber = view.findViewById(R.id.calendar_day);

        if (displayMonth == currentMonth && displayYear==currentYear){
            cellNumber.setTextColor(Color.parseColor("#000000"));
        } else {
            cellNumber.setTextColor(Color.parseColor("#a8a8a8"));
        }

        cellNumber.setText(String.valueOf(dayNo));

        if(isDateNow){
            cellNumber.setTextColor(Color.parseColor("#FFFFFF"));
            cellNumber.setPadding(10,4,10,4);
            cellNumber.setBackground(getContext().getResources().getDrawable(R.drawable.jj_border_event));
        }

        if(isDateSelected){
            cellNumber.setTextColor(Color.parseColor("#FFFFFF"));
            cellNumber.setPadding(10,4,10,4);
            cellNumber.setBackground(getContext().getResources().getDrawable(R.drawable.jj_border_selected_event));
        }

        LinearLayout eventsLinearLayout = view.findViewById(R.id.events_linear_layout);

        int size = (int) getContext().getResources().getDimension(R.dimen.event_row_size);

        if(calendarEventList.size() > 0){

            if(heightLinesSize[getIndexHeight(position)] > 0){
                size = (size * heightLinesSize[getIndexHeight(position)]) + (size);
            } else {
                size = size * 2;
            }

            for(JJCalendarEvent jjCalendarEvent: (List<JJCalendarEvent>) organaizeCalendarEventList[position]){
                eventsLinearLayout.addView(addEvents(jjCalendarEvent, position));
            }
        } else {
            size = size + (size /2);
        }


        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,  size);
        view.setLayoutParams(layoutParams);
        return view;
    }


    @Override
    public int getCount() {
        return dates.size();
    }

    @Nullable
    @Override
    public Object getItem(int position) {
        return dates.get(position);
    }

    @Override
    public int getPosition(@Nullable Object item) {
        return dates.indexOf(item);
    }

    private Date convertStringToDate(String dateInString){
        java.text.SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        Date date = null;
        try {
            date = format.parse(dateInString);

        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    private View addEvents(JJCalendarEvent jjCalendarEvent, int position){
       View view = inflater.inflate(R.layout.jj_events_rows_layout, null,false);

       CardView cardView = view.findViewById(R.id.card_view);
       cardView.setCardBackgroundColor(Color.RED);

        cardView.setTag(R.id.position, jjCalendarEvent.getId());

        if(onLongClickListener != null) {
            cardView.setOnLongClickListener((v) -> {
                for (JJCalendarEvent event : calendarEventList) {
                    if (event.getId().equals(v.getTag(R.id.position).toString())) {
                        onLongClickListener.onLongClick(event);
                        break;
                    }
                }
                return false;
            });
        }

        cardView.setTag(R.id.id, position);

        if(onItemClickListener != null){
            cardView.setOnClickListener((v)->{
                int index = Integer.parseInt(v.getTag(R.id.id).toString());
                onItemClickListener.onClick(index, dates.get(index));
            });
        }


        cardView.setCardBackgroundColor(getColor(jjCalendarEvent.getStatus()));

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
               (int) getContext().getResources().getDimension(R.dimen.event_row_size));
        view.setLayoutParams(params);
        TextView titleNameTextView = view.findViewById(R.id.events_name_text_view);

        titleNameTextView.setText(jjCalendarEvent.getEvent());

        return view;
    }

    private int getColor(int status){
        switch (status){
            case 0:
                return Color.parseColor("#0C5FFF");
            case 1:
            case 5:
                return Color.parseColor("#E2C344");
            case 2:
            case 3:
                return Color.parseColor("#42A144");
            case 6:
                return  Color.parseColor("#ff960c");
            default:
                return Color.parseColor("#E31221");
        }
    }

    public static int getIndexHeight(int position){
        int indexHeight = 0;
        if(position <= 6){
            indexHeight = 0;
        } else if(position <= 13){
            indexHeight = 1;
        } else  if(position <= 20){
            indexHeight = 2;
        } else  if(position <= 27){
            indexHeight = 3;
        } else  if(position <= 34){
            indexHeight = 4;
        } else  if(position <= 41){
            indexHeight = 5;
        }

        return indexHeight;
    }

    public boolean isChangeColorDate() {
        return isChangeColorDate;
    }

    public void setChangeColorDate(boolean changeColorDate) {
        isChangeColorDate = changeColorDate;
    }

    public OnLongClickListener getOnLongClickListener() {
        return onLongClickListener;
    }

    public void setOnLongClickListener(OnLongClickListener onLongClickListener) {
        this.onLongClickListener = onLongClickListener;
    }

    public OnItemClickListener getOnItemClickListener() {
        return onItemClickListener;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnLongClickListener{
       void onLongClick(JJCalendarEvent calendarEvent);
    }

    public interface OnItemClickListener{
        void onClick(int index, Date date);
    }
}
