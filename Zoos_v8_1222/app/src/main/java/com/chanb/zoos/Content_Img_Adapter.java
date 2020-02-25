package com.chanb.zoos;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.chanb.zoos.utils.Database;

import java.util.List;

public class Content_Img_Adapter extends RecyclerView.Adapter<Content_Img_Adapter.ViewHolder> {

    private final List<Content_Img_item> mDataList;
    Content_Img_item item;

    public Content_Img_Adapter(List<Content_Img_item> dataList) {
        mDataList = dataList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.content_img_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        item = mDataList.get(position);

        String url = "http://133.186.135.41/zozoStory/" + item.getUrl();
        String file_extn = item.getUrl().substring(item.getUrl().lastIndexOf(".") + 1);

        if (file_extn.equals("gif")) {
            Glide.with(holder.itemView.getContext())
                    .load(url)
                    .apply(new RequestOptions()
                            .override(360, 238)
                            .placeholder(R.drawable.placeholder)
                            .error(R.drawable.placeholder)
                            .dontTransform()
                    ).into(holder.content_Img);
            holder.gif_tag.setVisibility(View.VISIBLE);
        } else if (!file_extn.equals("gif")) {
            Glide.with(holder.itemView.getContext())
                    .load(url)
                    .apply(new RequestOptions()
                            .override(360, 238)
                            .placeholder(R.drawable.placeholder)
                            .error(R.drawable.placeholder)
                            .dontTransform()
                    ).into(holder.content_Img);
            holder.gif_tag.setVisibility(View.GONE);
        }

        try {
            btn(holder, item);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void btn(final Content_Img_Adapter.ViewHolder holder2, final Content_Img_item item2) {

    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView content_Img;
        Button gif_tag;

        public ViewHolder(View itemView) {
            super(itemView);
            content_Img = itemView.findViewById(R.id.content_img_view);
            gif_tag = itemView.findViewById(R.id.gif_tag);

        }


    }


}
