package com.prolificinteractive.materialcalendarview.sample;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.CalendarMode;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateLongClickListener;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.OnMonthChangedListener;
import com.prolificinteractive.materialcalendarview.OnWeekChangedListener;
import com.prolificinteractive.materialcalendarview.WeekDayRange;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Shows off the most basic usage
 */
public class BasicActivity extends AppCompatActivity implements OnDateSelectedListener, OnMonthChangedListener, OnDateLongClickListener, OnWeekChangedListener {

    private static final DateFormat FORMATTER = SimpleDateFormat.getDateInstance();

    @BindView(R.id.calendarView)
    MaterialCalendarView widget;

    @BindView(R.id.textView)
    TextView textView;

    String initStart;
    String initEnd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_basic);
        ButterKnife.bind(this);

        widget.state().edit()
                .setMinimumDate(CalendarDay.from(2018, 4, 3))
                .setMaximumDate(CalendarDay.from(2020, 5, 12))
                .commit();
        widget.setOnDateChangedListener(this);
        widget.setOnDateLongClickListener(this);
        widget.setOnMonthChangedListener(this);
        widget.setOnWeekChangedListener(this);

        //Setup initial text
        textView.setText("No Selection");
        Log.d("BasicActivity", widget.getInitStartDate() + " - " + widget.getInitEndDate());
        initStart = widget.getInitStartDate();
        initEnd = widget.getInitEndDate();
    }

    @Override
    public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
        textView.setText(selected ? FORMATTER.format(date.getDate()) : "No Selection");
    }

    @Override
    public void onDateLongClick(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date) {
        final String text = String.format("%s is available", FORMATTER.format(date.getDate()));
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onMonthChanged(MaterialCalendarView widget, CalendarDay date) {
        //noinspection ConstantConditions
        getSupportActionBar().setTitle(FORMATTER.format(date.getDate()));
    }

    @Override
    public void onWeekChanged(MaterialCalendarView view, WeekDayRange weekDayRange) {
        Log.d("BasicActivity", weekDayRange.startDate + " - " + weekDayRange.endDate);
    }

    @OnClick(R.id.button_mode_toggle)
    public void modeClicked() {
        if (widget.getCalendarMode() == CalendarMode.MONTHS) {
            widget.setTileHeightDp(66);
            widget.state().edit()
                    .setCalendarDisplayMode(CalendarMode.TWO_WEEKS)
                    .commit();
        } else {
            widget.setTileHeightDp(54);
            widget.state().edit()
                    .setCalendarDisplayMode(CalendarMode.MONTHS)
                    .commit();
        }
    }
}
