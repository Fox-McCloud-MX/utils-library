package com.mx.util.library.date;

import java.text.DateFormat;
import java.text.Normalizer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * Use this class with Java 7 or lower
 */
public class DateUtil {

    public static final int LUNES       = 0;
    public static final int MARTES      = 1;
    public static final int MIERCOLES   = 2;
    public static final int JUEVES      = 3;
    public static final int VIERNES     = 4;
    public static final int SABADO      = 5;
    public static final int DOMINGO     = 6;

    public static final String DAYS_TO_WEEK[] = {
            "LUNES",    //0
            "MARTES",   //1
            "MIERCOLES",//2
            "JUEVES",   //3
            "VIERNES",  //4
            "SABADO",   //5
            "DOMINGO"   //6
    };

    private static final HashMap<String,Integer> DAY_BY_WORD;
    static{
        DAY_BY_WORD = new HashMap<String,Integer>();
        DAY_BY_WORD.put("LUNES",    0);
        DAY_BY_WORD.put("MARTES",   1);
        DAY_BY_WORD.put("MIERCOLES",2);
        DAY_BY_WORD.put("JUEVES",   3);
        DAY_BY_WORD.put("VIERNES",  4);
        DAY_BY_WORD.put("SABADO",   5);
        DAY_BY_WORD.put("DOMINGO",  6);
    }

    public static int getDayByWord(String day_in_word) {
        day_in_word = deAccent(day_in_word);
        return DAY_BY_WORD.get(day_in_word.toUpperCase());
    }

    public static String getDAYS_TO_WEEK(int day) {
        return DAYS_TO_WEEK[day];
    }

