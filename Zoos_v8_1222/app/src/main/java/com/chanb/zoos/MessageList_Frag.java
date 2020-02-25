package com.chanb.zoos;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.chanb.zoos.utils.Database;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class MessageList_Frag extends Fragment {

    RecyclerView recyclerView, careRecyclerView;
    DatabaseReference messageDatabase;
    FirebaseAuth mAuth;
    String target_id, id;

    ViewGroup rootView;
    String nickname;
    Button messageView, careMessageView;
    RequestQueue requestQueue;
    GlobalApplication globalApplication;

    //파이어베이스 채팅 어뎁터
    ArrayList<MessageList_Item> dataList_chatting;
    MessageList_Adapter mAdapter;

    //케어 아이템 어뎁터
    ArrayList<CareItem> dataList;
    CareView_Adapter adapter;

    //데이터 베이스 (회원)
    Database database;

    //공백 시 나오는 메시지.
    LinearLayout empty_view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = (ViewGroup) inflater.inflate(R.layout.message_list_frag, container, false);

        try {
            setting();
            getCareList();
            init();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rootView;
    }

    private void setting() {

        String dbName = "UserDatabase_Zoos";
        int dataBaseVersion = 1;
        database = new Database(rootView.getContext().getApplicationContext(), dbName, null, dataBaseVersion);
        database.init();
        String[] temp = database.select("MEMBERINFO");
        if (temp != null) {
            id = temp[0];
        }

        requestQueue = Volley.newRequestQueue(rootView.getContext().getApplicationContext());
        dataList_chatting = new ArrayList<>();
        mAdapter = new MessageList_Adapter(dataList_chatting);
        messageView = rootView.findViewById(R.id.button_message_list);
        careMessageView = rootView.findViewById(R.id.button_care_list);
        mAuth = FirebaseAuth.getInstance();
        target_id = mAuth.getCurrentUser().getUid();
        messageDatabase = FirebaseDatabase.getInstance().getReference();
        globalApplication = new GlobalApplication();

        //메시지 리스트.
        recyclerView = rootView.findViewById(R.id.chattingList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(rootView.getContext());
        recyclerView.setLayoutManager(linearLayoutManager);

        //케어 리스트.
        careRecyclerView = rootView.findViewById(R.id.careList);
        LinearLayoutManager linearLayoutManager2 = new LinearLayoutManager(rootView.getContext());
        careRecyclerView.setLayoutManager(linearLayoutManager2);

        messageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                messageView.setBackgroundResource(R.drawable.button_save);
                messageView.setTextColor(Color.WHITE);
                careMessageView.setBackgroundResource(R.drawable.button_save_gray);
                careMessageView.setTextColor(Color.argb(100, 26, 26, 26));
                recyclerView.setVisibility(View.VISIBLE);
                careRecyclerView.setVisibility(View.GONE);
                empty_view.setVisibility(View.GONE);
                getList();

                if (dataList_chatting.size() == 0) {
                    empty_view.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                } else {
                    empty_view.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                }
            }
        });

        careMessageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                careMessageView.setBackgroundResource(R.drawable.button_save);
                careMessageView.setTextColor(Color.WHITE);
                messageView.setBackgroundResource(R.drawable.button_save_gray);
                messageView.setTextColor(Color.argb(100, 26, 26, 26));
                recyclerView.setVisibility(View.GONE);
                careRecyclerView.setVisibility(View.VISIBLE);
                empty_view.setVisibility(View.GONE);
                getCareList();
            }
        });
        empty_view = rootView.findViewById(R.id.msg_null);
    }

    //상단으로 스크롤
    public void setScrollView() {
        NestedScrollView scrollView = rootView.findViewById(R.id.message_list_scroll);
        scrollView.smoothScrollTo(0, 0);
    }

    //서버로 부터 json 받아오는 부분. 저장한 글들 받아오는 부분. 케어메시지.
    public void getCareList() {
        String SNSUrl = "http://133.186.135.41/zozo_care_list_show.php";
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
                Toast.makeText(rootView.getContext().getApplicationContext(), "인터넷 접속 상태를 확인해주세요.", Toast.LENGTH_SHORT).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parameters = new HashMap<String, String>();
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int date = calendar.get(Calendar.DATE);
                String yearForm = String.valueOf(year);
                String monthForm = String.valueOf(month + 1);
                String dateForm = String.valueOf(date);
                parameters.put("id", id);
                parameters.put("year", yearForm);
                parameters.put("month", monthForm);
                parameters.put("date", dateForm);
                return parameters;
            }
        };
        requestQueue.add(request);
    }

    //실제 레이아웃 구현 스토리 일 별 뷰.
    public void doJSONParser_grid(String response) {
        try {
            dataList = new ArrayList<>();
            JSONObject jsonOutput = JsonUtil.getJSONObjectFrom(response);
            String state = JsonUtil.getStringFrom(jsonOutput, "state");

            if (state.equals("sus1")) {
                JSONArray jArray = new JSONObject(response).getJSONArray("diary");
                for (int i = 0; i < jArray.length(); i++) {
                    JSONObject jObject = jArray.getJSONObject(i);
                    String title = jObject.getString("title");
                    String time = jObject.getString("time");
                    String seen = jObject.getString("seen");
                    String petName = jObject.getString("petName");
                    String year = jObject.getString("year");
                    String month = jObject.getString("month");
                    String date = jObject.getString("date");

                    //String text, String from, String seen, String time, String petName;
                    dataList.add(new CareItem(title, seen, time, petName, year, month, date));
                }
                recyclerView.setVisibility(View.VISIBLE);

            } else {
                recyclerView.setVisibility(View.GONE);
                empty_view.setVisibility(View.VISIBLE);
            }
            adapter = new CareView_Adapter(dataList);
            recyclerView.setAdapter(adapter);
            recyclerView.setNestedScrollingEnabled(false);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void getList() {
        messageDatabase.child("Message").child(target_id).orderByChild("time")
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        final String fromID = dataSnapshot.getKey();

                        messageDatabase.child("Message").child(target_id).child(fromID).orderByChild("time").limitToLast(1)
                                .addChildEventListener(new ChildEventListener() {
                                    @Override
                                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                                        final String text = dataSnapshot.child("text").getValue().toString();
                                        final String seen = dataSnapshot.child("seen").getValue().toString();
                                        final String time = dataSnapshot.child("time").getValue().toString();
                                        //member로 부터 닉네임 받아옴.
                                        messageDatabase.child("member").child(fromID)
                                                .addChildEventListener(new ChildEventListener() {
                                                    @Override
                                                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                                                        try {
                                                            String nickname = dataSnapshot.getValue().toString();
                                                            if (dataList_chatting.size() > 0) {
                                                                String getFrom;
                                                                for (int i = 0; i < dataList_chatting.size(); i++) {
                                                                    getFrom = dataList_chatting.get(i).getFrom();
                                                                    Log.d("getList_getFrom", getFrom);
                                                                    if (getFrom.equals(fromID)) {
                                                                        dataList_chatting.remove(i);
                                                                    }
                                                                }
                                                                dataList_chatting.add(new MessageList_Item(text, fromID, seen, time, nickname));
                                                                DescendingObj d = new DescendingObj();
                                                                Collections.sort(dataList_chatting, d);
                                                            } else {
                                                                dataList_chatting.add(new MessageList_Item(text, fromID, seen, time, nickname));
                                                                DescendingObj d = new DescendingObj();
                                                                Collections.sort(dataList_chatting, d);
                                                            }

                                                            mAdapter.notifyDataSetChanged();
                                                            if (dataList_chatting.size() == 0) {
                                                                empty_view.setVisibility(View.VISIBLE);
                                                                recyclerView.setVisibility(View.GONE);
                                                            } else {
                                                                empty_view.setVisibility(View.GONE);
                                                                recyclerView.setVisibility(View.VISIBLE);
                                                            }
                                                        } catch (Exception e) {
                                                            e.printStackTrace();
                                                        }
                                                    }

                                                    @Override
                                                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                                                    }

                                                    @Override
                                                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                                                    }

                                                    @Override
                                                    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                                    }
                                                });

                                    }

                                    @Override
                                    public void onChildChanged(@NonNull DataSnapshot
                                                                       dataSnapshot, @Nullable String s) {

                                        messageDatabase.child("member").child(fromID)
                                                .addChildEventListener(new ChildEventListener() {
                                                    @Override
                                                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                                                        try {
                                                            String nickname = dataSnapshot.getValue().toString();
                                                            //String from = dataSnapshot.child("from").getValue().toString();
                                                            String test = dataSnapshot.child("text").getValue().toString();
                                                            String seen = dataSnapshot.child("seen").getValue().toString();
                                                            String time = dataSnapshot.child("time").getValue().toString();
                                                            //Log.d("getList_changed", from + "changed_act");

                                                            //중복 제거
                                                            if (dataList_chatting.size() > 0) {
                                                                String getFrom;
                                                                for (int i = 0; i < dataList_chatting.size(); i++) {
                                                                    getFrom = dataList_chatting.get(i).getFrom();
                                                                    Log.d("getList_getFrom", getFrom);
                                                                    if (getFrom.equals(fromID)) {
                                                                        dataList_chatting.remove(i);
                                                                    }
                                                                }
                                                                dataList_chatting.add(new MessageList_Item(test, fromID, seen, time, nickname));
                                                                DescendingObj d = new DescendingObj();
                                                                Collections.sort(dataList_chatting, d);
                                                                mAdapter.notifyDataSetChanged();
                                                            }
                                                        } catch (Exception e) {
                                                            e.printStackTrace();
                                                        }
                                                    }

                                                    @Override
                                                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                                                    }

                                                    @Override
                                                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                                                    }

                                                    @Override
                                                    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                                    }
                                                });

                                    }

                                    @Override
                                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                                    }

                                    @Override
                                    public void onChildMoved(@NonNull DataSnapshot
                                                                     dataSnapshot, @Nullable String s) {

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

        //메세지 추가된 데이터리스트 등록.
        mAdapter = new MessageList_Adapter(dataList_chatting);
        recyclerView.setAdapter(mAdapter);


    }

    public void refresh_frag_chatting(String kind) {
        String dbName = "UserDatabase_Zoos";
        int dataBaseVersion = 1;
        database = new Database(rootView.getContext().getApplicationContext(), dbName, null, dataBaseVersion);
        database.init();
        String[] temp = database.select("MEMBERINFO");
        if (temp != null) {
            id = temp[0];
        }
        requestQueue = Volley.newRequestQueue(rootView.getContext().getApplicationContext());

        if (kind.equals("care"))
            getCareList();
        else if (kind.equals("message"))
            getList();
    }

    private void init() {
        careMessageView.setBackgroundResource(R.drawable.button_save);
        careMessageView.setTextColor(Color.WHITE);
        messageView.setBackgroundResource(R.drawable.button_save_gray);
        messageView.setTextColor(Color.argb(100, 26, 26, 26));
    }

    // String 내림차순
    class DescendingObj implements Comparator<MessageList_Item> {

        @Override
        public int compare(MessageList_Item o1, MessageList_Item o2) {
            return o2.getTime().compareTo(o1.getTime());
        }

    }


}
