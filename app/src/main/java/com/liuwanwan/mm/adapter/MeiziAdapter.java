package com.liuwanwan.mm.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
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
import com.liuwanwan.mm.ui.MeiziDetailActivity;

import java.util.List;

/**
 * Created by Administrator on 2018/3/11 0011.
 */

public class MeiziAdapter extends RecyclerView.Adapter<MeiziAdapter.ViewHolder> {
    private Context mContext = null;
    private List<Meizi> mMeiziList = null;
    private int parentContext = 0;

    static class ViewHolder extends RecyclerView.ViewHolder {
        private CardView cardView = null;
        private ImageView meiziImage = null;
        private TextView meiziTitle = null;

        public ViewHolder(View view) {
            super(view);
            cardView = (CardView) view;
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
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_card_meizi, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        if (parentContext == 0) {
            holder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = holder.getAdapterPosition();
                    Meizi meizi = mMeiziList.get(position);
                    Intent intent = new Intent(mContext, MeiziDetailActivity.class);
                    intent.putExtra(MeiziDetailActivity.MEIZI_TITLE, meizi.getTitleOfMeizi());
                    intent.putExtra(MeiziDetailActivity.MEIZI_IMAGE, meizi.getUrlOfMeizi());
                    intent.putExtra(MeiziDetailActivity.MEIZI_IMAGE_DETAIL, meizi.getDetailUrlOfMeizi());
                    mContext.startActivity(intent);
                }
            });
        }
        if (parentContext==1){
            holder.cardView.setOnLongClickListener(new View.OnLongClickListener() {//长按收藏
                @Override
                public boolean onLongClick(View view) {
                    return false;
                }
            });
        }
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
        //GlideUrl glideUrl=new GlideUrl(fruit.getUrl(),new LazyHeaders.Builder().addHeader("Referer", "http://m.mm131.com/").build());
        //GlideUrl glideUrl = new GlideUrl(meizi.getUrlOfMeizi(), new LazyHeaders.Builder().addHeader("Referer", "http://m.mzitu.com/").build());
        GlideUrl glideUrl = new GlideUrl(meizi.getUrlOfMeizi(), new LazyHeaders.Builder().addHeader("Referer", "http://www.mzitu.com/").build());
        Glide.with(mContext).load(glideUrl).into(holder.meiziImage);
    }

    @Override
    public int getItemCount() {
        return mMeiziList.size();
    }
}
