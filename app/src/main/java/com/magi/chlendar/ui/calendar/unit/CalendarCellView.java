package com.magi.chlendar.ui.calendar.unit;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;

import com.magi.chlendar.utils.date.DateTime;

import static com.magi.chlendar.ui.calendar.Util.dp2pix;
import static com.magi.chlendar.ui.calendar.Util.getDensity;
import static com.magi.chlendar.ui.calendar.Util.getTextHeight;
import static com.magi.chlendar.utils.date.Util.isNotEmpty;


/**
 * @author zhangzhaowen @ Zhihu Inc.
 * @since 01-04-2017
 */
public class CalendarCellView {

	private RectF mBound = null;
	private DateTime mDate = null;
	private Context mContext = null;

	private boolean isCurrentMonth = false;
	private boolean isSelected = false;
	private String strTip = "";
	private boolean isDuty = false;
	private boolean isRest = false;
	private boolean isToday = false;
	private boolean isLunarHoliday = false;
	private boolean isSolarHoliday = false;

	private int mEventCount = -1;

	private Paint mContentPaint = null;
	private Paint mTipPaint = null;
	private Paint mSelectedCirclePaint = null;
	private Paint mTodayCirclePaint = null;
	private Paint mHasEventsCirclePaint = null;

	private Bitmap mBitmapRest = null;
	private Bitmap mBitmapHoliday = null;
	//当月日历只有5行时，需要放大元素
	private boolean needLargeForFiveLine = false;

	public CalendarCellView(Context context, DateTime date, RectF rect, Paint contentPaint,
	                        Paint tipPaint) {
		mBound = rect;
		mContext = context;
		setContentPaint(contentPaint);
		setTipPaint(tipPaint);
		setDate(date);
	}

