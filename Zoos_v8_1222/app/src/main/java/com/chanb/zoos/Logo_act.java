package com.chanb.zoos;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
import com.chanb.zoos.utils.Database;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.kakao.auth.AuthType;
import com.kakao.auth.Session;
import com.nhn.android.naverlogin.OAuthLogin;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import static com.kakao.util.helper.Utility.getPackageInfo;

public class Logo_act extends AppCompatActivity {

    RequestQueue requestQueue;
    ImageView logo;
    Button start_login_text;
    ImageView startWithKakao, startWithNaver;
    public static Logo_act _logo_act;
    Database database;
    GlobalApplication globalApplication;
    TextView startWithPhone;
    String no;
    String getId, getPass;
    Loading_Dialog loading_dialog;

    //네이버 가입 관련
    N_Handler_Logo n_handler_logo;
    private static OAuthLogin naverLoginInstance;
    public static final String CLIENT_ID = "Bfy77HjCHEzc5ZnpNETX";
    private static final String CLIENT_SECRET = "zlyJiqM2gc";
    private static final String CLIENT_NAME = "네이버 아이디로 로그인 테스트";
    private final L_Handler l_handler = new L_Handler(this);
    static MThread mThread;

    //네이버 response
    public static String id_naver, naver_nickname, naver_email;
    public static String test;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.logo_act);
        try {
            Log.d("Logo_Act", getKeyHash(this) + "hash key");
            setting();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setting() {
        loading_dialog = new Loading_Dialog(this);
        setNaver_login();
        _logo_act = Logo_act.this;

        requestQueue = Volley.newRequestQueue(getApplicationContext());
        start_login_text = findViewById(R.id.start_login_text);
        globalApplication = new GlobalApplication();
        globalApplication.getWindow(this);

        //자동 로그인 구현을 위한 DB 검색.
        String dbName = "UserDatabase_Zoos";
        int dataBaseVersion = 1;
        database = new Database(this, dbName, null, dataBaseVersion);
        database.init();
        String[] temp = database.select("MEMBERINFO");
        if (temp != null) {
            getId = temp[0];
            getPass = temp[1];
            loginConnect();
        }
        database.close();

        logo = findViewById(R.id.logo_img);
        //핸드폰으로 가입하기 기본.
        startWithPhone = findViewById(R.id.startWithPhone);
        startWithPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Logo_act.this, Join_Act2.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                        | Intent.FLAG_ACTIVITY_CLEAR_TOP
                        | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
            }
        });

        //로그인하기.
        start_login_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Logo_act.this, Login_act.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                        | Intent.FLAG_ACTIVITY_CLEAR_TOP
                        | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
            }
        });

        //네이버로 가입하기.
        startWithNaver = findViewById(R.id.startWithNaver);
        startWithNaver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                naverLoginInstance.startOauthLoginActivity(Logo_act.this, n_handler_logo);
                mThread = new MThread(l_handler);
                mThread.setDaemon(true);
                mThread.start();
            }
        });

        //카카오톡으로 가입하기.
        startWithKakao = findViewById(R.id.startWithKakao);
        startWithKakao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Session session = Session.getCurrentSession();
                session.addCallback(new Kakao_SessionCallback_Logo());
                session.open(AuthType.KAKAO_ACCOUNT, Logo_act.this);
            }
        });
    }

    private void loginFireBase(final String _id, String email, final String pass, final String nickname) {
        FirebaseAuth mAuth;
        mAuth = FirebaseAuth.getInstance();
        Log.d("loginFireBaseLogo_act", email + pass);

        mThread = new MThread(l_handler);
        mThread.setDaemon(true);
        mThread.start();

        Message msg = new Message();
        msg.what = 1;
        msg_progress(msg);

        mAuth.signInWithEmailAndPassword(email, pass)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Intent intent = new Intent(Logo_act.this, Main_act.class);
                            intent.putExtra("nickname", nickname);
                            intent.putExtra("id", _id);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                                    | Intent.FLAG_ACTIVITY_CLEAR_TOP
                                    | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            if (loading_dialog != null) {
                                loading_dialog.dismiss();
                            }
                            database.insert(_id, pass, nickname, "MEMBERINFO");
                            startActivity(intent);
                            finish();
                        } else {
                            if (loading_dialog != null) {
                                loading_dialog.dismiss();
                            }
                            Toast.makeText(Logo_act.this, "구글 계정 로그인 실패", Toast.LENGTH_SHORT).show();
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
                    Log.d("Logo_act", response);
                    if (state.equals("need_join")) {
         /*               Intent intent = new Intent(Logo_act.this, Join_Act.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                                | Intent.FLAG_ACTIVITY_CLEAR_TOP
                                | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        startActivity(intent);*/
                    } else if (state.equals("need_not_join")) {
                        loginFireBase(id, email, pass, nickname);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parameters = new HashMap<String, String>();
                parameters.put("id", getId);
                parameters.put("pw", getPass);
                return parameters;
            }
        };
        requestQueue.add(request);
    }

    // 네이버 로그인 정보 받기.
    private void setNaver_login() {
        init();
    }

    private void init() {
        n_handler_logo = new N_Handler_Logo();
        naverLoginInstance = OAuthLogin.getInstance();
        naverLoginInstance.init(this, CLIENT_ID, CLIENT_SECRET, CLIENT_NAME);
    }

    protected static class RequestApiTask extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... voids) {
            String url = "https://openapi.naver.com/v1/nid/me";
            String at = naverLoginInstance.getAccessToken(_logo_act);
            return naverLoginInstance.requestApi(_logo_act, at, url);
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
                test = response.toString();
                id_naver = response.getString("id");
                naver_nickname = response.getString("nickname");
                naver_email = response.getString("email");
                //String name = response.getString("name");

                Message msg = new Message();
                msg.what = 0;
                msg_progress(msg);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    private static void msg_progress(Message msg2) {
        mThread.mBackHandler.sendMessage(msg2);
    }

    private void moveAct() {
        Intent intent = new Intent(_logo_act, Join_Act2.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra("kind", "naver");
        intent.putExtra("test", test);
        intent.putExtra("id", id_naver);
        intent.putExtra("nickname", naver_nickname);
        intent.putExtra("email", naver_email);
        _logo_act.startActivity(intent);
    }

    //로딩 이미지 및 네이버 로그인을 위한 핸들러
    private final class L_Handler extends Handler {

        private final WeakReference<Logo_act> ref;

        private L_Handler(Logo_act act) {
            ref = new WeakReference<Logo_act>(act);
        }

        @Override
        public void handleMessage(Message msg) {
            Logo_act act = ref.get();
            if (act != null) {
                switch (msg.what) {
                    case 0:
                        Logo_act logo_act = new Logo_act();
                        logo_act.moveAct();
                        break;

                    case 1:
                        dialog_show();
                        break;
                }
            }

        }

        private final Runnable runnable = new Runnable() {
            @Override
            public void run() {

            }
        };


    }

    private void dialog_show() {
        loading_dialog.show();
        loading_dialog.setCancelable(false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (loading_dialog != null) {
            loading_dialog.dismiss();
        }
        String test = database.getDatabaseName();
        Log.d("Logo_act_database", test);
        database.close();
    }

    public static String getKeyHash(final Context context) {
        PackageInfo packageInfo = getPackageInfo(context, PackageManager.GET_SIGNATURES);
        if (packageInfo == null)
            return null;

        for (Signature signature : packageInfo.signatures) {
            try {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                return Base64.encodeToString(md.digest(), Base64.NO_WRAP);
            } catch (NoSuchAlgorithmException e) {
                Log.d("Logo_Act", "Unable to get MessageDigest. signature=" + signature, e);
            }
        }
        return null;
    }
}