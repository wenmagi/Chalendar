package com.magi.chlendar.ui.calendar.month;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.view.MotionEvent;
import android.widget.FrameLayout;

import com.magi.chlendar.ui.calendar.unit.CalendarCellView;
import com.magi.chlendar.utils.date.CalendarHelper;
import com.magi.chlendar.utils.date.DateTime;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static com.magi.chlendar.ui.calendar.Util.updateCellBg;
import static com.magi.chlendar.ui.calendar.Util.updateChooseState;
import static com.magi.chlendar.ui.calendar.Util.updateEventCount;
import static com.magi.chlendar.ui.calendar.Util.updatePaintColor;
import static com.magi.chlendar.ui.calendar.Util.updateTips;
import static com.magi.chlendar.utils.CalendarConfig.CELL_HEIGHT;
import static com.magi.chlendar.utils.CalendarConfig.CELL_WIDTH;
import static com.magi.chlendar.utils.CalendarConfig.FIRST_DAY_OF_WEEK;
import static com.magi.chlendar.utils.CalendarConfig.MAX_MONTH_SCROLL_COUNT;
import static com.magi.chlendar.utils.date.Util.cc_dateByMovingToBeginningOfDay;

/**
 * @author zhangzhaowen @ Zhihu Inc.
 * @since 01-04-2017
 */
public class MonthView extends FrameLayout {

	protected ArrayList<DateTime> mDatetimeList;
	protected Context mContext;
	//选中的日期
	protected DateTime mCurrentDateTime;
	//今天
	protected DateTime mToday;
	private DateTime mSelectedTime;

	private CalendarCellView[][] mCells = null;
	private int mPosition = MAX_MONTH_SCROLL_COUNT / 2;
	//判断日历是否分6行
	private boolean hasSixLine = false;

	private int mSelectDateLine = 1;

	public MonthView(Context context) {
		super(context);
	}

	public MonthView(Context context, int position) {
		super(context);
		mPosition = position;
		mContext = context;

		initData();
	}

