package com.chanb.zoos;

import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import java.util.HashMap;
import java.util.Map;


public class FireBaseInstanceIDService extends FirebaseInstanceIdService {
    private static final String TAG = "FirebaseIIDService";
    RequestQueue requestQueue;

    @Override
    public void onTokenRefresh() {
        String token = FirebaseInstanceId.getInstance().getToken();
        int tokenLength = token.length();
        Log.d(TAG, "Refreshed token: " + token);
        Log.d(TAG, "Refreshed token length: " + tokenLength);
        requestQueue = Volley.newRequestQueue(this);
        // 각자 핸드폰 토큰값을 핸드폰으로 전송한다
        try {
            insertToken(token);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    //댓글 정보 json from server
    public void insertToken(final String token) {
        String url = "http://133.186.135.41/test.php";

        StringRequest request = new StringRequest(com.android.volley.Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    Log.d(TAG, response);
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
                parameters.put("Token", token);
                return parameters;
            }
        };
        requestQueue.add(request);
    }
}
