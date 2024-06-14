package br.com.jjconsulting.mobile.dansales.util;

import android.content.Context;

import java.util.Calendar;
import java.util.Date;

import br.com.jjconsulting.mobile.dansales.R;
import br.com.jjconsulting.mobile.dansales.database.PlanoCampoDao;
import br.com.jjconsulting.mobile.dansales.model.PlanoCampo;
import br.com.jjconsulting.mobile.jjlib.util.FormatUtils;
import br.com.jjconsulting.mobile.jjlib.util.TextUtils;

public enum PlanoCampoUtils {
    TODAY(1),
    MONDAY(2),
    TUESDAY(3),
    WEDNESDAY(4),
    THURSDAY(5),
    FRIDAY(6),
    SATURDAY(7),
    SUNDAY(8);

    private int intValue;

    public static final String VISITA= "V";
    public static final String PEDIDO = "P";
    public static final String VISITA_PEDIDO = "A";
    public static final String NAO_POSSUI = "N";
    public static final String VISITAEDI = "F";
    public static final String TIPO_E = "E";

    PlanoCampoUtils(int value) {
        intValue = value;
    }

    public int getValue() {
        return intValue;
    }

    public static final int[] filter = new int[] {
            PlanoCampoUtils.TODAY.getValue(),
            PlanoCampoUtils.MONDAY.getValue(),
            PlanoCampoUtils.TUESDAY.getValue(),
            PlanoCampoUtils.WEDNESDAY.getValue(),
            PlanoCampoUtils.THURSDAY.getValue(),
            PlanoCampoUtils.FRIDAY.getValue(),
            PlanoCampoUtils.SATURDAY.getValue(),
            PlanoCampoUtils.SUNDAY.getValue()
    };

    public static PlanoCampoUtils fromInteger(int value){
        PlanoCampoUtils planoCampoUtils = PlanoCampoUtils.TODAY;
        switch (value) {
            case 1:
                planoCampoUtils = PlanoCampoUtils.TODAY;
                break;
            case 2:
                planoCampoUtils = PlanoCampoUtils.MONDAY;
                break;
            case 3:
                planoCampoUtils = PlanoCampoUtils.TUESDAY;
                break;
            case 4:
                planoCampoUtils = PlanoCampoUtils.WEDNESDAY;
                break;
            case 5:
                planoCampoUtils = PlanoCampoUtils.THURSDAY;
                break;
            case 6:
                planoCampoUtils = PlanoCampoUtils.FRIDAY;
                break;
            case 7:
                planoCampoUtils = PlanoCampoUtils.SATURDAY;
                break;
            case 8:
                planoCampoUtils = PlanoCampoUtils.SUNDAY;
                break;
        }

        return planoCampoUtils;
    }

    public static String getFilterName(Context context, int value) {
        String name = "";
        switch (PlanoCampoUtils.fromInteger(value)) {
            case TODAY:
                 name = context.getString(R.string.today);
                 break;
            case MONDAY:
                name = context.getString(R.string.monday);
                break;
            case TUESDAY:
                name = context.getString(R.string.tuesday);
                break;
            case WEDNESDAY:
                name = context.getString(R.string.wednesday);
                break;
            case THURSDAY:
                name = context.getString(R.string.thusday);
                break;
            case FRIDAY:
                name = context.getString(R.string.friday);
                break;
            case SATURDAY:
                name = context.getString(R.string.saturday);
                break;
            case SUNDAY:
                name = context.getString(R.string.sunday);
                break;
        }

        return name;
    }

    public  static int  getDayOfWeek(Date date){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        return fromInteger(day).getValue();
    }

    public static String createPlanoCampo(PlanoCampo planoCampo){

        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK);

        String html = "";
        String content =  "";

        if(isVisita(planoCampo.getVisSeg())){
            content += createDayLabel(PlanoCampoUtils.MONDAY, day);
        }

        if(isVisita(planoCampo.getVisTer())){
            content += createDayLabel(PlanoCampoUtils.TUESDAY, day);
        }

        if(isVisita(planoCampo.getVisQua())){
            content += createDayLabel(PlanoCampoUtils.WEDNESDAY, day);
        }

