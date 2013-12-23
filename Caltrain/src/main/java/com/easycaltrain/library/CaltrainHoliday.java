package com.easycaltrain.library;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class CaltrainHoliday {

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private Date mDate;

    // www.caltrain.com/schedules/holidayservice.html
    private Holiday[] mSundayHolidays = {
            new FixedHoliday(Calendar.JANUARY, 1),
            new InverseOrdinalHoliday(Calendar.MAY, Calendar.MONDAY, 1),
            new FixedHoliday(Calendar.JULY, 4),
            new OrdinalHoliday(Calendar.SEPTEMBER, Calendar.MONDAY, 1),
            new OrdinalHoliday(Calendar.NOVEMBER, Calendar.THURSDAY, 4),
            new FixedHoliday(Calendar.DECEMBER, 25)
    };

    private Holiday[] mSaturdayHolidays = {
            new DayAfterHoliday( (new OrdinalHoliday(Calendar.NOVEMBER, Calendar.THURSDAY, 4)).date() )
    };

    public CaltrainHoliday(Date date){
        mDate = date;
    }

    public boolean isSaturdayHoliday(){
        return isHoliday(mSaturdayHolidays);
    }

    public boolean isSundayHoliday(){
        return isHoliday(mSundayHolidays);
    }

    private boolean isHoliday(Holiday[] holidays){
        boolean dayIsHoliday = false;
        String currentDateFormat = dateFormat.format(mDate);

        for(Holiday holiday: holidays){
            String holidayDateFormat = dateFormat.format(holiday.date());
            dayIsHoliday = dayIsHoliday
                        || currentDateFormat.compareTo(holidayDateFormat) == 0 ;
        }

        return dayIsHoliday;
    }


    private interface Holiday {
        public Date date();
    }

    private class FixedHoliday implements Holiday{
        int mMonth;
        int mDay;

        public FixedHoliday(int month, int day){
            mMonth = month;
            mDay = day;
        }

        public Date date(){
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.MONTH, mMonth);
            calendar.set(Calendar.DAY_OF_MONTH, mDay);
            return calendar.getTime();
        }
    }

    private class DayAfterHoliday implements Holiday{
        Date mDate;

        public DayAfterHoliday(Date date){
            mDate = date;
        }

        public Date date(){
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(mDate);
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            return calendar.getTime();
        }
    }

    private class OrdinalHoliday implements Holiday{
        protected int mMonth;
        protected int mDayOfWeek;
        protected int mOrdinal;

        private OrdinalHoliday(int mMonth, int mdayOfWeek, int mOrdinal) {
            this.mMonth = mMonth;
            this.mDayOfWeek = mdayOfWeek;
            this.mOrdinal = mOrdinal;
        }


        public Date date(){
            Calendar c = Calendar.getInstance();
            c.set(Calendar.MONTH, mMonth);
            c.set(Calendar.DAY_OF_MONTH, 1);

            while (c.get(Calendar.DAY_OF_WEEK) != mDayOfWeek ) {
                c.add(Calendar.DAY_OF_MONTH, 1);
            }
            c.add(Calendar.DAY_OF_MONTH, (mOrdinal - 1) * 7);

            return c.getTime();
        }
    }

    private class InverseOrdinalHoliday implements Holiday{
        protected int mMonth;
        protected int mDayOfWeek;
        protected int mOrdinal;

        private InverseOrdinalHoliday(int mMonth, int mdayOfWeek, int mOrdinal) {
            this.mMonth = mMonth;
            this.mDayOfWeek = mdayOfWeek;
            this.mOrdinal = mOrdinal;
        }

        public Date date(){
            Calendar c = Calendar.getInstance();
            c.set(Calendar.MONTH, mMonth);
            c.set(Calendar.DAY_OF_MONTH, c.getActualMaximum(Calendar.DAY_OF_MONTH));

            while (c.get(Calendar.DAY_OF_WEEK) != mDayOfWeek ) {
                c.add(Calendar.DAY_OF_MONTH, -1);
            }
            c.add(Calendar.DAY_OF_MONTH, (mOrdinal - 1) * -7);

            return c.getTime();
        }
    }

}
