package com.xinlan.imageeditlibrary.editimage.fragment;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.xinlan.imageeditlibrary.R;
import com.xinlan.imageeditlibrary.editimage.EditImageActivity;
import com.xinlan.imageeditlibrary.editimage.ModuleConfig;
import com.xinlan.imageeditlibrary.editimage.adapter.ToneListAdapter;
import com.xinlan.imageeditlibrary.editimage.model.ToneItem;
import com.xinlan.imageeditlibrary.editimage.utils.PhotoEnhance;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ToneFragment extends BaseEditFragment implements View.OnClickListener {
    public static final int INDEX = ModuleConfig.INDEX_TONE;
    public static final String TAG = ToneFragment.class.getName();

    private View mainView;
    private View backToMenu;// 返回主菜单
    private RecyclerView mToneListView;//色值列表View
    private SeekBar mToneSeekBar;//色值列表View
    private TextView seekbarValue;
    private ToneListAdapter mToneListAdapter;
    private List<ToneItem> dataList = new ArrayList<>();


    private PopupWindow setToneValueWindow;
    private static int POSITION = 0;
    private View popView;
    private TextView tvToneName;
    private TextView tvToneValue;
    private ImageView ivToneIcon;

    private View currentItem;

    private static final int MAX_VALUE = 255;
    private static final int MID_VALUE = 128;

    private PhotoEnhance mPhotoEnhance;
    private int mProgress = 0;

    private Bitmap editBitmap;

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
        editBitmap = activity.getMainBit();
        backToMenu = mainView.findViewById(R.id.back_to_main);
        mToneListView = mainView.findViewById(R.id.tone_list);
        backToMenu.setOnClickListener(this);// 返回主菜单

        initData();
        initToneListView();
        initStokeWidthPopWindow();

    }

    /**
     * 初始化Item数据
     */
    private void initData(){
        dataList.add(new ToneItem(R.drawable.crop_normal,R.string.luminance,MID_VALUE ));//亮度
        dataList.add(new ToneItem(R.drawable.crop_normal,R.string.contrast,MID_VALUE ));//对比度
        dataList.add(new ToneItem(R.drawable.crop_normal,R.string.saturation,MID_VALUE ));//饱和度
        dataList.add(new ToneItem(R.drawable.crop_normal,R.string.hue,MID_VALUE ));//色相
        dataList.add(new ToneItem(R.drawable.crop_normal,R.string.exposure,MID_VALUE ));//曝光
        mPhotoEnhance = new PhotoEnhance();
        mPhotoEnhance.setBitmap(editBitmap);
        mProgress = MID_VALUE;
    }

    /**
     * 初始化SeekBar
     */
    private void initStokeWidthPopWindow() {
        popView = LayoutInflater.from(activity).inflate(R.layout.view_set_stoke_width, null);
        setToneValueWindow = new PopupWindow(popView, ViewGroup.LayoutParams.MATCH_PARENT
                , ViewGroup.LayoutParams.WRAP_CONTENT);

        mToneSeekBar = (SeekBar) popView.findViewById(R.id.stoke_width_seekbar);
        seekbarValue = popView.findViewById(R.id.seekbar_value);

        setToneValueWindow.setFocusable(false);
        setToneValueWindow.setOutsideTouchable(false);
        setToneValueWindow.setBackgroundDrawable(new BitmapDrawable());
        setToneValueWindow.setAnimationStyle(R.style.popwin_anim_style);
    }


    /**
     *初始化recycleView
     */
    private void initToneListView() {

        mToneListView.setHasFixedSize(false);

        LinearLayoutManager stickerListLayoutManager = new LinearLayoutManager(activity);
        stickerListLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mToneListView.setLayoutManager(stickerListLayoutManager);

        mToneListAdapter = new ToneListAdapter(getContext(),dataList);
        mToneListAdapter.setOnItemClickListener(new ToneListAdapter.OnItemClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onItemClick(View itemView, int position) {
                POSITION = position;
                setToneValue(itemView);
                showOnClick(itemView);
                currentItem = itemView;
            }
        });
        mToneListView.setAdapter(mToneListAdapter);

    }


    /**
     * 设置数据
     */
    protected void setToneValue(View itemView) {
        if (popView.getMeasuredHeight() == 0) {
            popView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        }

        mToneSeekBar.setMax(MAX_VALUE);
        mToneSeekBar.setProgress(dataList.get(POSITION).getValue());

        mToneSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mProgress = progress;
                seekbarValue.setText(String.valueOf(progress-MID_VALUE));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                dataList.get(POSITION).setValue(mProgress);
                tvToneValue = itemView.findViewById(R.id.tv_tone_value);
                tvToneValue.setText(String.valueOf(mProgress-MID_VALUE));

                int type = 0;
                switch (POSITION){
                    case 0:{
                        //亮度调节
                        mPhotoEnhance.setBrightness(mProgress);
                        type = PhotoEnhance.ENHANCE_BRIGHTNESS;
                        break;
                    }
                    case 1:{
                        //对比度调节
                        mPhotoEnhance.setContrast(mProgress);
                        type = PhotoEnhance.ENHANCE_CONTRAST;
                        break;
                    }
                    case 2:{
                        //饱和度调节
                        mPhotoEnhance.setSaturation(mProgress);
                        type = PhotoEnhance.ENHANCE_SATURATION;
                        break;
                    }
                    case 3:{
                        //色相调节
                        mPhotoEnhance.setHue(mProgress);
                        type = PhotoEnhance.ENHANCE_HUE;
                        break;
                    }
                    case 4:{
                        //曝光调节
                        mPhotoEnhance.setExposure(mProgress);
                        type = PhotoEnhance.ENHANCE_EXPOSURE;
                        break;
                    }
                }

                editBitmap = mPhotoEnhance.handleImage(type);
                activity.mainImage.setImageBitmap(editBitmap);
            }
        });

        //设置SeeKBar的位置
        int[] locations = new int[2];
        activity.bottomGallery.getLocationOnScreen(locations);
        setToneValueWindow.showAtLocation(activity.bottomGallery,
                Gravity.NO_GRAVITY, 0, locations[1] - popView.getMeasuredHeight());
    }

    @Override
    public void onClick(View v) {
        if (v == backToMenu) {//back button click
            backToMain();
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

        setToneValueWindow.dismiss();
        dataList.clear();
    }


    @Override
    public void onShow() {
        activity.mode = EditImageActivity.MODE_TONE;
        activity.mainImage.setImageBitmap(activity.getMainBit());
        activity.bannerFlipper.showNext();
    }

    /**
     * 保存图片
     */
    public void applyToneImage() {

        if(editBitmap!=activity.getMainBit()){
            activity.changeMainBitmap(editBitmap,true);
        }else {
            activity.changeMainBitmap(activity.getMainBit(),true);
        }
        backToMain();
    }

    private void showOnClick(View itemView){
        if(currentItem!=itemView && currentItem!=null ){
            tvToneName = currentItem.findViewById(R.id.tv_tone_name);
            tvToneValue = currentItem.findViewById(R.id.tv_tone_value);
            tvToneName.setTextColor(getResources().getColor(R.color.white));
            tvToneValue.setTextColor(getResources().getColor(R.color.white));
        }

        tvToneName = itemView.findViewById(R.id.tv_tone_name);
        tvToneValue = itemView.findViewById(R.id.tv_tone_value);
        tvToneName.setTextColor(getResources().getColor(R.color.materialcolorpicker__blue));
        tvToneValue.setTextColor(getResources().getColor(R.color.materialcolorpicker__blue));
    }


}
