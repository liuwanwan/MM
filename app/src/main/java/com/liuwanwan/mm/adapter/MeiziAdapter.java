package com.liuwanwan.mm.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.liuwanwan.mm.R;
import com.liuwanwan.mm.model.Meizi;

import java.util.List;

public class MeiziAdapter extends RecyclerView.Adapter<MeiziAdapter.ViewHolder> {
    private Context mContext = null;
    private List<Meizi> mMeiziList = null;
    private int parentContext = 0;

    static class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView meiziImage = null;
        private TextView meiziTitle = null;

        public ViewHolder(View view) {
            super(view);
            meiziImage = (ImageView) view.findViewById(R.id.image_meizi);
            meiziTitle = (TextView) view.findViewById(R.id.title_meizi);
        }
    }

    public MeiziAdapter(List<Meizi> meiziList, int parentFlag) {
        mMeiziList = meiziList;
        parentContext = parentFlag;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mContext == null) {
            mContext = parent.getContext();
        }
        final View view = LayoutInflater.from(mContext).inflate(R.layout.item_card_meizi, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Meizi meizi = mMeiziList.get(position);
        holder.meiziTitle.setText(meizi.getTitleOfMeizi());
        ViewGroup.LayoutParams params = holder.meiziImage.getLayoutParams();
        if (parentContext == 0) {
            params.height = 600;
            holder.meiziImage.setLayoutParams(params);
        }
        if (meizi.getDetailUrlOfMeizi().equals("collection")) {//来自下载的图片
            Glide.with(mContext).load(meizi.getUrlOfMeizi()).into(holder.meiziImage);
        } else {
            GlideUrl glideUrl = new GlideUrl(meizi.getUrlOfMeizi(), new LazyHeaders.Builder().addHeader("Referer", "http://www.mzitu.com/").build());
            Glide.with(mContext).load(glideUrl).into(holder.meiziImage);
        }
    }

    @Override
    public int getItemCount() {
        return mMeiziList.size();
    }
}