	public void draw(Canvas canvas) {

		drawCellBg(canvas);

		float dayBottom = drawSolarNum(canvas);

		drawBottomDesc(canvas, dayBottom);

		drawEventCircle(canvas, dayBottom);

		try {
			//假期图标的绘制
			if (isDuty && mBitmapHoliday != null && !mBitmapHoliday.isRecycled()) {

				Rect sourceRect = new Rect(0, 0, mBitmapHoliday.getWidth(),
						mBitmapHoliday.getHeight());
				RectF dstRect = new RectF(mBound.right - mBitmapHoliday.getWidth()
						, mBound.top, mBound.right, mBound.top + mBitmapHoliday.getHeight());

				if (mBitmapHoliday != null && !mBitmapHoliday.isRecycled())
					canvas.drawBitmap(mBitmapHoliday, sourceRect, dstRect, null);

			}

			//休息日图标的绘制
			if (isRest && mBitmapRest != null && !mBitmapRest.isRecycled()) {

				Rect sourceRect = new Rect(0, 0, mBitmapRest.getWidth(),
						mBitmapRest.getHeight());
				RectF dstRect = new RectF(
						mBound.right - mBitmapRest.getWidth(), mBound.top,
						mBound.right, mBound.top + mBitmapRest.getHeight());
				if (mBitmapRest != null && !mBitmapRest.isRecycled())
					canvas.drawBitmap(mBitmapRest, sourceRect, dstRect, null);

			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * 绘制每个Cell的背景，当前定位圆圈
	 */

	private void drawCellBg(Canvas canvas) {

		//没选中的今天cell背景
		float centerX = mBound.centerX();
		float padding = dp2pix(mContext, 1);
		float edge = mBound.width() < mBound.height() ? mBound.width() : mBound.height();
		float radius = edge / 2 - padding;

		//已选中的cell背景
		if (isSelected) {

			canvas.drawCircle(centerX, mBound.centerY(),
					radius,
					mSelectedCirclePaint);

		} else if (isToday && isCurrentMonth) {

			canvas.drawCircle(centerX, mBound.centerY(),
					radius,
					mTodayCirclePaint);

		}
	}


	/**
	 * 绘制当天事件存在时的小圆点
	 *
	 * @param canvas    画布
	 * @param dayBottom 文字底部的y坐标
	 */
	private void drawEventCircle(Canvas canvas, float dayBottom) {
		if (!isCurrentMonth || mEventCount <= 0)
			return;

		int eventRadius = dp2pix(mContext, 2);
		int marginY = dp2pix(mContext, 2);
		if (getDensity(mContext) >= 1.5 && getDensity(mContext) < 2.0)// hdpi
			marginY = 0;
		canvas.drawCircle(mBound.centerX(), dayBottom
						- getTextHeight(mContentPaint, "0") - eventRadius - marginY, eventRadius,
				mHasEventsCirclePaint);
	}

	/**
	 * 绘制阳历日期底部的文字，没有节日或事件，默认阴历
	 *
	 * @param canvas    画布
	 * @param dayBottom 文字底部的y坐标
	 */
	private void drawBottomDesc(Canvas canvas, float dayBottom) {

		//绘制节日
		String strTip = getStrTip();
		if (isNotEmpty(strTip) && strTip.length() > 3) {
			strTip = strTip.substring(0, 3);
		}

		float tipLen = mTipPaint.measureText(strTip, 0, strTip.length());
		float tipLeft = mBound.left + (mBound.width() - tipLen) / 2.0f;
		float tipBottom = dayBottom + getTextHeight(mTipPaint, strTip);
		canvas.drawText(strTip,
				tipLeft,
				tipBottom, mTipPaint);
	}

	/**
	 * 绘制阳历数字
	 *
	 * @param canvas 画布
	 * @return 文字底部的y坐标
	 */
	private float drawSolarNum(Canvas canvas) {
		String strDay = String.valueOf(mDate.getDay());
		//绘制今天日期
		RectF bounds = new RectF(mBound);
		bounds.right = mContentPaint.measureText(strDay, 0, strDay.length());
		bounds.bottom = mContentPaint.descent() - mContentPaint.ascent();
		bounds.left += (mBound.width() - bounds.right) / 2.0f;
		bounds.top += (mBound.height() - bounds.bottom) / 2.0f;

		float dayBottom;
		if (getDensity(mContext) >= 1.5 && getDensity(mContext) < 2.0) { // hdpi

			dayBottom = bounds.top - mContentPaint.ascent()
					+ dp2pix(mContext, -10);

		} else {

			dayBottom = bounds.top - mContentPaint.ascent()
					+ dp2pix(mContext, -15);
		}

		canvas.drawText(strDay,
				bounds.left,
				dayBottom, mContentPaint);

		return dayBottom;
	}

	public boolean hitCell(int x, int y) {
		return mBound.contains(x, y);
	}

	public float[] getCenter(){
		float[] xy = new float[2];
		xy[0] = mBound.centerX();
		xy[1] = mBound.centerY();
		return xy;
	}

	@SuppressWarnings("unused")
	public boolean isCurrentMonth() {
		return isCurrentMonth;
	}

	public void setActiveMonth(boolean isCurrentMonth) {
		this.isCurrentMonth = isCurrentMonth;
	}

	public boolean isSelected() {
		return isSelected;
	}

	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}

	public String getStrTip() {
		return strTip;
	}

	public void setStrTip(String strTip) {
		this.strTip = strTip;
	}

	@SuppressWarnings("unused")
	public boolean isToday() {
		return isToday;
	}

	public void setToday(boolean isToday) {
		this.isToday = isToday;
	}

	public DateTime getDate() {
		return mDate;
	}

	public void setDate(DateTime mDate) {
		this.mDate = mDate;
	}

	@SuppressWarnings("unused")
	public boolean isDuty() {
		return isDuty;
	}

	public void setDuty(boolean isDuty) {
		this.isDuty = isDuty;
	}

	@SuppressWarnings("unused")
	public boolean isRest() {
		return isRest;
	}

	@SuppressWarnings("unused")
	public void setRest(boolean isRest) {
		this.isRest = isRest;
	}

	@SuppressWarnings("unused")
	public Paint getTipPaint() {
		return mTipPaint;
	}

	public void setTipPaint(Paint mTipPaint) {
		this.mTipPaint = mTipPaint;
	}

	@SuppressWarnings("unused")
	public Paint getContentPaint() {
		return mContentPaint;
	}

	public void setContentPaint(Paint mContentPaint) {
		this.mContentPaint = mContentPaint;
	}

	@SuppressWarnings("unused")
	public Paint getSelectedCirclePaint() {
		return mSelectedCirclePaint;
	}

	public void setSelectedCirclePaint(Paint mSelectedCirclePaint) {
		this.mSelectedCirclePaint = mSelectedCirclePaint;
	}

	@SuppressWarnings("unused")
	public Paint getTodayCirclePaint() {
		return mTodayCirclePaint;
	}

	public void setTodayCirclePaint(Paint mTodayCirclePaint) {
		this.mTodayCirclePaint = mTodayCirclePaint;
	}

	@SuppressWarnings("unused")
	public int getEventCount() {
		return mEventCount;
	}

	public void setEventCount(int mEventCount) {
		this.mEventCount = mEventCount;
	}

	@SuppressWarnings("unused")
	public Paint getHasEventsCirclePaint() {
		return mHasEventsCirclePaint;
	}

	public void setHasEventsCirclePaint(Paint mHasEventsCirclePaint) {
		this.mHasEventsCirclePaint = mHasEventsCirclePaint;
	}

	@SuppressWarnings("unused")
	public Bitmap getBitmapRest() {
		return mBitmapRest;
	}

	public void setBitmapRest(Bitmap mBitmapRest) {
		this.mBitmapRest = mBitmapRest;
	}

	@SuppressWarnings("unused")
	public Bitmap getBitmapHoliday() {
		return mBitmapHoliday;
	}

	@SuppressWarnings("unused")
	public void setBitmapHoliday(Bitmap mBitmapHoliday) {
		this.mBitmapHoliday = mBitmapHoliday;
	}

	public void setLunarHoliday(boolean isLunarHoliday) {
		this.isLunarHoliday = isLunarHoliday;
	}

	public void setSolarHoliday(boolean isSolarHoliday) {
		this.isSolarHoliday = isSolarHoliday;
	}

	public boolean isLunarHoliday() {
		return isLunarHoliday;
	}

	public boolean isSolarHoliday() {
		return isSolarHoliday;
	}

	public Context getContext() {
		return mContext;
	}

	public void setNeedLargeForFiveLine(boolean needLargeForFiveLine) {
		this.needLargeForFiveLine = needLargeForFiveLine;
	}
}
