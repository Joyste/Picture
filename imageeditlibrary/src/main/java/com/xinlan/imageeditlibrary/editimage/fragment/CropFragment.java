package com.xinlan.imageeditlibrary.editimage.fragment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.xinlan.imageeditlibrary.BaseActivity;
import com.xinlan.imageeditlibrary.R;
import com.xinlan.imageeditlibrary.editimage.EditImageActivity;
import com.xinlan.imageeditlibrary.editimage.ModuleConfig;
import com.xinlan.imageeditlibrary.editimage.adapter.CropListAdapter;
import com.xinlan.imageeditlibrary.editimage.model.RatioItem;
import com.xinlan.imageeditlibrary.editimage.model.appInfo;
import com.xinlan.imageeditlibrary.editimage.utils.Matrix3;
import com.xinlan.imageeditlibrary.editimage.view.CropImageView;
import com.xinlan.imageeditlibrary.editimage.view.imagezoom.ImageViewTouchBase;


/**
 * 图片剪裁Fragment
 * 
 * @author panyi
 * 
 */
public class CropFragment extends BaseEditFragment {
    public static final int INDEX = ModuleConfig.INDEX_CROP;
	public static final String TAG = CropFragment.class.getName();
	private View mainView;
	private View backToMenu;// 返回主菜单
	public CropImageView mCropPanel;// 剪裁操作面板
	private RecyclerView mCropListView;
	private CropListAdapter mCropListAdapter;
	private TextView tvCropName;
	private ImageView ivCropIcon;

	private View currentItem;
	private int currentPosition;
	private int icoNormal[] = {R.drawable.ico_custom_selected,R.drawable.ico_original_normal,R.drawable.ico_1_1_normal,R.drawable.ico_9_16_normal,R.drawable.ico_16_9_normal,R.drawable.ico_2_3_normal,R.drawable.ico_3_2_normal,R.drawable.ico_4_3_normal,R.drawable.ico_3_4_normal};
	private static boolean defaultSelectedItem;

	private List<RatioItem> dataList = new ArrayList<RatioItem>();

	public static CropFragment newInstance() {
		CropFragment fragment = new CropFragment();
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mainView = inflater.inflate(R.layout.fragment_edit_image_crop, null);
		return mainView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

        backToMenu = mainView.findViewById(R.id.back_to_main);
        mCropListView = mainView.findViewById(R.id.crop_list);

		initData();
		initCropListView();

        this.mCropPanel = ensureEditActivity().mCropPanel;
		backToMenu.setOnClickListener(new BackToMenuClick());// 返回主菜单
	}

	/**
	 * 初始化Item数据
	 */
	private void initData() {
		dataList.clear();
		RectF r = activity.mainImage.getBitmapRect();
		float bitmapRatio = r.width()/r.height();
		dataList.add(new RatioItem(icoNormal[0],getActivity().getResources().getString(R.string.custom), -1f));
		dataList.add(new RatioItem(icoNormal[1],getActivity().getResources().getString(R.string.original), bitmapRatio));
		dataList.add(new RatioItem(icoNormal[2],"1:1",1f));
		dataList.add(new RatioItem(icoNormal[3],"9:16", 9 / 16f));
		dataList.add(new RatioItem(icoNormal[4],"16:9", 16 / 9f));
		dataList.add(new RatioItem(icoNormal[5],"2:3", 2 / 3f));
		dataList.add(new RatioItem(icoNormal[6],"3:2", 3 / 2f));
		dataList.add(new RatioItem(icoNormal[7],"4:3", 4 / 3f));
		dataList.add(new RatioItem(icoNormal[8],"3:4", 3 / 4f));
	}

	/**
	 * 初始化recycleView
	 */
	private void initCropListView() {

		mCropListView.setHasFixedSize(false);

		LinearLayoutManager stickerListLayoutManager = new LinearLayoutManager(activity);
		stickerListLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
		mCropListView.setLayoutManager(stickerListLayoutManager);

		mCropListAdapter = new CropListAdapter(getContext(), dataList);
		mCropListAdapter.setOnItemClickListener(new CropListAdapter.OnItemClickListener() {
			@SuppressLint("ResourceAsColor")
			@Override
			public void onItemClick(View itemView, int position) {
				RatioItem dataItem = (RatioItem) itemView.getTag();
				mCropPanel.setRatioCropRect(activity.mainImage.getBitmapRect(), dataItem.getRatio());
			}
		});
		mCropListView.setAdapter(mCropListAdapter);
	}


