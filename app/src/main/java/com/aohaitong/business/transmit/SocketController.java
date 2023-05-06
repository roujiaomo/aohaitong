package com.aohaitong.business.transmit;

import android.text.TextUtils;
import android.util.Log;

import com.aohaitong.business.BaseController;
import com.aohaitong.business.IPController;
import com.aohaitong.business.request.MsgRequest;
import com.aohaitong.business.request.RequestQueueManager;
import com.aohaitong.constant.CommonConstant;
import com.aohaitong.constant.StatusConstant;

import org.greenrobot.eventbus.EventBus;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

public class SocketController {

    private boolean isStart = false;
    private static SocketController instance;
    private Socket socket;
    private MsgReceiveHandler msgReceiveHandler;
    private MsgSendHandler msgSendHandler;

    public static SocketController getInstance() {
        if (instance == null) {
            instance = new SocketController();
        }
        return instance;
    }

    private SocketController() {
    }

    /**
     * 开启socket连接
     */
    public void startConnect() {
        if (isStart) {
            return;
        }
        socket = new Socket();
        try {
            socket.connect(new InetSocketAddress(IPController.getIp(),
                    Integer.parseInt(IPController.PORT)), 5000);
            socket.setKeepAlive(true);
            msgReceiveHandler = new MsgReceiveHandler(socket);
            msgReceiveHandler.start();
            msgSendHandler = new MsgSendHandler(socket);
            msgSendHandler.start();
            isStart = true;
            Log.d(CommonConstant.LOGCAT_TAG, "socket连接成功");
        } catch (IOException e) {
            Log.e(CommonConstant.LOGCAT_TAG, "socket连接异常：" + e.getMessage());
            e.printStackTrace();
        }
    }

    public void stopConnect() {
        isStart = false;
        if (socket == null)
            return;
        try {
            socket.close();
            socket = null;
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (msgReceiveHandler == null || msgSendHandler == null)
            return;
        msgSendHandler.setRun(false);
        msgReceiveHandler.setRun(false);
        msgSendHandler = null;
        msgReceiveHandler = null;
    }

    public Boolean getIsSocketStart() {
        return socket != null && socket.isConnected() && isStart;
    }

    public static class MsgSendHandler extends Thread {
        private boolean isRun = true;

        public void setRun(boolean run) {
            isRun = run;
        }

        private final Socket mSocket;
        OutputStream mOutputString = null;
        DataOutputStream mDataOutputStream = null;

        /**
         * 会话唯一标识
         */
        public MsgSendHandler(Socket socket) {
            this.mSocket = socket;
        }

        public void run() {

            try {
                mOutputString = mSocket.getOutputStream();
                mDataOutputStream = new DataOutputStream(new BufferedOutputStream(
                        mOutputString));

                while (isRun) {
                    MsgRequest request = RequestQueueManager.getInstance().poll();
                    if (request == null) {
                        continue;
                    }
                    MsgRequest.SendCallback callback = request.getSendCallBack();
                    String param = request.getMsgParam();

                    if (param == null) {
                        continue;
                    }
                    //发送文字消息
                    doTextSend(param, callback);
                    try {
                        Thread.sleep(30);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }

            } catch (IOException e1) {
                e1.printStackTrace();
            }

        }

        /**
         * 发送文本消息
         *
         * @param entity
         */
        private void doTextSend(String entity, MsgRequest.SendCallback callback) {
            if (mDataOutputStream == null || entity == null || TextUtils.isEmpty(entity)) {
                return;
            }
            try {
                byte[] buffer = entity.getBytes();
                mDataOutputStream.write(buffer);
                mDataOutputStream.flush();
                if (callback != null) {
                    callback.onFinish();
                }
            } catch (IOException e) {
                Log.e(CommonConstant.LOGCAT_TAG, "socket发送消息异常1：" + e.getMessage());
                if (e.getMessage() != null && e.getMessage().contains(StatusConstant.CONNECTION_SOCKET_BROKEN_PIPE)) {
                    Log.e(CommonConstant.LOGCAT_TAG, "socket发送消息异常2：" + e.getMessage());
                    BaseController.logOut();
                    BaseController.onConnectStop();
                }
                if (callback != null) {
                    callback.onError(e.getMessage());
                }
            }
        }

    }

    public static class MsgReceiveHandler extends Thread {
        InputStream mInputString = null;
        DataInputStream mDataInputStream = null;
        private final Socket mSocket;
        private boolean isRun = true;

        public void setRun(boolean run) {
            isRun = run;
        }

        public MsgReceiveHandler(Socket socket) {
            this.mSocket = socket;
        }

        public void run() {
            try {
                mInputString = mSocket.getInputStream();
                mDataInputStream = new DataInputStream(new BufferedInputStream(
                        mInputString));
                while (isRun) {
                    if (mSocket == null || mDataInputStream == null) {
                        return;
                    }
                    doTextReceive(mDataInputStream);
//                    try {
//                        Thread.sleep(1000);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
                }
            } catch (IOException e1) {
                Log.e(CommonConstant.LOGCAT_TAG, "socket接收消息异常：" + e1.getMessage());
                e1.printStackTrace();
            }
        }

        /**
         * 接收文本信息
         *
         * @throws IOException
         */
        private void doTextReceive(DataInputStream dis) throws IOException {
            String msg;
            msg = dis.readLine();
            if (msg == null) {
                return;
            }
            Log.e("ooooo", "socket接收信息:" + msg);
            if (!TextUtils.isEmpty(msg.trim())) {
                EventBus.getDefault().post(msg);
            }
        }
    }

}
