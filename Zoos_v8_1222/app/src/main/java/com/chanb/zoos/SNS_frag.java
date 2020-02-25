package com.chanb.zoos;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageButton;

import android.widget.LinearLayout;

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
import com.chanb.zoos.utils.EqualSpacingItemDecoration;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SNS_frag extends Fragment {

    TextView nickname_main, notice;
    ViewGroup rootView;
    String id, nickname;
    RequestQueue requestQueue;

    //상단 인기케어 설정
    GridView_Adapter adapter_grid;
    List<GridItem> dataList_Grid;

    //하단 인기케어 설정
    CardView_Adapter adapter;
    List<CardItem> dataList;

    RecyclerView recyclerView, recyclerGridView;
    NestedScrollView scrollView;
    ImageButton alarmSettingBtn;

    //SwipeRefreshLayout swipe;
    public static SNS_frag _SNS_frag;
    int page_state = 0;

    RelativeLayout more_care_layout;
    String TAG = "SNS_FRAG";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = (ViewGroup) inflater.inflate(R.layout.sns_frag, container, false);
        Log.d("FRAG_SNS", "ACTIVE");
        try {
            setting();
            cardConnect();
            gridConnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rootView;
    }

    //화면 총 세팅.
    private void setting() {
        requestQueue = Volley.newRequestQueue(rootView.getContext().getApplicationContext());
        scrollView = rootView.findViewById(R.id.sns_scroll);
        more_care_layout = rootView.findViewById(R.id.more_care_layout);
        more_care_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(rootView.getContext(), More_care_act.class);
                intent.putExtra("id", id);
                intent.putExtra("nickname", nickname);
                startActivity(intent);
            }
        });
        _SNS_frag = this;

        String[] temp = new String[2];
        String dbName = "UserDatabase_Zoos";
        int dataBaseVersion = 1;
        Database database = new Database(rootView.getContext(), dbName, null, dataBaseVersion);
        database.init();
        temp = database.select("MEMBERINFO");
        id = temp[0];
        nickname = temp[2];

        nickname_main = rootView.findViewById(R.id.nickname_main);
        nickname_main.setText(nickname);

        alarmSettingBtn = rootView.findViewById(R.id.to_alarm_setting);
        alarmSettingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(rootView.getContext().getApplicationContext(), Calendar_input_act.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                        | Intent.FLAG_ACTIVITY_CLEAR_TOP
                        | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent.putExtra("id", id);
                startActivity(intent);
            }
        });

        //하단 인기케어 설정
        recyclerView = rootView.findViewById(R.id.recycle_main);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(rootView.getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAnimation(null);
        //스크롤 방지.
        recyclerView.setLayoutManager(new LinearLayoutManager(rootView.getContext().getApplicationContext()) {
            @Override
            public boolean canScrollVertically() { // 세로스크롤 막기
                return false;
            }

            @Override
            public boolean canScrollHorizontally() { //가로 스크롤막기
                return false;
            }
        });

        //상단 케어 설정
        recyclerGridView = rootView.findViewById(R.id.recycle_grid_main);
        RecyclerView.LayoutManager gridManager = new LinearLayoutManager(rootView.getContext());
        ((LinearLayoutManager) gridManager).setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerGridView.setLayoutManager(gridManager);
        recyclerGridView.setItemAnimator(null);

        //공간여백 만들어주는 기본 클래스. 간격을 매개변수로 받는다. 추가적으로 형태도 받음. Horizontal, Linear, Grid
        recyclerGridView.addItemDecoration(new EqualSpacingItemDecoration(12, LinearLayout.HORIZONTAL));

        //@님이 그냥, 좋아서 알려쥬는 소식
        notice = rootView.findViewById(R.id.notice_img_sns);
        notice.setText(nickname + "님이 그냥, \n좋아서 알려쥬는 소식~");


    }

    //상단으로 스크롤
    public void setScrollView() {
        scrollView.smoothScrollTo(0, 0);
    }

    //서버로 부터 json 받아오는 부분. 상단 인기케어
    public void gridConnect() {
        String SNSUrl = "http://133.186.135.41/zozo_sns_grid_show.php";
        StringRequest request = new StringRequest(Request.Method.POST, SNSUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("Grid", response);
                try {
                    doJSONParser_grid(response);
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
                parameters.put("id", id);
                return parameters;
            }
        };
        requestQueue.add(request);
    }

    //실제 레이아웃 구현 상단 인기케어
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

    //서버로 부터 json 받아오는 부분. 하단 인기케어
    public void cardConnect() {
        String url = "http://133.186.135.41/zozo_sns_card_show.php";
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
                //swipe.setRefreshing(false);
                Toast.makeText(rootView.getContext(), "인터넷 접속 상태를 확인해주세요.", Toast.LENGTH_SHORT).show();
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

    // 카드뷰에 아이템 셋트하는 메소드. 하단 인기케어
    public void doJSONParser_card(String response) {
        try {
            JSONArray jArray = new JSONArray(response);
            dataList = new ArrayList<>();
            for (int i = 0; i < jArray.length(); i++) {
                JSONObject jObject = jArray.getJSONObject(i);
                String title = jObject.getString("title");
                String writer = jObject.getString("writer");
                String timer = jObject.getString("time");
                String like = jObject.getString("like");
                String watch = jObject.getString("view");
                String img = jObject.getString("img1");
                String no = jObject.getString("no");
                String like_check = jObject.getString("like_check");

                if (like_check.equals("")) {
                    dataList.add(new CardItem(title, writer, timer, like, watch, img, no, "null"));
                } else {
                    dataList.add(new CardItem(title, writer, timer, like, watch, img, no, like_check));
                }

            }
            adapter = new CardView_Adapter(dataList);
            recyclerView.setAdapter(adapter);
            recyclerView.setNestedScrollingEnabled(false);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // 계속해서 불러오기에 필요한 메소드. 현재는 사용하지 않음.
    public void cardConnect2() {
        String url = "http://133.186.135.41/zozo_sns_card_addtion.php";
        requestQueue = Volley.newRequestQueue(rootView.getContext().getApplicationContext());
        StringRequest request2 = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    //Log.d("card2", response);
                    doJSONParser_card2(response);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //swipe.setRefreshing(false);
                Toast.makeText(rootView.getContext(), "인터넷 접속 상태를 확인해주세요.", Toast.LENGTH_SHORT).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parameters = new HashMap<String, String>();

                String new_item = "5";
                String item = String.valueOf(adapter.getItemCount());
                parameters.put("id", id);
                parameters.put("no_max", item);
                parameters.put("no", new_item);
                return parameters;
            }
        };
        requestQueue.add(request2);
    }

    // 계속해서 불러오기에 아이템 셋트하는 메소드.
    public void doJSONParser_card2(String response) {

        try {
            //Log.d("card", "card2 active");

            JSONArray jArray = new JSONArray(response);

            for (int i = 0; i < jArray.length(); i++) {
                JSONObject jObject = jArray.getJSONObject(i);
                String writer = jObject.getString("no");
                String timer = jObject.getString("time");
                String like = jObject.getString("like");
                String reply = jObject.getString("reply");
                //String watch = jObject.getString("view");
                String img = jObject.getString("img1");
                String no = jObject.getString("no");
                String comment = jObject.getString("comment");
                String like_check = jObject.getString("like_check");

                if (like_check.equals("")) {
                    dataList.add(new CardItem(writer, timer, like, reply, /*watch,*/ img, no, comment, "null"));
                } else {
                    dataList.add(new CardItem(writer, timer, like, reply, /*watch,*/ img, no, comment, like_check));
                }
            }

            adapter.notifyItemRangeChanged(adapter.getItemCount(), 5);
            page_state = 0;

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //frag에 intent value 주입.
    public void refresh_frag1(Intent intent) {

        Bundle extras = intent.getExtras();

        if (extras != null) {
            String id_refresh = extras.getString("id");
            String nickname_refresh = extras.getString("nickname");
            id = id_refresh;
            nickname = nickname_refresh;

            if (id == null) {
                Long id_refresh_pre = extras.getLong("id");
                id_refresh = id_refresh_pre.toString();
                id = id_refresh;
                nickname = nickname_refresh;
            }

            //Log.d("extras", id + nickname + "*******");
        } else {
            //Log.d("extras", "extras null");
        }


    }
}
