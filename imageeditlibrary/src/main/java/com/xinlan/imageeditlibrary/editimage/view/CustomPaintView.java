package com.xinlan.imageeditlibrary.editimage.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.os.Build;
import androidx.annotation.RequiresApi;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.xinlan.imageeditlibrary.editimage.model.DrawPath;

import java.util.ArrayList;
import java.util.List;

import static com.xinlan.imageeditlibrary.editimage.fragment.PaintFragment.TAG;

/**
 * Created by panyi on 17/2/11.
 */

public class CustomPaintView extends androidx.appcompat.widget.AppCompatImageView {
    private Paint mPaint;
    public Bitmap mDrawBit;
    private Paint mEraserPaint;

    private Canvas mPaintCanvas = null;

    private float last_x;
    private float last_y;
    private boolean eraser;

    private int mColor;


    private List<List<DrawPath>> pathListArrayList = new ArrayList<>();
    private List<DrawPath> drawPathList = new ArrayList<>();
    private int currentStep = 0;
    private FrameLayout redoIV;
    private FrameLayout undoIV;


    public CustomPaintView(Context context) {
        super(context);
        init(context);
    }

    public CustomPaintView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CustomPaintView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

//    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
//    public CustomPaintView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
//        super(context, attrs, defStyleAttr, defStyleRes);
//        init(context);
//    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (mDrawBit == null) {
            generatorBit();
        }
    }

    private void generatorBit() {
        mDrawBit = Bitmap.createBitmap(getMeasuredWidth(), getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        mPaintCanvas = new Canvas(mDrawBit);
    }

    private void init(Context context) {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);

        mPaint.setColor(Color.RED);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);


        mEraserPaint = new Paint();
        mEraserPaint.setAlpha(0);
        mEraserPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        mEraserPaint.setAntiAlias(true);
        mEraserPaint.setDither(true);
        mEraserPaint.setStyle(Paint.Style.STROKE);
        mEraserPaint.setStrokeJoin(Paint.Join.ROUND);
        mEraserPaint.setStrokeCap(Paint.Cap.ROUND);
        mEraserPaint.setStrokeWidth(40);
    }

    public void setColor(int color) {
        this.mColor = color;
        this.mPaint.setColor(mColor);
    }

    public void setWidth(float width) {
        this.mPaint.setStrokeWidth(width);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mDrawBit != null) {
            canvas.drawBitmap(mDrawBit, 0, 0, null);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean ret = super.onTouchEvent(event);
        float x = event.getX();
        float y = event.getY();


        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                ret = true;
                last_x = x;
                last_y = y;
                break;
            case MotionEvent.ACTION_MOVE:
                ret = true;
                Paint paint;
                if(eraser){
                    paint = mEraserPaint;
                }else {
                    paint = mPaint;
                }
                mPaintCanvas.drawLine(last_x, last_y, x, y, paint);
                DrawPath drawPath = new DrawPath(last_x, last_y, x, y,paint,paint.getColor(),paint.getStrokeWidth());
                Log.d("paintvdd", String.valueOf(drawPath.getPaint().getColor()));
                drawPathList.add(drawPath);
                last_x = x;
                last_y = y;
                this.postInvalidate();
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                ret = false;
                List<DrawPath> newList = new ArrayList<>(drawPathList);
                if (currentStep == pathListArrayList.size()){
                    pathListArrayList.add(newList);
                }else {
                    List<List<DrawPath>> list = new ArrayList<>(pathListArrayList);
                    pathListArrayList.clear();
                    for(int i= 0;i<currentStep;i++){
                        pathListArrayList.add(list.get(i));
                    }
                    pathListArrayList.add(newList);
                }
                drawPathList.clear();
                currentStep+=1;
                redoIV.setVisibility(VISIBLE);
                undoIV.setVisibility(GONE);
                break;
        }
        return ret;
    }

    /**
     * 上一步
     */
    public void redo(FrameLayout redoImageView, onRedoListener onRedoListener){
        this.redoIV = redoImageView;
        this.onRedoListener = onRedoListener;
        redoIV.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentStep!=0){
                    currentStep = currentStep-1;

                    //清空画布
                    mDrawBit = Bitmap.createBitmap(getMeasuredWidth(), getMeasuredHeight(), Bitmap.Config.ARGB_8888);
                    mPaintCanvas = new Canvas(mDrawBit);
                    invalidate();
                    for (int i = 0;i<currentStep;i++){
                        Log.d(TAG, String.valueOf(pathListArrayList.get(i).size()));
                        Log.d("paintvdd", String.valueOf(pathListArrayList.get(i).get(0).getPaint().getColor()));
                        for(int j = 0;j<pathListArrayList.get(i).size();j++){
                            DrawPath drawPath = pathListArrayList.get(i).get(j);
                            float last_x = drawPath.getLast_x();
                            float last_y = drawPath.getLast_y();
                            float x = drawPath.getX();
                            float y = drawPath.getY();
                            Paint paint = drawPath.getPaint();
                            int color = drawPath.getColor();
                            float width = drawPath.getWidth();
                            paint.setColor(color);
                            paint.setStrokeWidth(width);
//                            Log.d("paintvdd", String.valueOf(paint.getColor()));
                            mPaintCanvas.drawLine(last_x,last_y,x,y,paint);
                            invalidate();
                        }
                    }



                    onRedoListener.onRedo(mDrawBit);
                    if(currentStep == 0){
                        redoIV.setVisibility(GONE);
                    }else {
                        redoIV.setVisibility(VISIBLE);
                    }
                    undoIV.setVisibility(VISIBLE);
                }
            }
        });
    }

    /**
     * 下一步
     */
    public void undo(FrameLayout undoImageView,onUndoListener onUndoListener){
        this.undoIV = undoImageView;
        this.onUndoListener = onUndoListener;
        undoIV.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentStep!= pathListArrayList.size()){
                    currentStep = currentStep+1;

                    //清空画布
                    mDrawBit = Bitmap.createBitmap(getMeasuredWidth(), getMeasuredHeight(), Bitmap.Config.ARGB_8888);
                    mPaintCanvas = new Canvas(mDrawBit);
                    invalidate();

                    for (int i = 0;i<currentStep;i++){
                        for(int j = 0;j<pathListArrayList.get(i).size();j++ ){
                            DrawPath drawPath = pathListArrayList.get(i).get(j);
                            float last_x = drawPath.getLast_x();
                            float last_y = drawPath.getLast_y();
                            float x = drawPath.getX();
                            float y = drawPath.getY();
                            Paint paint = drawPath.getPaint();
                            int color = drawPath.getColor();
                            float width = drawPath.getWidth();
                            paint.setColor(color);
                            paint.setStrokeWidth(width);
                            mPaintCanvas.drawLine(last_x,last_y,x,y,paint);
                            invalidate();
                        }
                    }
                    onUndoListener.onUndo(mDrawBit);
                    if(currentStep == pathListArrayList.size()){
                        undoIV.setVisibility(GONE);
                    }else {
                        undoIV.setVisibility(VISIBLE);
                    }
                    redoIV.setVisibility(VISIBLE);
                }
            }
        });
    }


    public interface onRedoListener{
        void onRedo(Bitmap bitmap);
    }
    private onRedoListener onRedoListener;

    public interface onUndoListener{
        void onUndo(Bitmap bitmap);
    }
    private onUndoListener onUndoListener;

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mDrawBit != null && !mDrawBit.isRecycled()) {
            mDrawBit.recycle();
        }
    }

    public void setEraser(boolean eraser) {
        this.eraser = eraser;
        mPaint.setColor(eraser ? Color.TRANSPARENT : mColor);
    }

    public Bitmap getPaintBit() {
        return mDrawBit;
    }

    public void reset() {
        if (mDrawBit != null && !mDrawBit.isRecycled()) {
            mDrawBit.recycle();
            undoIV.setVisibility(GONE);
            redoIV.setVisibility(GONE);
            pathListArrayList.clear();
            drawPathList.clear();
            currentStep = 0;
        }
        generatorBit();
    }
}//end class
