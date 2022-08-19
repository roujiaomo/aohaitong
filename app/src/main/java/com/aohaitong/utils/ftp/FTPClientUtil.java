package com.aohaitong.utils.ftp;

import android.util.Log;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPClientConfig;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FTPClientUtil {

    private static final String TAG = "lllll";

    private FTPClient ftpClient = null; // FTP客户端

    /**
     * 连接到FTP服务器
     *
     * @param host     ftp服务器域名或ip地址 ，注意：前缀不要带'ftp://'字符
     * @param username 访问用户名
     * @param password 访问密码
     * @param port     端口
     * @return 是否连接成功
     */
    public boolean ftpConnect(String host, String username, String password, int port) {
        try {
            ftpClient = new FTPClient();
//            ftpClient.setControlEncoding("utf-8");
//            ftpClient.setBufferSize(1024);
            Log.d(TAG, "connecting to the ftp server ");
//            ftpClient.connect("5t749z9368.zicp.fun", 32062);
            ftpClient.connect("5t749z9368.zicp.fun", 36572);

            Log.d(TAG, "服务器回复码" + ftpClient.getReplyCode());


            // 根据返回的状态码，判断链接是否建立成功
            if (FTPReply.isPositiveCompletion(ftpClient.getReplyCode())) {
                Log.d(TAG, "login to the ftp server");
                boolean status = ftpClient.login("aohai-ftp", "12345678");
                String path = "/FTPFile";
                //检查上传路径是否存在 如果不存在返回false
                boolean flag = ftpClient.changeWorkingDirectory(path);
                if (!flag) {
                    //创建上传的路径  该方法只能创建一级目录，在这里如果/home/ftpuser存在则可创建image
                    ftpClient.makeDirectory(path);
                }
                ftpClient.setRemoteVerificationEnabled(false);
                ftpClient.enterLocalPassiveMode();
                ftpClient.setFileTransferMode(FTP.STREAM_TRANSFER_MODE);
                ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
                return status;
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "Error: could not connect to host " + e.getLocalizedMessage());
        }
        return false;
    }

    /**
     * 断开ftp服务器连接
     *
     * @return 断开结果
     */
    public boolean ftpDisconnect() {
        // 判断空指针
        if (ftpClient == null) {
            return true;
        }

        // 断开ftp服务器连接
        try {
            ftpClient.logout();
            ftpClient.disconnect();
            return true;
        } catch (Exception e) {
            Log.d(TAG, "Error occurred while disconnecting from ftp server.");
        }
        return false;
    }

    /**
     * ftp 文件上传
     *
     * @param srcFilePath  源文件目录
     * @param desFileName  文件名称
     * @param desDirectory 目标文件
     * @return 文件上传结果
     */
    public boolean ftpUpload(String srcFilePath, String desFileName, String desDirectory) {
        boolean status = false;
        try {
            ftpClient.setDataTimeout(120 * 1000);
            ftpClient.setControlKeepAliveTimeout(120);
            ftpClient.setControlKeepAliveReplyTimeout(120 * 1000);
            File file = new File(srcFilePath);
            FileInputStream srcFileStream = new FileInputStream(file);
            status = ftpClient.storeFile(desFileName, srcFileStream);
            Log.d(TAG, "upload status: " + status);
            srcFileStream.close();
            return status;
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "upload failed: " + e.getLocalizedMessage());
        }
        return status;
    }

    /**
     * ftp 更改目录
     *
     * @param path 更改的路径
     * @return 更改是否成功
     */
    public boolean ftpChangeDir(String path) {
        boolean status = false;
        try {
            status = ftpClient.changeWorkingDirectory(path);
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "change directory failed: " + e.getLocalizedMessage());
        }
        return status;
    }

    /**
     * FTP列表.
     */
    private final List<FTPFile> list = new ArrayList<>();

    public List<FTPFile> listFiles(String remotePath) {
        if (ftpClient != null) {
            // 获取文件
            try {
                FTPClientConfig config = new FTPClientConfig(ftpClient.getSystemType().split(" ")[0]);
                config.setServerLanguageCode("zh");
                ftpClient.configure(config);
                // 使用被动模式设为默认
//                ftpClient.enterLocalPassiveMode();
                FTPFile[] files = ftpClient.listFiles(remotePath);
                if (files != null && files.length > 0) {
                    // 遍历并且添加到集合
                    list.addAll(Arrays.asList(files));
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.d("lllll", e.getMessage());
            }
        }
        return list;
    }


}
