package com.magi.chlendar.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import static com.magi.chlendar.ui.calendar.Util.removeFromSuperView;

/**
 * Created by MVEN on 16/7/20.
 * <p/>
 * email: magiwen@126.com.
 * <p/>
 * 支持延迟加载的Fragment
 */


public abstract class BaseLazyLoadFragment extends BaseFragment {

	/**
	 * fragment当前状态是否加载过
	 */
	private boolean isLoaded;

	/**
	 * fragment是否已经CreateView
	 */
	private boolean isCreatedView = false;

	protected View inVisibleView, visibleView;

	private RelativeLayout rootView;

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		visibleView = createView(inflater, container, savedInstanceState);

		rootView = createRootView(visibleView);
		isCreatedView = true;
		onInVisible();
		return rootView;
	}

	private RelativeLayout createRootView(View visibleView) {
		RelativeLayout rootView = new RelativeLayout(context);
		inVisibleView = createInvisibleView();
		RelativeLayout.LayoutParams visibleViewParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
		RelativeLayout.LayoutParams inVisibleViewParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
		removeFromSuperView(visibleView);
		if (visibleView != null)
			rootView.addView(visibleView, visibleViewParams);
		if (inVisibleView != null)
			rootView.addView(inVisibleView, inVisibleViewParams);
		return rootView;
	}


	/**
	 * 子类必须继承的方法，代替{@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}
	 */
	protected abstract View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState);

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		super.setUserVisibleHint(isVisibleToUser);

		if (isVisibleToUser && !isLoaded && isCreatedView) {
			onVisible();
		}
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		if (getUserVisibleHint())
			onVisible();
	}

	/**
	 * Fragment不可见时展示的界面
	 */
	private void onInVisible() {
		if (inVisibleView != null)
			inVisibleView.setVisibility(View.VISIBLE);
		if (visibleView != null)
			visibleView.setVisibility(View.GONE);
	}

	/**
	 * Fragment可见时执行的操作
	 */
	private void onVisible() {
		isLoaded = true;
		if (inVisibleView != null) {
			rootView.removeView(inVisibleView);
		}
		if (visibleView != null) {
			visibleView.setVisibility(View.VISIBLE);
		}
		lazyLoad();
	}

	/**
	 * 延迟加载
	 * </p>
	 * 子类必须复写
	 */
	protected abstract void lazyLoad();

	/**
	 * 统一延迟加载默认页面样式
	 */
	protected View createInvisibleView() {

		return new View(context);
	}

	@Override
	public void onClick(View v) {
		if (!isValidActivity()) {
			return;
		}

		OnClickView(v);
	}

	/**
	 * 子类必须复写，代替onClick事件
	 *
	 * @param v 目标View
	 */
	protected abstract void OnClickView(View v);
}
