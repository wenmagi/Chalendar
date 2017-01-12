package com.magi.chlendar.ui;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.magi.chlendar.R;
import com.magi.chlendar.databinding.ActivityMainBinding;
import com.magi.chlendar.models.event.DayChangeEvent;
import com.magi.chlendar.ui.fragment.calendar.CalendarFragment;
import com.magi.chlendar.utils.CalendarConfig;
import com.magi.chlendar.utils.date.CalendarHelper;
import com.magi.chlendar.utils.date.Lunar;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Date;

import static android.text.format.DateFormat.format;
import static com.magi.chlendar.ui.calendar.Util.getToday;
import static com.magi.chlendar.ui.calendar.Util.setImageClickStateChangeListener;
import static com.magi.chlendar.utils.date.Util.cc_dateByMovingToBeginningOfDay;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

	ActivityMainBinding mBinding;
	private Lunar lunar = Lunar.getInstance();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		EventBus.getDefault().register(this);
		CalendarConfig.initInMainThread(getApplicationContext());

		mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);

		mBinding.fab.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
						.setAction("Action", null).show();
			}
		});
		setImageClickStateChangeListener(mBinding.titleLayout.iconBackToday);
		mBinding.titleLayout.iconBackToday.setOnClickListener(this);

		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		transaction.replace(R.id.content_main, new CalendarFragment());
		transaction.commitNow();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_main, menu);
		return false;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		//noinspection SimplifiableIfStatement
		if (id == R.id.action_settings) {
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onClick(View view) {
		if (view == mBinding.titleLayout.iconBackToday) {
			CalendarHelper.setSelectedDay(getToday());

			mBinding.titleLayout.iconBackToday.setVisibility(View.GONE);
		}
	}

	/**
	 * invoke this method when DayChangeEvent posted
	 */
	@SuppressWarnings("unused")
	@Subscribe(threadMode = ThreadMode.MAIN)
	public void onDayChangeEventMainThread(DayChangeEvent event) {
		lunar.setCalendar(event.currentDay);
		mBinding.titleLayout.tvTitle.setText(format("yyyy.MM.dd", event.currentDay));
		mBinding.titleLayout.tvSubTitle.setText(getString(R.string.main_activity_title_format, lunar.getLunarMonthString(), lunar.getLunarDayString()));

		if (event.currentDay != null && !event.currentDay.equals(cc_dateByMovingToBeginningOfDay(new Date()))) {
			mBinding.titleLayout.iconBackToday.setVisibility(View.VISIBLE);
		} else
			mBinding.titleLayout.iconBackToday.setVisibility(View.GONE);
	}
}
