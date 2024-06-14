package br.com.jjconsulting.mobile.jjlib.util;

import android.content.Context;
import androidx.annotation.NonNull;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import br.com.jjconsulting.mobile.jjlib.model.DateCompare;

public class FormatUtils {

    private static final int CNPJ_LENGHT = 14;

    private FormatUtils() {

    }

    public static String createIDTemp(String userID){
        Date dNow = new Date();
        SimpleDateFormat ft = new SimpleDateFormat("MMddhhmmssMs");
        String datetime = ft.format(dNow);
        //datetime += "-"+ String.valueOf(userID);

        if(datetime.length() > 10){
            return datetime.substring(0,10);
        } else {
            return datetime;
        }
    }

    public static boolean isSameDay(Date date1, Date date2) {
        Calendar calendar1 = Calendar.getInstance();
        calendar1.setTime(date1);

        Calendar calendar2 = Calendar.getInstance();
        calendar2.setTime(date2);

        if (calendar1 == null || calendar2 == null) {
            throw new IllegalArgumentException("The dates must not be null");
        }
        return (calendar1.get(Calendar.ERA) == calendar2.get(Calendar.ERA) &&
                calendar1.get(Calendar.YEAR) == calendar2.get(Calendar.YEAR) &&
                calendar1.get(Calendar.DAY_OF_YEAR) == calendar2.get(Calendar.DAY_OF_YEAR));
    }


    public static boolean isDateValid(String dateToValidate) {
        String dateFromat = "dd/MM/yyyy";

        if (dateToValidate == null) {
            return false;
        }

        SimpleDateFormat sdf = new SimpleDateFormat(dateFromat);
        sdf.setLenient(false);

        try {
            Date date = sdf.parse(dateToValidate);
            return true;
        } catch (ParseException e) {

            e.printStackTrace();
            return false;
        }

    }

    public static int getDayOfWeek(Date date){
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);

