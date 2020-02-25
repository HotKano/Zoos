package com.chanb.zoos;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
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

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class Main_act extends AppCompatActivity {

    RequestQueue requestQueue;
    FrameLayout frameLayout;
    TabLayout tabs;
    SNS_frag frag_home;
    Profile_frag frag_profile;
    Calendar_frag frag_diary;
    MessageList_Frag frag_chat;
    Save_frag save_frag;
    Database database;
    int page;
    public static Main_act _Main_act;
    public String id, type;
    GlobalApplication globalApplication;
    String nickname;

    int backButton = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_act);

        try {
            _Main_act = this;
            Log.d("MAIN_ACT", "active");
            setting();
            setFrag();
            setTabs();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    //뷰 세팅
    private void setting() {
        requestQueue = Volley.newRequestQueue(getApplicationContext());
        globalApplication = new GlobalApplication();
        globalApplication.getWindow(this);
        String dbName = "UserDatabase_Zoos";
        int dataBaseVersion = 1;
        database = new Database(this, dbName, null, dataBaseVersion);
        database.init();
        String[] temp = database.select("MEMBERINFO");

        Intent intent = getIntent();
        type = intent.getStringExtra("Type");
        page = 1;

        //id = intent.getStringExtra("id");
        //nickname = intent.getStringExtra("nickname");

        id = temp[0];
        nickname = temp[2];
    }

    // 프레그먼트 세팅
    public void setFrag() {
        frameLayout = findViewById(R.id.container_main);
        frag_home = new SNS_frag(); // 메인 화면
        frag_profile = new Profile_frag(); // 환경설정 화면
        frag_diary = new Calendar_frag(); // 캘린더 일정 화면
        frag_chat = new MessageList_Frag(); // 메세지 리스트 화면
        save_frag = new Save_frag(); // 세이브 화면

        Intent intent = getIntent();
        if (intent != null) {
            frag_home.refresh_frag1(intent);
            frag_profile.refresh_frag_info(intent);
            frag_diary.refresh_frag_calendar(intent);
            save_frag.refresh_frag_save(intent);
        }

        FragmentTransaction tf = getSupportFragmentManager().beginTransaction().replace(R.id.container_main, frag_home);
        tf.commitNowAllowingStateLoss();

    }

    //하단 tabs.
    public void setTabs() {
        page = 0;
        tabs = findViewById(R.id.tabs);
        int color_blue = getResources().getColor(R.color.start_with_phone_color);
        View view1 = getLayoutInflater().inflate(R.layout.custom_tab, null);
        view1.findViewById(R.id.icon).setBackgroundResource(R.drawable.home_main_tab_on);
        TextView title1 = view1.findViewById(R.id.title_tab);
        title1.setText("홈");
        title1.setTextColor(color_blue);
        tabs.addTab(tabs.newTab().setCustomView(view1));

        View view2 = getLayoutInflater().inflate(R.layout.custom_tab, null);
        view2.findViewById(R.id.icon).setBackgroundResource(R.drawable.save_tab);
        TextView title2 = view2.findViewById(R.id.title_tab);
        title2.setText("저장소");
        tabs.addTab(tabs.newTab().setCustomView(view2));

        View view3 = getLayoutInflater().inflate(R.layout.custom_tab, null);
        view3.findViewById(R.id.icon).setBackgroundResource(R.drawable.message_main_tab);
        TextView title3 = view3.findViewById(R.id.title_tab);
        title3.setText("메시지");
        tabs.addTab(tabs.newTab().setCustomView(view3));

        View view4 = getLayoutInflater().inflate(R.layout.custom_tab, null);
        view4.findViewById(R.id.icon).setBackgroundResource(R.drawable.profile_tab);
        TextView title4 = view4.findViewById(R.id.title_tab);
        title4.setText("프로필");
        tabs.addTab(tabs.newTab().setCustomView(view4));

        tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                int color = getResources().getColor(R.color.tab_on);
                Fragment selected = null;
                if (position == 0) { // 홈
                    selected = frag_home;
                    tabs.getTabAt(position).getCustomView().findViewById(R.id.icon).setBackgroundResource(R.drawable.home_main_tab_on);
                    TextView title3 = tabs.getTabAt(position).getCustomView().findViewById(R.id.title_tab);
                    title3.setTextColor(color);
                    page = 0;
                    backButton = 1;
                } else if (position == 1) { //저장소
                    selected = save_frag;
                    tabs.getTabAt(position).getCustomView().findViewById(R.id.icon).setBackgroundResource(R.drawable.save_tab_on);
                    TextView title3 = tabs.getTabAt(position).getCustomView().findViewById(R.id.title_tab);
                    title3.setTextColor(color);
                    page = 1;
                    backButton = 1;
                } else if (position == 2) { //메시지
                    selected = frag_chat;
                    tabs.getTabAt(position).getCustomView().findViewById(R.id.icon).setBackgroundResource(R.drawable.message_main_tab_on);
                    TextView title3 = tabs.getTabAt(position).getCustomView().findViewById(R.id.title_tab);
                    title3.setTextColor(color);
                    page = 2;
                    backButton = 1;
                } else if (position == 3) { //프로필
                    //selected = frag_home;
                    //tabs.getTabAt(position).getCustomView().findViewById(R.id.icon).setBackgroundResource(R.drawable.profile_tab_on);
                    //TextView title3 = tabs.getTabAt(position).getCustomView().findViewById(R.id.title_tab);
                    //title3.setTextColor(color);
                    petConnect();
                    tab = tabs.getTabAt(page);
                    tab.select();
                    backButton = 1;
                }

                if (position != 3) {
                    FragmentTransaction tf = getSupportFragmentManager().beginTransaction().replace(R.id.container_main, selected);
                    tf.commitNowAllowingStateLoss();
                }


            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

                int position = tab.getPosition();
                if (position == 0) { // 홈
                    tabs.getTabAt(position).getCustomView().findViewById(R.id.icon).setBackgroundResource(R.drawable.home_main_tab);
                    TextView title3 = tabs.getTabAt(position).getCustomView().findViewById(R.id.title_tab);
                    int color = getResources().getColor(R.color.sns_act_color);
                    title3.setTextColor(color);
                } else if (position == 1) { //저장소
                    tabs.getTabAt(position).getCustomView().findViewById(R.id.icon).setBackgroundResource(R.drawable.save_tab);
                    TextView title3 = tabs.getTabAt(position).getCustomView().findViewById(R.id.title_tab);
                    int color = getResources().getColor(R.color.sns_act_color);
                    title3.setTextColor(color);
                } else if (position == 2) { //메시지
                    tabs.getTabAt(position).getCustomView().findViewById(R.id.icon).setBackgroundResource(R.drawable.message_main_tab);
                    TextView title3 = tabs.getTabAt(position).getCustomView().findViewById(R.id.title_tab);
                    int color = getResources().getColor(R.color.sns_act_color);
                    title3.setTextColor(color);
                } else if (position == 3) { //프로필
                    tabs.getTabAt(position).getCustomView().findViewById(R.id.icon).setBackgroundResource(R.drawable.profile_tab);
                    TextView title3 = tabs.getTabAt(position).getCustomView().findViewById(R.id.title_tab);
                    int color = getResources().getColor(R.color.sns_act_color);
                    title3.setTextColor(color);
                }

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                int position = tab.getPosition();

                if (position == 0) {
                    frag_home.setScrollView();
                } else if (position == 1) {
                    //frag_diary.setScrollView();
                    save_frag.setScrollView();
                } else if (position == 2) {
                    frag_chat.setScrollView();
                } else if (position == 3) {

                }
            }
        });
    }

    //펫 정보 받아오는 부분.
    public void petConnect() {
        String SNSUrl = "http://133.186.135.41/zozo_edit_pet_show.php";
        StringRequest request = new StringRequest(Request.Method.POST, SNSUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonOutput = JsonUtil.getJSONObjectFrom(response);
                    String text = JsonUtil.getStringFrom(jsonOutput, "state");
                    if (text.equals("fail")) {
                        Intent intent = new Intent(Main_act.this, Pet_edit_act.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                                | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        intent.putExtra("id", id);
                        intent.putExtra("kind", "0");
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent(Main_act.this, Story_act.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                                | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        intent.putExtra("id", id);
                        intent.putExtra("target_id", id);
                        intent.putExtra("nickname", nickname);
                        startActivity(intent);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(Main_act.this, "인터넷 접속 상태를 확인해주세요.", Toast.LENGTH_SHORT).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parameters = new HashMap<String, String>();
                parameters.put("id", id);
                parameters.put("petNo", "1");
                return parameters;
            }
        };
        requestQueue.add(request);
    }

    // date to frag.
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        try {
            Bundle extras = intent.getExtras();

            if (extras != null) {
                frag_home.requestQueue = requestQueue;
                frag_diary.requestQueue = requestQueue;
                frag_home.id = id;
                frag_profile.id = id;
                frag_diary.id = id;
                String id_after = extras.getString("id");
                String nickname_after = extras.getString("nickname");

                if (!TextUtils.isEmpty(id_after)) {
                    id = id_after;
                    frag_home.id = id;
                    frag_profile.id = id;
                    frag_diary.id = id;
                }

                if (!TextUtils.isEmpty(nickname_after)) {
                    nickname = nickname_after;
                    frag_home.nickname = nickname;
                }

                String page = extras.getString("page");
                long id_kakao_pre = extras.getLong("id", -1);
                String id_naver_pre = extras.getString("id_naver");

                if (id_kakao_pre != -1) {
                    id_after = String.valueOf(id_kakao_pre);
                    id = id_after;
                    frag_home.id = id;
                    Log.d("kakao_login", frag_home.id + "tttt");
                } else if (id_naver_pre != null) {
                    id = id_naver_pre;
                    frag_home.id = id;
                    Log.d("naver_login", frag_home.id + "tttt");
                }

                if (page != null) {

                    if (page.equals("1")) {
                        String id = extras.getString("id");
                        String nickname = extras.getString("nickname");
                        frag_home.id = id;
                        frag_home.nickname = nickname;
                        frag_home.nickname_main.setText(nickname);
                        frag_home.cardConnect();
                        frag_home.gridConnect();
                        //getSupportFragmentManager().beginTransaction().replace(R.id.container_main, frag_home).commit();
                    } else if (page.equals("2")) {
                        frag_diary.id = id;
                        frag_diary.nickname = nickname;
                        frag_diary.cardConnect();
                        //frag_diary.monthViewAdapter.notifyDataSetChanged();
                    } else if (page.equals("3")) {
                        Log.d("frag_chat", "act");
                        String kind = extras.getString("kind");
                        frag_chat.refresh_frag_chatting(kind);
                    } else if (page.equals("4")) {
                        frag_profile.petConnect();
                        frag_profile.adapter_pet.notifyDataSetChanged();
                    }
                }

            } else {
                Log.d("test_for_Main", "null");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onBackPressed() {
        if (backButton == 1) {
            View view = findViewById(R.id.main_act);
            Snackbar snackbar = Snackbar
                    .make(view, "한 번 더 누르시면 종료 됩니다.", Snackbar.LENGTH_SHORT)
                    .setActionTextColor(Color.WHITE);
            View sbView = snackbar.getView();
            sbView.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.start_with_phone_color));
            snackbar.show();
            backButton = 2;
        } else {
            super.onBackPressed();
        }

    }

}
