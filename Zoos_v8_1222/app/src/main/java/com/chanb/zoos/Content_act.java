package com.chanb.zoos;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.text.TextUtils;
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
import com.bumptech.glide.request.RequestOptions;

import com.chanb.zoos.utils.Database;
import com.kakao.kakaolink.v2.KakaoLinkResponse;
import com.kakao.kakaolink.v2.KakaoLinkService;
import com.kakao.message.template.ButtonObject;
import com.kakao.message.template.ContentObject;
import com.kakao.message.template.FeedTemplate;
import com.kakao.message.template.LinkObject;
import com.kakao.network.ErrorResult;
import com.kakao.network.callback.ResponseCallback;
import com.kakao.util.exception.KakaoException;
import com.kakao.util.helper.log.Logger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Content_act extends AppCompatActivity {

    Comment_Adapter adapter;
    Content_Img_Adapter adapter_content;
    RecyclerView commentRecycle, contentImgRecycle;
    String board_no, id, target_id, kind, check, temp_number;
    RequestQueue requestQueue;
    ImageView back, like_img, profile_view, share_content;
    List<Comment_item> dataList_comment;
    List<Content_Img_item> dataList_content;
    TextView content_view, writer_view, time_view, like_number, tag_view, total_view;
    GlobalApplication globalApplication;
    EditText commentInsert;
    Button commentUpload;
    NestedScrollView scrollView;
    Database database;
    String like_number_string;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_act);

        try {
            setting();
            contentConnect();
            commentConnect();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setting() {

        String[] temp = new String[2];
        String dbName = "UserDatabase_Zoos";
        int dataBaseVersion = 1;
        database = new Database(this, dbName, null, dataBaseVersion);
        database.init();
        temp = database.select("MEMBERINFO");

        board_no = getIntent().getStringExtra("no");
        id = getIntent().getStringExtra("id");
        kind = getIntent().getStringExtra("kind");

        commentUpload = findViewById(R.id.comment_uploadBtn);
        commentUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                replyUploadConnect();
            }
        });
        commentInsert = findViewById(R.id.comment_insert_content);
        requestQueue = Volley.newRequestQueue(this);
        content_view = findViewById(R.id.content_content);
        writer_view = findViewById(R.id.content_writer);
        time_view = findViewById(R.id.time_view_content);
        like_img = findViewById(R.id.like_img_content);
        like_number = findViewById(R.id.like_txt_content);
        tag_view = findViewById(R.id.content_tag);
        profile_view = findViewById(R.id.content_profile);
        total_view = findViewById(R.id.total_number_content);
        scrollView = findViewById(R.id.content_act_scroll);

        globalApplication = new GlobalApplication();
        globalApplication.getWindow(this);

        commentRecycle = findViewById(R.id.reviewList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        commentRecycle.setLayoutManager(layoutManager);
        commentRecycle.setLayoutManager(new LinearLayoutManager(getApplicationContext()) {
            @Override
            public boolean canScrollVertically() { // 세로스크롤 막기
                return false;
            }

            @Override
            public boolean canScrollHorizontally() { //가로 스크롤막기
                return false;
            }
        });

        contentImgRecycle = findViewById(R.id.content_img_container);
        RecyclerView.LayoutManager horizontalLayoutManager = new LinearLayoutManager(getApplicationContext());
        ((LinearLayoutManager) horizontalLayoutManager).setOrientation(LinearLayoutManager.HORIZONTAL);
        contentImgRecycle.setLayoutManager(horizontalLayoutManager);

        //페이저처럼 아이템 바이 아이템 하기 위함. 스크롤 관련.
        SnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(contentImgRecycle);

        back = findViewById(R.id.backButton_content);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        share_content = findViewById(R.id.share_content);

    }

    //댓글 정보 json from server
    public void commentConnect() {
        String url = "http://133.186.135.41/zozo_sns_comment_story.php";

        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    Log.d("STORY_DATA", response);
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

    //setting data into comment view
    void doJSONParser(String response) {

        dataList_comment = new ArrayList<>();

        try {
            JSONObject jsonOutput = JsonUtil.getJSONObjectFrom(response);
            String total = JsonUtil.getStringFrom(jsonOutput, "total");
            total_view.setText("댓글 " + total);

            if (total.equals("0")) {
                commentRecycle.setVisibility(View.GONE);
                //Toast.makeText(this, "댓글이 없습니다.", Toast.LENGTH_SHORT).show();
            } else {
                JSONArray jArray = new JSONObject(response).getJSONArray("data");
                commentRecycle.setVisibility(View.VISIBLE);

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
        commentRecycle.setAdapter(adapter);

    }

    //본문 정보 from server
    private void contentConnect() {
        String url = "http://133.186.135.41/zozo_sns_content_story.php";

        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("contentViewSetting", response);
                try {
                    contentDoJSONParser(response);
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
                parameters.put("id", id);
                parameters.put("type", "story");
                return parameters;
            }
        };
        requestQueue.add(request);
    }

    //setting data into content view
    void contentDoJSONParser(String response) {
        try {
            JSONArray jArray = new JSONObject(response).getJSONArray("data");

            if (jArray.length() == 0) {

            } else {

                for (int i = 0; i < jArray.length(); i++) {
                    JSONObject jObject = jArray.getJSONObject(i);
                    String title = jObject.getString("title");
                    String content = jObject.getString("content");
                    String img = jObject.getString("img1");
                    String like_check = jObject.getString("like_check");
                    String like = jObject.getString("like");
                    String tag = jObject.getString("tag");
                    String time = jObject.getString("time");
                    String view = jObject.getString("view");
                    String writer = jObject.getString("writer");
                    String writer_id = jObject.getString("writer_id");
                    String profile = jObject.getString("profile");
                    target_id = writer_id;

                    //본문 이미지 세팅
                    contentViewSetting(title, content, img, like_check, like, tag, time, view, writer, profile);


                }
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void contentViewSetting(final String title, String content, String img, String likeCheck, final String like, String tag, String time, String view, String writer, final String profile) {
        content_view.setText(content);
        writer_view.setText(writer);
        like_number_string = like;
        time_view.setText(time + " | " + "조회 " + view);
        like_number.setText(like);
        tag_view.setText(tag);
        check = likeCheck;
        final String profile_form = "http://133.186.135.41/zozoPetProfile/" + profile;

        //  프로필 이미지
        Glide.with(this)
                .load(profile_form)
                .apply(new RequestOptions()
                        .placeholder(R.drawable.manprivate)
                        .error(R.drawable.manprivate)
                        .skipMemoryCache(true)
                        .dontTransform()
                        .centerCrop()
                        .circleCrop()
                        .override(100, 100)
                )
                .into(profile_view);

        profile_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Content_act.this, Story_act.class);
                intent.putExtra("id", id);
                intent.putExtra("target_id", target_id);
                startActivity(intent);
            }
        });


        dataList_content = new ArrayList<>();


        String[] img_split = img.split(":");
        int max = img_split.length;
        Log.d("contentViewSetting", max + " " + img + " " + likeCheck);
        for (int i = 0; i < max; i++) {
            dataList_content.add(new Content_Img_item(img_split[i]));
        }

        adapter_content = new Content_Img_Adapter(dataList_content);
        contentImgRecycle.setAdapter(adapter_content);

        if (!likeCheck.equals("a")) {
            //  좋아요 이미지.
            Glide.with(this)
                    .load(R.drawable.icon_like_after)
                    .apply(new RequestOptions()
                            .placeholder(R.drawable.placeholder)
                            .error(R.drawable.placeholder)
                            .skipMemoryCache(true)
                            .dontTransform()
                    )
                    .into(like_img);
        }

        like_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int temp_board = Integer.parseInt(board_no);
                likeConnect(temp_board);
                if (check.equals("a")) {
                    //추천
                    like_img.setImageResource(R.drawable.icon_like_after);
                    check = board_no;
                } else {
                    //추천취소
                    // 추천 감소
                    like_img.setImageResource(R.drawable.icon_like_before);
                    check = "a";
                }
            }
        });

        // 공유 버튼 데이터.
        final String imgFirst = "http://133.186.135.41/zozoSNS/" + img_split[0];
        share_content.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    FeedTemplate params = FeedTemplate
                            .newBuilder(ContentObject.newBuilder("알려쥬",
                                    imgFirst,
                                    LinkObject.newBuilder().setWebUrl("https://developers.kakao.com")
                                            .setMobileWebUrl("https://developers.kakao.com").build())
                                    .setDescrption(title)
                                    .build())
                            //좋아요 등
                            //.setSocial(SocialObject.newBuilder().setLikeCount(10).setCommentCount(20)
                            //.setSharedCount(30).setViewCount(40).build())
                            .addButton(new ButtonObject("앱에서 보기", LinkObject.newBuilder()
                                    .setWebUrl("'https://developers.kakao.com")
                                    .setMobileWebUrl("'https://developers.kakao.com")
                                    .setAndroidExecutionParams("no=" + board_no + "&kind=content")
                                    .build()))
                            .build();
                    Map<String, String> serverCallbackArgs = new HashMap<String, String>();
                    serverCallbackArgs.put("user_id", "${current_user_id}");
                    serverCallbackArgs.put("product_id", "${shared_product_id}");
                    KakaoLinkService.getInstance().sendDefault(getApplicationContext(), params, serverCallbackArgs, new ResponseCallback<KakaoLinkResponse>() {
                        @Override
                        public void onFailure(ErrorResult errorResult) {
                            Logger.e(errorResult.toString());
                        }

                        @Override
                        public void onSuccess(KakaoLinkResponse result) {
                            // 템플릿 밸리데이션과 쿼터 체크가 성공적으로 끝남. 톡에서 정상적으로 보내졌는지 보장은 할 수 없다. 전송 성공 유무는 서버콜백 기능을 이용하여야 한다.
                        }
                    });
                } catch (KakaoException k) {
                    k.printStackTrace();
                }

            }
        });

    }

    //추천 관련 서버 접속
    private void likeConnect(final int number) {
        String url = "http://133.186.135.41/zozo_sns_like.php";

        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.d("like", response.toString());
                try {
                    JSONObject jsonObject = JsonUtil.getJSONObjectFrom(response);
                    String state = JsonUtil.getStringFrom(jsonObject, "state");
                    String number_temp = JsonUtil.getStringFrom(jsonObject, "row");
                    like_number.setText(number_temp);
                    likeBtn(state, number);
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
                parameters.put("id", id);
                parameters.put("no", number + "");
                parameters.put("kind", "story");
                Log.d("like", id + number + "");

                return parameters;
            }
        };
        requestQueue.add(request);
    }

    protected void replyUploadConnect() {
        String sendUrl = "http://133.186.135.41/zozo_sns_comment_insert_story.php";
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
                        commentConnect();
                        scrollView.smoothScrollTo(0, 0);
                        commentInsert.setText("");
                        //upload_btn.setEnabled(true);
                    } else if (text.equals("fail_upload")) {
                        Toast.makeText(Content_act.this, "등록 실패.", Toast.LENGTH_SHORT).show();
                        //upload_btn.setEnabled(true);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(Content_act.this, "인터넷 또는 자료값 확인.", Toast.LENGTH_SHORT).show();
                //upload_btn.setEnabled(true);

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parameters = new HashMap<String, String>();
                String comment = commentInsert.getText().toString();
                String id_local = id;
                parameters.put("id", id_local);
                parameters.put("no", board_no);
                parameters.put("re_comment", comment);
                return parameters;
            }
        };
        requestQueue.add(request);
    }

    private void likeBtn(String state, final int number) {
        if (state.equals("sns_double_time")) {

            Snackbar snackbar = Snackbar
                    .make(content_view, "저장소에 저장 취소!", Snackbar.LENGTH_LONG)
                    .setActionTextColor(Color.WHITE)
                    .setAction("실행 취소", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            likeConnect(number);
                            like_img.setImageResource(R.drawable.icon_like_after);
                            check = board_no;
                        }
                    });
            View sbView = snackbar.getView();
            sbView.setBackgroundColor(ContextCompat.getColor(getApplication().getApplicationContext(), R.color.start_with_phone_color));
            snackbar.show();

        } else if (state.equals("sns_like_update")) {

            Snackbar snackbar = Snackbar
                    .make(content_view, "저장소에 저장 완료!", Snackbar.LENGTH_LONG)
                    .setActionTextColor(Color.WHITE)
                    .setAction("실행 취소", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // 추천 감소
                            likeConnect(number);
                            like_img.setImageResource(R.drawable.icon_like_before);
                            check = "a";
                        }
                    });
            View sbView = snackbar.getView();
            sbView.setBackgroundColor(ContextCompat.getColor(getApplication().getApplicationContext(), R.color.start_with_phone_color));
            snackbar.show();

        }
    }


}
