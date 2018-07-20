package com.prolificinteractive.materialcalendarview;

/**
 * Use math to calculate first days of months by position from a minimum date.
 */
interface DateRangeIndex {

    int getCount();

    int indexOf(CalendarDay day);

    CalendarDay getItem(int position);

    CalendarDay getWeeksMaxDate(int position);

    WeekDayRange getVisibleWeekDays(int position);
}
