package com.chanb.zoos;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Calendar;
import java.util.List;

public class DiaryView_Adapter extends RecyclerView.Adapter<DiaryView_Adapter.ViewHolder> {

    private final List<DiaryItem> mDataList;

    public DiaryView_Adapter(List<DiaryItem> dataList) {
        mDataList = dataList;
    }

    public String getDateDay(int year, int month, int date) {


        String day = "";

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month);
        cal.set(Calendar.DATE, date);

        int dayNum = cal.get(Calendar.DAY_OF_WEEK);

        switch (dayNum) {
            case 1:
                day = "일";
                break;
            case 2:
                day = "월";
                break;
            case 3:
                day = "화";
                break;
            case 4:
                day = "수";
                break;
            case 5:
                day = "목";
                break;
            case 6:
                day = "금";
                break;
            case 7:
                day = "토";
                break;

        }
        return day;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.calendar_diary_item, parent, false);
        return new ViewHolder(view);
    }

    //item view에 레이아웃 연결.
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        DiaryItem item = mDataList.get(position);

        String title = item.getTitle();
        String content = item.getContent();
        String date = item.getDate();
        String time = item.getTime();
        String year = item.getYear();
        String month = item.getMonth();
        String date_form = null;


        try {
            String date_resource = year + "-" + month + "-" + date;
            Log.d("date_day", date_resource);
            int year_form_input = Integer.valueOf(year);
            int month_form_input = (Integer.valueOf(month) - 1);
            int date_form_input = Integer.valueOf(date);

            String date_day = getDateDay(year_form_input, month_form_input, date_form_input);
            int data_change = Integer.parseInt(month);
            int date_change = Integer.parseInt(date);
            String year_form = year.substring(2, 4);
            String test[] = new String[2];
            test = time.split(":");

            if (Integer.valueOf(test[0]) < 10) {
                test[0] = "0" + test[0];
                time = test[0] + ":" + test[1];
            }

            if (Integer.valueOf(test[1]) < 10) {
                test[1] = "0" + test[1];
                time = test[0] + ":" + test[1];
            }

            if (data_change < 10) {
                month = "0" + month;
            }

            if (date_change < 10) {
                date = "0" + date;
            }

            date_form = year_form + "." + month + "." + date + "(" + date_day + ")";

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (title.length() > 20) {
            String title_cut = title.substring(0, 20);
            holder.title.setText(title_cut);
        } else {
            holder.title.setText(title);
        }

        holder.content.setText(content);
        holder.date.setText(date_form);
        holder.time.setText(time);
        try {
        /*String url = "http://133.186.135.41/zozoSNS/" + item.getImg();
        Log.d("INTERNET", url);

            //  카드 이미지
            Glide.with(holder.itemView.getContext())
                    .load(url)
                    .apply(new RequestOptions()
                            .placeholder(R.drawable.placeholder)
                            .error(R.drawable.placeholder)
                            .skipMemoryCache(true)
                            .dontTransform()
                            .override(64, 64)
                    )
                    .into(holder.color);
*/
            //btn(holder, item);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    //추천 및 해당 이미지 클릭 시 intent act 옮기는 처리 메소드.
    private void btn(final ViewHolder holder2, final DiaryItem item2) {

        holder2.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(holder2.itemView.getContext(), AlarmSettingAct.class);
                intent.putExtra("no", item2.getNo());
                intent.putExtra("from", "calendarFrag");
                intent.putExtra("time", item2.getTime());
                intent.putExtra("code", item2.getCode());
                holder2.itemView.getContext().startActivity(intent);
            }
        });


    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }


    // view 연동.
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, content, date, time;
        ImageView color;

        public ViewHolder(View itemView) {
            super(itemView);
            color = itemView.findViewById(R.id.diary_color_item);
            title = itemView.findViewById(R.id.diary_title);
            content = itemView.findViewById(R.id.diary_content);
            date = itemView.findViewById(R.id.diary_date);
            time = itemView.findViewById(R.id.diary_time);

        }
    }

}
