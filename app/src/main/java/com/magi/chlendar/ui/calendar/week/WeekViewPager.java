package com.magi.chlendar.ui.calendar.week;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.magi.chlendar.ui.calendar.month.MonthView;
import com.magi.chlendar.ui.calendar.month.MonthViewPager;
import com.magi.chlendar.utils.LogUtils;

import static com.magi.chlendar.utils.CalendarConfig.CELL_HEIGHT;
import static com.magi.chlendar.utils.CalendarConfig.MAX_WEEK_SCROLL_COUNT;

public class WeekViewPager extends ViewPager {

	// ******* Declaration *********
	//
	public static final int OFFSET = MAX_WEEK_SCROLL_COUNT / 2;

	/**
	 * invalidate child{@link MonthView} of viewpager{@link MonthViewPager} for
	 * <p>
	 * which can refresh view in time when sliding.
	 */
	public static final int OFF_SCREEN_LIMIT = 0;

	/**
	 * Enable swipe
	 */
	private boolean enabled = true;

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public WeekViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public WeekViewPager(Context context) {
		super(context);
	}

	@Override
	public void setAdapter(PagerAdapter adapter) {
		super.setAdapter(adapter);
		setCurrentItem(OFFSET);
		LogUtils.e("wwwwwwwwww setCurrentItem %s", OFFSET);
		setOffscreenPageLimit(OFF_SCREEN_LIMIT);
	}

	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return enabled && super.onTouchEvent(event);
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent event) {
		if (enabled) {
			try {
				return super.onInterceptTouchEvent(event);
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
				return false;
			}
		} else {
			return false;
		}
	}

	/**
	 * ViewPager does not respect "wrap_content". The code below tries to
	 * measure the height of the child and set the height of viewpager based on
	 * child height
	 * <p/>
	 * It was customized from
	 * http://stackoverflow.com/questions/9313554/measuring-a-viewpager
	 * <p/>
	 * Thanks Delyan for his brilliant code
	 */
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

		int calHeight = CELL_HEIGHT;

		heightMeasureSpec = MeasureSpec.makeMeasureSpec(calHeight,
				MeasureSpec.EXACTLY);

		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}
}
