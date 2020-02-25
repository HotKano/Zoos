package com.chanb.zoos;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
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
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.chanb.zoos.utils.Database;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Story_act extends AppCompatActivity {

    RequestQueue requestQueue;
    String id, target_id, petNo;
    GlobalApplication globalApplication;
    Story_frag story_frag;
    FrameLayout frameLayout;
    ImageButton set, share;
    public static Story_act _story_act;
    FloatingActionButton upload_btn;
    ImageView petProfile_story;
    TextView petContentView, petNameView;
    RelativeLayout more_info_pet_story_view;
    Database database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.story_act);
        _story_act = this;
        try {
            setting();
            setFrag();
            petConnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 프레그먼트 세팅
    public void setFrag() {
        frameLayout = findViewById(R.id.container_story);
        story_frag = new Story_frag(); // grid in story frag

        Intent intent = getIntent();
        if (intent != null) {
            story_frag.refresh_frag_story(intent);
        }

        getSupportFragmentManager().beginTransaction().replace(R.id.container_story, story_frag).commit();

    }

    @SuppressLint("RestrictedApi")
    private void setting() {
        petNo = getIntent().getStringExtra("petNo");
        if (TextUtils.isEmpty(petNo))
            petNo = "1";

        String[] temp = new String[2];
        String dbName = "UserDatabase_Zoos";
        int dataBaseVersion = 1;
        database = new Database(this, dbName, null, dataBaseVersion);
        database.init();

        globalApplication = new GlobalApplication();
        globalApplication.getWindow(this);
        requestQueue = Volley.newRequestQueue(this);
        set = findViewById(R.id.setting_save);
        share = findViewById(R.id.share_save);
        petProfile_story = findViewById(R.id.petProfile_story);
        petContentView = findViewById(R.id.petContent_Story_Act);
        petNameView = findViewById(R.id.petName_Story_Act);
        final Intent intent = getIntent();
        if (intent != null) {
            id = intent.getStringExtra("id");
            target_id = intent.getStringExtra("target_id");
            Log.d("extras_story_act", id + " " + target_id);
        } else {
            id = null;
            target_id = null;
            onBackPressed();
        }

        set.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(Story_act.this, Setting_act.class);
                Log.d("action", id + "ddd");
                intent1.putExtra("id", id);
                startActivity(intent1);
            }
        });

        upload_btn = findViewById(R.id.upload_btn_story_act);
        upload_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Story_act.this, Upload_act.class);
                intent.putExtra("id", id);
                startActivity(intent);
            }
        });

        more_info_pet_story_view = findViewById(R.id.more_info_pet_story_view);

        if (!id.equals(target_id)) {
            upload_btn.setVisibility(View.GONE);
            set.setImageResource(R.drawable.talk_2);
            set.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    connect();
                }
            });
            more_info_pet_story_view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent1 = new Intent(Story_act.this, Pet_show_act.class);
                    intent1.putExtra("id", target_id);
                    intent1.putExtra("petNo", petNo);
                    intent1.putExtra("view_mode", "see");
                    startActivity(intent1);
                }
            });
        } else if (id.equals(target_id)) {
            upload_btn.setVisibility(View.VISIBLE);
            set.setImageResource(R.drawable.set_gray);
            set.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent1 = new Intent(Story_act.this, Setting_act.class);
                    Log.d("action", id + "ddd");
                    intent1.putExtra("id", id);
                    startActivity(intent1);
                }
            });
            more_info_pet_story_view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent1 = new Intent(Story_act.this, Pet_show_act.class);
                    intent1.putExtra("id", id);
                    intent1.putExtra("petNo", petNo);
                    intent1.putExtra("view_mode", "edit");
                    startActivity(intent1);
                }
            });
        }


    }

    //메세지 액트로 이동하기 위한 부분.
    private void connect() {
        String sendUrl = "http://133.186.135.41/zozo_message_connect.php";
        StringRequest request = new StringRequest(Request.Method.POST, sendUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonOutput = JsonUtil.getJSONObjectFrom(response);
                    String state = JsonUtil.getStringFrom(jsonOutput, "state");
                    String uid = JsonUtil.getStringFrom(jsonOutput, "uid");
                    String url = JsonUtil.getStringFrom(jsonOutput, "profile");

                    if (state.equals("sus")) {
                        Intent intent1 = new Intent(Story_act.this, MessageShow_Act.class);
                        intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                                | Intent.FLAG_ACTIVITY_CLEAR_TOP
                                | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        intent1.putExtra("uid", uid);
                        intent1.putExtra("profile", url);
                        startActivity(intent1);
                    } else {
                        Toast.makeText(Story_act.this, "error", Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(Story_act.this, "인터넷 또는 자료값 확인.", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> parameters = new HashMap<>();
                String id_local = target_id;
                parameters.put("id", id_local);
                return parameters;
            }
        };

        requestQueue.add(request);
    }

    //펫 정보 받아오는 부분.
    public void petConnect() {
        String SNSUrl = "http://133.186.135.41/zozo_edit_pet_show.php";
        StringRequest request = new StringRequest(Request.Method.POST, SNSUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    doJSONParser_grid(response);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(Story_act.this, "인터넷 접속 상태를 확인해주세요.", Toast.LENGTH_SHORT).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parameters = new HashMap<String, String>();
                if (target_id == null)
                    parameters.put("id", id);
                else
                    parameters.put("id", target_id);

                parameters.put("petNo", petNo);
                return parameters;
            }
        };
        requestQueue.add(request);
    }

    //실제 레이아웃 구현 좋아요 표시한 글 그리드 뷰.
    public void doJSONParser_grid(String response) {

        try {
            JSONArray jArray = new JSONObject(response).getJSONArray("data");
            for (int i = 0; i < jArray.length(); i++) {
                JSONObject jObject = jArray.getJSONObject(i);
                String petName = jObject.getString("petName");
                String petProfile = jObject.getString("petProfile");
                String petGender = jObject.getString("petGender");
                String petKind = jObject.getString("petKind");
                String petAge = jObject.getString("petAge");
                String petContent = jObject.getString("content");
                String petNeutral = jObject.getString("neutral");
                String petNumber = jObject.getString("petNumber");
                String petRace = jObject.getString("petRace");

                // petName_view,  petContent_view,  petProfile,  petKind,  petAge,  petGender,  petNeutral,  petNumber, petWeight
                viewSetting(petName, petContent, petProfile, petKind, petAge, petGender, petNeutral, petNumber, petRace);
            }

        } catch (JSONException e) {
            more_info_pet_story_view.setVisibility(View.GONE);
            String dbName = "UserDatabase_Zoos";
            int dataBaseVersion = 1;
            database = new Database(this, dbName, null, dataBaseVersion);
            database.init();
            String[] temp = database.select("MEMBERINFO");
            petNameView.setText(target_id + "님의 스토리입니다.");
            e.printStackTrace();
        }
    }

    private void viewSetting(String petName, String petContent, String petProfile, String petKind, String petAge, String petGender, String petNeutral, String petNumber, String petRace) {
        petContentView.setText(petContent);
        petNameView.setText(petName);

        String url = "http://133.186.135.41/zozoPetProfile/" + petProfile;
        // 프로필 이미지.
        Glide.with(this)
                .asBitmap()
                .load(url)
                .apply(new RequestOptions()
                        .placeholder(R.drawable.placeholder)
                        .error(R.drawable.placeholder)
                        .override(231, 231)
                        .dontTransform()
                        .centerCrop()
                        .circleCrop()
                ).into(petProfile_story);
    }

    // 다른 화면에서 넘어올때의 새로고침.
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        story_frag.requestQueue = requestQueue;
        story_frag.id = id;
        story_frag.target_id = target_id;
        story_frag.gridConnect();
        story_frag.refresh_frag_story(intent);
        petConnect();
        Log.d("TEST_TEST", "TEST");
    }


}