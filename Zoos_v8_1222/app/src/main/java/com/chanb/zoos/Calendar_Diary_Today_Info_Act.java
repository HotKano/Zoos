package com.chanb.zoos;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
import com.chanb.zoos.utils.Database;
import com.kakao.kakaolink.v2.KakaoLinkResponse;
import com.kakao.kakaolink.v2.KakaoLinkService;
import com.kakao.message.template.ButtonObject;
import com.kakao.message.template.ContentObject;
import com.kakao.message.template.FeedTemplate;
import com.kakao.message.template.LinkObject;
import com.kakao.network.ErrorResult;
import com.kakao.network.callback.ResponseCallback;
import com.kakao.util.exception.KakaoException;
import com.kakao.util.helper.log.Logger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Calendar_Diary_Today_Info_Act extends AppCompatActivity {

    String TAG = "HappyNaru";

    RequestQueue requestQueue;
    String id, nickname;
    ImageButton back;

    //상단 일정 표시.
    List<DiaryItem> dataList;
    TodayView_Adapter adapter;
    RecyclerView recyclerView;

    //하단 추천 케어 설정 임시 더미.
    GridView_Adapter adapter_grid;
    List<GridItem> dataList_Grid;
    RecyclerView recyclerGridView;

    GlobalApplication globalApplication;

    TextView date_today, emptyText;
    RelativeLayout more_care_layout_today;
    Button later_calling_view;

    Database database;
    public static Calendar_Diary_Today_Info_Act _context;

    //공유 버튼
    ImageButton share_today;
    NotificationManager manager;
    CustomDialog_Main main;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calendar_diary_today_info_act);

        try {
            _context = this;
            setting();
            setting_notification();
            gridConnect();
            careConnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setting() {
        String dbName = "UserDatabase_Zoos";
        int dataBaseVersion = 1;
        database = new Database(this, dbName, null, dataBaseVersion);
        database.init();
        String[] temp = database.select("MEMBERINFO");

        Intent getIntent = getIntent();
        if (getIntent.getStringExtra("kind") != null) {
            id = getIntent.getStringExtra("id");
            nickname = temp[2];
        } else if (temp != null) {
            id = temp[0];
            nickname = temp[2];
        }

        date_today = findViewById(R.id.date_Today_info);
        globalApplication = new GlobalApplication();
        globalApplication.getWindow(this);
        requestQueue = Volley.newRequestQueue(this);

        //상단 일정
        recyclerView = findViewById(R.id.recycle_diary_Today_info);
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

        //하단 추천 케어 설정
        recyclerGridView = findViewById(R.id.recycle_diary_Today_care);
        RecyclerView.LayoutManager gridManager = new LinearLayoutManager(this);
        recyclerGridView.setLayoutManager(gridManager);
        recyclerGridView.setItemAnimator(null);
        recyclerGridView.stopNestedScroll();

        back = findViewById(R.id.backButton_calendar_TodayInfo);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        more_care_layout_today = findViewById(R.id.more_care_layout_today);
        more_care_layout_today.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Calendar_Diary_Today_Info_Act.this, More_care_act.class);
                intent.putExtra("id", id);
                intent.putExtra("nickname", nickname);
                startActivity(intent);
            }
        });

        share_today = findViewById(R.id.diary_frag_btn_today);
        share_today.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String url = "http://133.186.135.41/zozoSNS/" + "onlylogo.png";
                    FeedTemplate params = FeedTemplate
                            .newBuilder(ContentObject.newBuilder("알려쥬",
                                    url,
                                    LinkObject.newBuilder().setWebUrl("https://developers.kakao.com")
                                            .setMobileWebUrl("https://developers.kakao.com").build())
                                    .setDescrption(id + "님이 일정을 공유하였습니다.")
                                    .build())
                            //좋아요 등
                            //.setSocial(SocialObject.newBuilder().setLikeCount(10).setCommentCount(20)
                            //.setSharedCount(30).setViewCount(40).build())
                            .addButton(new ButtonObject("앱에서 보기", LinkObject.newBuilder()
                                    .setWebUrl("'https://developers.kakao.com")
                                    .setMobileWebUrl("'https://developers.kakao.com")
                                    .setAndroidExecutionParams("kind=today" + "&target_id=" + id)
                                    .build()))
                            .build();
                    Map<String, String> serverCallbackArgs = new HashMap<String, String>();
                    serverCallbackArgs.put("user_id", "${current_user_id}");
                    serverCallbackArgs.put("product_id", "${shared_product_id}");

                    KakaoLinkService.getInstance().sendDefault(getApplicationContext(), params, serverCallbackArgs, new ResponseCallback<KakaoLinkResponse>() {
                        @Override
                        public void onFailure(ErrorResult errorResult) {
                            Logger.e(errorResult.toString());
                        }

                        @Override
                        public void onSuccess(KakaoLinkResponse result) {
                            // 템플릿 밸리데이션과 쿼터 체크가 성공적으로 끝남. 톡에서 정상적으로 보내졌는지 보장은 할 수 없다. 전송 성공 유무는 서버콜백 기능을 이용하여야 한다.
                        }
                    });
                } catch (KakaoException k) {
                    k.printStackTrace();
                }
            }
        });

        //일정이 없습니다.
        //필수체크케어리스트 또는 추천케어를 참고하여 반려동물에게 케어를 선물해주세요
        String text = "일정이 없습니다 <br> <font color=#3583fb>필수체크케어리스트</font> 또는 <font color=#3583fb>추천케어</font>를 참고하여 반려동물에게 케어를 선물해주세요";
        emptyText = findViewById(R.id.emptyText_today);
        emptyText.setText(Html.fromHtml(text));

        SpannableString content = new SpannableString("잠시후 다시 알려주세요");
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        later_calling_view = findViewById(R.id.later_calling_today);
        later_calling_view.setText(content);
        later_calling_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                main = new CustomDialog_Main(Calendar_Diary_Today_Info_Act.this);
                main.setCancelable(true);
                main.show();

                main.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        int min = main.getMin();
                        if (min != 0)
                            setAlarm(min);
                    }
                });
            }
        });
    }

    //서버로 부터 json 받아오는 부분. 저장한 글들 받아오는 부분.
    public void gridConnect() {
        String url = "http://133.186.135.41/zozo_calendar_day_show.php";
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.d("test_grid", response);
                try {
                    doJSONParser_card(response);
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
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int date = calendar.get(Calendar.DATE);

                String yearForm = String.valueOf(year);
                String monthForm = String.valueOf(month + 1);
                String dateForm = String.valueOf(date);

                parameters.put("id", id);
                parameters.put("kind", "today");
                parameters.put("year", yearForm);
                parameters.put("month", monthForm);
                parameters.put("date", dateForm);
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
                    String check = jObject.getString("_check");
                    String code = jObject.getString("requestCode");
                    dataList.add(new DiaryItem(no, title, content, year, month, date, time, check, code));
                    int yearForm = Integer.valueOf(year);
                    int monthForm = Integer.valueOf(month);
                    int dateForm = Integer.valueOf(date);

                    String dayOfWeek = getDateDay(yearForm, monthForm - 1, dateForm);
                    date_today.setText(number(month) + "월 " + number(date) + "일 " + "(" + dayOfWeek + ")");
                }
                recyclerView.setVisibility(View.VISIBLE);
                emptyText.setVisibility(View.GONE);

            } else {
                JSONArray jArray = new JSONObject(response).getJSONArray("diary");
                JSONObject jObject = jArray.getJSONObject(0);
                String year = jObject.getString("year");
                String month = jObject.getString("month");
                String date = jObject.getString("date");
                int yearForm = Integer.valueOf(year);
                int monthForm = Integer.valueOf(month);
                int dateForm = Integer.valueOf(date);
                String dayOfWeek = getDateDay(yearForm, monthForm - 1, dateForm);
                date_today.setText(number(month) + "월 " + number(date) + "일 " + "(" + dayOfWeek + ")");
                recyclerView.setVisibility(View.GONE);
                emptyText.setVisibility(View.VISIBLE);
            }

            adapter = new TodayView_Adapter(dataList);
            recyclerView.setAdapter(adapter);
            recyclerView.setNestedScrollingEnabled(false);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //서버로 부터 json 받아오는 부분. 하단 추천 케어
    public void careConnect() {
        String SNSUrl = "http://133.186.135.41/zozo_sns_grid_show.php";
        StringRequest request = new StringRequest(Request.Method.POST, SNSUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.d("Grid", response);
                try {

                    doJSONParser_care(response);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }


        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "인터넷 접속 상태를 확인해주세요. 케어", Toast.LENGTH_SHORT).show();
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
    public void doJSONParser_care(String response) {
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
    public void onBackPressed() {
        Intent intent = new Intent(Calendar_Diary_Today_Info_Act.this, Main_act.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra("page", "3");
        intent.putExtra("kind", "care");
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        String test = database.getDatabaseName();
        Log.d("calendar_today_database", test + "*****");
        if (database != null)
            database.close();

        if (main != null)
            main.dismiss();
    }

    public void setting_notification() {

        manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        //오레오에서는 알림채널을 매니저에 생성해야 한다.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            manager.createNotificationChannel(new NotificationChannel("default3", "기본채널", NotificationManager.IMPORTANCE_HIGH));
        } else {
            manager.createNotificationChannel(new NotificationChannel("default3", "기본채널", NotificationManager.IMPORTANCE_HIGH));
            NotificationChannel channel = new NotificationChannel("Zoos_channel3", "기본채널", NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("기본채널입니다.");
            channel.enableLights(true);
            channel.setLightColor(Color.RED);
        }

    }

    private void setAlarm(int min) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(this, AlarmReceiver.class);
        intent.putExtra("title", "오늘의 케어리스트 도착입니다.");
        intent.putExtra("type", "once");
        intent.putExtra("code", "998998");
        Calendar calendar;
        calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.add(Calendar.MINUTE, min);
        Log.d(TAG, min + "setAlarm");
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 998998, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
    }

    private String number(String number) {
        int number_temp = Integer.valueOf(number);
        String result_temp;
        if (number_temp < 10) {
            result_temp = "0" + number_temp;
        } else {
            result_temp = String.valueOf(number_temp);
        }

        return result_temp;
    }


}
