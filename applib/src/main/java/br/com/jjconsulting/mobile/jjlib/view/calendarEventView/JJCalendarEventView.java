package br.com.jjconsulting.mobile.jjlib.view.calendarEventView;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import br.com.jjconsulting.mobile.jjlib.R;
import br.com.jjconsulting.mobile.jjlib.util.TextUtils;

public class JJCalendarEventView extends LinearLayout implements View.OnClickListener {

    private static final int MAX_Days = 42;

    private Context context;

    private TextView currentDateTextView;
    private GridView calendarGridView;
    private JJCalendarEventGridAdapter calendarEventGridAdapter;

    private Calendar calendar;
    private Calendar initialCalendar;

    private List<JJCalendarEvent> eventsList = new ArrayList<>();
    private List<Date> dateList = new ArrayList<>();

    private ItemClickListener onItemClickListener;
    private JJCalendarEventGridAdapter.OnLongClickListener onLongClickListener;


    private ImageView expandableImageView;

    private boolean isEnableExpandable;
    private boolean expandable;
    private boolean isChangeColorDate;

    public JJCalendarEventView(Context context, List<JJCalendarEvent> eventsList, Date date) {
        super(context);

        this.context = context;
        this.eventsList = eventsList;

        Calendar currentCalendar = Calendar.getInstance();
        currentCalendar.setTime(date);

        initialCalendar = currentCalendar;
        calendar = currentCalendar;
    }

    public JJCalendarEventView(Context context, Date date) {
        super(context);

        this.context = context;
        eventsList = new ArrayList<>();

        Calendar currentCalendar = Calendar.getInstance();
        currentCalendar.setTime(date);

        initialCalendar = currentCalendar;
        calendar = currentCalendar;
    }


    public void startCalendar(){
        intializeLayout();

        calendarGridView.setOnItemClickListener((parent, view, position, id)-> {
            if(onItemClickListener == null)
                return;

            Calendar calendarTemp = (Calendar) calendar.clone();
            calendarTemp.setTime(dateList.get(position));

            if(calendarTemp.get(Calendar.MONTH) != calendar.get(Calendar.MONTH))
                return;

            onItemClickListener.onClick(position, dateList.get(position));
            calendar = (Calendar) calendarTemp.clone();

            if(isChangeColorDate){
                reloadCalendar();
            }
        });

        setMonth(eventsList, 0);
    }

    public void setMonth(List<JJCalendarEvent> eventsList, int month){
        this.eventsList = eventsList;
        calendar = (Calendar) initialCalendar.clone();
        calendar.add(Calendar.MONTH, month);
        setupCalendar();
    }

    public void setMonth(int month){
        calendar = (Calendar) initialCalendar.clone();
        calendar.add(Calendar.MONTH, month);
    }


    private void intializeLayout(){
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.jj_calendar_events_layout,this);
        currentDateTextView = view.findViewById(R.id.current_Date);
        calendarGridView = view.findViewById(R.id.gridview);
        expandableImageView = view.findViewById(R.id.expanded_calendar_image_view);
        expandableImageView.setRotation(180);

