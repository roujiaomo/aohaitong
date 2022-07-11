package com.aohaitong.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.text.TextUtils;

import com.aohaitong.constant.CommonConstant;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Administrator  .
 */
public class BitmapUtil {

    private static final String DEFAULT_FILE_PATH = Environment.getExternalStorageDirectory().getAbsolutePath()
            + CommonConstant.PHOTO_FILE_PATH;

    /**
     * 读取本地图片
     *
     * @param path 图片路径
     */
    public static Bitmap getDiskBitmap(String path) {
        Bitmap bitmap = null;
        if (TextUtils.isEmpty(path)) {
            return null;
        }
        try {
            File file = new File(path);
            if (file.exists()) {
                BitmapFactory.Options opt = new BitmapFactory.Options();
                opt.inPreferredConfig = Bitmap.Config.RGB_565;
                bitmap = BitmapFactory.decodeFile(path, opt);
            }
        } catch (Exception e) {
            return null;
        }
        return bitmap;
    }

    /**
     * 保存图片到本地 第一个参数是图片名称 第二个参数为需要保存的bitmap
     */
    public static void saveImgToDisk(String imgName, Bitmap bitmap) {
        File file = new File(DEFAULT_FILE_PATH);
        if (!file.exists()) {
            file.mkdir();
        }
        File file1 = new File(DEFAULT_FILE_PATH + imgName);
        try {
            FileOutputStream out = new FileOutputStream(file1);
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
            String path = file.getPath();
            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 判断文件路径是否已经存在
     *
     * @param filePath 文件路径
     */
    private static boolean isFileExists(String filePath) {
        try {
            File file = new File(filePath);
            return file.exists();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 返回SD卡跟目录 <br>
     *
     * @return SD卡跟目录
     */
    public static String getSdCardPath() {
        File sdDir;
        boolean sdCardExist = isSdCardExist(); // 判断sd卡是否存在
        if (sdCardExist) {
            sdDir = Environment.getExternalStorageDirectory();// 获取跟目录
            return sdDir.toString();
        } else {
            return null;
        }
    }

    /**
     * 判断SD卡是否存在 <br>
     *
     * @return SD卡存在返回true，否则返回false
     */
    public static boolean isSdCardExist() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }
}