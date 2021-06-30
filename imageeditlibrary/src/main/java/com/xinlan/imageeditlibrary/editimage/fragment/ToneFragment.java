package com.xinlan.imageeditlibrary.editimage.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;

import androidx.recyclerview.widget.RecyclerView;

import com.xinlan.imageeditlibrary.R;
import com.xinlan.imageeditlibrary.editimage.EditImageActivity;
import com.xinlan.imageeditlibrary.editimage.ModuleConfig;
import com.xinlan.imageeditlibrary.editimage.ui.ColorPicker;
import com.xinlan.imageeditlibrary.editimage.view.CustomPaintView;
import com.xinlan.imageeditlibrary.editimage.view.PaintModeView;

public class ToneFragment extends BaseEditFragment implements View.OnClickListener, SeekBar.OnSeekBarChangeListener {
    public static final int INDEX = ModuleConfig.INDEX_TONE;
    public static final String TAG = ToneFragment.class.getName();

    private View mainView;
    private View backToMenu;// 返回主菜单
    private RecyclerView mToneListView;//色值列表View
    private SeekBar mToneSeekBar;//色值列表View
    private View mReset;



    public static ToneFragment newInstance() {
        ToneFragment fragment = new ToneFragment();
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mainView = inflater.inflate(R.layout.fragment_edit_image_tone, null);
        return mainView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        backToMenu = mainView.findViewById(R.id.back_to_main);
        mToneListView = mainView.findViewById(R.id.tone_list);
        mToneSeekBar = mainView.findViewById(R.id.tone_seekbar);
        mReset = mainView.findViewById(R.id.btn_reset);
        backToMenu.setOnClickListener(this);// 返回主菜单

        initToneListView();
        mToneSeekBar.setOnSeekBarChangeListener(this);


    }

    /**
     *初始化set
     */
    private void initToneListView() {


    }


    @Override
    public void onClick(View v) {
        if (v == backToMenu) {//back button click
            backToMain();
        }else if (v == mReset){

        }
    }
    /**
     * 返回主菜单
     */
    @Override
    public void backToMain() {
        activity.mode = EditImageActivity.MODE_NONE;
        activity.bottomGallery.setCurrentItem(MainMenuFragment.INDEX);
        activity.mainImage.setImageBitmap(activity.getMainBit());// 返回原图

        activity.mainImage.setVisibility(View.VISIBLE);
        activity.mainImage.setScaleEnabled(true);
        activity.bannerFlipper.showPrevious();
    }


    @Override
    public void onShow() {
        activity.mode = EditImageActivity.MODE_TONE;
        activity.mainImage.setImageBitmap(activity.getMainBit());
        activity.bannerFlipper.showNext();
//        mTextStickerView.setVisibility(View.VISIBLE);
    }


    /**
     * 保存图片
     */
    public void applyToneImage() {

        activity.changeMainBitmap(activity.getMainBit(),true);

        backToMain();
    }


    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}