        if(isEnableExpandable){
            expandableImageView.setOnClickListener(this);
            expandableImageView.setVisibility(View.VISIBLE);
        } else{
            LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
            calendarGridView.setLayoutParams(layoutParams);

            expandableImageView.setVisibility(View.GONE);
        }


    }

    public void reloadCalendar(){
        setupCalendar();
    }

    private void setupCalendar() {
        dateList.clear();
        currentDateTextView.setText(TextUtils.firstLetterUpperCase(getMonthLabel(calendar)));

        Calendar monthCalendar = (Calendar) calendar.clone();
        monthCalendar.set(Calendar.DAY_OF_MONTH, 1);
        int FirstDayOfMonth = monthCalendar.get(Calendar.DAY_OF_WEEK) - 1;
        monthCalendar.add(Calendar.DAY_OF_MONTH, -FirstDayOfMonth);


        int count = 0;
        List<Date> dateListTemp = new ArrayList<>();

        int positionSelected = 0;


        while (dateList.size() < MAX_Days) {
            dateList.add(monthCalendar.getTime());
            monthCalendar.add(Calendar.DAY_OF_MONTH, 1);

            if (dateList.get(count).equals(calendar.getTime())) {
                positionSelected = count;
            }

            count++;
        }

        if (expandable) {
            for (int ind = 0; ind < dateList.size(); ind++) {
                if (JJCalendarEventGridAdapter.getIndexHeight(ind) == JJCalendarEventGridAdapter.getIndexHeight(positionSelected)) {
                    dateListTemp.add(dateList.get(ind));
                }
            }
        }

        if (dateListTemp.size() > 0) {
            dateList = dateListTemp;
        }

        calendarEventGridAdapter = new JJCalendarEventGridAdapter(context, dateList, calendar, eventsList, expandable);
        calendarEventGridAdapter.setOnLongClickListener(onLongClickListener);
        calendarEventGridAdapter.setOnItemClickListener(new JJCalendarEventGridAdapter.OnItemClickListener() {
            @Override
            public void onClick(int index, Date date) {
                if(onItemClickListener != null){
                    onItemClickListener.onClick(index, date);
                }
            }
        });

        calendarEventGridAdapter.setChangeColorDate(isChangeColorDate);
        calendarGridView.setAdapter(calendarEventGridAdapter);

    }

    public GridView getCalendarGridView() {
        return calendarGridView;
    }

    public void setCalendarGridView(GridView calendarGridView) {
        this.calendarGridView = calendarGridView;
    }

    public ItemClickListener onItemClickListener() {
        return onItemClickListener;
    }

    public void setOnItemClickListener(ItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public Calendar getCalendar() {
        return calendar;
    }

    public void setCalendar(Calendar calendar) {
        this.calendar = calendar;
    }

    public List<JJCalendarEvent> getEventsList() {
        return eventsList;
    }

    public void setEventsList(List<JJCalendarEvent> eventsList) {
        this.eventsList = eventsList;
    }

    public String getMonthLabel(int addMoth){
        Calendar calendar = (Calendar) initialCalendar.clone();
        calendar.add(Calendar.MONTH, addMoth);
        SimpleDateFormat simpleDateFormatPT = new SimpleDateFormat("MMMM yyyy", new Locale("pt", "BR"));
        String currentDate = simpleDateFormatPT.format(calendar.getTime());

        return currentDate;
    }

    public String getMonthLabel(Calendar calendar){
        SimpleDateFormat simpleDateFormatPT = new SimpleDateFormat("MMMM yyyy", new Locale("pt", "BR"));
        String currentDate = simpleDateFormatPT.format(calendar.getTime());

        return currentDate;
    }

    public boolean isEnableExpandable() {
        return isEnableExpandable;
    }

    public void setEnableExpandable(boolean enableExpandable) {
        isEnableExpandable = enableExpandable;
    }

    public boolean isExpandable() {
        return expandable;
    }

    public void setExpandable(boolean expandable) {
        this.expandable = expandable;
    }

    public boolean isChangeColorDate() {
        return isChangeColorDate;
    }

    public void setChangeColorDate(boolean changeColorDate) {
        isChangeColorDate = changeColorDate;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.expanded_calendar_image_view) {
            expandable = !expandable;
            expandableImageView.setRotation(expandable ? 0:180);
            setupCalendar();
        }
    }

    public JJCalendarEventGridAdapter.OnLongClickListener getOnLongClickListener() {
        return onLongClickListener;
    }

    public void setOnLongClickListener(JJCalendarEventGridAdapter.OnLongClickListener onLongClickListener) {
        this.onLongClickListener = onLongClickListener;
    }

    public interface ItemClickListener{
        void onClick(int index, Date date);
    }

}
