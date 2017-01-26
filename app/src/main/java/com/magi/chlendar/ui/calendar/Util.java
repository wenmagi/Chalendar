package com.magi.chlendar.ui.calendar;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.WindowManager;
import android.widget.ImageView;

import com.magi.chlendar.R;
import com.magi.chlendar.ui.calendar.month.MonthViewCache;
import com.magi.chlendar.ui.calendar.unit.CalendarCellView;
import com.magi.chlendar.utils.LogUtils;
import com.magi.chlendar.utils.date.CalendarHelper;
import com.magi.chlendar.utils.date.DateTime;
import com.magi.chlendar.utils.date.Lunar;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static android.text.TextUtils.isEmpty;
import static com.magi.chlendar.utils.CalendarConfig.MAX_MONTH_SCROLL_COUNT;
import static com.magi.chlendar.utils.date.Util.cc_dateByMovingToBeginningOfDay;
import static com.magi.chlendar.utils.date.Util.getString;
import static com.magi.chlendar.utils.date.Util.isNotEmpty;

/**
 * @author zhangzhaowen @ Zhihu Inc.
 * @since 01-04-2017
 */

public class Util {

	public static float DENSITY;
	public static float SCALEDDENSITY;
	public static int WIDTH;
	public static int HEIGHT;

	@SuppressWarnings("unused")
	public static float pix2sp(Context context, int pix) {
		return pix > 0 ? pix / getScaleDensity(context) + 0.5f : pix;
	}

	@SuppressWarnings("unused")
	public static float pix2dp(Context context, int pix) {
		return pix > 0 ? pix / getDensity(context) + 0.5f : pix;
	}

	@SuppressWarnings("unused")
	public static int sp2pix(Context context, float sp) {
		return sp > 0 ? (int) (sp * getScaleDensity(context) + 0.5f) : (int) sp;
	}

	public static int dp2pix(Context context, float dp) {
		return dp > 0 ? (int) (dp * getDensity(context) + 0.5f) : (int) dp;
	}

	@SuppressWarnings("unused")
	public static ArrayList<DateTime> getMonthDateTimeByDay(int position) {
		Calendar firstDayOfTodayMonth = Calendar.getInstance();
		firstDayOfTodayMonth.set(Calendar.DAY_OF_MONTH, 1);

		Date beginOf = cc_dateByMovingToBeginningOfDay(firstDayOfTodayMonth.getTime());

		int offSet = position - MAX_MONTH_SCROLL_COUNT / 2;

		DateTime currentTime;
		if (offSet > 0)
			currentTime = CalendarHelper.convertDateToDateTime(
					beginOf).plus(0, offSet, 0, 0, 0, 0, 0,
					DateTime.DayOverflow.LastDay);
		else if (offSet < 0)
			currentTime = CalendarHelper.convertDateToDateTime(
					beginOf).minus(0, -offSet, 0, 0, 0, 0, 0,
					DateTime.DayOverflow.LastDay);
		else
			currentTime = CalendarHelper
					.convertDateToDateTime(beginOf);

		return MonthViewCache.getInstance().getDateTimeList(
				currentTime);
	}

	public static float getDensity(Context context) {

		if (DENSITY != 0)
			return DENSITY;

		WindowManager wm = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		DisplayMetrics metric = new DisplayMetrics();
		display.getMetrics(metric);
		DENSITY = metric.density;
		SCALEDDENSITY = metric.scaledDensity;
		return DENSITY;
	}

	public static float getScaleDensity(Context context) {
		if (SCALEDDENSITY != 0)
			return SCALEDDENSITY;

		WindowManager wm = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		DisplayMetrics metric = new DisplayMetrics();
		display.getMetrics(metric);
		DENSITY = metric.density;
		SCALEDDENSITY = metric.scaledDensity;
		return SCALEDDENSITY;
	}

