package com.xinlan.imageeditlibrary.editimage.fragment;

import android.view.View;

import com.xinlan.imageeditlibrary.editimage.EditImageActivity;
import com.xinlan.imageeditlibrary.editimage.ModuleConfig;

public class ToneFragment extends BaseEditFragment{
    public static final int INDEX = ModuleConfig.INDEX_TONE;
    public static final String TAG = ToneFragment.class.getName();


    public static ToneFragment newInstance() {
        ToneFragment fragment = new ToneFragment();
        return fragment;
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
}
