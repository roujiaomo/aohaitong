package com.aohaitong.business.transmit;

import android.text.TextUtils;
import android.util.Log;

import com.aohaitong.MyApplication;
import com.aohaitong.business.BaseController;
import com.aohaitong.business.IPController;
import com.aohaitong.business.request.HeartQueueManager;
import com.aohaitong.business.request.MsgRequest;
import com.aohaitong.business.request.RequestQueueManager;
import com.aohaitong.constant.CommonConstant;
import com.aohaitong.constant.StatusConstant;
import com.google.gson.Gson;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.Map;

import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;


public class MqController {

    private static MqController instance;
    private MqController.MsgReceiveHandler msgReceiveHandler;
    private MqController.MsgReceiveHeartHandler msgReceiveHeartHandler;
    private MqController.MsgSendHandler msgSendHandler;
    private MqController.MsgSendHeartHandler msgSendHeartHandler;

    static Gson gson = new Gson();
    static String sendStr = "offshoreServerTopic";
    static String receiveStr = "offshoreClientTopic";
    static String receiveHeartStr = "offshoreHeartbeatTopic";
    ConnectionFactory connfactory;//连接工厂
    ActiveMQConnection conn = null;//连接
    Session session;//接收或者发送消息的线程
    private final long startTime = 0;//用于记录成功连接的时间,30s内不中断重连
    private boolean isMQConnected = false;

    public static MqController getInstance() {
        if (instance == null) {
            instance = new MqController();
        }
        return instance;
    }

    private MqController() {
    }

    public boolean getIsStart() {
        return isMQConnected;
    }

    public boolean getConnExist() {
        return conn != null;
    }

    /**
     * 开启socket连接 执行到这说明ping通了 可在此处
     */
    public void startConnect() {
        try {
            Log.d(CommonConstant.LOGCAT_TAG, "开始MQ连接时conn的状态: " + conn);
            String url = "failover:" + "(tcp://" + IPController.getIp() + ":" + IPController.PORT + ")";
            connfactory = new ActiveMQConnectionFactory(url);
            Log.d(CommonConstant.LOGCAT_TAG, "connfactory = new ActiveMQConnectionFactory(url)");
            conn = (ActiveMQConnection) connfactory.createConnection();//获取连接
            Log.d(CommonConstant.LOGCAT_TAG, "connfactory.createConnection()");
            conn.setClientID(MyApplication.TEL + "");
            conn.start();
            Log.d(CommonConstant.LOGCAT_TAG, "conn.start(): ");
            session = conn.createSession(false, Session.AUTO_ACKNOWLEDGE);//以事务模式获取会话
            msgReceiveHandler = new MqController.MsgReceiveHandler(session);
            msgReceiveHandler.start();
            msgReceiveHeartHandler = new MqController.MsgReceiveHeartHandler(session);
            msgReceiveHeartHandler.start();
            msgSendHandler = new MqController.MsgSendHandler(session);
            msgSendHandler.start();
            msgSendHeartHandler = new MqController.MsgSendHeartHandler(session);
            msgSendHeartHandler.start();
            isMQConnected = true;
            Log.e(CommonConstant.LOGCAT_TAG, "MQ长连接成功");
        } catch (Exception e) {
            //考虑当mq已连接的时候 杀进程 并重启登录界面
            Log.e(CommonConstant.LOGCAT_TAG, "MQ长连接失败: " + e.getLocalizedMessage());
        }
    }

