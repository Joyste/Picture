package com.seu.magicfilter.widget;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.opengl.EGL14;
import android.opengl.GLES20;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.RequiresApi;

import com.seu.magicfilter.R;
import com.seu.magicfilter.camera.CameraEngine;
import com.seu.magicfilter.camera.utils.CameraInfo;
import com.seu.magicfilter.encoder.video.TextureMovieEncoder;
import com.seu.magicfilter.filter.advanced.MagicBeautyFilter;
import com.seu.magicfilter.filter.base.MagicCameraInputFilter;
import com.seu.magicfilter.filter.helper.MagicFilterType;
import com.seu.magicfilter.helper.SavePictureTask;
import com.seu.magicfilter.utils.MagicParams;
import com.seu.magicfilter.utils.OpenGlUtils;
import com.seu.magicfilter.utils.Rotation;
import com.seu.magicfilter.utils.TextureRotationUtil;
import com.seu.magicfilter.widget.base.MagicBaseView;

import java.io.File;
import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;


import static android.content.ContentValues.TAG;
import static com.seu.magicfilter.camera.CameraEngine.getCameraID;

/**
 * Created by why8222 on 2016/2/25.
 */
public class MagicCameraView extends MagicBaseView {

    private MagicCameraInputFilter cameraInputFilter;
    private MagicBeautyFilter beautyFilter;

    private SurfaceTexture surfaceTexture;

    public MagicCameraView(Context context) {
        this(context, null);
    }

    private boolean recordingEnabled;
    private int recordingStatus;

    private static final int RECORDING_OFF = 0;
    private static final int RECORDING_ON = 1;
    private static final int RECORDING_RESUMED = 2;
    private static TextureMovieEncoder videoEncoder = new TextureMovieEncoder();


    private File outputFile;
    private int mSensorRotation;
    private float ratio = 4/3f;

    public int getmSensorRotation() {
        return mSensorRotation;
    }

    public void setmSensorRotation(int mSensorRotation) {
        this.mSensorRotation = mSensorRotation;
    }

    public float getRatio() {
        return ratio;
    }

    public void setRatio(float ratio) {
        this.ratio = ratio;
    }

    public MagicCameraView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.getHolder().addCallback(this);
        outputFile = new File(MagicParams.videoPath, MagicParams.videoName);
        recordingStatus = -1;
        recordingEnabled = false;
        scaleType = ScaleType.CENTER_CROP;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        super.onSurfaceCreated(gl, config);
        recordingEnabled = videoEncoder.isRecording();
        if (recordingEnabled)
            recordingStatus = RECORDING_RESUMED;
        else
            recordingStatus = RECORDING_OFF;
        if (cameraInputFilter == null)
            cameraInputFilter = new MagicCameraInputFilter();
        cameraInputFilter.init();
        if (textureId == OpenGlUtils.NO_TEXTURE) {
            textureId = OpenGlUtils.getExternalOESTextureID();
            if (textureId != OpenGlUtils.NO_TEXTURE) {
                surfaceTexture = new SurfaceTexture(textureId);
                surfaceTexture.setOnFrameAvailableListener(onFrameAvailableListener);
            }
        }
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        super.onSurfaceChanged(gl, width, height);
        openCamera();
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public void onDrawFrame(GL10 gl) {
        super.onDrawFrame(gl);
        if (surfaceTexture == null)
            return;
        surfaceTexture.updateTexImage();
        if (recordingEnabled) {
            switch (recordingStatus) {
                case RECORDING_OFF:
                    CameraInfo info = CameraEngine.getCameraInfo();
                    videoEncoder.setPreviewSize(info.previewWidth, info.pictureHeight);
                    videoEncoder.setTextureBuffer(gLTextureBuffer);
                    videoEncoder.setCubeBuffer(gLCubeBuffer);
                    videoEncoder.startRecording(new TextureMovieEncoder.EncoderConfig(
                            outputFile, info.previewWidth, info.pictureHeight,
                            1000000, EGL14.eglGetCurrentContext(),
                            info));
                    recordingStatus = RECORDING_ON;
                    break;
                case RECORDING_RESUMED:
                    videoEncoder.updateSharedContext(EGL14.eglGetCurrentContext());
                    recordingStatus = RECORDING_ON;
                    break;
                case RECORDING_ON:
                    break;
                default:
                    throw new RuntimeException("unknown status " + recordingStatus);
            }
        } else {
            switch (recordingStatus) {
                case RECORDING_ON:
                case RECORDING_RESUMED:
                    videoEncoder.stopRecording();
                    recordingStatus = RECORDING_OFF;
                    break;
                case RECORDING_OFF:
                    break;
                default:
                    throw new RuntimeException("unknown status " + recordingStatus);
            }
        }
        float[] mtx = new float[16];
        surfaceTexture.getTransformMatrix(mtx);
        cameraInputFilter.setTextureTransformMatrix(mtx);
        int id = textureId;
        if (filter == null) {
            cameraInputFilter.onDrawFrame(textureId, gLCubeBuffer, gLTextureBuffer);
        } else {
            id = cameraInputFilter.onDrawToTexture(textureId);
            filter.onDrawFrame(id, gLCubeBuffer, gLTextureBuffer);
        }
        videoEncoder.setTextureId(id);
        videoEncoder.frameAvailable(surfaceTexture);
    }

