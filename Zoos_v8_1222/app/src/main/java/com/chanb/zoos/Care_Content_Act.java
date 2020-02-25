package com.chanb.zoos;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LevelListDrawable;
import android.os.AsyncTask;

import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
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
import com.chanb.zoos.utils.MyHtmlTagHandler;
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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Care_Content_Act extends AppCompatActivity implements Html.ImageGetter {

    TextView webView;
    ImageView webView_nail;
    RequestQueue requestQueue;
    String id, board_no, tag, nickname;
    TabLayout tabs_content;
    List<Comment_item> dataList_comment;
    Comment_Adapter adapter;
    TextView total_view;
    RecyclerView commentRecycle;
    GlobalApplication globalApplication;
    Button comment_uploadBtn_care;
    EditText commentInsert;
    TextView content_tag_care, title_care_content;
    Database database;
    NestedScrollView scroll_test_care;
    asyncListener mListener;
    ImageButton backBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.care_content_act);
        try {
            setting();
            setTabs();
            connect();
            commentConnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setting() {

        backBtn = findViewById(R.id.backBtn_care_content);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        mListener = new asyncListener() {

            @Override

            public void onPost() {

                //Todo something
                setOnTop();

            }

        };

        scroll_test_care = findViewById(R.id.scroll_test_care);
        String[] temp = new String[2];
        String dbName = "UserDatabase_Zoos";
        int dataBaseVersion = 1;
        database = new Database(this, dbName, null, dataBaseVersion);
        database.init();
        temp = database.select("MEMBERINFO");
        globalApplication = new GlobalApplication();
        globalApplication.getWindow(this);
        board_no = getIntent().getStringExtra("no");
        id = temp[0];
        nickname = temp[2];
        webView = findViewById(R.id.webText);
        webView_nail = findViewById(R.id.webText_title);
        requestQueue = Volley.newRequestQueue(this);
        tabs_content = findViewById(R.id.tabs_content_care);
        total_view = findViewById(R.id.total_number_content_care);
        commentRecycle = findViewById(R.id.comment_reply_care);
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

        commentInsert = findViewById(R.id.comment_insert_content_care);
        commentInsert.setSelected(false);

        comment_uploadBtn_care = findViewById(R.id.comment_uploadBtn_care);
        comment_uploadBtn_care.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                replyUploadConnect();
            }
        });
        content_tag_care = findViewById(R.id.content_tag_care);

        title_care_content = findViewById(R.id.title_care_content);
    }

    public void setOnTop() {
        scroll_test_care.smoothScrollTo(0, 0);
    }

    private void setTabs() {
        View view1 = getLayoutInflater().inflate(R.layout.custom_tab_care, null);
        view1.findViewById(R.id.icon).setBackgroundResource(R.drawable.share_2_tab);
        TextView textView = view1.findViewById(R.id.text_care);
        textView.setText("공유하기");
        tabs_content.addTab(tabs_content.newTab().setCustomView(view1));

        View view2 = getLayoutInflater().inflate(R.layout.custom_tab_care, null);
        view2.findViewById(R.id.icon).setBackgroundResource(R.drawable.paw);
        TextView textView2 = view2.findViewById(R.id.text_care);
        textView2.setText("저장하기");
        tabs_content.addTab(tabs_content.newTab().setCustomView(view2));

        View view3 = getLayoutInflater().inflate(R.layout.custom_tab_care, null);
        view3.findViewById(R.id.icon).setBackgroundResource(R.drawable.calendar_tab);
        TextView tabTitle = (TextView) view3.findViewById(R.id.text_care);
        tabTitle.setText("일정등록하기");
        tabs_content.addTab(tabs_content.newTab().setCustomView(view3));

        tabs_content.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();

                //공유하기 버튼
                if (position == 0) {
                    share();
                } else if (position == 1) {
                    //좋아요 버튼
                    int no = Integer.valueOf(board_no);
                    likeConnect(no);
                } else if (position == 2) {
                    //달력에 등록하기 버튼
                    tag = content_tag_care.getText().toString();
                    Intent intent = new Intent(Care_Content_Act.this, Calendar_input_act.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                            | Intent.FLAG_ACTIVITY_CLEAR_TOP
                            | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    intent.putExtra("id", id);
                    int tag_check = tag.indexOf("#");
                    if (tag_check != -1) {
                        String[] tag_temp = tag.split("#");
                        intent.putExtra("tag", tag_temp[1]);
                    } else {
                        intent.putExtra("tag", tag);
                    }

                    startActivity(intent);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                int position = tab.getPosition();

                //공유하기 버튼
                if (position == 0) {
                    share();
                } else if (position == 1) {
                    //좋아요 버튼
                    int no = Integer.valueOf(board_no);
                    likeConnect(no);
                } else if (position == 2) {
                    //달력에 등록하기 버튼
                    tag = content_tag_care.getText().toString();
                    Intent intent = new Intent(Care_Content_Act.this, Calendar_input_act.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                            | Intent.FLAG_ACTIVITY_CLEAR_TOP
                            | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    intent.putExtra("id", id);
                    int tag_check = tag.indexOf("#");
                    if (tag_check != -1) {
                        String[] tag_temp = tag.split("#");
                        intent.putExtra("tag", tag_temp[1]);
                    } else {
                        intent.putExtra("tag", tag);
                    }

                    startActivity(intent);
                }
            }
        });
    }

    //케어 게시물 읽기.
    public void connect() {
        String SNSUrl = "http://133.186.135.41/zozo_sns_content.php";
        StringRequest request = new StringRequest(Request.Method.POST, SNSUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray jArray = new JSONObject(response).getJSONArray("data");
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
                        content = replace(content);
                        String htmlText = content.replace("span style=\"color:", "font color=\"").replace("</span>", "</font>");
                        content = replaceRGBColorsWithHex(htmlText);
                        test_input(content);
                        title_input(title);

                        content_tag_care.setText(tag);
                        String url = "http://133.186.135.41" + img;
                        Glide.with(webView_nail)
                                .load(url)
                                .apply(new RequestOptions()
                                        .placeholder(R.drawable.placeholder)
                                        .error(R.drawable.placeholder)
                                        .dontTransform()
                                ).into(webView_nail);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            private void title_input(String title) {
                String keyWord = "nickname";
                int temp;

                temp = title.indexOf(keyWord);
                if (temp == -1) {
                    title_care_content.setText(Html.fromHtml(title));
                } else {
                    title = title.substring(temp + keyWord.length() + 1);
                    String text = "<font color=#3583fb>" + nickname + "님의 </font> <font color=#262626>" + title + "</font>";
                    title_care_content.setText(Html.fromHtml(text));
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(Care_Content_Act.this, "인터넷 접속 상태를 확인해주세요.", Toast.LENGTH_SHORT).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parameters = new HashMap<String, String>();
                parameters.put("no", board_no);
                parameters.put("id", id);
                parameters.put("type", "care");
                return parameters;
            }
        };
        requestQueue.add(request);
    }

    // 글자만 출력
    public void test_input(String content) {
        Spanned spanned = Html.fromHtml(content, this, null);
        webView.setText(spanned);
    }

    //font tag 제거
    private String replace(String html) {
        // using regular expression to find all occurences of rgb(a,b,c) using
        // capturing groups to get separate numbers.
        Pattern p = Pattern.compile("(<font color=\"#(\\w+)\">)");
        Matcher m = p.matcher(html);

        while (m.find()) {
            // get whole matched rgb(a,b,c) text
            String foundRGBColor = m.group(1);
            //String hexColorString = "rgb(#" + rHexFormatted + gHexFormatted + bHexFormatted + ")";
            String hexColorString = "";
            html = html.replaceAll(Pattern.quote(foundRGBColor), hexColorString);
        }

        Pattern p_end = Pattern.compile("(</font>)");
        Matcher m_end = p_end.matcher(html);

        while (m_end.find()) {
            // get whole matched rgb(a,b,c) text
            String foundRGBColor = m_end.group(1);
            //String hexColorString = "rgb(#" + rHexFormatted + gHexFormatted + bHexFormatted + ")";
            String hexColorString = "";
            html = html.replaceAll(Pattern.quote(foundRGBColor), hexColorString);
        }

        return html;
    }

    //그림을 출력
    @Override
    public Drawable getDrawable(String source) {
        LevelListDrawable d = new LevelListDrawable();
        Drawable empty = getResources().getDrawable(R.drawable.round_upload);
        d.addLevel(0, 0, empty);
        d.setBounds(0, 0, empty.getIntrinsicWidth(), empty.getIntrinsicHeight());
        new LoadImage(mListener).execute(source, d);
        return d;
    }

    //댓글 정보 json from server
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

    //insert comment on care
    protected void replyUploadConnect() {
        String sendUrl = "http://133.186.135.41/zozo_sns_comment_insert.php";
        StringRequest request = new StringRequest(Request.Method.POST, sendUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                JSONObject jsonOutput = null;
                String text = null;
                try {
                    jsonOutput = JsonUtil.getJSONObjectFrom(response);
                    text = JsonUtil.getStringFrom(jsonOutput, "state");
                    if (text.equals("sus1") || text.equals("sus2")) {
                        commentConnect();
                        commentInsert.setText("");
                        //upload_btn.setEnabled(true);
                    } else if (text.equals("fail_upload")) {
                        Toast.makeText(Care_Content_Act.this, "등록 실패.", Toast.LENGTH_SHORT).show();
                        //upload_btn.setEnabled(true);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(Care_Content_Act.this, "인터넷 또는 자료값 확인.", Toast.LENGTH_SHORT).show();
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

    //추천 관련 서버 접속
    private void likeConnect(final int number) {
        String url = "http://133.186.135.41/zozo_sns_like.php";

        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = JsonUtil.getJSONObjectFrom(response);
                    String state = JsonUtil.getStringFrom(jsonObject, "state");
                    if (state.equals("sns_double_time")) {

                        Snackbar snackbar = Snackbar
                                .make(webView, "저장소에 저장 취소!", Snackbar.LENGTH_LONG)
                                .setActionTextColor(Color.WHITE)
                                .setAction("실행 취소", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        likeConnect(number);
                                    }
                                });
                        View sbView = snackbar.getView();
                        sbView.setBackgroundColor(ContextCompat.getColor(getApplication().getApplicationContext(), R.color.start_with_phone_color));
                        snackbar.show();

                    } else if (state.equals("sns_like_update")) {
                        Snackbar snackbar = Snackbar
                                .make(webView, "저장소에 저장 완료!", Snackbar.LENGTH_LONG)
                                .setActionTextColor(Color.WHITE)
                                .setAction("실행 취소", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        likeConnect(number);
                                    }
                                });
                        View sbView = snackbar.getView();
                        sbView.setBackgroundColor(ContextCompat.getColor(getApplication().getApplicationContext(), R.color.start_with_phone_color));
                        snackbar.show();

                    }
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
                parameters.put("kind", "notice");
                return parameters;
            }
        };
        requestQueue.add(request);
    }

    private void share() {
        String url = "http://133.186.135.41/zozoSNS/" + "onlylogo.png";
        try {
            FeedTemplate params = FeedTemplate
                    .newBuilder(ContentObject.newBuilder("알려쥬",
                            url,
                            LinkObject.newBuilder().setWebUrl("https://developers.kakao.com")
                                    .setMobileWebUrl("https://developers.kakao.com").build())
                            .setDescrption("test")
                            .build())
                    //좋아요 등
                    //.setSocial(SocialObject.newBuilder().setLikeCount(10).setCommentCount(20)
                    //.setSharedCount(30).setViewCount(40).build())
                    .addButton(new ButtonObject("앱에서 보기", LinkObject.newBuilder()
                            .setWebUrl("'https://developers.kakao.com")
                            .setMobileWebUrl("'https://developers.kakao.com")
                            .setAndroidExecutionParams("no=" + board_no + "&kind=care")
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


    //인터넷 글에서 사진 정의하기 위한 부분.
    public class LoadImage extends AsyncTask<Object, Void, Bitmap> {

        private LevelListDrawable mDrawable;
        asyncListener asyncListener;

        public LoadImage(asyncListener listener) {
            this.asyncListener = listener;
        }

        @Override
        protected Bitmap doInBackground(Object... params) {
            String source = (String) params[0];
            mDrawable = (LevelListDrawable) params[1];
            try {
                source = "http://133.186.135.41" + source;
                InputStream is = new URL(source).openStream();
                return BitmapFactory.decodeStream(is);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (bitmap != null) {
                BitmapDrawable d = new BitmapDrawable(bitmap);
                mDrawable.addLevel(1, 1, d);
                DisplayMetrics displayMetrics = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                int width = displayMetrics.widthPixels;// 가로;
                int height = 720; //세로;

                //bitmap.getWidth, getHeight 원본 이미지 size setting 였었음.
                mDrawable.setBounds(0, 0, width, height);
                mDrawable.setLevel(1);
                CharSequence t = webView.getText();
                webView.setText(t);
            } else {
                CharSequence t = webView.getText();
                webView.setText(t);
            }

            if (this.asyncListener != null) {
                this.asyncListener.onPost();

            }
        }


    }

    //span style color to font color.
    private static String replaceRGBColorsWithHex(String html) {
        // using regular expression to find all occurences of rgb(a,b,c) using
        // capturing groups to get separate numbers.
        Pattern p = Pattern.compile("(rgb\\(\\s*(\\d+)\\s*,\\s*(\\d+)\\s*,\\s*(\\d+)\\s*\\);)");
        Matcher m = p.matcher(html);

        while (m.find()) {
            // get whole matched rgb(a,b,c) text
            String foundRGBColor = m.group(1);
            System.out.println("Found: " + foundRGBColor);

            // get r value
            String rString = m.group(2);
            // get g value
            String gString = m.group(3);
            // get b value
            String bString = m.group(4);

            System.out.println(" separated r value: " + rString);
            System.out.println(" separated g value: " + gString);
            System.out.println(" separated b value: " + bString);

            // converting numbers from string to int
            int rInt = Integer.parseInt(rString);
            int gInt = Integer.parseInt(gString);
            int bInt = Integer.parseInt(bString);

            // converting int to hex value
            String rHex = Integer.toHexString(rInt);
            String gHex = Integer.toHexString(gInt);
            String bHex = Integer.toHexString(bInt);

            // add leading zero if number is small to avoid converting
            // rgb(1,2,3) to rgb(#123)
            String rHexFormatted = String.format("%2s", rHex).replace(" ", "0");
            String gHexFormatted = String.format("%2s", gHex).replace(" ", "0");
            String bHexFormatted = String.format("%2s", bHex).replace(" ", "0");

            System.out.println(" converted " + rString + " to hex: " + rHexFormatted);
            System.out.println(" converted " + gString + " to hex: " + gHexFormatted);
            System.out.println(" converted " + bString + " to hex:" + bHexFormatted);

            // concatenate new color in hex
            //String hexColorString = "rgb(#" + rHexFormatted + gHexFormatted + bHexFormatted + ")";
            String hexColorString = "#" + rHexFormatted + gHexFormatted + bHexFormatted;

            System.out.println("  replacing " + foundRGBColor + " with " + hexColorString);
            html = html.replaceAll(Pattern.quote(foundRGBColor), hexColorString);
            Log.d("COLOR_TEST", html);
        }
        return html;
    }


    //test for span background.
    public void testStylesFromHtml() {
        Spanned s = Html.fromHtml("<span style=\"color:#FF0000; background-color:#00FF00; "
                + "text-decoration:line-through;\">style</span>");
        SpannableString spannableString = new SpannableString(webView.getText().toString());
        ForegroundColorSpan foregroundSpan = new ForegroundColorSpan(Color.RED);
        BackgroundColorSpan backgroundSpan = new BackgroundColorSpan(Color.YELLOW);
        spannableString.setSpan(foregroundSpan, 0, spannableString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(backgroundSpan, 0, spannableString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        webView.setText(spannableString);
    }
}