    /**
     * 心跳停止时,断开mq连接
     */
    public void stopConnect() {
        Log.d(CommonConstant.LOGCAT_TAG, "开始停止MQ连接");
        stopMQWorkThread();
        try {
            if (IPController.CONNECT_TYPE == StatusConstant.CONNECT_MQ) {
                if (session != null) {
                    session.close();
                    session = null;
                    Log.d(CommonConstant.LOGCAT_TAG, "MQ session清除成功");
                }
                if (conn != null) {
//                    conn.doCleanup(true);
                    conn.close();
                    conn = null;
                    Log.d(CommonConstant.LOGCAT_TAG, "MQ conn清除成功");
                }
            }
        } catch (JMSException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.e(CommonConstant.LOGCAT_TAG, "终止MQ长连接成功");
    }

    public void stopMQWorkThread() {
        if (msgSendHeartHandler != null) {
            msgSendHeartHandler.setRun(false);
            msgSendHeartHandler = null;
        }
        if (msgSendHandler != null) {
            msgSendHandler.setRun(false);
            msgSendHandler = null;
        }
        if (msgReceiveHandler != null) {
            msgReceiveHandler.setRun(false);
            msgReceiveHandler = null;
        }
        if (msgReceiveHeartHandler != null) {
            msgReceiveHeartHandler.setRun(false);
            msgReceiveHeartHandler = null;
        }
        isMQConnected = false;
    }

    public void startReConnect(MQConnectListener mqConnectListener) {
        try {
            Log.d(CommonConstant.LOGCAT_TAG, "调用开启连接时conn的状态: " + conn);
            if (conn != null && conn.isStarted()) {
                mqConnectListener.failed();
                return;
            }
            Log.d(CommonConstant.LOGCAT_TAG, "开始MQ连接");
            String url = "failover:" + "(tcp://" + IPController.getIp() + ":" + IPController.PORT + ")";
            connfactory = new ActiveMQConnectionFactory("admin", "admin", url);
            Log.d(CommonConstant.LOGCAT_TAG, "connfactory = new ActiveMQConnectionFactory(url)");
            conn = (ActiveMQConnection) connfactory.createConnection();//获取连接
            Log.d(CommonConstant.LOGCAT_TAG, "connfactory.createConnection()");
            conn.setClientID(MyApplication.TEL + "");
            conn.start();
            Log.d(CommonConstant.LOGCAT_TAG, "conn.start(): ");
            session = conn.createSession(false, Session.AUTO_ACKNOWLEDGE);//以事务模式获取会话
            msgReceiveHandler = new MqController.MsgReceiveHandler(session);
            msgReceiveHandler.start();
            msgReceiveHeartHandler = new MqController.MsgReceiveHeartHandler(session);
            msgReceiveHeartHandler.start();
            msgSendHandler = new MqController.MsgSendHandler(session);
            msgSendHandler.start();
            msgSendHeartHandler = new MqController.MsgSendHeartHandler(session);
            msgSendHeartHandler.start();
            isMQConnected = true;
            Log.e(CommonConstant.LOGCAT_TAG, "MQ长连接成功");
            mqConnectListener.success();
        } catch (Exception e) {
            //考虑当mq已连接的时候 杀进程 并重启登录界面
            Log.e(CommonConstant.LOGCAT_TAG, "MQ长连接失败: " + e.getLocalizedMessage());
            if (e.getLocalizedMessage() != null && e.getLocalizedMessage().contains("already connected")) {
                BaseController.stopMQConnect(true);
                BaseController.stopHeartBeat();
                BaseController.onConnectStop();
            }
        }
    }

    public static class MsgSendHeartHandler extends Thread {
        private boolean isRun = true;
        Session session;
        Destination dest;
        MessageProducer producer;//消息的生产者

        public MsgSendHeartHandler(Session session) {
            try {
                this.dest = session.createTopic(sendStr);
            } catch (JMSException e) {
                e.printStackTrace();
            }
            this.session = session;
        }

        public void setRun(boolean run) {
            isRun = run;
        }

        public void run() {

            try {
                producer = session.createProducer(dest);//创建消息生产者
                Log.d(CommonConstant.LOGCAT_TAG, "MQ发送消息线程工作");
                while (isRun) {
                    MsgRequest request = HeartQueueManager.getInstance().poll();
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
                }

            } catch (JMSException e1) {
                e1.printStackTrace();
            }

        }

        /**
         * 发送文本消息
         *
         * @param entity
         */
        private void doTextSend(String entity, MsgRequest.SendCallback callback) {
            if (producer == null || entity == null || TextUtils.isEmpty(entity)) {
                return;
            }
            try {
                Map map = new HashMap();
                map.put("sourceAccount", MyApplication.TEL);
                map.put("message", entity.replace("\r\n", ""));
                TextMessage text = session.createTextMessage(gson.toJson(map));//session用来生产消息
                producer.send(dest, text);//MessageProducer用来发送消息
                if (callback != null) {
                    callback.onFinish();
                }

            } catch (JMSException e) {
                if (callback != null) {
                    Log.e(CommonConstant.LOGCAT_TAG, "MQ发送消息异常" + e.getMessage());
                    callback.onError(e.getMessage());
                }
            }
        }

    }


    public static class MsgSendHandler extends Thread {
        private boolean isRun = true;
        Session session;
        Destination dest;
        MessageProducer producer;//消息的生产者

        public MsgSendHandler(Session session) {
            try {
                this.dest = session.createTopic(sendStr);
            } catch (JMSException e) {
                e.printStackTrace();
            }
            this.session = session;
        }

        public void setRun(boolean run) {
            isRun = run;
        }

        public void run() {

            try {
                producer = session.createProducer(dest);//创建消息生产者
                Log.d(CommonConstant.LOGCAT_TAG, "MQ发送消息线程工作");
                while (isRun) {
                    try {
                        Thread.sleep(20);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
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

                }

            } catch (JMSException e1) {
                e1.printStackTrace();
            }

        }

        /**
         * 发送文本消息
         *
         * @param entity
         */
        private void doTextSend(String entity, MsgRequest.SendCallback callback) {
            if (producer == null || entity == null || TextUtils.isEmpty(entity)) {
                return;
            }
            try {
                Map map = new HashMap();
                map.put("sourceAccount", MyApplication.TEL);
                map.put("message", entity.replace("\r\n", ""));
                TextMessage text = session.createTextMessage(gson.toJson(map));//session用来生产消息
                producer.send(dest, text);//MessageProducer用来发送消息
                if (callback != null) {
                    callback.onFinish();
                }

            } catch (JMSException e) {
                if (callback != null) {
                    Log.e(CommonConstant.LOGCAT_TAG, "MQ发送消息异常" + e.getMessage());
                    callback.onError(e.getMessage());
                }
            }
        }

    }

    public static class MsgReceiveHandler extends Thread implements MessageListener {
        private final Session session;
        private Destination dest;
        private boolean isRun = true;

        public void setRun(boolean run) {
            isRun = run;
        }

        public MsgReceiveHandler(Session session) {
            this.session = session;
            try {
                this.dest = this.session.createTopic(receiveStr);
            } catch (JMSException e) {
                e.printStackTrace();
            }
        }

        public void run() {
            try {
                MessageConsumer consumer = session.createConsumer(dest);
                consumer.setMessageListener(this);
            } catch (JMSException e1) {
                e1.printStackTrace();
            }
        }


        /**
         * 接收文本信息
         */
        @Override
        public void onMessage(Message msg) {
            TextMessage text = (TextMessage) msg;
            if (text != null) {
                try {
                    String s = text.getText();
                    if (!s.startsWith("{")) {
                        EventBus.getDefault().post(text.getText());
                    }
                } catch (JMSException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    public static class MsgReceiveHeartHandler extends Thread implements MessageListener {
        private final Session session;
        private Destination dest;
        private boolean isRun = true;

        public void setRun(boolean run) {
            isRun = run;
        }

        public MsgReceiveHeartHandler(Session session) {
            this.session = session;
            try {
                this.dest = session.createTopic(receiveHeartStr);
            } catch (JMSException e) {
                e.printStackTrace();
            }
        }

        public void run() {
            try {
                MessageConsumer consumer = session.createConsumer(dest);
                consumer.setMessageListener(this);
            } catch (JMSException e1) {
                e1.printStackTrace();
            }
        }


        /**
         * 接收文本信息
         */
        @Override
        public void onMessage(Message msg) {
            TextMessage text = (TextMessage) msg;
            if (text != null) {
                try {
                    String s = text.getText();
                    if (!s.startsWith("{"))
                        EventBus.getDefault().post(text.getText());
                } catch (JMSException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public interface MQConnectListener {
        void success();

        void failed();
    }

}

