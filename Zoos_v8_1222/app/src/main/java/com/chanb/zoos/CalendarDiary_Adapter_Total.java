package com.chanb.zoos;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chanb.zoos.utils.EqualSpacingItemDecoration;

import java.util.List;


public class CalendarDiary_Adapter_Total extends RecyclerView.Adapter<CalendarDiary_Adapter_Total.SingleItemRowHolder> {

    private final List<CalendarDiaryItem_Total> mDataList;
    private Context mContext;

    //참고의 Recycler.
    public CalendarDiary_Adapter_Total(Context context, List<CalendarDiaryItem_Total> dataList) {
        this.mDataList = dataList;
        this.mContext = context;
    }

    @Override
    public SingleItemRowHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.calendar_diary_item_total, parent, false);
        return new SingleItemRowHolder(view);
    }

    //item View 레이아웃 연결.
    @Override
    public void onBindViewHolder(SingleItemRowHolder holder, int position) {

        CalendarDiaryItem_Total item = mDataList.get(position);
        List<CalendarDiaryItem_Case> SingleSection = mDataList.get(position).getImageView();
        final String date = item.getDate();
        holder.title.setText(date);

        CalendarDiary_Adapter_Case itemListData = new CalendarDiary_Adapter_Case(mContext, SingleSection);
        holder.rv.setHasFixedSize(false);
        holder.rv.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        holder.rv.setAdapter(itemListData);
        holder.rv.setNestedScrollingEnabled(false);
        //공간여백 만들어주는 기본 클래스. 간격을 매개변수로 받는다. 추가적으로 형태도 받음. Horizontal, Linear, Grid
        holder.rv.addItemDecoration(new EqualSpacingItemDecoration(15, LinearLayout.VERTICAL));
    }


    @Override
    public int getItemCount() {
        return (null != mDataList ? mDataList.size() : 0);
    }


    // view 연동.
    public static class SingleItemRowHolder extends RecyclerView.ViewHolder {
        TextView title;
        RecyclerView rv;

        public SingleItemRowHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.date_calendar_diary_item_total);
            rv = itemView.findViewById(R.id.recycle_calendar_diary_total);

        }
    }

}
