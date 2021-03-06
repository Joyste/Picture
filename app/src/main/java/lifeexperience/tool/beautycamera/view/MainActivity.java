package lifeexperience.tool.beautycamera.view;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
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
import lifeexperience.tool.beautycamera.MyApp;
import lifeexperience.tool.beautycamera.adapter.ViewPagerAdapter;
import lifeexperience.tool.beautycamera.tool.StatisticsManager;
import lifeexperience.tool.beautycamera.utils.SharedPreferencesUtil;
import lifeexperience.tool.beautycamera.view.activity.CameraActivity;
import lifeexperience.tool.beautycamera.view.activity.CustomPuzzleActivity;
import lifeexperience.tool.beautycamera.view.activity.SettingActivity;
import lifeexperience.tool.beautycamera.view.activity.ShowProductActivity;
import lifeexperience.tool.beautycamera.view.widget.AutoHeightViewPager;
import com.xinlan.imageeditlibrary.editimage.EditImageActivity;
import lifeexperience.tool.beautycamera.R;
import lifeexperience.tool.beautycamera.tool.PhotoTool;
import lifeexperience.tool.beautycamera.utils.FileUtils;
import lifeexperience.tool.beautycamera.view.activity.BaseActivity;
import lifeexperience.tool.beautycamera.view.activity.ShowResultActivity;


import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static lifeexperience.tool.beautycamera.utils.FirebaseAnalyticsEventUtil.ALBUM_SINGLE;
import static lifeexperience.tool.beautycamera.utils.FirebaseAnalyticsEventUtil.CUSTOM_PUZZLE;
import static lifeexperience.tool.beautycamera.utils.FirebaseAnalyticsEventUtil.DIALOG_SHOW;
import static lifeexperience.tool.beautycamera.utils.FirebaseAnalyticsEventUtil.EDIT_PHOTO;
import static lifeexperience.tool.beautycamera.utils.FirebaseAnalyticsEventUtil.EXIT_SHOW;
import static lifeexperience.tool.beautycamera.utils.FirebaseAnalyticsEventUtil.PUZZLE;
import static lifeexperience.tool.beautycamera.utils.FirebaseAnalyticsEventUtil.SETTING;
import static lifeexperience.tool.beautycamera.utils.FirebaseAnalyticsEventUtil.SHOW_PRODUCT;
import static lifeexperience.tool.beautycamera.utils.FirebaseAnalyticsEventUtil.TAKE_PHOTO;


public class MainActivity extends BaseActivity implements ViewPager.OnPageChangeListener{


    /**
     * ????????????????????????
     */
    public static final int REQUEST_CAMERA_CODE = 0x100;//??????

    public static final int REQUEST_PUZZLE_CODE = 4;  //??????

    public static final int REQUEST_SINGLE_CODE = 5; //????????????

    public static final int REQUEST_MULTIPLE_CODE_FOR_PUZZLE = 6; //??????

    public static final int REQUEST_CUSTOM_PUZZLE_CODE = 7; //???????????????

    public static final int REQUEST_EDITIMAGE_CODE = 8;//????????????

    public static final int ACTION_REQUEST_EDITIMAGE = 9;

    public static int num = 0;//????????????

    private MainActivity context;
    private ImageView openAblumForMultiple;//??????????????????
    private ImageView openAblumForSingle;//??????????????????
    private ImageView editImage;//????????????
    private ImageView mTakenPhoto;//????????????????????????
    private ImageView customPuzzle;//???????????????
    private ImageView showProduct;//???????????????
    private ImageView btnRemoveAd;//????????????
    private int imageWidth, imageHeight;//
    private ArrayList<Photo> selectedPhotoList = new ArrayList<>();
    private RewardedAd mRewardedAd;

    private static final String AD_UNIT_ID = "ca-app-pub-4907444560177166/3142881234";
    private boolean isLoading;
    private boolean isShowAd = false;
    private FrameLayout mAdView;
    private AdView adView;
    private InterstitialAd mInterstitialAd;
    private AlertDialog dialog;
    private int getGlod;
    private RelativeLayout remove_rllt;
    private RelativeLayout look_movie_rllt;
    private ImageView dialogDismiss;
    private boolean isOnClickLookAd = false;
    private View.OnClickListener dialogListener;

