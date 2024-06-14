package br.com.jjconsulting.mobile.dansales.util;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import br.com.jjconsulting.mobile.dansales.R;
import br.com.jjconsulting.mobile.dansales.database.MultiValuesDao;
import br.com.jjconsulting.mobile.dansales.model.MultiValues;
import br.com.jjconsulting.mobile.dansales.model.TMultiValuesType;
import br.com.jjconsulting.mobile.jjlib.util.FormatUtils;
import br.com.jjconsulting.mobile.jjlib.util.LogUser;
import br.com.jjconsulting.mobile.jjlib.util.TextUtils;

public class DialogsMultiValue {

    public final int DIALOG_TYPE_ERROR = 0;
    public final int DIALOG_TYPE_WARNING = 1;
    public final int DIALOG_TYPE_SUCESS = 2;
    public final int DIALOG_TYPE_QUESTION = 3;

    private Activity context;


    private TimePickerDialog.OnTimeSetListener mHoursDateSetListener;
    private TimePickerDialog mTimeDatePickerDialog;

    private DatePickerDialog.OnDateSetListener mDateSetListener;
    private DatePickerDialog mDatePickerDialog;

    private TextView timeCheckoutTextView;
    private TextView dateCheckoutTextView;

    private int hours;
    private int minutes;

    private int year;
    private int month;
    private int day;

    private Date mCurrentDate;

    public void showDialogSpinner(ArrayList<MultiValues> multiValues, String message, int type, OnClickDialogMessage onClickDialogMessage) {
        try {
            showDialog(multiValues, message, type, onClickDialogMessage);
        } catch (Exception ex) {
            LogUser.log(Config.TAG, ex.toString());
        }
    }

    public void showDialogSpinner(TMultiValuesType tMultiValuesTypeString, String message, int type, OnClickDialogMessage onClickDialogMessage) {
        try {

            MultiValuesDao multiValuesDao = new MultiValuesDao(context);
            ArrayList<MultiValues> multiValues = multiValuesDao.getList(tMultiValuesTypeString);

            showDialog(multiValues, message, type, onClickDialogMessage);

        } catch (Exception ex) {
            LogUser.log(Config.TAG, ex.toString());
        }
    }

    public void showDialogDateSpinner(TMultiValuesType tMultiValuesTypeString, String date, int type, OnClickDialogTimeMessage onClickDialogMessage) {
        try {

            MultiValuesDao multiValuesDao = new MultiValuesDao(context);
            ArrayList<MultiValues> multiValues = multiValuesDao.getList(tMultiValuesTypeString);

            showDialogDate(multiValues, date, type, onClickDialogMessage);

        } catch (Exception ex) {
            LogUser.log(Config.TAG, ex.toString());
        }
    }

