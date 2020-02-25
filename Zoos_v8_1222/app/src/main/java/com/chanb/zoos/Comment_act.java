package com.chanb.zoos;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
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
import org.w3c.dom.Comment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Dr.kim on 2017-06-26.
 */

public class Comment_act extends AppCompatActivity {

    Comment_Adapter adapter;
    RecyclerView listView1;
    String board_no;
    RequestQueue requestQueue;
    ImageButton back;
    Button writeBtn;
    TextView emptyText;
    List<Comment_item> dataList_comment;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.comment_act);

        requestQueue = Volley.newRequestQueue(this);

        emptyText = findViewById(R.id.emptytext);
        writeBtn = findViewById(R.id.writeBtn_reply);
        board_no = getIntent().getStringExtra("no");
        listView1 = findViewById(R.id.reviewList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        listView1.setLayoutManager(layoutManager);
        back = findViewById(R.id.backbutton_comment);

/*        back.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        back.setAlpha(50);
                        return true;

                    case MotionEvent.ACTION_UP:
                        back.setAlpha(255);
                        onBackPressed();

                        return false;

                }
                return true;
            }
        });

*/


        try {
            commentConnect();
            setting();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void setting() { // 댓글 남기기 넘어가는 intent 제공
        writeBtn.findViewById(R.id.writeBtn_reply);
        writeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Comment_act.this, Comment_input_act.class);
                intent.putExtra("board_no", board_no);
                startActivity(intent);
            }
        });
    }

    public void commentConnect() {
        String url = "http://133.186.135.41/zozo_sns_comment.php";

        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    doJSONParser(response);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "인터넷 접속 상태를 확인해주세요.", Toast.LENGTH_SHORT).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parameters = new HashMap<String, String>();
                parameters.put("no", board_no);

                return parameters;
            }
        };
        requestQueue.add(request);
    }

    void doJSONParser(String response) {

        dataList_comment = new ArrayList<>();


        try {
            JSONArray jArray = new JSONArray(response);
            if (jArray.length() == 0) {
                emptyText.setVisibility(View.VISIBLE);
                listView1.setVisibility(View.GONE);
            } else {
                emptyText.setVisibility(View.GONE);
                listView1.setVisibility(View.VISIBLE);
                for (int i = 0; i < jArray.length(); i++) {
                    JSONObject jObject = jArray.getJSONObject(i);

                    String board_no = jObject.getString("no");
                    String re_no = jObject.getString("re_no");
                    String re_comment = jObject.getString("re_comment");
                    String re_time = jObject.getString("re_time");
                    String re_writer = jObject.getString("re_writer");
                    String re_writer_id = jObject.getString("re_writer_id");
                    String profile = jObject.getString("profile");
                    String time = jObject.getString("time");

                    dataList_comment.add(new Comment_item(board_no, re_no, re_comment, re_time, re_writer, re_writer_id, profile, time));


                }
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }

        adapter = new Comment_Adapter(dataList_comment);
        listView1.setAdapter(adapter);

    }

    public void reFresh() {
        adapter.notifyDataSetChanged();
        commentConnect();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        onRestart();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        commentConnect();
        //reFresh();
    }
}
