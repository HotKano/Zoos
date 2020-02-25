package com.chanb.zoos;


import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

public class SaveView_Adapter extends RecyclerView.Adapter<SaveView_Adapter.ViewHolder> {

    private final List<SaveItem> mDataList;
    public Save_frag _Save_frag;

    public SaveView_Adapter(List<SaveItem> dataList) {
        mDataList = dataList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.save_view_item, parent, false);

        return new ViewHolder(view);
    }

    //item View 레이아웃 연결.
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        SaveItem item = mDataList.get(position);
        String title = item.getTitle();
        String keyWord = "nickname";
        int temp;
        temp = title.indexOf(keyWord);
        if (temp == -1) {
            holder.title.setText(item.getTitle());
        } else {
            title = title.substring(temp + keyWord.length() + 1);
            holder.title.setText(title);
        }

        String[] img_split = item.getImage().split(":");
        String type = item.getType();
        String url;
        if (type.equals("story"))
            url = "http://133.186.135.41/zozoStory/" + img_split[0];
        else
            url = "http://133.186.135.41" + img_split[0];

        try {
            //  카드 이미지
            Glide.with(holder.itemView.getContext())
                    .asBitmap()
                    .load(url)
                    .apply(new RequestOptions()
                            .placeholder(R.drawable.placeholder)
                            .error(R.drawable.placeholder)
                            .override(200, 200)
                            .dontTransform()
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                    )
                    .into(holder.img);

            btn(holder, item);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    //저장한 글 또는 스토리로 이동하는 부분. 나중에 카테고리에 따라서 구분을 지을 필요가 있음.
    private void btn(final ViewHolder holder, final SaveItem item) {

        _Save_frag = Save_frag._Save_frag;

        holder.line_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String type = item.getType();
                Intent intent;
                if (type.equals("story")) {
                    intent = new Intent(holder.itemView.getContext(), Content_act.class);
                } else {
                    intent = new Intent(holder.itemView.getContext(), Care_Content_Act.class);
                }
                intent.putExtra("no", item.getNo());
                intent.putExtra("id", _Save_frag.id);
                intent.putExtra("type", "SaveView");
                holder.itemView.getContext().startActivity(intent);

            }
        });

    }


    @Override
    public int getItemCount() {
        return mDataList.size();
    }


    // view 연동.
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        ImageView img;
        LinearLayout line_save;

        public ViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title_save);
            img = itemView.findViewById(R.id.image_save);
            line_save = itemView.findViewById(R.id.linear_save);

        }
    }

}
