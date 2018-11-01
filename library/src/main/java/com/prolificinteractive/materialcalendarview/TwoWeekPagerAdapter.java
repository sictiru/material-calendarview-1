package com.prolificinteractive.materialcalendarview;

import android.support.annotation.NonNull;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

@Experimental
public class TwoWeekPagerAdapter extends CalendarPagerAdapter<TwoWeekView> {

    public TwoWeekPagerAdapter(MaterialCalendarView mcv) {
        super(mcv);
    }

    @Override
    protected TwoWeekView createView(int position) {
        return new TwoWeekView(mcv, getItem(position), mcv.getFirstDayOfWeek(), showWeekDays, disabledPastDates);
    }

    @Override
    protected int indexOf(TwoWeekView view) {
        CalendarDay month = view.getFirstViewDay();
        return getRangeIndex().indexOf(month);
    }

    @Override
    protected boolean isInstanceOfView(Object object) {
        return object instanceof TwoWeekView;
    }

    @Override
    protected DateRangeIndex createRangeIndex(CalendarDay min, CalendarDay max) {
        return new BiWeekly(min, max, mcv.getFirstDayOfWeek());
    }

    public static class BiWeekly implements DateRangeIndex {

        private static final int DAYS_IN_TWO_WEEKS = 14;
        private final CalendarDay min;
        private final int count;

        public BiWeekly(@NonNull CalendarDay min, @NonNull CalendarDay max, int firstDayOfWeek) {
            this.min = getFirstDayOfWeek(min, firstDayOfWeek);
            this.count = weekNumberDifference(this.min, max) + 1;
        }

        @Override
        public int getCount() {
            return count;
        }

        @Override
        public int indexOf(CalendarDay day) {
            return weekNumberDifference(min, day);
        }

        @Override
        public CalendarDay getItem(int position) {
            long minMillis = min.getDate().getTime();
            long millisOffset = TimeUnit.MILLISECONDS.convert(position * DAYS_IN_TWO_WEEKS, TimeUnit.DAYS);
            long positionMillis = minMillis + millisOffset;
            return CalendarDay.from(positionMillis);
        }

        private int weekNumberDifference(@NonNull CalendarDay min, @NonNull CalendarDay max) {
            long millisDiff = max.getDate().getTime() - min.getDate().getTime();

            int dstOffsetMax = max.getCalendar().get(Calendar.DST_OFFSET);
            int dstOffsetMin = min.getCalendar().get(Calendar.DST_OFFSET);

            long dayDiff = TimeUnit.DAYS.convert(millisDiff + dstOffsetMax - dstOffsetMin, TimeUnit.MILLISECONDS);
            return (int) (dayDiff / DAYS_IN_TWO_WEEKS);
        }

        /*
         * Necessary because of how Calendar handles getting the first day of week internally.
         * /TODO: WTF IS THIS/
         */
        private CalendarDay getFirstDayOfWeek(@NonNull CalendarDay min, int wantedFirstDayOfWeek) {
            Calendar calendar = Calendar.getInstance();
            min.copyTo(calendar);
            while (calendar.get(Calendar.DAY_OF_WEEK) != wantedFirstDayOfWeek) {
                calendar.add(Calendar.DAY_OF_WEEK, -1);
            }
            return CalendarDay.from(calendar);
        }

        @Override
        public WeekDayRange getVisibleWeekDays(int position) {
            WeekDayRange weekDayRange = new WeekDayRange();
            long minMillis = min.getDate().getTime();
            long millisOffset = TimeUnit.MILLISECONDS.convert(position * DAYS_IN_TWO_WEEKS, TimeUnit.DAYS);
            long currentMillis = minMillis + millisOffset;
            long lastDayOffset = TimeUnit.MILLISECONDS.convert(13, TimeUnit.DAYS);
            weekDayRange.startDate = CalendarDay.from(currentMillis).getDateString();
            weekDayRange.endDate = CalendarDay.from(lastDayOffset + currentMillis).getDateString();
            return weekDayRange;
        }

        @Override
        public CalendarDay getWeeksMaxDate(int position) {
            long minMillis = min.getDate().getTime();
            long millisOffset = TimeUnit.MILLISECONDS.convert(position * DAYS_IN_TWO_WEEKS, TimeUnit.DAYS);
            long currentMillis = minMillis + millisOffset;
            long nextDayOffset = TimeUnit.MILLISECONDS.convert(13, TimeUnit.DAYS);
            return CalendarDay.from(nextDayOffset + currentMillis);
        }
    }
}