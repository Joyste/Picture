package com.xinlan.imageeditlibrary.editimage.adapter;

import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.xinlan.imageeditlibrary.R;
import com.xinlan.imageeditlibrary.editimage.fragment.PaintFragment;


/**
 * 颜色列表Adapter
 *
 * @author panyi
 */
public class ColorListAdapter extends RecyclerView.Adapter<ViewHolder> {
    public static final int TYPE_COLOR = 1;
    public static final int TYPE_MORE = 2;
    private int curPosition = -1;
    private View curView;

    public interface IColorListAction{
        void onColorSelected(final int position,final int color);
        void onMoreSelected(final int position);
    }

    private Context mContext;
    private int[] colorsData;

    private IColorListAction mCallback;
    private boolean isWhite = false;

    public boolean isWhite() {
        return isWhite;
    }

    public void setWhite(boolean white) {
        isWhite = white;
    }

    public ColorListAdapter(Context frg, int[] colors, IColorListAction action) {
        super();
        this.mContext = frg;
        this.colorsData = colors;
        this.mCallback = action;
    }



    public class ColorViewHolder extends ViewHolder {
        View colorPanelView;

        public ColorViewHolder(View itemView) {
            super(itemView);
            this.colorPanelView = itemView.findViewById(R.id.color_panel_view);
        }
    }// end inner class

    public class MoreViewHolder extends ViewHolder {
        TextView moreBtn;
        public MoreViewHolder(View itemView) {
            super(itemView);
            this.moreBtn = itemView.findViewById(R.id.color_panel_more);
        }

    }//end inner class


    @Override
    public int getItemCount() {
        return colorsData.length + 1;
    }

    @Override
    public int getItemViewType(int position) {
        return colorsData.length == position ? TYPE_MORE : TYPE_COLOR;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = null;
        ViewHolder viewHolder = null;
        if (viewType == TYPE_COLOR) {
            v = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.view_color_panel, parent,false);
            viewHolder = new ColorViewHolder(v);
        } else if (viewType == TYPE_MORE) {
            v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.view_color_more_panel,parent,false);
            viewHolder = new MoreViewHolder(v);
        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        int type = getItemViewType(position);
        if(type == TYPE_COLOR){
            onBindColorViewHolder((ColorViewHolder)holder,position);
        }else if(type == TYPE_MORE){
            onBindColorMoreViewHolder((MoreViewHolder)holder,position);
        }
    }

    private void onBindColorViewHolder(final ColorViewHolder holder,final int position){
        holder.colorPanelView.setBackgroundColor(mContext.getResources().getColor(colorsData[position]));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mCallback!=null){
                    if(curView!=null){
                        curView.setVisibility(View.INVISIBLE);
                    }
                    View view = holder.itemView.findViewById(R.id.ico_selected);
                    view.setVisibility(View.VISIBLE);
                    curPosition = position;
                    curView = view;
                    mCallback.onColorSelected(position,colorsData[position]);
                }
            }
        });
    }

    private void onBindColorMoreViewHolder(final MoreViewHolder holder,final int position){
        if(isWhite){
            holder.moreBtn.setTextColor(Color.WHITE);
        }
        holder.moreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mCallback!=null){
                    if(curView!=null){
                        curView.setVisibility(View.INVISIBLE);
                    }
                    mCallback.onMoreSelected(position);
                }
            }
        });
    }

}// end class
