package com.chanb.zoos;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.KeyListener;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.chanb.zoos.utils.DatabaseSearchDiary;
import com.chanb.zoos.utils.EqualSpacingItemDecoration;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class Calendar_input_act extends AppCompatActivity {

    static Calendar_input_act c;
    String id, nickname, profile, petNo, petName, petProfile, petRace;
    RequestQueue requestQueue;

    //동물 관련 어뎁터, 리스트.
    ArrayList<CalendarPetItem> dataList_pet;
    CalendarPetView_Adapter adapter_pet;

    //최근 검색어 관련 어뎁터 리스트.
    ArrayList<SearchItem> dataList_search;
    Search_Adapter adapter_search;

    RecyclerView recyclerView, recycle_search_calendar;
    GlobalApplication globalApplication;
    Button submit, info;
    EditText calendar_input_editText;
    ImageView back;
    String tag_from_care_content_act;

    //검색어 관련 DB
    DatabaseSearchDiary databaseSearchDiary;

    //pet and to do select act
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calendar_input_act);
        c = this;

        try {
            initDatabase();
            setting();
            selectSearchMessage();
            petConnect();
        } catch (Exception e) {

        }
    }

    private void setting() {
        globalApplication = new GlobalApplication();
        globalApplication.getWindow(this);


        back = findViewById(R.id.backButton_calendar_input);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        //펫 할 일 입력 에딧텍스트.
        calendar_input_editText = findViewById(R.id.calendar_input_editText);
        //more care act 에서 지정되어 있는 태그 서식 받아서 입력 폼에 입력.
        tag_from_care_content_act = getIntent().getStringExtra("tag");
        if (!TextUtils.isEmpty(tag_from_care_content_act))
            calendar_input_editText.setText(tag_from_care_content_act);
        requestQueue = Volley.newRequestQueue(this);
        recyclerView = findViewById(R.id.calendar_input_act_recycle);
        recycle_search_calendar = findViewById(R.id.recycle_search_calendar);


        //반려 동물
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.addItemDecoration(new EqualSpacingItemDecoration(20, LinearLayout.HORIZONTAL));
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setItemAnimator(null);

        //최근 검색어
        LinearLayoutManager linearLayoutManager_search = new LinearLayoutManager(this);
        linearLayoutManager_search.setOrientation(LinearLayoutManager.VERTICAL);
        recycle_search_calendar.addItemDecoration(new EqualSpacingItemDecoration(20, LinearLayout.VERTICAL));
        recycle_search_calendar.setLayoutManager(linearLayoutManager_search);
        recycle_search_calendar.setItemAnimator(null);

        id = getIntent().getStringExtra("id");

        //일반 등록
        submit = findViewById(R.id.calendar_input_submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = calendar_input_editText.getText().toString();
                if (title.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "일정을 입력해주세요.", Toast.LENGTH_SHORT).show();
                } else if (!title.isEmpty() && petNo != null && petName != null && petProfile != null && petRace != null) {
                    Calendar c = Calendar.getInstance();
                    c.setTimeInMillis(System.currentTimeMillis());
                    int year_input = c.get(Calendar.YEAR);
                    int month_input = c.get(Calendar.MONTH);
                    int day_input = c.get(Calendar.DATE);
                    int hour_input = c.get(Calendar.HOUR_OF_DAY);
                    int minute_input = c.get(Calendar.MINUTE);

                    String year_form = String.valueOf(year_input);
                    String month_form = String.valueOf(month_input);
                    String day_form = String.valueOf(day_input);
                    String hour_form = String.valueOf(hour_input);
                    String minute_form = String.valueOf(minute_input);

                    Intent intent = new Intent(Calendar_input_act.this, Calendar_upload_act.class);
                    intent.putExtra("id", id);
                    intent.putExtra("petNo", petNo);

                    intent.putExtra("title", title);
                    intent.putExtra("content", "");
                    intent.putExtra("year", year_form);
                    intent.putExtra("month", month_form);
                    intent.putExtra("date", day_form);
                    intent.putExtra("hour", hour_form);
                    intent.putExtra("minute", minute_form);

                    intent.putExtra("yearEnd", year_form);
                    intent.putExtra("monthEnd", month_form);
                    intent.putExtra("dateEnd", day_form);
                    intent.putExtra("hourEnd", hour_form);
                    intent.putExtra("minuteEnd", minute_form);

                    Log.d("input", year_form + ":" + month_form + ":" + day_form + ":" + hour_form + ":" + minute_form);

                    intent.putExtra("uploadFrom", "input");
                    insertSearchMessage(title);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), "일정을 입력해주세요.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //추가 선택 등록
        info = findViewById(R.id.calendar_info_button);
        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = calendar_input_editText.getText().toString();
                if (title.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "일정을 입력해주세요.", Toast.LENGTH_SHORT).show();
                } else if (!title.isEmpty() && petNo != null && petName != null && petProfile != null && petRace != null) {
                    Intent intent = new Intent(Calendar_input_act.this, Calendar_info_act.class);
                    intent.putExtra("id", id);
                    intent.putExtra("petNo", petNo);
                    intent.putExtra("PetName", petName);
                    intent.putExtra("petProfile", petProfile);
                    intent.putExtra("petRace", petRace);
                    intent.putExtra("title", title);
                    insertSearchMessage(title);
                    startActivity(intent);
                } else {
                    Toast.makeText(getApplicationContext(), "일정을 입력해주세요.", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    //서버로 부터 json 받아오는 부분. 펫 정보 리사이클 뷰에 정보 들어가는 부분.
    public void petConnect() {
        String SNSUrl = "http://133.186.135.41/zozo_edit_pet_show.php";
        StringRequest request = new StringRequest(Request.Method.POST, SNSUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.d("PetEdit", response);
                try {
                    doJSONParser_pet(response);
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

    //choose diary pick pet view.
    public void doJSONParser_pet(String response) {

        try {
            JSONObject jsonOutput = JsonUtil.getJSONObjectFrom(response);
            String total = JsonUtil.getStringFrom(jsonOutput, "state");
            dataList_pet = new ArrayList<>();

            if (total.equals("fail")) {

            } else if (total.equals("sus1")) {
                JSONArray jArray = new JSONObject(response).getJSONArray("data");
                for (int i = 0; i <= jArray.length(); i++) {
                    if (i == jArray.length()) {

                    } else {
                        JSONObject jObject = jArray.getJSONObject(i);
                        String petNo = jObject.getString("petNo");
                        String petProfile = jObject.getString("petProfile");
                        String petName = jObject.getString("petName");
                        String petRace = jObject.getString("petRace");

                        //petNo, petProfile, petName;
                        dataList_pet.add(new CalendarPetItem(petNo, petProfile, petName, petRace));
                    }
                }
            }
            adapter_pet = new CalendarPetView_Adapter(dataList_pet);
            recyclerView.setAdapter(adapter_pet);
            recyclerView.setNestedScrollingEnabled(false);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void insertSearchMessage(String text) {
        if (TextUtils.isEmpty(text)) {
            Toast.makeText(getApplicationContext(), "일정을 입력해주세요.", Toast.LENGTH_SHORT).show();
        } else {
            databaseSearchDiary.insert(text, "DIARYSEARCH");
        }
    }

    private void initDatabase() {
        String dbName = "SearchDatabase";
        databaseSearchDiary = new DatabaseSearchDiary(this, dbName, null, 1);
        databaseSearchDiary.init();
    }

    private void selectSearchMessage() {
        ArrayList<String> temp = databaseSearchDiary.select("DIARYSEARCH");
        dataList_search = new ArrayList<>();

        for (int i = 0; i < temp.size(); i++) {
            dataList_search.add(new SearchItem(temp.get(i)));
        }
        adapter_search = new Search_Adapter(dataList_search);
        recycle_search_calendar.setAdapter(adapter_search);
        adapter_search.notifyDataSetChanged();
    }

}
