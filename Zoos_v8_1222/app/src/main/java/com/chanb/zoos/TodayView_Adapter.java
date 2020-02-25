package com.chanb.zoos;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.chanb.zoos.utils.Database;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TodayView_Adapter extends RecyclerView.Adapter<TodayView_Adapter.ViewHolder> {

    private final List<DiaryItem> mDataList;

    public TodayView_Adapter(List<DiaryItem> dataList) {
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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.calendar_today_item, parent, false);
        return new ViewHolder(view);
    }

    //item view에 레이아웃 연결.
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        DiaryItem item = mDataList.get(position);

        String title = item.getTitle();
        String date = item.getDate();
        String time = item.getTime();
        String year = item.getYear();
        String month = item.getMonth();
        String check = item.getCheck();


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

            if (check.equals("true")) {
                //삭선 제거
                holder.title.setText(title);
                holder.time.setText(month + "." + date + " " + time + " (" + date_day + ")");
            } else {
                //삭선
                holder.title.setText(title);
                holder.time.setText(month + "." + date + " " + time + "(" + date_day + ")");
                holder.title.setPaintFlags(holder.title.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                holder.time.setPaintFlags(holder.time.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                holder.title.setTextColor(Color.GRAY);
                holder.time.setTextColor(Color.GRAY);
                holder.check.setImageResource(R.drawable.check);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


        try {
            btn(holder, item);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    //추천 및 해당 이미지 클릭 시 intent act 옮기는 처리 메소드.
    private void btn(final ViewHolder holder2, final DiaryItem item2) {

        holder2.check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String seen = item2.getCheck();
                if (seen.equals("true")) {
                    //삭선
                    holder2.title.setPaintFlags(holder2.title.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    holder2.time.setPaintFlags(holder2.time.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    holder2.title.setTextColor(Color.GRAY);
                    holder2.time.setTextColor(Color.GRAY);
                    holder2.check.setImageResource(R.drawable.check);
                    item2.setCheck("false");
                    update_check(item2);
                } else if (seen.equals("false")) {
                    holder2.title.setPaintFlags(0);
                    holder2.time.setPaintFlags(0);
                    //android:textColor="#262626"
                    holder2.title.setTextColor(Color.argb(255, 26, 26, 26));
                    //android:textColor="#3583fb"
                    holder2.time.setTextColor(Color.argb(255, 35, 83, 251));
                    holder2.check.setImageResource(R.drawable.check_2);
                    item2.setCheck("true");
                    update_check(item2);
                }
            }
        });

        holder2.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String[] temp = new String[2];
                String dbName = "UserDatabase_Zoos";
                int dataBaseVersion = 1;
                Database database = new Database(Calendar_Diary_Today_Info_Act._context, dbName, null, dataBaseVersion);
                database.init();
                temp = database.select("MEMBERINFO");

                Intent intent = new Intent(Calendar_Diary_Today_Info_Act._context, Calendar_input_act.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                        | Intent.FLAG_ACTIVITY_CLEAR_TOP
                        | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent.putExtra("id", temp[0]);
                intent.putExtra("tag", item2.getTitle());
                Calendar_Diary_Today_Info_Act._context.startActivity(intent);
            }
        });


    }


    //서버로 부터 json 받아오는 부분. 저장한 글들 받아오는 부분.
    public void update_check(final DiaryItem item2) {
        Database database;
        String dbName = "UserDatabase_Zoos";
        int dataBaseVersion = 1;
        database = new Database(Calendar_Diary_Today_Info_Act._context, dbName, null, dataBaseVersion);
        database.init();
        String[] temp = database.select("MEMBERINFO");
        final String id = temp[0];
        RequestQueue requestQueue = null;
        requestQueue = Volley.newRequestQueue(Calendar_Diary_Today_Info_Act._context);
        String url = "http://133.186.135.41/zozo_calendar_edit_check.php";
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.d("test_grid", response);
                try {

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parameters = new HashMap<String, String>();
                parameters.put("id", id);
                parameters.put("no", item2.getNo());
                parameters.put("check", item2.getCheck());
                return parameters;
            }
        };
        requestQueue.add(request);
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }


    // view 연동.
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, time;
        ImageButton check;


        public ViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title_today);
            time = itemView.findViewById(R.id.time_today);
            check = itemView.findViewById(R.id.check_today);
        }
    }


}