    private AutoHeightViewPager viewPager;
    private ViewPagerAdapter pagerAdapter;
    private List<View> views;
    private View view1,view2;
    private LinearLayout mLlDot;
    private boolean isAlbum = false;
    private ArrayList<Photo> photos = new ArrayList<>();
    private String adUnitId="ca-app-pub-4907444560177166/6470918001";

    /**
     * ???????????????????????????
     */
    private int curIndex = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
    /**
     * ????????????
     */
    public void setOvalLayout() {
        for (int i = 0; i < views.size(); i++) {
            mLlDot.addView(LayoutInflater.from(MainActivity.this).inflate(R.layout.dot, null));
        }
        // ?????????????????????
        mLlDot.getChildAt(0).findViewById(R.id.v_dot)
                .setBackgroundResource(R.drawable.vp_selected_point);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView() {
        views = new ArrayList<View>();

        viewPager = findViewById(R.id.vp);
        mLlDot = (LinearLayout) findViewById(R.id.ll_dot);

        //??????View
        view1 = LayoutInflater.from(MainActivity.this).inflate(R.layout.adapter_vp_source, null);
        view2 = LayoutInflater.from(MainActivity.this).inflate(R.layout.adapter_vp_source2, null);
        views.add(view1);
        views.add(view2);

        pagerAdapter = new ViewPagerAdapter(views);
        viewPager.setAdapter(pagerAdapter);
        viewPager.setCurrentItem(curIndex);
        viewPager.setOffscreenPageLimit(views.size());
        viewPager.addOnPageChangeListener(this);

        //??????viewpager?????????
        setOvalLayout();


        //???????????????
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        //????????????
        mAdView = findViewById(R.id.ad_view);
        adView = new AdView(this);
        adView.setAdUnitId(adUnitId);
        mAdView.addView(adView);
        loadBanner();


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

        if (MyApp.getIsAppListNull()){
            showProduct.setVisibility(View.INVISIBLE);
        }else {
            showProduct.setVisibility(View.VISIBLE);
        }
    }

    //??????????????????
    private void loadBanner() {
        AdRequest adRequest = new AdRequest.Builder().build();
        AdSize adSize = getAdSize();
        adView.setAdSize(adSize);
        adView.loadAd(adRequest);
    }

    //????????????
    private AdSize getAdSize() {
        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);

        float widthPixels = outMetrics.widthPixels;
        float density = outMetrics.density;

        int adWidth = (int) (widthPixels / density);
        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(this, adWidth);
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
        view1.findViewById(R.id.rl_vp_btn).setOnClickListener(this);
        view2.findViewById(R.id.rl_vp_btn).setOnClickListener(this);

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
                            SharedPreferencesUtil.putBoolean(MainActivity.this, SharedPreferencesUtil.IS_REMOVE_AD_SUCCESS,true);
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
     * ??????????????????Dialog
     */
    private void removeDialog() {

        //??????????????????????????????
        getGlod = SharedPreferencesUtil.getInt(this, SharedPreferencesUtil.GOLD_NUM, 0);
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
     * ?????????????????????
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
     * ???????????? ????????????
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

                        // ????????????  ????????????????????????
                        if (isOnClickLookAd){
                            int goldNum = SharedPreferencesUtil.getInt(MainActivity.this, SharedPreferencesUtil.GOLD_NUM,0);
                            SharedPreferencesUtil.putInt(MainActivity.this, SharedPreferencesUtil.GOLD_NUM,goldNum+=1);
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
        Boolean isRemoveAdSuccess = SharedPreferencesUtil.getBoolean(MainActivity.this, SharedPreferencesUtil.IS_REMOVE_AD_SUCCESS,false);
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
            case R.id.edit_image:
            case R.id.rl_vp_btn: {
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
                setting();
                if (isShowAd) {
                    StatisticsManager.uploadAnalyticsEvents(this, SETTING);
                }
                break;
            }
        }//end switch
    }

    /**
     * ??????????????????
     */
    private void setting(){
        startActivity(SettingActivity.class);
    }

    /**
     * ????????????
     */
    private void editImageClick() {
        photos.clear();
        PhotoTool.getInstance().openAlbumForSingle(this, ACTION_REQUEST_EDITIMAGE,photos);
    }


    /**
     * ??????
     */
    private void takePhotoClick() {
        checkPermissions();
    }

    /**
     * ???????????????????????? ??????
     */
    private void selectFromAlbumMultipleForPuzzle() {
        PhotoTool.getInstance().openAlbumForMultiple(this, 9, REQUEST_MULTIPLE_CODE_FOR_PUZZLE);
    }

    /**
     * ???????????????????????? ??????
     */
    private void selectFromAlbumSingle() {
        photos.clear();
        PhotoTool.getInstance().openAlbumForSingle(this, REQUEST_SINGLE_CODE,photos);
    }

    /**
     * ???????????????
     */
    private void startCustomPuzzle() {
        PhotoTool.getInstance().openAlbumForMultiple(this, 9, REQUEST_CUSTOM_PUZZLE_CODE);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_SINGLE_CODE: {
                    //???????????????????????????
                    ArrayList<Photo> resultPhotos = data.getParcelableArrayListExtra(EasyPhotos.RESULT_PHOTOS);
                    isAlbum = true;
                    showResult(resultPhotos.get(0).path);
                    break;
                }
                case REQUEST_MULTIPLE_CODE_FOR_PUZZLE: {
                    //?????????????????????
                    ArrayList<Photo> resultPhotos = data.getParcelableArrayListExtra(EasyPhotos.RESULT_PHOTOS);
                    selectedPhotoList.clear();
                    selectedPhotoList.addAll(resultPhotos);


                    if (resultPhotos.size() == 1) {
                        //?????????????????????1????????????????????????
                        selectedPhotoList.addAll(resultPhotos);
                    }

                    PhotoTool.getInstance().startPuzzleWithPhotos(MainActivity.this, selectedPhotoList, REQUEST_PUZZLE_CODE);

                    break;
                }
                case REQUEST_PUZZLE_CODE: {
                    //??????????????????
                    if (null == data) {
                        return;
                    }
                    Photo puzzlePhoto = data.getParcelableExtra(EasyPhotos.RESULT_PHOTOS); //?????????????????????Photo??????
                    showResult(puzzlePhoto.path);
                    break;
                }
                case REQUEST_CUSTOM_PUZZLE_CODE: {
                    //?????????????????????
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
                    photos.clear();
                    photos.addAll(resultPhotos);
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
        int RETURN_OPERATION = data.getIntExtra(EditImageActivity.RETURN_OPERATION_NAME, 0);

        if (!isImageEdit) {
            //?????????  ????????????????????????
            switch (RETURN_OPERATION){
                case 0:{
                    break;
                }
                case 1:{
                    newFilePath = data.getStringExtra(EditImageActivity.FILE_PATH);
                    showResult(newFilePath);
                    break;
                }
                case 2:{
                    showResult(newFilePath);
                    break;
                }
            }
        }else {
            showResult(newFilePath);
        }

    }

    /**
     * ?????????????????????????????????
     *
     * @param path
     */
    private void showResult(String path) {
        Bundle bundle = new Bundle();
        bundle.putString("path", path);
        bundle.putBoolean("isAlbum",isAlbum);
        startActivity(ShowResultActivity.class, bundle);
        isAlbum = false;
    }

    /**
     * ????????????
     */
    private void checkPermissions() {
        // ???????????????????????????
        if (ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // ???????????????????????????????????????????????????????????????
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CAMERA_CODE);
        } else {
            // ?????????????????????????????????
            startActivity(CameraActivity.class);
        }
    }


    /**
     * ??????????????????
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (REQUEST_CAMERA_CODE == requestCode) {
            // ????????????
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startActivity(CameraActivity.class);
            } else {
                // ????????????
                Toast.makeText(getApplicationContext(), R.string.toast_permissions, Toast.LENGTH_SHORT).show();
            }
        }
    }


    /**
     * ??????
     */
    private void interstitialAd() {
        AdRequest adRequest = new AdRequest.Builder().build();
        InterstitialAd.load(this, "ca-app-pub-4907444560177166/6152187951", adRequest,
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
     * ??????????????????
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
     * ????????????????????????
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            StatisticsManager.uploadAnalyticsEvents(this, DIALOG_SHOW);
            //?????????????????????
            interstitialAd();
            if (mInterstitialAd!=null){
                mInterstitialAd.show(MainActivity.this);
                mInterstitialAd = null;
            }
            showExitAlertDialog();

        }
        return true;
    }


    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        // ??????????????????
        mLlDot.getChildAt(curIndex)
                .findViewById(R.id.v_dot)
                .setBackgroundResource(R.drawable.vp_select_point);
        // ????????????
        mLlDot.getChildAt(position)
                .findViewById(R.id.v_dot)
                .setBackgroundResource(R.drawable.vp_selected_point);
        curIndex = position;
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

}