    private SurfaceTexture.OnFrameAvailableListener onFrameAvailableListener = new SurfaceTexture.OnFrameAvailableListener() {

        @Override
        public void onFrameAvailable(SurfaceTexture surfaceTexture) {
            requestRender();
        }
    };

    @Override
    public void setFilter(MagicFilterType type) {
        super.setFilter(type);
        videoEncoder.setFilter(type);
    }

    private void openCamera() {
        if (CameraEngine.getCamera() == null) {
            CameraEngine.openCamera(0);
        }
        CameraInfo info = CameraEngine.getCameraInfo();
        if (info.orientation == 90 || info.orientation == 270) {
            imageWidth = info.previewHeight;
            imageHeight = info.previewWidth;
        } else {
            imageWidth = info.previewWidth;
            imageHeight = info.previewHeight;
        }
        cameraInputFilter.onInputSizeChanged(imageWidth, imageHeight);
        adjustSize(info.orientation, info.isFront, true);
        if (surfaceTexture != null)
            CameraEngine.startPreview(surfaceTexture);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        super.surfaceDestroyed(holder);
        CameraEngine.releaseCamera();
    }

    public void changeRecordingState(boolean isRecording) {
        recordingEnabled = isRecording;
    }

    protected void onFilterChanged() {
        super.onFilterChanged();
        cameraInputFilter.onDisplaySizeChanged(surfaceWidth, surfaceHeight);
        if (filter != null)
            cameraInputFilter.initCameraFrameBuffer(imageWidth, imageHeight);
        else
            cameraInputFilter.destroyFramebuffers();
    }

