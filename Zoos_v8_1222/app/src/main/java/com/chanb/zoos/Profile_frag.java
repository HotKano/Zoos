package com.chanb.zoos;

import android.content.Intent;

import android.os.Bundle;

import android.support.v4.app.Fragment;


import android.support.v4.widget.SwipeRefreshLayout;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.chanb.zoos.utils.ItemMoveCallback;
import com.chanb.zoos.utils.StartDragListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Profile_frag extends Fragment implements StartDragListener {

    ViewGroup rootView;
    String id, nickname, profile;
    RequestQueue requestQueue;
    List<PetItem> dataList_pet;
    PetView_Adapter adapter_pet;
    RecyclerView recyclerEditPet;
    LinearLayout info_to_save, info_to_setting;
    ItemTouchHelper touchHelper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = (ViewGroup) inflater.inflate(R.layout.profile_frag, container, false);
        requestQueue = Volley.newRequestQueue(rootView.getContext().getApplicationContext());

        try {
            setting();
            petConnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rootView;
    }

    private void setting() {

        String[] temp = new String[2];
        String dbName = "UserDatabase_Zoos";
        int dataBaseVersion = 1;
        Database database = new Database(rootView.getContext(), dbName, null, dataBaseVersion);
        database.init();
        temp = database.select("MEMBERINFO");
        id = temp[0];
        nickname = temp[2];

        info_to_save = rootView.findViewById(R.id.info_to_save);
        info_to_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(rootView.getContext().getApplicationContext(), Loading_act.class);
                intent.putExtra("id", id);
                intent.putExtra("type", "save");
                if (id != null) {
                    startActivity(intent);
                } else {
                    Toast.makeText(rootView.getContext().getApplicationContext(), "error from id", Toast.LENGTH_SHORT).show();
                }

            }
        });

        info_to_setting = rootView.findViewById(R.id.info_to_setting);
        info_to_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(rootView.getContext().getApplicationContext(), Setting_act.class);
                intent.putExtra("id", id);
                if (id != null) {
                    startActivity(intent);
                } else {
                    Toast.makeText(rootView.getContext().getApplicationContext(), "error from id", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //펫 진
        recyclerEditPet = rootView.findViewById(R.id.recycle_pet_profile);
        RecyclerView.LayoutManager gridManager = new LinearLayoutManager(rootView.getContext());
        ((LinearLayoutManager) gridManager).setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerEditPet.setLayoutManager(gridManager);
        recyclerEditPet.setItemAnimator(null);
    }

    //서버로 부터 json 받아오는 부분. 펫 정보 리사이클 뷰에 정보 들어가는 부분.
    public void petConnect() {
        String SNSUrl = "http://133.186.135.41/zozo_edit_pet_show.php";
        StringRequest request = new StringRequest(Request.Method.POST, SNSUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                //Log.d("PetEdit", response);
                try {
                    doJSONParser_pet(response);
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
                return parameters;
            }
        };
        requestQueue.add(request);
    }

    //실제 레이아웃 구현
    public void doJSONParser_pet(String response) {

        try {
            JSONObject jsonOutput = JsonUtil.getJSONObjectFrom(response);
            String total = JsonUtil.getStringFrom(jsonOutput, "state");

            dataList_pet = new ArrayList<>();

            if (total.equals("fail")) {
                dataList_pet.add(new PetItem("", "", "", "", "", ""));
            } else if (total.equals("sus1")) {
                JSONArray jArray = new JSONObject(response).getJSONArray("data");
                for (int i = 0; i <= jArray.length(); i++) {
                    if (i == jArray.length()) {
                        dataList_pet.add(new PetItem("", "", "", "", "", ""));
                    } else {
                        JSONObject jObject = jArray.getJSONObject(i);
                        String petNo = jObject.getString("petNo");
                        String petProfile = jObject.getString("petProfile");
                        String petName = jObject.getString("petName");

                        String petGender = jObject.getString("petGender");
                        String petKind = jObject.getString("petKind");
                        String petAge = jObject.getString("petAge");

                        //petNo, petProfile, petName, petGender, petKind, petWeight, petAge, id;
                        dataList_pet.add(new PetItem(petNo, petProfile, petName, petGender, petKind, petAge));
                    }


                }

            }

            adapter_pet = new PetView_Adapter(dataList_pet, this);

            ItemTouchHelper.Callback callback =
                    new ItemMoveCallback(adapter_pet);
            touchHelper = new ItemTouchHelper(callback);
            touchHelper.attachToRecyclerView(recyclerEditPet);

            recyclerEditPet.setAdapter(adapter_pet);
            recyclerEditPet.setNestedScrollingEnabled(false);


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //frag에 intent value 주입.
    public void refresh_frag_info(Intent intent) {

        Bundle extras = intent.getExtras();

        if (extras != null) {
            String id_refresh = extras.getString("id");
            String nickname_refresh = extras.getString("nickname");
            String profile_refresh = extras.getString("profile");
            id = id_refresh;
            nickname = nickname_refresh;
            profile = profile_refresh;
            Log.d("extras_info", id + nickname + profile);
        } else {
            Log.d("extras_info", "extras null");
        }


    }

    @Override
    public void requestDrag(RecyclerView.ViewHolder viewHolder) {
        touchHelper.startDrag(viewHolder);
    }
}
