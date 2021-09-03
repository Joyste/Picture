package com.xinlan.imageeditlibrary.editimage.fragment;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xinlan.imageeditlibrary.BaseActivity;
import com.xinlan.imageeditlibrary.R;
import com.xinlan.imageeditlibrary.editimage.EditImageActivity;
import com.xinlan.imageeditlibrary.editimage.ModuleConfig;
import com.xinlan.imageeditlibrary.editimage.fliter.PhotoProcessing;
import com.xinlan.imageeditlibrary.editimage.view.imagezoom.ImageViewTouchBase;


/**
 * 滤镜列表fragment
 *
 * @author panyi
 */
public class FilterListFragment extends BaseEditFragment {
    public static final int INDEX = ModuleConfig.INDEX_FILTER;
    public static final String TAG = FilterListFragment.class.getName();
    private View mainView;
    private View backBtn;// 返回主菜单按钮

    private Bitmap fliterBit;// 滤镜处理后的bitmap

    private LinearLayout mFilterGroup;// 滤镜列表
    private int[] filterIcon = {R.drawable.ico_filter_1, R.drawable.ico_filter_2, R.drawable.ico_filter_3, R.drawable.ico_filter_4, R.drawable.ico_filter_5, R.drawable.ico_filter_6, R.drawable.ico_filter_7, R.drawable.ico_filter_8, R.drawable.ico_filter_9, R.drawable.ico_filter_10, R.drawable.ico_filter_11, R.drawable.ico_filter_12, R.drawable.ico_filter_13};


    private String[] fliters;
    private Bitmap currentBitmap;// 标记变量
    private View curView;
    private int curPosition;

    public static FilterListFragment newInstance() {
        FilterListFragment fragment = new FilterListFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mainView = inflater.inflate(R.layout.fragment_edit_image_fliter, null);
        return mainView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        backBtn = mainView.findViewById(R.id.back_to_main);
        mFilterGroup = (LinearLayout) mainView.findViewById(R.id.filter_group);


        backBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                backToMain();
            }
        });
        setUpFliters();
    }

    @Override
    public void onShow() {
        activity.mode = EditImageActivity.MODE_FILTER;
        activity.mFilterListFragment.setCurrentBitmap(activity.getMainBit());
        activity.mainImage.setImageBitmap(activity.getMainBit());
        activity.mainImage.setDisplayType(ImageViewTouchBase.DisplayType.FIT_TO_SCREEN);
        activity.mainImage.setScaleEnabled(false);
        activity.bannerFlipper.showNext();
        activity.setRedoUndoPanelVisibility(View.INVISIBLE);
    }

    /**
     * 返回主菜单
     */
    @Override
    public void backToMain() {
        if(curPosition != 0){
            showAlertDialog(getContext(), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    activity.changeMainBitmap(fliterBit,true);
                    back();
                }
            }, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    back();
                }
            });
        }else {
            back();
        }
    }

    private void back(){
        currentBitmap = activity.getMainBit();
        fliterBit = null;
        activity.mainImage.setImageBitmap(activity.getMainBit());// 返回原图
        activity.mode = EditImageActivity.MODE_NONE;
        activity.bottomGallery.setCurrentItem(0);
        activity.mainImage.setScaleEnabled(true);
        activity.bannerFlipper.showPrevious();
        activity.setRedoUndoPanelVisibility(View.VISIBLE);
    }

    /**
     * 保存滤镜处理后的图片
     */
    public void applyFilterImage() {
        // System.out.println("保存滤镜处理后的图片");
        if (currentBitmap == activity.getMainBit()) {// 原始图片
            // System.out.println("原始图片");
            back();
            return;
        } else {// 经滤镜处理后的图片
            // System.out.println("滤镜图片");
            activity.changeMainBitmap(fliterBit,true);
            back();
        }// end if
    }

    /**
     * 装载滤镜
     */
    private void setUpFliters() {
        fliters = getResources().getStringArray(R.array.filters);
        if (fliters == null)
            return;

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER_VERTICAL;
        params.leftMargin = 20;
        params.rightMargin = 20;

        mFilterGroup.removeAllViews();
        for (int i = 0, len = fliters.length; i < len; i++) {
            LinearLayout mFilterItem = (LinearLayout) getLayoutInflater().inflate(R.layout.view_fliter_item, null);
            ImageView imageView = mFilterItem.findViewById(R.id.iv_filter_icon);
            imageView.setImageResource(filterIcon[i]);
            TextView text = mFilterItem.findViewById(R.id.tv_filter_name);
            text.setText(fliters[i]);
            if(i==0){
                ImageView selected = mFilterItem.findViewById(R.id.iv_filter_icon_selected);
                selected.setVisibility(View.VISIBLE);
                curView = selected;
                curPosition = 0;
            }
            mFilterGroup.addView(mFilterItem, params);
            mFilterItem.setTag(i);
            mFilterItem.setOnClickListener(new FliterClick());
        }// end for i
    }

    @Override
    public void onDestroy() {
        if (fliterBit != null && (!fliterBit.isRecycled())) {
            fliterBit.recycle();
        }
        super.onDestroy();
    }

    /**
     * 选择滤镜效果
     */
    private final class FliterClick implements OnClickListener {
        @Override
        public void onClick(View v) {
            int position = ((Integer) v.getTag()).intValue();
            if (position == 0) {// 原始图片效果
                activity.mainImage.setImageBitmap(activity.getMainBit());
                currentBitmap = activity.getMainBit();
            }
            if(curView!=null){
                curView.setVisibility(View.INVISIBLE);
            }
            ImageView selected = v.findViewById(R.id.iv_filter_icon_selected);
            selected.setVisibility(View.VISIBLE);
            curView = selected;
            curPosition = position;

            // 滤镜处理
            ProcessingImage task = new ProcessingImage();
            task.execute(position);
        }
    }// end inner class

    /**
     * 图片滤镜处理任务
     *
     * @author panyi
     */
    private final class ProcessingImage extends AsyncTask<Integer, Void, Bitmap> {
        private Dialog dialog;
        private Bitmap srcBitmap;

        @Override
        protected Bitmap doInBackground(Integer... params) {
            int type = params[0];
            if (srcBitmap != null && !srcBitmap.isRecycled()) {
                srcBitmap.recycle();
            }

            srcBitmap = Bitmap.createBitmap(activity.getMainBit().copy(
                    Bitmap.Config.ARGB_8888, true));
            return PhotoProcessing.filterPhoto(srcBitmap, type);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            dialog.dismiss();
        }

        @TargetApi(Build.VERSION_CODES.HONEYCOMB)
        @Override
        protected void onCancelled(Bitmap result) {
            super.onCancelled(result);
            dialog.dismiss();
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            super.onPostExecute(result);
            dialog.dismiss();
            if (result == null)
                return;
            if (fliterBit != null && (!fliterBit.isRecycled())) {
                fliterBit.recycle();
            }
            fliterBit = result;
            activity.mainImage.setImageBitmap(fliterBit);
            currentBitmap = fliterBit;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = BaseActivity.getLoadingDialog(getActivity(), R.string.handing,
                    false);
            dialog.show();
        }

    }// end inner class

    public Bitmap getCurrentBitmap() {
        return currentBitmap;
    }

    public void setCurrentBitmap(Bitmap currentBitmap) {
        this.currentBitmap = currentBitmap;
    }
}// end class
