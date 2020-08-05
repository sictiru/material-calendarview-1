package com.prolificinteractive.materialcalendarview.sample;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.CalendarMode;
import com.prolificinteractive.materialcalendarview.OnCalendarMovedBackwardListener;
import com.prolificinteractive.materialcalendarview.OnCalendarMovedForwardListener;
import com.prolificinteractive.materialcalendarview.OnDateLongClickListener;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.OnMonthChangedListener;
import com.prolificinteractive.materialcalendarview.OnWeekChangedListener;
import com.prolificinteractive.materialcalendarview.WeekDayRange;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Shows off the most basic usage
 */

public class BasicActivity extends AppCompatActivity implements OnDateSelectedListener, OnMonthChangedListener, OnDateLongClickListener, OnWeekChangedListener, OnCalendarMovedBackwardListener, OnCalendarMovedForwardListener {

    private static final DateFormat FORMATTER = SimpleDateFormat.getDateInstance();

    @BindView(R.id.calendarView)
    MaterialCalendarView widget;

    @BindView(R.id.textView)
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_basic);
        ButterKnife.bind(this);

        widget.state().edit()
                .setMinimumDate(CalendarDay.from(2018, 10, 1))
                .setMaximumDate(CalendarDay.from(2020, 5, 12))
                .commit();
        widget.setOnDateChangedListener(this);
        widget.setOnDateLongClickListener(this);
        widget.setOnMonthChangedListener(this);
        widget.setOnWeekChangedListener(this);
        widget.setOnCalendarMoveForwardListener(this);
        widget.setOnCalendarMoveBackwardListener(this);

        //Setup initial text
        textView.setText("No Selection");
    }

    @Override
    public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected, boolean isDisabled) {
        textView.setText(selected && !isDisabled ? FORMATTER.format(date.getDate()) : "No Selection");
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

    @OnClick(R.id.button_disable_days)
    public void disableDaysClicked() {
        ArrayList<CalendarDay> disabledDays = new ArrayList<>();
        disabledDays.add(CalendarDay.from(2018, 9, 30));
        disabledDays.add(CalendarDay.from(2018, 9, 31));
        widget.setDisabledDays(disabledDays);
    }

    public void onCalendarMovedBackward() {
        Toast.makeText(this, "backward", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCalendarMovedForward() {
        Toast.makeText(this, "forward", Toast.LENGTH_SHORT).show();
    }
}
