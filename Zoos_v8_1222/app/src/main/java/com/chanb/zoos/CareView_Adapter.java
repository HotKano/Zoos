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

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CareView_Adapter extends RecyclerView.Adapter<CareView_Adapter.ViewHolder> {

    private final List<CareItem> mDataList;

    public CareView_Adapter(List<CareItem> dataList) {
        mDataList = dataList;
    }

    public String getDateDay(int year, int month, int date) {


        String day = "";

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month);
        cal.set(Calendar.DATE, date);

        int dayNum = cal.get(Calendar.DAY_OF_WEEK);

        switch (dayNum) {
            case 1:
                day = "일";
                break;
            case 2:
                day = "월";
                break;
            case 3:
                day = "화";
                break;
            case 4:
                day = "수";
                break;
            case 5:
                day = "목";
                break;
            case 6:
                day = "금";
                break;
            case 7:
                day = "토";
                break;

        }
        return day;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_list_item, parent, false);
        return new ViewHolder(view);
    }

    //item view에 레이아웃 연결.
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        CareItem item = mDataList.get(position);

        String title = item.getText();
        String seen = item.getSeen();
        String time = item.getTime();
        String[] temp_time = time.split(":");
        int hour = Integer.valueOf(temp_time[0]);
        int min = Integer.valueOf(temp_time[1]);
        String year = item.getYear();
        String month = item.getMonth();
        String date = item.getDate();

        try {
            if (seen.equals("false")) {
                //#f9f9f9
                holder.background.setBackgroundColor(Color.argb(255, 249, 249, 249));
            } else {
                holder.background.setBackgroundColor(Color.WHITE);
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

        //tools:text="쥬스D케어봇 - [시바] 담당 "
        holder.id.setText("알람쥬 - [" + item.getPetName() + "]");

        int temp = title.indexOf("산책");

        if (temp != -1)
            title = "쥔님, 저 목줄이랑 배변봉투 꼭 챙겨주세요!";
        else
            title = "오늘의 케어 LIST 배달 완료 !";

        holder.message.setText(title);
        //String temp_result = interval(mYear, mMonth, mDate, mTime, mMin);
        Calendar c = Calendar.getInstance();
        int year_temp = c.get(Calendar.YEAR);
        int year_to_int = Integer.valueOf(year);

        //int year_target, int month_target, int date_target
        try {
            int year_interval = Integer.valueOf(year);
            int month_interval = Integer.valueOf(month);
            int date_interval = Integer.valueOf(date);

            String result_temp = interval(year_interval, month_interval, date_interval);
            holder.time.setText(result_temp);
        } catch (Exception e) {
            e.printStackTrace();
        }


        try {

            //카드 이미지
            Glide.with(holder.itemView.getContext())
                    .load(R.drawable.onlylogo)
                    .apply(new RequestOptions()
                            .placeholder(R.drawable.placeholder)
                            .error(R.drawable.placeholder)
                            .dontTransform()
                            .centerCrop()
                            .circleCrop()
                            .override(64, 64)
                    )
                    .into(holder.profile);
            holder.submit.setVisibility(View.VISIBLE);
            btn(holder, item);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    //추천 및 해당 이미지 클릭 시 intent act 옮기는 처리 메소드.
    private void btn(final ViewHolder holder2, final CareItem item2) {

        holder2.submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(holder2.itemView.getContext(), Calendar_Diary_Today_Info_Act.class);
                intent.putExtra("from", "calendarFrag");
                intent.putExtra("nickname", Main_act._Main_act.nickname);
                intent.putExtra("time", item2.getTime());
                holder2.itemView.getContext().startActivity(intent);
            }
        });


    }

    @Override
    public int getItemCount() {
        return (null != mDataList ? mDataList.size() : 0);
    }

    // view 연동.
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView message, id, time;
        ImageView profile, seen;
        RelativeLayout background;
        Button submit;

        public ViewHolder(View itemView) {
            super(itemView);
            message = itemView.findViewById(R.id.message_txt);
            id = itemView.findViewById(R.id.from);
            profile = itemView.findViewById(R.id.profile_message);
            seen = itemView.findViewById(R.id.seen_message);
            background = itemView.findViewById(R.id.message_list_item_background);
            time = itemView.findViewById(R.id.time_message);
            submit = itemView.findViewById(R.id.btn_message_list);

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

    private String time_string(String time) {
        String result = time;
        int time_temp = Integer.valueOf(result);
        if (time_temp < 10) {
            result = "0" + time;
        } else {
            result = String.valueOf(time);
        }
        return result;
    }

    private String interval(int year_target, int month_target, int date_target) throws ParseException {
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
        switch (month_target) {
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
                            if (date_long == 1) {
                                result = "어제";
                            } else {
                                result = date_long + "일 전";
                            }
                        }
                    } else {
                        String temp = String.valueOf(year_target);
                        if (year == year_target) {
                            result = time(month_target) + "월" + time(date_target) + "일";
                        } else {
                            result = temp.substring(2, 4) + "년" + time(month_target) + "월" + time(date_target) + "일";
                        }
                    }
                } else if (date_long < 0) {
                    if (date_long > -3) {
                        switch ((int) date_long) {
                            case -1:
                                result = "1일 후";
                                break;
                            case -2:
                                result = "2일 후";
                                break;
                            case -3:
                                result = "3일 후";
                                break;
                        }
                    } else {
                        String temp = String.valueOf(year_target);
                        if (year == year_target) {
                            result = time(month_target) + "월" + time(date_target) + "일";
                        } else {
                            result = temp.substring(2, 4) + "년" + time(month_target) + "월" + time(date_target) + "일";
                        }
                    }
                } else {
                    result = "오늘";
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
