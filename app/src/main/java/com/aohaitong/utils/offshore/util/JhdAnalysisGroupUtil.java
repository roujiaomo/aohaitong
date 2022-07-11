package com.aohaitong.utils.offshore.util;

import android.util.Log;

import com.aohaitong.business.transmit.BusinessController;
import com.aohaitong.business.transmit.SendController;
import com.aohaitong.constant.CommonConstant;
import com.aohaitong.constant.NumConstant;
import com.aohaitong.constant.StatusConstant;
import com.aohaitong.utils.base.Utils;
import com.aohaitong.utils.offshore.message.OffshoreCommunicationMessage26;
import com.aohaitong.utils.offshore.message.OffshoreCommunicationMessage27;
import com.aohaitong.utils.offshore.sentence.JHDSentence;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class JhdAnalysisGroupUtil {

    //发送手机号+序号+总包数为键，值中键为当前包数，值为OffshoreCommunicationMessage对像
    private static final Map<String, Map<Integer, OffshoreCommunicationMessage27>> info = new HashMap<>();
    //消息接收时间计时，发送手机号+序号+总包数为键，值为最新包接收时间戳
    private static final Map<String, Long> messageLastTimeMap = new HashMap<String, Long>();
    //发送手机号+序号+总包数为键，值为接收次数
    private static final Map<String, Integer> messageReceiveCountMap = new HashMap<String, Integer>();

    //拆包数据,键是接收手机号+序号，值中键是当前包，值为拆包对象
    private static final Map<String, Map<Integer, JHDSentence>> splitDataMap = new HashMap<>();
    //消息发送时间计时，发送手机号+序号为键，值为最新包接收时间戳
    private static final Map<String, Long> messageSendTimeMap = new HashMap<String, Long>();
    private static final Map<String, Long> messageReSendTimeMap = new HashMap<String, Long>();
    private static final Map<String, Integer> messageResendCountMap = new HashMap<String, Integer>();

    //收消息的时候，每收一包开始计时，4s间隔提醒补包
    private static final Long reSendTime = 4000L;
    //发文字消息，3次重发的时间
    private static final Long reSendTextTime = 3000L;
    //发消息，只有60s内没收到成功应答，默认超时
    private static final Long sendTimeOut = 60000L;

    /**
     * 接收好友消息(27号消息),每接到一包,27号消息调用一次
     * 同一消息ID的消息接收到的包数不全,且两秒内没接着新包时,则应答缺少的包数
     * 收全了通知
     */

    public static void addOffshoreCommunicationMessage(OffshoreCommunicationMessage27 offshoreCommunicationMessage27, boolean isNeedMessageReissue) {
        //序列号
        Integer sequenceNumger = offshoreCommunicationMessage27.getSequenceNumber();
        //发送手机号
        Long phoneNumber27 = offshoreCommunicationMessage27.getSourceAccount();
        //总包数
        Integer total = offshoreCommunicationMessage27.getTotal();
        //当前包数
        Integer current = offshoreCommunicationMessage27.getCurrent();
        //组成键
        String key = phoneNumber27 + "-" + sequenceNumger + "-" + total;

        // 存放当前时间 每次收到新的消息就清空对应key的时间
        if (isNeedMessageReissue) {
            messageLastTimeMap.put(key, new Date().getTime());
        }
        //存放到数组中 jhdInfo的每个元素为一条消息的其中一包
        Map<Integer, OffshoreCommunicationMessage27> jhdInfo = info.get(key);
        if (Utils.notEmpty(jhdInfo)) {
            jhdInfo.put(current, offshoreCommunicationMessage27);
        } else {
            jhdInfo = new HashMap<>();
            jhdInfo.put(current, offshoreCommunicationMessage27);
            info.put(key, jhdInfo);
        }

        //如果收全
        OffshoreCommunicationMessage27 totalMessage27 = null;
        //包收集全后，重新封装27号消息
        if (jhdInfo.size() == total) {
            Log.e(CommonConstant.LOGCAT_TAG, "27号消息接收完整, 总包数: " + jhdInfo.size());
            Integer sequenceNumber = null;
            Integer year = null;
            Integer month = null;
            Integer day = null;
            Integer hour = null;
            Integer minute = null;
            Integer second = null;
            Integer resFlag = null;
            Long sourceAccount = null;
            Long destinationAccount = null;
            Long groupId = null;
            Integer msgType = null;
            Integer spare = null;
            Integer recordTime = null;
            StringBuilder dataStr = new StringBuilder();
            for (int i = 1; i <= total; i++) {
                OffshoreCommunicationMessage27 message27 = jhdInfo.get(i);
                if (i == 1) {
                    sequenceNumber = message27.getSequenceNumber();
                    year = message27.getYear();
                    month = message27.getMonth();
                    day = message27.getDay();
                    hour = message27.getHour();
                    minute = message27.getMinute();
                    second = message27.getSecond();
                    resFlag = message27.getResFlag();
                    sourceAccount = message27.getSourceAccount();
                    destinationAccount = message27.getDestinationAccount();
                    groupId = message27.getGroupId();
                    msgType = message27.getMsgType();
                    spare = message27.getSpare();
                    recordTime = message27.getTimeLong();
                }
                dataStr.append(message27.getData());
            }
            totalMessage27 = new OffshoreCommunicationMessage27();
            totalMessage27.setSequenceNumber(sequenceNumber);
            totalMessage27.setYear(year);
            totalMessage27.setMonth(month);
            totalMessage27.setDay(day);
            totalMessage27.setHour(hour);
            totalMessage27.setMinute(minute);
            totalMessage27.setSecond(second);
            totalMessage27.setResFlag(resFlag);
            totalMessage27.setSourceAccount(sourceAccount);
            totalMessage27.setDestinationAccount(destinationAccount);
            totalMessage27.setGroupId(groupId);
            totalMessage27.setMsgType(msgType);
            totalMessage27.setTotal(1);
            totalMessage27.setCurrent(1);
            totalMessage27.setSpare(spare);
            totalMessage27.setData(dataStr.toString());
            totalMessage27.setTimeLong(recordTime);
            //应答27号消息接收成功
            SendController.getInstance().handleReceiveGroupChatMessage(totalMessage27);
            //清空缓存11
            info.remove(key);
            messageLastTimeMap.remove(key);
            messageReceiveCountMap.remove(key);
        }
    }

    /**
     * 接收数据后,时间处理,开启线程每隔1s循环一次
     * 接收到同一数据id的消息,判断与上次的时间是否相差两秒
     * 潜在问题:频繁收到消息
     */
    public static void messageAnalysis() {
        //遍历处理
        for (String key : messageLastTimeMap.keySet()) {
            //最后一次消息接收时间
            Long lastTime = messageLastTimeMap.get(key);
            //几秒内未收到最新消息
            if (lastTime != null && new Date().getTime() - lastTime > reSendTime) {
                Log.e(CommonConstant.LOGCAT_TAG, "4s未收到key为" + key + " 的新包");
                //获取重传次数
                Integer count = messageReceiveCountMap.get(key);
                Log.e(CommonConstant.LOGCAT_TAG, "重传次数：" + count);
                if (Utils.isEmpty(count)) {
                    count = 1;
                } else {
                    count++;
                }
                //重传次数保存
                messageReceiveCountMap.put(key, count);

                //结果处理
                Map<Integer, OffshoreCommunicationMessage27> jhdInfo = info.get(key);
                if (jhdInfo != null) {
                    OffshoreCommunicationMessage27 offshoreCommunicationMessage27 = null;
                    //少于三次，返回未收到包的序号
                    if (count < 3) {
                        Log.e(CommonConstant.LOGCAT_TAG, "第" + count + "次重传");
                        StringBuilder rel = new StringBuilder();
                        int total = Integer.parseInt(key.split("-")[2]);
                        for (int i = 1; i <= total; i++) {
                            if (Utils.isEmpty(jhdInfo.get(i))) {
                                rel.append(i).append(",");
                            } else {
                                if (Utils.isEmpty(offshoreCommunicationMessage27)) {
                                    try {
                                        offshoreCommunicationMessage27 = (OffshoreCommunicationMessage27) jhdInfo.get(i).clone();
                                    } catch (CloneNotSupportedException e) {
                                        e.printStackTrace();
                                        offshoreCommunicationMessage27 = jhdInfo.get(i);
                                    }
                                }
                            }
                        }
                        rel.deleteCharAt(rel.length() - 1);//清空最后一个逗号
                        offshoreCommunicationMessage27.setData("1/" + rel.toString());
                        // 存放当前时间
                        messageLastTimeMap.put(key, new Date().getTime());
                        // 应答缺少的包
                        Log.e(CommonConstant.LOGCAT_TAG, "发送需要补包应答");
                        Log.e(CommonConstant.LOGCAT_TAG, "发送需要补包数据: " + offshoreCommunicationMessage27.getData());

                        BusinessController.sendGroupMsgReceiveAnswer(false, offshoreCommunicationMessage27,
                                null, offshoreCommunicationMessage27.getSequenceNumber());
                    } else {
                        //已重传三次，默认该消息发送失败,应答接收消息失败,清空该消息时间遍历的map
                        Log.e(CommonConstant.LOGCAT_TAG, "重传三次，结束重传");
                        for (Integer keyInt : jhdInfo.keySet()) {
                            offshoreCommunicationMessage27 = jhdInfo.get(keyInt);
                            break;
                        }
                        offshoreCommunicationMessage27.setData("1/0");
                        //应答失败
                        BusinessController.sendGroupMsgReceiveAnswer(false, offshoreCommunicationMessage27,
                                null, offshoreCommunicationMessage27.getSequenceNumber());
                        //清空缓存
                        info.remove(key);
                        messageLastTimeMap.remove(key);
                        messageReceiveCountMap.remove(key);
                    }
                }

            }
        }

    }

    /**
     * 给好友发送消息后，收到应答需要补发包
     * 当总计时15s超时后，即使调用也无逻辑
     *
     * @param offshoreCommunicationMessage26
     */
    public static void messageReissue(OffshoreCommunicationMessage26 offshoreCommunicationMessage26) {
        Log.e(CommonConstant.LOGCAT_TAG, "补包开始");
        Long destinationAccount = offshoreCommunicationMessage26.getDestinationAccount();
        Integer sequenceNumber = offshoreCommunicationMessage26.getSequenceNumber();
        String data = offshoreCommunicationMessage26.getData();
        String key = destinationAccount + "-" + sequenceNumber;
        String[] reissues = data.split("/");
        if ("1".equals(reissues[0])) {
            if ("0".equals(reissues[1])) {
                splitDataMap.remove(key);
            } else {
                //获取缺少的包 包序号的数组
                String[] reissueIndex = reissues[1].split(",");
                Map<Integer, JHDSentence> splitData = splitDataMap.get(key);
                Log.e(CommonConstant.LOGCAT_TAG, "补包的序号: " + Arrays.toString(reissueIndex));
                //如果还在15s内、未移除缓存的拆包发送数据
                Log.e(CommonConstant.LOGCAT_TAG, "补包总数: " + reissueIndex.length);
                if (splitData != null) {
                    for (String index : reissueIndex) {
                        //未发送的包
                        JHDSentence jhdSentence = splitData.get(Integer.parseInt(index));
                        if (jhdSentence != null) {
                            try {
                                //补发包也不触发回调lister的逻辑
//                                Log.d(CommonConstant.LOGCAT_TAG, "补包序号:" + index);
                                SendController.getInstance().sendMsg(null,
                                        CommVdesMessageUtil.messageJHD(jhdSentence),
                                        jhdSentence.getOffshoreCommunicationMessage().getSequenceNumber(), NumConstant.COMMON_FAIL_TIME);
                            } catch (Exception e) {
                                Log.e(CommonConstant.LOGCAT_TAG, "补包异常" + e.getMessage());
                                e.printStackTrace();
                            }
                        }
                    }
                    Log.e(CommonConstant.LOGCAT_TAG, "补包完成");
                }

            }
        }
    }

    /**
     * 收到26号消息成功应答
     *
     * @param offshoreCommunicationMessage26
     */
    public static void removeSplitMessage(OffshoreCommunicationMessage26 offshoreCommunicationMessage26) {
        Long destinationAccount = offshoreCommunicationMessage26.getDestinationAccount();
        Integer sequenceNumber = offshoreCommunicationMessage26.getSequenceNumber();
        String key = destinationAccount + "-" + sequenceNumber;
        messageSendTimeMap.remove(key);
        messageReSendTimeMap.remove(key);
        splitDataMap.remove(key);
    }

    /**
     * 消息计时
     * 多少秒之内中心仍未收全包
     */
    public static void messageTimeOut() {
        //遍历处理
        for (String key : messageSendTimeMap.keySet()) {
            //获取消息发送时间
            Long lastTime = messageSendTimeMap.get(key);
            //消息发送超过60s，发送超时
            if (new Date().getTime() - lastTime > sendTimeOut) {
                //消除发送消息
                messageSendTimeMap.remove(key);
                splitDataMap.remove(key);
            }
        }
    }

    /**
     * 拆包的时候，也将拆包数据缓存了一份，用于补发包用
     *
     * @param offshoreCommunicationMessage26
     * @return key:存入的消息语句整体的key
     */
    public static String messageSplit(OffshoreCommunicationMessage26 offshoreCommunicationMessage26) {
        String messageData = offshoreCommunicationMessage26.getData();
        Long sourceAccount = offshoreCommunicationMessage26.getSourceAccount();
        Integer sequenceNumber = offshoreCommunicationMessage26.getSequenceNumber();
        String key = sourceAccount + "-" + sequenceNumber;
        //每包最大600byte，但加密串是16的倍數 通过加密算法，如果等于592byte，加密后会变成608byte，每包长度需减一，592-1=591byte。
        // 数据格式前缀包括24byte，每包最大长度为591-24=567byte
        int total = (messageData.length() % 567 == 0) ? messageData.length() / 567 : messageData.length() / 567 + 1;
        Map<Integer, JHDSentence> sendDTOMap = new HashMap<>();
        for (int i = 1; i <= total; i++) {
            // 发送近海通信26号消息
            OffshoreCommunicationMessage26 sendOffshoreCommunicationMessage26 = new OffshoreCommunicationMessage26();
            sendOffshoreCommunicationMessage26.setMsgId(26);
            sendOffshoreCommunicationMessage26.setSequenceNumber(offshoreCommunicationMessage26.getSequenceNumber());
            sendOffshoreCommunicationMessage26.setYear(offshoreCommunicationMessage26.getYear());
            sendOffshoreCommunicationMessage26.setMonth(offshoreCommunicationMessage26.getMonth());
            sendOffshoreCommunicationMessage26.setDay(offshoreCommunicationMessage26.getDay());
            sendOffshoreCommunicationMessage26.setHour(offshoreCommunicationMessage26.getHour());
            sendOffshoreCommunicationMessage26.setMinute(offshoreCommunicationMessage26.getMinute());
            sendOffshoreCommunicationMessage26.setSecond(offshoreCommunicationMessage26.getSecond());
            sendOffshoreCommunicationMessage26.setResFlag(0);
            sendOffshoreCommunicationMessage26.setGroupId(offshoreCommunicationMessage26.getGroupId());
            sendOffshoreCommunicationMessage26.setSourceAccount(offshoreCommunicationMessage26.getSourceAccount());
            sendOffshoreCommunicationMessage26.setDestinationAccount(offshoreCommunicationMessage26.getDestinationAccount());
            sendOffshoreCommunicationMessage26.setMsgType(offshoreCommunicationMessage26.getMsgType());
            sendOffshoreCommunicationMessage26.setTotal(total);
            sendOffshoreCommunicationMessage26.setCurrent(i);
            sendOffshoreCommunicationMessage26.setTimeLong(offshoreCommunicationMessage26.getTimeLong());
            sendOffshoreCommunicationMessage26.setSpare(6);
            String dataStr = messageData.substring((i - 1) * 567, Math.min(((i - 1) * 567 + 567), messageData.length()));
            sendOffshoreCommunicationMessage26.setData(dataStr);
            JHDSentence jhdSentence26 = new JHDSentence();
            jhdSentence26.setOffshoreCommunicationMessage(sendOffshoreCommunicationMessage26);
            sendDTOMap.put(i, jhdSentence26);
        }
        Integer msgType = offshoreCommunicationMessage26.getMsgType();
        //消息发送计时开始
        long currentTime = new Date().getTime();
        messageSendTimeMap.put(key, currentTime);
        splitDataMap.put(key, sendDTOMap);
        //文字消息加入重连校验
        if (msgType == StatusConstant.TYPE_TEXT_MESSAGE) {
            messageReSendTimeMap.put(key, currentTime);
        }
        return key;
    }

    /**
     * 接收数据后,时间处理,开启线程每隔1s循环一次
     * 接收到同一数据id的消息,判断与上次的时间是否相差两秒
     * 潜在问题:频繁收到消息
     */
    public static void messageResend() {
        for (String key : messageReSendTimeMap.keySet()) {
            //只有文字消息会手动重发
            Map<Integer, JHDSentence> splitData = splitDataMap.get(key);
            OffshoreCommunicationMessage26 offshoreCommunicationMessage =
                    (OffshoreCommunicationMessage26) splitData.get(1).getOffshoreCommunicationMessage();
            if (offshoreCommunicationMessage.getMsgType() == StatusConstant.TYPE_RECORD_MESSAGE) {
                continue;
            }
            //最后一次消息接收时间
            Long lastTime = messageReSendTimeMap.get(key);
            //2秒内未收到最新消息
            if (lastTime != null && new Date().getTime() - lastTime > reSendTextTime) {
                //获取重传次数
                Integer count = messageResendCountMap.get(key);
                if (Utils.isEmpty(count)) {
                    count = 1;
                } else {
                    count++;
                }
                Log.e(CommonConstant.LOGCAT_TAG, "船端文字消息重发: " + count);
                //重传次数保存
                messageResendCountMap.put(key, count);
                //少于三次，重发该消息
                if (count < 3) {
                    int num = NumConstant.getJHDNum();
                    JHDSentence jhdSentence = new JHDSentence();
                    jhdSentence.setOffshoreCommunicationMessage(offshoreCommunicationMessage);
                    try {
                        SendController.getInstance().sendMsg(null,
                                CommVdesMessageUtil.messageJHD(jhdSentence), num, NumConstant.COMMON_FAIL_TIME);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    // 存放当前时间
                    messageReSendTimeMap.put(key, new Date().getTime());
                } else {
                    //清空重传缓存，等待60秒超时
                    messageResendCountMap.remove(key);
                    messageReSendTimeMap.remove(key);
                }
            }
        }
    }


    public static Map<String, Map<Integer, JHDSentence>> getSplitDataMap() {
        return splitDataMap;
    }
}
	
