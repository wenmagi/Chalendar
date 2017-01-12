package com.magi.chlendar.ui.adapter;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.util.LruCache;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.magi.chlendar.R;
import com.magi.chlendar.ui.calendar.week.WeekView;

import static com.magi.chlendar.utils.CalendarConfig.FIRST_DAY_OF_WEEK;
import static com.magi.chlendar.utils.CalendarConfig.MAX_WEEK_SCROLL_COUNT;


/**
 * @author zhangzhaowen @ Zhihu Inc.
 * @since 01-06-2017
 */

public class WeekPagerAdapter extends PagerAdapter {
	private LruCache<Integer, WeekView> views;
	private Context context;

	public WeekPagerAdapter(Context context) {
		views = new LruCache<>(MAX_WEEK_SCROLL_COUNT);
		this.context = context;
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		WeekView view;

		if (views.get(position) == null) {
			view  = new WeekView(context, position);
			view.setBackgroundResource(R.color.setting_background_gray);
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
				int[] attrs = new int[]{android.R.attr.selectableItemBackground};
				TypedArray typedArray = context.obtainStyledAttributes(attrs);
				Drawable drawableFromTheme = typedArray.getDrawable(0);
				typedArray.recycle();
				view.setForeground(drawableFromTheme);
			}
			views.put(position, view);
		} else {
			view = views.get(position);
			view.invalidate();
		}

		view.setTag(position);
		container.addView(view);
		return view;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		if (views.get(position) != null)
			container.removeView(views.get(position));
	}

	@Override
	public int getItemPosition(Object object) {
		return POSITION_NONE;
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		return view == object;
	}

	@Override
	public int getCount() {
		return MAX_WEEK_SCROLL_COUNT;
	}
}
