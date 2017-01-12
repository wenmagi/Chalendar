package com.magi.chlendar.ui.fragment.calendar;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.magi.chlendar.models.event.DayChangeEvent;
import com.magi.chlendar.ui.adapter.MonthPagerAdapter;
import com.magi.chlendar.ui.calendar.month.MonthView;
import com.magi.chlendar.ui.calendar.month.MonthViewPager;
import com.magi.chlendar.ui.fragment.BaseFragment;
import com.magi.chlendar.utils.date.CalendarHelper;
import com.magi.chlendar.utils.date.DateTime;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Calendar;
import java.util.Date;

import static com.magi.chlendar.ui.calendar.Util.getToday;
import static com.magi.chlendar.utils.CalendarConfig.MAX_MONTH_SCROLL_COUNT;
import static com.magi.chlendar.utils.date.Util.cc_dateByMovingToBeginningOfDay;

/**
 * @author zhangzhaowen @ Zhihu Inc.
 * @since 01-04-2017
 */
public class MonthFragment extends BaseFragment implements ViewPager.OnPageChangeListener {

	private volatile boolean bMoveToDate = false;
	private volatile boolean isPageChanged = false;

	private MonthViewPager monthPager;
	private DateTime mToday;

	//选中的日期
	private DateTime mCurrentDay;

	//在本月点击其他月份的记录，如果不为空，onPageSelected 时不需要重新计算默认日期
	private DateTime mOtherMonthDay;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (savedInstanceState != null)
			mCurrentDay = (DateTime) savedInstanceState.getSerializable("current_day");
		EventBus.getDefault().register(this);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putSerializable("current_day", mCurrentDay);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		monthPager = new MonthViewPager(context);

		mToday = getToday();

		if (mCurrentDay == null)
			mCurrentDay = mToday;

		initUI();

		return monthPager;
	}

	private void initUI() {
		MonthPagerAdapter mMonthAdapter = new MonthPagerAdapter(context);
		monthPager.setAdapter(mMonthAdapter);
		monthPager.addOnPageChangeListener(this);
	}

	public MonthView getCurrentMonthView() {
		return ((MonthPagerAdapter) monthPager.getAdapter()).getPrimaryItem(monthPager.getCurrentItem());
	}

	@Override
	public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
		bMoveToDate = false;
	}

	@Override
	public void onPageSelected(int position) {
		isPageChanged = true;
	}

	@Override
	public void onPageScrollStateChanged(int state) {
		switch (state) {
			case ViewPager.SCROLL_STATE_IDLE:
				if (isPageChanged) {
					if (!bMoveToDate) {

						// 如果手动点击他月 Cell 滑动时不需要更新 SelectDay
						if (mOtherMonthDay != null) {
							mOtherMonthDay = null;
							return;
						}

						Calendar firstDayOfTodayMonth = Calendar
								.getInstance();
						firstDayOfTodayMonth.set(Calendar.DAY_OF_MONTH,
								1);
						Date beginOf = cc_dateByMovingToBeginningOfDay(firstDayOfTodayMonth
								.getTime());

						int offSet = monthPager
								.getCurrentItem()
								- MAX_MONTH_SCROLL_COUNT / 2;

						if (offSet > 0)
							mCurrentDay = CalendarHelper
									.convertDateToDateTime(beginOf)
									.plus(0, offSet, 0, 0, 0, 0, 0, DateTime.DayOverflow.LastDay);


						else if (offSet < 0)
							mCurrentDay = CalendarHelper
									.convertDateToDateTime(beginOf)
									.minus(0, -offSet, 0, 0, 0, 0, 0, DateTime.DayOverflow.LastDay);

						else
							mCurrentDay = mToday;

						CalendarHelper.setSelectedDay(mCurrentDay);
						updateMonthCellsForDaysChange();
					} else {
						bMoveToDate = false;
					}
				}
				break;
			case ViewPager.SCROLL_STATE_DRAGGING:
				break;
			case ViewPager.SCROLL_STATE_SETTLING:
				break;
		}
	}

	private void updateMonthCellsForDaysChange() {
		MonthView view = (MonthView) monthPager.findViewWithTag(monthPager.getCurrentItem());
		view.updateCells(CalendarHelper.getSelectedDay(), true);
	}

	/**
	 * 当手动点击的日期，在 {@link MonthView} 中不属于当月
	 * <p>
	 * 需要切换 ViewPager 到点击的日期月份
	 * <p>
	 * 点击当月日期时，月视图 UI 自动更新
	 * 此方法仅处理，点击非本月 Cell 的情况
	 */
	private void changeCurrentDateAndMonth() {
		if (mOtherMonthDay == null)
			return;

		Date date = CalendarHelper.convertDateTimeToDate(mOtherMonthDay);
		Date curr = CalendarHelper.convertDateTimeToDate(mCurrentDay);
		int item = monthPager.getCurrentItem();

		if (date.after(curr)) {
			monthPager.setCurrentItem(item + 1);
		} else {
			monthPager.setCurrentItem(item - 1);
		}

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		EventBus.getDefault().unregister(this);
		CalendarHelper.setSelectedDay(mToday);
	}

	@Override
	protected void OnClickView(View v) {

	}

	public DateTime getMonthCurrentDay() {
		return mCurrentDay;
	}

	/**
	 * 当日期改变时，触发此方法
	 */
	@SuppressWarnings("unused")
	@Subscribe(threadMode = ThreadMode.MAIN)
	public void onDayChangeEventMainThread(DayChangeEvent event) {
		DateTime day = CalendarHelper.convertDateToDateTime(event.currentDay);
		if (mCurrentDay.equals(day))
			return;

		if (day.equals(getToday()) && monthPager != null && monthPager.getCurrentItem() != MAX_MONTH_SCROLL_COUNT / 2)
			monthPager.setCurrentItem(MAX_MONTH_SCROLL_COUNT / 2);

		if (!day.getMonth().equals(mCurrentDay.getMonth())) {
			mOtherMonthDay = day;
			changeCurrentDateAndMonth();
		}

		mCurrentDay = day;
		updateMonthCellsForDaysChange();
	}


}
