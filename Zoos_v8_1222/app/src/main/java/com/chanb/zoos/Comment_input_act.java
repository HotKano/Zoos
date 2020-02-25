package com.chanb.zoos;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Comment_input_act extends AppCompatActivity {

    EditText comment_input;
    Button submit_input;
    RequestQueue requestQueue;
    String id, board_no;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.comment_input_act);
        requestQueue = Volley.newRequestQueue(this);
        comment_input = findViewById(R.id.comment_input);
        comment_input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.d("text", comment_input.getText().toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        submit_input = findViewById(R.id.submit_input);

        try {

            Intent intent = getIntent();
            board_no = intent.getStringExtra("board_no");
        } catch (Exception e) {
            e.printStackTrace();
        }

        submit_input.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                replyUploadConnect();
            }
        });

    }

    protected void replyUploadConnect() {
        String sendUrl = "http://133.186.135.41/zozo_sns_comment_insert.php";
        StringRequest request = new StringRequest(Request.Method.POST, sendUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                JSONObject jsonOutput = null;
                String text = null;

                try {

                    jsonOutput = JsonUtil.getJSONObjectFrom(response);
                    text = JsonUtil.getStringFrom(jsonOutput, "state");
                    Log.d("INTERNET", "ok2" + text);
                    if (text.equals("sus1") || text.equals("sus2")) {

                        Intent intent = new Intent(Comment_input_act.this, Comment_act.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                                | Intent.FLAG_ACTIVITY_CLEAR_TOP
                                | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        intent.putExtra("refresh", 1);
                        startActivity(intent);
                        finish();
                        //upload_btn.setEnabled(true);
                    } else if (text.equals("fail_upload")) {
                        Toast.makeText(Comment_input_act.this, "등록 실패.", Toast.LENGTH_SHORT).show();
                        //upload_btn.setEnabled(true);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(Comment_input_act.this, "인터넷 또는 자료값 확인.", Toast.LENGTH_SHORT).show();
                //upload_btn.setEnabled(true);

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parameters = new HashMap<String, String>();


                String comment = comment_input.getText().toString();
                String id_local = id;
                //String img = getStringImage(photo);
                Log.d("INTERNET", "ok1");
                parameters.put("id", id_local);
                //parameters.put("file", img);
                parameters.put("board_no", board_no);
                parameters.put("re_comment", comment);
         /*       Message msg = new Message();
                msg.what = 0;
                msg_progress(msg);*/
                return parameters;
            }
        };
        requestQueue.add(request);
    }

}
