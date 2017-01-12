package com.magi.chlendar.utils.date;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by MVEN on 16/8/5.
 * <p/>
 * email: magiwen@126.com.
 */

public class Lunar {

    private Calendar cal;
    private int year;// 农历年
    private int yearDays;/*农历天数**/
    private int month;// 农历月
    private int day;// 农历日
    private boolean leap;// 是否闰年
    final static String chineseNumber[] = {"正", "二", "三", "四", "五", "六", "七",
            "八", "九", "十", "冬", "腊"};
    final static String chineseNumber2[] = {"一", "二", "三", "四", "五", "六", "七",
            "八", "九", "十"};
    final static String Big_Or_Small[] = {"大", "小", "大", "小", "大", "小", "大",
            "大", "小", "大", "小", "大"};
    private static String[] SolarTerm = new String[]{"小寒", "大寒", "立春", "雨水",
            "惊蛰", "春分", "清明", "谷雨", "立夏", "小满", "芒种", "夏至", "小暑", "大暑", "立秋",
            "处暑", "白露", "秋分", "寒露", "霜降", "立冬", "小雪", "大雪", "冬至"};
    private static int[] sTermInfo = new int[]{0, 21208, 42467, 63836, 85337,
            107014, 128867, 150921, 173149, 195551, 218072, 240693, 263343,
            285989, 308563, 331033, 353350, 375494, 397447, 419210, 440795,
            462224, 483532, 504758};
    private static final int LUNAR_TO_SOLAR_STEPS = 4;

    // 公历节日
    private static class SolarHolidayStruct {
        public int Month;
        public int Day;
        public int Recess; // 假期长度
        public String HolidayName;

        public SolarHolidayStruct(int month, int day, int recess, String name) {
            Month = month;
            Day = day;
            Recess = recess;
            HolidayName = name;
        }
    }

    // 农历节日
    private static class LunarHolidayStruct {
        public int Month;
        public int Day;
        public int Recess;
        public String HolidayName;

        public LunarHolidayStruct(int month, int day, int recess, String name) {
            Month = month;
            Day = day;
            Recess = recess;
            HolidayName = name;
        }
    }

