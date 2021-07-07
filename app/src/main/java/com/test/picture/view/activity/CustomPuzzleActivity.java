package com.test.picture.view.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.huantansheng.easyphotos.EasyPhotos;
import com.huantansheng.easyphotos.models.album.entity.Photo;
import com.test.picture.R;
import com.test.picture.tool.PhotoTool;
import com.test.picture.utils.FileUtils;
import com.test.picture.task.StickerTask;
import com.xinlan.imageeditlibrary.editimage.view.StickerItem;
import com.xinlan.imageeditlibrary.editimage.view.StickerView;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import static com.test.picture.view.MainActivity.REQUEST_SINGLE_CODE;
import static com.xinlan.imageeditlibrary.editimage.utils.BitmapUtils.getViewBitmap;

public class CustomPuzzleActivity extends BaseActivity {

    private TextView customPuzzleCancel;
    private TextView customPuzzleConfirm;
    private Button selectAlbumSingle;
    private StickerView stickerPanel;
    private ArrayList<String> pathList;
    private Handler handler = new Handler();
    private SaveStickersTask mSaveTask;
    private ImageView ivPanel;

    private Bitmap bitmap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    protected int getLayoutId() {
        return R.layout.activity_custom_puzzle;
    }

    @Override
    protected void initView() {
        customPuzzleCancel = findViewById(R.id.custom_puzzle_cancel);
        customPuzzleConfirm = findViewById(R.id.custom_puzzle_confirm);
        selectAlbumSingle = findViewById(R.id.select_album_single);
        stickerPanel = findViewById(R.id.sticker_panel);
        ivPanel = findViewById(R.id.iv_panel);


        //等待StickerView初始化完成再添加bitmap;
        stickerPanel.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        Intent intent = getIntent();
                        pathList = intent.getStringArrayListExtra("pathList");

                        for (String path : pathList) {
                            //等待0.2s
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    addBitImage(path);
                                }
                            }, 200);
                        }

                        stickerPanel.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    }
                });

    }

    @Override
    protected void initListeners() {
        customPuzzleCancel.setOnClickListener(this);
        customPuzzleConfirm.setOnClickListener(this);
        selectAlbumSingle.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.custom_puzzle_cancel: {
                finish();
                break;
            }
            case R.id.custom_puzzle_confirm: {
                applyStickers();
                break;
            }
            case R.id.select_album_single: {
                selectFromAlbumSingle();
                break;
            }
        }

    }

    /**
     * 从图库中选择图片 单选
     */
    private void selectFromAlbumSingle() {
        PhotoTool.getInstance().openAlbumForSingle(this, REQUEST_SINGLE_CODE);
    }

    private void addBitImage(String path) {
        Bitmap bitmap = BitmapFactory.decodeFile(path);
        stickerPanel.addBitImage(bitmap);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_SINGLE_CODE: {
                    //获取单选的返回数据
                    ArrayList<Photo> resultPhotos = data.getParcelableArrayListExtra(EasyPhotos.RESULT_PHOTOS);
                    String path = resultPhotos.get(0).path;
                    addBitImage(path);
                    break;
                }
            }
        }
    }

    /**
     * 保存贴图任务
     *
     * @author panyi
     */
    private final class SaveStickersTask extends StickerTask {

        public SaveStickersTask(CustomPuzzleActivity activity) {
            super(activity);
        }

        @Override
        public void handleImage(Canvas canvas, Matrix m) {
            LinkedHashMap<Integer, StickerItem> addItems = stickerPanel.getBank();
            for (Integer id : addItems.keySet()) {
                StickerItem item = addItems.get(id);
                item.matrix.postConcat(m);// 乘以底部图片变化矩阵
                canvas.drawBitmap(item.bitmap, item.matrix, null);
            }// end for
        }

        @Override
        public void onPostResult(Bitmap result) {
            bitmap = result;

            //存bitmap为图片
            String filename = File.separator + System.currentTimeMillis() + ".jpeg";
            String path = FileUtils.saveBitmap(filename, bitmap);
            EasyPhotos.notifyMedia(CustomPuzzleActivity.this, path);

            //显示结果
            Bundle bundle = new Bundle();
            bundle.putString("path", path);
            startActivity(ShowResultActivity.class, bundle);

        }
    }// end inner class

    /**
     * 保存贴图层 合成一张图片
     */
    public void applyStickers() {
        // System.out.println("保存 合成图片");
        if (mSaveTask != null) {
            mSaveTask.cancel(true);
        }
        mSaveTask = new SaveStickersTask(this);
        bitmap = getViewBitmap(ivPanel);
        mSaveTask.execute(bitmap);
    }
}