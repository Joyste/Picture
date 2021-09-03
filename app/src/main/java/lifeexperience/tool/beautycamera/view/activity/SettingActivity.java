package lifeexperience.tool.beautycamera.view.activity;



import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

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
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import lifeexperience.tool.beautycamera.R;
import lifeexperience.tool.beautycamera.tool.StatisticsManager;
import lifeexperience.tool.beautycamera.utils.SharedPreferencesUtil;

import static lifeexperience.tool.beautycamera.utils.FirebaseAnalyticsEventUtil.REMOVE_AD_DIALOG_SHOW;

public class SettingActivity extends BaseActivity {

    private ImageView back;
    private LinearLayout llPrivacyPolicy;
    private LinearLayout llRemoveAdvertising;
    private LinearLayout llCheckUpdate;
    private LinearLayout llEncourageUs;
    private int getGlod;
    private AlertDialog dialog;
    private ImageView dialogDismiss;
    private RelativeLayout remove_rllt;
    private RelativeLayout look_movie_rllt;
    private View.OnClickListener dialogListener;
    private boolean isOnClickLookAd = false;
    private boolean isLoading;
    private static final String AD_UNIT_ID = "ca-app-pub-4907444560177166/3142881234";
    private RewardedAd mRewardedAd;
    private FrameLayout mAdView;
    private AdView adView;
    private RatingBar rbEncourage;
    private TextView tvEncourageConfirm;
    private float ratingNum;
    private String adUnitId="ca-app-pub-4907444560177166/6470918001";
    private String StoreUri = "https://play.google.com/store/apps/details?id=lifeexperience.tool.beautycamera";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_setting;
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
        adView = new AdView(this);
        adView.setAdUnitId(adUnitId);
        mAdView.addView(adView);
        loadBanner();

        back = findViewById(R.id.iv_back);
        llPrivacyPolicy = findViewById(R.id.ll_privacy_policy);
        llRemoveAdvertising = findViewById(R.id.ll_remove_advertising);
        llCheckUpdate = findViewById(R.id.ll_check_update);
        llEncourageUs = findViewById(R.id.ll_encourage_us);

    }

    //加载横幅广告
    private void loadBanner() {
        AdRequest adRequest = new AdRequest.Builder().build();
        AdSize adSize = getAdSize();
        adView.setAdSize(adSize);
        adView.loadAd(adRequest);
    }

    //设置尺寸
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
        back.setOnClickListener(this);
        llPrivacyPolicy.setOnClickListener(this);
        llRemoveAdvertising.setOnClickListener(this);
        llCheckUpdate.setOnClickListener(this);
        llEncourageUs.setOnClickListener(this);


        dialogListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()){
                    case R.id.dialog_dismiss:{
                        dialog.dismiss();
                        break;
                    }
                    case R.id.remove_rllt:{
                        if (getGlod < 10) {
                            Toast.makeText(SettingActivity.this, getResources().getString(R.string.remove_ad_toast), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(SettingActivity.this,getResources().getString(R.string.remove_ad_toast_success) , Toast.LENGTH_SHORT).show();
                            SharedPreferencesUtil.putBoolean(SettingActivity.this, SharedPreferencesUtil.IS_REMOVE_AD_SUCCESS,true);
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
                    case R.id.tv_encourage_confirm:{
                        dialog.dismiss();
                        Toast.makeText(SettingActivity.this, getResources().getString(R.string.encourage_toast), Toast.LENGTH_SHORT).show();
                        break;
                    }
                }
            }
        };
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
                            int goldNum = SharedPreferencesUtil.getInt(SettingActivity.this, SharedPreferencesUtil.GOLD_NUM,0);
                            SharedPreferencesUtil.putInt(SettingActivity.this, SharedPreferencesUtil.GOLD_NUM,goldNum+=1);
                            isOnClickLookAd = false;
                        }
                        mRewardedAd = null;
                        return;
                    }
                });
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_back:{
                finish();
                break;
            }
            case R.id.ll_privacy_policy:{
                Uri uri = Uri.parse("https://lifeexperience.oss-cn-hongkong.aliyuncs.com/lfbeautycamera/static/web/privacypolicy.html");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
                break;
            }
            case R.id.ll_remove_advertising:{
                removeDialog();
                StatisticsManager.uploadAnalyticsEvents(this, REMOVE_AD_DIALOG_SHOW);
                break;
            }
            case R.id.ll_check_update:
            case R.id.ll_encourage_us: {
                openGoogleStore();
//                showCheckUpdateAlertDialog();
//                encourageDialog();
                break;
            }


        }

    }

    private void openGoogleStore(){
        Uri uri = Uri.parse(StoreUri);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }

    /**
     * 显示检查更新弹窗
     */
    private void showCheckUpdateAlertDialog(){
        AlertDialog alertDialog = new AlertDialog.Builder(SettingActivity.this).create();
        alertDialog.setMessage(this.getResources().getString(R.string.check_update_message));
        alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, this.getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                alertDialog.dismiss();
            }
        });

        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, this.getResources().getString(R.string.confirm), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                alertDialog.dismiss();
            }
        });
        alertDialog.show();
    }


    /**
     * 评分Dialog
     */
    private void encourageDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(SettingActivity.this, R.style.Transparent);
        dialog = builder.create();
        dialog.show();
        dialog.setCanceledOnTouchOutside(false);
        Window window = dialog.getWindow();
        window.setContentView(R.layout.encourage_us_show);

        dialogDismiss = dialog.findViewById(R.id.dialog_dismiss);
        rbEncourage = dialog.findViewById(R.id.rb_encourage);
        tvEncourageConfirm = dialog.findViewById(R.id.tv_encourage_confirm);

        dialogDismiss.setOnClickListener(dialogListener);
        tvEncourageConfirm.setOnClickListener(dialogListener);

        rbEncourage.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                ratingNum = rating;
            }
        });
        return;
    }


    /**
     * 去除广告提示Dialog
     */
    private void removeDialog() {

        //拿去本地存在金币个数
        getGlod = SharedPreferencesUtil.getInt(this, SharedPreferencesUtil.GOLD_NUM, 0);
        AlertDialog.Builder builder = new AlertDialog.Builder(SettingActivity.this, R.style.Transparent);
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
}