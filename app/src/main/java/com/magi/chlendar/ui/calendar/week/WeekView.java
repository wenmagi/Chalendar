package com.magi.chlendar.ui.calendar.week;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.view.MotionEvent;
import android.widget.FrameLayout;

import com.magi.chlendar.ui.calendar.unit.CalendarCellView;
import com.magi.chlendar.utils.date.CalendarHelper;
import com.magi.chlendar.utils.date.DateTime;

import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;

import static com.magi.chlendar.ui.calendar.Util.updateCellBg;
import static com.magi.chlendar.ui.calendar.Util.updateChooseState;
import static com.magi.chlendar.ui.calendar.Util.updateEventCount;
import static com.magi.chlendar.ui.calendar.Util.updatePaintColor;
import static com.magi.chlendar.ui.calendar.Util.updateTips;
import static com.magi.chlendar.utils.CalendarConfig.CELL_HEIGHT;
import static com.magi.chlendar.utils.CalendarConfig.CELL_WIDTH;
import static com.magi.chlendar.utils.CalendarConfig.FIRST_DAY_OF_WEEK;
import static com.magi.chlendar.utils.CalendarConfig.MAX_WEEK_SCROLL_COUNT;
import static com.magi.chlendar.utils.date.Util.cc_dateByMovingToBeginningOfDay;


public class WeekView extends FrameLayout {

	protected Timer mRefreshTipTimer = null;
	protected ArrayList<DateTime> mDatetimeList;
	protected Context mContext;
	protected DateTime mCurrentDateTime;
	protected DateTime mToday;
	private DateTime mSelectedTime;

	private CalendarCellView[][] mCells = null;
	private int mPosition = MAX_WEEK_SCROLL_COUNT / 2;

	public WeekView(Context context) {
		super(context);
	}

	public WeekView(Context context, int position) {
		super(context);
		mPosition = position;
		mContext = context;

		initData();
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		heightMeasureSpec = MeasureSpec.makeMeasureSpec(CELL_HEIGHT,
				MeasureSpec.EXACTLY);
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		clearTimer();
		WeekViewCache.getInstance().clearCache();
	}

	public void clearTimer() {
		if (mRefreshTipTimer != null) {
			mRefreshTipTimer.cancel();
			mRefreshTipTimer = null;
		}
	}

	@SuppressWarnings("unused")
	public ArrayList<DateTime> getDatetimeList() {
		return mDatetimeList;
	}

	public void initData() {

		DateTime firstDayOfTodayWeek = CalendarHelper.getFirstDateOfThisWeek(
				getToday(), FIRST_DAY_OF_WEEK);

		Date beginOf = cc_dateByMovingToBeginningOfDay(CalendarHelper
				.convertDateTimeToDate(firstDayOfTodayWeek));

		firstDayOfTodayWeek = CalendarHelper.convertDateToDateTime(beginOf);

		int offSet = mPosition - MAX_WEEK_SCROLL_COUNT / 2;

		mCurrentDateTime = firstDayOfTodayWeek.plusDays(7 * offSet);

		mDatetimeList = WeekViewCache.getInstance().getDateTimeList(
				mCurrentDateTime);

		if (mDatetimeList == null || mDatetimeList.size() < 7) {
			mDatetimeList = CalendarHelper.getThisWeek(mCurrentDateTime,
					FIRST_DAY_OF_WEEK);
			WeekViewCache.getInstance().putDateTimeList(mCurrentDateTime,
					mDatetimeList);
		}

		initCells();
		updateCells(CalendarHelper.getSelectedDay(), true);
	}

	public void initCells() {
		if (mCells == null) {

			mCells = new CalendarCellView[mDatetimeList.size() / 7][7];
			RectF bound = new RectF(0, 0, CELL_WIDTH, CELL_HEIGHT);

			for (int week = 0; week < mCells.length; week++) {
				for (int day = 0; day < mCells[week].length; day++) {
					mCells[week][day] = new CalendarCellView(getContext(), mDatetimeList.get(week
							* 7 + day), new RectF(bound),
							CalendarHelper.getContentPaintBlack(getContext()),
							CalendarHelper.getTipPaintGray(getContext()));
					bound.offset(CELL_WIDTH, 0);
				}
				bound.offset(0, CELL_HEIGHT);
				bound.left = 0;
				bound.right = CELL_WIDTH;
			}

		}

		for (int week = 0; week < mCells.length; week++) {
			for (int day = 0; day < mCells[week].length; day++) {

				CalendarCellView kdc = mCells[week][day];
				kdc.setDate(mDatetimeList.get(week * 7 + day));
				kdc.setContentPaint(CalendarHelper.getContentPaintBlack(getContext()));
				kdc.setTipPaint(CalendarHelper.getTipPaintGray(getContext()));

			}
		}
	}

	public void updateCells(DateTime selectedTime, boolean forceUpdate) {

		if (mCells == null
				|| (!forceUpdate && selectedTime.equals(mSelectedTime))) {
			return;
		}

		mSelectedTime = selectedTime;
		for (CalendarCellView[] mCell : mCells) {
			for (CalendarCellView aMCell : mCell) {

				DateTime dateTime = aMCell.getDate();

				updateTips(aMCell, dateTime);

				updatePaintColor(aMCell, dateTime, mCurrentDateTime, false);

				updateCellBg(aMCell, dateTime);

				updateChooseState(aMCell, dateTime, mCurrentDateTime, mSelectedTime, false);

				updateEventCount(aMCell, dateTime, mCurrentDateTime);
			}
		}
		invalidate();
	}

	protected DateTime getToday() {
		if (mToday == null) {
			mToday = CalendarHelper.convertDateToDateTime(new Date());
		}
		return mToday;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (mCells != null) {
			for (CalendarCellView[] week : mCells) {
				for (CalendarCellView day : week) {
					day.draw(canvas);
				}
			}
		}
	}

	private float lastX;
	private float lastY;

	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouchEvent(MotionEvent event) {
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

							if (day.hitCell((int) event.getX(), (int) event.getY())) {
								CalendarHelper.setSelectedDay(day.getDate());
								updateCells(day.getDate(), true);
							}
						}
					}

					return true;
				}
				break;
			default:
				break;
		}
		return isHandled;
	}

	@SuppressWarnings("unused")
	public int getPosition() {
		return mPosition;
	}

	@SuppressWarnings("unused")
	public void setPosition(int position) {
		mPosition = position;
	}
}