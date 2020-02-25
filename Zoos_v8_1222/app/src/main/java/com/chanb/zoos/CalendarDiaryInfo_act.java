package com.chanb.zoos;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class CalendarDiaryInfo_act extends AppCompatActivity {

    RequestQueue requestQueue;
    String id;
    ImageButton back, diary_info_act_input;

    ArrayList<CalendarDiaryItem_Total> dataList_total;
    ArrayList<CalendarDiaryItem_Case> dataList_case;
    CalendarDiary_Adapter_Total adapter_total;
    RecyclerView recyclerView;
    GlobalApplication globalApplication;
    LinearLayout null_layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calendar_diary_info_act);

        try {
            setting();
            gridConnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setting() {
        null_layout = findViewById(R.id.msg_null_info);
        id = getIntent().getStringExtra("id");
        globalApplication = new GlobalApplication();
        globalApplication.getWindow(this);
        requestQueue = Volley.newRequestQueue(this);
        recyclerView = findViewById(R.id.calendar_diary_recycle);
        RecyclerView.LayoutManager LinearManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(LinearManager);
        recyclerView.setAnimation(null);
        recyclerView.setLayoutManager(new LinearLayoutManager(this) {
            @Override
            public boolean canScrollVertically() { // 세로스크롤 막기
                return false;
            }

            @Override
            public boolean canScrollHorizontally() { //가로 스크롤막기
                return false;
            }
        });
        back = findViewById(R.id.backButton_diary_info);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        diary_info_act_input = findViewById(R.id.diary_info_act_input);
        diary_info_act_input.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CalendarDiaryInfo_act.this, Calendar_input_act.class);
                intent.putExtra("id", id);
                startActivity(intent);
                finish();
            }
        });


    }

    //서버로 부터 json 받아오는 부분. 저장한 글들 받아오는 부분.
    public void gridConnect() {
        String SNSUrl = "http://133.186.135.41/zozo_calendar_diary_show.php";
        StringRequest request = new StringRequest(Request.Method.POST, SNSUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {


                Log.d("test_grid", response);
                try {
                    JSONObject jsonOutput = JsonUtil.getJSONObjectFrom(response);
                    String total = JsonUtil.getStringFrom(jsonOutput, "total");

                    if(total.equals("fail")){
                        null_layout.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                    } else {
                        doJSONParser_grid(response);
                        null_layout.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "인터넷 접속 상태를 확인해주세요.", Toast.LENGTH_SHORT).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parameters = new HashMap<String, String>();
                parameters.put("id", id);
                return parameters;
            }
        };
        requestQueue.add(request);
    }

    //실제 레이아웃 구현 스토리 일 별 뷰.
    public void doJSONParser_grid(String response) {

        try {
            JSONArray jArray = new JSONObject(response).getJSONArray("month");
            JSONObject jsonOutput = JsonUtil.getJSONObjectFrom(response);
            String total = JsonUtil.getStringFrom(jsonOutput, "total");

            dataList_total = new ArrayList<>();

            for (int i = 0; i < jArray.length(); i++) {

                CalendarDiaryItem_Total si = new CalendarDiaryItem_Total();
                JSONObject iObject = jArray.getJSONObject(i);

                String year_month = iObject.getString("year");
                String month_month = iObject.getString("month");
                String date_month = iObject.getString("date");

                int year_input = Integer.valueOf(year_month);
                int month_input = Integer.valueOf(month_month);
                int date_input = Integer.valueOf(date_month);

                if (month_input < 10)
                    month_month = "0" + month_month;

                if (date_input < 10)
                    date_month = "0" + date_month;

                String day = getDateDay(year_input, month_input-1, date_input);

                String date_form = year_month + "." + month_month + "." + date_month + " (" + day + ")";
                si.setDate(date_form);

                int value = iObject.getInt("value");
                //가로 이미지 뷰를 위한 리셋.
                dataList_case = new ArrayList<>();

                for (int j = 0; j < value; j++) {
                    JSONArray jArray2 = new JSONObject(response).getJSONArray("data" + i);
                    JSONObject jObject = jArray2.getJSONObject(j);
                    String no = jObject.getString("no");
                    String title = jObject.getString("title");
                    String content = jObject.getString("content");

                    String year = jObject.getString("year");
                    String month = jObject.getString("month");
                    String date_data = jObject.getString("date");
                    String time = jObject.getString("time");
                    String code = jObject.getString("requestCode");

                    //String no, String title, String content, String year, String month, String date, String time
                    dataList_case.add(new CalendarDiaryItem_Case(no, title, content, year, month, date_data, time, code));

                }
                si.setImgView(dataList_case);
                dataList_total.add(si);

            }

            //total 값을 받아와 set.
            adapter_total = new CalendarDiary_Adapter_Total(getApplicationContext(), dataList_total);
            recyclerView.setAdapter(adapter_total);


        } catch (JSONException e) {
            e.printStackTrace();
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
