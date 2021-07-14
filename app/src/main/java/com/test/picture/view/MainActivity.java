package com.test.picture.view;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.huantansheng.easyphotos.EasyPhotos;
import com.huantansheng.easyphotos.models.album.entity.Photo;
import com.test.picture.tool.StatisticsManager;
import com.test.picture.utils.SharedPreferencesUtil;
import com.test.picture.view.activity.CameraActivity;
import com.test.picture.view.activity.CustomPuzzleActivity;
import com.test.picture.view.activity.ShowProductActivity;
import com.xinlan.imageeditlibrary.editimage.EditImageActivity;
import com.test.picture.R;
import com.test.picture.tool.PhotoTool;
import com.test.picture.utils.FileUtils;
import com.test.picture.view.activity.BaseActivity;
import com.test.picture.view.activity.ShowResultActivity;


import java.io.File;
import java.util.ArrayList;

import static com.test.picture.utils.FirebaseAnalyticsEventUtil.ALBUM_SINGLE;
import static com.test.picture.utils.FirebaseAnalyticsEventUtil.CUSTOM_PUZZLE;
import static com.test.picture.utils.FirebaseAnalyticsEventUtil.DIALOG_SHOW;
import static com.test.picture.utils.FirebaseAnalyticsEventUtil.EDIT_PHOTO;
import static com.test.picture.utils.FirebaseAnalyticsEventUtil.EXIT_SHOW;
import static com.test.picture.utils.FirebaseAnalyticsEventUtil.PUZZLE;
import static com.test.picture.utils.FirebaseAnalyticsEventUtil.REMOVE_AD_DIALOG_SHOW;
import static com.test.picture.utils.FirebaseAnalyticsEventUtil.SHOW_PRODUCT;
import static com.test.picture.utils.FirebaseAnalyticsEventUtil.TAKE_PHOTO;
import static com.test.picture.utils.SharedPreferencesUtil.GOLD_NUM;
import static com.test.picture.utils.SharedPreferencesUtil.IS_REMOVE_AD_SUCCESS;


public class MainActivity extends BaseActivity {


    /**
     * 相机权限请求标识
     */
    public static final int REQUEST_CAMERA_CODE = 0x100;//拍照

    public static final int REQUEST_PUZZLE_CODE = 4;  //拼图

    public static final int REQUEST_SINGLE_CODE = 5; //图库单选

    public static final int REQUEST_MULTIPLE_CODE_FOR_PUZZLE = 6; //多选

    public static final int REQUEST_CUSTOM_PUZZLE_CODE = 7; //自定义拼图

    public static final int REQUEST_EDITIMAGE_CODE = 8;//编辑图片

    public static final int ACTION_REQUEST_EDITIMAGE = 9;

    public static int num = 0;//记录次数

    private MainActivity context;
    private Button openAblumForMultiple;//打开图库多选
    private Button openAblumForSingle;//打开图库单选
    private Button editImage;//编辑照片
    private Button mTakenPhoto;//拍摄照片用于编辑
    private Button customPuzzle;//自定义拼图
    private Button showProduct;//自定义拼图
    private Button btnRemoveAd;//去除广告
    private int imageWidth, imageHeight;//
    private ArrayList<Photo> selectedPhotoList = new ArrayList<>();
    private RewardedAd mRewardedAd;

