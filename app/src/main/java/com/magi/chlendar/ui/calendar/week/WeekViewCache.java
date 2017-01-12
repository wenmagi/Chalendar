package com.magi.chlendar.ui.calendar.week;


import com.magi.chlendar.utils.date.DateTime;

import java.util.ArrayList;
import java.util.HashMap;


public class WeekViewCache {

    private static WeekViewCache mWeekViewCache = null;

    private HashMap<DateTime, ArrayList<DateTime>> mWeekDaysListCache = null;
    private static final int DAYS_LIST_COUNT_MAX = 20 * 7;

    private WeekViewCache() {

    }

    public static WeekViewCache getInstance() {
        if (mWeekViewCache == null)
            mWeekViewCache = new WeekViewCache();
        return mWeekViewCache;
    }


    ArrayList<DateTime> getDateTimeList(DateTime beginOfWeek) {
        if (mWeekDaysListCache == null)
            return null;
        return mWeekDaysListCache.get(beginOfWeek);
    }

    void putDateTimeList(DateTime beginOfWeek, ArrayList<DateTime> list) {
        if (mWeekDaysListCache == null)
            mWeekDaysListCache = new HashMap<>();
        if (mWeekDaysListCache.size() > DAYS_LIST_COUNT_MAX) {
            mWeekDaysListCache.clear();
        }
        mWeekDaysListCache.put(beginOfWeek, list);
    }

    void clearCache() {
        if (mWeekDaysListCache != null) {
            mWeekDaysListCache.clear();
            mWeekDaysListCache = null;
        }

    }
}
