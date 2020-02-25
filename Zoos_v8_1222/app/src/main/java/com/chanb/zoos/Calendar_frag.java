package com.chanb.zoos;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.chanb.zoos.utils.Database;
import com.chanb.zoos.utils.GridItemDecoration;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Calendar_frag extends Fragment {

    ViewGroup rootView;
    /**
     * 월별 캘린더 뷰 객체
     */
    RecyclerView monthView;

    /**
     * 월별 캘린더 어댑터
     */
    CalendarAdapter monthViewAdapter;

    /**
     * 월을 표시하는 텍스트뷰
     */
    TextView monthText;
    TextView monthNext, monthPrevious;
    /**
     * 현재 연도
     */
    int curYear;

    /**
     * 현재 월
     */
    int curMonth;

    String id, nickname;
    RequestQueue requestQueue;

    //띠 모양 일정 표시
    List<CalendarLineItem> dayDataList;

    ImageButton diary_frag_btn, diary_frag_today;
    FloatingActionButton testButton;
    public static Calendar_frag cf;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = (ViewGroup) inflater.inflate(R.layout.calendar_frag, container, false);
        try {
            cf = this;
            setting();
            cardConnect();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return rootView;
    }

    private void setting() {
        String[] temp = new String[2];
        String dbName = "UserDatabase_Zoos";
        int dataBaseVersion = 1;
        Database database = new Database(rootView.getContext(), dbName, null, dataBaseVersion);
        database.init();
        temp = database.select("MEMBERINFO");
        id = temp[0];
        nickname = temp[2];

        monthView = rootView.findViewById(R.id.monthView);
        //그리드매니저를 넣어주어 리니어형태가 아닌 그리드 형태로 뷰를 제공한다. spanCount 한 줄에 표시할 아이템 갯수. //스크롤 방지.
        monthView.setLayoutManager(new GridLayoutManager(rootView.getContext().getApplicationContext(), 7) {
            @Override
            public boolean canScrollVertically() { // 세로스크롤 막기
                return false;
            }

            @Override
            public boolean canScrollHorizontally() { //가로 스크롤막기
                return false;
            }
        });

        monthViewAdapter = new CalendarAdapter(rootView.getContext());
        requestQueue = Volley.newRequestQueue(rootView.getContext().getApplicationContext());

        //전체 일정 살펴보는 버튼.
        testButton = rootView.findViewById(R.id.test_button);
        testButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(rootView.getContext(), CalendarDiaryInfo_act.class);
                intent.putExtra("id", id);
                startActivity(intent);
            }
        });

        monthText = rootView.findViewById(R.id.monthText);
        monthText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                monthViewAdapter.setPresentMonth();
                setMonthText();
                cardConnect();
                //monthViewAdapter.notifyDataSetChanged();
            }
        });

        // 이전 월로 넘어가는 이벤트 처리
        monthPrevious = rootView.findViewById(R.id.monthPrevious);
        monthPrevious.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                monthViewAdapter.setPreviousMonth();
                setMonthText();
                cardConnect();
                //monthViewAdapter.notifyDataSetChanged();
            }
        });

        // 다음 월로 넘어가는 이벤트 처리
        monthNext = rootView.findViewById(R.id.monthNext);
        monthNext.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                monthViewAdapter.setNextMonth();
                setMonthText();
                cardConnect();
                //monthViewAdapter.notifyDataSetChanged();
            }
        });

        diary_frag_today = rootView.findViewById(R.id.diary_frag_today);
        diary_frag_today.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                monthViewAdapter.setPresentMonth();
                setMonthText();
                cardConnect();
                //monthViewAdapter.notifyDataSetChanged();
            }
        });

        diary_frag_btn = rootView.findViewById(R.id.diary_frag_btn);
        diary_frag_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(rootView.getContext(), Calendar_input_act.class);
                intent.putExtra("id", id);
                startActivity(intent);
            }
        });
        setMonthText();
    }

    //상단으로 스크롤
    public void setScrollView() {
        //NestedScrollView scrollView = rootView.findViewById(R.id.calendar_frag_scroll);
        //scrollView.smoothScrollTo(0, 0);
    }

    /**
     * 월 표시 텍스트 설정
     */
    private void setMonthText() {
        curYear = monthViewAdapter.getCurYear();
        curMonth = monthViewAdapter.getCurMonth();

        if ((curMonth + 1) < 10)
            monthText.setText(curYear + ".0" + (curMonth + 1));
        else
            monthText.setText(curYear + "." + (curMonth + 1));

        if (curMonth == 0) {
            monthNext.setText(curYear + ".0" + (curMonth + 2));
            curMonth = 12;
            monthPrevious.setText((curYear - 1) + "." + curMonth);
        } else {
            if (curMonth < 10)
                monthPrevious.setText(curYear + ".0" + curMonth);
            else
                monthPrevious.setText(curYear + "." + curMonth);

            int nextMonth = curMonth + 2;
            if (nextMonth >= 13)
                monthNext.setText((curYear + 1) + ".0" + 1);
            else if (nextMonth < 10)
                monthNext.setText(curYear + ".0" + nextMonth);
            else
                monthNext.setText(curYear + "." + nextMonth);
        }


    }

    //서버로부터 띠 일정 받아 오는 곳.
    public void cardConnect() {
        String url = "http://133.186.135.41/zozo_calendar_show.php";
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    doJSONParser_card(response);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(rootView.getContext(), "인터넷 접속 상태를 확인해주세요.", Toast.LENGTH_SHORT).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parameters = new HashMap<String, String>();
                int curYear = monthViewAdapter.getCurYear();
                int curMonth = monthViewAdapter.getCurMonth();
                parameters.put("id", id);
                parameters.put("year", curYear + "");
                parameters.put("month", (curMonth + 1) + "");
                return parameters;
            }
        };
        requestQueue.add(request);
    }

    // 띠 일정 표시
    public void doJSONParser_card(String response) {

        try {
            dayDataList = new ArrayList<>();
            dayDataList.clear();
            JSONObject jsonOutput = JsonUtil.getJSONObjectFrom(response);
            String state = JsonUtil.getStringFrom(jsonOutput, "state");

            if (state.equals("sus1")) {
                JSONArray jArray = new JSONObject(response).getJSONArray("diary");
                for (int i = 0; i < jArray.length(); i++) {
                    JSONObject jObject = jArray.getJSONObject(i);
                    String year = jObject.getString("year");
                    String month = jObject.getString("month");
                    String title = jObject.getString("title");
                    String date = jObject.getString("date");

                    int dateNumber = Integer.parseInt(date);
                    dayDataList.add(new CalendarLineItem(title, dateNumber));
                }
            }
            monthViewAdapter.setDataList(dayDataList);
            monthView.setAdapter(monthViewAdapter);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //frag에 intent value 주입.
    public void refresh_frag_calendar(Intent intent) {

        Bundle extras = intent.getExtras();

        if (extras != null) {
            String id_refresh = extras.getString("id");
            String nickname_refresh = extras.getString("nickname");
            id = id_refresh;
            nickname = nickname_refresh;
            //profile = profile_refresh;

            if (id == null) {
                Long id_refresh_pre = extras.getLong("id");
                id_refresh = id_refresh_pre.toString();
                id = id_refresh;
                //profile = profile_refresh;
            }

            Log.d("extras", id);
        } else {
            Log.d("extras", "extras null");
        }


    }

    public void moveAct(int position) {
        // 현재 선택한 일자 정보 표시
        CalendarItem curItem = (CalendarItem) monthViewAdapter.getItem(position);

        int year = curItem.getYear();
        int month = curItem.getMonth();
        int day = curItem.getDay();
        String year_form = String.valueOf(year);
        String month_form = String.valueOf(month);
        String day_form = String.valueOf(day);

        if (day != 0) {
            Intent intent = new Intent(rootView.getContext(), CalendarDiaryDayInfo_act.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                    | Intent.FLAG_ACTIVITY_CLEAR_TOP
                    | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            intent.putExtra("id", id);
            intent.putExtra("nickname", nickname);
            intent.putExtra("year", year_form);
            intent.putExtra("month", month_form);
            intent.putExtra("date", day_form);

            startActivity(intent);
        }

    }
}

