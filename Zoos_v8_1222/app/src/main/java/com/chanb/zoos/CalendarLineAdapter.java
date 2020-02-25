package com.chanb.zoos;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;


public class CalendarLineAdapter extends RecyclerView.Adapter<CalendarLineAdapter.ViewHolder> {

    private final List<CalendarLineItem> mDataList;
    CalendarItem cl;
    String TAG = "CalendarAdapter";

    public CalendarLineAdapter(List<CalendarLineItem> mDataList, CalendarItem cl) {
        this.mDataList = mDataList;
        this.cl = cl;
    }

    // view 연동.
    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView text;

        public ViewHolder(View itemView) {
            super(itemView);
            text = itemView.findViewById(R.id.text_day_work);
        }
    }

    @Override
    public CalendarLineAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.calendar_line_item, parent, false);
        return new CalendarLineAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        CalendarLineItem item = mDataList.get(position);
        int day = cl.getDay();

        if (day == item.getDay()) {
            holder.text.setText(item.getDayWork());
            int color = R.color.start_with_phone_color;
            holder.text.setBackgroundColor(holder.itemView.getResources().getColor(color));
        } else {
            holder.text.setVisibility(View.GONE);
            holder.text.setText("");
        }
    }

    @Override
    public int getItemCount() {
        return (null != mDataList ? mDataList.size() : 0);
    }
}
