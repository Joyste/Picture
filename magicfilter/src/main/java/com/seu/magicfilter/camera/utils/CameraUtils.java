package com.seu.magicfilter.camera.utils;

import android.content.Context;
import android.hardware.Camera;
import android.view.Display;
import android.view.WindowManager;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.List;

import static com.seu.magicfilter.utils.MagicParams.context;

/**
 * Created by why8222 on 2016/2/25.
 */
public class CameraUtils {

    public static Camera.Size getLargePictureSize(Camera camera){
        if(camera != null){
            List<Camera.Size> sizes = camera.getParameters().getSupportedPictureSizes();
            Camera.Size temp = sizes.get(0);
            for(int i = 1;i < sizes.size();i ++){
                float scale = (float)(sizes.get(i).height) / sizes.get(i).width;
                if(temp.width < sizes.get(i).width && scale < 0.6f && scale > 0.5f)
                    temp = sizes.get(i);
            }
            return temp;
        }
        return null;
    }

    public static Camera.Size getLargePreviewSize(Camera camera){
        if(camera != null){
            Camera.Size size = getLargePictureSize(camera);
            float scale = (float)size.height/size.width;
            scale = new BigDecimal(scale).setScale(1, BigDecimal.ROUND_HALF_UP).floatValue();

            List<Camera.Size> sizes = camera.getParameters().getSupportedPreviewSizes();
            Camera.Size temp = sizes.get(0);
            for(int i = 1;i < sizes.size();i ++){
                float newScale = (float)sizes.get(i).height/sizes.get(i).width;
                newScale = new BigDecimal(newScale).setScale(1, BigDecimal.ROUND_HALF_UP).floatValue();

                if(temp.width < sizes.get(i).width && newScale == scale )
                    temp = sizes.get(i);
            }
            return temp;
        }
        return null;

    }
}
