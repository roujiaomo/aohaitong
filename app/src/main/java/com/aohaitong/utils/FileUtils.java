package com.aohaitong.utils;

import android.os.Environment;
import android.util.Log;

import com.aohaitong.constant.CommonConstant;
import com.aohaitong.utils.base.Utils;
import com.blankj.utilcode.util.FileIOUtils;
import com.haocai.ffmpegtest.util.VideoPlayer;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 子线程暂未处理
 */
public class FileUtils {

    public static String BASE_FILE_PATH = Environment.getExternalStorageDirectory().getAbsolutePath();

    public static String fileToString(String filePath) {
        FileInputStream fileInputStream = null;
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        //读本地的发送文件
        try {
            fileInputStream = new FileInputStream(filePath);
            byte[] buffer = new byte[1024];
            int len = 0;
            while (-1 != (len = fileInputStream.read(buffer))) {
                outStream.write(buffer, 0, len);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fileInputStream != null) {
                    fileInputStream.close();
                    outStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        byte[] bytes = outStream.toByteArray();
        StringBuffer sb = new StringBuffer();
        for (byte b : bytes) {
            sb.append((char) b);
        }
        return sb.toString();
    }

    /**
     * 文件转字符串,用于发送消息
     *
     * @param filePath
     * @return
     */
    public static List<String> videoFileToString(String filePath) {
        FileInputStream fileInputStream = null;
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        //读本地的发送文件
        try {
            fileInputStream = new FileInputStream(filePath);
            byte[] buffer = new byte[1024];
            int len = 0;
            while (-1 != (len = fileInputStream.read(buffer))) {
                outStream.write(buffer, 0, len);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fileInputStream != null) {
                    fileInputStream.close();
                    outStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        byte[] bytes = outStream.toByteArray();
        List<String> sbList = new ArrayList<String>();
        StringBuffer sb = new StringBuffer();
        Log.d("qqqqq", "fileToString: " + bytes.length);
        int index = 0;
        for (byte b : bytes) {
            index++;
            if (index % 2500000 == 0) {
                sbList.add(sb.toString());
                sb = new StringBuffer();
                Log.d("qqqqq", "已组装2500000 此时index : " + index);
            }
            sb.append((char) b);
        }

        if (Utils.notEmpty(sb.toString())) {
            sbList.add(sb.toString());
        }
        Log.d("qqqqq", "fileToString2: " + sbList.size());
        long strLength = 0L;
        for (String str : sbList) {
            strLength += str.length();
        }
        Log.d("qqqqq", "字符串长度: " + strLength);
        return sbList;
    }

    /**
     * 字符串转文件,用于接收语音消息
     */
    public static void stringToFile(String data, String filePath) {
        byte[] bytes = new byte[data.length()];
        for (int i = 0; i < data.length(); i++) {
            int c = data.charAt(i);
            bytes[i] = (byte) c;
        }
        //生成文件
        FileIOUtils.writeFileFromBytesByStream(filePath, bytes);
    }

    /**
     * 生成文件名
     */
    public static String generateFileName() {
        return UUID.randomUUID().toString() + ".amr";
    }

    public static void deleteAllFile(String path) {
        File dir = new File(path);
        if (!dir.exists() || !dir.isDirectory())
            return;
        try {
            for (File file : dir.listFiles()) {
                if (file.isFile())
                    file.delete(); // 删除所有文件
                else if (file.isDirectory())
                    deleteAllFile(path); // 递规的方式删除文件夹
            }
            dir.delete();// 删除目录本身
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void deleteFiles(List<String> paths) {
        try {
            for (String path : paths) {
                File file = new File(path);
                if (file.isFile())
                    file.delete();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 生成文件名
     */
    public static String generateFileName(String fileType) {
        return UUID.randomUUID().toString() + "." + fileType;
    }

    /**
     * 压缩文件
     *
     * @param oldFilePath    原文件的的路径 getAbsolutePath1
     * @param outputFilePath 输出文件的路径 getAbsolutePath
     * @return
     */
    public static Boolean compressVideo(String oldFilePath, String outputFilePath) {
        try {
            String builder = "ffmpeg " +
                    "-i " +
                    oldFilePath + " " +
                    "-b:v 1924k " +  //码率越大 清晰度越高 码率越小 清晰度越低
                    outputFilePath;
            String[] argv = builder.split(" ");
            new VideoPlayer().ffmpegCmdUtil(argv.length, argv);
            return true;
        } catch (Exception e) {
            Log.e(CommonConstant.LOGCAT_TAG, "压缩失败: " + e.getMessage());
        }
        return false;
    }
}