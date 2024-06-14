package br.com.jjconsulting.mobile.dansales.util;

import android.content.Context;
import android.os.Bundle;

import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.Calendar;
import java.util.Date;

import br.com.jjconsulting.mobile.dansales.R;
import br.com.jjconsulting.mobile.dansales.model.Pesquisa;
import br.com.jjconsulting.mobile.dansales.model.PlanoCampo;
import br.com.jjconsulting.mobile.dansales.model.TFreq;
import br.com.jjconsulting.mobile.jjlib.util.FormatUtils;
import br.com.jjconsulting.mobile.jjlib.util.LogUser;

public class PesquisaUtils {

    public static int getStatus(int status) {
        switch (status) {
            case Pesquisa.EM_ABERTO:
                return R.drawable.ic_peq_sts_yellow;
            case Pesquisa.PUBLICADO:
                return R.drawable.ic_peq_sts_green;
            case Pesquisa.INATIVO:
                return R.drawable.ic_peq_sts_red;
            default:
                return R.drawable.ic_peq_sts_green;
        }
    }

    public static int getStatusResposta(int statusResposta) {
        switch (statusResposta) {
            case Pesquisa.PARCIL_RESPONDIDAS:
                return R.drawable.ic_peq_sts_yellow;
            case Pesquisa.OBRIGATORIAS_RESPONDIDAS:
                return R.drawable.ic_peq_sts_green;
            case Pesquisa.OBRIGATORIAS_NAO_RESPONDIDAS:
                return R.drawable.ic_peq_sts_red;
            default:
                return R.drawable.ic_peq_sts_yellow;
        }
    }

    public static boolean isAtividadeObrigatoriaRota(Pesquisa pesquisa, Date date, Date nextDate){
        boolean isObrigatoria = false;

        if(!pesquisa.isAtividadeObrigatoria()){
            return false;
        }

        try {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            int currentDay = calendar.get(Calendar.DAY_OF_MONTH);
            int currentWeekOfYear = calendar.get(Calendar.WEEK_OF_MONTH);
            int currentMonth = calendar.get(Calendar.MONTH);
            int currentYear = calendar.get(Calendar.YEAR);

            currentMonth++;

            Calendar calendarNext = Calendar.getInstance();
            calendarNext.setTime(nextDate);
            int nextDay = calendarNext.get(Calendar.DAY_OF_MONTH);
            int nextWeekOfYear = calendarNext.get(Calendar.WEEK_OF_MONTH);
            int nextMonth = calendarNext.get(Calendar.MONTH);
            int nextYear = calendarNext.get(Calendar.YEAR);
            nextMonth++;

            int currentMonthTemp;
            int nextMonthTemp;

            isObrigatoria = true;

            switch (pesquisa.getFreq()) {
                case DAILY:
                    isObrigatoria = pesquisa.isAtividadeObrigatoria();
                    break;
                case WEEKLY:
                    if(currentYear == nextYear && currentMonth == nextMonth) {
                        if (currentWeekOfYear == nextWeekOfYear) {
                            isObrigatoria = false;
                        }
                    }
                    break;
                case BIWEEKLY:
                    if(currentYear == nextYear && currentMonth == nextMonth) {
                        if((currentDay <= 15 && nextDay <= 15) || (currentDay > 15 && nextDay > 15)){
                            isObrigatoria = false;
                        }

                    }
                    break;
                case MONTHLY:
                    if(currentYear == nextYear && currentMonth == nextMonth) {
                        isObrigatoria = false;
                    }
                    break;
                case BIMONTHLY:
                    if(currentMonth % 2 != 0){
                        currentMonthTemp = currentMonth + 1;
                    } else {
                        currentMonthTemp = currentMonth;
                    }

                    if(nextMonth % 2 != 0){
                        nextMonthTemp = nextMonth + 1;
                    } else {
                        nextMonthTemp = nextMonth;
                    }

                    if((currentMonthTemp / 2) == (nextMonthTemp / 2)){
                        isObrigatoria = false;
                    }

                    break;
                case QUARTERLY:
                    currentMonthTemp = TFreq.getQuarterly(date);
                    nextMonthTemp = TFreq.getQuarterly(nextDate);

                    if(currentMonthTemp == nextMonthTemp){
                        isObrigatoria = false;
                    }

                    break;
                case SEMIANNUAL:
                    if(currentYear == nextYear ) {
                        if((currentMonth <= 6 && nextMonth <= 6) || (currentMonth > 6 && nextMonth > 6)){
                            isObrigatoria = false;
                        }
                    }
                    break;
                case YEARLY:
                    if(currentYear == nextYear) {
                        isObrigatoria = false;
                    }
                    break;

            }

        }catch (Exception ex){

        }

        return isObrigatoria;
    }


}
