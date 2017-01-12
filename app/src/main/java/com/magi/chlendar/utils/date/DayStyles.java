package com.magi.chlendar.utils.date;

/**
 * Created by MVEN on 16/7/29.
 * <p/>
 * email: magiwen@126.com.
 */


import java.util.Calendar;

public class DayStyles {

    private static String[] getShortWeekName() {
        String[] shortWeek = new String[8];
        shortWeek[Calendar.SUNDAY] = "日";
        shortWeek[Calendar.MONDAY] = "一";
        shortWeek[Calendar.TUESDAY] = "二";
        shortWeek[Calendar.WEDNESDAY] = "三";
        shortWeek[Calendar.THURSDAY] = "四";
        shortWeek[Calendar.FRIDAY] = "五";
        shortWeek[Calendar.SATURDAY] = "六";

        return shortWeek;
    }

    private final static String[] vecStrShortWeekDayNames = getShortWeekName();

    public static String getShortWeekDayName(int iDay) {
        return vecStrShortWeekDayNames[iDay];
    }

    public static int getWeekDay(int index, int iFirstDayOfWeek) {
        int iWeekDay = -1;

        if (iFirstDayOfWeek == Calendar.MONDAY) {
            iWeekDay = index + Calendar.MONDAY;

            if (iWeekDay > Calendar.SATURDAY)
                iWeekDay = Calendar.SUNDAY;
        }

        if (iFirstDayOfWeek == Calendar.SUNDAY) {
            iWeekDay = index + Calendar.SUNDAY;
        }

        return iWeekDay;
    }
}
