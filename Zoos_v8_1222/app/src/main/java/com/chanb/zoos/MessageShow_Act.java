package com.chanb.zoos;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class MessageShow_Act extends AppCompatActivity {

    FirebaseAuth mAuth;
    Button send;
    DatabaseReference messageDatabase;
    String sender_id, target_id, url;
    RecyclerView recyclerView;
    ArrayList<Message_Item> mDataList;
    Message_Adapter mAdapter;
    EditText message;
    TextView nickname_info, last_date;
    ImageView profile_info;
    ImageButton backButton_message_show;
    ChildEventListener childListener;
    GlobalApplication globalApplication;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.message_show_act);
        try {
            setting();
            takeMessage();
            setNickname();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //data from message_list adapter
    private void setting() {
        globalApplication = new GlobalApplication();
        globalApplication.getWindow(this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        mAuth = FirebaseAuth.getInstance();
        sender_id = mAuth.getCurrentUser().getUid();

        //uid
        target_id = getIntent().getStringExtra("uid");
        url = getIntent().getStringExtra("profile");
        url = "http://133.186.135.41/zozoPetProfile/" + url;
        Log.d("msg_show", sender_id + " " + target_id);


        //nickname
        nickname_info = findViewById(R.id.nickname_message_show);
        profile_info = findViewById(R.id.profile_message_show);

        //backButton
        backButton_message_show = findViewById(R.id.backButton_Message_Show);
        backButton_message_show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        recyclerView = findViewById(R.id.message_recycler);
        recyclerView.setLayoutManager(linearLayoutManager);
        mDataList = new ArrayList<>();
        mAdapter = new Message_Adapter(mDataList);
        recyclerView.setAdapter(mAdapter);

        messageDatabase = FirebaseDatabase.getInstance().getReference();
        message = findViewById(R.id.message_send_edit);
        send = findViewById(R.id.sendBtn_Main);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
                message.setText("");
                recyclerView.scrollToPosition(mDataList.size() - 1);
            }
        });

        last_date = findViewById(R.id.date_msg_show);
    }

    private void setNickname() {
        DatabaseReference messageDatabase = FirebaseDatabase.getInstance().getReference();

        //member 부터 닉네임 받아옴.
        messageDatabase.child("member").child(target_id)
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        String nickname = dataSnapshot.getValue().toString();
                        nickname_info.setText(nickname);
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


        getImage(target_id);
    }

    private void sendMessage() {
        String messageText = message.getText().toString();
        if (TextUtils.isEmpty(messageText)) {
            Toast.makeText(MessageShow_Act.this, "메세지를 입력해주세요.", Toast.LENGTH_SHORT).show();
        } else {
            String message_ref = "Message/" + sender_id + "/" + target_id;
            String target_ref = "Message/" + target_id + "/" + sender_id;
            DatabaseReference databaseReference = messageDatabase.child("Message").child(sender_id).child(target_id).push();
            final String message_push_id = databaseReference.getKey();

            Map messageTextBody = new HashMap();
            messageTextBody.put("text", messageText);
            messageTextBody.put("time", ServerValue.TIMESTAMP);
            messageTextBody.put("from", sender_id);
            messageTextBody.put("seen", false);

            Map messageDetailBody = new HashMap();
            messageDetailBody.put(message_ref + "/" + message_push_id, messageTextBody);
            messageDetailBody.put(target_ref + "/" + message_push_id, messageTextBody);

            messageDatabase.updateChildren(messageDetailBody, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                    if (databaseError != null) {
                        Log.d("chat_log", databaseError.getMessage());
                    }

                    message.setText("");
                    assert message_push_id != null;
                    messageDatabase.child("Message").child(sender_id).child(target_id).child(message_push_id).child("seen").setValue(true);
                    recyclerView.scrollToPosition(mDataList.size() - 1);

                }
            });
        }
    }

    private void takeMessage() {
        childListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String data = dataSnapshot.child("text").getValue().toString();
                String from = dataSnapshot.child("from").getValue().toString();
                String time = dataSnapshot.child("time").getValue().toString();
                messageDatabase.child("Message").child(sender_id).child(target_id).child(dataSnapshot.getKey()).child("seen").setValue(true);
                mDataList.add(new Message_Item(data, from, time));
                mAdapter.notifyDataSetChanged();

                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(Long.parseLong(time));
                int mTime = calendar.get(Calendar.HOUR_OF_DAY);
                int mMin = calendar.get(Calendar.MINUTE);
                int mYear = calendar.get(Calendar.YEAR);
                int mMonth = calendar.get(Calendar.MONTH);
                int mDay = calendar.get(Calendar.DAY_OF_MONTH);

                String temp_data = (mMonth + 1) + "월" + mDay + "일" + " (" + getDateDay(mYear, mMonth, mDay) + ")";

                last_date.setText(temp_data);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        messageDatabase.child("Message").child(sender_id).child(target_id).addChildEventListener(childListener);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(MessageShow_Act.this, Main_act.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra("page", "3");
        intent.putExtra("kind", "message");
        Main_act._Main_act.onNewIntent(intent);
        finish();
    }

    //화면 벗어나면 메세지 알림을 확인하지 않는 기능.
    @Override
    protected void onPause() {
        super.onPause();
        messageDatabase.child("Message").child(sender_id).child(target_id).removeEventListener(childListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        messageDatabase.child("Message").child(sender_id).child(target_id).removeEventListener(childListener);
    }

    public void getImage(final String uid) {
        String sendUrl = "http://133.186.135.41/zozo_sns_getImage.php";
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        StringRequest request = new StringRequest(Request.Method.POST, sendUrl, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonOutput = JsonUtil.getJSONObjectFrom(response);
                    String text = JsonUtil.getStringFrom(jsonOutput, "state");
                    Log.d("MessageList_Adapter", response);
                    if (text.equals("sus1")) {
                        String temp_image = JsonUtil.getStringFrom(jsonOutput, "petProfile");
                        final String data_form = "http://133.186.135.41/zozoPetProfile/" + temp_image;

                        Glide.with(getApplicationContext())
                                .load(data_form)
                                .apply(new RequestOptions()
                                        .centerCrop()
                                        .placeholder(R.drawable.manprivate)
                                        .error(R.drawable.manprivate)
                                        .circleCrop())
                                .into(profile_info);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }


            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MessageShow_Act.this, "인터넷 접속 확인", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> parameters = new HashMap<>();
                parameters.put("uid", uid);
                return parameters;
            }
        };

        requestQueue.add(request);
    }

    public String getDateDay(int year, int month, int date) {

        String day = "";

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month);
        cal.set(Calendar.DATE, date);

        int dayNum = cal.get(Calendar.DAY_OF_WEEK);

        switch (dayNum) {
            case 1:
                day = "일";
                break;
            case 2:
                day = "월";
                break;
            case 3:
                day = "화";
                break;
            case 4:
                day = "수";
                break;
            case 5:
                day = "목";
                break;
            case 6:
                day = "금";
                break;
            case 7:
                day = "토";
                break;

        }


        return day;
    }

}
