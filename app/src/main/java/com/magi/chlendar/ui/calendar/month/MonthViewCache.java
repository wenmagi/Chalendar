package com.magi.chlendar.ui.calendar.month;

import com.magi.chlendar.utils.date.DateTime;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * @author zhangzhaowen @ Zhihu Inc.
 * @since 01-04-2017
 */
public class MonthViewCache {

	private static MonthViewCache mMonthViewCache = null;

	private HashMap<DateTime, ArrayList<DateTime>> mMonthDaysListCache = null;
	private static final int DAYS_LIST_COUNT_MAX = 20;

	private MonthViewCache() {

	}

	public static MonthViewCache getInstance() {
		if (mMonthViewCache == null)
			mMonthViewCache = new MonthViewCache();
		return mMonthViewCache;
	}

	public ArrayList<DateTime> getDateTimeList(DateTime beginOfMonth) {
		if (mMonthDaysListCache == null)
			return null;
		return mMonthDaysListCache.get(beginOfMonth);
	}

	void putDateTimeList(DateTime beginOfMonth, ArrayList<DateTime> list) {
		if (mMonthDaysListCache == null)
			mMonthDaysListCache = new HashMap<>();
		if (mMonthDaysListCache.size() > DAYS_LIST_COUNT_MAX) {
			mMonthDaysListCache.clear();
		}
		mMonthDaysListCache.put(beginOfMonth, list);
	}

	void clearCache() {
		if (mMonthDaysListCache != null) {
			mMonthDaysListCache.clear();
			mMonthDaysListCache = null;
		}

	}
}
