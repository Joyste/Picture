package com.xinlan.imageeditlibrary.editimage.fragment;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
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
import com.xinlan.imageeditlibrary.editimage.model.appInfo;
import com.xinlan.imageeditlibrary.editimage.utils.PhotoEnhance;

import java.util.ArrayList;
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
    private List<appInfo> dataList = new ArrayList<>();


    private PopupWindow setToneValueWindow;
    private static int POSITION = 0;
    private View popView;
    private TextView tvToneName;
    private TextView tvToneValue;
    private ImageView ivToneIcon;

    private View currentItem;
    private int currentPosition;

    private static final int MAX_VALUE = 255;
    private static final int MID_VALUE = 128;

    private PhotoEnhance mPhotoEnhance;
    private int mProgress = 0;

    private Bitmap editBitmap;
    private int icoNormal[] = {R.drawable.ico_brightness_normal,R.drawable.ico_contrast_normal,R.drawable.ico_saturation_normal,R.drawable.ico_color_temp_normal,R.drawable.ico_exposure_normal};
    private int icoSelected[] = {R.drawable.ico_brightness_selected,R.drawable.ico_contrast_selected,R.drawable.ico_saturation_selected,R.drawable.ico_color_temp_selected,R.drawable.ico_exposure_selected};


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
        initSetToneValuePopWindow();

    }

    /**
     * 初始化Item数据
     */
    private void initData() {
        dataList.clear();
        dataList.add(new appInfo(icoNormal[0], R.string.luminance, MID_VALUE));//亮度
        dataList.add(new appInfo(icoNormal[1], R.string.contrast, MID_VALUE));//对比度
        dataList.add(new appInfo(icoNormal[2], R.string.saturation, MID_VALUE));//饱和度
        dataList.add(new appInfo(icoNormal[3], R.string.hue, MID_VALUE));//色温
        dataList.add(new appInfo(icoNormal[4], R.string.exposure, MID_VALUE));//曝光
        mPhotoEnhance = new PhotoEnhance();
        mPhotoEnhance.setBitmap(editBitmap);
        mProgress = MID_VALUE;
    }

    /**
     * 初始化SeekBar
     */
    private void initSetToneValuePopWindow() {
        popView = LayoutInflater.from(activity).inflate(R.layout.view_set_adjust_value, null);
        setToneValueWindow = new PopupWindow(popView, ViewGroup.LayoutParams.MATCH_PARENT
                , ViewGroup.LayoutParams.WRAP_CONTENT);

        mToneSeekBar = (SeekBar) popView.findViewById(R.id.tone_value_seekbar);
        seekbarValue = popView.findViewById(R.id.seekbar_value);

        setToneValueWindow.setFocusable(false);
        setToneValueWindow.setOutsideTouchable(false);
        setToneValueWindow.setBackgroundDrawable(new BitmapDrawable());
        setToneValueWindow.setAnimationStyle(R.style.popwin_anim_style);
    }


    /**
     * 初始化recycleView
     */
    private void initToneListView() {

        mToneListView.setHasFixedSize(false);

        LinearLayoutManager stickerListLayoutManager = new LinearLayoutManager(activity);
        stickerListLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mToneListView.setLayoutManager(stickerListLayoutManager);

        mToneListAdapter = new ToneListAdapter(getContext(), dataList);
        mToneListAdapter.setOnItemClickListener(new ToneListAdapter.OnItemClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onItemClick(View itemView, int position) {
                POSITION = position;
                setToneValue(itemView);
                showOnClick(itemView,position);
                currentItem = itemView;
                currentPosition = position;
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
                seekbarValue.setText(String.valueOf(progress - MID_VALUE));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                dataList.get(POSITION).setValue(mProgress);
                tvToneValue = itemView.findViewById(R.id.tv_tone_value);
                tvToneValue.setText(String.valueOf(mProgress - MID_VALUE));

                int type = 0;
                switch (POSITION) {
                    case 0: {
                        //亮度调节
                        mPhotoEnhance.setBrightness(mProgress);
                        type = PhotoEnhance.ENHANCE_BRIGHTNESS;
                        break;
                    }
                    case 1: {
                        //对比度调节
                        mPhotoEnhance.setContrast(mProgress);
                        type = PhotoEnhance.ENHANCE_CONTRAST;
                        break;
                    }
                    case 2: {
                        //饱和度调节
                        mPhotoEnhance.setSaturation(mProgress);
                        type = PhotoEnhance.ENHANCE_SATURATION;
                        break;
                    }
                    case 3: {
                        //色相调节
                        mPhotoEnhance.setHue(mProgress);
                        type = PhotoEnhance.ENHANCE_HUE;
                        break;
                    }
                    case 4: {
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
        if (editBitmap != activity.getMainBit()) {
            showAlertDialog(getContext(), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    applyToneImage();
                }
            }, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    back();
                }
            });
        } else {
            back();
        }
    }

    private void back(){
        activity.mode = EditImageActivity.MODE_NONE;
        activity.bottomGallery.setCurrentItem(MainMenuFragment.INDEX);
        activity.mainImage.setImageBitmap(activity.getMainBit());// 返回原图
        activity.mainImage.setVisibility(View.VISIBLE);
        activity.mainImage.setScaleEnabled(true);
        activity.bannerFlipper.showPrevious();

        setToneValueWindow.dismiss();
        dataList.clear();
        activity.setRedoUndoPanelVisibility(View.VISIBLE);
    }


    @Override
    public void onShow() {
        activity.mode = EditImageActivity.MODE_TONE;
        activity.mainImage.setImageBitmap(activity.getMainBit());
        activity.bannerFlipper.showNext();
        activity.setRedoUndoPanelVisibility(View.INVISIBLE);
    }

    /**
     * 保存图片
     */
    public void applyToneImage() {

        if (editBitmap != activity.getMainBit()) {
            activity.changeMainBitmap(editBitmap, true);
        } else {
            activity.changeMainBitmap(activity.getMainBit(), true);
        }
        back();
    }

    private void showOnClick(View itemView ,int position) {
        if (currentItem != itemView && currentItem != null) {
            tvToneName = currentItem.findViewById(R.id.tv_tone_name);
            tvToneValue = currentItem.findViewById(R.id.tv_tone_value);
            ivToneIcon = currentItem.findViewById(R.id.iv_tone_icon);
            if (tvToneValue.getText().equals("0")) {
                ivToneIcon.setImageResource(icoNormal[currentPosition]);
                tvToneName.setTextColor(getResources().getColor(R.color.black));
                tvToneValue.setTextColor(getResources().getColor(R.color.black));
            }

        }

        tvToneName = itemView.findViewById(R.id.tv_tone_name);
        tvToneValue = itemView.findViewById(R.id.tv_tone_value);
        ivToneIcon = itemView.findViewById(R.id.iv_tone_icon);

        ivToneIcon.setImageResource(icoSelected[position]);
        tvToneName.setTextColor(getResources().getColor(R.color.theme_color));
        tvToneValue.setTextColor(getResources().getColor(R.color.theme_color));
    }


}
