package com.xinlan.imageeditlibrary.editimage.fragment;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.TextView;

import com.xinlan.imageeditlibrary.R;
import com.xinlan.imageeditlibrary.editimage.EditImageActivity;
import com.xinlan.imageeditlibrary.editimage.ModuleConfig;
import com.xinlan.imageeditlibrary.editimage.adapter.ColorListAdapter;
import com.xinlan.imageeditlibrary.editimage.task.StickerTask;
import com.xinlan.imageeditlibrary.editimage.ui.ColorPicker;
import com.xinlan.imageeditlibrary.editimage.view.CustomPaintView;
import com.xinlan.imageeditlibrary.editimage.view.PaintModeView;


/**
 * 用户自由绘制模式 操作面板
 * 可设置画笔粗细 画笔颜色
 * custom draw mode panel
 *
 * @author panyi
 */
public class PaintFragment extends BaseEditFragment implements View.OnClickListener, ColorListAdapter.IColorListAction {
    public static final int INDEX = ModuleConfig.INDEX_PAINT;
    public static final String TAG = PaintFragment.class.getName();

    private View mainView;
    private View backToMenu;// 返回主菜单
    private PaintModeView mPaintModeView;
    private RecyclerView mColorListView;//颜色列表View
    private ColorListAdapter mColorAdapter;
    private View popView;
    private Bitmap bitmap;


    private CustomPaintView mPaintView;

    private ColorPicker mColorPicker;//颜色选择器

    private PopupWindow setStokenWidthWindow;
    private SeekBar mStokenWidthSeekBar;
    private TextView mSeekbarValue;

    private ImageView mEraserView;

    public boolean isEraser = false;//是否是擦除模式

    private SaveCustomPaintTask mSavePaintImageTask;
    private FrameLayout frameLayout;
    private LinearLayout paintRedoUodoPanel;
    private FrameLayout redoIV;
    private FrameLayout undoIV;


    public int[] mPaintColors = {R.color.color_list_1,
            R.color.color_list_2,R.color.color_list_3,R.color.color_list_4,R.color.color_list_5,
            R.color.color_list_6,R.color.color_list_7,R.color.color_list_8,R.color.color_list_9,
            R.color.color_list_10,R.color.color_list_11,R.color.color_list_12,R.color.color_list_13,
            R.color.color_list_14,R.color.color_list_15};

    public static PaintFragment newInstance() {
        PaintFragment fragment = new PaintFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mainView = inflater.inflate(R.layout.fragment_edit_paint, null);
        return mainView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        paintRedoUodoPanel = activity.getPaintRedoUodoPanel();
        redoIV = paintRedoUodoPanel.findViewById(R.id.paint_redo_btn);
        undoIV = paintRedoUodoPanel.findViewById(R.id.paint_undo_btn);
        mPaintView = (CustomPaintView)getActivity().findViewById(R.id.custom_paint_view);
        backToMenu = mainView.findViewById(R.id.back_to_main);
        mPaintModeView = (PaintModeView) mainView.findViewById(R.id.paint_thumb);
        mColorListView = (RecyclerView) mainView.findViewById(R.id.paint_color_list);
        mEraserView = (ImageView) mainView.findViewById(R.id.paint_eraser);

        mPaintView.redo(redoIV, new CustomPaintView.onRedoListener() {
            @Override
            public void onRedo(Bitmap bitmap) {

            }
        });
        mPaintView.undo(undoIV, new CustomPaintView.onUndoListener() {
            @Override
            public void onUndo(Bitmap bitmap) {

            }
        });
        backToMenu.setOnClickListener(this);// 返回主菜单


        mColorPicker = new ColorPicker(getActivity(), 255, 0, 0);
        initColorListView();
        mPaintModeView.setOnClickListener(this);

        initStokeWidthPopWindow();

        mEraserView.setOnClickListener(this);
        updateEraserView();
    }

    /**
     * 初始化颜色列表
     */
    private void initColorListView() {

        mColorListView.setHasFixedSize(false);

        LinearLayoutManager stickerListLayoutManager = new LinearLayoutManager(activity);
        stickerListLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mColorListView.setLayoutManager(stickerListLayoutManager);

        mColorAdapter = new ColorListAdapter(getContext(), mPaintColors, this);
        mColorListView.setAdapter(mColorAdapter);


    }

    @Override
    public void onClick(View v) {
        if (v == backToMenu) {//back button click
            backToMain();
        } else if (v == mPaintModeView) {//设置绘制画笔粗细
            setStokeWidth();
        } else if (v == mEraserView) {
            toggleEraserView();
        }
        //end if
    }

    /**
     * 返回主菜单
     */
    public void backToMain() {
        setStokenWidthWindow.dismiss();
        activity.mode = EditImageActivity.MODE_NONE;
        activity.bottomGallery.setCurrentItem(MainMenuFragment.INDEX);
        activity.mainImage.setVisibility(View.VISIBLE);
        activity.bannerFlipper.showPrevious();
        this.mPaintView.setVisibility(View.GONE);
        activity.setRedoUndoPanelVisibility(View.VISIBLE);
        activity.setPaintRedoUndoPanelVisibility(View.GONE);
    }

