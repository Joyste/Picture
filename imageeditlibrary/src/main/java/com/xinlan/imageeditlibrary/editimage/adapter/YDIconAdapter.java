//package com.xinlan.imageeditlibrary.editimage.adapter;
//
//import android.annotation.SuppressLint;
//import android.content.Context;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.TextView;
//
//
//import androidx.annotation.NonNull;
//import androidx.recyclerview.widget.RecyclerView;
//
///**
// * 小圆点 滚动的适配器
// */
//public class YDIconAdapter extends RecyclerView.Adapter<YDIconAdapter.ViewHolder>
//{
//
////    private List<WeatherBaseEntity> list;
//    private Context mContext;
//    private int getIndex;
//    String getCity;
//
//    @NonNull
//    @Override
//    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
//    {
//        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.yuan_dian_item, parent, false);
//        ViewHolder holder = new ViewHolder(view);
//
//        return holder;
//    }
//
//    /**
//     * @param holder
//     * @param position
//     */
//    @SuppressLint("NewApi") @Override public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position)
//    {
//        WeatherBaseEntity entity = null;
//        if (entity == null) {
//            entity = list.get(position);
//        }
//
//        //先设置位置头部项 为位置天气项
//        if (sharedCity.equals(entity.city_name)) {
//            holder.yd_icon_tv.setBackgroundResource(R.drawable.nav_outline_icon);
//        }
//
//        //进行判断是否为位置天气
//        if (position == getIndex) {
//            if (sharedCity.equals(entity.city_name)) {
//                holder.yd_icon_tv.setBackgroundResource(R.drawable.nav_outline_icon);
//                holder.yd_icon_tv.setAlpha(1);
//            } else {
//                holder.yd_icon_tv.setBackgroundResource(R.drawable.nav_yd_icon);
//                holder.yd_icon_tv.setAlpha(1);
//            }
//        }else {
//
//        }
//    }
//
//    /**
//     * 设置头部小圆点 根据滑动的变换位置
//     *
//     * @param mContext
//     * @param list
//     * @param Index
//     */
//    public YDIconAdapter(Context mContext, List<WeatherBaseEntity> list, int Index, String shareCity)
//    {
//        this.list = list;
//        this.mContext = mContext;
//        this.getIndex = Index;
//        this.sharedCity = shareCity;
//    }
//
//    private String sharedCity = "";
//
//    @Override
//    public int getItemCount()
//    {
//        return list.size();
//    }
//
//    //进行ui绑定
//    static class ViewHolder extends RecyclerView.ViewHolder
//    {
//
//        TextView yd_icon_tv;
//
//        public ViewHolder(@NonNull @NotNull View itemView)
//        {
//            super(itemView);
//            yd_icon_tv = itemView.findViewById(R.id.yd_icon_tv);
//        }
//    }
//}
