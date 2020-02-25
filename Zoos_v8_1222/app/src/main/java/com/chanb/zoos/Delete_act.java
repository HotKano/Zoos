package com.chanb.zoos;

import android.content.Intent;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Delete_act extends AppCompatActivity {

    RequestQueue requestQueue;
    String device_id;
    Button button;
    Main_act main;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.delete_act);
        requestQueue = Volley.newRequestQueue(getApplicationContext());
        device_id = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        main = (Main_act) Main_act._Main_act;
        button = findViewById(R.id.delete);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteMet();
            }
        });
    }

    public void deleteMet() {
        String loginUrl = "http://133.186.135.41/zozo_delete.php";
        StringRequest request = new StringRequest(Request.Method.POST, loginUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                System.out.println(response.toString());

                try {
                    JSONObject jsonOutput = JsonUtil.getJSONObjectFrom(response.toString());
                    String state = JsonUtil.getStringFrom(jsonOutput, "state");
                    Intent intent = new Intent(Delete_act.this, Logo_act.class);
                    if (state.equals("success")) {
                        Toast.makeText(Delete_act.this, "계정 탈퇴가 정상적으로 이루어짐", Toast.LENGTH_SHORT).show();
                        startActivity(intent);
                        main.finish();
                        finish();

                    } else if (state.equals("fail")) {
                        Toast.makeText(Delete_act.this, "계절 탈퇴 안됨! 인터넷 확인!", Toast.LENGTH_SHORT).show();
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(Delete_act.this, "인터넷 접속 상태를 확인해주세요.", Toast.LENGTH_SHORT).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parameters = new HashMap<String, String>();

                Log.d("INTERNET", device_id + "를 보냄");
                parameters.put("device_id", device_id);

                return parameters;
            }
        };
        requestQueue.add(request);
    }
}