	public static int getScreenWith(Context context) {
		if (WIDTH > 0)
			return WIDTH;

		WindowManager wm = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB_MR2) {
			WIDTH = display.getWidth();
			HEIGHT = display.getHeight();
		} else {
			Point size = new Point();
			display.getSize(size);
			WIDTH = size.x;
			HEIGHT = size.y;
		}
		return WIDTH;
	}

	public static int getScreenHeight(Context context) {
		if (HEIGHT > 0)
			return HEIGHT;

		WindowManager wm = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB_MR2) {
			WIDTH = display.getWidth();
			HEIGHT = display.getHeight();
		} else {
			Point size = new Point();
			display.getSize(size);
			WIDTH = size.x;
			HEIGHT = size.y;
		}
		return HEIGHT;
	}

	public static DateTime getToday() {
		return CalendarHelper.convertDateToDateTime(new Date());
	}


	//===============View=================

	/**
	 * 获取文字的高度
	 *
	 * @param paint
	 * @param str
	 * @return
	 */
	public static float getTextHeight(Paint paint, String str) {
		if (paint == null || isEmpty(str))
			return 0;
		Paint.FontMetrics metrics = paint.getFontMetrics();
		return (int) Math.ceil(metrics.descent - metrics.top) + 2;
	}

	/**
	 * 获取文字的宽度
	 *
	 * @param paint
	 * @param str
	 * @return
	 */
	@SuppressWarnings("unused")
	public static float getTextWidth(Paint paint, String str) {
		if (paint == null || isEmpty(str))
			return 0;
		return paint.measureText(str);
	}

	/**
	 * 设置Cell是否有事件
	 */
	public static void updateEventCount(CalendarCellView mCell, DateTime dateTime, DateTime currentDateTime) {
		if (!dateTime.getMonth().equals(currentDateTime.getMonth())) {
			mCell.setEventCount(0);
		} else {
			// TODO 获取事件个数
			int eventsCount = 0;
			mCell.setEventCount(eventsCount);
			if (eventsCount > 0) {
				if (mCell.isSelected()) {
					mCell.setHasEventsCirclePaint(CalendarHelper
							.getHasEventsCirclePaintWhite(mCell.getContext()));
				} else {
					mCell.setHasEventsCirclePaint(CalendarHelper
							.getHasEventsCirclePaintGreen(mCell.getContext()));
				}
			} else {
				mCell.setEventCount(0);
			}
		}
	}

	/**
	 * 设置选中和未选中的状态
	 *
	 * @param mCell            日期Cell
	 * @param dateTime         日期Cell中代表的日期
	 * @param currentDateTime  当前日期
	 * @param selectedTime     选中的日期
	 * @param distinguishMonth 是否区分月份
	 */
	public static void updateChooseState(CalendarCellView mCell, DateTime dateTime, DateTime currentDateTime, DateTime selectedTime, boolean distinguishMonth) {

		if (!distinguishMonth) {
			if (dateTime.equals(selectedTime)) {
				drawSelectedCell(mCell);
			} else {
				if (mCell.isLunarHoliday())
					mCell.setTipPaint(CalendarHelper.getTipPaintLunarHolidayRed(mCell.getContext()));
				else if (mCell.isSolarHoliday())
					mCell.setTipPaint(CalendarHelper.getTipPaintSolarHoliday(mCell.getContext()));
				else
					mCell.setTipPaint(CalendarHelper.getTipPaintLunarDate(mCell.getContext()));
				mCell.setSelected(false);
				mCell.setSelectedCirclePaint(null);
			}

			return;
		}
		if ((currentDateTime.getMonth().equals(selectedTime.getMonth()) && dateTime.equals(selectedTime))
				|| (!currentDateTime.getMonth().equals(selectedTime.getMonth()) && dateTime.equals(currentDateTime))) {

			drawSelectedCell(mCell);

		} else {

			if (dateTime.getMonth().equals(currentDateTime.getMonth())) {

				if (mCell.isLunarHoliday())
					mCell.setTipPaint(CalendarHelper.getTipPaintLunarHolidayRed(mCell.getContext()));
				else if (mCell.isSolarHoliday())
					mCell.setTipPaint(CalendarHelper.getTipPaintSolarHoliday(mCell.getContext()));
				else
					mCell.setTipPaint(CalendarHelper.getTipPaintLunarDate(mCell.getContext()));

			} else {

				mCell.setTipPaint(CalendarHelper
						.getTipPaintGray(mCell.getContext()));
			}
			mCell.setSelected(false);
			mCell.setSelectedCirclePaint(null);

		}
	}

	private static void drawSelectedCell(CalendarCellView cellView) {
		cellView.setSelected(true);
		cellView.setContentPaint(CalendarHelper
				.getContentPaintWhite(cellView.getContext()));
		cellView.setTipPaint(cellView.isLunarHoliday() ? CalendarHelper.getTipPaintLunarHolidayWhite(cellView.getContext()) : CalendarHelper.getTipPaintWhite(cellView.getContext()));
		cellView.setSelectedCirclePaint(CalendarHelper
				.getSelectedCirclePaint(cellView.getContext()));
	}

	/**
	 * 设置当天或他天背景 Stroke 圆圈
	 */
	public static void updateCellBg(CalendarCellView mCell, DateTime dateTime) {

		if (dateTime.equals(getToday())) {
			mCell.setToday(true);
			mCell.setTodayCirclePaint(CalendarHelper
					.getTodayCirclePaint(mCell.getContext()));
		} else {
			mCell.setToday(false);
			mCell.setTodayCirclePaint(null);
		}

		mCell.setDuty(false);
		mCell.setBitmapHoliday(null);
	}

	/**
	 * 设置阳历文本颜色，28，29，30，31 等等
	 */
	public static void updatePaintColor(CalendarCellView mCell, DateTime dateTime, DateTime currentDateTime, boolean distinguishMonth) {

		//设置双休和工作日文本颜色
		if (dateTime.getWeekDay() == Calendar.SATURDAY
				|| dateTime.getWeekDay() == Calendar.SUNDAY)
			mCell.setContentPaint(CalendarHelper
					.getContentPaintGreen(mCell.getContext()));
		else
			mCell.setContentPaint(CalendarHelper
					.getContentPaintBlack(mCell.getContext()));

		if (!distinguishMonth)
			return;

		//设置当月和他月文本颜色
		if (dateTime.getMonth().equals(currentDateTime.getMonth())) {
			mCell.setActiveMonth(true);
		} else {
			mCell.setActiveMonth(false);
			mCell.setContentPaint(CalendarHelper
					.getContentPaintGray(mCell.getContext()));
		}
	}

	/**
	 * 设置tips，包括节假日、农历
	 */
	public static void updateTips(CalendarCellView mCell, DateTime dateTime) {
		Lunar lunar = Lunar.getInstance();
		lunar.setCalendar(dateTime.getDate());
		String lunarStr = lunar.getLunarDayString();
		String holiday = lunar.getChineseHoliday();
		String solarHoliday = lunar.getDateHoliday();
		String solarTerm = lunar.getChineseTwentyTermsDay();
		String tips;
		if (isNotEmpty(holiday)) {
			mCell.setLunarHoliday(true);
			tips = holiday;
		} else if (isNotEmpty(solarHoliday)) {
			mCell.setSolarHoliday(true);
			tips = solarHoliday;
		} else if(isNotEmpty(solarTerm)){
			mCell.setSolarHoliday(true);
			tips = solarTerm;
		}else {
			if (lunarStr != null && lunarStr.contains(getString(mCell.getContext(), R.string.first_day_of_lunar_month))) {
				tips = lunar.getLunarMonthString();
			} else {
				tips = lunarStr;
			}
			mCell.setLunarHoliday(false);
			mCell.setSolarHoliday(false);
		}
		if (tips != null)
			mCell.setStrTip(tips);
	}

	/**
	 * Remove a view from viewgroup.
	 *
	 * @param o can be null
	 */
	public static void removeFromSuperView(Object o) {
		if (o == null)
			return;

		if (o instanceof View) {
			View view = (View) o;
			final ViewParent parent = view.getParent();
			if (parent == null)
				return;
			if (parent instanceof ViewGroup) {
				ViewGroup group = (ViewGroup) parent;
				group.removeView(view);
			} else
				LogUtils.w("the parent of view %s is not a viewgroup: %s", view, parent);
		} else if (o instanceof Dialog) {
			Dialog dialog = (Dialog) o;
			dialog.hide();
		}
	}

	/**
	 * 设置ImageView点击变色效果,这样不需要为ImageView准备两套图
	 * 点击前不透明/不缩放，点击后透明度变为60%／缩放90%
	 *
	 * @param targetView 需要添加点击效果的ImageView
	 */
	public static void setImageClickStateChangeListener(final ImageView targetView) {
		targetView.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
					case MotionEvent.ACTION_DOWN:
						targetView.setAlpha(0.6f);
						targetView.setScaleX(0.9f);
						targetView.setScaleY(0.9f);
						break;
					case MotionEvent.ACTION_UP:
						targetView.setAlpha(1f);
						targetView.setScaleX(1f);
						targetView.setScaleY(1f);
						break;
				}
				return false;
			}
		});
	}


	/**
	 * 设置ImageView点击变色效果,这样不需要为View准备两个Shape文件
	 *
	 * @param targetView 需要添加点击效果的View
	 */
	public static void setViewClickStateChangeListener(final View targetView) {
		targetView.setOnTouchListener(new View.OnTouchListener() {
			@RequiresApi(api = Build.VERSION_CODES.M)
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
					case MotionEvent.ACTION_DOWN:
						targetView.getBackground().setColorFilter(targetView.getContext().getColor(R.color.alpha_20_percent_black), PorterDuff.Mode.SRC_ATOP);
						break;
					case MotionEvent.ACTION_UP:
						targetView.getBackground().setColorFilter(targetView.getContext().getColor(R.color.transparency), PorterDuff.Mode.SRC_ATOP);
						break;
					case MotionEvent.ACTION_CANCEL:
						targetView.getBackground().setColorFilter(targetView.getContext().getColor(R.color.transparency), PorterDuff.Mode.SRC_ATOP);
						break;
				}
				return false;
			}
		});
	}

}
