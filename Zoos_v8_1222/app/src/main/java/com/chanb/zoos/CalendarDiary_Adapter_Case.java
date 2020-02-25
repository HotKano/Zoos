package com.chanb.zoos;


import android.content.Context;
import android.content.Intent;
import android.icu.text.RelativeDateTimeFormatter;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.chanb.zoos.utils.Database;

import java.util.Calendar;
import java.util.List;

public class CalendarDiary_Adapter_Case extends RecyclerView.Adapter<CalendarDiary_Adapter_Case.ItemRowHolder> {

    private final List<CalendarDiaryItem_Case> mDataList;
    private Context mContext;


    // 참고의 Section.
    public CalendarDiary_Adapter_Case(Context context, List<CalendarDiaryItem_Case> dataList) {
        this.mDataList = dataList;
        this.mContext = context;
    }

    @Override
    public ItemRowHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.calendar_diary_item, null);
        ItemRowHolder mh = new ItemRowHolder(view);
        return mh;
    }

    //item View 레이아웃 연결. 날짜 표시하는 부분.
    @Override
    public void onBindViewHolder(ItemRowHolder holder, int position) {
        CalendarDiaryItem_Case item = mDataList.get(position);
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

        btn(holder, item);
    }


    @Override
    public int getItemCount() {
        return (null != mDataList ? mDataList.size() : 0);
    }


    // 일정 시간 변경으로 이동하는 부분.
    private void btn(final ItemRowHolder holder, final CalendarDiaryItem_Case item) {

        String[] temp = new String[2];
        String dbName = "UserDatabase_Zoos";
        int dataBaseVersion = 1;
        Database database = new Database(holder.itemView.getContext().getApplicationContext(), dbName, null, dataBaseVersion);
        database.init();
        temp = database.select("MEMBERINFO");
        final String id = temp[0];
        final String nickname = temp[2];

        holder.background.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(holder.itemView.getContext().getApplicationContext(), CalendarDiaryDayInfo_act.class);
                intent.putExtra("no", item.getNo());
                intent.putExtra("year", item.getYear());
                intent.putExtra("month", item.getMonth());
                intent.putExtra("date", item.getDate());
                intent.putExtra("id", id);
                intent.putExtra("nickname", nickname);
                holder.itemView.getContext().getApplicationContext().startActivity(intent);
            }
        });

    }

    // view 연동.
    public static class ItemRowHolder extends RecyclerView.ViewHolder {
        TextView title, content, date, time;
        ImageView color;
        RelativeLayout background;


        public ItemRowHolder(View itemView) {
            super(itemView);
            color = itemView.findViewById(R.id.diary_color_item);
            title = itemView.findViewById(R.id.diary_title);
            content = itemView.findViewById(R.id.diary_content);
            date = itemView.findViewById(R.id.diary_date);
            time = itemView.findViewById(R.id.diary_time);
            background = itemView.findViewById(R.id.calendar_diary_item_background);


        }
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

}