    private static final String AD_UNIT_ID = "ca-app-pub-3940256099942544/5224354917";
    private boolean isLoading;
    private boolean isShowAd = false;
    private AdView mAdView;
    private InterstitialAd mInterstitialAd;
    private AlertDialog dialog;
    private int getGlod;
    private RelativeLayout remove_rllt;
    private RelativeLayout look_movie_rllt;
    private ImageView dialogDismiss;
    private boolean isOnClickLookAd = false;
    private View.OnClickListener dialogListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView() {
        //初始化广告
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });


        //横幅广告
        mAdView = findViewById(R.id.ad_view);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        context = this;
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        imageWidth = metrics.widthPixels;
        imageHeight = metrics.heightPixels;

        openAblumForMultiple = findViewById(R.id.select_album_multiple_for_puzzle);
        openAblumForSingle = findViewById(R.id.select_album_single);
        editImage = findViewById(R.id.edit_image);
        mTakenPhoto = findViewById(R.id.take_photo);
        customPuzzle = findViewById(R.id.custom_puzzle);
        showProduct = findViewById(R.id.btn_show_product);
        btnRemoveAd = findViewById(R.id.btn_remove_ad);

    }

    @Override
    protected void initListeners() {
        openAblumForMultiple.setOnClickListener(this);
        openAblumForSingle.setOnClickListener(this);
        editImage.setOnClickListener(this);
        mTakenPhoto.setOnClickListener(this);
        customPuzzle.setOnClickListener(this);
        showProduct.setOnClickListener(this);
        btnRemoveAd.setOnClickListener(this);

        dialogListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()){
                    case R.id.dialog_dismiss:{
                        dialog.dismiss();
                        break;
                    }
                    case R.id.remove_rllt:{
                        if (getGlod < 11) {
                            Toast.makeText(MainActivity.this, "Insufficient gold coins", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(MainActivity.this, "Ads removed successfully", Toast.LENGTH_SHORT).show();
                            SharedPreferencesUtil.putBoolean(MainActivity.this,IS_REMOVE_AD_SUCCESS,true);
                        }
                        dialog.dismiss();
                        break;
                    }
                    case R.id.look_movie_rllt:{
                        isOnClickLookAd = true;
                        loadRewardedAd();
                        dialog.dismiss();
                        break;
                    }
                }
            }
        };
    }

    /**
     * 去除广告提示Dialog
     */
    private void removeDialog() {

        //拿去本地存在金币个数
        getGlod = SharedPreferencesUtil.getInt(this, GOLD_NUM, 0);
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, R.style.Transparent);
        dialog = builder.create();
        dialog.show();
        dialog.setCanceledOnTouchOutside(false);
        Window window = dialog.getWindow();
        window.setContentView(R.layout.remove_adview_show);

        TextView glod_many_tv = dialog.findViewById(R.id.glod_many_tv);
        dialogDismiss = dialog.findViewById(R.id.dialog_dismiss);
        remove_rllt = dialog.findViewById(R.id.remove_rllt);
        look_movie_rllt = dialog.findViewById(R.id.look_movie_rllt);

        dialogDismiss.setOnClickListener(dialogListener);
        look_movie_rllt.setOnClickListener(dialogListener);
        remove_rllt.setOnClickListener(dialogListener);

        glod_many_tv.setText(getGlod + "");

        return;
    }


    /**
     * 加载激励式广告
     */
    private void loadRewardedAd() {
        if (mRewardedAd == null) {
            isLoading = true;
            AdRequest adRequest = new AdRequest.Builder().build();
            RewardedAd.load(
                    this,
                    AD_UNIT_ID,
                    adRequest,
                    new RewardedAdLoadCallback() {
                        @Override
                        public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                            // Handle the error.
                            mRewardedAd = null;
                            isLoading = false;
                            isOnClickLookAd = false;
                        }

                        @Override
                        public void onAdLoaded(@NonNull RewardedAd rewardedAd) {
                            mRewardedAd = rewardedAd;
                            isLoading = false;
                            showRewardedVideo();
                        }
                    });
        }
    }

    /**
     * 进行显示 广告视频
     */
    private void showRewardedVideo() {

        if (mRewardedAd == null) {
            Log.d("TAG", "The rewarded ad wasn't ready yet.");
            return;
        }

        mRewardedAd.setFullScreenContentCallback(
                new FullScreenContentCallback() {
                    @Override
                    public void onAdShowedFullScreenContent() {
                        // Called when ad is shown.

                    }

                    @Override
                    public void onAdFailedToShowFullScreenContent(AdError adError) {
                        mRewardedAd = null;
                    }

                    @Override
                    public void onAdDismissedFullScreenContent() {
                        mRewardedAd = null;

                    }
                });

        mRewardedAd.show(
                this,
                new OnUserEarnedRewardListener() {
                    @Override
                    public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                        // Handle the reward.

                        // 广告看完  这里进行添加金币
                        if (isOnClickLookAd){
                            int goldNum = SharedPreferencesUtil.getInt(MainActivity.this,GOLD_NUM,0);
                            SharedPreferencesUtil.putInt(MainActivity.this,GOLD_NUM,goldNum+=1);
                            isOnClickLookAd = false;
                        }
                        mRewardedAd = null;
                        return;
                    }
                });
    }


    @Override
    public void onClick(View v) {
        isShowAd = false;
        num++;
        Boolean isRemoveAdSuccess = SharedPreferencesUtil.getBoolean(MainActivity.this,IS_REMOVE_AD_SUCCESS,false);
        if (num % 4 == 0 && !isRemoveAdSuccess) {
            loadRewardedAd();
            isShowAd = true;
        }


        switch (v.getId()) {
            case R.id.take_photo: {
                takePhotoClick();
                if (isShowAd) {
                    StatisticsManager.uploadAnalyticsEvents(this, TAKE_PHOTO);
                }
                break;
            }
            case R.id.edit_image: {
                editImageClick();
                if (isShowAd) {
                    StatisticsManager.uploadAnalyticsEvents(this, EDIT_PHOTO);
                }
                break;
            }

            case R.id.select_album_multiple_for_puzzle: {
                selectFromAlbumMultipleForPuzzle();
                if (isShowAd) {
                    StatisticsManager.uploadAnalyticsEvents(this, PUZZLE);
                }
                break;
            }
            case R.id.select_album_single: {
                selectFromAlbumSingle();
                if (isShowAd) {
                    StatisticsManager.uploadAnalyticsEvents(this, ALBUM_SINGLE);
                }
                break;
            }
            case R.id.custom_puzzle: {
                startCustomPuzzle();
                if (isShowAd) {
                    StatisticsManager.uploadAnalyticsEvents(this, CUSTOM_PUZZLE);
                }
                break;
            }
            case R.id.btn_show_product: {
                startActivity(ShowProductActivity.class);
                if (isShowAd) {
                    StatisticsManager.uploadAnalyticsEvents(this, SHOW_PRODUCT);
                }
                break;
            }
            case R.id.btn_remove_ad: {
                removeDialog();
                if (isShowAd) {
                    StatisticsManager.uploadAnalyticsEvents(this, REMOVE_AD_DIALOG_SHOW);
                }
                break;
            }
        }//end switch
    }

    /**
     * 修改图片
     */
    private void editImageClick() {
        PhotoTool.getInstance().openAlbumForSingle(this, ACTION_REQUEST_EDITIMAGE);
    }


    /**
     * 拍照
     */
    private void takePhotoClick() {
        checkPermissions();
    }

    /**
     * 从图库中选择图片 多选
     */
    private void selectFromAlbumMultipleForPuzzle() {
        PhotoTool.getInstance().openAlbumForMultiple(this, 9, selectedPhotoList, REQUEST_MULTIPLE_CODE_FOR_PUZZLE);
    }

    /**
     * 从图库中选择图片 单选
     */
    private void selectFromAlbumSingle() {
        PhotoTool.getInstance().openAlbumForSingle(this, REQUEST_SINGLE_CODE);
    }

    /**
     * 自定义拼图
     */
    private void startCustomPuzzle() {
        PhotoTool.getInstance().openAlbumForMultiple(this, 9, selectedPhotoList, REQUEST_CUSTOM_PUZZLE_CODE);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_SINGLE_CODE: {
                    //获取单选的返回数据
                    ArrayList<Photo> resultPhotos = data.getParcelableArrayListExtra(EasyPhotos.RESULT_PHOTOS);
                    showResult(resultPhotos.get(0).path);
                    break;
                }
                case REQUEST_MULTIPLE_CODE_FOR_PUZZLE: {
                    //获取多选的数据
                    ArrayList<Photo> resultPhotos = data.getParcelableArrayListExtra(EasyPhotos.RESULT_PHOTOS);
                    selectedPhotoList.clear();
                    selectedPhotoList.addAll(resultPhotos);


                    if (resultPhotos.size() == 1) {
                        //如果图片数量为1，则多添加一遍。
                        selectedPhotoList.addAll(resultPhotos);
                    }

                    PhotoTool.getInstance().startPuzzleWithPhotos(MainActivity.this, selectedPhotoList, REQUEST_PUZZLE_CODE);

                    break;
                }
                case REQUEST_PUZZLE_CODE: {
                    //拼图返回结果
                    if (null == data) {
                        return;
                    }
                    Photo puzzlePhoto = data.getParcelableExtra(EasyPhotos.RESULT_PHOTOS); //获取拼图文件的Photo对象
                    showResult(puzzlePhoto.path);
                    break;
                }
                case REQUEST_CUSTOM_PUZZLE_CODE: {
                    //获取多选的数据
                    ArrayList<Photo> resultPhotos = data.getParcelableArrayListExtra(EasyPhotos.RESULT_PHOTOS);

                    ArrayList<String> resultPhotosPath = new ArrayList<>();

                    for (Photo photo : resultPhotos) {
                        resultPhotosPath.add(photo.path);
                    }
                    Bundle bundle = new Bundle();
                    bundle.putStringArrayList("pathList", resultPhotosPath);
                    startActivity(CustomPuzzleActivity.class, bundle);
                    break;
                }
                case ACTION_REQUEST_EDITIMAGE: {
                    ArrayList<Photo> resultPhotos = data.getParcelableArrayListExtra(EasyPhotos.RESULT_PHOTOS);
                    File outputFile = FileUtils.getEditFile();
                    EditImageActivity.start(this, resultPhotos.get(0).path, outputFile.getAbsolutePath(), REQUEST_EDITIMAGE_CODE);
                    break;
                }
                case REQUEST_EDITIMAGE_CODE: {
                    handleEditorImage(data);
                    break;
                }
            }
        }
    }


    private void handleEditorImage(Intent data) {
        String newFilePath = data.getStringExtra(EditImageActivity.EXTRA_OUTPUT);
        boolean isImageEdit = data.getBooleanExtra(EditImageActivity.IMAGE_IS_EDIT, false);

        if (!isImageEdit) {//未编辑  还是用原来的图片
            newFilePath = data.getStringExtra(EditImageActivity.FILE_PATH);
        }
        showResult(newFilePath);
    }

    /**
     * 传图片地址用于显示结果
     *
     * @param path
     */
    private void showResult(String path) {
        Bundle bundle = new Bundle();
        bundle.putString("path", path);
        startActivity(ShowResultActivity.class, bundle);
    }

    /**
     * 申请权限
     */
    private void checkPermissions() {
        // 拍照及文件权限申请
        if (ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // 权限还没有授予，需要在这里写申请权限的代码
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CAMERA_CODE);
        } else {
            // 权限已经申请，直接拍照
            startActivity(CameraActivity.class);
        }
    }


    /**
     * 查看申请结果
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (REQUEST_CAMERA_CODE == requestCode) {
            // 权限允许
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startActivity(CameraActivity.class);
            } else {
                // 权限拒绝
                Toast.makeText(getApplicationContext(), R.string.toast_permissions, Toast.LENGTH_SHORT).show();
            }
        }
    }


    /**
     * 插页
     */
    private void interstitialAd() {
        AdRequest adRequest = new AdRequest.Builder().build();
        InterstitialAd.load(this, "ca-app-pub-3940256099942544/1033173712", adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        // The mInterstitialAd reference will be null until
                        // an ad is loaded.
                        mInterstitialAd = interstitialAd;
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error
                        mInterstitialAd = null;
                    }
                });
    }

    /**
     * 显示退出弹窗
     */
    private void showExitAlertDialog(){
        AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
        alertDialog.setMessage(this.getResources().getString(R.string.exit_message));
        alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, this.getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                alertDialog.dismiss();
            }
        });

        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, this.getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                StatisticsManager.uploadAnalyticsEvents(MainActivity.this, EXIT_SHOW);
                onBackPressed();
            }
        });
        alertDialog.show();
    }

    /**
     * 菜单、返回键响应
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            StatisticsManager.uploadAnalyticsEvents(this, DIALOG_SHOW);
            //加载插页式广告
            interstitialAd();
            if (mInterstitialAd!=null){
                mInterstitialAd.show(MainActivity.this);
                mInterstitialAd = null;
            }
            showExitAlertDialog();

        }
        return true;
    }





}