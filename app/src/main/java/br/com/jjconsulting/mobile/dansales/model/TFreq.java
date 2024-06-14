package br.com.jjconsulting.mobile.dansales.model;

import android.content.Context;
import android.os.Bundle;

import com.google.firebase.analytics.FirebaseAnalytics;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

import br.com.jjconsulting.mobile.dansales.R;
import br.com.jjconsulting.mobile.dansales.util.FirebaseUtils;
import br.com.jjconsulting.mobile.jjlib.util.FormatUtils;
import br.com.jjconsulting.mobile.jjlib.util.LogUser;

public enum TFreq implements Serializable {
    DAILY(1),
    WEEKLY(2),
    BIWEEKLY(3),
    MONTHLY(4),
    BIMONTHLY(5),
    QUARTERLY(6),
    SEMIANNUAL(7),
    YEARLY(8);

    private int intValue;

    TFreq(int value) {
        intValue = value;
    }

    public int getValue() {
        return intValue;
    }

    public static TFreq fromInteger(int x) {
        switch(x) {
            case 1:
                return DAILY;
            case 2:
                return WEEKLY;
            case 3:
                return BIWEEKLY;
            case 4:
                return MONTHLY;
            case 5:
                return BIMONTHLY;
            case 6:
                return QUARTERLY;
            case 7:
                return SEMIANNUAL;
            case 8:
                return YEARLY;
            default:
                return DAILY;
        }
    }

    public static String getName(Context context, TFreq tFreq) {
        switch(tFreq) {
            case DAILY:
                return  context.getString(R.string.daily);
            case WEEKLY:
                return  context.getString(R.string.weekly);
            case BIWEEKLY:
                return  context.getString(R.string.biweekly);
            case BIMONTHLY:
                return  context.getString(R.string.monthly);
            case MONTHLY:
                return  context.getString(R.string.bimonthly);
            case QUARTERLY:
                return  context.getString(R.string.quarterly);
            case SEMIANNUAL:
                return  context.getString(R.string.semiannual);
            case YEARLY:
                return  context.getString(R.string.yearly);
            default:
                return  context.getString(R.string.nofreq);
        }
    }

    public static String fromInteger(Context context, int x) {
        switch(x) {
            case 0:
                return  context.getString(R.string.nofreq);
            case 1:
                return  context.getString(R.string.daily);
            case 2:
                return  context.getString(R.string.weekly);
            case 3:
                return  context.getString(R.string.biweekly);
            case 4:
                return  context.getString(R.string.monthly);
            case 5:
                return  context.getString(R.string.bimonthly);
            case 6:
                return  context.getString(R.string.quarterly);
            case 7:
                return  context.getString(R.string.semiannual);
            case 8:
                return  context.getString(R.string.yearly);
            default:
                return  context.getString(R.string.nofreq);

        }
    }

/*
    public static String getFreq(TFreq tFreq){
        String dateString = null;
        String freq = "";
        try {
            Date date = new Date();
            dateString = FormatUtils.toDefaultDateBrazilianFormat(date);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);

            int day = calendar.get(Calendar.DAY_OF_MONTH);
            int weekOfYear = calendar.get(Calendar.WEEK_OF_MONTH);
            int month = calendar.get(Calendar.MONTH);

            month++;

            switch (tFreq) {
                case DAILY:
                    freq = "DI_";
                    dateString = FormatUtils.toConvertDate(dateString, "dd/MM/yyyy", "yyyyMMdd");
                    freq += dateString;
                        break;
                case WEEKLY:
                    freq = "SM_";
                    dateString = FormatUtils.toConvertDate(dateString, "dd/MM/yyyy", "yyyyMM");
                    freq += dateString;
                    freq+= "_0" + weekOfYear;
                    break;
                case BIWEEKLY:
                    freq = "QU_";
                    dateString = FormatUtils.toConvertDate(dateString, "dd/MM/yyyy", "yyyyMM");
                    freq += dateString;

                    if(day<= 15){
                        freq+=  "_01";
                    } else {
                        freq+=  "_02";
                    }

                    break;

                case MONTHLY:
                    freq = "ME_";
                    dateString = FormatUtils.toConvertDate(dateString, "dd/MM/yyyy", "yyyyMM");
                    freq += dateString;

                    break;
                case BIMONTHLY:
                    freq = "BI_";
                    dateString = FormatUtils.toConvertDate(dateString, "dd/MM/yyyy", "yyyy");
                    freq += dateString;

                    if(month % 2 != 0){
                        month++;
                    }
                    int bimonthly = month / 2;
                    freq += "_0" + bimonthly;

                    break;
                case QUARTERLY:
                    freq = "TR_";
                    dateString = FormatUtils.toConvertDate(dateString, "dd/MM/yyyy", "yyyy");
                    freq += dateString;

                    if(month % 2 != 0){
                        month++;
                    } else {
                        month += 2;
                    }

                    int quarterly = month / 3;
                    freq += "_0"+ quarterly;
                    break;
                case SEMIANNUAL:
                    freq = "SE_";
                    dateString = FormatUtils.toConvertDate(dateString, "dd/MM/yyyy", "yyyy");
                    freq += dateString;


                    int semiannual;

                    if(month <= 6){
                        semiannual = 1;
                    } else {
                        semiannual = 2;
                    }
                    freq += "_0" + semiannual;
                    break;

                case YEARLY:
                    freq = "AN_";
                    dateString = FormatUtils.toConvertDate(dateString, "dd/MM/yyyy", "yyyy");
                    freq += dateString;
                    break;

            }

        }catch (Exception ex){

        }

        return freq;
    }*/


