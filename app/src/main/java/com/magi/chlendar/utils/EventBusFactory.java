package com.magi.chlendar.utils;

import com.magi.chlendar.models.event.DayChangeEvent;

import org.greenrobot.eventbus.EventBus;

import java.util.Date;

/**
 * @author zhangzhaowen @ Zhihu Inc.
 * @since 01-05-2017
 */

public class EventBusFactory {

	public static void postDayChangeEvent(final Date date) {
		DayChangeEvent event = new DayChangeEvent();
		event.currentDay = date;
		EventBus.getDefault().post(event);
	}

}
