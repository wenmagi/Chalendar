package com.magi.chlendar.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.FrameLayout;


/**
 * @author zhangzhaowen @ Zhihu Inc.
 * @since 01-11-2017
 */

public class RippleLayout extends FrameLayout {

	private MotionEvent mLastMotionEvent = null;

	public RippleLayout(Context context) {
		super(context);
	}

	public RippleLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public RippleLayout(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
//		if (mLastMotionEvent != null && mLastMotionEvent == ev)
//			return false;

		return super.onInterceptTouchEvent(ev);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
//		if (mLastMotionEvent == null) {
//			mLastMotionEvent = event;
//			MotionEvent newEvent = MotionEvent.obtain(event);
//			((ViewGroup) getParent()).dispatchTouchEvent(newEvent);
//			newEvent.recycle();
//			return true;
//		} else if (mLastMotionEvent.equals(event)) {
//			mLastMotionEvent = null;
//			return false;
//		}

		return super.onTouchEvent(event);
	}
}
