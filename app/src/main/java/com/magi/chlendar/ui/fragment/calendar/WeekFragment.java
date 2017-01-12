package com.magi.chlendar.ui.fragment.calendar;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.magi.chlendar.models.event.DayChangeEvent;
import com.magi.chlendar.ui.adapter.WeekPagerAdapter;
import com.magi.chlendar.ui.calendar.week.WeekView;
import com.magi.chlendar.ui.calendar.week.WeekViewPager;
import com.magi.chlendar.ui.fragment.BaseFragment;
import com.magi.chlendar.utils.EventBusFactory;
import com.magi.chlendar.utils.date.CalendarHelper;
import com.magi.chlendar.utils.date.DateTime;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Date;

import static com.magi.chlendar.ui.calendar.Util.getToday;
import static com.magi.chlendar.utils.CalendarConfig.FIRST_DAY_OF_WEEK;
import static com.magi.chlendar.utils.CalendarConfig.MAX_WEEK_SCROLL_COUNT;


public class WeekFragment extends BaseFragment implements ViewPager.OnPageChangeListener {
	private volatile boolean bMoveToDate = false;
	private volatile boolean isPageChanged = false;

	private WeekViewPager weekPager;
	private DateTime mToday;
	//选中的日期
	private DateTime mCurrentDay;

	private int mCurrentItem = MAX_WEEK_SCROLL_COUNT / 2;

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
		weekPager = new WeekViewPager(context);

		mToday = getToday();

		if (mCurrentDay == null)
			mCurrentDay = mToday;

		initUI();

		return weekPager;
	}

	private void initUI() {
		WeekPagerAdapter mMonthAdapter = new WeekPagerAdapter(context);
		weekPager.setAdapter(mMonthAdapter);
		weekPager.addOnPageChangeListener(this);
	}

	@Override
	public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
		bMoveToDate = false;
	}

	@Override
	public void onPageSelected(int position) {
		if (mCurrentItem != position) {
			isPageChanged = true;
			mCurrentItem = position;
		}
	}

	@Override
	public void onPageScrollStateChanged(int state) {
		switch (state) {
			case ViewPager.SCROLL_STATE_IDLE:
				if (isPageChanged) {
					if (!bMoveToDate) {

						DateTime firstDayOfTodayWeek = CalendarHelper.getFirstDateOfThisWeek(
								getToday(), FIRST_DAY_OF_WEEK);

						int offSet = weekPager.getCurrentItem() - MAX_WEEK_SCROLL_COUNT / 2;

						mCurrentDay = offSet == 0 ? getToday() : firstDayOfTodayWeek.plusDays(7 * offSet);

						CalendarHelper.setSelectedDay(mCurrentDay);
						updateWeekCellsForDaysChange(CalendarHelper.convertDateTimeToDate(mCurrentDay));
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

	private void updateWeekCellsForDaysChange(Date selectDay) {
		mCurrentDay = CalendarHelper.convertDateToDateTime(selectDay);
		WeekView view = (WeekView) weekPager.findViewWithTag(weekPager.getCurrentItem());
		view.updateCells(CalendarHelper.getSelectedDay(), true);
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

	/**
	 * invoke this method when DayChangeEvent posted
	 */
	@SuppressWarnings("unused")
	@Subscribe(threadMode = ThreadMode.MAIN)
	public void onDayChangeEventMainThread(DayChangeEvent event) {
		if (mCurrentDay.equals(CalendarHelper.convertDateToDateTime(event.currentDay)))
			return;

		if (CalendarHelper.convertDateToDateTime(event.currentDay).equals(getToday()) && weekPager != null && weekPager.getCurrentItem() != MAX_WEEK_SCROLL_COUNT / 2)
			weekPager.setCurrentItem(MAX_WEEK_SCROLL_COUNT / 2);

		mCurrentDay = CalendarHelper.convertDateToDateTime(event.currentDay);
		updateWeekCellsForDaysChange(event.currentDay);
	}
}
