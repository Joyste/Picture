package com.xinlan.imageeditlibrary.editimage.ui;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.xinlan.imageeditlibrary.R;
import com.xinlan.imageeditlibrary.editimage.adapter.ColorListAdapter;
import com.xinlan.imageeditlibrary.editimage.view.TextStickerView;

import org.w3c.dom.Text;


public class AddTextPopupWindow extends PopupWindow  implements ColorListAdapter.IColorListAction , TextWatcher {

    private View mView; // PopupWindow 菜单布局
    private Context mContext; // 上下文参数
    private View.OnClickListener mConfirmListener; // 确定的点击监听器
    public int[] mPaintColors = {R.color.color_list_1,
            R.color.color_list_2,R.color.color_list_4,R.color.color_list_5,
            R.color.color_list_6,R.color.color_list_7,R.color.color_list_8,R.color.color_list_9,
            R.color.color_list_10,R.color.color_list_11,R.color.color_list_12,R.color.color_list_13,
            R.color.color_list_14,R.color.color_list_15};

    private RecyclerView mColorListView;//颜色列表View
    private ColorListAdapter mColorAdapter;
    private ColorPicker mColorPicker;//颜色选择器
    private TextStickerView textStickerView;
    private EditText editText;
    private static String text;
    private static int curColor = -1;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public AddTextPopupWindow(Context context, View.OnClickListener confirmListener, TextStickerView textStickerView) {
        super(context);
        this.mContext = context;
        this.mConfirmListener = confirmListener;
        this.textStickerView = textStickerView;
        Init();
    }

    /**
     * 设置布局以及点击事件
     */
    private void Init() {
        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mView = inflater.inflate(R.layout.popupwindow_add_text, null);
        ImageView btn_confirm = mView.findViewById(R.id.iv_confirm);
        ImageView btn_cancel = mView.findViewById(R.id.iv_cancel);
        mColorListView = (RecyclerView) mView.findViewById(R.id.text_color_list);
        mColorPicker = new ColorPicker((Activity)mContext, 255, 0, 0);
        initColorListView();
        editText = mView.findViewById(R.id.add_text_edittext);
        editText.addTextChangedListener(this);
        if (text!=null){
            editText.setText(text);
        }
        if (curColor!=-1){
            editText.setTextColor(curColor);
            textStickerView.setTextColor(curColor);
        }
        textStickerView.setEditText(editText);
        btn_confirm.setOnClickListener(mConfirmListener);
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

    /**
     * 初始化颜色列表
     */
    private void initColorListView() {

        mColorListView.setHasFixedSize(false);

        LinearLayoutManager colorListLayoutManager = new LinearLayoutManager(mContext);
        colorListLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mColorListView.setLayoutManager(colorListLayoutManager);

        mColorAdapter = new ColorListAdapter(mContext, mPaintColors, this);
        mColorAdapter.setWhite(true);
        mColorListView.setAdapter(mColorAdapter);


    }

    @Override
    public void onColorSelected(int position, int color) {
        setTextColor(mContext.getResources().getColor(color));
        curColor = mContext.getResources().getColor(color);
    }

    @Override
    public void onMoreSelected(int position) {
        mColorPicker.show();
        Button okColor = (Button) mColorPicker.findViewById(R.id.okColorButton);
        okColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setTextColor(mColorPicker.getColor());
                curColor = mColorPicker.getColor();
                mColorPicker.dismiss();
            }
        });
    }

    protected void setTextColor(final int paintColor) {
        textStickerView.setTextColor(paintColor);
        editText.setTextColor(paintColor);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        //mTextStickerView change
        text = s.toString();
        //接受字符，放在StringBuffer里；
        StringBuffer str = new StringBuffer(text);
        //从后往前每隔三位插入一个逗号；
//        for (int i = 20;i < str.length(); i = i + 20) {
//            str.insert(i, "\n");
//        }
        textStickerView.setText(str.toString());
    }
}