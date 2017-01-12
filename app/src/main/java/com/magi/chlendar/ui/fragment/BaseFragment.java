package com.magi.chlendar.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.View.OnClickListener;

/**
 * @author zhangzhaowen @ Zhihu Inc.
 * @since 01-04-2017
 */
public abstract class BaseFragment extends Fragment implements OnClickListener {

    /**
     * 该fragment attach的activity
     */
    protected Context context;

    /**
     * 初始化activity，子Fragment代替{@link #getActivity()}方法
     *
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context =  context;
    }

    /**
     * 得到Fragment填充View后，添加注解
     *
     */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        onSendView();
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    protected void onSendView() {

    }

    /**
     * {@link #context} 是否可用
     */
    public boolean isValidActivity() {
	    return !isRemoving() && context != null && !isDetached() && isAdded();

    }

    /**
     * 防止极端情况的出现:Activity结束,事件还没有开始处理
     *
     * @param v targetView
     */
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