        if(isVisita(planoCampo.getVisQui())){
            content += createDayLabel(PlanoCampoUtils.THURSDAY, day);
        }

        if(isVisita(planoCampo.getVisSex())){
            content += createDayLabel(PlanoCampoUtils.FRIDAY, day);
        }

        if(isVisita(planoCampo.getVisSab())){
            content += createDayLabel(PlanoCampoUtils.SATURDAY, day);
        }

        if(isVisita(planoCampo.getVisDom())){
            content += createDayLabel(PlanoCampoUtils.SUNDAY, day);
        }

        if (content.length() > 0) {
            html = "<html><body>" + content.substring(0, content.length() - 2) + "</body></html>";
        }

        return html;
    }

    private static String createDayLabel(PlanoCampoUtils planoCampoUtils, int today){
        String content = "";
        String day = "";
        String color = "";

        switch (planoCampoUtils){
            case TODAY:
                day = "TO";
                break;
            case MONDAY:
                day = "SEG";
                break;
            case TUESDAY:
                day = "TER";
                break;
            case WEDNESDAY:
                day = "QUA";
                break;
            case THURSDAY:
                day = "QUI";
                break;
            case FRIDAY:
                day = "SEX";
                break;
            case SATURDAY:
                day = "SAB";
                break;
        }

        if(today == planoCampoUtils.getValue()){
            color = "<font color=\"#489649\">"+ day + "</font>";
            day = color;
        }

        if(content.length() == 0){
            content = day + ", ";
        }

        return content;
    }


    public static String getPlanoCampoFromDate(PlanoCampo planoCampo, Date date) {
        int dayOdweek = getDayOfWeek(date);

        switch (dayOdweek) {
            case 1:
                return  planoCampo.getVisDom();
            case 2:
                return planoCampo.getVisSeg();
            case 3:
                return planoCampo.getVisTer();
            case 4:
                return planoCampo.getVisQua();
            case 5:
                return planoCampo.getVisQui();
            case 6:
                return planoCampo.getVisSex();
            case 7:
                return planoCampo.getVisSab();
            default:
                return "";
        }
    }

    public static String getPlanoCampoToday(PlanoCampo planoCampo) {
        Date now = new Date();
        return getPlanoCampoFromDate(planoCampo, now);
    }

    public static Date getProxVisita(Context context, PlanoCampo planoCampo, Date date) {
        PlanoCampoDao planoCampoDao = new PlanoCampoDao(context);

        int dayOdweek = getDayOfWeek(date);
        Date nextDate = null;


        int countDays = 0;
        boolean isNextPlanoCampo = false;

        while (!isNextPlanoCampo){
            String vis = "";

            countDays++;

            if(dayOdweek == 7){
                dayOdweek = 1;
            } else {
                dayOdweek++;
            }

            switch (dayOdweek) {
                case 1:
                    vis =  planoCampo.getVisDom();
                    break;
                case 2:
                    vis =  planoCampo.getVisSeg();
                    break;
                case 3:
                    vis =  planoCampo.getVisTer();
                    break;
                case 4:
                    vis =  planoCampo.getVisQua();
                    break;
                case 5:
                    vis =  planoCampo.getVisQui();
                    break;
                case 6:
                    vis =  planoCampo.getVisSex();
                    break;
                case 7:
                    vis = planoCampo.getVisSab();
                    break;
                default:
                    vis =  "";
            }

            if (!TextUtils.isNullOrEmpty(vis) && !PlanoCampoUtils.PEDIDO.equals(vis) &&  !PlanoCampoUtils.TIPO_E.equals(vis) && !PlanoCampoUtils.NAO_POSSUI.equals(vis)) {
                nextDate = FormatUtils.addDate(date, countDays,0,0);

                if(planoCampoDao.checkFeriado(nextDate)){
                    nextDate = null;
                }
            }

            if(nextDate != null || countDays > 28){
                isNextPlanoCampo = true;
            }

        }

        return nextDate;

    }


    public static  boolean isVisita(String vis){
        boolean isVisita = false;

        if(!TextUtils.isNullOrEmpty(vis)){
            if(!vis.trim().equals(NAO_POSSUI)){
                isVisita = true;
            }
        }

        return isVisita;
    }
}
