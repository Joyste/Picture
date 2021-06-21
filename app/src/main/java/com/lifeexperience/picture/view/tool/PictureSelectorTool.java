package com.lifeexperience.picture.view.tool;

import android.app.Activity;
import android.content.pm.ActivityInfo;

import com.lifeexperience.picture.R;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.listener.OnResultCallbackListener;

import static com.luck.picture.lib.config.PictureMimeType.ofImage;

public class PictureSelectorTool {


    private static int MULTIPLE = PictureConfig.MULTIPLE;//多选
    private static int SINGLE = PictureConfig.SINGLE;//单选
    private static int maxSelectNum = 9;//最大数
    private static int minSelectNum = 1;//最小数
    private int selectionMode;//选择模式
    private Activity activity;
    private OnResultCallbackListener<LocalMedia> onResultCallbackListener;


    /**
     * 默认多选
     * @param activity
     * @param onResultCallbackListener
     */
    public PictureSelectorTool(Activity activity, OnResultCallbackListener<LocalMedia> onResultCallbackListener) {
        this.activity = activity;
        this.onResultCallbackListener = onResultCallbackListener;
        changeSelection(activity,MULTIPLE ,onResultCallbackListener);
    }

    /**
     * 更改选择模式 单选or多选
     * @param activity
     * @param selectionMode
     * @param onResultCallbackListener
     */
    public PictureSelectorTool(Activity activity, int selectionMode, OnResultCallbackListener<LocalMedia> onResultCallbackListener) {
        this.activity = activity;
        this.selectionMode = selectionMode;
        this.onResultCallbackListener = onResultCallbackListener;
        changeSelection(activity, selectionMode,onResultCallbackListener);
    }

    /**
     * 更改选择模式 单选or多选
     * @param activity
     * @param selectionMode
     * @param onResultCallbackListener
     */
    private void changeSelection(Activity activity,int selectionMode,OnResultCallbackListener<LocalMedia> onResultCallbackListener){
        com.luck.picture.lib.PictureSelector.create(activity)
                .openGallery(ofImage())// 全部.PictureMimeType.ofAll()、图片.ofImage()、视频.ofVideo()、音频.ofAudio()
                .imageEngine(GlideEngine.createGlideEngine())// 外部传入图片加载引擎，必传项
                .theme(R.style.picture_QQ_style)//风格
                .isMaxSelectEnabledMask(true)// 选择数到了最大阀值列表是否启用蒙层效果
                .maxSelectNum(maxSelectNum)// 最大图片选择数量
                .minSelectNum(minSelectNum)// 最小选择数量
                .isReturnEmpty(false)// 未选择数据时点击按钮是否可以返回
                .setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)// 设置相册Activity方向，不设置默认使用系统
                .isOriginalImageControl(true)// 是否显示原图控制按钮，如果设置为true则用户可以自由选择是否使用原图，压缩、裁剪功能将会失效
                .selectionMode(selectionMode)// 多选 PictureConfig.MULTIPLE or 单选 PictureConfig.SINGLE
                .isSingleDirectReturn(true)// 单选模式下是否直接返回，PictureConfig.SINGLE模式下有效
                .isPreviewImage(true)// 是否可预览图片
                .isCamera(false)// 是否显示拍照按钮
                .isZoomAnim(true)// 图片列表点击 缩放效果 默认true
                .isEnableCrop(false)// 是否裁剪
                .isCompress(false)// 是否压缩
                .synOrAsy(false)//同步true或异步false 压缩 默认同步
                .minimumCompressSize(100)// 小于多少kb的图片不压缩
                .forResult(onResultCallbackListener);

    }
}
