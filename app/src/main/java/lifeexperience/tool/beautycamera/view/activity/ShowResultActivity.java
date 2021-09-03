package lifeexperience.tool.beautycamera.view.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.huantansheng.easyphotos.EasyPhotos;
import com.huantansheng.easyphotos.models.album.entity.Photo;
import com.xinlan.imageeditlibrary.editimage.EditImageActivity;
import lifeexperience.tool.beautycamera.R;
import lifeexperience.tool.beautycamera.tool.PhotoTool;
import lifeexperience.tool.beautycamera.utils.FileUtils;
import lifeexperience.tool.beautycamera.utils.ToastUtil;

import java.io.File;
import java.util.ArrayList;

import static lifeexperience.tool.beautycamera.utils.FileUtils.decodeBitmap;
import static lifeexperience.tool.beautycamera.view.MainActivity.REQUEST_EDITIMAGE_CODE;
import static lifeexperience.tool.beautycamera.view.MainActivity.REQUEST_MULTIPLE_CODE_FOR_PUZZLE;
import static lifeexperience.tool.beautycamera.view.MainActivity.REQUEST_PUZZLE_CODE;

public class ShowResultActivity extends BaseActivity {

    private String TAG = "ShowResultActivity";
    private ImageView ivShowPhoto;
    private ImageView btnBack;
    private ImageView btnPuzzle;
    private ImageView btnEdit;
    private Bitmap bitmap;
    private String path;
    private boolean isAlbum;
    private ArrayList<Photo> selectedPhotoList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    protected int getLayoutId() {
        return R.layout.activity_show_result;
    }


    @Override
    protected void initView() {
        ivShowPhoto = findViewById(R.id.iv_show_photo);
        btnBack = findViewById(R.id.btn_back);
        btnPuzzle = findViewById(R.id.btn_puzzle);
        btnEdit = findViewById(R.id.btn_edit);


        Intent intent = getIntent();
        path = intent.getStringExtra("path");
        isAlbum = intent.getBooleanExtra("isAlbum",false);


        //获取屏幕分辨率
        WindowManager wmanager = this.getWindowManager();
        int width = wmanager.getDefaultDisplay().getWidth();
        int height = wmanager.getDefaultDisplay().getHeight();

        if (path != null) {
            bitmap = decodeBitmap(path,this,width,height);
            Log.d(TAG, "initView: "+path);
        }


        if (isAlbum){
            btnPuzzle.setVisibility(View.VISIBLE);
        }else {
            btnPuzzle.setVisibility(View.GONE);
            ToastUtil.showShortToastCenter(getResources().getString(R.string.image_save_toast));
        }

        ivShowPhoto.setImageBitmap(bitmap);


    }

    @Override
    protected void initListeners() {
        btnBack.setOnClickListener(this);
        btnPuzzle.setOnClickListener(this);
        btnEdit.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btn_back: {
                finish();
                break;
            }
            case R.id.btn_puzzle: {
                StartPuzzle();
                break;
            }
            case R.id.btn_edit: {
                StartEditImage();
                break;
            }
        }
    }


    /**
     * 编辑照片
     */
    private void StartEditImage() {
        File outputFile = FileUtils.getEditFile();
        EditImageActivity.start(this, path, outputFile.getAbsolutePath(), REQUEST_EDITIMAGE_CODE);
    }

    /**
     * 拼图
     */
    private void StartPuzzle() {
        File file = new File(path);
        Photo photo = new Photo(file.getName(), null, path, 0, 0, 0, 0, 0, 0, "JPEG");
        ArrayList<Photo> selectedPhotoList = new ArrayList<>();
        selectedPhotoList.add(photo);
        PhotoTool.getInstance().openAlbumForMultiple(this, 9, REQUEST_MULTIPLE_CODE_FOR_PUZZLE);
    }



    private void handleEditorImage(Intent data) {
        String newFilePath = data.getStringExtra(EditImageActivity.EXTRA_OUTPUT);
        boolean isImageEdit = data.getBooleanExtra(EditImageActivity.IMAGE_IS_EDIT, false);

        if (!isImageEdit) {//未编辑  还是用原来的图片
            newFilePath = data.getStringExtra(EditImageActivity.FILE_PATH);
        }else {
            ToastUtil.showShortToastCenter(getResources().getString(R.string.image_save_toast));
        }
        showResult(newFilePath);
    }

    /**
     * 显示图片
     *
     * @param newFilePath
     */
    private void showResult(String newFilePath) {
        path = newFilePath;
        bitmap = BitmapFactory.decodeFile(newFilePath);
        ivShowPhoto.setImageBitmap(bitmap);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_EDITIMAGE_CODE: {
                    handleEditorImage(data);
                    break;
                }
                case REQUEST_MULTIPLE_CODE_FOR_PUZZLE: {
                    //获取多选的数据
                    ArrayList<Photo> resultPhotos = data.getParcelableArrayListExtra(EasyPhotos.RESULT_PHOTOS);
                    selectedPhotoList.clear();
                    selectedPhotoList.addAll(resultPhotos);

                    if (resultPhotos.size() == 1) {
                        //如果图片数量为1，则提示用户并重新选择。
                        selectedPhotoList.addAll(resultPhotos);
                    }
                    //大于1，则进入拼图界面
                    PhotoTool.getInstance().startPuzzleWithPhotos(this, selectedPhotoList, REQUEST_PUZZLE_CODE);

                    break;
                }
                case REQUEST_PUZZLE_CODE: {
                    //拼图返回结果
                    if (null == data) {
                        return;
                    }
                    Photo puzzlePhoto = data.getParcelableExtra(EasyPhotos.RESULT_PHOTOS); //获取拼图文件的Photo对象
                    showResult(puzzlePhoto.path);
                    ToastUtil.showShortToastCenter(getResources().getString(R.string.image_save_toast));
                    break;
                }
            }
        }
    }
}