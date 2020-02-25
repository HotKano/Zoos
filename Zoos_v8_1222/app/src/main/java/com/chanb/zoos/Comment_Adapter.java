package com.chanb.zoos;

import android.content.Intent;
import android.media.Image;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.chanb.zoos.utils.Database;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Comment_Adapter extends RecyclerView.Adapter<Comment_Adapter.ViewHolder> {

    private final List<Comment_item> mDataList;
    Comment_item item;
    Database database;

    public Comment_Adapter(List<Comment_item> dataList) {
        mDataList = dataList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_act_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        item = mDataList.get(position);
        holder.nickname.setText(item.getRe_writer());
        holder.comment.setText(item.getRe_comment());
        holder.date.setText(item.getRe_time());

        String img = item.getProfile();
        String url = "http://133.186.135.41/zozoPetProfile/" + img;

        //프로파일 이미지
        Glide.with(holder.itemView.getContext())
                .load(url)
                .apply(new RequestOptions()
                        .placeholder(R.drawable.manprivate)
                        .error(R.drawable.manprivate)
                        .dontTransform()
                        .skipMemoryCache(false)
                        .centerCrop()
                        .override(100, 100)
                        .circleCrop())
                .into(holder.profile);

        try {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(Long.parseLong(item.getTime()));
            int mYear = calendar.get(Calendar.YEAR);
            int mMonth = calendar.get(Calendar.MONTH);
            int mDate = calendar.get(Calendar.DATE);
            int mTime = calendar.get(Calendar.HOUR_OF_DAY);
            int mMin = calendar.get(Calendar.MINUTE);
            String temp_result = interval(mYear, mMonth, mDate, mTime, mMin);
            holder.date.setText(temp_result);
        } catch (ParseException e) {
            e.printStackTrace();
        }


        try {
            btn(holder, item);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void btn(final Comment_Adapter.ViewHolder holder2, final Comment_item item2) {
        String dbName = "UserDatabase_Zoos";
        int dataBaseVersion = 1;
        String id = null;
        database = new Database(holder2.itemView.getContext(), dbName, null, dataBaseVersion);
        database.init();
        String[] temp = database.select("MEMBERINFO");
        if (temp != null) {
            id = temp[0];
        }
        final String finalId = id;
        holder2.profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(holder2.itemView.getContext(), Story_act.class);
                intent.putExtra("target_id", item2.getRe_writer_id());
                intent.putExtra("id", finalId);
                holder2.itemView.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView nickname, date, comment;
        ImageView profile;

        public ViewHolder(View itemView) {
            super(itemView);
            nickname = itemView.findViewById(R.id.nickName_reply);
            date = itemView.findViewById(R.id.date_reply);
            comment = itemView.findViewById(R.id.comment_reply);
            profile = itemView.findViewById(R.id.profile_reply);
        }


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
