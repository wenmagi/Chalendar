package com.magi.chlendar.utils.date;

import android.content.Context;

import java.lang.reflect.Array;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.TimeZone;
import java.util.logging.Logger;

public final class Util {

	static boolean textHasContent(String aText) {
		return (aText != null) && (aText.trim().length() > 0);
	}

	static String quote(Object aObject) {
		return SINGLE_QUOTE + String.valueOf(aObject) + SINGLE_QUOTE;
	}

	static String getArrayAsString(Object aArray) {
		final String fSTART_CHAR = "[";
		final String fEND_CHAR = "]";
		final String fSEPARATOR = ", ";
		final String fNULL = "null";

		if (aArray == null) return fNULL;
		checkObjectIsArray(aArray);

		StringBuilder result = new StringBuilder(fSTART_CHAR);
		int length = Array.getLength(aArray);
		for (int idx = 0; idx < length; ++idx) {
			Object item = Array.get(aArray, idx);
			if (isNonNullArray(item)) {
				//recursive call!
				result.append(getArrayAsString(item));
			} else {
				result.append(item);
			}
			if (!isLastItem(idx, length)) {
				result.append(fSEPARATOR);
			}
		}
		result.append(fEND_CHAR);
		return result.toString();
	}

	static Logger getLogger(Class<?> aClass) {
		return Logger.getLogger(aClass.getPackage().getName());
	}

	public static Date cc_dateByMovingToBeginningOfDay(Date d1) {
		return cc_dateByMovingToBeginningOfDay(d1, TimeZone.getDefault());
	}

	public static Date cc_dateByMovingToBeginningOfDay(Date date, TimeZone tz) {
		if (date == null) {
			return null;
		}
		Calendar calendar = Calendar.getInstance(tz);
		calendar.setTime(date);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		return calendar.getTime();
	}

	public static boolean isEmpty(CharSequence s) {
		return s == null || s.length() == 0;
	}

	public static boolean isNotEmpty(CharSequence s) {
		return s != null && s.length() > 0;
	}

	public static <E> boolean isEmpty(Collection<E> list) {
		return list == null || list.size() == 0;
	}

	public static <E> boolean isNotEmpty(Collection<E> list) {
		return list != null && list.size() > 0;
	}

	public static String getString(Context context, int resID) {
		if (resID <= 0)
			return "";
		return context.getString(resID);
	}


	// PRIVATE

	private static final String SINGLE_QUOTE = "'";

	private static boolean isNonNullArray(Object aItem) {
		return aItem != null && aItem.getClass().isArray();
	}

	private static void checkObjectIsArray(Object aArray) {
		if (!aArray.getClass().isArray()) {
			throw new IllegalArgumentException("Object is not an array.");
		}
	}

	private static boolean isLastItem(int aIdx, int aLength) {
		return (aIdx == aLength - 1);
	}

}
