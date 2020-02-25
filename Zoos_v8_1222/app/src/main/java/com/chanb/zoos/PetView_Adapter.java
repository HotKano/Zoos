package com.chanb.zoos;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
import com.chanb.zoos.utils.ItemMoveCallback;
import com.chanb.zoos.utils.StartDragListener;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PetView_Adapter extends RecyclerView.Adapter<PetView_Adapter.ViewHolder> implements ItemMoveCallback.ItemTouchHelperContract {

    private final List<PetItem> mDataList;
    private final StartDragListener mStartDragListener;
    //과거 번호 추출
    private String no_before;

    // view 연동.
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView petName, petInfo;
        ImageView petProfile, petGender, petMenu;
        RelativeLayout rl;

        public ViewHolder(View itemView) {
            super(itemView);
            petName = itemView.findViewById(R.id.pet_edit_petName);
            petProfile = itemView.findViewById(R.id.pet_profile_edit);
            petGender = itemView.findViewById(R.id.pet_edit_petGender);
            petMenu = itemView.findViewById(R.id.pet_edit_petMenu);
            rl = itemView.findViewById(R.id.profile_pet_zone);

            //petKind, petWeight, petAge가 여기로 set 된다.
            //form : petKind + " / " + petWeight + " / " + petAge
            petInfo = itemView.findViewById(R.id.pet_edit_petInfo);
        }
    }

    public PetView_Adapter(List<PetItem> dataList, StartDragListener startDragListener) {
        mStartDragListener = startDragListener;
        this.mDataList = dataList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.profile_pet_item, parent, false);
        return new ViewHolder(view);
    }

    //item View 레이아웃 연결.
    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(final PetView_Adapter.ViewHolder holder, int position) {
        PetItem item = mDataList.get(position);
        //Log.d("mDataListSize", position + "/" + mDataList.size() + "");

        try {

            if (mDataList.size() > 2) {
                //순서 변경
                holder.petMenu.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        if (event.getAction() ==
                                MotionEvent.ACTION_DOWN) {
                            mStartDragListener.requestDrag(holder);
                        }
                        return false;
                    }
                });
            } else {
                holder.petMenu.setVisibility(View.GONE);
            }

            //  카드 이미지
            if ((position + 1) != mDataList.size()) {
                holder.petName.setText(item.getPetName());
                String petInfo_value = item.getPetKind() + " / " + item.getPetAge() + "살";
                holder.petInfo.setText(petInfo_value);

                String url = "http://133.186.135.41/zozoPetProfile/" + item.getPetProfile();

                Glide.with(holder.itemView.getContext())
                        .load(url)
                        .apply(new RequestOptions()
                                .placeholder(R.drawable.placeholder)
                                .error(R.drawable.placeholder)
                                .skipMemoryCache(false)
                                .dontTransform()
                                .centerCrop()
                                .circleCrop()
                        ).into(holder.petProfile);

                switch (item.getPetGender()) { //0:암컷 1:수컷.
                    case "0":
                        Glide.with(holder.itemView.getContext())
                                .load(R.drawable.women)
                                .apply(new RequestOptions()
                                        .placeholder(R.drawable.placeholder)
                                        .error(R.drawable.placeholder)
                                        .skipMemoryCache(false)
                                        .dontTransform()
                                ).into(holder.petGender);
                        break;
                    case "1":
                        Glide.with(holder.itemView.getContext())
                                .load(R.drawable.men)
                                .apply(new RequestOptions()
                                        .placeholder(R.drawable.placeholder)
                                        .error(R.drawable.placeholder)
                                        .skipMemoryCache(false)
                                        .dontTransform()
                                ).into(holder.petGender);
                        break;
                }
                //pet data update mode kind 0
                btn(holder, item, "1");
            } else {

                holder.petMenu.setVisibility(View.GONE);

                Glide.with(holder.itemView.getContext())
                        .load(R.drawable.round_pet_insert)
                        .apply(new RequestOptions()
                                .placeholder(R.drawable.placeholder)
                                .error(R.drawable.placeholder)
                        ).into(holder.petProfile);

                holder.petName.setText("가족 추가");
                holder.rl.setBackgroundResource(R.drawable.none);
                holder.petGender.setVisibility(View.GONE);
                holder.petInfo.setVisibility(View.GONE);
                //pet data insert mode kind 0
                btn(holder, item, "0");
            }


        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    //펫 정보 편집하는 act로 이동하는 버튼.
    private void btn(final ViewHolder holder, final PetItem item, final String kind) {

        if (kind.equals("0")) {
            holder.rl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(holder.itemView.getContext(), Pet_edit_act.class);
                    intent.putExtra("petNo", item.getPetNo());
                    intent.putExtra("kind", kind);
                    intent.putExtra("id", SNS_frag._SNS_frag.id);
                    holder.itemView.getContext().startActivity(intent);

                }
            });
        } else {


            holder.petProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(holder.itemView.getContext(), Story_act.class);
                    intent.putExtra("petNo", item.getPetNo());
                    intent.putExtra("kind", kind);
                    intent.putExtra("target_id", SNS_frag._SNS_frag.id);
                    intent.putExtra("id", SNS_frag._SNS_frag.id);
                    holder.itemView.getContext().startActivity(intent);

                }
            });
        }


    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    @Override
    public void onRowMoved(int fromPosition, int toPosition) {
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(mDataList, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(mDataList, i, i - 1);
            }
        }
        notifyItemMoved(fromPosition, toPosition);
    }

    @Override
    public void onRowSelected(ViewHolder myViewHolder) {
        no_before = String.valueOf(myViewHolder.getAdapterPosition() + 1);
    }

    @Override
    public void onRowClear(ViewHolder myViewHolder) {
        Log.d("movementClear", myViewHolder.petName.getText().toString());
        Log.d("movementClear", "before : " + no_before + " change : " + (myViewHolder.getAdapterPosition() + 1) + "");
        String no_new = String.valueOf(myViewHolder.getAdapterPosition() + 1);

        String dbName = "UserDatabase_Zoos";
        int dataBaseVersion = 1;
        Database database = new Database(myViewHolder.itemView.getContext(), dbName, null, dataBaseVersion);
        database.init();
        String[] temp = database.select("MEMBERINFO");
        String id = temp[0];
        petConnect(id, no_before, no_new, myViewHolder);

    }

    // 번호 업데이트
    public void petConnect(final String id, final String no, final String no_new, ViewHolder holder) {
        String SNSUrl = "http://133.186.135.41/zozo_edit_pet_number.php";
        RequestQueue requestQueue = Volley.newRequestQueue(holder.itemView.getContext());
        StringRequest request = new StringRequest(Request.Method.POST, SNSUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.d("petViewEdit", response);
                try {

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
                parameters.put("id", id);
                parameters.put("no", no);
                parameters.put("new_no", no_new);
                //Log.d("petViewEdit", id + no + no_new);
                return parameters;
            }
        };
        requestQueue.add(request);
    }

}
