package com.chanb.zoos;


import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class Search_Adapter extends RecyclerView.Adapter<Search_Adapter.ViewHolder> {

    private final List<SearchItem> mDataList;

    public Search_Adapter(List<SearchItem> dataList) {
        mDataList = dataList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_item, parent, false);

        return new ViewHolder(view);
    }

    //item View 레이아웃 연결.
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        SearchItem item = mDataList.get(position);
        holder.title.setText(item.getTitle());

        btn(holder, item);

    }

    //저장한 글 또는 스토리로 이동하는 부분. 나중에 카테고리에 따라서 구분을 지을 필요가 있음.
    private void btn(final ViewHolder holder, final SearchItem item) {

        holder.title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar_input_act c = new Calendar_input_act();
                c.c.calendar_input_editText.setText(item.getTitle());
            }
        });
    }


    @Override
    public int getItemCount() {
        return mDataList.size();
    }


    // view 연동.
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title;

        public ViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.search_title);
        }
    }

}