    public static String getFreq(TFreq tFreq, Date currentDate){
        String dateString;
        String freq = "";
        try {
            dateString = FormatUtils.toDefaultDateBrazilianFormat(currentDate);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(currentDate);
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            int weekOfYear = calendar.get(Calendar.WEEK_OF_MONTH);
            int month = calendar.get(Calendar.MONTH);
            int year = calendar.get(Calendar.YEAR);

            month++;

            switch (tFreq) {
                case DAILY:
                    freq = "DI_";
                    dateString = FormatUtils.toConvertDate(dateString, "dd/MM/yyyy", "yyyyMMdd");
                    freq += dateString;
                    break;
                case WEEKLY:
                    freq = "SM_";
                    dateString = FormatUtils.toConvertDate(dateString, "dd/MM/yyyy", "yyyyMM");

                    //Caso seja a primeira semana do mês verifica se o dia 01 começou no domingo, caso não
                    //vai considerar como frequencia semanal a ultima semana do mês anterior
                    LogUser.log("tesfre - frequencia original: " + dateString);
                    LogUser.log("tesfre - semana: " + weekOfYear);

                    if(weekOfYear == 1){
                       try{
                           Date dateStart = FormatUtils.toDate(year + "-" + (month > 9 ? month: "0" + month) + "-" + "01 00:00:00");
                           Calendar calendarStart = Calendar.getInstance();
                           calendarStart.setTime(dateStart);

                           LogUser.log("tesfre - dia da semana: " + calendarStart.get(Calendar.DAY_OF_WEEK));

                           if(Calendar.SUNDAY != calendarStart.get(Calendar.DAY_OF_WEEK)){
                               Date dateBefore = FormatUtils.addDate(dateStart, -1, 0, 0);
                               Calendar calendarBefore = Calendar.getInstance();
                               calendarBefore.setTime(dateBefore);
                               weekOfYear = calendarBefore.get(Calendar.WEEK_OF_MONTH);
                               dateString = FormatUtils.toConvertDate(FormatUtils.toDefaultShortDateFormat(dateBefore), "yyyy-MM-dd", "yyyyMM");

                               LogUser.log("tesfre - Ajudatada - frequencia origianl: " + dateString);
                               LogUser.log("tesfre - Ajustada  - semana: " + weekOfYear);

                           }
                       }catch (Exception ex){
                            LogUser.log(ex.toString());
                       }
                    }

                    freq += dateString;
                    freq+= "_0" + weekOfYear;

                    LogUser.log("tesfre - final: " + freq);


                    break;
                case BIWEEKLY:
                    freq = "QU_";
                    dateString = FormatUtils.toConvertDate(dateString, "dd/MM/yyyy", "yyyyMM");
                    freq += dateString;

                    if(day<= 15){
                        freq+=  "_01";
                    } else {
                        freq+=  "_02";
                    }

                    break;

                case MONTHLY:
                    freq = "ME_";
                    dateString = FormatUtils.toConvertDate(dateString, "dd/MM/yyyy", "yyyyMM");
                    freq += dateString;

                    break;
                case BIMONTHLY:
                    freq = "BI_";
                    dateString = FormatUtils.toConvertDate(dateString, "dd/MM/yyyy", "yyyy");
                    freq += dateString;

                    if(month % 2 != 0){
                        month++;
                    }
                    int bimonthly = month / 2;
                    freq += "_0" + bimonthly;

                    break;
                case QUARTERLY:
                    freq = "TR_";
                    dateString = FormatUtils.toConvertDate(dateString, "dd/MM/yyyy", "yyyy");
                    freq += dateString;
                    freq += "_0" + getQuarterly(currentDate);

                    break;
                case SEMIANNUAL:
                    freq = "SE_";
                    dateString = FormatUtils.toConvertDate(dateString, "dd/MM/yyyy", "yyyy");
                    freq += dateString;

                    int semiannual;

                    if(month <= 6){
                        semiannual = 1;
                    } else {
                        semiannual = 2;
                    }
                    freq += "_0" + semiannual;
                    break;

                case YEARLY:
                    freq = "AN_";
                    dateString = FormatUtils.toConvertDate(dateString, "dd/MM/yyyy", "yyyy");
                    freq += dateString;
                    break;

            }

        }catch (Exception ex){

        }


        return freq;
    }

    public static int getQuarterly(Date date){
        int quarterly = 0;

        try {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);

            int month = calendar.get(Calendar.MONTH);
            month++;

            switch (month){
                case 1:
                case 2:
                case 3:
                    return 1;
                case 4:
                case 5:
                case 6:
                    return 2;
                case 7:
                case 8:
                case 9:
                    return 3;
                case 10:
                case 11:
                case 12:
                    return 4;

            }

        }catch (Exception ex){
            LogUser.log(ex.toString());
        }

        return quarterly;
    }


}
