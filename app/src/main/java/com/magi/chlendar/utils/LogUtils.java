package com.magi.chlendar.utils;


import android.util.Log;

/**
 * @author zhangzhaowen @ Zhihu Inc.
 * @since 01-04-2017
 */
public class LogUtils {
	public static final String LOG_TAG = "Chlendar";
	public static final boolean V = Log.VERBOSE >= AppConfig.MIN_LOG_LEVEL;
	public static final boolean D = Log.DEBUG >= AppConfig.MIN_LOG_LEVEL;
	public static final boolean I = Log.INFO >= AppConfig.MIN_LOG_LEVEL;
	public static final boolean W = Log.WARN >= AppConfig.MIN_LOG_LEVEL;
	public static final boolean DW = AppConfig.DEV_BUILD
			&& Log.WARN >= AppConfig.MIN_LOG_LEVEL;
	public static final boolean E = Log.ERROR >= AppConfig.MIN_LOG_LEVEL;

	private LogUtils() {
	}

	@SuppressWarnings("unused")
	private static void log(int priority, String msg, Throwable throwable) {
		try {
			if (throwable != null)
				Log.println(
						priority,
						LOG_TAG,
						msg + ", exception:\n\t" + Log
								.getStackTraceString(throwable));
			else
				Log.println(priority, LOG_TAG, msg);

		} catch (Exception e) {
			Log.e(LOG_TAG, "Failed to log: " + e.getMessage());
		}
	}

	/**
	 * Warn the incomplete implemented of methods.
	 */
	@SuppressWarnings("unused")
	public static void warnIncomplete() {
		if (AppConfig.LOG_LINE_NUMBER) {
			StackTraceElement[] sts = Thread.currentThread().getStackTrace();
			StackTraceElement st = null;
			for (int i = 2; i < sts.length; ++i) {
				StackTraceElement tmp = sts[i];
				if (!tmp.getClassName().contains("LogUtils")) {
					st = tmp;
					break;
				}
			}
			if (st != null)
				Log.println(
						Log.WARN,
						LOG_TAG,
						new StringBuilder().append(st.getFileName())
								.append(" line").append(st.getLineNumber())
								.append(", ").append(st.getMethodName())
								.append("(): incomplete implementation")
								.toString());
			else
				Log.println(Log.WARN, LOG_TAG, "incomplete implementation");
		} else
			Log.println(Log.WARN, LOG_TAG, "incomplete implementation");
	}

	/**
	 * Output VERBOSE information in log.
	 */
	@SuppressWarnings("unused")
	public static void v(Throwable throwable, String format, Object... args) {
			try {
				log(Log.VERBOSE, String.format(format, args), throwable);
			} catch (Exception e) {
				Log.e(LOG_TAG, "Failed to v: " + e.getMessage());
			}
	}

	/**
	 * Output VERBOSE information in log.
	 */
	public static void v(String format, Object... args) {
		v(null, format, args);
	}

	/**
	 * Output DEBUG information in log.
	 */
	public static void d(Throwable throwable, String format, Object... args) {
		try {
			if (args == null || args.length == 0)
				log(Log.DEBUG, format, throwable);
			else
				log(Log.DEBUG, String.format(format, args), throwable);
		} catch (Exception e) {
			Log.e(LOG_TAG, "Failed to d: " + e.getMessage());
		}
	}

	/**
	 * Output DEBUG information in log.
	 */
	public static void d(String format, Object... args) {
		d(null, format, args);
	}

	/**
	 * Output INFO information in log.
	 */
	public static void i(Throwable throwable, String format, Object... args) {
		try {
			if (args == null || args.length == 0)
				log(Log.INFO, format, throwable);
			else
				log(Log.INFO, String.format(format, args), throwable);
		} catch (Exception e) {
			Log.e(LOG_TAG, "Failed to i: " + e.getMessage());
		}
	}

	/**
	 * Output INFO information in log.
	 */
	public static void i(String format, Object... args) {
		i(null, format, args);
	}

	/**
	 * Output WARN information in log.
	 */
	public static void w(Throwable throwable, String format, Object... args) {
		try {
			if (args == null || args.length == 0)
				log(Log.WARN, format, throwable);
			else
				log(Log.WARN, String.format(format, args), throwable);
		} catch (Exception e) {
			Log.e(LOG_TAG, "Failed to w: " + e.getMessage());
		}
	}

	/**
	 * Output WARN information in log.
	 *
	 */
	public static void w(String format, Object... args) {
		w(null, format, args);
	}

	/**
	 * Output ERROR information in log
	 */
	public static void e(Throwable throwable, String format, Object... args) {
		if (Log.ERROR >= AppConfig.MIN_LOG_LEVEL)
			try {
				String msg = format;
				if (args != null && args.length > 0)
					msg = String.format(format, args);

				log(Log.ERROR, msg, throwable);

			} catch (Exception e) {
				Log.e(LOG_TAG, "Failed to e: " + e.getMessage());
			}
	}

	/**
	 * Output ERROR information in log.
	 */
	public static void e(String format, Object... args) {
		e(null, format, args);
	}

	/**
	 * Output the assertion that something is true in log.
	 *
	 */
	public static void assertTrue(boolean bool) {
		if (!bool)
			LogUtils.e("Assertion failed");
	}

	/**
	 * Out put WARN & DEV_BUILD information in log.
	 *
	 */
	public static void dw(Throwable throwable, String format, Object... args) {
		if (DW)
			try {
				String msg = format;
				if (args != null && args.length > 0)
					msg = String.format(format, args);

				log(Log.WARN, msg, throwable);

			} catch (Exception e) {
				Log.e(LOG_TAG, "Failed to w: " + e.getMessage()); //$NON-NLS-1$
			}
	}

	/**
	 * Out put WARN & DEV_BUILD information in log.
	 */
	@SuppressWarnings("unused")
	public static void dw(String format, Object... args) {
		dw(null, format, args);
	}

	/**
	 * Output in log that a specific feature is not supported.
	 *
	 */
	@SuppressWarnings("unused")
	public static void missedFeature(String feature) {
		e("%s is not supported in Social SDK", feature);
	}
}
