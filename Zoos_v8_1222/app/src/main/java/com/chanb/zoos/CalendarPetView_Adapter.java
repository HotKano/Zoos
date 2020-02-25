package com.chanb.zoos;


import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;
import java.util.Objects;

public class CalendarPetView_Adapter extends RecyclerView.Adapter<CalendarPetView_Adapter.ViewHolder> {

    private final List<CalendarPetItem> mDataList;
    boolean test[];


    public CalendarPetView_Adapter(List<CalendarPetItem> dataList) {
        mDataList = dataList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.calendar_pet_view_item, parent, false);


        return new ViewHolder(view);
    }

    //item View 레이아웃 연결.
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final CalendarPetItem item = mDataList.get(position);

        Log.d("mDataListSize", position + "/" + mDataList.size() + "");

        try {

            holder.petName.setText(item.getPetName());

            String url = "http://133.186.135.41/zozoPetProfile/" + item.getPetProfile();
            Glide.with(holder.itemView.getContext())
                    .load(url)
                    .apply(new RequestOptions()
                            .placeholder(R.drawable.placeholder)
                            .error(R.drawable.placeholder)
                            .dontTransform()
                            .centerCrop()
                            .circleCrop()
                    ).into(holder.petProfile);

            if (position == 0) {
                Calendar_input_act c = Calendar_input_act.c;
                c.petNo = item.getPetNo();
                c.petName = item.getPetName();
                c.petProfile = item.getPetProfile();
                c.petRace = item.getPetRace();
                holder.petProfile.setAlpha(1.0f);
                holder.petProfile.setBackgroundResource(R.drawable.select_red_background);
            }


            holder.petProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Calendar_input_act c = Calendar_input_act.c;
                    c.petNo = item.getPetNo();
                    c.petName = item.getPetName();
                    c.petProfile = item.getPetProfile();
                    c.petRace = item.getPetRace();

                    for (int i = 0; i < mDataList.size(); i++) {
                        int width = Objects.requireNonNull(c.recyclerView.findViewHolderForLayoutPosition(i)).itemView.findViewById(R.id.calendar_pet_view_img).getWidth();
                        int height = Objects.requireNonNull(c.recyclerView.findViewHolderForLayoutPosition(i)).itemView.findViewById(R.id.calendar_pet_view_img).getHeight();
                        LinearLayout.LayoutParams linearLayoutParams = new LinearLayout.LayoutParams(width, height);
                        linearLayoutParams.setMargins(0, 0, 0, sizeData(holder, 12));

                        int temp = sizeData(holder, 5);

                        if (i == position) {
                            Objects.requireNonNull(c.recyclerView.findViewHolderForLayoutPosition(i)).itemView.findViewById(R.id.calendar_pet_view_img).setLayoutParams(linearLayoutParams);
                            Objects.requireNonNull(c.recyclerView.findViewHolderForLayoutPosition(i)).itemView.findViewById(R.id.calendar_pet_view_img).setBackgroundResource(R.drawable.select_red_background);
                            Objects.requireNonNull(c.recyclerView.findViewHolderForLayoutPosition(i)).itemView.findViewById(R.id.calendar_pet_view_img).setAlpha(1.0f);
                        } else {
                            Objects.requireNonNull(c.recyclerView.findViewHolderForLayoutPosition(i)).itemView.findViewById(R.id.calendar_pet_view_img).setLayoutParams(linearLayoutParams);
                            Objects.requireNonNull(c.recyclerView.findViewHolderForLayoutPosition(i)).itemView.findViewById(R.id.calendar_pet_view_img).setBackgroundResource(R.color.start);
                            Objects.requireNonNull(c.recyclerView.findViewHolderForLayoutPosition(i)).itemView.findViewById(R.id.calendar_pet_view_img).setAlpha(1.0f);
                        }

                    }
                }


            });

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }


    // view 연동.
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView petName;
        ImageButton petProfile;

        public ViewHolder(View itemView) {
            super(itemView);
            petName = itemView.findViewById(R.id.calendar_pet_view_name);
            petProfile = itemView.findViewById(R.id.calendar_pet_view_img);
        }
    }

    public int sizeData(ViewHolder holder, float dip) {
        Resources r = holder.itemView.getResources();
        float px = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dip,
                r.getDisplayMetrics()
        );

        return Math.round(px);
    }

}
