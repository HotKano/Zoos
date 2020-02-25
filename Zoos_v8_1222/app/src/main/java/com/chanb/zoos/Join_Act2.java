package com.chanb.zoos;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.chanb.zoos.utils.Database;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;

public class Join_Act2 extends AppCompatActivity {

    RequestQueue requestQueue;
    String email, nickname, kind, id_naver;
    TextView text_join;
    ImageView backButton2;
    RelativeLayout next_box_join2;
    EditText phoneNumberView, passWordView, confirmPassWordView, emailView, nicknameView;
    Database database;
    long id;
    String phoneNumber_form, password_form, nickName_form, email_form, uid_form;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.join_act2);

        try {
            setting();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void setting() {
        requestQueue = Volley.newRequestQueue(getApplicationContext());
        backButton2 = findViewById(R.id.backbutton_join2);
        backButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        text_join = findViewById(R.id.text_join2);
        phoneNumberView = findViewById(R.id.cellphone_join2);
        passWordView = findViewById(R.id.password_join2);
        confirmPassWordView = findViewById(R.id.passwordConfirm_Join2);
        emailView = findViewById(R.id.email_join2);
        nicknameView = findViewById(R.id.nickname_join2);
        Intent intent = getIntent();
        if (intent != null) {
            kind = intent.getStringExtra("kind");
            if (!TextUtils.isEmpty(kind)) {
                if (kind.equals("kakao")) {
                    text_join.setText("카카오톡으로 가입하기");
                    id = intent.getLongExtra("id", -1);
                    nickname = intent.getStringExtra("nickname");
                    email = intent.getStringExtra("email");

                    String id_form = String.valueOf(id);
                    phoneNumberView.setText(id_form);
                    phoneNumberView.setFocusable(false);
                    phoneNumberView.setClickable(false);

                    nicknameView.setText(nickname);
                    emailView.setText(email);

                } else if (kind.equals("naver")) {
                    text_join.setText("네이버로 가입하기");
                    String test = intent.getStringExtra("test");
                    Log.d("Join_Act2", test);
                    id_naver = intent.getStringExtra("id");
                    nickname = intent.getStringExtra("nickname");
                    email = intent.getStringExtra("email");

                    String id_form = String.valueOf(id_naver);
                    phoneNumberView.setText(id_form);
                    phoneNumberView.setFocusable(false);
                    phoneNumberView.setClickable(false);

                    nicknameView.setText(nickname);
                    emailView.setText(email);
                }
            }
        }

        //submit to main_act
        next_box_join2 = findViewById(R.id.next_box_join2);
        next_box_join2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                phoneNumber_form = phoneNumberView.getText().toString();
                password_form = passWordView.getText().toString();
                String confirmPassword = confirmPassWordView.getText().toString();
                nickName_form = nicknameView.getText().toString();
                email_form = emailView.getText().toString();

                // 유효성 검사
                if (!TextUtils.isEmpty(phoneNumber_form) && !TextUtils.isEmpty(password_form) && !TextUtils.isEmpty(confirmPassword)
                        && !TextUtils.isEmpty(nickName_form) && !TextUtils.isEmpty(email_form)) {

                    if (nickName_form.length() > 6) {
                        View view = findViewById(R.id.background_join2);
                        Snackbar snackbar = Snackbar
                                .make(view, "닉네임은 6글자까지만 가능합니다.", Snackbar.LENGTH_LONG)
                                .setActionTextColor(Color.WHITE);
                        View sbView = snackbar.getView();
                        sbView.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.start_with_phone_color));
                        snackbar.show();
                    } else if (!password_form.equals(confirmPassword)) {
                        View view = findViewById(R.id.background_join2);
                        Snackbar snackbar = Snackbar
                                .make(view, "비밀번호가 다릅니다.", Snackbar.LENGTH_LONG)
                                .setActionTextColor(Color.WHITE);
                        View sbView = snackbar.getView();
                        sbView.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.start_with_phone_color));
                        snackbar.show();
                    } else if (password_form.length() < 6) {
                        View view = findViewById(R.id.background_join2);
                        Snackbar snackbar = Snackbar
                                .make(view, "비밀번호는 6자 이상이어야합니다.", Snackbar.LENGTH_LONG)
                                .setActionTextColor(Color.WHITE);
                        View sbView = snackbar.getView();
                        sbView.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.start_with_phone_color));
                        snackbar.show();
                    } else {
                        joinFireBase(email_form, password_form, nickName_form);
                    }

                } else {
                    View view = findViewById(R.id.background_join2);
                    Snackbar snackbar = Snackbar
                            .make(view, "정보를 다 입력해주세요.", Snackbar.LENGTH_LONG)
                            .setActionTextColor(Color.WHITE);
                    View sbView = snackbar.getView();
                    sbView.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.start_with_phone_color));
                    snackbar.show();
                }


            }
        });
    }

    public void joinConnect() {
        String loginUrl = "http://133.186.135.41/zozo_join_upload.php";

        StringRequest request = new StringRequest(Request.Method.POST, loginUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonOutput = JsonUtil.getJSONObjectFrom(response);
                    String state = JsonUtil.getStringFrom(jsonOutput, "state");
                    String nickname = JsonUtil.getStringFrom(jsonOutput, "nickname");
                    String id = JsonUtil.getStringFrom(jsonOutput, "id");
                    String email = JsonUtil.getStringFrom(jsonOutput, "email");
                    String pass = JsonUtil.getStringFrom(jsonOutput, "pass");

                    if (state.equals("need_join")) {
                        Intent intent = new Intent(Join_Act2.this, Main_act.class);
                        intent.putExtra("id", id);
                        intent.putExtra("nickname", nickName_form);
                        DatabaseReference messageDatabase = FirebaseDatabase.getInstance().getReference();
                        messageDatabase.child("member").child(uid_form).child("nickname").setValue(nickName_form);
                        String dbName = "UserDatabase_Zoos";
                        int dataBaseVersion = 1;
                        database = new Database(Join_Act2.this, dbName, null, dataBaseVersion);
                        database.init();
                        database.insert(phoneNumber_form, password_form, nickName_form, "MEMBERINFO");
                        startActivity(intent);
                        finish();
                    } else if (state.equals("need_not_join")) {
                        Toast.makeText(Join_Act2.this, "아이디가 중복됩니다.", Toast.LENGTH_SHORT).show();
                    } else if (state.equals("incorrect")) {
                        Toast.makeText(Join_Act2.this, "비밀번호를 확인하세요.", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(Join_Act2.this, "인터넷 접속 상태 또는 값을 확인해주세요.", Toast.LENGTH_SHORT).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parameters = new HashMap<String, String>();
                String id = phoneNumberView.getText().toString();
                String pw = passWordView.getText().toString();

                parameters.put("id", id);
                parameters.put("password", pw);
                parameters.put("uid", uid_form);
                parameters.put("email", email_form);
                parameters.put("nickname", nickName_form);

                Log.d("join_act2_join", id + pw + uid_form + email_form + nickName_form);

                return parameters;
            }
        };
        requestQueue.add(request);
    }

    private void joinFireBase(final String email, final String pass, final String nickname) {
        final FirebaseAuth mAuth;
        mAuth = FirebaseAuth.getInstance();
        FirebaseAuth.getInstance().signOut();
        Log.d("joinFireBase", email + pass);
        mAuth.createUserWithEmailAndPassword(email, pass)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            uid_form = mAuth.getCurrentUser().getUid();
                            joinConnect();
                        } else {
                            View view = findViewById(R.id.background_join2);
                            Snackbar snackbar = Snackbar
                                    .make(view, "회원 가입에 실패했습니다 정보를 확인해주세요.", Snackbar.LENGTH_LONG)
                                    .setActionTextColor(Color.WHITE);
                            View sbView = snackbar.getView();
                            sbView.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.start_with_phone_color));
                            snackbar.show();
                        }
                    }
                });


    }


}
