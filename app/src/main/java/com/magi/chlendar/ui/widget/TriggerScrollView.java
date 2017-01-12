package com.magi.chlendar.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.github.ksoichiro.android.observablescrollview.ObservableScrollView;


/**
 * 解除 ScrollView 嵌套 Viewpager 滑动时拦截滑动方向 >slop 的滑动事件
 *
 * ScrollView #652 - #657 行
 *
 * @author zhangzhaowen @ Zhihu Inc.
 * @since 01-09-2017
 */

public class TriggerScrollView extends ObservableScrollView {
	private float mStartX;
	private float mStartY;
	private boolean isTrigger = false;

	public TriggerScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {

		int deltaX;
		int deltaY;

		final float x = ev.getX();
		final float y = ev.getY();

		switch (ev.getAction()) {
			case MotionEvent.ACTION_MOVE:
				deltaX = (int)(mStartX - x);
				deltaY = (int)(mStartY - y);

				int HORIZONTAL_LENGTH = 20;
				int TIGER_LENGTH = 50;
				if (Math.abs(deltaY) > TIGER_LENGTH
						&& Math.abs(deltaX) < HORIZONTAL_LENGTH) {

					isTrigger = true;
					return super.onInterceptTouchEvent(ev);
				}

				return false;//没有触发拦截条件，不拦截事件，继续分发至 ScrollView

			case MotionEvent.ACTION_DOWN:
				mStartX = x;
				mStartY = y;
				requestDisallowInterceptTouchEvent(false);
				return  super.onInterceptTouchEvent(ev);

			case MotionEvent.ACTION_CANCEL:
			case MotionEvent.ACTION_UP:
				if (isTrigger) {

					isTrigger = false;
					return  super.onInterceptTouchEvent(ev);
				}

				break;
		}
		return super.onInterceptTouchEvent(ev);

	}
}