	@Override
    public void onShow() {
        activity.mode = EditImageActivity.MODE_CROP;

        activity.mCropPanel.setVisibility(View.VISIBLE);
        activity.mainImage.setImageBitmap(activity.getMainBit());
        activity.mainImage.setDisplayType(ImageViewTouchBase.DisplayType.FIT_TO_SCREEN);
        activity.mainImage.setScaleEnabled(false);// 禁用缩放
        activity.bannerFlipper.showNext();
		activity.setRedoUndoPanelVisibility(View.INVISIBLE);
		activity.mainImage.post(new Runnable() {
			@Override
			public void run() {
				final RectF r = activity.mainImage.getBitmapRect();
				activity.mCropPanel.setCropRect(r);
			}
		});
    }

    /**
	 * 返回按钮逻辑
	 * 
	 * @author panyi
	 * 
	 */
	private final class BackToMenuClick implements OnClickListener {
		@Override
		public void onClick(View v) {
			backToMain();
		}
	}// end class

	/**
	 * 返回主菜单
	 */
	@Override
	public void backToMain() {
		activity.mode = EditImageActivity.MODE_NONE;
		mCropPanel.setVisibility(View.GONE);
		activity.mainImage.setScaleEnabled(true);// 恢复缩放功能
		activity.bottomGallery.setCurrentItem(0);
		mCropPanel.setRatioCropRect(activity.mainImage.getBitmapRect(), -1);
		activity.bannerFlipper.showPrevious();
		activity.setRedoUndoPanelVisibility(View.VISIBLE);
	}

	/**
	 * 保存剪切图片
	 */
	public void applyCropImage() {
		// System.out.println("保存剪切图片");
		CropImageTask task = new CropImageTask();
		task.execute(activity.getMainBit());
	}

	/**
	 * 图片剪裁生成 异步任务
	 * 
	 * @author panyi
	 * 
	 */
	private final class CropImageTask extends AsyncTask<Bitmap, Void, Bitmap> {
		private Dialog dialog;

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
		protected void onPreExecute() {
			super.onPreExecute();
			dialog = BaseActivity.getLoadingDialog(getActivity(), R.string.saving_image,
					false);
			dialog.show();
		}

		@SuppressWarnings("WrongThread")
        @Override
		protected Bitmap doInBackground(Bitmap... params) {
			RectF cropRect = mCropPanel.getCropRect();// 剪切区域矩形
			Matrix touchMatrix = activity.mainImage.getImageViewMatrix();
			// Canvas canvas = new Canvas(resultBit);
			float[] data = new float[9];
			touchMatrix.getValues(data);// 底部图片变化记录矩阵原始数据
			Matrix3 cal = new Matrix3(data);// 辅助矩阵计算类
			Matrix3 inverseMatrix = cal.inverseMatrix();// 计算逆矩阵
			Matrix m = new Matrix();
			m.setValues(inverseMatrix.getValues());
			m.mapRect(cropRect);// 变化剪切矩形

			Bitmap resultBit = Bitmap.createBitmap(params[0],
					(int) cropRect.left, (int) cropRect.top,
					(int) cropRect.width(), (int) cropRect.height());

			return resultBit;
		}

		@Override
		protected void onPostExecute(Bitmap result) {
			super.onPostExecute(result);
			dialog.dismiss();
			if (result == null)
				return;

            activity.changeMainBitmap(result,true);
			activity.mCropPanel.setCropRect(activity.mainImage.getBitmapRect());
			backToMain();
		}
	}// end inner class

	/**
	 * 保存Bitmap图片到指定文件
	 */
	public static void saveBitmap(Bitmap bm, String filePath) {
		File f = new File(filePath);
		if (f.exists()) {
			f.delete();
		}
		try {
			FileOutputStream out = new FileOutputStream(f);
			bm.compress(Bitmap.CompressFormat.PNG, 90, out);
			out.flush();
			out.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}// end class
