package com.seu.magicfilter.camera.utils;

import android.content.Context;
import android.hardware.Camera;
import android.view.Display;
import android.view.WindowManager;

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

//        WindowManager wm = (WindowManager) context.getSystemService(
//                Context.WINDOW_SERVICE);
//        Display display = wm.getDefaultDisplay();
//        int screenWidth = display.getWidth();
//        int screenHeight = display.getHeight();
//
//        //摄像头画面显示在Surface上
//        if (camera != null) {
//            Camera.Parameters parameters = camera.getParameters();
//            List<Camera.Size> sizes = parameters.getSupportedPreviewSizes();
//            int[] a = new int[sizes.size()];
//            int[] b = new int[sizes.size()];
//            for (int i = 0; i < sizes.size(); i++) {
//                int supportH = sizes.get(i).height;
//                int supportW = sizes.get(i).width;
//                a[i] = Math.abs(supportW - screenHeight);
//                b[i] = Math.abs(supportH - screenWidth);
//            }
//            int minW=0,minA=a[0];
//            for( int i=0; i<a.length; i++){
//                if(a[i]<=minA){
//                    minW=i;
//                    minA=a[i];
//                }
//            }
//            int minH=0,minB=b[0];
//            for( int i=0; i<b.length; i++){
//                if(b[i]<minB){
//                    minH=i;
//                    minB=b[i];
//                }
//            }
////            List<Integer> list = parameters.getSupportedPreviewFrameRates();
//            return sizes.get(minW);
////            parameters.setPreviewFrameRate(list.get(list.size() - 1));
////            camera.setParameters(parameters);
////            camera.setDisplayOrientation(90);
////            camera.startPreview();
//        }
//        return null;
//

        if(camera != null){
            List<Camera.Size> sizes = camera.getParameters().getSupportedPreviewSizes();
            Camera.Size temp = sizes.get(0);
            for(int i = 1;i < sizes.size();i ++){
                if(temp.width < sizes.get(i).width)
                    temp = sizes.get(i);
            }
            return temp;
        }
        return null;

    }
}
