package com.chanb.zoos;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Edit_act extends AppCompatActivity {

    EditText nickname_edit, pet_kind_edit, pet_gender_edit, pet_vac_edit, pet_birth_edit;
    RequestQueue requestQueue;
    Button submit;
    String id;

    //개인 설정(환경 설정)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_act);

        onSetting();
    }

    private void onSetting() {
        nickname_edit = findViewById(R.id.nickname_edit);
        pet_kind_edit = findViewById(R.id.pet_kind_edit);
        pet_gender_edit = findViewById(R.id.pet_gender_edit);
        pet_vac_edit = findViewById(R.id.pet_vac_edit);
        pet_birth_edit = findViewById(R.id.pet_birth_edit);
        submit = findViewById(R.id.submit_edit);
        requestQueue = Volley.newRequestQueue(getApplicationContext());
        Intent intent = getIntent();
        id = intent.getStringExtra("id");

        editConnect();
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edit_Change_Connect();
            }
        });
    }

    //서버에서 받아오는 역할.
    private void editConnect() {
        String url = "http://133.186.135.41/zozo_edit_show.php";
        StringRequest stringRequest = new StringRequest(StringRequest.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jsonObject = JsonUtil.getJSONObjectFrom(response);
                    Log.d("edit_Connect", response);
                    String nickname = JsonUtil.getStringFrom(jsonObject, "nickname");
                    String pet_kind = JsonUtil.getStringFrom(jsonObject, "pet_kind");
                    String pet_gender = JsonUtil.getStringFrom(jsonObject, "pet_gender");
                    String pet_vac = JsonUtil.getStringFrom(jsonObject, "pet_vac");
                    String pet_birth = JsonUtil.getStringFrom(jsonObject, "pet_birth");

                    nickname_edit.setText(nickname);
                    pet_kind_edit.setText(pet_kind);
                    pet_gender_edit.setText(pet_gender);
                    pet_vac_edit.setText(pet_vac);
                    pet_birth_edit.setText(pet_birth);
                } catch (Exception e) {

                }


            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parameters = new HashMap<>();
                parameters.put("id", id);
                Log.d("edit_connect", id);
                return parameters;
            }
        };
        requestQueue.add(stringRequest);
    }

    private void edit_Change_Connect() {
        String url = "http://133.186.135.41/zozo_edit_input.php";
        StringRequest stringRequest = new StringRequest(StringRequest.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jsonObject = JsonUtil.getJSONObjectFrom(response);
                    Log.d("edit_Connect", response);
                    String state = JsonUtil.getStringFrom(jsonObject, "state");
                    if (state.equals("sus1")) {

                    }
                } catch (Exception e) {

                }


            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parameters = new HashMap<>();
                String id = "";
                String pet_kind = pet_kind_edit.getText().toString();
                String pet_gender = pet_gender_edit.getText().toString();
                String pet_birth = pet_birth_edit.getText().toString();
                String pet_vac = pet_vac_edit.getText().toString();

                parameters.put("id", id);
                parameters.put("pet_kind", pet_kind);
                parameters.put("pet_gender", pet_gender);
                parameters.put("pet_birth", pet_birth);
                parameters.put("pet_vac", pet_vac);
                return parameters;
            }
        };
        requestQueue.add(stringRequest);
    }
}
