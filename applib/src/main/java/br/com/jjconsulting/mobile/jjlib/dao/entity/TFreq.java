package br.com.jjconsulting.mobile.jjlib.dao.entity;

import android.content.Context;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

import br.com.jjconsulting.mobile.jjlib.R;
import br.com.jjconsulting.mobile.jjlib.util.FormatUtils;

public enum TFreq implements Serializable {
    NO(0),
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
            case 0:
                return NO;
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
        }
        return null;
    }

    public static String getName(Context context, TFreq tFreq) {
        switch(tFreq) {
            case NO:
                return  context.getString(R.string.no_freq);
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
        }
        return null;
    }

    public static String fromInteger(Context context, int x) {
        switch(x) {
            case 0:
                return  context.getString(R.string.no_freq);
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
        }
        return null;
    }


    public static String getFreq(TFreq tFreq){
        String dateString = null;
        String freq = "";
        try {
            Date date = FormatUtils.getDateTimeNow(0, 0, 0);
            dateString = FormatUtils.toDefaultDateBrazilianFormat(date);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            int weekOfYear = calendar.get(Calendar.WEEK_OF_MONTH);
            int month = calendar.get(Calendar.MONTH);
            month++;

            switch (tFreq) {
                case NO:
                    freq = "NO";
                    break;
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
    }

}
