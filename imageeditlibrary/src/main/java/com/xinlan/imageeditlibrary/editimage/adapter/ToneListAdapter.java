package com.xinlan.imageeditlibrary.editimage.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.xinlan.imageeditlibrary.R;
import com.xinlan.imageeditlibrary.editimage.model.appInfo;

import java.util.List;

/**
 * 调整菜单的适配器
 */
public class ToneListAdapter extends RecyclerView.Adapter<ToneListAdapter.ViewHolder> {

    private List<appInfo> list;
    private Context context;
    private OnItemClickListener listener;

    /**
     * @param context
     * @param list
     */
    public ToneListAdapter(Context context, List<appInfo> list) {
        this.list = list;
        this.context = context;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_tone_item, parent, false);
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

        holder.ivToneIcon.setImageResource(list.get(position).getIcon());
        holder.tvToneName.setText(context.getResources().getString(list.get(position).getName()));
        holder.tvToneValue.setText(String.valueOf(list.get(position).getValue() - 128));
    }


    @Override
    public int getItemCount() {
        return list.size();
    }


    //进行ui绑定
    static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView ivToneIcon;
        TextView tvToneName;
        TextView tvToneValue;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivToneIcon = itemView.findViewById(R.id.iv_tone_icon);
            tvToneName = itemView.findViewById(R.id.tv_tone_name);
            tvToneValue = itemView.findViewById(R.id.tv_tone_value);
        }
    }

    public List<appInfo> getList() {
        return list;
    }

    public void setList(List<appInfo> list) {
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
