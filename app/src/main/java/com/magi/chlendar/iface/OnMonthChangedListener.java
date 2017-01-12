package com.magi.chlendar.iface;

import android.view.View;

import java.util.Date;

/**
 * @author zhangzhaowen @ Zhihu Inc.
 * @since 01-04-2017
 */
public abstract class OnMonthChangedListener {

    /**
     * 选择日期的回调
     */
    public abstract void onSelectDate(Date date, View view);


    /**
     * 长按日期的回调
     */
    public void onLongClickDate(Date date, View view) {
        // Do nothing
    }


    /**
     * 月份改变的回调
     */
    public void onChangeMonth(int day, int month, int year) {
        // Do nothing
    }


    /**
     * View创建完毕的回调
     */
    public void onCaldroidViewCreated() {
        // Do nothing
    }
}
