package com.chanb.zoos;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Join_Act extends AppCompatActivity {

    File file;

    private String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA};
    private static final int MULTIPLE_PERMISSIONS = 101;
    RequestQueue requestQueue;
    Logo_act logo_act;
    Bitmap img;
    EditText cellphone_join;
    ImageView backbutton_join;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.join_act);

        try {
            setting();
            checkPermissions();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void setting() {
        logo_act = Logo_act._logo_act;
        cellphone_join = findViewById(R.id.cellphone_join);

        GlobalApplication globalApplication = new GlobalApplication();
        globalApplication.getWindow(this);
        backbutton_join = findViewById(R.id.backbutton_join);
        backbutton_join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        requestQueue = Volley.newRequestQueue(getApplicationContext());
        windowBar(this);
    }

    private boolean checkString() {

        String cellphone = cellphone_join.getText().toString();

        return true;


    }

    private boolean checkPermissions() {
        int result;
        List<String> permissionList = new ArrayList<>();
        for (String pm : permissions) {
            result = ContextCompat.checkSelfPermission(this, pm);
            if (result != PackageManager.PERMISSION_GRANTED) {
                permissionList.add(pm);
            }
        }
        if (!permissionList.isEmpty()) {
            ActivityCompat.requestPermissions(this, permissionList.toArray(new String[permissionList.size()]), MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MULTIPLE_PERMISSIONS: {
                if (grantResults.length > 0) {
                    for (int i = 0; i < permissions.length; i++) {
                        if (permissions[i].equals(this.permissions[0])) {
                            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                                showNoPermissionToastAndFinish();
                            }
                        } else if (permissions[i].equals(this.permissions[1])) {
                            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                                showNoPermissionToastAndFinish();

                            }
                        } else if (permissions[i].equals(this.permissions[2])) {
                            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                                showNoPermissionToastAndFinish();

                            }
                        }
                    }
                } else {
                    showNoPermissionToastAndFinish();
                }
                return;
            }
        }
    }

    private void showNoPermissionToastAndFinish() {
        Toast.makeText(this, "권한 요청에 동의 해주셔야 이용 가능합니다. 설정에서 권한 허용 하시기 바랍니다.", Toast.LENGTH_SHORT).show();
        finish();
    }

    protected void sendMet() {
        String sendUrl = "http://133.186.135.41/zozo_join.php";
        StringRequest request = new StringRequest(Request.Method.POST, sendUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                JSONObject jsonOutput = null;
                String text = null;
                String nickname = null;
                String id = null;
                try {
                    jsonOutput = JsonUtil.getJSONObjectFrom(response.toString());
                    text = JsonUtil.getStringFrom(jsonOutput, "state");
                    nickname = JsonUtil.getStringFrom(jsonOutput, "nickname");
                    id = JsonUtil.getStringFrom(jsonOutput, "id");

                    Intent intent = new Intent(Join_Act.this, Main_act.class);

                    if (text.equals("fail_upload")) {
                        Toast.makeText(Join_Act.this, "아이디 중복입니다.", Toast.LENGTH_SHORT).show();
                    } else if (text.equals("sus1")) {
                        intent.putExtra("nickname", nickname);
                        intent.putExtra("id", id);
                        intent.putExtra("profile", "1");
                        intent.putExtra("Type", "new");
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                                | Intent.FLAG_ACTIVITY_CLEAR_TOP
                                | Intent.FLAG_ACTIVITY_SINGLE_TOP);

                        startActivity(intent);
                        logo_act.finish();
                        finish();
                    } else {
                        Toast.makeText(Join_Act.this, "업로드 실패 값을 확인", Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    Toast.makeText(Join_Act.this, e.toString() + "", Toast.LENGTH_SHORT).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(Join_Act.this, "인터넷 또는 자료값 확인.", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parameters = new HashMap<String, String>();
                String cellphone = cellphone_join.getText().toString();

                parameters.put("cellphone", cellphone);
                //parameters.put("id", id);
                return parameters;


            }
        };
        requestQueue.add(request);
    }

    public void windowBar(Activity act) {
        GlobalApplication globalApplication = new GlobalApplication();
        globalApplication.getWindow(act);
    }
}
