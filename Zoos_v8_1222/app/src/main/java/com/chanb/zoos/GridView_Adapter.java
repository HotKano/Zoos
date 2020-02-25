package com.chanb.zoos;

import android.content.Intent;
import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GridView_Adapter extends RecyclerView.Adapter<GridView_Adapter.ViewHolder> {

    private final List<GridItem> mDataList;
    SNS_frag sns_frag;
    RequestQueue requestQueue;

    public GridView_Adapter(List<GridItem> dataList) {
        mDataList = dataList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_view_main_item, parent, false);

        return new ViewHolder(view);
    }

    //item view에 레이아웃 연결.
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        requestQueue = Volley.newRequestQueue(holder.itemView.getContext());
        GridItem item = mDataList.get(position);
        String title = item.getTitle();
        String keyWord = "nickname";
        int temp;
        temp = title.indexOf(keyWord);
        if (temp == -1) {
            holder.title_topCare.setText(item.getTitle());
            holder.nickname_topCare.setVisibility(View.GONE);
        } else {
            title = title.substring(temp + keyWord.length() + 1);
            holder.title_topCare.setText(title);
            holder.nickname_topCare.setText(item.getNickname() + "님의 ");
        }

        holder.tag_topCare.setText(item.getTag());

        String check = item.getLike_check();
        if (!check.equals("a")) {
            holder.like_topCare.setImageResource(R.drawable.icon_like_after);
        } else {
            holder.like_topCare.setImageResource(R.drawable.icon_like_before);
        }

        String[] img_split = item.getURL().split(":");
        String url = "http://133.186.135.41" + img_split[0];
        //Log.d("INTERNET", url);
        try {
            //메인 이미지
            Glide.with(holder.itemView.getContext())
                    .asBitmap()
                    .load(url)
                    .apply(new RequestOptions()
                            .placeholder(R.drawable.placeholder)
                            .override(300, 150)
                            .error(R.drawable.placeholder)
                            .transform(new RoundedCorners(10))
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                    ).into(holder.url);

            btn(holder, item);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void btn(final GridView_Adapter.ViewHolder holder2, final GridItem item2) {
        sns_frag = SNS_frag._SNS_frag;

        holder2.url.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(holder2.itemView.getContext(), Care_Content_Act.class);
                String test = String.valueOf(item2.getNo());
                intent.putExtra("no", test);
                intent.putExtra("id", sns_frag.id);
                intent.putExtra("type", "Grid");
                intent.putExtra("kind", "care");
                holder2.itemView.getContext().startActivity(intent);

            }
        });

        holder2.like_topCare.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {
                int test = Integer.parseInt(item2.getNo());
                View view = holder2.itemView;
                String check = item2.getLike_check();
                likeConnect(test, view, holder2, item2);

                Log.d("data_test", check);

                if (check.equals("a")) {
                    holder2.like_topCare.setImageResource(R.drawable.icon_like_after);
                    item2.setLike_check(item2.getNo());
                } else {
                    holder2.like_topCare.setImageResource(R.drawable.icon_like_before);
                    item2.setLike_check("a");
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView url, like_topCare;
        TextView title_topCare, tag_topCare, nickname_topCare;
        String check;

        public ViewHolder(View itemView) {
            super(itemView);
            url = itemView.findViewById(R.id.image_topCare);
            like_topCare = itemView.findViewById(R.id.like_img_topCare);
            title_topCare = itemView.findViewById(R.id.title_topCare);
            tag_topCare = itemView.findViewById(R.id.tag_topCare);
            nickname_topCare = itemView.findViewById(R.id.nickname_topCare);
        }
    }

    //추천 관련 서버 접속
    public void likeConnect(final int number, final View view, final GridView_Adapter.ViewHolder holder2, final GridItem item2) {
        String url = "http://133.186.135.41/zozo_sns_like.php";

        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jsonObject = JsonUtil.getJSONObjectFrom(response);
                    String state = JsonUtil.getStringFrom(jsonObject, "state");
                    if (state.equals("sns_double_time")) {
                        Snackbar snackbar = Snackbar
                                .make(view, "저장소에 저장 취소!", Snackbar.LENGTH_SHORT)
                                .setActionTextColor(Color.WHITE)
                                .setAction("실행 취소", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        likeConnect(number, view, holder2, item2);
                                        holder2.like_topCare.setImageResource(R.drawable.icon_like_after);
                                        item2.setLike_check(item2.getNo());
                                    }
                                });
                        View sbView = snackbar.getView();
                        sbView.setBackgroundColor(ContextCompat.getColor(holder2.itemView.getContext(), R.color.start_with_phone_color));
                        snackbar.show();

                    } else if (state.equals("sns_like_update")) {
                        Snackbar snackbar = Snackbar
                                .make(view, "저장소에 저장 완료!", Snackbar.LENGTH_SHORT)
                                .setActionTextColor(Color.WHITE)
                                .setAction("실행 취소", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        likeConnect(number, view, holder2, item2);
                                        holder2.like_topCare.setImageResource(R.drawable.icon_like_before);
                                        item2.setLike_check("a");
                                    }
                                });
                        View sbView = snackbar.getView();
                        sbView.setBackgroundColor(ContextCompat.getColor(holder2.itemView.getContext().getApplicationContext(), R.color.start_with_phone_color));
                        snackbar.show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }


        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(holder2.itemView.getContext().getApplicationContext(), "인터넷 접속 상태를 확인해주세요.", Toast.LENGTH_SHORT).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parameters = new HashMap<String, String>();
                parameters.put("id", sns_frag.id);
                parameters.put("no", number + "");
                parameters.put("kind", "notice");
                //Log.d("like", id + number + "");

                return parameters;
            }
        };
        requestQueue.add(request);
    }


}
