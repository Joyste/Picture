package com.test.picture.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.bumptech.glide.Glide;
import com.test.picture.R;
import com.test.picture.model.AppInfo;
import com.test.picture.tool.GlideEngine;

import java.util.List;

/**
 * app菜单的适配器
 */
public class AppListAdapter extends RecyclerView.Adapter<AppListAdapter.ViewHolder> {

    private List<AppInfo> list;
    private Context context;
    private OnItemClickListener listener;

    /**
     * @param context
     * @param list
     */
    public AppListAdapter(Context context, List<AppInfo> list) {
        this.list = list;
        this.context = context;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_app_item, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    /**
     * @param holder
     * @param position
     */
    @SuppressLint("NewApi")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null) {
                    listener.onItemClick(holder.itemView, holder.getAdapterPosition());
                }
            }
        });

        Uri uri = Uri.parse(list.get(position).getApp_img());
        GlideEngine.getInstance().loadPhoto(context,uri,holder.ivAppIcon);
//        Glide.with(context).load(list.get(position).getApp_img()).transition().into(holder.ivAppIcon);
        holder.appTitle.setText(list.get(position).getApp_title());

        if (list.get(position).getApp_isNew() == 1){
            holder.tvIsNew.setVisibility(View.VISIBLE);
        }else {
            holder.tvIsNew.setVisibility(View.INVISIBLE);
        }
    }


    @Override
    public int getItemCount() {
        return list.size();
    }


    //进行ui绑定
    static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView ivAppIcon;
        TextView appTitle;
        TextView tvIsNew;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivAppIcon = itemView.findViewById(R.id.app_icon);
            appTitle = itemView.findViewById(R.id.app_title);
            tvIsNew = itemView.findViewById(R.id.tv_is_new);
        }
    }

    public List<AppInfo> getList() {
        return list;
    }

    public void setList(List<AppInfo> list) {
        this.list = list;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(View itemView, int position);
    }


}
