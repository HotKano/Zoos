package com.chanb.zoos;

import android.app.Notification;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.Calendar;
import java.util.List;

public class Message_Adapter extends RecyclerView.Adapter<Message_Adapter.ViewHolder> {

    private final List<Message_Item> mDataList;
    Message_Item item;
    private FirebaseAuth mAuth;

    public Message_Adapter(List<Message_Item> dataList) {
        mDataList = dataList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_item, parent, false);
        mAuth = FirebaseAuth.getInstance();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        item = mDataList.get(position);
        String id_mine = mAuth.getCurrentUser().getUid();
        String id_from = item.getFrom();
        String time = item.getTime();

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(Long.parseLong(time));
        int mTime = calendar.get(Calendar.HOUR_OF_DAY);
        int mMin = calendar.get(Calendar.MINUTE);
        int mYear = calendar.get(Calendar.YEAR);
        int mMonth = calendar.get(Calendar.MONTH);
        int mDay = calendar.get(Calendar.DAY_OF_MONTH);

        Log.d("TIMEDATA", mYear + "년 " + mMonth + " 월 " + mDay + " 일 ");


        //메시지를 받은 것이 됨.
        if (!id_from.equals(id_mine)) {
            holder.message.setVisibility(View.GONE);
            holder.time.setVisibility(View.GONE);
            holder.receiverMessage.setVisibility(View.VISIBLE);
            holder.receiverTime.setVisibility(View.VISIBLE);
            holder.receiverMessage.setText(item.getText());
            holder.receiverTime.setText(time(mTime) + ":" + time(mMin));
            holder.receiverMessage.setBackgroundResource(R.drawable.message_receiver);
            holder.receiverMessage.setTextColor(holder.itemView.getResources().getColor(R.color.message_reciever));
            holder.receiverMessage.setPadding(16, 16, 16, 16);
        } else {
            //메시지를 보낸 것이 됨.
            holder.message.setVisibility(View.VISIBLE);
            holder.time.setVisibility(View.VISIBLE);
            holder.message.setText(item.getText());
            holder.time.setText(time(mTime) + ":" + time(mMin));
            holder.receiverMessage.setVisibility(View.GONE);
            holder.receiverTime.setVisibility(View.GONE);
            holder.message.setBackgroundResource(R.drawable.message_send);
            holder.message.setTextColor(holder.itemView.getResources().getColor(R.color.message_send));
            holder.message.setPadding(16, 16, 16, 16);
        }


        try {
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return (null != mDataList ? mDataList.size() : 0);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView message, time, receiverMessage, receiverTime;

        public ViewHolder(View itemView) {
            super(itemView);
            message = itemView.findViewById(R.id.message);
            time = itemView.findViewById(R.id.time);
            receiverMessage = itemView.findViewById(R.id.receiverMessage);
            receiverTime = itemView.findViewById(R.id.time_receiver);


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


}
