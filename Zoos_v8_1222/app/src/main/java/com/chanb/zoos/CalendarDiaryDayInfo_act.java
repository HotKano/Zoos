package com.chanb.zoos;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CalendarDiaryDayInfo_act extends AppCompatActivity {

    GlobalApplication globalApplication;
    RequestQueue requestQueue;
    RecyclerView recyclerView;
    String id, nickname, curYear, curMonth, curDate;

    TextView total_day_info, date_day_info;

    //상단 일정 표시.
    List<DiaryItem> dataList;
    DiaryView_Adapter adapter;

    //하단 추천 케어 설정 임시 더미.
    GridView_Adapter adapter_grid;
    List<GridItem> dataList_Grid;
    RecyclerView recyclerGridView;

    ImageButton back_day_info, calendar_diary_input;

    //최 하단 버튼
    RelativeLayout more_care_layout_day;

    //emptyView
    TextView emptyView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calendar_diary_day_info_act);

        try {
            setting();
            cardConnect();
            gridConnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setting() {
        id = getIntent().getStringExtra("id");
        nickname = getIntent().getStringExtra("nickname");
        curYear = getIntent().getStringExtra("year");
        curMonth = getIntent().getStringExtra("month");
        curDate = getIntent().getStringExtra("date");
        globalApplication = new GlobalApplication();
        globalApplication.getWindow(this);
        requestQueue = Volley.newRequestQueue(this);

        //상단 일정 표시
        recyclerView = findViewById(R.id.recycle_diary_day_info);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAnimation(null);
        //자체 스크롤링 방지
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

        //하단 추천 케어 설정
        recyclerGridView = findViewById(R.id.recycle_diary_day_care);
        RecyclerView.LayoutManager gridManager = new LinearLayoutManager(this);
        recyclerGridView.setLayoutManager(gridManager);
        recyclerGridView.setItemAnimator(null);
        recyclerGridView.stopNestedScroll();

        back_day_info = findViewById(R.id.backButton_calendar_dayInfo);
        back_day_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        total_day_info = findViewById(R.id.total_day_info);
        date_day_info = findViewById(R.id.date_day_info);
        int year = Integer.valueOf(curYear);
        int month = Integer.valueOf(curMonth);
        int date = Integer.valueOf(curDate);
        String curDay = globalApplication.getDateDay(year, month - 1, date);
        date_day_info.setText(curYear + "." + curMonth + "." + curDate + "(" + curDay + ")");

        more_care_layout_day = findViewById(R.id.more_care_layout_day);
        more_care_layout_day.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CalendarDiaryDayInfo_act.this, More_care_act.class);
                intent.putExtra("id", id);
                intent.putExtra("nickname", nickname);
                startActivity(intent);
            }
        });

        calendar_diary_input = findViewById(R.id.calendar_diary_input);
        calendar_diary_input.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CalendarDiaryDayInfo_act.this, Calendar_input_act.class);
                intent.putExtra("id", id);
                startActivity(intent);
                finish();
            }
        });

        emptyView = findViewById(R.id.emptyText_today_info);
        String text = "일정이 없습니다 <br> <font color=#3583fb>필수체크케어리스트</font> 또는 <font color=#3583fb>추천케어</font>를 참고하여 반려동물에게 케어를 선물해주세요";
        emptyView.setText(Html.fromHtml(text));

    }

    //서버로 부터 json 받아오는 부분. 상단 일정
    public void cardConnect() {
        String url = "http://133.186.135.41/zozo_calendar_day_show.php";
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    //swipe.setRefreshing(false);
                    JSONObject jsonOutput = JsonUtil.getJSONObjectFrom(response);
                    String state = JsonUtil.getStringFrom(jsonOutput, "state");
                    if (state.equals("fail")) {
                        emptyView.setVisibility(View.VISIBLE);
                        total_day_info.setVisibility(View.GONE);
                    } else {
                        doJSONParser_card(response);
                        emptyView.setVisibility(View.GONE);
                        total_day_info.setVisibility(View.VISIBLE);
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
                parameters.put("year", curYear);
                parameters.put("month", curMonth);
                parameters.put("date", curDate);
                return parameters;
            }
        };
        requestQueue.add(request);
    }

    // 카드뷰에 아이템 셋트하는 메소드. 상단 일정
    public void doJSONParser_card(String response) {

        try {
            dataList = new ArrayList<>();
            JSONObject jsonOutput = JsonUtil.getJSONObjectFrom(response);
            String state = JsonUtil.getStringFrom(jsonOutput, "state");
            String total = JsonUtil.getStringFrom(jsonOutput, "total");
            total_day_info.setText("일정 " + total + "개");

            if (state.equals("sus1")) {

                JSONArray jArray = new JSONObject(response).getJSONArray("diary");
                for (int i = 0; i < jArray.length(); i++) {
                    JSONObject jObject = jArray.getJSONObject(i);
                    String no = jObject.getString("no");
                    String title = jObject.getString("title");
                    String content = jObject.getString("content");
                    String year = jObject.getString("year");
                    String month = jObject.getString("month");
                    String date = jObject.getString("date");
                    String time = jObject.getString("time");
                    String code = jObject.getString("requestCode");
                    dataList.add(new DiaryItem(no, title, content, year, month, date, time, "", code));
                }
                total_day_info.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.VISIBLE);

            } else {
                total_day_info.setText("일정 0개");
                total_day_info.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
                emptyView.setVisibility(View.GONE);
                recyclerGridView.setVisibility(View.VISIBLE);
            }

            adapter = new DiaryView_Adapter(dataList);
            recyclerView.setAdapter(adapter);
            recyclerView.setNestedScrollingEnabled(false);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //서버로 부터 json 받아오는 부분. 하단 추천 케어
    public void gridConnect() {
        String SNSUrl = "http://133.186.135.41/zozo_sns_grid_show.php";
        StringRequest request = new StringRequest(Request.Method.POST, SNSUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    doJSONParser_grid(response);
                } catch (
                        Exception e) {
                    e.printStackTrace();
                }

            }


        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //swipe.setRefreshing(false);
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

    //실제 레이아웃 구현 하단 추천 케어
    public void doJSONParser_grid(String response) {
        try {
            JSONArray jArray = new JSONArray(response);
            dataList_Grid = new ArrayList<>();
            for (int i = 0; i < jArray.length(); i++) {
                JSONObject jObject = jArray.getJSONObject(i);
                String img = jObject.getString("img1");
                String no = jObject.getString("no");
                String title = jObject.getString("title");
                String tag = jObject.getString("tag");
                String nickname_grid = nickname;
                String like_check = jObject.getString("like_check");
                dataList_Grid.add(new GridItem(img, no, title, tag, nickname_grid, like_check));
            }

            adapter_grid = new GridView_Adapter(dataList_Grid);
            recyclerGridView.setAdapter(adapter_grid);
            recyclerGridView.setNestedScrollingEnabled(false);


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
