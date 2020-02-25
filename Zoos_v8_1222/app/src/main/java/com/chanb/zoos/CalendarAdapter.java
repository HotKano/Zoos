package com.chanb.zoos;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chanb.zoos.utils.EqualSpacingItemDecoration;

import java.util.Calendar;
import java.util.List;

/**
 * 어댑터 객체 정의
 *
 * @author Mike
 */
//total
public class CalendarAdapter extends RecyclerView.Adapter<CalendarAdapter.ViewHolder_Calendar> {

    private Context mContext;

    private int selectedPosition = -1;
    public CalendarItem[] items;

    private int curYear;
    private int curMonth;

    private int firstDay;
    private int lastDay;

    private Calendar mCalendar;
    boolean recreateItems = false;
    private List<CalendarLineItem> mDataList;


    public CalendarAdapter(Context context) {
        this.mContext = context;
        init();
    }

    private void init() {
        items = new CalendarItem[7 * 6];
        mCalendar = Calendar.getInstance();
        recalculate();
        resetDayNumbers();
    }

    // view 연동.
    public static class ViewHolder_Calendar extends RecyclerView.ViewHolder {

        public CalendarItem item;
        TextView textView;
        RelativeLayout gridItem;
        RecyclerView rv;

        public ViewHolder_Calendar(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.date_diary_item);
            rv = itemView.findViewById(R.id.recycle_day_line);
            gridItem = itemView.findViewById(R.id.grid_item);
        }

        public CalendarItem getItem() {
            return item;
        }

        public void setItem(CalendarItem item) {
            this.item = item;

            int day = item.getDay();
            if (day != 0) {
                textView.setText(String.valueOf(day));
            } else {
                textView.setText("");
            }
        }
    }

    public void setDataList(List<CalendarLineItem> dataList) {
        this.mDataList = dataList;
    }

    private void recalculate() {

        // set to the first day of the month
        mCalendar.set(Calendar.DAY_OF_MONTH, 1);

        // get week day
        int dayOfWeek = mCalendar.get(Calendar.DAY_OF_WEEK);
        firstDay = getFirstDay(dayOfWeek);

        int mStartDay = mCalendar.getFirstDayOfWeek();
        curYear = mCalendar.get(Calendar.YEAR);
        curMonth = mCalendar.get(Calendar.MONTH);
        lastDay = getMonthLastDay(curYear, curMonth);
    }

    public void setPresentMonth() {
        init();
        resetDayNumbers();
        selectedPosition = -1;
    }

    public void setPreviousMonth() {
        mCalendar.add(Calendar.MONTH, -1);
        recalculate();
        resetDayNumbers();
        selectedPosition = -1;
    }

    public void setNextMonth() {
        mCalendar.add(Calendar.MONTH, 1);
        recalculate();
        resetDayNumbers();
        selectedPosition = -1;
    }

    private void resetDayNumbers() {
        for (int i = 0; i < 42; i++) {
            // calculate day number
            int dayNumber = (i + 1) - firstDay;
            if (dayNumber < 1 || dayNumber > lastDay) {
                dayNumber = 0;
            }

            // save as a data item
            items[i] = new CalendarItem(curYear, curMonth + 1, dayNumber);
        }
    }

    private int getFirstDay(int dayOfWeek) {
        int result = 0;
        if (dayOfWeek == Calendar.SUNDAY) {
            result = 0;
        } else if (dayOfWeek == Calendar.MONDAY) {
            result = 1;
        } else if (dayOfWeek == Calendar.TUESDAY) {
            result = 2;
        } else if (dayOfWeek == Calendar.WEDNESDAY) {
            result = 3;
        } else if (dayOfWeek == Calendar.THURSDAY) {
            result = 4;
        } else if (dayOfWeek == Calendar.FRIDAY) {
            result = 5;
        } else if (dayOfWeek == Calendar.SATURDAY) {
            result = 6;
        }

        return result;
    }

    public int getCurYear() {
        return curYear;
    }

    public int getCurMonth() {
        return curMonth;
    }

    public Object getItem(int position) {
        return items[position];
    }

    @Override
    public ViewHolder_Calendar onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.calendar_item, viewGroup, false);
        return new ViewHolder_Calendar(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder_Calendar viewHolder, final int i) {

        // calculate row and column
        int countColumn = 7;
        //int rowIndex = i / countColumn;
        int columnIndex = i % countColumn;

        // set item data and properties, 배경.
        viewHolder.setItem(items[i]);

        // set properties
        viewHolder.textView.setTextSize(14);

        //요일 별 색상 정리 (공휴일은 반영 안되어 있음)
        if (columnIndex == 0) {
            viewHolder.textView.setTextColor(Color.RED);
        } else if (columnIndex == 6) {
            viewHolder.textView.setTextColor(Color.BLUE);
        } else {
            viewHolder.textView.setTextColor(Color.BLACK);
        }

        viewHolder.textView.setBackgroundColor(Color.WHITE);
        CalendarItem cl = (CalendarItem) getItem(i);

        viewHolder.gridItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar_frag.cf.moveAct(i);
            }
        });


        //날짜 별 일정 받는 곳.
        CalendarLineAdapter calendarLineAdapter = new CalendarLineAdapter(mDataList, cl);


        //스크롤 블록
        viewHolder.rv.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false) {
            @Override
            public boolean canScrollVertically() { // 세로스크롤 막기
                return false;
            }

            @Override
            public boolean canScrollHorizontally() { //가로 스크롤막기
                return false;
            }
        });
        viewHolder.rv.setHasFixedSize(true);
        viewHolder.rv.setAdapter(calendarLineAdapter);
        viewHolder.rv.setNestedScrollingEnabled(false);

        //공간여백 만들어주는 기본 클래스. 간격을 매개변수로 받는다. 추가적으로 형태도 받음. Horizontal, Linear, Grid
        viewHolder.rv.addItemDecoration(new EqualSpacingItemDecoration(1, LinearLayout.VERTICAL));
    }

    @Override
    public int getItemCount() {
        return items.length;
    }

    /**
     * get day count for each month
     *
     * @param year
     * @param month
     * @return
     */
    private int getMonthLastDay(int year, int month) {
        switch (month) {
            case 0:
            case 2:
            case 4:
            case 6:
            case 7:
            case 9:
            case 11:
                return (31);

            case 3:
            case 5:
            case 8:
            case 10:
                return (30);

            default:
                if (((year % 4 == 0) && (year % 100 != 0)) || (year % 400 == 0)) {
                    return (29);   // 2월 윤년계산
                } else {
                    return (28);
                }
        }
    }


}
