package com.chanb.zoos;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.chanb.zoos.utils.Database;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.kakao.auth.AuthType;
import com.kakao.auth.Session;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.LogoutResponseCallback;

import com.nhn.android.naverlogin.OAuthLogin;
import com.nhn.android.naverlogin.ui.view.OAuthLoginButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

public class Login_act extends AppCompatActivity {
    RequestQueue requestQueue;
    ImageView naver, kakao, backButton;
    GlobalApplication globalApplication;
    Intent intent;
    EditText id_login, pw_login;
    Button login_btn;
    Logo_act logo_act;
    public static Login_act _Login_act;
    private OAuthLoginButton naver_login;
    private static OAuthLogin naverLoginInstance;
    public static final String CLIENT_ID = "Bfy77HjCHEzc5ZnpNETX";
    private static final String CLIENT_SECRET = "zlyJiqM2gc";
    private static final String CLIENT_NAME = "네이버 아이디로 로그인 테스트";
    N_Handler n_handler;
    TextView join;
    GlobalApplication application;
    Database database;
    Loading_Dialog loading_dialog;

    private final L_Handler l_handler = new L_Handler(this);
    static MThread mThread;
    static String id_naver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_act);
        viewSetting();
        setNaver_login();
        application = new GlobalApplication();
    }

    private void viewSetting() {
        _Login_act = this;
        loading_dialog = new Loading_Dialog(this);
        String dbName = "UserDatabase_Zoos";
        int dataBaseVersion = 1;
        database = new Database(this, dbName, null, dataBaseVersion);
        database.init();

        intent = new Intent();
        logo_act = Logo_act._logo_act;
        requestQueue = Volley.newRequestQueue(getApplicationContext());
        id_login = findViewById(R.id.id_login);
        pw_login = findViewById(R.id.pw_login);
        login_btn = findViewById(R.id.loginBtn_login);
        backButton = findViewById(R.id.backbutton_login);
        kakao = findViewById(R.id.kakao_btn);
        naver = findViewById(R.id.naver_btn);
        globalApplication = new GlobalApplication();
        globalApplication.getWindow(this);
        join = findViewById(R.id.join_login);

        Glide.with(getApplicationContext())
                .load(R.drawable.kakao_login)
                .into(kakao);

        Glide.with(getApplicationContext())
                .load(R.drawable.naver_login)
                .into(naver);

        //카카오톡으로 시작하기.
        kakao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Session session = Session.getCurrentSession();
                session.addCallback(new Kakao_SessionCallback());
                session.open(AuthType.KAKAO_ACCOUNT, Login_act.this);
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        //네이버로 시작하기.
        naver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                naverLoginInstance.startOauthLoginActivity(Login_act.this, n_handler);
                mThread = new MThread(l_handler);
                mThread.setDaemon(true);
                mThread.start();
            }
        });

        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginConnect();
            }
        });

        join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Login_act.this, Join_Act.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                        | Intent.FLAG_ACTIVITY_CLEAR_TOP
                        | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();

            }
        });

    }

    private void loginFireBase(final String email, final String pass, final String nickname, final String id) {
        FirebaseAuth mAuth;
        mAuth = FirebaseAuth.getInstance();
        mAuth.signOut();

        mThread = new MThread(l_handler);
        mThread.setDaemon(true);
        mThread.start();

        Message msg = new Message();
        msg.what = 1;
        msg_progress(msg);

        mAuth.signInWithEmailAndPassword(email, pass)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Intent intent = new Intent(_Login_act, Main_act.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                                    | Intent.FLAG_ACTIVITY_CLEAR_TOP
                                    | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            intent.putExtra("nickname", nickname);
                            intent.putExtra("id", id);
                            _Login_act.database.insert(id, pass, nickname, "MEMBERINFO");
                            if (_Login_act.loading_dialog != null) {
                                _Login_act.loading_dialog.dismiss();
                            }
                            _Login_act.startActivity(intent);
                            _Login_act.finish();
                        } else {
                            if (_Login_act.loading_dialog != null) {
                                _Login_act.loading_dialog.dismiss();
                            }
                            Toast.makeText(Login_act._Login_act, "구글 계정 로그인 실패", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void loginConnect() {
        String loginUrl = "http://133.186.135.41/zozo_login.php";

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
                        Toast.makeText(Login_act.this, "아이디가 존재하지 않습니다.", Toast.LENGTH_SHORT).show();
                    } else if (state.equals("need_not_join")) {
                        loginFireBase(email, pass, nickname, id);
                    } else if (state.equals("incorrect")) {
                        Toast.makeText(Login_act.this, "비밀번호를 확인하세요.", Toast.LENGTH_SHORT).show();
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(Login_act.this, "인터넷 접속 상태 또는 값을 확인해주세요.", Toast.LENGTH_SHORT).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parameters = new HashMap<String, String>();
                String id = id_login.getText().toString();
                String pw = pw_login.getText().toString();
                parameters.put("id", id);
                parameters.put("pw", pw);

                return parameters;
            }
        };
        requestQueue.add(request);
    }

    public void kakao_logout() {
        UserManagement.getInstance().requestLogout(new LogoutResponseCallback() {
            @Override
            public void onCompleteLogout() {

            }
        });
    }

    public void naver_logout() {

        if (_Login_act != null) {
            //naverLoginInstance.logout(mContext);
            new DeleteTokenTask().execute();
        } else
            Log.d("naver_logout", "logout_fail");
    }

    // 네이버 로그인 정보 받기.
    protected static class RequestApiTask extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... voids) {
            String url = "https://openapi.naver.com/v1/nid/me";
            String at = naverLoginInstance.getAccessToken(_Login_act);
            return naverLoginInstance.requestApi(_Login_act, at, url);
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.d("onPostExecuteNaver", "on");
            try {
                JSONObject jsonObject = new JSONObject(s);
                JSONObject response = jsonObject.getJSONObject("response");
                id_naver = response.getString("id");
                String nickname = response.getString("nickname");
                String name = response.getString("name");
                Log.d("naver_login", nickname + name);

                Message msg = new Message();
                msg.what = 0;
                msg_progress(msg);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    // 초기화
    private void setNaver_login() {
        init();
    }

    private void init() {
        n_handler = new N_Handler();
        naverLoginInstance = OAuthLogin.getInstance();
        naverLoginInstance.init(this, CLIENT_ID, CLIENT_SECRET, CLIENT_NAME);
    }

    //네이버 핸들러 동작.
    private static void msg_progress(Message msg2) {
        mThread.mBackHandler.sendMessage(msg2);
    }

    protected static class DeleteTokenTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            boolean isSuccessDeleteToken = naverLoginInstance.logoutAndDeleteToken(_Login_act);

            if (!isSuccessDeleteToken) {
                // 서버에서 token 삭제에 실패했어도 클라이언트에 있는 token 은 삭제되어 로그아웃된 상태이다
                // 실패했어도 클라이언트 상에 token 정보가 없기 때문에 추가적으로 해줄 수 있는 것은 없음
                Log.d(CLIENT_NAME, "errorCode:" + naverLoginInstance.getLastErrorCode(_Login_act));
                Log.d(CLIENT_NAME, "errorDesc:" + naverLoginInstance.getLastErrorDesc(_Login_act));
            }

            return null;
        }

        protected void onPostExecute(Void v) {
            //updateView();
        }
    }


    //네이버를 위한 액트 이동.
    public void moveAct() {
        loginConnect(id_naver);
    }

    //sns용 검증된 로그인
    public void loginConnect(final String id) {
        String loginUrl = "http://133.186.135.41/zozo_login_sns.php";
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
                    Log.d("Logo_act", response);
                    if (state.equals("need_join")) {
         /*               Intent intent = new Intent(Logo_act.this, Join_Act.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                                | Intent.FLAG_ACTIVITY_CLEAR_TOP
                                | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        startActivity(intent);*/
                    } else if (state.equals("need_not_join")) {
                        loginFireBase(email, pass, nickname, id);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Toast.makeText(Logo_act.this, "인터넷 접속 상태를 확인해주세요.", Toast.LENGTH_SHORT).show();
                Log.d("Logo_act", error.getMessage());
            }
        }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parameters = new HashMap<String, String>();
                parameters.put("id", id);

                return parameters;
            }
        };
        _Login_act.requestQueue.add(request);
    }

    //로딩 이미지
    private void dialog_show() {
        _Login_act.loading_dialog.show();
        _Login_act.loading_dialog.setCancelable(false);
    }

    private final class L_Handler extends Handler {

        private final WeakReference<Login_act> ref;

        private L_Handler(Login_act act) {
            ref = new WeakReference<Login_act>(act);
        }

        @Override
        public void handleMessage(Message msg) {
            Login_act act = ref.get();
            if (act != null) {
                Login_act login_act = new Login_act();
                switch (msg.what) {
                    case 0:
                        login_act.moveAct();
                        break;

                    case 1:
                        login_act.dialog_show();
                        break;
                }
            }

        }
    }

    @Override
    protected void onDestroy() {
        if (_Login_act.loading_dialog != null) {
            _Login_act.loading_dialog.dismiss();
        }
        database.close();
        super.onDestroy();
    }


}