    @Override
    public void savePicture(final SavePictureTask savePictureTask) {
        CameraEngine.takePicture(null, null, new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] data, Camera camera) {
                CameraEngine.stopPreview();
                final Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                queueEvent(new Runnable() {
                    @Override
                    public void run() {
                        CameraInfo cameraInfo = CameraEngine.getCameraInfo();
                        Bitmap photo = drawPhoto(bitmap, cameraInfo.isFront);
                        GLES20.glViewport(0, 0, surfaceWidth, surfaceHeight);
                        if (photo != null) {
                            Matrix matrix = new Matrix();
                            //??????????????????????????????????????????????????? ?????? ?????????????????????
                            int rotation = (calculateCameraPreviewOrientation((Activity) MagicParams.context)) % 360;
                            if (getCameraID() == Camera.CameraInfo.CAMERA_FACING_BACK) {
                                //????????????????????????????????????????????????????????????????????????
                                matrix.setRotate(rotation);
                            } else {
                                //????????????????????????????????????????????????????????????????????????postScale(-1, 1)
                                //????????????????????????360-rotation?????????????????????????????????????????????
                                Log.d("=========", "rotation"+rotation);
                                rotation = (rotation + 180) % 360;
//                                Log.d("=========", "rotation"+rotation);
                                matrix.setRotate(rotation);
                                matrix.postScale(-1, 1);
                            }
                            photo = Bitmap.createBitmap(photo, 0, 0, photo.getWidth(), photo.getHeight(), matrix, true);
                            if(photo.getWidth() > photo.getHeight()) {
                                photo = rotateBitmapByDegree(photo,-90);
                            }
                            photo = rotateBitmapByDegree(photo,mSensorRotation);
                            Log.d("run", "run: "+ratio);
                            photo = cropBitmap(photo,ratio);
                            savePictureTask.execute(photo);
                        }
                    }
                });
                CameraEngine.startPreview();
            }
        });
    }

    /**
     * ???????????????????????????????????????
     *
     * @param bm
     *            ?????????????????????
     * @param degree
     *            ????????????
     * @return ??????????????????
     */
    public static Bitmap rotateBitmapByDegree(Bitmap bm, int degree) {
        Bitmap returnBm = null;

        // ???????????????????????????????????????
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        try {
            // ?????????????????????????????????????????????????????????????????????
            returnBm = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), matrix, true);
        } catch (OutOfMemoryError e) {
        }
        if (returnBm == null) {
            returnBm = bm;
        }
        if (bm != returnBm) {
            bm.recycle();
        }
        return returnBm;

    }


    /**
     * ?????????????????????setDisplayOrientation?????????????????????????????????
     * previewFrameCallback???????????????????????????????????????????????????????????????????????????????????????????????????
     * ?????????????????????????????????
     */
    public static int calculateCameraPreviewOrientation(Activity context) {
        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(getCameraID(), info);
        int rotation = context.getWindowManager().getDefaultDisplay().getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }

        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;
        } else {
            result = (info.orientation - degrees + 360) % 360;
        }
        return result;
    }

    private Bitmap drawPhoto(Bitmap bitmap, boolean isRotated) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int[] mFrameBuffers = new int[1];
        int[] mFrameBufferTextures = new int[1];
        if (beautyFilter == null)
            beautyFilter = new MagicBeautyFilter();
        beautyFilter.init();
        beautyFilter.onDisplaySizeChanged(width, height);
        beautyFilter.onInputSizeChanged(width, height);

        if (filter != null) {
            filter.onInputSizeChanged(width, height);
            filter.onDisplaySizeChanged(width, height);
        }

        GLES20.glGenFramebuffers(1, mFrameBuffers, 0);
        GLES20.glGenTextures(1, mFrameBufferTextures, 0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mFrameBufferTextures[0]);
        GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, width, height, 0,
                GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, null);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, mFrameBuffers[0]);
        GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0,
                GLES20.GL_TEXTURE_2D, mFrameBufferTextures[0], 0);

        GLES20.glViewport(0, 0, width, height);
        int textureId = OpenGlUtils.loadTexture(bitmap, OpenGlUtils.NO_TEXTURE, true);

        FloatBuffer gLCubeBuffer = ByteBuffer.allocateDirect(TextureRotationUtil.CUBE.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        FloatBuffer gLTextureBuffer = ByteBuffer.allocateDirect(TextureRotationUtil.TEXTURE_NO_ROTATION.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        gLCubeBuffer.put(TextureRotationUtil.CUBE).position(0);
        if (isRotated)
            gLTextureBuffer.put(TextureRotationUtil.getRotation(Rotation.NORMAL, false, true)).position(0);
        else
            gLTextureBuffer.put(TextureRotationUtil.getRotation(Rotation.NORMAL, false, true)).position(0);


        if (filter == null) {
            beautyFilter.onDrawFrame(textureId, gLCubeBuffer, gLTextureBuffer);
        } else {
            beautyFilter.onDrawFrame(textureId);
            filter.onDrawFrame(mFrameBufferTextures[0], gLCubeBuffer, gLTextureBuffer);
        }
        IntBuffer ib = IntBuffer.allocate(width * height);
        GLES20.glReadPixels(0, 0, width, height, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, ib);
        Bitmap result = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        result.copyPixelsFromBuffer(ib);

        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
        GLES20.glDeleteTextures(1, new int[]{textureId}, 0);
        GLES20.glDeleteFramebuffers(mFrameBuffers.length, mFrameBuffers, 0);
        GLES20.glDeleteTextures(mFrameBufferTextures.length, mFrameBufferTextures, 0);

        beautyFilter.destroy();
        beautyFilter = null;
        if (filter != null) {
            filter.onDisplaySizeChanged(surfaceWidth, surfaceHeight);
            filter.onInputSizeChanged(imageWidth, imageHeight);
        }
        return result;
    }

    public void onBeautyLevelChanged() {
        cameraInputFilter.onBeautyLevelChanged();
    }

    /**
     * ??????
     *
     * @param bitmap ??????
     * @return ??????????????????
     */
    private Bitmap cropBitmap(Bitmap bitmap,float ratio) {
        int w = bitmap.getWidth(); // ????????????????????????
        int h = bitmap.getHeight();

        Log.d("==========", "cropBitmap: w???"+w+",h:"+h);

        if(h>w){
            if(ratio == 1f){
                int y = (h-w)/2;
                return Bitmap.createBitmap(bitmap, 0, y, w, w, null, false);
            }else if(ratio == 4/3f){
                float newRatio = (float)h/w;
                newRatio = new BigDecimal(newRatio).setScale(10, BigDecimal.ROUND_HALF_UP).floatValue();
                if(newRatio - ratio >= 0){
                    int y = (int)(h-w*ratio)/2;
                    y = Math.max(y, 0);
                    return Bitmap.createBitmap(bitmap, 0, y, w,(int)(w*ratio), null, false);
                }else {
                    int x = (int)(w-h/ratio)/2;
                    x = Math.max(x, 0);
                    return Bitmap.createBitmap(bitmap, x, 0, (int)(h/ratio),h, null, false);
                }
            }else if(ratio == 16/9f){
                float newRatio = (float)h/w;
                newRatio = new BigDecimal(newRatio).setScale(10, BigDecimal.ROUND_HALF_UP).floatValue();
                if(newRatio - ratio >= 0){
                    int y = (int)(h-w*ratio)/2;
                    y = Math.max(y, 0);
                    return Bitmap.createBitmap(bitmap, 0, y, w,(int)(w*ratio), null, false);
                }else {
                    int x = (int)(w-h/ratio)/2;
                    x = Math.max(x, 0);
                    return Bitmap.createBitmap(bitmap, x, 0, (int)(h/ratio),h, null, false);
                }
            }
        }else {
            if(ratio == 1f){
                int x = (w-h)/2;
                return Bitmap.createBitmap(bitmap, x, 0, h, h, null, false);
            }else if(ratio == 4/3f){
                float newRatio = (float)w/h;
                newRatio = new BigDecimal(newRatio).setScale(10, BigDecimal.ROUND_HALF_UP).floatValue();
                if(newRatio - ratio >= 0 ){
                    int x = (int)(w-h*ratio)/2;
                    x = Math.max(x, 0);
                    return Bitmap.createBitmap(bitmap, x, 0, (int)(h*ratio),h, null, false);
                }else {
                    int y = (int)(h-w/ratio)/2;
                    y = Math.max(y, 0);
                    return Bitmap.createBitmap(bitmap, 0, y,w,(int)(w/ratio), null, false);
                }
            }else if(ratio == 16/9f){
                float newRatio = (float)w/h;
                newRatio = new BigDecimal(newRatio).setScale(10, BigDecimal.ROUND_HALF_UP).floatValue();
                if(newRatio - ratio >= 0){
                    int x = (int)(w-h*ratio)/2;
                    x = Math.max(x, 0);
                    return Bitmap.createBitmap(bitmap,x, 0, (int)(h*ratio),h, null, false);
                }else {
                    int y = (int)(h-w/ratio)/2;
                    y = Math.max(y, 0);
                    return Bitmap.createBitmap(bitmap, 0,y,w,(int)(w/ratio), null, false);
                }
            }
        }
        return bitmap;
    }
}
