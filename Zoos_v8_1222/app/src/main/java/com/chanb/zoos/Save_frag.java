package com.chanb.zoos;

import android.support.v4.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.chanb.zoos.utils.GridItemDecoration;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Save_frag extends Fragment {

    RequestQueue requestQueue;
    String id;
    List<SaveItem> dataList_save;
    SaveView_Adapter adapter_save;
    RecyclerView recyclerSaveView;
    ViewGroup rootView;
    public static Save_frag _Save_frag;
    LinearLayout story_null_mark;
    int page = 0;
    GlobalApplication globalApplication;
    Button all, care, story;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = (ViewGroup) inflater.inflate(R.layout.save_frag, container, false);
        _Save_frag = this;
        try {
            setting();
            //gridConnect("notice");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rootView;
    }

    private void setting() {
        requestQueue = Volley.newRequestQueue(rootView.getContext().getApplicationContext());
        recyclerSaveView = rootView.findViewById(R.id.recycle_grid_save);
        //그리드매니저를 넣어주어 리니어형태가 아닌 그리드 형태로 뷰를 제공한다. spanCount 한 줄에 표시할 아이템 갯수. //스크롤 방지.
        recyclerSaveView.setLayoutManager(new GridLayoutManager(rootView.getContext().getApplicationContext(), 2));
        //recyclerSaveView.addItemDecoration(new GridItemDecoration(2, 8, false));

        story_null_mark = rootView.findViewById(R.id.save_null);

        all = rootView.findViewById(R.id.all_save);
        all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gridConnect("all");
                page = 0;
                //@drawable/button_save
                all.setBackgroundResource(R.drawable.button_save);
                care.setBackgroundResource(R.drawable.button_save_gray);
                story.setBackgroundResource(R.drawable.button_save_gray);
            }
        });
        care = rootView.findViewById(R.id.care_save);
        care.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gridConnect("notice");
                page = 1;
                all.setBackgroundResource(R.drawable.button_save_gray);
                care.setBackgroundResource(R.drawable.button_save);
                story.setBackgroundResource(R.drawable.button_save_gray);
            }
        });
        story = rootView.findViewById(R.id.story_save);
        story.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gridConnect("story");
                page = 2;
                all.setBackgroundResource(R.drawable.button_save_gray);
                care.setBackgroundResource(R.drawable.button_save_gray);
                story.setBackgroundResource(R.drawable.button_save);
            }
        });
    }

    //서버로 부터 json 받아오는 부분. 저장한 글들 받아오는 부분.
    public void gridConnect(final String type) {
        String SNSUrl = "http://133.186.135.41/zozo_sns_like_show.php";
        StringRequest request = new StringRequest(Request.Method.POST, SNSUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    Log.d("SAVEFRAG_DATA", response);
                    switch (type) {
                        case "all":
                            page = 0;
                            break;
                        case "notice":
                            page = 1;
                            break;
                        case "story":
                            page = 2;
                            break;
                    }
                    doJSONParser_grid(response);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }


        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(rootView.getContext().getApplicationContext(), "인터넷 접속 상태를 확인해주세요.", Toast.LENGTH_SHORT).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parameters = new HashMap<String, String>();
                parameters.put("id", id);
                parameters.put("type", type);
                return parameters;
            }
        };
        requestQueue.add(request);
    }

    //실제 레이아웃 구현 좋아요 표시한 글 그리드 뷰.
    public void doJSONParser_grid(String response) {

        try {
            JSONArray jArray = new JSONArray(response);
            dataList_save = new ArrayList<>();
            JSONObject jObject = jArray.getJSONObject(0);
            String state = jObject.getString("state");
            if (state.equals("sus")) {

                story_null_mark.setVisibility(View.GONE);
                recyclerSaveView.setVisibility(View.VISIBLE);

                for (int i = 0; i < jArray.length(); i++) {
                    JSONObject jObject2 = jArray.getJSONObject(i);
                    String img = jObject2.getString("img");
                    String title = jObject2.getString("title");
                    String no = jObject2.getString("no");
                    String type = jObject2.getString("type");

                    // private String title, image, no;
                    dataList_save.add(new SaveItem(title, img, no, type));
                }
            } else {
                story_null_mark.setVisibility(View.VISIBLE);
                recyclerSaveView.setVisibility(View.GONE);
            }
            adapter_save = new SaveView_Adapter(dataList_save);
            recyclerSaveView.setAdapter(adapter_save);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //frag에 intent value 주입.
    public void refresh_frag_save(Intent intent) {

        Bundle extras = intent.getExtras();

        if (extras != null) {
            String id_refresh = extras.getString("id");
            String nickname_refresh = extras.getString("nickname");
            id = id_refresh;
            //profile = profile_refresh;

            if (id == null) {
                Long id_refresh_pre = extras.getLong("id");
                id_refresh = id_refresh_pre.toString();
                id = id_refresh;
                //profile = profile_refresh;
            }

        } else {
            Log.d("extras", "extras null");
        }


    }

    @Override
    public void onResume() {
        super.onResume();
        switch (page) {
            case 0:
                gridConnect("all");
                break;
            case 1:
                gridConnect("notice");
                break;
            case 2:
                gridConnect("story");
                break;
        }

    }

    public void setScrollView() {
        recyclerSaveView.scrollToPosition(0);
    }
}
