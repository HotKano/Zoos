package com.chanb.zoos;


import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;


import java.util.List;

public class CardView_Adapter extends RecyclerView.Adapter<CardView_Adapter.ViewHolder> {

    private final List<CardItem> mDataList;
    SNS_frag sns_frag;


    public CardView_Adapter(List<CardItem> dataList) {
        mDataList = dataList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_main_item, parent, false);
        return new ViewHolder(view);
    }

    //item view에 레이아웃 연결.
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        CardItem item = mDataList.get(position);
        holder.timer.setText(item.getTimer());
        holder.like.setText(item.getLike());
        String check = item.getLike_check();
        if (!check.equals("a")) {
            holder.like_image.setImageResource(R.drawable.icon_like_after);
        } else {
            holder.like_image.setImageResource(R.drawable.icon_like_before);
        }
        holder.watch.setText(item.getWatch());

        String title = item.getTitle();
        if (title.length() > 20) {
            String title_cut = title.substring(0, 20);

        } else {

        }

        String keyWord = "nickname";
        int temp;
        temp = title.indexOf(keyWord);
        if (temp == -1) {
            holder.title.setText(item.getTitle());
        } else {
            title = title.substring(temp + keyWord.length() + 1);
            holder.title.setText(title);
        }

        String[] img_split = item.getImg().split(":");
        String url = "http://133.186.135.41" + img_split[0];
        //Log.d("INTERNET", url);
        try {
            //  카드 이미지
            Glide.with(holder.itemView.getContext())
                    .asBitmap()
                    .load(url)
                    .apply(new RequestOptions()
                            .placeholder(R.drawable.placeholder)
                            .override(256, 256)
                            .error(R.drawable.placeholder)
                            .transform(new RoundedCorners(20))
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                    )
                    .into(holder.img);

            btn(holder, item);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //추천 및 해당 이미지 클릭 시 intent act 옮기는 처리 메소드.
    private void btn(final ViewHolder holder2, final CardItem item2) {

        sns_frag = SNS_frag._SNS_frag;

        holder2.img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(holder2.itemView.getContext(), Care_Content_Act.class);
                intent.putExtra("no", item2.getNo());
                intent.putExtra("id", sns_frag.id);
                intent.putExtra("type", "Card");
                intent.putExtra("kind", "care");
                holder2.itemView.getContext().startActivity(intent);

            }
        });

        holder2.like_image.setOnClickListener(new View.OnClickListener() {

            String check = item2.getLike_check();
            int test_number = Integer.parseInt(item2.getLike());

            @Override
            public void onClick(View v) {
                int test = Integer.parseInt(item2.getNo());
                View view = holder2.itemView;
                //sns_frag.likeConnect(test, view, holder2);

                if (check.equals("a")) {
                    holder2.like.setText((test_number + 1) + "");
                    test_number = test_number + 1;
                    holder2.like_image.setImageResource(R.drawable.icon_like_after);
                    check = String.valueOf(test);
                } else {
                    holder2.like.setText((test_number - 1) + "");
                    test_number = test_number - 1;
                    holder2.like_image.setImageResource(R.drawable.icon_like_before);
                    check = String.valueOf("a");
                }

            }
        });
    }


    @Override
    public int getItemCount() {
        return mDataList.size();
    }


    // view 연동.
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, timer, like, watch;
        ImageView img, like_image;

        public ViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title_main_cardview);
            img = itemView.findViewById(R.id.img_main_cardview);
            like_image = itemView.findViewById(R.id.like_img_main_cradview);
            timer = itemView.findViewById(R.id.time_main_cardview);
            like = itemView.findViewById(R.id.like_txt_main_cradview);
            watch = itemView.findViewById(R.id.view_main_cardview);
        }
    }

}
