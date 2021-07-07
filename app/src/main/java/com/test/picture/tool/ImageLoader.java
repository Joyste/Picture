package com.test.picture.tool;

import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.test.picture.R;

/**
 * Created by v_yanligang on 2016/9/13.
 */
public class ImageLoader {
    public static void load (ImageView imageView, String path, int width, int height) {
        Glide.with(imageView.getContext())
                .load(path)
                .override(width, height)
                .into(imageView);
    }

    public static void loadwithDefault (ImageView imageView, String path, int width, int height) {
        Glide.with(imageView.getContext())
                .load(path)
                .override(width, height)
                .placeholder(R.mipmap.ic_launcher)
                .into(imageView);
    }
}
