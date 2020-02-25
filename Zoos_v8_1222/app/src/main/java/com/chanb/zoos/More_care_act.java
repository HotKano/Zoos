package com.chanb.zoos;

import android.content.Intent;
import android.graphics.Color;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageButton;
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
import com.chanb.zoos.utils.EqualSpacingItemDecoration;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class More_care_act extends AppCompatActivity {

    String id, nickname, tag;
    RequestQueue requestQueue;
    ImageButton back;
    Button tagSetting;
    NestedScrollView nestedScrollView;
    SwipeRefreshLayout swipeRefreshLayout;

    //상단 인기케어 설정
    GridView_Adapter adapter_grid;
    List<GridItem> dataList_Grid;

    RecyclerView recyclerGridView;
    GlobalApplication globalApplication;
    TextView titleView;
    int page_state = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.more_care_act);

        try {
            setting();
            scrollSetting();
            gridConnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setting() {
        requestQueue = Volley.newRequestQueue(this);
        //set System bar
        globalApplication = new GlobalApplication();
        globalApplication.getWindow(this);

        nestedScrollView = findViewById(R.id.more_care_scroll);
        swipeRefreshLayout = findViewById(R.id.more_care_swipe);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                gridConnect();
            }
        });
        //상단 케어 설정
        recyclerGridView = findViewById(R.id.more_care_recycle);
        RecyclerView.LayoutManager gridManager = new LinearLayoutManager(this);
        recyclerGridView.setLayoutManager(gridManager);
        recyclerGridView.setItemAnimator(null);
        recyclerGridView.stopNestedScroll();

        //공간여백 만들어주는 기본 클래스. 간격을 매개변수로 받는다. 추가적으로 형태도 받음. Horizontal, Linear, Grid
        recyclerGridView.addItemDecoration(new EqualSpacingItemDecoration(8, LinearLayout.HORIZONTAL));

        id = getIntent().getStringExtra("id");
        nickname = getIntent().getStringExtra("nickname");
        titleView = findViewById(R.id.moreCare_title);
        titleView.setText("짠! " + nickname + "님을 위해 골라봤어요");
        swipeRefreshLayout.setColorSchemeResources(
                R.color.start,
                R.color.start,
                R.color.start);
        int myColor = Color.parseColor("#3583fb");
        swipeRefreshLayout.setProgressBackgroundColorSchemeColor(myColor);

        back = findViewById(R.id.more_care_backButton);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        tagSetting = findViewById(R.id.more_care_tag);
        tagSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(More_care_act.this, TagSetting_act.class);
                startActivity(intent);

            }
        });

        //아래로 드래그시 새로고침.
/*        nestedScrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                if (nestedScrollView != null) {
                    if (nestedScrollView.getChildAt(0).getBottom() == (nestedScrollView.getHeight() + nestedScrollView.getScrollY())) {
                        //scroll view is at bottom
                        Log.i("state", "bottom");
                        if (page_state == 0) {
                            gridConnect();
                            page_state = 1;
                        } else {
                            page_state = 0;
                        }
                    }
                }
            }
        });*/
    }

    private void scrollSetting() {
        nestedScrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                if (nestedScrollView != null) {
                    if (nestedScrollView.getChildAt(0).getBottom() == (nestedScrollView.getHeight() + nestedScrollView.getScrollY())) {
                        //scroll view is at bottom
                        if (page_state == 0) {
                            cardConnect2();
                        }
                    }
                }

            }
        });
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
                //swipe.setRefreshing(false);
                Toast.makeText(getApplicationContext(), "인터넷 접속 상태를 확인해주세요.", Toast.LENGTH_SHORT).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parameters = new HashMap<String, String>();
                parameters.put("id", id);
                parameters.put("item", "5");
                if (tag != null)
                    parameters.put("tag", tag);
                Log.d("Grid", id);

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
                dataList_Grid.add(new GridItem(img, no, title, "", nickname_grid, like_check));
            }

            adapter_grid = new GridView_Adapter(dataList_Grid);
            recyclerGridView.setAdapter(adapter_grid);
            recyclerGridView.setNestedScrollingEnabled(false);
            swipeRefreshLayout.setRefreshing(false);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //하단 드래그 추가 불러오기.
    public void cardConnect2() {
        String url = "http://133.186.135.41/zozo_sns_card_addtion.php";
        StringRequest request2 = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    Log.d("card2", response);
                    doJSONParser_card2(response);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //swipe.setRefreshing(false);
                Toast.makeText(More_care_act.this, "인터넷 접속 상태를 확인해주세요.", Toast.LENGTH_SHORT).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parameters = new HashMap<String, String>();

                String new_item = "5";
                String item = String.valueOf(adapter_grid.getItemCount());
                Log.d("card2", item);
                parameters.put("id", id);
                parameters.put("no_max", item);
                parameters.put("item", new_item);
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
                String img = jObject.getString("img1");
                String no = jObject.getString("no");
                String title = jObject.getString("title");
                String nickname_grid = nickname;
                String like_check = jObject.getString("like_check");
                dataList_Grid.add(new GridItem(img, no, title, "", nickname_grid, like_check));
            }
            adapter_grid.notifyItemRangeChanged(adapter_grid.getItemCount(), 5);
            page_state = 0;
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent != null) {
            Bundle extras = intent.getExtras();
            tag = extras.getString("tag");
            if (!TextUtils.isEmpty(tag)) {
                gridConnect();
                Log.d("More_care_act", tag + " test");
            }

        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, Main_act.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra("page", "1");
        intent.putExtra("id", id);
        intent.putExtra("nickname", nickname);
        startActivity(intent);
        finish();
    }
}