    // 按农历计算的节日
    private static LunarHolidayStruct[] lHolidayInfo = new LunarHolidayStruct[]{
            new LunarHolidayStruct(1, 1, 1, "春节"), new LunarHolidayStruct(2, 2, 0, "龙抬头"),
            new LunarHolidayStruct(1, 15, 0, "元宵节"), new LunarHolidayStruct(5, 5, 0, "端午节"),
            new LunarHolidayStruct(7, 7, 0, "七夕"), new LunarHolidayStruct(7, 15, 0, "中元节"),
            new LunarHolidayStruct(8, 15, 0, "中秋节"), new LunarHolidayStruct(9, 9, 0, "重阳节"),
            new LunarHolidayStruct(12, 8, 0, "腊八节"),
            new LunarHolidayStruct(12, 30, 0, "除夕") //注意除夕需要其它方法进行计算
    };    // 按公历计算的节日
    private static SolarHolidayStruct[] sHolidayInfo = new SolarHolidayStruct[]{
            new SolarHolidayStruct(1, 1, 1, "元旦"),
            new SolarHolidayStruct(2, 14, 0, "情人节"),
            new SolarHolidayStruct(3, 8, 0, "妇女节"),
            new SolarHolidayStruct(3, 12, 0, "植树节"),
            new SolarHolidayStruct(4, 1, 0, "愚人节"),
            new SolarHolidayStruct(5, 1, 1, "劳动节"),
            new SolarHolidayStruct(5, 4, 0, "青年节"),
            new SolarHolidayStruct(6, 1, 0, "儿童节"),
            new SolarHolidayStruct(7, 1, 0, "建党节"),
            new SolarHolidayStruct(8, 1, 0, "建军节"),
            new SolarHolidayStruct(9, 10, 0, "教师节"),
            new SolarHolidayStruct(10, 1, 1, "国庆节"),
            new SolarHolidayStruct(10, 31, 0, "万圣夜"),
            new SolarHolidayStruct(11, 1, 0, "万圣节"),
            new SolarHolidayStruct(11, 11, 0, "光棍节"),
            new SolarHolidayStruct(12, 24, 0, "平安夜"),
            new SolarHolidayStruct(12, 25, 0, "圣诞节"),
            new SolarHolidayStruct(2, 14, 0, "情人节"),
            new SolarHolidayStruct(5, 1, 1, "劳动节"),
            new SolarHolidayStruct(8, 1, 0, "建军节"),
            new SolarHolidayStruct(9, 10, 0, "教师节"),
            new SolarHolidayStruct(10, 6, 0, "老人节"),
            new SolarHolidayStruct(11, 10, 0, "青年节"),
            new SolarHolidayStruct(12, 25, 0, "圣诞节")
    };    // 星座名称
    private static String[] _constellationName = {"白羊座", "金牛座", "双子座", "巨蟹座",
            "狮子座", "处女座", "天秤座", "天蝎座", "射手座", "摩羯座", "水瓶座", "双鱼座"};
    final static long[] lunarInfo = new long[]
            {0x04bd8, 0x04ae0, 0x0a570, 0x054d5, 0x0d260, 0x0d950, 0x16554, 0x056a0, 0x09ad0, 0x055d2,
                    0x04ae0, 0x0a5b6, 0x0a4d0, 0x0d250, 0x1d255, 0x0b540, 0x0d6a0, 0x0ada2, 0x095b0, 0x14977,
                    0x04970, 0x0a4b0, 0x0b4b5, 0x06a50, 0x06d40, 0x1ab54, 0x02b60, 0x09570, 0x052f2, 0x04970,
                    0x06566, 0x0d4a0, 0x0ea50, 0x06e95, 0x05ad0, 0x02b60, 0x186e3, 0x092e0, 0x1c8d7, 0x0c950,
                    0x0d4a0, 0x1d8a6, 0x0b550, 0x056a0, 0x1a5b4, 0x025d0, 0x092d0, 0x0d2b2, 0x0a950, 0x0b557,
                    0x06ca0, 0x0b550, 0x15355, 0x04da0, 0x0a5d0, 0x14573, 0x052d0, 0x0a9a8, 0x0e950, 0x06aa0,
                    0x0aea6, 0x0ab50, 0x04b60, 0x0aae4, 0x0a570, 0x05260, 0x0f263, 0x0d950, 0x05b57, 0x056a0,
                    0x096d0, 0x04dd5, 0x04ad0, 0x0a4d0, 0x0d4d4, 0x0d250, 0x0d558, 0x0b540, 0x0b5a0, 0x195a6,
                    0x095b0, 0x049b0, 0x0a974, 0x0a4b0, 0x0b27a, 0x06a50, 0x06d40, 0x0af46, 0x0ab60, 0x09570,
                    0x04af5, 0x04970, 0x064b0, 0x074a3, 0x0ea50, 0x06b58, 0x055c0, 0x0ab60, 0x096d5, 0x092e0,
                    0x0c960, 0x0d954, 0x0d4a0, 0x0da50, 0x07552, 0x056a0, 0x0abb7, 0x025d0, 0x092d0, 0x0cab5,
                    0x0a950, 0x0b4a0, 0x0baa4, 0x0ad50, 0x055d9, 0x04ba0, 0x0a5b0, 0x15176, 0x052b0, 0x0a930,
                    0x07954, 0x06aa0, 0x0ad50, 0x05b52, 0x04b60, 0x0a6e6, 0x0a4e0, 0x0d260, 0x0ea65, 0x0d530,
                    0x05aa0, 0x076a3, 0x096d0, 0x04bd7, 0x04ad0, 0x0a4d0, 0x1d0b6, 0x0d250, 0x0d520, 0x0dd45,
                    0x0b5a0, 0x056d0, 0x055b2, 0x049b0, 0x0a577, 0x0a4b0, 0x0aa50, 0x1b255, 0x06d20, 0x0ada0};

    public Lunar(Date date) {
        setCalendar(date);
    }

    private static Lunar lunar;

    public static Lunar getInstance() {
        if (lunar == null)
            lunar = new Lunar(new Date());
        return lunar;
    }