    public void onShow() {
        activity.mode = EditImageActivity.MODE_PAINT;
        activity.mainImage.setImageBitmap(activity.getMainBit());
        activity.bannerFlipper.showNext();
        this.mPaintView.setVisibility(View.VISIBLE);
        activity.setRedoUndoPanelVisibility(View.INVISIBLE);
        activity.setPaintRedoUndoPanelVisibility(View.VISIBLE);
    }

    @Override
    public void onColorSelected(int position, int color) {
        setPaintColor(getResources().getColor(color));
        setStokeWidth();
    }

    @Override
    public void onMoreSelected(int position) {
        mColorPicker.show();
        Button okColor = (Button) mColorPicker.findViewById(R.id.okColorButton);
        okColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setPaintColor(mColorPicker.getColor());
                mColorPicker.dismiss();
            }
        });
    }

    /**
     * 设置画笔颜色
     *
     * @param paintColor
     */
    protected void setPaintColor(final int paintColor) {
        mPaintModeView.setPaintStrokeColor(paintColor);
        updatePaintView();
    }

    private void updatePaintView() {
        isEraser = false;
        updateEraserView();

        this.mPaintView.setColor(mPaintModeView.getStokenColor());
        this.mPaintView.setWidth(mPaintModeView.getStokenWidth());
    }

    /**
     * 设置画笔粗细
     * show popwidnow to set paint width
     */
    protected void setStokeWidth() {
        if (popView.getMeasuredHeight() == 0) {
            popView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        }

        mStokenWidthSeekBar.setMax(mPaintModeView.getMeasuredHeight());

        mStokenWidthSeekBar.setProgress((int) mPaintModeView.getStokenWidth());

        mStokenWidthSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mPaintModeView.setPaintStrokeWidth(progress);
                updatePaintView();
                mSeekbarValue.setText(String.valueOf(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        int[] locations = new int[2];
        activity.bottomGallery.getLocationOnScreen(locations);
        setStokenWidthWindow.showAtLocation(activity.bottomGallery,
                Gravity.NO_GRAVITY, 0, locations[1] - popView.getMeasuredHeight());
    }

    private void initStokeWidthPopWindow() {
        popView = LayoutInflater.from(activity).
                inflate(R.layout.view_set_stoke_width, null);
        setStokenWidthWindow = new PopupWindow(popView, ViewGroup.LayoutParams.MATCH_PARENT
                , ViewGroup.LayoutParams.WRAP_CONTENT);

        mStokenWidthSeekBar = (SeekBar) popView.findViewById(R.id.stoke_width_seekbar);
        mSeekbarValue = popView.findViewById(R.id.seekbar_value);

        setStokenWidthWindow.setFocusable(false);
        setStokenWidthWindow.setOutsideTouchable(false);
        setStokenWidthWindow.setBackgroundDrawable(new BitmapDrawable());
        setStokenWidthWindow.setAnimationStyle(R.style.popwin_anim_style);


        mPaintModeView.setPaintStrokeColor(Color.RED);
        mPaintModeView.setPaintStrokeWidth(20);

        updatePaintView();
    }

    private void toggleEraserView() {
        isEraser = !isEraser;
        updateEraserView();
    }

    private void updateEraserView() {
        mEraserView.setImageResource(isEraser ? R.drawable.ico_paint_eraser_selected : R.drawable.ico_paint_eraser);
        mPaintView.setEraser(isEraser);
    }

    /**
     * 保存涂鸦
     */
    public void savePaintImage() {
        if (mSavePaintImageTask != null && !mSavePaintImageTask.isCancelled()) {
            mSavePaintImageTask.cancel(true);
        }

        mSavePaintImageTask = new SaveCustomPaintTask(activity);
        mSavePaintImageTask.execute(activity.getMainBit());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mSavePaintImageTask != null && !mSavePaintImageTask.isCancelled()) {
            mSavePaintImageTask.cancel(true);
        }
    }

    /**
     * 文字合成任务
     * 合成最终图片
     */
    private final class SaveCustomPaintTask extends StickerTask {

        public SaveCustomPaintTask(EditImageActivity activity) {
            super(activity);
        }

        @Override
        public void handleImage(Canvas canvas, Matrix m) {
            float[] f = new float[9];
            m.getValues(f);
            int dx = (int) f[Matrix.MTRANS_X];
            int dy = (int) f[Matrix.MTRANS_Y];
            float scale_x = f[Matrix.MSCALE_X];
            float scale_y = f[Matrix.MSCALE_Y];
            canvas.save();
            canvas.translate(dx, dy);
            canvas.scale(scale_x, scale_y);

            if (mPaintView.getPaintBit() != null) {
                canvas.drawBitmap(mPaintView.getPaintBit(), 0, 0, null);
            }
            canvas.restore();
        }

        @Override
        public void onPostResult(Bitmap result) {
            mPaintView.reset();
            activity.changeMainBitmap(result , true);
            backToMain();
        }
    }//end inner class

}// end class
