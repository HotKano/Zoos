package com.chanb.zoos;

import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Pet_show_act extends AppCompatActivity {

    GlobalApplication globalApplication;
    String id, kind, petNo, view_mode;
    RequestQueue requestQueue;
    Intent intent;
    SNS_frag sns_frag;
    TextView petName_view, petContent_view, toPetEdit, petKind_view, petAge_view,
            petGender_view, petNeutral_view, petNumber_view, petRace_view;
    ImageView petProfileEdit;
    Button back_show;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pet_show_act);

        try {
            setting();
            gridConnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setting() {
        globalApplication = new GlobalApplication();
        globalApplication.getWindow(this);
        sns_frag = SNS_frag._SNS_frag;

        requestQueue = Volley.newRequestQueue(getApplicationContext());
        toPetEdit = findViewById(R.id.petShow_to_petEdit);
        petRace_view = findViewById(R.id.pet_Race_kind_show);
        petName_view = findViewById(R.id.petShow_name);
        petContent_view = findViewById(R.id.petShow_content);
        petProfileEdit = findViewById(R.id.petShow_petProfile);
        petKind_view = findViewById(R.id.pet_kind_show);
        petAge_view = findViewById(R.id.pet_age_show);
        petGender_view = findViewById(R.id.pet_gender_show);
        petNeutral_view = findViewById(R.id.pet_neutral_show);
        petNumber_view = findViewById(R.id.pet_petNumber_show);
        back_show = findViewById(R.id.backbutton_petShow);
        back_show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        intent = getIntent();
        if (intent != null) {
            id = intent.getStringExtra("id");
            petNo = intent.getStringExtra("petNo");
            kind = intent.getStringExtra("kind");
            view_mode = intent.getStringExtra("view_mode");
            Log.d("test_pet_edit", id + petNo + kind);
            toPetEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Pet_show_act.this, Pet_edit_act.class);
                    intent.putExtra("id", id);
                    intent.putExtra("petNo", petNo);
                    intent.putExtra("kind", "1");
                    startActivity(intent);
                }
            });

            if (!TextUtils.isEmpty(view_mode)) {
                if (view_mode.equals("see")) {
                    toPetEdit.setVisibility(View.INVISIBLE);
                } else {
                    toPetEdit.setVisibility(View.VISIBLE);
                }
            }

        }


    }

    //서버로 부터 json 받아오는 부분. 저장한 글들 받아오는 부분.
    public void gridConnect() {
        String SNSUrl = "http://133.186.135.41/zozo_edit_pet_show.php";
        StringRequest request = new StringRequest(Request.Method.POST, SNSUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.d("Save_response", response);
                try {
                    doJSONParser_grid(response);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(Pet_show_act.this, "인터넷 접속 상태를 확인해주세요.", Toast.LENGTH_SHORT).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parameters = new HashMap<String, String>();
                parameters.put("id", id);
                parameters.put("petNo", petNo);
                Log.d("Save", id);

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
            e.printStackTrace();
        }
    }


    private void viewSetting(String petName, String petContent, String petProfile, String petKind, String petAge,
                             String petGender, String petNeutral, String petNumber, String petRace) {
        petName_view.setText(petName);
        petContent_view.setText(petContent);
        petKind_view.setText(petKind);
        petAge_view.setText(petAge + "살");
        petRace_view.setText(petRace);

        if (petGender.equals("0"))
            petGender_view.setText("암컷");
        else
            petGender_view.setText("수컷");

        if (petNeutral.equals("0"))
            petNeutral_view.setText("미실시");
        else
            petNeutral_view.setText("실시");

        petNumber_view.setText(petNumber);

        Log.d("viewSetting", petProfile + "petProfileShow");

        String url = "http://133.186.135.41/zozoPetProfile/" + petProfile;
        //메인 이미지
        Glide.with(this)
                .load(url)
                .apply(new RequestOptions()
                        .placeholder(R.drawable.placeholder)
                        .error(R.drawable.placeholder)
                        .skipMemoryCache(false)
                        .override(231, 231)
                        .dontTransform()
                        .centerCrop()
                        .circleCrop()
                ).into(petProfileEdit);
    }

}
