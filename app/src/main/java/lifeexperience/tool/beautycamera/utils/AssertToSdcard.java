package lifeexperience.tool.beautycamera.utils;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class AssertToSdcard {

    private InputStream is;
    private OutputStream os;
    private File sdcardFile = null;
    private String sdcardFilePath = null;

    /**
     *
     * @param context
     *            用于获取sd路径
     * @param AssetsFileName
     *            放入assets中的html文件名
     * @return
     * 			  AssetsFileName文件复杂到sd卡后的路径
     */
    public String transFilePath(Context context, String AssetsFileName) {

        sdcardFile = getSdcardPath();

        if (null == sdcardFile) {
            return null;
        }

        sdcardFilePath = sdcardFile + File.separator + AssetsFileName;
        Log.i("--->", "" + sdcardFile.getAbsolutePath());

        if (new File(sdcardFilePath).exists()) {
            return sdcardFilePath;
        }

        try {
            is = context.getAssets().open(AssetsFileName);
            os = new FileOutputStream(sdcardFilePath);
            byte[] buffer = new byte[1024];
            int count = 0;
            while ((count = is.read(buffer)) > 0) {
                os.write(buffer, 0, count);
            }
            os.close();
            is.close();

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return sdcardFilePath;
    }

    private File getSdcardPath() {
        File sdcardpath = null;
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            sdcardpath = Environment.getExternalStorageDirectory();
        }
        return sdcardpath;
        // TODO Auto-generated method stub

    }

}