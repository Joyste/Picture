package com.xinlan.imageeditlibrary.editimage.widget;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.xinlan.imageeditlibrary.R;


public class EditStickerPopupWindow extends PopupWindow {

    private View mView; // PopupWindow 菜单布局
    private Context mContext; // 上下文参数
    private View.OnClickListener mDownListener; // 下移的点击监听器
    private View.OnClickListener mUpListener; // 上移的点击监听器

    public EditStickerPopupWindow(Context context, View.OnClickListener upListener, View.OnClickListener downListener) {
        super(context);
        this.mContext = context;
        this.mUpListener = upListener;
        this.mDownListener = downListener;
        Init();
    }

    /**
     * 设置布局以及点击事件
     */
    private void Init() {
        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mView = inflater.inflate(R.layout.head_select_pop_item, null);
        TextView btn_up = mView.findViewById(R.id.icon_btn_up);
        TextView btn_down = mView.findViewById(R.id.icon_btn_down);
        TextView btn_cancel = mView.findViewById(R.id.icon_btn_cancel);

        btn_down.setOnClickListener(mDownListener);
        btn_up.setOnClickListener(mUpListener);
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });


        // 导入布局
        this.setContentView(mView);


        // 设置动画效果
        this.setAnimationStyle(R.style.popwindow_anim_style);
        this.setWidth(WindowManager.LayoutParams.MATCH_PARENT);
        this.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        // 设置可触
        this.setFocusable(true);
        //设置背景颜色
        ColorDrawable dw = new ColorDrawable(0x00000000);
        this.setBackgroundDrawable(dw);
        // 单击弹出窗以外处 关闭弹出窗
        mView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int height = mView.findViewById(R.id.ll_pop).getTop();
                int y = (int) event.getY();
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (y < height) {
                        dismiss();
                    }
                }
                return true;
            }
        });
    }
}