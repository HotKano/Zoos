package com.chanb.zoos;


import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.chanb.zoos.utils.Database;

import java.util.ArrayList;
import java.util.List;

public class StoryView_Adapter_Img extends RecyclerView.Adapter<StoryView_Adapter_Img.ItemRowHolder> {

    private final List<StoryItem_Img> mDataList;
    private Context mContext;


    // 참고의 Section.
    public StoryView_Adapter_Img(Context context, List<StoryItem_Img> dataList) {
        this.mDataList = dataList;
        this.mContext = context;
    }

    @Override
    public ItemRowHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.story_item_img, null);
        ItemRowHolder mh = new ItemRowHolder(view);
        return mh;
    }

    //item View 레이아웃 연결. 날짜 표시하는 부분.
    @Override
    public void onBindViewHolder(ItemRowHolder holder, int position) {
        StoryItem_Img item = mDataList.get(position);

        String[] img_split = item.getImg().split(":");
        String url = "http://133.186.135.41/zozoStory/" + img_split[0];
        try {
            //메인 이미지
            Glide.with(holder.itemView.getContext())
                    .asBitmap()
                    .load(url)
                    .apply(new RequestOptions()
                            .placeholder(R.drawable.placeholder)
                            .override(200, 200)
                            .error(R.drawable.placeholder)
                            .dontTransform()
                    ).into(holder.img);
            btn(holder, item);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return (null != mDataList ? mDataList.size() : 0);
    }

    //저장한 글 또는 스토리로 이동하는 부분. 나중에 카테고리에 따라서 구분을 지을 필요가 있음.
    private void btn(final ItemRowHolder holder, final StoryItem_Img item) {

        String[] temp = new String[2];
        String dbName = "UserDatabase_Zoos";
        int dataBaseVersion = 1;
        Database database = new Database(holder.itemView.getContext(), dbName, null, dataBaseVersion);
        database.init();
        temp = database.select("MEMBERINFO");

        final String[] finalTemp = temp;
        holder.img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(holder.itemView.getContext(), Content_act.class);
                intent.putExtra("no", item.getNo());
                intent.putExtra("id", finalTemp[0]);
                intent.putExtra("type", "StoryView");
                intent.putExtra("kind", "story");
                holder.itemView.getContext().startActivity(intent);

            }
        });

    }

    // view 연동.
    public static class ItemRowHolder extends RecyclerView.ViewHolder {
        ImageView img;


        public ItemRowHolder(View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.img_story_grid);


        }
    }

}