        return dayOfWeek;
    }

    /**
     * ???
     *
     * @param date
     * @return
     */
    public static String toDefaultDateBrazilianFormat(Date date) {
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        return df.format(date);
    }

    /**
     * ???
     *
     * @param date
     * @return
     */
    public static String toDefaultDateHoursBrazilianFormat(Date date) {
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        return df.format(date);
    }

    /**
     * The default date format is the same the device.
     */
    public static String toDefaultDateFormat(Context context, Date date) {
        DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(context);
        return dateFormat.format(date);
    }

    /**
     * Compare date
     */
    public static DateCompare compareDate(Date date1, Date date2) {
        int status = resetTimeToMidnight(date1).compareTo(resetTimeToMidnight(date2));
        switch (status) {
            case 0:
                return DateCompare.EQUAL;
            case 1:
                return DateCompare.AFTER;
            case -1:
                return DateCompare.BERFORE;
            default:
                return DateCompare.BERFORE;
        }
    }

    /**
     * Reset time
     *
     * @param date
     * @return
     */
    public static Date resetTimeToMidnight(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        return calendar.getTime();
    }

    /**
     * The default hour format is H:mm for 24h and h:mma for AM/PM.
     */
    public static String toDefaultHourFormat(Context context, Date date) {
        boolean is24HourFormat = android.text.format.DateFormat.is24HourFormat(context);
        SimpleDateFormat hourFormat = new SimpleDateFormat(is24HourFormat ? "H:mm" : "h:mma");
        return hourFormat.format(date);
    }

    /**
     * The default date format is the same the device and
     * the default hour format is H:mm for 24h and h:mma for AM/PM.
     */
    public static String toDefaultDateAndHourFormat(Context context, Date date) {
        return String.format("%s %s", toDefaultDateFormat(context, date),
                toDefaultHourFormat(context, date));
    }


    /**
     * Format a BigDecimal value as text using the Brazilian Portuguese currency format.
     */
    public static String toBrazilianRealCurrency(BigDecimal value) {
        NumberFormat numberFormat = NumberFormat.getCurrencyInstance(
                LocaleUtils.getPortugueseBrazilianLocale());
        return numberFormat.format(value);
    }

    /**
     * Format a double value as text using the Brazilian Portuguese currency format.
     */
    public static String toBrazilianRealCurrency(double value) {
        NumberFormat numberFormat = NumberFormat.getCurrencyInstance(
                LocaleUtils.getPortugueseBrazilianLocale());
        return numberFormat.format(value);
    }

    /**
     * Format a double value as text using percent notation format.
     */
    public static String toPercent(double value, int precision) {
        String format = "%1$,." + String.valueOf(precision) + "f%%";
        return String.format(LocaleUtils.getPortugueseBrazilianLocale(), format, value);
    }

    /**
     * Format a double value as (text) presentation for kilogram.
     */
    public static String toKilogram(double value, int precision) {
        String format = "%1$,." + String.valueOf(precision) + "f kg";
        return String.format(LocaleUtils.getPortugueseBrazilianLocale(), format, value);
    }

    /**
     * ???
     *
     * @param value
     * @return
     * @throws ParseException
     */
    public static Date toDate(String value) throws ParseException {
        Date d = null;
        if (value != null && value.length() > 0) {
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            d = df.parse(value);
        }
        return d;
    }

    /**
     * @param value
     * @return
     * @throws ParseException
     */
    public static Date toDate(String value, String format) throws ParseException {
        Date d = null;
        if (value != null && value.length() > 0) {
            DateFormat df = new SimpleDateFormat(format);
            d = df.parse(value);
        }
        return d;
    }


    /**
     * ???
     *
     * @param value
     * @return
     * @throws ParseException
     */
    public static Date toDateSeconds(String value) throws ParseException {
        Date d = null;
        if (value != null && value.length() > 0) {
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            d = df.parse(value);
        }
        return d;
    }

    /**
     * ???
     *
     * @param value
     * @return
     * @throws ParseException
     */

    /**
     * ???
     *
     * @param value
     * @return
     * @throws ParseException
     */
    public static Date toDateBrazilian(String value) throws ParseException {
        Date d = null;
        if (value != null && value.length() > 0) {
            DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            d = df.parse(value);
        }
        return d;
    }

    /**
     * ???
     *
     * @param value
     * @return
     * @throws ParseException
     */
    public static Date toDateShort(String value) throws ParseException {
        Date d = null;
        if (value != null && value.length() > 0) {
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            d = df.parse(value);
        }
        return d;
    }

    /**
     * ???
     *
     * @param date
     * @return
     */
    public static String toDefaultDateFormat(Date date) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        return df.format(date);
    }

    /**
     * ???
     *
     * @param date
     * @return
     */
    public static String toDefaultShortDateFormat(Date date) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        return df.format(date);
    }

    /**
     * ???
     *
     * @param value
     * @return
     * @throws ParseException
     */
    public static String toDateTimeText(String value) throws ParseException {

        value = value.replace("-", "");
        value = value.replace(":", "");
        value = value.trim();

        SimpleDateFormat sdfDatabase = new SimpleDateFormat("yyyyMMddHHmm");

        Date currentdate = sdfDatabase.parse(value);

        SimpleDateFormat sdfDefautl = new SimpleDateFormat("dd/MM/yyyy");

        return sdfDefautl.format(currentdate);
    }

    public static Date toDateTimeFixed(){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());

        return toCreateDatePicker(calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
    }

    public static Calendar removeTimeCalendar(Calendar calendar){
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        return calendar;
    }

    public static Date toCreateDatePicker(int year, int month, int day) {
        GregorianCalendar calendarBeg = new GregorianCalendar(year,
                month, day);
        Date date = calendarBeg.getTime();
        return date;
    }

    public static String toDateCreateDatePicker(int year, int month, int day) throws ParseException {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);
        String dateFormat = String.format(new Locale("pt", "BR"),
                "%1$te/%1$tm/%1$tY", calendar);
        if (day < 10) {
            dateFormat = String.format("%s%s", "0", dateFormat);
        }
        return dateFormat;
    }

    public static String toConvertDate(String value, String format, String convertFormat) throws ParseException {
        SimpleDateFormat sdfDatabase = new SimpleDateFormat(format);
        Date currentdate = sdfDatabase.parse(value);
        SimpleDateFormat sdfDefautl = new SimpleDateFormat(convertFormat);
        return sdfDefautl.format(currentdate);
    }


    public static Date convertTextInDatePT(String stringDate){
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

        Date date = null;

        try {
             date = sdf.parse(stringDate);
        } catch (ParseException ex) {
            LogUser.log(Config.TAG, ex.getMessage());
        }

        return date;

    }

    /**
     * ???
     *
     * @param value
     * @return
     * @throws ParseException
     */
    public static String toDateText(String value) throws ParseException {

        SimpleDateFormat sdfDatabase = new SimpleDateFormat("yyyyMMdd");

        Date currentdate = sdfDatabase.parse(value);

        SimpleDateFormat sdfDefautl = new SimpleDateFormat("dd/MM/yyyy");

        return sdfDefautl.format(currentdate).toString();
    }

    /**
     * Format a string as a CNPJ. Must have 14 characters.
     *
     * @param cnpj
     * @return formatted cnpj
     * @throws NullPointerException
     * @throws Exception
     */
    public static String toCnpjFormat(@NonNull String cnpj) throws Exception {
        if (cnpj == null) {
            throw new NullPointerException("CNPJ must be provided.");
        }

        if (cnpj.length() != CNPJ_LENGHT) {
            throw new Exception("CNPJ must have 14 characters to be formatted.");
        }

        return cnpj.substring(0, 2) +
                "." + cnpj.substring(2, 5) +
                "." + cnpj.substring(5, 8) +
                "/" + cnpj.substring(8, 12) +
                "-" + cnpj.substring(12);
    }

    public static String toStringIntFormat(int number) {
        String stringNumber = NumberFormat.getNumberInstance(LocaleUtils.getPortugueseBrazilianLocale()).format(number);
        return stringNumber;
    }

    public static String toStringDoubleFormat(String number) {
        try {
            DecimalFormat df = new DecimalFormat("#,##0.00");
            double numberDouble = Double.parseDouble(number);
            return df.format(numberDouble);
        } catch (Exception ex) {
            return number;
        }
    }

    public static String toDoubleFormat(double number) {
        try {
            DecimalFormat df = new DecimalFormat("#,##0.00");
            double numberDouble = number;
            return df.format(numberDouble);
        } catch (Exception ex) {
            return String.valueOf(number);
        }
    }

    public static Date toJsonDateConvert(String jsonDate) {
        int idx1 = jsonDate.indexOf("(");
        int idx2 = jsonDate.indexOf(")") - 5;
        String s = jsonDate.substring(idx1 + 1, idx2);
        long l = Long.valueOf(s);
        return new Date(l);
    }

    /**
     * Format a string as a CNPJ. Must have 14 characters.
     *
     * @param cnpj
     * @return
     */
    public static String maskCNPJ(String cnpj) {
        Pattern pattern = Pattern.compile("(\\d{2})(\\d{3})(\\d{3})(\\d{4})(\\d{2})");
        Matcher matcher = pattern.matcher(cnpj);
        if (matcher.find()) {
            return matcher.replaceAll("$1.$2.$3/$4-$5");
        }
        return cnpj;
    }

    /**
     * Insert carcater "," in String
     *
     * @param value
     * @param qtd
     * @return
     */
    public static double insertCaseDecimal(String value, int qtd) {
        try {
            if (value != null && !value.isEmpty() && value.length() > qtd) {
                value = new StringBuffer(value).insert(value.length() - qtd, ".").toString();
            } else {
                value = "0.00";
            }
        } catch (Exception ex) {
            value = "0.00";
        }

        return Double.parseDouble(value);
    }

    /**
     * Add days, months and years in date
     *
     * @param date
     * @param addDays
     * @param addMonth
     * @param addYear
     * @return
     */
    public static Date addDate(Date date, int addDays, int addMonth, int addYear) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.DATE, addDays);
        c.add(Calendar.MONTH, addMonth);
        c.add(Calendar.YEAR, addYear);
        Date dt = c.getTime();
        return dt;
    }

    /**
     * Date and hour now
     *
     * @param addDays
     * @param addMonth
     * @param addYear
     * @return
     */
    public static Date getDateTimeNow(int addDays, int addMonth, int addYear) {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE, addDays);
        c.add(Calendar.MONTH, addMonth);
        c.add(Calendar.YEAR, addYear);
        Date dt = c.getTime();
        return dt;
    }

    public static long calculateDays(Date dateEarly, Date dateLater) {
        return (dateLater.getTime() - dateEarly.getTime()) / (24 * 60 * 60 * 1000);
    }

    public static Date addDay(Date oldDate, int numberOfDays) {
        Calendar c = Calendar.getInstance();
        try {
            c.setTime(oldDate);
        } catch (Exception e) {
            e.printStackTrace();
        }
        c.add(Calendar.DAY_OF_YEAR,numberOfDays);
        Date newDate = new Date(c.getTimeInMillis());

        return newDate;
    }

    public static String toTextToDatePT(Date date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        return simpleDateFormat.format(date);
    }

    public static Date getLastDayMonthBefore() {
        try {
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.MONTH, -1);

            Calendar calendarLastDay = calendar;
            calendarLastDay.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));

            Date date = calendarLastDay.getTime();
            SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
            String dateOutput = format.format(date);

            Date dateFinal = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").parse(dateOutput + " 23:59:59");


            return dateFinal;
        }catch (Exception ex){
            return getFirstDateMonth();
        }
    }

    public static Date getFirstDateMonth(){
        Calendar c = Calendar.getInstance();
        c.set(Calendar.DAY_OF_MONTH, 1);
        return c.getTime();
    }


    /**
     * Calcula intervalo entre datas em minutos
     * @param dateStart
     * @param dateEnd
     * @return
     */
    public static long  diffDateMin(Date dateStart, Date dateEnd) {

        long millisStart = dateStart.getTime();
        long millisEnd = dateEnd.getTime();
        long diff = millisEnd - millisStart;

        long diffInMinutes = TimeUnit.MILLISECONDS.toSeconds(diff);

        return diffInMinutes ;
    }


    /**
     * Format a date as the requested format to compare dates in SQLite queries.
     */
    public static String toTextToCompareDateInSQlite(Date date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return simpleDateFormat.format(date);
    }

    public static String toTextToCompareDateTInSQlite(Date date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        return simpleDateFormat.format(date);
    }

    public static String toTextToCompareshortDateInSQlite(Date date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return simpleDateFormat.format(date);
    }

    public static String convertFileSize(long size) {
        if (size <= 0)
            return "0";

        final String[] units = new String[]{"B", "KB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));

        return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }

    public static String toCleanNumber(String text) {
        text = text.replace(" ","");
        return text;
    }

    public static boolean compareHours(String startHours, String endHours) {
        SimpleDateFormat sdfConvert = new SimpleDateFormat("HH:mm");
        Date startDate = null;
        Date endDate = null;
        try {
            startDate = sdfConvert.parse(startHours);
            endDate = sdfConvert.parse(endHours);
        } catch (ParseException ex) {
            LogUser.log(ex.getMessage());
            return false;
        }

        GregorianCalendar gc = new GregorianCalendar();
        gc.setTime(startDate);

        if (startDate.getTime() >= endDate.getTime()){
            return false;
        } else{
            return true;
        }

    }
}
