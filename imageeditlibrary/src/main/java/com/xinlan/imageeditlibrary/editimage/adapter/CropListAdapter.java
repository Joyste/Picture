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
import com.xinlan.imageeditlibrary.editimage.model.RatioItem;

import java.util.List;

/**
 * 调整裁剪的适配器
 */
public class CropListAdapter extends RecyclerView.Adapter<CropListAdapter.ViewHolder> {

    private List<RatioItem> list;
    private Context context;
    private OnItemClickListener listener;
    private int icoSelected[] = {R.drawable.ico_custom_selected,R.drawable.ico_original_selected,R.drawable.ico_1_1_selected,R.drawable.ico_9_16_selected,R.drawable.ico_16_9_selected,R.drawable.ico_2_3_selected,R.drawable.ico_3_2_selected,R.drawable.ico_4_3_selected,R.drawable.ico_3_4_selected};
    private int icoNormal[] = {R.drawable.ico_custom_normal,R.drawable.ico_original_normal,R.drawable.ico_1_1_normal,R.drawable.ico_9_16_normal,R.drawable.ico_16_9_normal,R.drawable.ico_2_3_normal,R.drawable.ico_3_2_normal,R.drawable.ico_4_3_normal,R.drawable.ico_3_4_normal};
    private int curPosition = 0;
    private View curView;
    private boolean isSelected = true;

    /**
     * @param context
     * @param list
     */
    public CropListAdapter(Context context, List<RatioItem> list) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_crop_item, parent, false);
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
                    if (position!=0){
                        isSelected = false;
                    }else {
                        isSelected = true;
                    }
                    if (curView!=null){
                        ImageView imageView = curView.findViewById(R.id.iv_crop_icon);
                        imageView.setImageResource(icoNormal[curPosition]);
                        TextView textView = curView.findViewById(R.id.tv_crop_name);
                        textView.setTextColor(context.getResources().getColor(R.color.black));
                    }
                    holder.ivCropIcon.setImageResource(icoSelected[position]);
                    holder.tvCropName.setTextColor(context.getResources().getColor(R.color.theme_color));
                    curView = holder.itemView;
                    curPosition = position;
                    listener.onItemClick(holder.itemView, holder.getAdapterPosition());
                }
            }
        });
        holder.itemView.setTag(list.get(position));
        if(position == 0&& isSelected){
            holder.tvCropName.setTextColor(context.getResources().getColor(R.color.theme_color));
            curView = holder.itemView;
        }
        holder.ivCropIcon.setImageResource(list.get(position).getIcon());
        holder.tvCropName.setText(list.get(position).getText());

    }


    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    //进行ui绑定
    static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView ivCropIcon;
        TextView tvCropName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivCropIcon = itemView.findViewById(R.id.iv_crop_icon);
            tvCropName = itemView.findViewById(R.id.tv_crop_name);
        }
    }

    public List<RatioItem> getList() {
        return list;
    }

    public void setList(List<RatioItem> list) {
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
