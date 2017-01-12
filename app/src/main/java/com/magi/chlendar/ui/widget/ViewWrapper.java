package com.magi.chlendar.ui.widget;

import android.view.View;

/**
 * @author zhangzhaowen @ Zhihu Inc.
 * @since 01-06-2017
 */


class ViewWrapper {

	private View view;

	private ViewWrapper(View view) {
		this.view = view;
	}

	public static ViewWrapper decorator(View view) {
		return new ViewWrapper(view);
	}

	public int getHeight() {
		return view.getLayoutParams().height;
	}

	public void setHeight(int height) {
		view.getLayoutParams().height = height;
		view.requestLayout();
	}

	public int getWidth() {
		return view.getLayoutParams().width;
	}

	public void setWidth(int width) {
		view.getLayoutParams().width = width;
		view.requestLayout();
	}
}

