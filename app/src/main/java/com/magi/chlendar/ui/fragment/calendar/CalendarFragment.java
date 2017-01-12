package com.magi.chlendar.ui.fragment.calendar;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.magi.chlendar.R;
import com.magi.chlendar.databinding.FragmentCalendarBinding;
import com.magi.chlendar.ui.adapter.WeekdayArrayAdapter;
import com.magi.chlendar.ui.fragment.BaseLazyLoadFragment;
import com.magi.chlendar.utils.CalendarConfig;
import com.magi.chlendar.utils.EventBusFactory;
import com.magi.chlendar.utils.LogUtils;
import com.magi.chlendar.utils.date.DayStyles;
import com.nineoldandroids.view.ViewHelper;

import java.util.ArrayList;
import java.util.Date;

import static com.magi.chlendar.utils.CalendarConfig.CELL_HEIGHT;
import static com.magi.chlendar.utils.CalendarConfig.FIRST_DAY_OF_WEEK;
import static com.magi.chlendar.utils.CalendarConfig.ROWS_OF_MONTH_CALENDAR;

/**
 * 1  2  3  4  5  6  7   不滑动                translateY: scrollY;
 * <p>
 * 8  9  10 11 12 13 14  滑动CELL_HEIGHT      translateY: scrollY * 3 / 4;
 * <p>
 * 15 16 17 18 19 20 21  滑动CELL_HEIGHT * 2  translateY: scrollY / 2;
 * <p>
 * 22 23 24 25 26 27 28  滑动CELL_HEIGHT * 3  translateY: scrollY * 1 / 4;
 * <p>
 * 29 30 31              滑动CELL_HEIGHT * 4  translateY: scrollY: 0;
 * <p>
 * 以上为 Calendar 占 5 行的情况.
 * <p>
 * 当 Calendar 占 6 行时：
 * <p>
 * 1 translateY: scrollY;
 * <p>
 * 2 translateY: scrollY * 4 / 5;
 * <p>
 * 3 translateY: scrollY * 3 / 5;
 * <p>
 * 4 translateY: scrollY *2 / 5;
 * <p>
 * 5 translateY: scrollY * 1 / 5;
 * <p>
 * 6 translateY: scrollY * 0;
 *
 * @author zhangzhaowen @ Zhihu Inc.
 * @since 01-04-2017
 */
public class CalendarFragment extends BaseLazyLoadFragment implements ObservableScrollViewCallbacks {

	FragmentCalendarBinding mBinding;

	private Date mSelectedDate;
	// MonthView 的高度
	private int mParallaxMonthHeight;
	// WeekView 的高度
	private int mParallaxWeekHeight;
	// WeekHeader 的高度
	private int mParallaxWeekHeaderHeight;

	private int mParallaxContentViewHeight;

	private MonthFragment mMonthPagerFragment;

	private boolean mUpOrCancelTriggered = false;

