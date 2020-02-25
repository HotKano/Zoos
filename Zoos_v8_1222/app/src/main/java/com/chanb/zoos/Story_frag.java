package com.chanb.zoos;

import android.support.v4.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class Story_frag extends Fragment {

    RequestQueue requestQueue;
    String id, target_id;

    ArrayList<StoryItem_Total> dataList_total;
    ArrayList<StoryItem_Img> dataList_img;
    StoryView_Adapter_Total adapter_total;

    Story_act story_act;
    RecyclerView recyclerStoryView;
    ViewGroup rootView;
    TextView totalNumber;
    LinearLayout story_null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = (ViewGroup) inflater.inflate(R.layout.story_frag, container, false);

        try {
            setting();
            gridConnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rootView;
    }

    public void init() {

    }


    //화면 총 세팅.
    private void setting() {
        story_null = rootView.findViewById(R.id.story_null);
        requestQueue = Volley.newRequestQueue(rootView.getContext().getApplicationContext());
        recyclerStoryView = rootView.findViewById(R.id.recycle_grid_story);
        RecyclerView.LayoutManager LinearManager = new LinearLayoutManager(rootView.getContext().getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        recyclerStoryView.setLayoutManager(LinearManager);
        recyclerStoryView.setAnimation(null);
        recyclerStoryView.setLayoutManager(new LinearLayoutManager(rootView.getContext().getApplicationContext()) {
            @Override
            public boolean canScrollVertically() { // 세로스크롤 막기
                return false;
            }

            @Override
            public boolean canScrollHorizontally() { //가로 스크롤막기
                return false;
            }
        });
        totalNumber = rootView.findViewById(R.id.total_number_story);
    }

    //서버로 부터 json 받아오는 부분. 저장한 글들 받아오는 부분.
    public void gridConnect() {
        String SNSUrl = "http://133.186.135.41/zozo_story_show.php";
        StringRequest request = new StringRequest(Request.Method.POST, SNSUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.d("test_grid", response);
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
                if (target_id == null)
                    parameters.put("id", id);
                else
                    parameters.put("id", target_id);
                return parameters;
            }
        };
        requestQueue.add(request);
    }

    //실제 레이아웃 구현 스토리 월별 뷰. 예!!!
    //Total 갯수도 정의함.
    public void doJSONParser_grid(String response) {

        try {
            JSONArray jArray = new JSONObject(response).getJSONArray("month");
            JSONObject jsonOutput = JsonUtil.getJSONObjectFrom(response);
            String total = JsonUtil.getStringFrom(jsonOutput, "total");

            dataList_total = new ArrayList<>();

            Log.d("test_hope", jArray.length() + "length");

            if (jArray.length() > 0) {
                for (int i = 0; i < jArray.length(); i++) {

                    StoryItem_Total si = new StoryItem_Total();
                    JSONObject iObject = jArray.getJSONObject(i);

                    String date = iObject.getString("month");
                    String date_format = date.substring(0, 4);
                    String date_format2 = date.substring(5, 7);
                    date = date_format + "년 " + date_format2 + "월";
                    si.setDate(date);


                    int value = iObject.getInt("value");
                    //가로 이미지 뷰를 위한 리셋.
                    dataList_img = new ArrayList<>();

                    for (int j = 0; j < value; j++) {
                        JSONArray jArray2 = new JSONObject(response).getJSONArray("data" + i);
                        JSONObject jObject = jArray2.getJSONObject(j);
                        String no = jObject.getString("no");
                        String img = jObject.getString("img");
                        String private_temp = jObject.getString("private");

                        if (!id.equals(target_id)) {
                            if (private_temp.equals("true")) {

                            } else {
                                dataList_img.add(new StoryItem_Img(no, img));
                            }

                        } else {
                            //String no, String img, String date;
                            dataList_img.add(new StoryItem_Img(no, img));
                        }


                    }
                    si.setImgView(dataList_img);
                    dataList_total.add(si);

                }

                //total 값을 받아와 set.
                totalNumber.setText("총 스토리 " + total + "개");
                adapter_total = new StoryView_Adapter_Total(story_act, dataList_total);
                recyclerStoryView.setAdapter(adapter_total);
                story_null.setVisibility(View.GONE);
                recyclerStoryView.setVisibility(View.VISIBLE);
            } else {
                story_null.setVisibility(View.VISIBLE);
                recyclerStoryView.setVisibility(View.GONE);
            }


        } catch (JSONException e) {
            e.printStackTrace();
            story_null.setVisibility(View.VISIBLE);
            recyclerStoryView.setVisibility(View.GONE);
        }
    }

    //frag에 intent value 주입.
    public void refresh_frag_story(Intent intent) {

        Bundle extras = intent.getExtras();

        if (extras != null) {
            String id_refresh = extras.getString("id");
            String id_target = extras.getString("target_id");
            if (!TextUtils.isEmpty(id_refresh)) {
                id = id_refresh;
            }

            if (!TextUtils.isEmpty(id_target)) {
                target_id = id_target;
            }
            Log.d("extras_story", id + " " + target_id);

            onResume();

        } else {
            Log.d("extras_story", "extras null");
        }


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}


