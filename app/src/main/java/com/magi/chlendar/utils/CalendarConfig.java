package com.magi.chlendar.utils;

import android.content.Context;

import java.util.Calendar;

import static com.magi.chlendar.ui.calendar.Util.getScreenWith;

/**
 * @author zhangzhaowen @ Zhihu Inc.
 * @since 01-05-2017
 */

public class CalendarConfig {
	//============Constant==============
	public static final int MONTH_CALENDAR_COLUMN = 7;
	public static final int ROWS_OF_MONTH_CALENDAR = 6;
	private static final int MAX_EVENTS_SCROLL_COUNT = 84000;


	public static final int MAX_WEEK_SCROLL_COUNT = MAX_EVENTS_SCROLL_COUNT / 7;
	public static final int MAX_MONTH_SCROLL_COUNT = MAX_EVENTS_SCROLL_COUNT / 30;
	public static int FIRST_DAY_OF_WEEK = Calendar.MONDAY;

	//============Field=================
	public static int CELL_HEIGHT;

	public static int CELL_WIDTH;

	public static void initInMainThread(Context context) {
		if (context == null)
			return;

		CELL_HEIGHT = CELL_WIDTH = getScreenWith(context) / MONTH_CALENDAR_COLUMN;
	}

	/**
	 * Custom CellHeight.
	 *
	 * @param cellHeight Height of Calendar Cell.
	 */
	@SuppressWarnings("unused")
	public static void setCellHeight(int cellHeight) {
		CELL_HEIGHT = cellHeight;
	}

	/**
	 * Custom CellWidth.
	 *
	 * @param cellWidth Width of Calendar Cell.
	 */
	@SuppressWarnings("unused")
	public static void setCellWidth(int cellWidth) {
		CELL_WIDTH = cellWidth;
	}

	/**
	 * Value of the DAY_OF_WEEK field indicating.
	 * <p>
	 * Monday to Sunday, default is Monday for china.
	 *
	 * @param firstDayOfWeek First day of Week
	 */
	@SuppressWarnings("unused")
	public static void setFirstDayOfWeek(int firstDayOfWeek) {
		FIRST_DAY_OF_WEEK = firstDayOfWeek;
	}
}