	@Override
	protected View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_calendar, container, false);
		return mBinding.getRoot();
	}

	@Override
	public void onAttach(Context context) {
		super.onAttach(context);
		mSelectedDate = new Date();
	}

	@Override
	protected void lazyLoad() {
		mBinding.scrollView.setScrollViewCallbacks(this);
		// 月视图，周视图的位置都需要根据周名称de布局来确定
		// 所以先进行周名称头部的创建
		generateWeekHeaderView();
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
	}

	/**
	 * month View
	 */
	private void generateMonthPagerView() {
		FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) mBinding.monthCalendar.getLayoutParams();
		mParallaxMonthHeight = params.height = ROWS_OF_MONTH_CALENDAR * CELL_HEIGHT;
		params.topMargin = mParallaxWeekHeaderHeight;
		mMonthPagerFragment = new MonthFragment();

		FragmentTransaction t = ((FragmentActivity) context).getSupportFragmentManager().beginTransaction();
		t.replace(R.id.month_calendar, mMonthPagerFragment);
		t.commit();
	}

	/**
	 * Week view 5 6 7 8 9 10 11
	 */
	private void generateWeekPagerView() {
		//初始化周视图高度为CELL_HEIGHT
		resetWeekPagerLayoutParams(CELL_HEIGHT);

		mBinding.layoutWeekCalendar.bringToFront();

		WeekFragment mWeekPagerFragment = new WeekFragment();

		FragmentTransaction t = ((FragmentActivity) context).getSupportFragmentManager().beginTransaction();
		t.add(R.id.week_calendar, mWeekPagerFragment);
		t.commit();

	}

	private void resetWeekPagerLayoutParams(int height) {
		FrameLayout.LayoutParams weekParams = (FrameLayout.LayoutParams) mBinding.layoutWeekCalendar.getLayoutParams();
		mParallaxWeekHeight = weekParams.height = height;
		weekParams.topMargin = mParallaxWeekHeaderHeight;
		mBinding.layoutWeekCalendar.setLayoutParams(weekParams);
	}

	/**
	 * week header 一 二 三 四 五 六 日
	 */
	private void generateWeekHeaderView() {

		ArrayList<String> weekDayNameLists = new ArrayList<>();
		for (int iDay = 0; iDay < CalendarConfig.MONTH_CALENDAR_COLUMN; iDay++) {
			String WeekDay = DayStyles.getShortWeekDayName(DayStyles
					.getWeekDay(iDay, FIRST_DAY_OF_WEEK));
			weekDayNameLists.add(WeekDay);
		}

		WeekdayArrayAdapter weekdaysAdapter = new WeekdayArrayAdapter(context,
				R.layout.adapter_week_name_list_item, weekDayNameLists);
		mBinding.weekdayGrid.setAdapter(weekdaysAdapter);

		mBinding.weekdayGrid.post(new Runnable() {
			@Override
			public void run() {
				mParallaxWeekHeaderHeight = mBinding.weekdayGrid.getHeight();

				generateWeekPagerView();
				generateMonthPagerView();
				generateContentView();
				EventBusFactory.postDayChangeEvent(mSelectedDate);
			}
		});
	}

	/**
	 * 涟漪效果
	 */
	private void generateContentView() {
		FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
		params.topMargin = mParallaxMonthHeight + mParallaxWeekHeaderHeight;
		mBinding.contentMain.setLayoutParams(params);
		mBinding.contentMain.setOnClickListener(this);

		mBinding.contentMain.post(new Runnable() {
			@Override
			public void run() {
				mParallaxContentViewHeight = mBinding.contentMain.getHeight();
				FrameLayout.LayoutParams lps = (FrameLayout.LayoutParams) mBinding.contentMain.getLayoutParams();
				lps.height = mParallaxContentViewHeight + mParallaxMonthHeight - mParallaxWeekHeight;
				mBinding.contentMain.setLayoutParams(lps);
			}
		});
	}

	@Override
	protected void OnClickView(View v) {

	}

	private boolean mIsShowingWeekPager = false;

	/**
	 * 获取周视图的高度
	 * <p>
	 * 需要与月视图高度进行统一
	 */
	private int getWeekHeight() {
		if (mMonthPagerFragment.getCurrentMonthView().isHasSixLine()) {
			return CELL_HEIGHT;
		} else {
			return CELL_HEIGHT * 6 / 5;
		}
	}

	private float getMonthPagerTranslationFactor() {

		boolean hasSixLine = mMonthPagerFragment.getCurrentMonthView().isHasSixLine();

		switch (mMonthPagerFragment.getCurrentMonthView().getSelectDateLine()) {
			case 1:
				return 1;
			case 2:
				return hasSixLine ? 4 / 5f : 3 / 4f;
			case 3:
				return hasSixLine ? 3 / 5f : 1 / 2f;
			case 4:
				return hasSixLine ? 2 / 5f : 1 / 4f;
			case 5:
				return hasSixLine ? 1 / 5f : 0;
			case 6:
				return 0;

			default:
				return 1 / 2;
		}
	}

	private float factor;
	private int weekHeight;

	@Override
	public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {

		ViewHelper.setTranslationY(mBinding.weekdayGrid, scrollY);
		ViewHelper.setTranslationY(mBinding.layoutWeekCalendar, scrollY);

		if (scrollY >= mParallaxMonthHeight - weekHeight) {

			ViewHelper.setTranslationY(mBinding.layoutMonthCalendar, scrollY);

			if (!mIsShowingWeekPager) {
				resetWeekPagerLayoutParams(weekHeight);
				mBinding.layoutMonthCalendar.setVisibility(View.GONE);
				mBinding.layoutWeekCalendar.setVisibility(View.VISIBLE);
				mIsShowingWeekPager = true;
			}

		} else {
			stickyWeekPager(scrollY, dragging);

			ViewHelper.setTranslationY(mBinding.layoutMonthCalendar, scrollY * factor);

		}
	}

	/**
	 * 如果滑动距离大于总移动距离的 2／5
	 * 自动上滑
	 * 否则，返回原位置
	 *
	 * @param inertiaScrolling 是否正在惯性滑动中
	 */
	@SuppressWarnings("unused")
	private void nestedMonthPager(boolean inertiaScrolling) {
		if (mUpOrCancelTriggered && (mIsShowingWeekPager || inertiaScrolling)) {
			mUpOrCancelTriggered = false;
			return;
		}

		mUpOrCancelTriggered = false;
		float translationY = mBinding.layoutMonthCalendar.getTranslationY();
		int deltaY = (int) (mParallaxMonthHeight - weekHeight - translationY);

		if (translationY > (mParallaxMonthHeight - weekHeight) * 2 / 5) {
			mBinding.scrollView.smoothScrollBy(0, deltaY);
		} else {
			mBinding.scrollView.smoothScrollBy(0, -deltaY);
		}
	}

	/**
	 * 当 WeekViewPager 处于显示状态，
	 * <p>
	 * 对周视图的下滑进行粘性操作
	 */
	private void stickyWeekPager(int scrollY, boolean dragging) {
		if (mBinding.layoutWeekCalendar.getVisibility() == View.VISIBLE) {

			int scrollDis = Math.abs(mParallaxMonthHeight - weekHeight - scrollY);
			int SCROLL_SLOP = 200;
			if (mIsShowingWeekPager && scrollDis < SCROLL_SLOP) {
				//滑动状态下，下滑过程中动态改变周视图的宽度
				if (dragging) {
					FrameLayout.LayoutParams weekParams = (FrameLayout.LayoutParams) mBinding.layoutWeekCalendar.getLayoutParams();
					weekParams.height = weekHeight + scrollDis;
					mBinding.layoutWeekCalendar.setLayoutParams(weekParams);

				}
			} else {
				if (mIsShowingWeekPager) {
					mIsShowingWeekPager = false;
					mBinding.layoutMonthCalendar.setVisibility(View.VISIBLE);
					mBinding.layoutWeekCalendar.setVisibility(View.GONE);
				}
			}

		}
	}

	/**
	 * 手指按下
	 * <p>
	 * 重新计算 周视图的高度 以及 滑动因子
	 */
	@Override
	public void onDownMotionEvent() {
		factor = getMonthPagerTranslationFactor();
		mParallaxWeekHeight = weekHeight = getWeekHeight();
		FrameLayout.LayoutParams lps = (FrameLayout.LayoutParams) mBinding.contentMain.getLayoutParams();
		lps.height = mParallaxContentViewHeight + mParallaxMonthHeight - mParallaxWeekHeight;
		mBinding.contentMain.setLayoutParams(lps);
	}

	@Override
	public void onUpOrCancelMotionEvent(ScrollState scrollState) {
		mUpOrCancelTriggered = true;
	}

}
