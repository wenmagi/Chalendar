package com.magi.chlendar.utils;

import android.util.Log;

import java.net.MalformedURLException;
import java.net.URL;

class AppConfig {
    static final boolean DEV_BUILD = true;
    static final int MIN_LOG_LEVEL = DEV_BUILD ? Log.DEBUG : Log.INFO;
    static final boolean LOG_LINE_NUMBER = false;
    private static final String DEFAULT_URL;

	static {
        //区分测试服，正式服URL
        if (DEV_BUILD) {
            DEFAULT_URL = "";
        } else {
            DEFAULT_URL = "";
        }
		URL DEFAULT_WEB_URL;
		try {
            DEFAULT_WEB_URL = new URL(DEFAULT_URL);
        } catch (MalformedURLException e) {
            DEFAULT_WEB_URL = null;
            LogUtils.e("parse Default URL error %s", e);
        }
    }

}