	public void setBackground(Drawable d) {
		super.setBackground(d);
	}

	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		MonthViewCache.getInstance().clearCache();
	}

	@SuppressWarnings("unused")
	public ArrayList<DateTime> getDatetimeList() {
		return mDatetimeList;
	}

	private void initData() {
		Calendar firstDayOfTodayMonth = Calendar.getInstance();
		firstDayOfTodayMonth.set(Calendar.DAY_OF_MONTH, 1);

		Date beginOf = cc_dateByMovingToBeginningOfDay(firstDayOfTodayMonth.getTime());

		initDateTimeList(beginOf);

		if (mDatetimeList.get(5 * 7).getMonth().equals(mCurrentDateTime.getMonth())) {
			hasSixLine = true;
		}

		initCells();
		updateCells(CalendarHelper.getSelectedDay(), true);
	}

	/**
	 * 获取该日期整个月的 DateTime 列表
	 */
	private void initDateTimeList(Date beginOf) {
		int offSet = mPosition - MAX_MONTH_SCROLL_COUNT / 2;

		if (offSet > 0)
			mCurrentDateTime = CalendarHelper.convertDateToDateTime(
					beginOf).plus(0, offSet, 0, 0, 0, 0, 0,
					DateTime.DayOverflow.LastDay);
		else if (offSet < 0)
			mCurrentDateTime = CalendarHelper.convertDateToDateTime(
					beginOf).minus(0, -offSet, 0, 0, 0, 0, 0,
					DateTime.DayOverflow.LastDay);
		else
			mCurrentDateTime = CalendarHelper
					.convertDateToDateTime(beginOf);

		mDatetimeList = MonthViewCache.getInstance().getDateTimeList(
				mCurrentDateTime);

		if (mDatetimeList == null || mDatetimeList.size() < 28) {

			mDatetimeList = CalendarHelper.getFullWeeks(
					mCurrentDateTime.getMonth(), mCurrentDateTime.getYear(),
					FIRST_DAY_OF_WEEK, true);
			MonthViewCache.getInstance().putDateTimeList(mCurrentDateTime,
					mDatetimeList);

		}
	}

	public void initCells() {
		if (mCells != null) {
			for (CalendarCellView[] week : mCells) {
				for (CalendarCellView kdc : week) {
					kdc.setBitmapRest(null);
					kdc.setBitmapHoliday(null);
					kdc.setContentPaint(null);
					kdc.setHasEventsCirclePaint(null);
					kdc.setSelectedCirclePaint(null);
					kdc.setTipPaint(null);
					kdc.setTodayCirclePaint(null);
				}
			}
			mCells = null;
		}

		mCells = new CalendarCellView[mDatetimeList.size() / 7][7];

		RectF bound = new RectF(0, 0, CELL_WIDTH, hasSixLine ? CELL_HEIGHT : CELL_HEIGHT * 6 / 5);

		for (int week = 0; week < mCells.length; week++) {
			for (int day = 0; day < mCells[week].length; day++) {

				if (!hasSixLine && week == 5)
					break;

				mCells[week][day] = new CalendarCellView(getContext(), mDatetimeList.get(week * 7
						+ day), new RectF(bound),
						CalendarHelper.getContentPaintBlack(getContext()),
						CalendarHelper.getTipPaintLunarDate(getContext()));
				mCells[week][day].setNeedLargeForFiveLine(!hasSixLine);

				bound.offset(CELL_WIDTH, 0);

			}

			bound.offset(0, hasSixLine ? CELL_HEIGHT : CELL_HEIGHT * 6 / 5);
			bound.left = 0;
			bound.right = CELL_WIDTH;
		}
	}

	public void updateCells(DateTime selectedTime, boolean forceUpdate) {
		if (mCells == null
				|| (!forceUpdate && selectedTime.equals(mSelectedTime))) {
			return;
		}
		mSelectedTime = selectedTime;
		int line = 0;
		for (CalendarCellView[] mCell : mCells) {

			line++;

			for (CalendarCellView aMCell : mCell) {

				if (aMCell == null)
					continue;

				DateTime dateTime = aMCell.getDate();

				if ((mCurrentDateTime.getMonth().equals(selectedTime.getMonth()) && dateTime.equals(selectedTime))
						|| (!mCurrentDateTime.getMonth().equals(selectedTime.getMonth()) && dateTime.equals(mCurrentDateTime))) {
					mSelectDateLine = line;
				}

				updateTips(aMCell, dateTime);

				updatePaintColor(aMCell, dateTime, mCurrentDateTime, true);

				updateCellBg(aMCell, dateTime);

				updateChooseState(aMCell, dateTime, mCurrentDateTime, mSelectedTime, true);

				updateEventCount(aMCell, dateTime, mCurrentDateTime);
			}
		}
		invalidate();
	}


	@SuppressWarnings("unused")
	public void updateToday() {
		mToday = CalendarHelper.convertDateToDateTime(new Date());
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (mCells != null) {
			for (CalendarCellView[] week : mCells) {
				for (CalendarCellView day : week) {

					if (day == null)
						continue;

					day.draw(canvas);
				}
			}
		}
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		heightMeasureSpec = MeasureSpec.makeMeasureSpec(CELL_HEIGHT * 6,
				MeasureSpec.EXACTLY);

		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	private float lastX;
	private float lastY;

	@Override
	public boolean onTouchEvent(MotionEvent event) {

		//从日期中心触发 Ripple
		super.onTouchEvent(event);

		boolean isHandled = false;
		switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				lastX = event.getX();
				lastY = event.getY();
				isHandled = true;
				break;
			case MotionEvent.ACTION_CANCEL:
				isHandled = false;
				break;
			case MotionEvent.ACTION_MOVE:
				isHandled = false;
				break;
			case MotionEvent.ACTION_UP:

				if (Math.abs(event.getX() - lastX) < 100 && Math.abs(event.getY() - lastY) < 100) {
					for (CalendarCellView[] week : mCells) {
						for (CalendarCellView day : week) {

							if (day == null || !day.hitCell((int) event.getX(), (int) event.getY()))
								continue;

							CalendarHelper.setSelectedDay(day.getDate());

							if (day.getDate().getMonth().equals(CalendarHelper.getSelectedDay().getMonth())) {
								updateCells(day.getDate(), true);
							}
						}
					}

					isHandled = true;
				}
				break;
			default:
				break;
		}


		return isHandled;
	}

	public boolean isHasSixLine() {
		return hasSixLine;
	}

	public int getSelectDateLine() {
		return mSelectDateLine;
	}
}
