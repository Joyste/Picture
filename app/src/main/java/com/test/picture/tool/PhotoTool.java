package com.test.picture.tool;

import android.app.Activity;

import androidx.annotation.NonNull;

import com.huantansheng.easyphotos.EasyPhotos;
import com.huantansheng.easyphotos.engine.ImageEngine;
import com.huantansheng.easyphotos.models.album.entity.Photo;
import com.test.picture.utils.FileUtils;
import com.test.picture.view.MainActivity;

import java.util.ArrayList;

public class PhotoTool {
    //单例
    private static PhotoTool instance = null;
    private static String fileProviderAuthority = "com.test.picture.fileProvider";

    //单例模式，私有构造方法
    private PhotoTool() {
    }

    //获取单例
    public static PhotoTool getInstance() {
        if (null == instance) {
            synchronized (PhotoTool.class) {
                if (null == instance) {
                    instance = new PhotoTool();
                }
            }
        }
        return instance;
    }


    /**
     * 拼图
     *
     * @param activity
     * @param photos
     * @param REQUEST_CODE
     */
    public void startPuzzleWithPhotos(Activity activity, ArrayList<Photo> photos, int REQUEST_CODE) {
        EasyPhotos.startPuzzleWithPhotos(activity, photos, FileUtils.createFolders().getPath(),
                "puzzle_", REQUEST_CODE, false, GlideEngine.getInstance());
    }

    /**
     * 单选
     *
     * @param activity
     * @param REQUEST_CODE
     */
    public void openAlbumForSingle(Activity activity, int REQUEST_CODE) {
        EasyPhotos.createAlbum(activity, false, true, GlideEngine.getInstance())//参数说明：上下文，是否显示相机按钮,是否使用宽高数据（false时宽高数据为0，扫描速度更快），[配置Glide为图片加载引擎](https://github.com/HuanTanSheng/EasyPhotos/wiki/12-%E9%85%8D%E7%BD%AEImageEngine%EF%BC%8C%E6%94%AF%E6%8C%81%E6%89%80%E6%9C%89%E5%9B%BE%E7%89%87%E5%8A%A0%E8%BD%BD%E5%BA%93)
                .setPuzzleMenu(false)
                .start(REQUEST_CODE);
    }


    /**
     * 多选拼图
     *
     * @param activity
     * @param count
     * @param photos
     * @param REQUEST_CODE
     */
    public void openAlbumForMultiple(Activity activity, int count, ArrayList<Photo> photos, int REQUEST_CODE) {
        openAlbum(activity, false, false, GlideEngine.getInstance(), false, count, photos, REQUEST_CODE);
    }


    private void openAlbum(Activity activity, boolean isShowCamera, boolean useWidth, @NonNull ImageEngine imageEngine, boolean isPuzzle, int count, ArrayList<Photo> photos, int REQUEST_CODE) {
        EasyPhotos.createAlbum(activity, isShowCamera, useWidth, imageEngine)//参数说明：上下文，是否显示相机按钮,是否使用宽高数据（false时宽高数据为0，扫描速度更快），[配置Glide为图片加载引擎](https://github.com/HuanTanSheng/EasyPhotos/wiki/12-%E9%85%8D%E7%BD%AEImageEngine%EF%BC%8C%E6%94%AF%E6%8C%81%E6%89%80%E6%9C%89%E5%9B%BE%E7%89%87%E5%8A%A0%E8%BD%BD%E5%BA%93)
                .setPuzzleMenu(isPuzzle)
                .setFileProviderAuthority(fileProviderAuthority)
                .setCount(count)
                .setSelectedPhotos(photos)
                .start(REQUEST_CODE);
    }
}
