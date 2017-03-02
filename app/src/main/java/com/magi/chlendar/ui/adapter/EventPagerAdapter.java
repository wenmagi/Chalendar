package com.magi.chlendar.ui.adapter;

import android.content.Context;
import android.support.v4.util.LruCache;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.magi.chlendar.R;
import com.magi.chlendar.models.Event;

import java.util.ArrayList;
import java.util.List;

import static com.magi.chlendar.utils.CalendarConfig.MAX_EVENTS_SCROLL_COUNT;


/**
 * @author zhangzhaowen @ Zhihu Inc.
 * @since 01-26-2017
 */

public class EventPagerAdapter extends PagerAdapter {
	private LruCache<Integer, View> views;
	private List<Event> events;
	private Context context;

	public EventPagerAdapter(Context context, List<Event> events) {
		this.context = context;
		views = new LruCache<>(MAX_EVENTS_SCROLL_COUNT);
		if (events != null) {
			this.events = new ArrayList<>();
			this.events.addAll(events);
		}
	}


	public void setEvents(List<Event> events) {
		if (events == null)
			return;

		if (this.events == null)
			this.events = new ArrayList<>();
		this.events.clear();
		this.events.addAll(events);
		notifyDataSetChanged();
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		View view;
		if (views.get(position) == null) {
			view = LayoutInflater.from(context).inflate(R.layout.layout_event, null);
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
	public int getCount() {
		return MAX_EVENTS_SCROLL_COUNT;
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		return view == object;
	}

	@Override
	public int getItemPosition(Object object) {
		return POSITION_NONE;
	}
}