    private void  showDialog(ArrayList<MultiValues> multiValues, String message, int type, OnClickDialogMessage onClickDialogMessage) {

        String[] listItems = new String[multiValues.size()];

        for (int ind = 0; ind < multiValues.size(); ind++) {
            listItems[ind] = multiValues.get(ind).getDesc();
        }

        Dialog mMessageDialog = new Dialog(context, android.R.style.Theme_Translucent_NoTitleBar);
        mMessageDialog.setCancelable(true);
        mMessageDialog.setCanceledOnTouchOutside(true);

        mMessageDialog.setContentView(R.layout.dialog_multi_value_spinner);

        ListView spinner = mMessageDialog.findViewById(R.id.justificativa_list_view);

        TextView mMessageDialogTextView = mMessageDialog.findViewById(R.id.tv_dialog_message);
        mMessageDialogTextView.setText(message);

        setIconDialog(mMessageDialog, type, context);

        spinner.setAdapter(new ArrayAdapter(context, android.R.layout.simple_list_item_1, listItems));


            spinner.setOnItemClickListener((parent, view, positionValues, id) -> {
                mMessageDialog.dismiss();
                if (onClickDialogMessage != null) {
                    try {
                        if (!multiValues.isEmpty() && positionValues < multiValues.size()) {
                            onClickDialogMessage.onClick(multiValues.get(positionValues));
                        }
                    }catch(IndexOutOfBoundsException ex){
                        LogUser.log(ex.getMessage());
                    }
                }
            });

        ImageView cancelImageView = mMessageDialog.findViewById(R.id.close_image_view);
        cancelImageView.setOnClickListener((v) -> {
            mMessageDialog.dismiss();
        });

        mMessageDialog.show();
        context.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

    private void showDialogDate(ArrayList<MultiValues> multiValues, String currentDate, int type, OnClickDialogTimeMessage onClickDialogMessage) {


        String[] listItems = new String[multiValues.size()];

        String message = "";

        try {
            message = context.getString(R.string.title_dialog_visita_incompleta_data,
                    FormatUtils.toDefaultDateHoursBrazilianFormat(FormatUtils.toDate(currentDate)));
        } catch (Exception ex) {

        }

        for (int ind = 0; ind < multiValues.size(); ind++) {
            listItems[ind] = multiValues.get(ind).getDesc();
        }

        Dialog mMessageDialog = new Dialog(context, android.R.style.Theme_Translucent_NoTitleBar);
        mMessageDialog.setCancelable(true);
        mMessageDialog.setCanceledOnTouchOutside(true);

        mMessageDialog.setContentView(R.layout.dialog_datetime_multi_value_spinner);

        ListView spinner = mMessageDialog.findViewById(R.id.justificativa_list_view);

        TextView mMessageDialogTextView = mMessageDialog.findViewById(R.id.tv_dialog_message);
        mMessageDialogTextView.setText(message);

        timeCheckoutTextView = mMessageDialog.findViewById(R.id.time_checkout_text_view);
        LinearLayout timeLinearLayout = mMessageDialog.findViewById(R.id.time_linear_layout);

        dateCheckoutTextView = mMessageDialog.findViewById(R.id.date_checkout_text_view);
        LinearLayout dateLinearLayout = mMessageDialog.findViewById(R.id.date_linear_layout);

        setIconDialog(mMessageDialog, type, context);

        spinner.setAdapter(new ArrayAdapter(context, android.R.layout.simple_list_item_1, listItems));

        spinner.setOnItemClickListener((parent, view, positionDate, id) -> {
            try {

                if(mCurrentDate.after(getDateCheckout())){
                    Toast.makeText(context, context.getString(R.string.hours_checkout_justificativa_error), Toast.LENGTH_LONG).show();
                    return;
                }

                mMessageDialog.dismiss();
                if (onClickDialogMessage != null && positionDate < multiValues.size()) {
                    onClickDialogMessage.onClick(multiValues.get(positionDate),
                            FormatUtils.toTextToCompareDateInSQlite(getDateCheckout()));
                }
            } catch (Exception ex) {
                LogUser.log(ex.toString());
            }
        });

        ImageView cancelImageView = mMessageDialog.findViewById(R.id.close_image_view);
        cancelImageView.setOnClickListener((v) -> {
            mMessageDialog.dismiss();
        });


        timeLinearLayout.setOnClickListener((v) -> {
            mHoursDateSetListener = (TimePicker datePicker, int hours, int minutes) -> {
                this.hours = hours;
                this.minutes = minutes;
                setTime();
            };
            createStartTimePicker(context);
        });

        dateLinearLayout.setOnClickListener((v)-> {
           mDateSetListener = (view, year, month, dayOfMonth)-> {
               this.day = dayOfMonth;
               this.month = month;
               this.year = year;
               setDate();
           };
            createStartDataPicker(context, currentDate);
        });

        mMessageDialog.show();
        context.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        createDefaultDateTime(currentDate);

    }

    public DialogsMultiValue(Activity context) {
        this.context = context;
    }

    /**
     * @param dialog
     * @param type   0 - Error, 1 - Warning, 2 - Sucess
     */
    public void setIconDialog(Dialog dialog, int type, Context context) {

        ImageView icon;

        switch (type) {
            case 0:
                icon = dialog.findViewById(R.id.icon_image_view);
                icon.setColorFilter(Color.RED, android.graphics.PorterDuff.Mode.SRC_ATOP);
                icon.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_cancel_black_24dp));
                break;
            case 1:
                icon = dialog.findViewById(R.id.icon_image_view);
                icon.setColorFilter(context.getResources().getColor(R.color.alertCollor), android.graphics.PorterDuff.Mode.SRC_ATOP);
                icon.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_error_black_24dp));
                break;
            case 2:
                icon = dialog.findViewById(R.id.icon_image_view);
                icon.setColorFilter(context.getResources().getColor(R.color.sucessCollor), android.graphics.PorterDuff.Mode.SRC_ATOP);
                icon.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_check_circle_black_24dp));
                break;
            case 3:
                icon = dialog.findViewById(R.id.icon_image_view);
                icon.setColorFilter(context.getResources().getColor(R.color.questionCollor), android.graphics.PorterDuff.Mode.SRC_ATOP);
                icon.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_info_black_24dp));
                break;
        }

    }

    private void createStartTimePicker(Context context) {
        mTimeDatePickerDialog = new TimePickerDialog(
                context, android.R.style.Theme_Holo_Light_Dialog, mHoursDateSetListener, hours, minutes, true);
        mTimeDatePickerDialog.updateTime(hours, minutes);
        mTimeDatePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mTimeDatePickerDialog.show();
    }

    private void createStartDataPicker(Context context, String currentDate) {
        Date date = new Date();

        try{
            date = FormatUtils.toDate(currentDate);
        }catch (Exception ex){
            LogUser.log(ex.getMessage());
        }

        mDatePickerDialog = new DatePickerDialog(context, mDateSetListener, year, month, day);
        mDatePickerDialog.updateDate(year, month, day);
        mDatePickerDialog.getDatePicker().setMinDate(date.getTime());
        mDatePickerDialog.getDatePicker().setMaxDate(FormatUtils.addDate(date, 1,0,0).getTime());

        mDatePickerDialog.show();
    }

    public void createDefaultDateTime(String currentDate){

        try{
            mCurrentDate = FormatUtils.toDate(currentDate);
        }catch (Exception ex){
            LogUser.log(ex.getMessage());
        }

        final Calendar c = Calendar.getInstance();
        c.setTime(mCurrentDate);
        c.add(Calendar.HOUR_OF_DAY, +1);

        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);

        hours = c.get(Calendar.HOUR_OF_DAY);
        minutes = c.get(Calendar.MINUTE);

        setTime();
        setDate();
    }

    private void setTime(){
        String time = "Horário: " + (hours < 10 ? "0" : "") + hours + ":" + (minutes < 10 ? "0" : "") + minutes;
        timeCheckoutTextView.setText(time);
    }


    private void setDate(){
        int currentMonth = month + 1;
        String date = "Data: " + (day < 10 ? "0" : "") + day + "/" + (currentMonth < 10 ? "0" : "") + currentMonth + "/" + year;
        dateCheckoutTextView.setText(date);
    }

    private Date getDateCheckout(){
        int currentMonth = month + 1;
        String date = year + "-" + (currentMonth < 10 ? "0" : "") + currentMonth + "-" + (day < 10 ? "0" : "") + day ;
        String time = (hours < 10 ? "0" : "") + hours + ":" + minutes;

        try{
            Date checkoutDate = FormatUtils.toDate(date + " " + time);
            return checkoutDate;

        }catch (Exception ex){
            LogUser.log(ex.getMessage());
            return  null;
        }
    }

    public interface OnClickDialogMessage {
        void onClick(MultiValues multiValues);
    }

    public interface OnClickDialogTimeMessage {
        void onClick(MultiValues multiValues, String dateTime);
    }
}


