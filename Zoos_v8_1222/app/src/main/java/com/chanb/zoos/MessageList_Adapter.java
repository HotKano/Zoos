package com.chanb.zoos;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
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

import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MessageList_Adapter extends RecyclerView.Adapter<MessageList_Adapter.ViewHolder> {

    private final List<MessageList_Item> mDataList;
    MessageList_Item item;
    String nickname;
    String temp;
    String TAG = "TEST_TIME_DATA";
    RequestQueue requestQueue;
    Main_act main_act;

    public MessageList_Adapter(List<MessageList_Item> dataList) {
        mDataList = dataList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        try {
            main_act = Main_act._Main_act;
            item = mDataList.get(position);
            String seen = item.getSeen();
            requestQueue = Volley.newRequestQueue(main_act);

            String title = item.getText();
            int length = title.length();
            if (length > 15) {
                title = title.substring(0, 15) + "......";
            }
            holder.message.setText(title);
            holder.id.setText(item.getNickname());

            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(Long.parseLong(item.getTime()));
            int mYear = calendar.get(Calendar.YEAR);
            int mMonth = calendar.get(Calendar.MONTH);
            int mDate = calendar.get(Calendar.DATE);
            int mTime = calendar.get(Calendar.HOUR_OF_DAY);
            int mMin = calendar.get(Calendar.MINUTE);

            String temp_result = interval(mYear, mMonth, mDate, mTime, mMin);

            holder.time.setText(temp_result);
            holder.btn_check.setVisibility(View.INVISIBLE);
            btn(holder, item, temp, seen);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void btn(final ViewHolder holder, final MessageList_Item item, final String url, String seen) {

        getImage(item.getFrom(), holder);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(holder.itemView.getContext(), MessageShow_Act.class);
                intent.putExtra("uid", item.getFrom());
                intent.putExtra("profile", url);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                        | Intent.FLAG_ACTIVITY_CLEAR_TOP
                        | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                holder.itemView.getContext().startActivity(intent);
            }
        });

        if (seen.equals("true")) {
            holder.seen.setVisibility(View.GONE);
            holder.background.setBackgroundColor(Color.WHITE);
        } else if (seen.equals("false")) {
            holder.seen.setVisibility(View.VISIBLE);
            holder.background.setBackgroundColor(Color.argb(255, 249, 249, 249));
        }
    }

    @Override
    public int getItemCount() {
        return (null != mDataList ? mDataList.size() : 0);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView message, id, time;
        ImageView profile, seen;
        RelativeLayout background;
        Button btn_check;

        public ViewHolder(View itemView) {
            super(itemView);
            message = itemView.findViewById(R.id.message_txt);
            id = itemView.findViewById(R.id.from);
            profile = itemView.findViewById(R.id.profile_message);
            seen = itemView.findViewById(R.id.seen_message);
            background = itemView.findViewById(R.id.message_list_item_background);
            time = itemView.findViewById(R.id.time_message);
            btn_check = itemView.findViewById(R.id.btn_message_list);

        }


    }

    private void getImage(final String uid, final ViewHolder holder) {
        final String[] txt = new String[1];
        String sendUrl = "http://133.186.135.41/zozo_sns_getImage.php";

        StringRequest request = new StringRequest(Request.Method.POST, sendUrl, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                JSONObject jsonOutput = null;
                String text = null;

                try {
                    jsonOutput = JsonUtil.getJSONObjectFrom(response);
                    text = JsonUtil.getStringFrom(jsonOutput, "state");
                    Log.d("MessageList_Adapter", response);
                    if (text.equals("sus1")) {
                        String temp_image = JsonUtil.getStringFrom(jsonOutput, "petProfile");
                        final String data_form = "http://133.186.135.41/zozoPetProfile/" + temp_image;
                        setImageName(data_form);

                        Glide.with(holder.itemView)
                                .load(temp)
                                .apply(new RequestOptions()
                                        .centerCrop()
                                        .placeholder(R.drawable.manprivate)
                                        .error(R.drawable.manprivate)
                                        .circleCrop())
                                .into(holder.profile);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }


            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(main_act, "인터넷 접속 확인", Toast.LENGTH_SHORT).show();
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

    private void setImageName(String name) {
        this.temp = name;
    }

    private String time(int time) {
        String result;
        if (time < 10) {
            result = "0" + time;
        } else {
            result = String.valueOf(time);
        }

        return result;
    }

    private String interval(int year_target, int month_target, int date_target, int hour_target, int min_target) throws ParseException {
        String result = "test";

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int date = calendar.get(Calendar.DATE);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int min = calendar.get(Calendar.MINUTE);
        String month_temp = null;
        String year_temp = String.valueOf(year);
        String date_temp = String.valueOf(date);

        //현재 월 변환
        switch (month) {
            case 1:
                month_temp = "January";
                break;
            case 2:
                month_temp = "Febuary";
                break;
            case 3:
                month_temp = "March";
                break;
            case 4:
                month_temp = "April";
                break;
            case 5:
                month_temp = "May";
                break;
            case 6:
                month_temp = "June";
                break;
            case 7:
                month_temp = "July";
                break;
            case 8:
                month_temp = "August";
                break;
            case 9:
                month_temp = "September";
                break;
            case 10:
                month_temp = "October";
                break;
            case 11:
                month_temp = "November";
                break;
            case 12:
                month_temp = "December";
                break;
        }

        //타겟 월 변환
        String mMonth_temp = String.valueOf(month_target);
        switch (month_target + 1) {
            case 1:
                mMonth_temp = "January";
                break;
            case 2:
                mMonth_temp = "Febuary";
                break;
            case 3:
                mMonth_temp = "March";
                break;
            case 4:
                mMonth_temp = "April";
                break;
            case 5:
                mMonth_temp = "May";
                break;
            case 6:
                mMonth_temp = "June";
                break;
            case 7:
                mMonth_temp = "July";
                break;
            case 8:
                mMonth_temp = "August";
                break;
            case 9:
                mMonth_temp = "September";
                break;
            case 10:
                mMonth_temp = "October";
                break;
            case 11:
                mMonth_temp = "November";
                break;
            case 12:
                mMonth_temp = "December";
                break;
        }

        String mDate_temp = String.valueOf(date_target);
        String mYear_temp = String.valueOf(year_target);
        String target_month = mMonth_temp + mDate_temp + "," + mYear_temp; // parse 형식 만들기 위해서.
        long d = Date.parse(target_month); // 밀리세컨드 구하기.
        String e = month_temp + date_temp + "," + year_temp; // parse 형식 만들기 위해서.
        long t = Date.parse(e); // 70년 1월 1일부터 오늘까지의 밀리세컨즈로의 변환.
        long now = (t - d) / 1000; // 살아온 초 계산 위해서 1000을 나누는 이유는 1000밀리세컨즈는 1초이기때문.

        long year_long = now / 31536000; // 1년의 초.
        long Month_long = (now % 31536000) / 2592000; // 달의 초. 앞의 나머지 연산자는 년을 계산한 다음에 남은 시간들을 월로 환산하기 위해서.
        long date_long = ((now % 31536000) % 2592000) / 86400; // 일의 초. 연산자는 월을 계산한 다음에 남은 시간들을 일로 환산하기 위해서.

        Date today = new Date();
        int h = today.getHours();
        int m = today.getMinutes();
        int s = today.getSeconds();

        String m_temp = time(m);
        String s_temp = time(s);
        m = Integer.valueOf(m_temp);
        s = Integer.valueOf(s_temp);

        SimpleDateFormat f = new SimpleDateFormat("HH:mm:ss", Locale.KOREA);
        Date d1 = null;
        String now_time = hour + ":" + min + ":00";
        String target_time = hour_target + ":" + min_target + ":00";
        d1 = f.parse(now_time); // now
        Date d2 = f.parse(target_time); // target
        long diff = d1.getTime() - d2.getTime();
        long sec = diff / 1000;


        if (year_long <= 0) {
            if (Month_long > 0) {
                String temp = String.valueOf(year_target);
                if (year == year_target) {
                    result = time(month_target + 1) + "월" + time(date_target) + "일";
                } else {
                    result = temp.substring(2, 4) + "년" + time(month_target + 1) + "월" + time(date_target) + "일";
                }
            } else {
                if (date_long > 0) {
                    if (date_long < 4) {
                        if (year == year_target) {
                            result = date_long + "일 전";
                        }
                    } else {
                        String temp = String.valueOf(year_target);
                        if (year == year_target) {
                            result = time(month_target + 1) + "월" + time(date_target) + "일";
                        } else {
                            result = temp.substring(2, 4) + "년" + time(month_target + 1) + "월" + time(date_target) + "일";
                        }
                    }
                } else {
                    long temp_sec = sec / 60;
                    long temp_time = temp_sec / 60;

                    if (temp_sec < 1) {
                        result = "방금";
                    } else if (temp_sec < 60) {
                        result = temp_sec + "분 전";
                    } else if (temp_time >= 1) {
                        result = temp_time + "시간 전";
                    }
                }
            }
        } else {
            String temp = String.valueOf(year_target);
            if (year == year_target) {
                result = time(month_target + 1) + "월" + time(date_target) + "일";
            } else {
                result = temp.substring(2, 4) + "년" + time(month_target + 1) + "월" + time(date_target) + "일";
            }
        }

        return result;
    }


}
