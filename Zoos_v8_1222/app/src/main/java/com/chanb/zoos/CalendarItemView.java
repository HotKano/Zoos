package com.chanb.zoos;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * 일자에 표시하는 텍스트뷰 정의
 *
 * @author Mike
 */
public class CalendarItemView extends LinearLayout {

    private CalendarItem item;
    TextView textView;
    RelativeLayout gridItem;
    RecyclerView rv;

    public CalendarItemView(Context mContext) {
        super(mContext);
        init(mContext);
    }

    private void init(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.calendar_item, this, true);

        textView = findViewById(R.id.date_diary_item);
        rv = findViewById(R.id.recycle_day_line);
        gridItem = findViewById(R.id.grid_item);
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