    /**
     * 传回农历 y年的总天数
     */
    final public static int yearDays(int y) {
        int i, sum = 348;
        try {
            for (i = 0x8000; i > 0x8; i >>= 1) {
                if ((lunarInfo[y - 1900] & i) != 0)
                    sum += 1;
            }
            return (sum + leapDays(y));
        } catch (Exception e) {
            // TODO: handle exception
            return sum;
        }
    }

    /**
     * 传回农历 y年闰月的天数
     */
    final private static int leapDays(int y) {
        try {
            if (leapMonth(y) != 0) {
                if ((lunarInfo[y - 1900] & 0x10000) != 0)
                    return 30;
                else
                    return 29;
            } else
                return 0;
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * 传回农历 y年闰哪个月 1-12 , 没闰传回 0
     */
    final private static int leapMonth(int y) {
        int month = 0;
        try {
            month = (int) (lunarInfo[y - 1900] & 0xf);
        } catch (Exception e) {
            // TODO: handle exception
        }
        return month;
    }

    /**
     * 传回农历 y年m月的总天数
     */
    final public static int monthDays(int y, int m) {
        try {
            if (
                    (lunarInfo[y - 1900] & (0x10000 >> m)) == 0
                    )
                return 29;
            else
                return 30;
        } catch (Exception e) {
            return 29;
        }

    }

    public void setCalendar(Date date) {
        if (cal == null)
            cal = Calendar.getInstance();
        cal.setTime(date);
        int leapMonth = 0;
        Date baseDate = null;
        try {
            SimpleDateFormat chineseDateFormat = new SimpleDateFormat(
                    " yyyy年MM月dd日 ");
            baseDate = chineseDateFormat.parse(" 1900年1月31日 ");
        } catch (ParseException e) {
            e.printStackTrace();
        }        // 求出和1900年1月31日相差的天数
        int offset = (int) ((cal.getTime().getTime() - baseDate.getTime()) / 86400000L);
        // 用offset减去每农历年的天数
        // 计算当天是农历第几天
        // i最终结果是农历的年份
        // offset是当年的第几天
        int iYear = 0, daysOfYear = 0;
        for (iYear = 1900; iYear < 2050 && offset > 0; iYear++) {
            daysOfYear = yearDays(iYear);
            offset -= daysOfYear;
        }
        if (offset < 0) {
            offset += daysOfYear;
            iYear--;
        }
        // 农历年份
        year = iYear;

        leapMonth = leapMonth(iYear); // 闰哪个月,1-12
        leap = false;
        // 用当年的天数offset,逐个减去每月（农历）的天数，求出当天是本月的第几天
        int iMonth, daysOfMonth = 0;
        for (iMonth = 1; iMonth < 13 && offset > 0; iMonth++) {
            // 闰月
            if (leapMonth > 0 && iMonth == (leapMonth + 1) && !leap) {
                --iMonth;
                leap = true;
                daysOfMonth = leapDays(year);
            } else
                daysOfMonth = monthDays(year, iMonth);

            offset -= daysOfMonth;            // 解除闰月
            if (leap && iMonth == (leapMonth + 1))
                leap = false;
        }
        // offset为0时，并且刚才计算的月份是闰月，要校正
        if (offset == 0 && leapMonth > 0 && iMonth == leapMonth + 1) {
            if (leap) {
                leap = false;
            } else {
                leap = true;
                --iMonth;
            }
        }
        // offset小于0时，也要校正
        if (offset < 0) {
            offset += daysOfMonth;
            --iMonth;
        }
        month = iMonth;
        day = offset + 1;
    }

    public static String getChinaDayString(int day) {
        try {

            String chineseTen[] = {"初", "十", "廿", "三"/*"卅"*/};
            int n = day % 10 == 0 ? 9 : day % 10 - 1;
            if (day > 30)
                return "";
            if (day == 10) {
                return "初十";
            } else if (day == 20) {
                return "二十";
            } else {
                return chineseTen[day / 10] + chineseNumber2[n];
            }
        } catch (Exception e) {
            return "";
        }

    }

    public String toString() {
        String str = getChinaDayString(day);
        if (day == 1) {
            str = (leap ? "闰" : "") + chineseNumber[month - 1] + "月";
        }
        return str;
    }

    public String getMonthAndDay() {
        String str = (leap ? "闰" : "") + chineseNumber[month - 1] + "月" + getChinaDayString(day);
        return str;
    }

    public String getLunarMonthString() {
        String str = (leap ? "闰" : "") + chineseNumber[month - 1] + "月";
        return str;
    }

    public String getLunarDayString() {
        return getChinaDayString(day);
    }

    public String getMonthAndDayNotLeap() {
        String str = chineseNumber[month - 1] + "月" + getChinaDayString(day);
        return str;
    }

    /**
     * 使用之前,必须setcalendar
     ******/
    public int getLunarMonth() {
        return month;
    }

    public int getLunarDay() {
        return day;
    }

    public int getLunarYear() {
        return year;
    }

    /**
     * 使用之前,必须setcalendar
     ******/

    public boolean getLeap() {
        return leap;
    }

    public String getMonthAndDay(Date date) {
        setCalendar(date);

        String str = (leap ? "闰" : "") + chineseNumber[month - 1] + "月" + getChinaDayString(day);
        return str;
    }

    public String GetDay(Date date) {/*根据日期输出类似初五  add by shi***********/
        setCalendar(date);

        String str = getChinaDayString(day);
        return str;
    }

    /*
     * 获取农历年add by shi
     * **/
    public int getYear() {
        return year;
    }

    public int getMonth() {
        return month;
    }

    public int getDay() {
        return day;
    }

    public String GetMonth(Date date) {/*根据日期输出类似正月  add by shi***********/
        setCalendar(date);

        String str = (leap ? "闰" : "") + chineseNumber[month - 1] + "月";
        return str;
    }

    public String GetMonthNotLeap(Date date) {/*根据日期输出类似正月  add by shi***********/
        setCalendar(date);

        String str = chineseNumber[month - 1] + "月";
        return str;
    }

    /**
     * 返回阿拉伯数字的阴历日期
     */
    public String numeric_md() {
        String temp_day;
        String temp_mon;
        temp_mon = month < 10 ? "0" + month : "" + month;
        temp_day = day < 10 ? "0" + day : "" + day;
        return temp_mon + temp_day;
    }

    /**
     * 返回阴历的月份
     */
    public String get_month() {
        return chineseNumber[month - 1];
    }

    /**
     * 返回阴历的天
     */
    public String get_date() {
        return getChinaDayString(day);
    }

    /**
     * 返回的月份的大或小
     */
    public String get_Big_Or_Small() {
        return Big_Or_Small[month - 1];
    }

    /**
     * 计算并返回中国农历节日
     */
    public String getChineseHoliday() {
        String tempStr = "";
        if (this.leap == false) // 闰月不计算节日
        {
            for (LunarHolidayStruct lh : lHolidayInfo) {
                if ((lh.Month == this.month) && (lh.Day == this.day)) {
                    tempStr = lh.HolidayName;
                    break;
                }
            }
            // 对除夕进行特别处理
            if (this.month == 12) {
                int i = monthDays(this.year, 12); // 计算当年农历12月的总天数
                if (this.day == i)// 如果为最后一天
                {
                    tempStr = "除夕";
                }
            }
        }
        return tempStr;
    }

    /**
     * 按公历计算的节日
     */
    public String getDateHoliday() {
        String tempStr = "";
        for (SolarHolidayStruct sh : sHolidayInfo) {
            if ((sh.Month == cal.get(Calendar.MONTH) + 1)
                    && (sh.Day == cal.get(Calendar.DAY_OF_MONTH))) {
                tempStr = sh.HolidayName;
                break;
            }
        }
        return tempStr;
    }

    /**
     * 定气法计算二十四节气
     */
    public String getChineseTwentyFourDay() {
        if (this.cal.get(Calendar.YEAR) == 2015 &&
                this.cal.get(Calendar.MONTH) == 0) {
            int day = this.cal.get(Calendar.DAY_OF_MONTH);
            if (day == 5) {
                return "";
            } else if (day == 6) {
                return SolarTerm[0];
            }
        }
        // //#1/6/1900 2:05:00 AM#
        Calendar ca = Calendar.getInstance();
        ca.set(1900, 0, 6, 2, 5, 0);
        Calendar newDate = null;
        final Date baseDateAndTime = ca.getTime();
        double num;
        int y;
        String tempStr = "";
        y = this.cal.get(Calendar.YEAR);
        for (int i = 1; i <= 24; i++) {
            num = 525948.76 * (y - 1900) + sTermInfo[i - 1];
            newDate = Calendar.getInstance();
            newDate.setTime(baseDateAndTime);
            newDate.add(Calendar.MINUTE, (int) Math.ceil(num) + 5);// 按分钟计算//(int)Math.ceil(num)+5
// 找来这个计算方法不晓得+5么意思，地址：http://hi.baidu.com/1039580989/item/4fe7743d00394389b711db02
            if (newDate.get(Calendar.DAY_OF_YEAR) == this.cal
                    .get(Calendar.DAY_OF_YEAR)) {
                tempStr = SolarTerm[i - 1];
                break;
            }
        }
        return tempStr;
    }

    /**
     * 返回星座
     */
    public String getConstellation() {
        int index = 0;
        int y, m, d;
        y = this.cal.get(Calendar.YEAR);
        m = this.cal.get(Calendar.MONTH);
        d = this.cal.get(Calendar.DAY_OF_MONTH);
        y = m * 100 + d;
        if (((y >= 321) && (y <= 419))) {
            index = 0;
        } else if ((y >= 420) && (y <= 520)) {
            index = 1;
        } else if ((y >= 521) && (y <= 620)) {
            index = 2;
        } else if ((y >= 621) && (y <= 722)) {
            index = 3;
        } else if ((y >= 723) && (y <= 822)) {
            index = 4;
        } else if ((y >= 823) && (y <= 922)) {
            index = 5;
        } else if ((y >= 923) && (y <= 1022)) {
            index = 6;
        } else if ((y >= 1023) && (y <= 1121)) {
            index = 7;
        } else if ((y >= 1122) && (y <= 1221)) {
            index = 8;
        } else if ((y >= 1222) || (y <= 119)) {
            index = 9;
        } else if ((y >= 120) && (y <= 218)) {
            index = 10;
        } else if ((y >= 219) && (y <= 320)) {
            index = 11;
        } else {
            index = 0;
        }
        return _constellationName[index];
    }


    public static Calendar toSolar(int LunarYear, int LunarMonth, int LunarDay) {
        if (LunarYear < 1500) { // 非常复杂的问题
            return null;
        }

        int ret;
        int y = LunarYear;
        int m = Math.abs(LunarMonth);
        int d = LunarDay;

        int i, ldiff = 0;
        int ly, lm, ld;
        Calendar calendar = Calendar.getInstance();
        calendar.set(y, m, d);
        for (i = 0; i < LUNAR_TO_SOLAR_STEPS; i++) {
            Calendar calendar2 = Calendar.getInstance();
            calendar2.set(y, m, d);
            Date date = calendar2.getTime();
            Lunar lunar = new Lunar(date);
            int year = lunar.getYear();
            int month = lunar.getMonth();
            int day = lunar.getDay();
            calendar2.set(year, month, day);
            ly = year;
            lm = month;
            ld = day;
            if (Math.abs(lm) == Math.abs(LunarMonth) && ld == LunarDay) {
                if (lm == LunarMonth) {
                    Calendar calendar3 = Calendar.getInstance();
                    calendar3.set(y, m, d);
                    return calendar3;
                }
                // 如果找到一个，则开始探测闰月
                ldiff = 30;         // 要么直接转到，要么多了一天而已
            } else {
                ldiff = (LunarYear - ly) * 12 * 29 + (Math.abs(LunarMonth) - Math.abs(lm)) * 29 + (LunarDay - ld);
                if (ldiff == 0) {    // 稍做修正
                    ldiff = 1;
                }
            }
            calendar.add(Calendar.DAY_OF_YEAR, ldiff);
            y = calendar.get(Calendar.YEAR);
            m = calendar.get(Calendar.MONTH);
            d = calendar.get(Calendar.DAY_OF_MONTH);

        }
        return null;
    }
}