    public static String deAccent(String str) {
        String nfdNormalizedString = Normalizer.normalize(str, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pattern.matcher(nfdNormalizedString).replaceAll("");
    }

    public List<Integer> getFirtsLastDayOfCurrentWeek(int day_of_week){

        List<Integer> days = new ArrayList<>();

        Calendar c = Calendar.getInstance();
        DateFormat df = new SimpleDateFormat("yyyyMMdd");

        //Calendar.DAY_OF_WEEK
        c.set(day_of_week, Calendar.MONDAY);

        try {
            days.add(Integer.parseInt(df.format(c.getTime())));
        } catch (Exception e) {
            days.add(0);
        }

        c.set(day_of_week, Calendar.SUNDAY);
        try {
            days.add(Integer.parseInt(df.format(c.getTime())));
        } catch (Exception e) {
            days.add(0);
        }

        return days;
    }

    public List<String> getDateIncrement(String date, int daysToIncrement, String format) throws ParseException{
        List<String> days = new ArrayList();

        if (format == null || format.equals(""))
            format = "dd-MM-yyyy";

        DateFormat df = new SimpleDateFormat(format);

        try {
            Date dateStart = df.parse(date);

            for (int i = 0; i < daysToIncrement; i++) {
                Calendar cal = Calendar.getInstance();
                cal.setTime(dateStart);
                cal.add(Calendar.DATE, i);
                days.add(df.format(cal.getTime()));
                //System.out.println(df.format(cal.getTime()));
            }
        }
        finally{}

        Collections.reverse(days);
        return days;
    }

    public List<String> getStartEndDateByWeek(int week,String format, int year){
        SimpleDateFormat    sdf     = new SimpleDateFormat(format);
        List<String>        days    = new ArrayList();
        Calendar cal = Calendar.getInstance(Locale.US);

        if (format == null || format.equals(""))
            format = "dd-MM-yyyy";

        if (getNumberOfWeeksInAYear(year) < 53) {
            week = week+1;
        }
        cal.set(Calendar.YEAR,year);
        cal.set(Calendar.WEEK_OF_YEAR, week);
        cal.setFirstDayOfWeek(Calendar.MONDAY);
        //cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);

        Calendar first = (Calendar) cal.clone();
        first.add(Calendar.DAY_OF_WEEK,first.getFirstDayOfWeek() - first.get(Calendar.DAY_OF_WEEK));

        //plus 6 days
        Calendar last = (Calendar) first.clone();
        last.add(Calendar.DAY_OF_YEAR, 6);

        days.add(sdf.format(first.getTime()));
        days.add(sdf.format(last.getTime()));

        return days;
    }

    public List<String> getStartEndDateByMonth(int month, int year, String format){
        SimpleDateFormat    sdf     = new SimpleDateFormat(format);
        List<String>        days    = new ArrayList();

        Calendar gc = new GregorianCalendar();

        gc.set(Calendar.YEAR, year);
        gc.set(Calendar.MONTH, month);
        gc.set(Calendar.DAY_OF_MONTH, 1);
        Date monthStart = gc.getTime();

        gc.add(Calendar.MONTH, 1);
        gc.add(Calendar.DAY_OF_MONTH, -1);
        Date monthEnd = gc.getTime();

        days.add(sdf.format(monthStart));
        days.add(sdf.format(monthEnd));

        return days;
    }

    public int getNumberOfDaysInAMonth(int year, int month ){
        Calendar mycal = new GregorianCalendar(year, month, 1);

        // Get the number of days in that month
        return  mycal.getActualMaximum(Calendar.DAY_OF_MONTH);
    }


    public HashMap<String,String> getDayName(List<String> days, String search_by, int month, int year){
        Collections.reverse(days);
        HashMap<String,String> map = new LinkedHashMap<>();

        int index = 0;

        if (month >= 0)
            index = getDayByWord(getDayOfFirstDayOfMonth(month, year));

        for (int i = 0; i < days.size(); i++) {

            if (i != 0) {
                if (index % 7 == 0 ) {
                    index = 0;
                }
            }

            String date = days.get(i);

            if (date.length() == 8) {
                if (date.substring(6).contains("0") && !date.substring(6).equals("10") &&
                        !date.substring(6).equals("20") && !date.substring(6).equals("30"))

                    date = date.substring(7);
                else
                    date = date.substring(6);
            }

            String dia_letra = DateUtil.DAYS_TO_WEEK[index];

            if (search_by.equals("month")) {
                dia_letra = ""+dia_letra.charAt(0);
            }

            map.put(date, dia_letra);

            index++;
        }

        return map;
    }

    //format = yyyyMMdd
    public String getDayByDate(String date){

        if (date.length() == 8) {
            String var = date.substring(6);
            if (date.substring(6).contains("0") && !date.substring(6).equals("10") &&
                    !date.substring(6).equals("20") && !date.substring(6).equals("30"))

                date = date.substring(7);
            else
                date = date.substring(6);
        }

        return date;
    }

    public String getDayOfFirstDayOfMonth(int month, int year){
        Calendar cal = Calendar.getInstance();

        cal.set(Calendar.MONTH, month);
        cal.set(Calendar.YEAR, year);

        cal.set(Calendar.DAY_OF_MONTH, 1);
        Date firstDayOfMonth = cal.getTime();

        Locale local = new Locale("es", "ES");

        DateFormat sdf = new SimpleDateFormat("EEEEEEEE",local);
        String dia = sdf.format(firstDayOfMonth);

        return dia;
    }

    public int getNumberOfWeeksInAYear(int year){
        Locale.setDefault(Locale.GERMAN);
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, Calendar.DECEMBER);
        c.set(Calendar.DAY_OF_MONTH, 31);
        //System.out.println("max:    " + c.getActualMaximum(Calendar.WEEK_OF_YEAR));
        //System.out.println("actual: " + c.get(Calendar.WEEK_OF_YEAR));
        return c.getActualMaximum(Calendar.WEEK_OF_YEAR);
    }

    public List<Integer> addOrSubstractDate(int month, int year, String operation){
        List<Integer> new_dates = new ArrayList<>();

        if (operation.equals("+")) {
            if (month >= 12){
                new_dates.add(1);
                new_dates.add(year+1);
            }
            else{
                new_dates.add(month+1);
                new_dates.add(year);
            }
        }
        else if(operation.equals("-")){
            if (month >= 2 && month <= 12){
                new_dates.add(month-1);
                new_dates.add(year);
            }
            else{
                new_dates.add(12);
                new_dates.add(year-1);
            }
        }
        else{
            new_dates.add(month);
            new_dates.add(year);
        }
        return new_dates;
    }

    public static int getCurrentYear(){
        SimpleDateFormat    sdf     = new SimpleDateFormat("YYYY");
        Calendar cal = Calendar.getInstance();
        Date date = cal.getTime();
        return Integer.parseInt(sdf.format(date.getTime()));
    }

}
