package lifeexperience.tool.beautycamera.view.activity;

import android.content.DialogInterface;
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

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.huantansheng.easyphotos.EasyPhotos;
import com.huantansheng.easyphotos.models.album.entity.Photo;
import lifeexperience.tool.beautycamera.R;
import lifeexperience.tool.beautycamera.adapter.ColorListAdapter;
import lifeexperience.tool.beautycamera.tool.PhotoTool;
import lifeexperience.tool.beautycamera.utils.FileUtils;
import lifeexperience.tool.beautycamera.task.StickerTask;

import com.xinlan.imageeditlibrary.editimage.ui.ColorPicker;
import com.xinlan.imageeditlibrary.editimage.view.StickerItem;
import com.xinlan.imageeditlibrary.editimage.view.StickerView;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import lifeexperience.tool.beautycamera.view.MainActivity;

import static com.xinlan.imageeditlibrary.editimage.utils.BitmapUtils.getViewBitmap;

public class CustomPuzzleActivity extends BaseActivity implements  ColorListAdapter.IColorListAction {

    private ImageView customPuzzleCancel;
    private TextView customPuzzleConfirm;
    private ImageView selectAlbumSingle;
    private StickerView stickerPanel;
    private ArrayList<String> pathList;
    private Handler handler = new Handler();
    private SaveStickersTask mSaveTask;
    private ImageView ivPanel;

    private Bitmap bitmap;

    public int[] mPaintColors = {R.color.color_list_1,
            R.color.color_list_2,R.color.color_list_3,R.color.color_list_4,R.color.color_list_5,
            R.color.color_list_6,R.color.color_list_7,R.color.color_list_8,R.color.color_list_9,
            R.color.color_list_10,R.color.color_list_11,R.color.color_list_12,R.color.color_list_13,
            R.color.color_list_14,R.color.color_list_15};
    private RecyclerView mColorListView;//颜色列表View
    private ColorListAdapter mColorAdapter;
    private ColorPicker mColorPicker;//颜色选择器

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
        mColorListView = (RecyclerView) findViewById(R.id.background_color_list);
        stickerPanel.setLayout(R.layout.activity_custom_puzzle);
        mColorPicker = new ColorPicker(this, 255, 0, 0);
        initColorListView();

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

    /**
     * 初始化颜色列表
     */
    private void initColorListView() {

        mColorListView.setHasFixedSize(false);

        LinearLayoutManager stickerListLayoutManager = new LinearLayoutManager(this);
        stickerListLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mColorListView.setLayoutManager(stickerListLayoutManager);

        mColorAdapter = new ColorListAdapter(this, mPaintColors, this);
        mColorListView.setAdapter(mColorAdapter);
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
                onBackPressed();
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

    @Override
    public void onBackPressed() {
        LinkedHashMap<Integer, StickerItem> stickerItemLinkedHashMap = stickerPanel.getBank();
        if (stickerItemLinkedHashMap.size() == 0) {
            finish();
        } else {//图片还未被保存    弹出提示框确认
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setMessage(com.xinlan.imageeditlibrary.R.string.exit_without_save)
                    .setCancelable(false).setPositiveButton(com.xinlan.imageeditlibrary.R.string.yes, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    applyStickers();
                }
            }).setNegativeButton(com.xinlan.imageeditlibrary.R.string.no, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    finish();
                }
            });

            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }
    }

    /**
     * 从图库中选择图片 单选
     */
    private void selectFromAlbumSingle() {
        ArrayList<Photo> photos = new ArrayList<>();
        PhotoTool.getInstance().openAlbumForSingle(this, MainActivity.REQUEST_SINGLE_CODE,photos);
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
                case MainActivity.REQUEST_SINGLE_CODE: {
                    //获取单选的返回数据
                    ArrayList<Photo> resultPhotos = data.getParcelableArrayListExtra(EasyPhotos.RESULT_PHOTOS);
                    String path = resultPhotos.get(0).path;
                    addBitImage(path);
                    break;
                }
            }
        }
    }

    @Override
    public void onColorSelected(int position, int color) {
        setBackgroundColor(color);
    }

    @Override
    public void onMoreSelected(int position) {
        mColorPicker.show();
        Button okColor = (Button) mColorPicker.findViewById(com.xinlan.imageeditlibrary.R.id.okColorButton);
        okColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ivPanel.setBackgroundColor(mColorPicker.getColor());
                mColorPicker.dismiss();
            }
        });
    }

    /**
     * 设置背景颜色
     *
     * @param paintColor
     */
    protected void setBackgroundColor(final int paintColor) {
        ivPanel.setBackgroundColor(getResources().getColor(paintColor));
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
            finish();

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