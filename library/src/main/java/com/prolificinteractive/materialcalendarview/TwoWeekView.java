package com.prolificinteractive.materialcalendarview;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;

import java.util.Calendar;
import java.util.Collection;

@SuppressLint("ViewConstructor")
public class TwoWeekView extends CalendarPagerView {

    private static int MAX_WEEKS = 2;

    TwoWeekView(@NonNull MaterialCalendarView view, CalendarDay month, int firstDayOfWeek, boolean showWeekDays, boolean disabledPastDates) {
        super(view, month, firstDayOfWeek, showWeekDays, disabledPastDates);
    }

    @Override
    protected void buildDayViews(Collection<DayView> dayViews, Calendar calendar) {
        for (int w = 0; w < MAX_WEEKS; w++) {
            for (int d = 0; d < DEFAULT_DAYS_IN_WEEK; d++) {
                addDayView(dayViews, calendar);
            }
        }
    }

    public CalendarDay getMonth() {
        return getFirstViewDay();
    }

    @Override
    protected boolean isDayEnabled(CalendarDay day) {
        return true;
    }

    @Override
    protected int getRows() {
        return showWeekDays ? MAX_WEEKS + DAY_NAMES_ROW : MAX_WEEKS;
    }
}