package com.magi.chlendar.ui.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.TextView;


import com.magi.chlendar.R;
import com.magi.chlendar.utils.CalendarConfig;

import java.util.List;

/**
 * 一 二 三 四 五 六 日
 *
 * @author zhangzhaowen @ Zhihu Inc.
 * @since 01-04-2017
 */
public class WeekdayArrayAdapter extends ArrayAdapter<String> {
	private Context mContext;

	public WeekdayArrayAdapter(Context context, int textViewResourceId,
	                           List<String> objects) {
		super(context, textViewResourceId, objects);
		mContext = context;
	}

	@Override
	public boolean areAllItemsEnabled() {
		return false;
	}

	@Override
	public boolean isEnabled(int position) {
		return false;
	}

	@NonNull
	@Override
	public View getView(int position, View convertView, @NonNull ViewGroup parent) {

		TextView textView = (TextView) super.getView(position, convertView,
				parent);
		textView.setTextSize(TypedValue.COMPLEX_UNIT_PX,
				mContext.getResources().getDimensionPixelSize(R.dimen.text_size_describe));
		String item = getItem(position);
		if (item == null)
			return textView;

		if (item.equals("日") || item.equals("六"))
			textView.setTextColor(mContext.getResources().getColor(
					R.color.text_color_gray_99));
		else
			textView.setTextColor(mContext.getResources().getColor(
					R.color.text_color_gray_99));

		textView.setBackgroundColor(mContext.getResources().getColor(
				R.color.setting_background_gray));
		return textView;
	}

}
