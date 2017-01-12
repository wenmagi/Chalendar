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
import com.magi.chlendar.ui.calendar.month.MonthView;

import static com.magi.chlendar.R.attr.selectableItemBackground;
import static com.magi.chlendar.utils.CalendarConfig.MAX_MONTH_SCROLL_COUNT;

/**
 * @author zhangzhaowen @ Zhihu Inc.
 * @since 01-04-2017
 */
public class MonthPagerAdapter extends PagerAdapter {

	private LruCache<Integer, MonthView> views;
	private Context context;

	public MonthPagerAdapter(Context context) {
		views = new LruCache<>(MAX_MONTH_SCROLL_COUNT);
		this.context = context;
	}

	@SuppressWarnings("ResourceType")
	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		MonthView v;
		if (views.get(position) == null) {
			v = new MonthView(context, position);
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
				int[] attrs = new int[]{android.R.attr.selectableItemBackground};
				TypedArray typedArray = context.obtainStyledAttributes(attrs);
				Drawable drawableFromTheme = typedArray.getDrawable(0);
				typedArray.recycle();
				v.setClickable(true);
				v.setForeground(drawableFromTheme);
			}
			v.setBackgroundResource(R.color.white);
			views.put(position, v);
		} else {
			v = views.get(position);
			v.invalidate();
		}

		v.setTag(position);
		container.addView(v);
		return v;
	}

	public MonthView getPrimaryItem(int position) {
		return views.get(position);
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
		return MAX_MONTH_SCROLL_COUNT;
	}

}
