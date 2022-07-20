package com.aohaitong.utils.offshore.util;


import android.util.Log;

import com.aohaitong.constant.CommonConstant;
import com.aohaitong.utils.aes.AesEncryptDecrypt;
import com.aohaitong.utils.base.Utils;
import com.aohaitong.utils.binary.BinArray;
import com.aohaitong.utils.binary.SixbitEncoder;
import com.aohaitong.utils.binary.SixbitException;
import com.aohaitong.utils.offshore.Exception.MessageLengthException;
import com.aohaitong.utils.offshore.Exception.MessageNoException;
import com.aohaitong.utils.offshore.Exception.MessageVerificationException;
import com.aohaitong.utils.offshore.message.OffshoreCommunicationAccountMessage;
import com.aohaitong.utils.offshore.message.OffshoreCommunicationMessage;
import com.aohaitong.utils.offshore.message.OffshoreCommunicationMessage01;
import com.aohaitong.utils.offshore.message.OffshoreCommunicationMessage02;
import com.aohaitong.utils.offshore.message.OffshoreCommunicationMessage03;
import com.aohaitong.utils.offshore.message.OffshoreCommunicationMessage04;
import com.aohaitong.utils.offshore.message.OffshoreCommunicationMessage05;
import com.aohaitong.utils.offshore.message.OffshoreCommunicationMessage06;
import com.aohaitong.utils.offshore.message.OffshoreCommunicationMessage07;
import com.aohaitong.utils.offshore.message.OffshoreCommunicationMessage08;
import com.aohaitong.utils.offshore.message.OffshoreCommunicationMessage09;
import com.aohaitong.utils.offshore.message.OffshoreCommunicationMessage10;
import com.aohaitong.utils.offshore.message.OffshoreCommunicationMessage11;
import com.aohaitong.utils.offshore.message.OffshoreCommunicationMessage12;
import com.aohaitong.utils.offshore.message.OffshoreCommunicationMessage13;
import com.aohaitong.utils.offshore.message.OffshoreCommunicationMessage14;
import com.aohaitong.utils.offshore.message.OffshoreCommunicationMessage15;
import com.aohaitong.utils.offshore.message.OffshoreCommunicationMessage16;
import com.aohaitong.utils.offshore.message.OffshoreCommunicationMessage17;
import com.aohaitong.utils.offshore.message.OffshoreCommunicationMessage18;
import com.aohaitong.utils.offshore.message.OffshoreCommunicationMessage19;
import com.aohaitong.utils.offshore.message.OffshoreCommunicationMessage20;
import com.aohaitong.utils.offshore.message.OffshoreCommunicationMessage21;
import com.aohaitong.utils.offshore.message.OffshoreCommunicationMessage22;
import com.aohaitong.utils.offshore.message.OffshoreCommunicationMessage23;
import com.aohaitong.utils.offshore.message.OffshoreCommunicationMessage24;
import com.aohaitong.utils.offshore.message.OffshoreCommunicationMessage25;
import com.aohaitong.utils.offshore.message.OffshoreCommunicationMessage26;
import com.aohaitong.utils.offshore.message.OffshoreCommunicationMessage27;
import com.aohaitong.utils.offshore.message.OffshoreCommunicationMessage28;
import com.aohaitong.utils.offshore.message.OffshoreCommunicationMessage29;
import com.aohaitong.utils.offshore.message.OffshoreCommunicationMessage30;
import com.aohaitong.utils.offshore.message.OffshoreCommunicationMessage31;
import com.aohaitong.utils.offshore.message.OffshoreCommunicationMessage32;
import com.aohaitong.utils.offshore.message.OffshoreCommunicationMessage33;
import com.aohaitong.utils.offshore.message.OffshoreCommunicationMessage34;
import com.aohaitong.utils.offshore.message.OffshoreCommunicationMessage35;
import com.aohaitong.utils.offshore.sentence.BHMSentence;
import com.aohaitong.utils.offshore.sentence.JHDSentence;

/**
 * @ClassName: MessageUtil <br>
 * 用于合成各种报文以及解析
 */
public class CommVdesMessageUtil {
    private static final String comma = ",";

    //BHM报文编号
    private static int bhmNumber = 0;

    private static int getBhmNumber() {
        if (bhmNumber >= 10000) {
            bhmNumber = 0;
        }
        return (bhmNumber++) % 100;
    }

    /**
     * 空字符串处理
     *
     * @return
     */
    public static String dealEmptyStr(String data) {
        return Utils.isEmpty(data) ? "" : data;
    }

    /**
     * 空Long处理
     *
     * @param data
     * @return
     */
    public static String dealEmptyLong(Long data) {
        return data == null ? "" : data.toString();
    }

    /**
     * 空Integer处理
     *
     * @param data
     * @return
     */
    public static String dealEmptyInteger(Integer data) {
        return data == null ? "" : data.toString();
    }

    /**
     * 空Double处理
     *
     * @param data
     * @return
     */
    public static String dealEmptyDouble(Double data) {
        return data == null ? "" : data.toString();
    }

    /**
     * 处理空对象
     *
     * @param obj
     * @return
     */
    public static String dealEmptyObject(Object obj) {

        if (obj instanceof String) {
            return dealEmptyStr((String) obj);
        } else if (obj instanceof Long) {
            return dealEmptyLong((Long) obj);
        } else if (obj instanceof Integer) {
            return dealEmptyInteger((Integer) obj);
        } else if (obj instanceof Double) {
            return dealEmptyDouble((Double) obj);
        }
        return "";
    }

    /*
     *
     * 近海通信报文封装
     *
     */
    private static String offshoreCommunicationMessage(OffshoreCommunicationMessage offshoreCommunicationMessage) throws Exception {
        String message = "";
        Integer msgId = offshoreCommunicationMessage.getMsgId();
        SixbitEncoder encoder = new SixbitEncoder();

        switch (msgId) {
            //1、2、3号报文
            case 1:
                message = offshoreCommunicationMessage01(encoder, (OffshoreCommunicationMessage01) offshoreCommunicationMessage);
                break;
            case 2:
                message = offshoreCommunicationMessage02(encoder, (OffshoreCommunicationMessage02) offshoreCommunicationMessage);
                break;
            case 3:
                message = offshoreCommunicationMessage03(encoder, (OffshoreCommunicationMessage03) offshoreCommunicationMessage);
                break;
            case 4:
                message = offshoreCommunicationMessage04(encoder, (OffshoreCommunicationMessage04) offshoreCommunicationMessage);
                break;
            case 5:
                message = offshoreCommunicationMessage05(encoder, (OffshoreCommunicationMessage05) offshoreCommunicationMessage);
                break;
            case 6:
                message = offshoreCommunicationMessage06(encoder, (OffshoreCommunicationMessage06) offshoreCommunicationMessage);
                break;
            case 7:
                message = offshoreCommunicationMessage07(encoder, (OffshoreCommunicationMessage07) offshoreCommunicationMessage);
                break;
            case 8:
                message = offshoreCommunicationMessage08(encoder, (OffshoreCommunicationMessage08) offshoreCommunicationMessage);
                break;
            case 9:
                message = offshoreCommunicationMessage09(encoder, (OffshoreCommunicationMessage09) offshoreCommunicationMessage);
                break;
            case 10:
                message = offshoreCommunicationMessage10(encoder, (OffshoreCommunicationMessage10) offshoreCommunicationMessage);
                break;
            case 11:
                message = offshoreCommunicationMessage11(encoder, (OffshoreCommunicationMessage11) offshoreCommunicationMessage);
                break;
            case 12:
                message = offshoreCommunicationMessage12(encoder, (OffshoreCommunicationMessage12) offshoreCommunicationMessage);
                break;
            case 13:
                message = offshoreCommunicationMessage13(encoder, (OffshoreCommunicationMessage13) offshoreCommunicationMessage);
                break;
            case 14:
                message = offshoreCommunicationMessage14(encoder, (OffshoreCommunicationMessage14) offshoreCommunicationMessage);
                break;
            case 15:
                message = offshoreCommunicationMessage15(encoder, (OffshoreCommunicationMessage15) offshoreCommunicationMessage);
                break;
            case 16:
                message = offshoreCommunicationMessage16(encoder, (OffshoreCommunicationMessage16) offshoreCommunicationMessage);
                break;
            case 17:
                message = offshoreCommunicationMessage17(encoder, (OffshoreCommunicationMessage17) offshoreCommunicationMessage);
                break;
            case 18:
                message = offshoreCommunicationMessage18(encoder, (OffshoreCommunicationMessage18) offshoreCommunicationMessage);
                break;
            case 19:
                message = offshoreCommunicationMessage19(encoder, (OffshoreCommunicationMessage19) offshoreCommunicationMessage);
                break;
            case 20:
                message = offshoreCommunicationMessage20(encoder, (OffshoreCommunicationMessage20) offshoreCommunicationMessage);
                break;
            case 21:
                message = offshoreCommunicationMessage21(encoder, (OffshoreCommunicationMessage21) offshoreCommunicationMessage);
                break;
            case 22:
                message = offshoreCommunicationMessage22(encoder, (OffshoreCommunicationMessage22) offshoreCommunicationMessage);
                break;
            case 23:
                message = offshoreCommunicationMessage23(encoder, (OffshoreCommunicationMessage23) offshoreCommunicationMessage);
                break;
            case 24:
                message = offshoreCommunicationMessage24(encoder, (OffshoreCommunicationMessage24) offshoreCommunicationMessage);
                break;
            case 25:
                message = offshoreCommunicationMessage25(encoder, (OffshoreCommunicationMessage25) offshoreCommunicationMessage);
                break;
            case 26:
                message = offshoreCommunicationMessage26(encoder, (OffshoreCommunicationMessage26) offshoreCommunicationMessage);
                break;
            case 27:
                message = offshoreCommunicationMessage27(encoder, (OffshoreCommunicationMessage27) offshoreCommunicationMessage);
                break;
            case 28:
                message = offshoreCommunicationMessage28(encoder, (OffshoreCommunicationMessage28) offshoreCommunicationMessage);
                break;
            case 29:
                message = offshoreCommunicationMessage29(encoder, (OffshoreCommunicationMessage29) offshoreCommunicationMessage);
                break;
            case 30:
                message = offshoreCommunicationMessage30(encoder, (OffshoreCommunicationMessage30) offshoreCommunicationMessage);
                break;
            case 31:
                message = offshoreCommunicationMessage31(encoder, (OffshoreCommunicationMessage31) offshoreCommunicationMessage);
                break;
            case 32:
                message = offshoreCommunicationMessage32(encoder, (OffshoreCommunicationMessage32) offshoreCommunicationMessage);
                break;
            case 33:
                message = offshoreCommunicationMessage33(encoder, (OffshoreCommunicationMessage33) offshoreCommunicationMessage);
                break;
            case 34:
                message = offshoreCommunicationMessage34(encoder, (OffshoreCommunicationMessage34) offshoreCommunicationMessage);
                break;
            case 35:
                message = offshoreCommunicationMessage35(encoder, (OffshoreCommunicationMessage35) offshoreCommunicationMessage);
                break;
        }
        return message;
    }

    private static void offshoreCommunicationMessage(SixbitEncoder encoder, OffshoreCommunicationMessage offshoreCommunicationMessage) {
        //消息ID
        encoder.addVal(offshoreCommunicationMessage.getMsgId(), 6);
        //序号
        encoder.addVal(offshoreCommunicationMessage.getSequenceNumber(), 8);
        //年
        encoder.addVal(offshoreCommunicationMessage.getYear(), 11);
        //月
        encoder.addVal(offshoreCommunicationMessage.getMonth(), 4);
        //日
        encoder.addVal(offshoreCommunicationMessage.getDay(), 5);
        //时
        encoder.addVal(offshoreCommunicationMessage.getHour(), 5);
        //分
        encoder.addVal(offshoreCommunicationMessage.getMinute(), 6);
        //秒
        encoder.addVal(offshoreCommunicationMessage.getSecond(), 6);
        //应答标志
        encoder.addVal(offshoreCommunicationMessage.getResFlag(), 1);
    }

    private static void offshoreCommunicationAccountMessage(SixbitEncoder encoder, OffshoreCommunicationAccountMessage offshoreCommunicationAccountMessage) {

        offshoreCommunicationMessage(encoder, offshoreCommunicationAccountMessage);
        //源APP账号
        encoder.addVal(offshoreCommunicationAccountMessage.getSourceAccount(), 36);
        //目的APP账号
        encoder.addVal(offshoreCommunicationAccountMessage.getDestinationAccount(), 36);
    }

    /*
     *
     * 近海通信 1号报文封装
     *
     */
    private static String offshoreCommunicationMessage01(SixbitEncoder encoder, OffshoreCommunicationMessage01 message1) throws Exception {
        offshoreCommunicationAccountMessage(encoder, message1);
        //备用
        encoder.addVal(message1.getSpare(), 4);
        //二进制数据
        encoder.addDataString(message1.getData());
//                .addUTF8DataString(message1.getData());

        //16进制字符串
        String data = encoder.encode4bit();

        String dataEncrypt = AesEncryptDecrypt.encryptHexStrByHexStr(data);
        return dataEncrypt;
    }

    /*
     *
     * 近海通信 2号报文封装
     *
     */
    private static String offshoreCommunicationMessage02(SixbitEncoder encoder, OffshoreCommunicationMessage02 message2) throws Exception {
        offshoreCommunicationAccountMessage(encoder, message2);
        //备用
        encoder.addVal(message2.getSpare(), 4);
        //二进制数据
        encoder.addUTF8DataString(message2.getData());

        //16进制字符串
        String data = encoder.encode4bit();

        String dataEncrypt = AesEncryptDecrypt.encryptHexStrByHexStr(data);
        return dataEncrypt;
    }

    /*
     *
     * 近海通信 3号报文封装
     *
     */
    private static String offshoreCommunicationMessage03(SixbitEncoder encoder, OffshoreCommunicationMessage03 message3) throws Exception {
        offshoreCommunicationAccountMessage(encoder, message3);
        //备用
        encoder.addVal(message3.getSpare(), 4);
        //二进制数据
        encoder.addUTF8DataString(message3.getData());

        //16进制字符串
        String data = encoder.encode4bit();

        String dataEncrypt = AesEncryptDecrypt.encryptHexStrByHexStr(data);
        return dataEncrypt;
    }

    /*
     *
     * 近海通信 4号报文封装
     *
     */
    private static String offshoreCommunicationMessage04(SixbitEncoder encoder, OffshoreCommunicationMessage04 message4) throws Exception {
        offshoreCommunicationAccountMessage(encoder, message4);
        //备用
        encoder.addVal(message4.getSpare(), 4);
        //二进制数据
        encoder.addUTF8DataString(message4.getData());

        //16进制字符串
        String data = encoder.encode4bit();

        String dataEncrypt = AesEncryptDecrypt.encryptHexStrByHexStr(data);
        return dataEncrypt;
    }

    /*
     *
     * 近海通信 5号报文封装
     *
     */
    private static String offshoreCommunicationMessage05(SixbitEncoder encoder, OffshoreCommunicationMessage05 message5) throws Exception {
        offshoreCommunicationAccountMessage(encoder, message5);
        //备用
        encoder.addVal(message5.getSpare(), 4);
        //二进制数据
        encoder.addUTF8DataString(message5.getData());

        //16进制字符串
        String data = encoder.encode4bit();

        String dataEncrypt = AesEncryptDecrypt.encryptHexStrByHexStr(data);
        return dataEncrypt;
    }

    /*
     *
     * 近海通信 6号报文封装
     *
     */
    private static String offshoreCommunicationMessage06(SixbitEncoder encoder, OffshoreCommunicationMessage06 message6) throws Exception {
        offshoreCommunicationAccountMessage(encoder, message6);
        //备用
        encoder.addVal(message6.getSpare(), 4);
        //源密码
        encoder.addDataString(Utils.fillStr(message6.getOldPassword(), 16, " "));
        //二进制数据
        encoder.addUTF8DataString(message6.getData());

        //16进制字符串
        String data = encoder.encode4bit();

        String dataEncrypt = AesEncryptDecrypt.encryptHexStrByHexStr(data);
        return dataEncrypt;
    }

    /*
     *
     * 近海通信 7号报文封装
     *
     */
    private static String offshoreCommunicationMessage07(SixbitEncoder encoder, OffshoreCommunicationMessage07 message7) throws Exception {
        offshoreCommunicationAccountMessage(encoder, message7);
        //备用
        encoder.addVal(message7.getSpare(), 4);
        //二进制数据
        encoder.addUTF8DataString(message7.getData());

        //16进制字符串
        String data = encoder.encode4bit();

        String dataEncrypt = AesEncryptDecrypt.encryptHexStrByHexStr(data);
        return dataEncrypt;
    }

    /*
     *
     * 近海通信 8号报文封装
     *
     */
    private static String offshoreCommunicationMessage08(SixbitEncoder encoder, OffshoreCommunicationMessage08 message8) throws Exception {
        offshoreCommunicationAccountMessage(encoder, message8);
        //备用
        encoder.addVal(message8.getSpare(), 4);
        //二进制数据
        encoder.addUTF8DataString(message8.getData());

        //16进制字符串
        String data = encoder.encode4bit();

        String dataEncrypt = AesEncryptDecrypt.encryptHexStrByHexStr(data);
        return dataEncrypt;
    }

    /*
     *
     * 近海通信 9号报文封装
     *
     */
    private static String offshoreCommunicationMessage09(SixbitEncoder encoder, OffshoreCommunicationMessage09 message9) throws Exception {
        offshoreCommunicationAccountMessage(encoder, message9);
        //备用
        encoder.addVal(message9.getSpare(), 4);
        //二进制数据
        encoder.addUTF8DataString(message9.getData());

        //16进制字符串
        String data = encoder.encode4bit();

        String dataEncrypt = AesEncryptDecrypt.encryptHexStrByHexStr(data);
        return dataEncrypt;
    }

    /*
     *
     * 近海通信 10号报文封装
     *
     */
    private static String offshoreCommunicationMessage10(SixbitEncoder encoder, OffshoreCommunicationMessage10 message10) throws Exception {
        offshoreCommunicationAccountMessage(encoder, message10);
        //备用
        encoder.addVal(message10.getSpare(), 4);
        //二进制数据
        encoder.addUTF8DataString(message10.getData());

        //16进制字符串
        String data = encoder.encode4bit();

        String dataEncrypt = AesEncryptDecrypt.encryptHexStrByHexStr(data);
        return dataEncrypt;
    }

    /*
     *
     * 近海通信 11号报文封装
     *
     */
    private static String offshoreCommunicationMessage11(SixbitEncoder encoder, OffshoreCommunicationMessage11 message11) throws Exception {
        offshoreCommunicationAccountMessage(encoder, message11);
        //备用
        encoder.addVal(message11.getSpare(), 4);
        //二进制数据
        encoder.addUTF8DataString(message11.getData());

        //16进制字符串
        String data = encoder.encode4bit();

        String dataEncrypt = AesEncryptDecrypt.encryptHexStrByHexStr(data);
        return dataEncrypt;
    }

    /*
     *
     * 近海通信 12号报文封装
     *
     */
    private static String offshoreCommunicationMessage12(SixbitEncoder encoder, OffshoreCommunicationMessage12 message12) throws Exception {
        offshoreCommunicationAccountMessage(encoder, message12);
        //发起删除好友请求APP账号
        encoder.addVal(message12.getSpare(), 4);
        //二进制数据
        encoder.addUTF8DataString(message12.getData());

        //16进制字符串
        String data = encoder.encode4bit();

        String dataEncrypt = AesEncryptDecrypt.encryptHexStrByHexStr(data);
        return dataEncrypt;
    }

    /*
     *
     * 近海通信 13号报文封装
     *
     */
    private static String offshoreCommunicationMessage13(SixbitEncoder encoder, OffshoreCommunicationMessage13 message13) throws Exception {
        offshoreCommunicationAccountMessage(encoder, message13);
        //被修改备注APP账号
        encoder.addVal(message13.getFriendAccount(), 36);
        //二进制数据
        encoder.addUTF8DataString(message13.getData());

        //16进制字符串
        String data = encoder.encode4bit();

        String dataEncrypt = AesEncryptDecrypt.encryptHexStrByHexStr(data);
        return dataEncrypt;
    }

    /*
     *
     * 近海通信 14号报文封装
     *
     */
    private static String offshoreCommunicationMessage14(SixbitEncoder encoder, OffshoreCommunicationMessage14 message14) throws Exception {
        offshoreCommunicationAccountMessage(encoder, message14);
        //群组ID
        encoder.addVal(message14.getGroupId(), 34);
        //群组名称
        encoder.addUTF8DataStringForLength(message14.getGroupName(), 256);
        //备用
        encoder.addVal(message14.getSpare(), 2);
        //二进制数据
        encoder.addUTF8DataString(message14.getData());

        //16进制字符串
        String data = encoder.encode4bit();

        String dataEncrypt = AesEncryptDecrypt.encryptHexStrByHexStr(data);
        return dataEncrypt;
    }

    /*
     *
     * 近海通信 15号报文封装
     *
     */
    private static String offshoreCommunicationMessage15(SixbitEncoder encoder, OffshoreCommunicationMessage15 message15) throws Exception {
        offshoreCommunicationAccountMessage(encoder, message15);
        //群组ID
        encoder.addVal(message15.getGroupId(), 34);
        //备用
        encoder.addVal(message15.getSpare(), 2);
        //二进制数据
        encoder.addUTF8DataString(message15.getData());

        //16进制字符串
        String data = encoder.encode4bit();

        String dataEncrypt = AesEncryptDecrypt.encryptHexStrByHexStr(data);
        return dataEncrypt;
    }

    /*
     *
     * 近海通信 16号报文封装
     *
     */
    private static String offshoreCommunicationMessage16(SixbitEncoder encoder, OffshoreCommunicationMessage16 message16) throws Exception {
        offshoreCommunicationAccountMessage(encoder, message16);
        //发起拉人请求APP账号
        encoder.addVal(message16.getFriendAccount(), 36);
        //群组ID
        encoder.addVal(message16.getGroupId(), 34);
        //群组名称
        encoder.addUTF8DataStringForLength(message16.getGroupName(), 256);
        //备用
        encoder.addVal(message16.getSpare(), 6);
        //二进制数据
        encoder.addUTF8DataString(message16.getData());

        //16进制字符串
        String data = encoder.encode4bit();

        String dataEncrypt = AesEncryptDecrypt.encryptHexStrByHexStr(data);
        return dataEncrypt;
    }

    /*
     *
     * 近海通信 17号报文封装
     *
     */
    private static String offshoreCommunicationMessage17(SixbitEncoder encoder, OffshoreCommunicationMessage17 message17) throws Exception {
        offshoreCommunicationAccountMessage(encoder, message17);
        //发起拉人请求APP账号
        encoder.addVal(message17.getFriendAccount(), 36);
        //群组ID
        encoder.addVal(message17.getGroupId(), 34);
        //备用
        encoder.addVal(message17.getSpare(), 6);
        //二进制数据
        encoder.addUTF8DataString(message17.getData());

        //16进制字符串
        String data = encoder.encode4bit();

        String dataEncrypt = AesEncryptDecrypt.encryptHexStrByHexStr(data);
        return dataEncrypt;
    }

    /*
     *
     * 近海通信 18号报文封装
     *
     */
    private static String offshoreCommunicationMessage18(SixbitEncoder encoder, OffshoreCommunicationMessage18 message18) throws Exception {
        offshoreCommunicationAccountMessage(encoder, message18);
        //群组ID
        encoder.addVal(message18.getGroupId(), 34);
        //备用
        encoder.addVal(message18.getSpare(), 2);
        //二进制数据
        encoder.addUTF8DataString(message18.getData());

        //16进制字符串
        String data = encoder.encode4bit();

        String dataEncrypt = AesEncryptDecrypt.encryptHexStrByHexStr(data);
        return dataEncrypt;
    }

    /*
     *
     * 近海通信 19号报文封装
     *
     */
    private static String offshoreCommunicationMessage19(SixbitEncoder encoder, OffshoreCommunicationMessage19 message19) throws Exception {
        offshoreCommunicationAccountMessage(encoder, message19);
        //发起移除申请APP账号
        encoder.addVal(message19.getFriendAccount(), 36);
        //组ID
        encoder.addVal(message19.getGroupId(), 34);
        //备用
        encoder.addVal(message19.getSpare(), 6);
        //二进制数据
        encoder.addUTF8DataString(message19.getData());

        //16进制字符串
        String data = encoder.encode4bit();

        String dataEncrypt = AesEncryptDecrypt.encryptHexStrByHexStr(data);
        return dataEncrypt;
    }

    /*
     *
     * 近海通信 20号报文封装
     *
     */
    private static String offshoreCommunicationMessage20(SixbitEncoder encoder, OffshoreCommunicationMessage20 message20) throws Exception {
        offshoreCommunicationAccountMessage(encoder, message20);
        //群组ID
        encoder.addVal(message20.getGroupId(), 34);
        //备用
        encoder.addVal(message20.getSpare(), 2);
        //二进制数据
        encoder.addUTF8DataString(message20.getData());

        //16进制字符串
        String data = encoder.encode4bit();

        String dataEncrypt = AesEncryptDecrypt.encryptHexStrByHexStr(data);
        return dataEncrypt;
    }

    /*
     *
     * 近海通信 21号报文封装
     *
     */
    private static String offshoreCommunicationMessage21(SixbitEncoder encoder, OffshoreCommunicationMessage21 message21) throws Exception {
        offshoreCommunicationAccountMessage(encoder, message21);
        //组ID
        encoder.addVal(message21.getGroupId(), 34);
        //备用
        encoder.addVal(message21.getSpare(), 2);
        //二进制数据
        encoder.addUTF8DataString(message21.getData());

        //16进制字符串
        String data = encoder.encode4bit();

        String dataEncrypt = AesEncryptDecrypt.encryptHexStrByHexStr(data);
        return dataEncrypt;
    }

    /*
     *
     * 近海通信 22号报文封装
     *
     */
    private static String offshoreCommunicationMessage22(SixbitEncoder encoder, OffshoreCommunicationMessage22 message22) throws Exception {
        offshoreCommunicationAccountMessage(encoder, message22);
        //群组ID
        encoder.addVal(message22.getGroupId(), 34);
        //备用
        encoder.addVal(message22.getSpare(), 2);
        //二进制数据
        encoder.addUTF8DataString(message22.getData());

        //16进制字符串
        String data = encoder.encode4bit();

        String dataEncrypt = AesEncryptDecrypt.encryptHexStrByHexStr(data);
        return dataEncrypt;
    }

    /*
     *
     * 近海通信 23号报文封装
     *
     */
    private static String offshoreCommunicationMessage23(SixbitEncoder encoder, OffshoreCommunicationMessage23 message23) throws Exception {
        offshoreCommunicationAccountMessage(encoder, message23);
        //组ID
        encoder.addVal(message23.getGroupId(), 34);
        //备用
        encoder.addVal(message23.getSpare(), 2);
        //二进制数据
        encoder.addUTF8DataString(message23.getData());

        //16进制字符串
        String data = encoder.encode4bit();

        String dataEncrypt = AesEncryptDecrypt.encryptHexStrByHexStr(data);
        return dataEncrypt;
    }

    /*
     *
     * 近海通信 24号报文封装
     *
     */
    private static String offshoreCommunicationMessage24(SixbitEncoder encoder, OffshoreCommunicationMessage24 message24) throws Exception {
        offshoreCommunicationAccountMessage(encoder, message24);
        //消息类型
        encoder.addVal(message24.getMsgType(), 3);
        //总报文数
        encoder.addVal(message24.getTotal(), 10);
        //当前报文号
        encoder.addVal(message24.getCurrent(), 10);
        //语音时长
        encoder.addVal(message24.getTimeLong(), 5);
        //备用
//        encoder.addVal(message24.getSpare(), 1);
        //二进制数据
        if (message24.getMsgType() == 0) {
            //二进制数据(UTF8 16bit)
            encoder.addUTF8DataString(message24.getData());
        } else {
            //二进制数据(ASCII 8bit)
            encoder.addDataString(message24.getData());
        }
        //16进制字符串
        String data = encoder.encode4bit();
        String dataEncrypt = AesEncryptDecrypt.encryptHexStrByHexStr(data);
        return dataEncrypt;
    }

    /*
     *
     * 近海通信 25号报文封装
     *
     */
    private static String offshoreCommunicationMessage25(SixbitEncoder encoder, OffshoreCommunicationMessage25 message25) throws Exception {
        offshoreCommunicationAccountMessage(encoder, message25);
        //消息类型
        encoder.addVal(message25.getMsgType(), 3);
        //总报文数
        encoder.addVal(message25.getTotal(), 10);
        //当前报文号
        encoder.addVal(message25.getCurrent(), 10);
        //备用
//        encoder.addVal(message25.getSpare(), 1);
        encoder.addVal(message25.getTimeLong(), 5);
        //二进制数据
        if (message25.getMsgType() == 0) {
            //二进制数据(UTF8 16bit)
            encoder.addUTF8DataString(message25.getData());
        } else {
            //二进制数据(ASCII 8bit)
            encoder.addDataString(message25.getData());
        }

        //16进制字符串
        String data = encoder.encode4bit();

        String dataEncrypt = AesEncryptDecrypt.encryptHexStrByHexStr(data);
        return dataEncrypt;
    }

    /*
     *
     * 近海通信 26号报文封装
     *
     */
    private static String offshoreCommunicationMessage26(SixbitEncoder encoder, OffshoreCommunicationMessage26 message26) throws Exception {
        offshoreCommunicationAccountMessage(encoder, message26);
        //群组id
        encoder.addVal(message26.getGroupId(), 34);
        //消息类型
        encoder.addVal(message26.getMsgType(), 3);
        //总报文数
        encoder.addVal(message26.getTotal(), 10);
        //当前报文号
        encoder.addVal(message26.getCurrent(), 10);
        //语音时长
        encoder.addVal(message26.getTimeLong(), 5);
        //备用
        encoder.addVal(message26.getSpare(), 6);
        //二进制数据
        if (message26.getMsgType() == 0) {
            //二进制数据(UTF8 16bit)
            encoder.addUTF8DataString(message26.getData());
        } else {
            //二进制数据(ASCII 8bit)
            encoder.addDataString(message26.getData());
        }

        //16进制字符串
        String data = encoder.encode4bit();

        String dataEncrypt = AesEncryptDecrypt.encryptHexStrByHexStr(data);
        return dataEncrypt;
    }

    /*
     *
     * 近海通信 27号报文封装
     *
     */
    private static String offshoreCommunicationMessage27(SixbitEncoder encoder, OffshoreCommunicationMessage27 message27) throws Exception {
        offshoreCommunicationAccountMessage(encoder, message27);
        //群组id
        encoder.addVal(message27.getGroupId(), 34);
        //消息类型
        encoder.addVal(message27.getMsgType(), 3);
        //总报文数
        encoder.addVal(message27.getTotal(), 10);
        //当前报文号
        encoder.addVal(message27.getCurrent(), 10);
        //语句时长
        encoder.addVal(message27.getTimeLong(), 5);
        //备用
        encoder.addVal(message27.getSpare(), 6);
        //二进制数据
        if (message27.getMsgType() == 0) {
            //二进制数据(UTF8 16bit)
            encoder.addUTF8DataString(message27.getData());
        } else {
            //二进制数据(ASCII 8bit)
            encoder.addDataString(message27.getData());
        }

        //16进制字符串
        String data = encoder.encode4bit();

        String dataEncrypt = AesEncryptDecrypt.encryptHexStrByHexStr(data);
        return dataEncrypt;
    }

    /*
     *
     * 近海通信 28号报文封装
     *
     */
    private static String offshoreCommunicationMessage28(SixbitEncoder encoder, OffshoreCommunicationMessage28 message28) throws Exception {
        offshoreCommunicationMessage(encoder, message28);
        //业务类型
        encoder.addVal(message28.getBusinessType(), 4);
        //二进制数据
        encoder.addUTF8DataString(message28.getData());

        //16进制字符串
        String data = encoder.encode4bit();

        String dataEncrypt = AesEncryptDecrypt.encryptHexStrByHexStr(data);
        return dataEncrypt;
    }

    /*
     *
     * 近海通信 29号报文封装
     *
     */
    private static String offshoreCommunicationMessage29(SixbitEncoder encoder, OffshoreCommunicationMessage29 message29) throws Exception {
        offshoreCommunicationAccountMessage(encoder, message29);
        //备用
        encoder.addVal(message29.getSpare(), 4);
        //二进制数据
        encoder.addUTF8DataString(message29.getData());

        //16进制字符串
        String data = encoder.encode4bit();

        String dataEncrypt = AesEncryptDecrypt.encryptHexStrByHexStr(data);
        return dataEncrypt;
    }

    /*
     *
     * 近海通信 30号报文封装
     *
     */
    private static String offshoreCommunicationMessage30(SixbitEncoder encoder, OffshoreCommunicationMessage30 message30) throws Exception {
        offshoreCommunicationAccountMessage(encoder, message30);
        //群组ID
        encoder.addVal(message30.getGroupId(), 34);
        //群组名称
        encoder.addUTF8DataStringForLength(message30.getGroupName(), 256);
        //备用
        encoder.addVal(message30.getSpare(), 2);
        //二进制数据
        encoder.addUTF8DataString(message30.getData());

        //16进制字符串
        String data = encoder.encode4bit();

        String dataEncrypt = AesEncryptDecrypt.encryptHexStrByHexStr(data);
        return dataEncrypt;
    }

    /*
     *
     * 近海通信 31号报文封装
     *
     */
    private static String offshoreCommunicationMessage31(SixbitEncoder encoder, OffshoreCommunicationMessage31 message31) throws Exception {
        offshoreCommunicationAccountMessage(encoder, message31);
        //群组ID
        encoder.addVal(message31.getGroupId(), 34);
        //修改群名人的手机号
        encoder.addVal(message31.getFriendAccount(), 36);
        //备用
        encoder.addVal(message31.getSpare(), 6);
        //二进制数据
        encoder.addUTF8DataString(message31.getData());
        //16进制字符串
        String data = encoder.encode4bit();
        String dataEncrypt = AesEncryptDecrypt.encryptHexStrByHexStr(data);
        return dataEncrypt;
    }

    /*
     *
     * 近海通信 32号报文封装
     *
     */
    private static String offshoreCommunicationMessage32(SixbitEncoder encoder, OffshoreCommunicationMessage32 message32) throws Exception {
        offshoreCommunicationAccountMessage(encoder, message32);
        //备用
        encoder.addVal(message32.getSpare(), 4);
        //二进制数据
        encoder.addUTF8DataString(message32.getData());

        //16进制字符串
        String data = encoder.encode4bit();

        String dataEncrypt = AesEncryptDecrypt.encryptHexStrByHexStr(data);
        return dataEncrypt;
    }

    /*
     *
     * 近海通信 33号报文封装
     *
     */
    private static String offshoreCommunicationMessage33(SixbitEncoder encoder, OffshoreCommunicationMessage33 message33) throws Exception {
        offshoreCommunicationAccountMessage(encoder, message33);
        //备用
        encoder.addVal(message33.getSpare(), 4);
        //二进制数据
        encoder.addUTF8DataString(message33.getData());

        //16进制字符串
        String data = encoder.encode4bit();

        String dataEncrypt = AesEncryptDecrypt.encryptHexStrByHexStr(data);
        return dataEncrypt;
    }


    /*
     *
     * 近海通信 34号报文封装
     *
     */
    private static String offshoreCommunicationMessage34(SixbitEncoder encoder, OffshoreCommunicationMessage34 message34) throws Exception {
        offshoreCommunicationAccountMessage(encoder, message34);
        //备用
        encoder.addVal(message34.getSpare(), 4);
        //二进制数据
        encoder.addUTF8DataString(message34.getData());

        //16进制字符串
        String data = encoder.encode4bit();

        String dataEncrypt = AesEncryptDecrypt.encryptHexStrByHexStr(data);
        return dataEncrypt;
    }

    /*
     *
     * 近海通信 35号报文封装
     *
     */
    private static String offshoreCommunicationMessage35(SixbitEncoder encoder, OffshoreCommunicationMessage35 message35) throws Exception {
        offshoreCommunicationAccountMessage(encoder, message35);
        //备用
        encoder.addVal(message35.getSpare(), 4);
        //二进制数据
        encoder.addUTF8DataString(message35.getData());

        //16进制字符串
        String data = encoder.encode4bit();

        String dataEncrypt = AesEncryptDecrypt.encryptHexStrByHexStr(data);
        return dataEncrypt;
    }

    private static void offshoreCommunicationMessage(OffshoreCommunicationMessage message, BinArray sixbit) throws SixbitException {
        message.setSequenceNumber((int) sixbit.getVal(8));//序号
        message.setYear((int) sixbit.getVal(11));//年
        message.setMonth((int) sixbit.getVal(4));//月
        message.setDay((int) sixbit.getVal(5));//日
        message.setHour((int) sixbit.getVal(5));//时
        message.setMinute((int) sixbit.getVal(6));//分
        message.setSecond((int) sixbit.getVal(6));//秒
        message.setResFlag((int) sixbit.getVal(1));//应答标志

    }


    private static void offshoreCommunicationAccountMessage(OffshoreCommunicationAccountMessage accountMessage, BinArray sixbit) throws SixbitException {
        offshoreCommunicationMessage(accountMessage, sixbit);
        accountMessage.setSourceAccount(sixbit.getVal(36));//源APP账号
        accountMessage.setDestinationAccount(sixbit.getVal(36));//目的APP账号

    }

    /*
     *
     * 近海通信 1号报文解析
     *
     */
    private static OffshoreCommunicationMessage01 offshoreCommunicationMessage01(SixbitEncoder encoder) throws SixbitException, MessageLengthException {
        OffshoreCommunicationMessage01 message1 = new OffshoreCommunicationMessage01();
        BinArray sixbit = encoder.getBinArray();
        if (sixbit.getLength() < 128) {
            throw new MessageLengthException("Message " + 1 + " wrong length: " + sixbit.getLength());
        }
        offshoreCommunicationAccountMessage(message1, sixbit);
        message1.setSpare((int) sixbit.getVal(4));  //备用
        message1.setData(encoder.encode16bit(128));  //二进制数据

        return message1;
    }

    /*
     *
     * 近海通信 2号报文解析
     *
     */
    private static OffshoreCommunicationMessage02 offshoreCommunicationMessage02(SixbitEncoder encoder) throws SixbitException, MessageLengthException {
        OffshoreCommunicationMessage02 message2 = new OffshoreCommunicationMessage02();
        BinArray sixbit = encoder.getBinArray();
        if (sixbit.getLength() < 128) {
            throw new MessageLengthException("Message " + 2 + " wrong length: " + sixbit.getLength());
        }
        offshoreCommunicationAccountMessage(message2, sixbit);
        message2.setSpare((int) sixbit.getVal(4));  //备用
        message2.setData(encoder.encode16bit(128));  //二进制数据

        return message2;
    }


    /*
     *
     * 近海通信 3号报文解析
     *
     */
    private static OffshoreCommunicationMessage03 offshoreCommunicationMessage03(SixbitEncoder encoder) throws SixbitException, MessageLengthException {
        OffshoreCommunicationMessage03 message3 = new OffshoreCommunicationMessage03();
        BinArray sixbit = encoder.getBinArray();
        if (sixbit.getLength() < 128) {
            throw new MessageLengthException("Message " + 3 + " wrong length: " + sixbit.getLength());
        }
        offshoreCommunicationAccountMessage(message3, sixbit);
        message3.setSpare((int) sixbit.getVal(4));  //备用
        message3.setData(encoder.encode16bit(128));  //二进制数据

        return message3;
    }

    /*
     *
     * 近海通信 4号报文解析
     *
     */
    private static OffshoreCommunicationMessage04 offshoreCommunicationMessage04(SixbitEncoder encoder) throws SixbitException, MessageLengthException {
        OffshoreCommunicationMessage04 message4 = new OffshoreCommunicationMessage04();
        BinArray sixbit = encoder.getBinArray();
        if (sixbit.getLength() < 128) {
            throw new MessageLengthException("Message " + 4 + " wrong length: " + sixbit.getLength());
        }
        offshoreCommunicationAccountMessage(message4, sixbit);
        message4.setSpare((int) sixbit.getVal(4));  //备用
        message4.setData(encoder.encode16bit(128));  //二进制数据

        return message4;
    }

    /*
     *
     * 近海通信 5号报文解析
     *
     */
    private static OffshoreCommunicationMessage05 offshoreCommunicationMessage05(SixbitEncoder encoder) throws SixbitException, MessageLengthException {
        OffshoreCommunicationMessage05 message5 = new OffshoreCommunicationMessage05();
        BinArray sixbit = encoder.getBinArray();
        if (sixbit.getLength() < 128) {
            throw new MessageLengthException("Message " + 5 + " wrong length: " + sixbit.getLength());
        }
        offshoreCommunicationAccountMessage(message5, sixbit);
        message5.setSpare((int) sixbit.getVal(4));  //备用
        message5.setData(encoder.encode16bit(128));  //二进制数据

        return message5;
    }

    /*
     *
     * 近海通信 6号报文解析
     *
     */
    private static OffshoreCommunicationMessage06 offshoreCommunicationMessage06(SixbitEncoder encoder) throws SixbitException, MessageLengthException {
        OffshoreCommunicationMessage06 message6 = new OffshoreCommunicationMessage06();
        BinArray sixbit = encoder.getBinArray();
        if (sixbit.getLength() < 256) {
            throw new MessageLengthException("Message " + 6 + " wrong length: " + sixbit.getLength());
        }
        offshoreCommunicationAccountMessage(message6, sixbit);
        message6.setSpare((int) sixbit.getVal(4));  //备用
        message6.setOldPassword(sixbit.getAsciiString(16).trim());
        message6.setData(encoder.encode16bit(256));  //二进制数据

        return message6;
    }

    /*
     *
     * 近海通信 7号报文解析
     *
     */
    private static OffshoreCommunicationMessage07 offshoreCommunicationMessage07(SixbitEncoder encoder) throws SixbitException, MessageLengthException {
        OffshoreCommunicationMessage07 message7 = new OffshoreCommunicationMessage07();
        BinArray sixbit = encoder.getBinArray();
        if (sixbit.getLength() < 128) {
            throw new MessageLengthException("Message " + 7 + " wrong length: " + sixbit.getLength());
        }
        offshoreCommunicationAccountMessage(message7, sixbit);
        message7.setSpare((int) sixbit.getVal(4));  //备用
        message7.setData(encoder.encode16bit(128));  //二进制数据

        return message7;
    }

    /*
     *
     * 近海通信 8号报文解析
     *
     */
    private static OffshoreCommunicationMessage08 offshoreCommunicationMessage08(SixbitEncoder encoder) throws SixbitException, MessageLengthException {
        OffshoreCommunicationMessage08 message8 = new OffshoreCommunicationMessage08();
        BinArray sixbit = encoder.getBinArray();
        if (sixbit.getLength() < 128) {
            throw new MessageLengthException("Message " + 8 + " wrong length: " + sixbit.getLength());
        }
        offshoreCommunicationAccountMessage(message8, sixbit);
        message8.setSpare((int) sixbit.getVal(4));  //备用
        message8.setData(encoder.encode16bit(128));  //二进制数据

        return message8;
    }

    /*
     *
     * 近海通信 9号报文解析
     *
     */
    private static OffshoreCommunicationMessage09 offshoreCommunicationMessage09(SixbitEncoder encoder) throws SixbitException, MessageLengthException {
        OffshoreCommunicationMessage09 message9 = new OffshoreCommunicationMessage09();
        BinArray sixbit = encoder.getBinArray();
        if (sixbit.getLength() < 128) {
            throw new MessageLengthException("Message " + 9 + " wrong length: " + sixbit.getLength());
        }
        offshoreCommunicationAccountMessage(message9, sixbit);
        message9.setSpare((int) sixbit.getVal(4));  //备用
        message9.setData(encoder.encode16bit(128));  //二进制数据

        return message9;
    }

    /*
     *
     * 近海通信 10号报文解析
     *
     */
    private static OffshoreCommunicationMessage10 offshoreCommunicationMessage10(SixbitEncoder encoder) throws SixbitException, MessageLengthException {
        OffshoreCommunicationMessage10 message10 = new OffshoreCommunicationMessage10();
        BinArray sixbit = encoder.getBinArray();
        if (sixbit.getLength() < 128) {
            throw new MessageLengthException("Message " + 10 + " wrong length: " + sixbit.getLength());
        }
        offshoreCommunicationAccountMessage(message10, sixbit);
        message10.setSpare((int) sixbit.getVal(4));  //备用
        message10.setData(encoder.encode16bit(128));  //二进制数据

        return message10;
    }

    /*
     *
     * 近海通信 11号报文解析
     *
     */
    private static OffshoreCommunicationMessage11 offshoreCommunicationMessage11(SixbitEncoder encoder) throws SixbitException, MessageLengthException {
        OffshoreCommunicationMessage11 message11 = new OffshoreCommunicationMessage11();
        BinArray sixbit = encoder.getBinArray();
        if (sixbit.getLength() < 128) {
            throw new MessageLengthException("Message " + 11 + " wrong length: " + sixbit.getLength());
        }
        offshoreCommunicationAccountMessage(message11, sixbit);
        message11.setSpare((int) sixbit.getVal(4));  //备用
        message11.setData(encoder.encode16bit(128));  //二进制数据

        return message11;
    }

    /*
     *
     * 近海通信 12号报文解析
     *
     */
    private static OffshoreCommunicationMessage12 offshoreCommunicationMessage12(SixbitEncoder encoder) throws SixbitException, MessageLengthException {
        OffshoreCommunicationMessage12 message12 = new OffshoreCommunicationMessage12();
        BinArray sixbit = encoder.getBinArray();
        if (sixbit.getLength() < 128) {
            throw new MessageLengthException("Message " + 12 + " wrong length: " + sixbit.getLength());
        }
        offshoreCommunicationAccountMessage(message12, sixbit);
        message12.setSpare((int) sixbit.getVal(4));  //备用
        message12.setData(encoder.encode16bit(128));  //二进制数据

        return message12;
    }

    /*
     *
     * 近海通信 13号报文解析
     *
     */
    private static OffshoreCommunicationMessage13 offshoreCommunicationMessage13(SixbitEncoder encoder) throws SixbitException, MessageLengthException {
        OffshoreCommunicationMessage13 message13 = new OffshoreCommunicationMessage13();
        BinArray sixbit = encoder.getBinArray();
        if (sixbit.getLength() < 160) {
            throw new MessageLengthException("Message " + 13 + " wrong length: " + sixbit.getLength());
        }
        offshoreCommunicationAccountMessage(message13, sixbit);
        message13.setFriendAccount(sixbit.getVal(36));  //被修改备注APP账号
        message13.setData(encoder.encode16bit(160));  //二进制数据

        return message13;
    }

    /*
     *
     * 近海通信 14号报文解析
     *
     */
    private static OffshoreCommunicationMessage14 offshoreCommunicationMessage14(SixbitEncoder encoder) throws SixbitException, MessageLengthException {
        OffshoreCommunicationMessage14 message14 = new OffshoreCommunicationMessage14();
        BinArray sixbit = encoder.getBinArray();
        if (sixbit.getLength() < 416) {
            throw new MessageLengthException("Message " + 14 + " wrong length: " + sixbit.getLength());
        }
        offshoreCommunicationAccountMessage(message14, sixbit);
        message14.setGroupId(sixbit.getVal(34));//群组ID
        message14.setGroupName(encoder.encode16bit(158, 16).trim());//群组名称
        message14.setSpare((int) sixbit.getVal(2));  //备用
        message14.setData(encoder.encode16bit(416));  //二进制数据
        return message14;
    }

    /*
     *
     * 近海通信 15号报文解析
     *
     */
    private static OffshoreCommunicationMessage15 offshoreCommunicationMessage15(SixbitEncoder encoder) throws SixbitException, MessageLengthException {
        OffshoreCommunicationMessage15 message15 = new OffshoreCommunicationMessage15();
        BinArray sixbit = encoder.getBinArray();
        if (sixbit.getLength() < 160) {
            throw new MessageLengthException("Message " + 15 + " wrong length: " + sixbit.getLength());
        }
        offshoreCommunicationAccountMessage(message15, sixbit);
        message15.setGroupId(sixbit.getVal(34));//群组ID
        message15.setSpare((int) sixbit.getVal(2));  //备用
        message15.setData(encoder.encode16bit(160));  //二进制数据

        return message15;
    }

    /*
     *
     * 近海通信 16号报文解析
     *
     */
    private static OffshoreCommunicationMessage16 offshoreCommunicationMessage16(SixbitEncoder encoder) throws SixbitException, MessageLengthException {
        OffshoreCommunicationMessage16 message16 = new OffshoreCommunicationMessage16();
        BinArray sixbit = encoder.getBinArray();
        if (sixbit.getLength() < 456) {
            throw new MessageLengthException("Message " + 16 + " wrong length: " + sixbit.getLength());
        }
        offshoreCommunicationAccountMessage(message16, sixbit);
        message16.setFriendAccount(sixbit.getVal(36));  //发起拉人请求APP账号
        message16.setGroupId(sixbit.getVal(34));//群组ID
        message16.setGroupName(encoder.encode16bit(194, 16).trim());//群组名称
        message16.setSpare((int) sixbit.getVal(6));  //备用
        message16.setData(encoder.encode16bit(456));  //二进制数据

        return message16;
    }

    /*
     *
     * 近海通信 17号报文解析
     *
     */
    private static OffshoreCommunicationMessage17 offshoreCommunicationMessage17(SixbitEncoder encoder) throws SixbitException, MessageLengthException {
        OffshoreCommunicationMessage17 message17 = new OffshoreCommunicationMessage17();
        BinArray sixbit = encoder.getBinArray();
        if (sixbit.getLength() < 128) {
            throw new MessageLengthException("Message " + 17 + " wrong length: " + sixbit.getLength());
        }
        offshoreCommunicationAccountMessage(message17, sixbit);
        message17.setFriendAccount(sixbit.getVal(36));  //发起拉人请求APP账号
        message17.setGroupId(sixbit.getVal(34));//群组ID
        message17.setSpare((int) sixbit.getVal(6));  //备用
        message17.setData(encoder.encode16bit(200));  //二进制数据

        return message17;
    }

    /*
     *
     * 近海通信 18号报文解析
     *
     */
    private static OffshoreCommunicationMessage18 offshoreCommunicationMessage18(SixbitEncoder encoder) throws SixbitException, MessageLengthException {
        OffshoreCommunicationMessage18 message18 = new OffshoreCommunicationMessage18();
        BinArray sixbit = encoder.getBinArray();
        if (sixbit.getLength() < 160) {
            throw new MessageLengthException("Message " + 18 + " wrong length: " + sixbit.getLength());
        }
        offshoreCommunicationAccountMessage(message18, sixbit);
        message18.setGroupId(sixbit.getVal(34));//群组ID
        message18.setSpare((int) sixbit.getVal(2));  //备用
        message18.setData(encoder.encode16bit(160));  //二进制数据

        return message18;
    }

    /*
     *
     * 近海通信 19号报文解析
     *
     */
    private static OffshoreCommunicationMessage19 offshoreCommunicationMessage19(SixbitEncoder encoder) throws SixbitException, MessageLengthException {
        OffshoreCommunicationMessage19 message19 = new OffshoreCommunicationMessage19();
        BinArray sixbit = encoder.getBinArray();
        if (sixbit.getLength() < 200) {
            throw new MessageLengthException("Message " + 19 + " wrong length: " + sixbit.getLength());
        }
        offshoreCommunicationAccountMessage(message19, sixbit);
        message19.setFriendAccount(sixbit.getVal(36));  //发起移除申请APP账号
        message19.setGroupId(sixbit.getVal(34));//群组ID
        message19.setSpare((int) sixbit.getVal(6));  //备用
        message19.setData(encoder.encode16bit(200));  //二进制数据

        return message19;
    }

    /*
     *
     * 近海通信 20号报文解析
     *
     */
    private static OffshoreCommunicationMessage20 offshoreCommunicationMessage20(SixbitEncoder encoder) throws SixbitException, MessageLengthException {
        OffshoreCommunicationMessage20 message20 = new OffshoreCommunicationMessage20();
        BinArray sixbit = encoder.getBinArray();
        if (sixbit.getLength() < 160) {
            throw new MessageLengthException("Message " + 20 + " wrong length: " + sixbit.getLength());
        }
        offshoreCommunicationAccountMessage(message20, sixbit);
        message20.setGroupId(sixbit.getVal(34));//群组ID
        message20.setSpare((int) sixbit.getVal(2));  //备用
        message20.setData(encoder.encode16bit(160));  //二进制数据

        return message20;
    }

    /*
     *
     * 近海通信 21号报文解析
     *
     */
    private static OffshoreCommunicationMessage21 offshoreCommunicationMessage21(SixbitEncoder encoder) throws SixbitException, MessageLengthException {
        OffshoreCommunicationMessage21 message21 = new OffshoreCommunicationMessage21();
        BinArray sixbit = encoder.getBinArray();
        if (sixbit.getLength() < 128) {
            throw new MessageLengthException("Message " + 21 + " wrong length: " + sixbit.getLength());
        }
        offshoreCommunicationAccountMessage(message21, sixbit);
//        message21.setFriendAccount(sixbit.getVal(36));  //发起移除申请APP账号
        message21.setGroupId(sixbit.getVal(34));//群组ID
        message21.setSpare((int) sixbit.getVal(2));  //备用
        message21.setData(encoder.encode16bit(160));  //二进制数据

        return message21;
    }

    /*
     *
     * 近海通信 22号报文解析
     *
     */
    private static OffshoreCommunicationMessage22 offshoreCommunicationMessage22(SixbitEncoder encoder) throws SixbitException, MessageLengthException {
        OffshoreCommunicationMessage22 message22 = new OffshoreCommunicationMessage22();
        BinArray sixbit = encoder.getBinArray();
        if (sixbit.getLength() < 160) {
            throw new MessageLengthException("Message " + 22 + " wrong length: " + sixbit.getLength());
        }
        offshoreCommunicationAccountMessage(message22, sixbit);
        message22.setGroupId(sixbit.getVal(34));//群组ID
        message22.setSpare((int) sixbit.getVal(2));  //备用
        message22.setData(encoder.encode16bit(160));  //二进制数据

        return message22;
    }

    /*
     *
     * 近海通信 23号报文解析
     *
     */
    private static OffshoreCommunicationMessage23 offshoreCommunicationMessage23(SixbitEncoder encoder) throws SixbitException, MessageLengthException {
        OffshoreCommunicationMessage23 message23 = new OffshoreCommunicationMessage23();
        BinArray sixbit = encoder.getBinArray();
        if (sixbit.getLength() < 88) {
            throw new MessageLengthException("Message " + 23 + " wrong length: " + sixbit.getLength());
        }
        offshoreCommunicationAccountMessage(message23, sixbit);
        message23.setGroupId(sixbit.getVal(34));//群组ID
        message23.setSpare((int) sixbit.getVal(2));  //备用
        message23.setData(encoder.encode16bit(160));  //二进制数据

        return message23;
    }

    /*
     *
     * 近海通信 24号报文解析
     *
     */
    private static OffshoreCommunicationMessage24 offshoreCommunicationMessage24(SixbitEncoder encoder) throws SixbitException, MessageLengthException {
        OffshoreCommunicationMessage24 message24 = new OffshoreCommunicationMessage24();
        BinArray sixbit = encoder.getBinArray();
        if (sixbit.getLength() < 152) {
            throw new MessageLengthException("Message " + 24 + " wrong length: " + sixbit.getLength());
        }
        offshoreCommunicationAccountMessage(message24, sixbit);
        message24.setMsgType((int) sixbit.getVal(3)); //消息类型
        message24.setTotal((int) sixbit.getVal(10));  //总包数
        message24.setCurrent((int) sixbit.getVal(10)); //当前包
        message24.setTimeLong((int) sixbit.getVal(5));//语句时长
//        message24.setSpare((int) sixbit.getVal(1));  //备用
        if (message24.getMsgType() == 0) {
            message24.setData(encoder.encode16bit(152));  //文本二进制数据
        } else {
            message24.setData(encoder.encode8bit(152)); //非文本（语音或图片）二进制数据
        }

        return message24;
    }

    /*
     *
     * 近海通信 25号报文解析
     *
     */
    private static OffshoreCommunicationMessage25 offshoreCommunicationMessage25(SixbitEncoder encoder) throws SixbitException, MessageLengthException {
        OffshoreCommunicationMessage25 message25 = new OffshoreCommunicationMessage25();
        BinArray sixbit = encoder.getBinArray();
        if (sixbit.getLength() < 152) {
            throw new MessageLengthException("Message " + 25 + " wrong length: " + sixbit.getLength());
        }
        offshoreCommunicationAccountMessage(message25, sixbit);
        message25.setMsgType((int) sixbit.getVal(3)); //消息类型
        message25.setTotal((int) sixbit.getVal(10));  //总包数
        message25.setCurrent((int) sixbit.getVal(10)); //当前包
        message25.setTimeLong((int) sixbit.getVal(5));//语句时长

//        message25.setSpare((int) sixbit.getVal(1));  //备用
        if (message25.getMsgType() == 0) {
            message25.setData(encoder.encode16bit(152));  //文本二进制数据
        } else {
            message25.setData(encoder.encode8bit(152)); //非文本（语音或图片）二进制数据
        }
        return message25;
    }

    /*
     *
     * 近海通信 26号报文解析
     *
     */
    private static OffshoreCommunicationMessage26 offshoreCommunicationMessage26(SixbitEncoder encoder) throws SixbitException, MessageLengthException {
        OffshoreCommunicationMessage26 message26 = new OffshoreCommunicationMessage26();
        BinArray sixbit = encoder.getBinArray();
        if (sixbit.getLength() < 160) {
            throw new MessageLengthException("Message " + 26 + " wrong length: " + sixbit.getLength());
        }
        offshoreCommunicationAccountMessage(message26, sixbit);
        message26.setGroupId(sixbit.getVal(34));//群组ID
        message26.setMsgType((int) sixbit.getVal(3)); //消息类型
        message26.setTotal((int) sixbit.getVal(10));  //总包数
        message26.setCurrent((int) sixbit.getVal(10)); //当前包
        message26.setTimeLong((int) sixbit.getVal(5));//语句时长
        message26.setSpare((int) sixbit.getVal(6));  //备用
        if (message26.getMsgType() == 0) {
            message26.setData(encoder.encode16bit(192));  //文本二进制数据
        } else {
            message26.setData(encoder.encode8bit(192)); //非文本（语音或图片）二进制数据
        }
        return message26;
    }

    /*
     *
     * 近海通信 27号报文解析
     *
     */
    private static OffshoreCommunicationMessage27 offshoreCommunicationMessage27(SixbitEncoder encoder) throws SixbitException, MessageLengthException {
        OffshoreCommunicationMessage27 message27 = new OffshoreCommunicationMessage27();
        BinArray sixbit = encoder.getBinArray();
        if (sixbit.getLength() < 192) {
            throw new MessageLengthException("Message " + 27 + " wrong length: " + sixbit.getLength());
        }
        offshoreCommunicationAccountMessage(message27, sixbit);
        message27.setGroupId(sixbit.getVal(34));//群组ID
        message27.setMsgType((int) sixbit.getVal(3)); //消息类型
        message27.setTotal((int) sixbit.getVal(10));  //总包数
        message27.setCurrent((int) sixbit.getVal(10)); //当前包
        message27.setTimeLong((int) sixbit.getVal(5));//语句时长
        message27.setSpare((int) sixbit.getVal(6));  //备用
        if (message27.getMsgType() == 0) {
            message27.setData(encoder.encode16bit(192));  //文本二进制数据
        } else {
            message27.setData(encoder.encode8bit(192)); //非文本（语音或图片）二进制数据
        }
        return message27;
    }

    /*
     *
     * 近海通信 28号报文解析
     *
     */
    private static OffshoreCommunicationMessage28 offshoreCommunicationMessage28(SixbitEncoder encoder) throws SixbitException, MessageLengthException {
        OffshoreCommunicationMessage28 message28 = new OffshoreCommunicationMessage28();
        BinArray sixbit = encoder.getBinArray();
        if (sixbit.getLength() < 56) {
            throw new MessageLengthException("Message " + 28 + " wrong length: " + sixbit.getLength());
        }
        offshoreCommunicationMessage(message28, sixbit);
        message28.setBusinessType((int) sixbit.getVal(4));  //备用
        message28.setData(encoder.encode16bit(56));  //二进制数据

        return message28;
    }

    /*
     *
     * 近海通信 29号报文解析
     *
     */
    private static OffshoreCommunicationMessage29 offshoreCommunicationMessage29(SixbitEncoder encoder) throws SixbitException, MessageLengthException {
        OffshoreCommunicationMessage29 message29 = new OffshoreCommunicationMessage29();
        BinArray sixbit = encoder.getBinArray();
        if (sixbit.getLength() < 128) {
            throw new MessageLengthException("Message " + 8 + " wrong length: " + sixbit.getLength());
        }
        offshoreCommunicationAccountMessage(message29, sixbit);
        message29.setSpare((int) sixbit.getVal(4));  //备用
        message29.setData(encoder.encode16bit(128));  //二进制数据

        return message29;
    }

    /*
     *
     * 近海通信 30号报文解析
     *
     */
    private static OffshoreCommunicationMessage30 offshoreCommunicationMessage30(SixbitEncoder encoder) throws SixbitException, MessageLengthException {
        OffshoreCommunicationMessage30 message30 = new OffshoreCommunicationMessage30();
        BinArray sixbit = encoder.getBinArray();
        if (sixbit.getLength() < 128) {
            throw new MessageLengthException("Message " + 8 + " wrong length: " + sixbit.getLength());
        }
        offshoreCommunicationAccountMessage(message30, sixbit);
        message30.setGroupId(sixbit.getVal(34));//群组ID
        message30.setGroupName(encoder.encode16bit(158, 16).trim());//群组名称
        message30.setSpare((int) sixbit.getVal(2));  //备用
        message30.setData(encoder.encode16bit(416));  //二进制数据
        return message30;
    }

    /*
     *
     * 近海通信 31号报文解析
     *
     */
    private static OffshoreCommunicationMessage31 offshoreCommunicationMessage31(SixbitEncoder encoder) throws SixbitException, MessageLengthException {
        OffshoreCommunicationMessage31 message31 = new OffshoreCommunicationMessage31();
        BinArray sixbit = encoder.getBinArray();
        if (sixbit.getLength() < 128) {
            throw new MessageLengthException("Message " + 8 + " wrong length: " + sixbit.getLength());
        }
        offshoreCommunicationAccountMessage(message31, sixbit);
        message31.setGroupId(sixbit.getVal(34));//群组ID
        message31.setFriendAccount(sixbit.getVal(36));//修改群名的人
        message31.setSpare((int) sixbit.getVal(6));  //备用
        message31.setData(encoder.encode16bit(200));  //二进制数据

        return message31;
    }

    /*
     *
     * 近海通信 32号报文解析
     *
     */
    private static OffshoreCommunicationMessage32 offshoreCommunicationMessage32(SixbitEncoder encoder) throws SixbitException, MessageLengthException {
        OffshoreCommunicationMessage32 message32 = new OffshoreCommunicationMessage32();
        BinArray sixbit = encoder.getBinArray();
        if (sixbit.getLength() < 128) {
            throw new MessageLengthException("Message " + 4 + " wrong length: " + sixbit.getLength());
        }
        offshoreCommunicationAccountMessage(message32, sixbit);
        message32.setSpare((int) sixbit.getVal(4));  //备用
        message32.setData(encoder.encode16bit(128));  //二进制数据

        return message32;
    }

    /*
     *
     * 近海通信 33号报文解析
     *
     */
    private static OffshoreCommunicationMessage33 offshoreCommunicationMessage33(SixbitEncoder encoder) throws SixbitException, MessageLengthException {
        OffshoreCommunicationMessage33 message33 = new OffshoreCommunicationMessage33();
        BinArray sixbit = encoder.getBinArray();
        if (sixbit.getLength() < 128) {
            throw new MessageLengthException("Message " + 4 + " wrong length: " + sixbit.getLength());
        }
        offshoreCommunicationAccountMessage(message33, sixbit);
        message33.setSpare((int) sixbit.getVal(4));  //备用
        message33.setData(encoder.encode16bit(128));  //二进制数据

        return message33;
    }

    /*
     *
     * 近海通信 34号报文解析
     *
     */
    private static OffshoreCommunicationMessage34 offshoreCommunicationMessage34(SixbitEncoder encoder) throws SixbitException, MessageLengthException {
        OffshoreCommunicationMessage34 message34 = new OffshoreCommunicationMessage34();
        BinArray sixbit = encoder.getBinArray();
        if (sixbit.getLength() < 128) {
            throw new MessageLengthException("Message " + 34 + " wrong length: " + sixbit.getLength());
        }
        offshoreCommunicationAccountMessage(message34, sixbit);
        message34.setSpare((int) sixbit.getVal(4));  //备用
        message34.setData(encoder.encode16bit(128));  //二进制数据
        return message34;
    }

    /*
     *
     * 近海通信 35号报文解析
     *
     */
    private static OffshoreCommunicationMessage35 offshoreCommunicationMessage35(SixbitEncoder encoder) throws SixbitException, MessageLengthException {
        OffshoreCommunicationMessage35 message35 = new OffshoreCommunicationMessage35();
        BinArray sixbit = encoder.getBinArray();
        if (sixbit.getLength() < 128) {
            throw new MessageLengthException("Message " + 34 + " wrong length: " + sixbit.getLength());
        }
        offshoreCommunicationAccountMessage(message35, sixbit);
        message35.setSpare((int) sixbit.getVal(4));  //备用
        message35.setData(encoder.encode16bit(128));  //二进制数据
        return message35;
    }

    /*
     *
     * 报文校验
     *
     */
    public static String verification(String str) {
        int c1 = str.charAt(0);

        for (int i = 1; i < str.length(); i++) {
            int c = str.charAt(i);

            c1 = c1 ^ c;
        }

        String verificationCode = Long.toHexString(c1).toUpperCase();

        if (verificationCode.length() < 2) {
            verificationCode = "0" + verificationCode;
        }

        return verificationCode;
    }

    //以下为调用接口


    /*
     *
     * JHD报文封装
     *
     */
    public static String messageJHD(JHDSentence jhdSentence) throws Exception {
        //Long mmsi, Long longitude, Long latitude
        StringBuffer sb = new StringBuffer();
        sb.append("$--JHD");
        sb.append(comma);
        sb.append(dealEmptyObject(jhdSentence.getMmsi()));
        sb.append(comma);
        String offshoreCommunicationMessage = offshoreCommunicationMessage(jhdSentence.getOffshoreCommunicationMessage());
        sb.append(dealEmptyObject(offshoreCommunicationMessage));
        String verificationCode = verification(sb.toString().substring(1));
        sb.append("*");
        sb.append(verificationCode);
        sb.append("\r\n");
        return sb.toString();
    }

    /*
     *
     * JHD报文解析
     *
     *
     */
    public static JHDSentence reslvToJHD(String str) throws Exception {
        String flag = String.valueOf((char) 0);
        String filterStr = str.replaceAll(flag, "");
        final int startIndex = filterStr.indexOf("*");
        String verification = filterStr.substring(startIndex + 1, startIndex + 3);
        String verificationCode = verification(filterStr.substring(1, startIndex));
        JHDSentence jhdInfo = new JHDSentence();
        if (verification.equals(verificationCode)) {
            String[] strs = str.split(",");
            //获取语句类型
            String inputType = strs[0].substring(3);

            Long shoreMmsi = Utils.notEmpty(strs[1]) ? Long.parseLong(strs[1]) : null;
            jhdInfo.setMmsi(shoreMmsi);

            //电文内容解析
            String dataInfo = strs[2].substring(0, strs[2].indexOf("*"));


            //AES 解密
            String dataDecrypt = AesEncryptDecrypt.decryptHexStrByHexStr(dataInfo);

            SixbitEncoder encoder = new SixbitEncoder();
            encoder.add16String(dataDecrypt);
            int messageId = (int) encoder.getBinArray().getVal(6);

            switch (messageId) {
                //1号报文
                case 1:
                    OffshoreCommunicationMessage01 message1 = offshoreCommunicationMessage01(encoder);
                    jhdInfo.setOffshoreCommunicationMessage(message1);
                    break;
                //2号报文
                case 2:
                    OffshoreCommunicationMessage02 message2 = offshoreCommunicationMessage02(encoder);
                    jhdInfo.setOffshoreCommunicationMessage(message2);
                    break;
                //3号报文
                case 3:
                    OffshoreCommunicationMessage03 message3 = offshoreCommunicationMessage03(encoder);
                    jhdInfo.setOffshoreCommunicationMessage(message3);
                    break;
                //4号报文
                case 4:
                    OffshoreCommunicationMessage04 message4 = offshoreCommunicationMessage04(encoder);
                    jhdInfo.setOffshoreCommunicationMessage(message4);
                    break;
                //5号报文
                case 5:
                    OffshoreCommunicationMessage05 message5 = offshoreCommunicationMessage05(encoder);
                    jhdInfo.setOffshoreCommunicationMessage(message5);
                    break;
                //6号报文
                case 6:
                    OffshoreCommunicationMessage06 message6 = offshoreCommunicationMessage06(encoder);
                    jhdInfo.setOffshoreCommunicationMessage(message6);
                    break;
                //7号报文
                case 7:
                    OffshoreCommunicationMessage07 message7 = offshoreCommunicationMessage07(encoder);
                    jhdInfo.setOffshoreCommunicationMessage(message7);
                    break;
                //8号报文
                case 8:
                    OffshoreCommunicationMessage08 message8 = offshoreCommunicationMessage08(encoder);
                    jhdInfo.setOffshoreCommunicationMessage(message8);
                    break;
                //9号报文
                case 9:
                    OffshoreCommunicationMessage09 message9 = offshoreCommunicationMessage09(encoder);
                    jhdInfo.setOffshoreCommunicationMessage(message9);
                    break;
                //10号报文
                case 10:
                    OffshoreCommunicationMessage10 message10 = offshoreCommunicationMessage10(encoder);
                    jhdInfo.setOffshoreCommunicationMessage(message10);
                    break;
                //11号报文
                case 11:
                    OffshoreCommunicationMessage11 message11 = offshoreCommunicationMessage11(encoder);
                    jhdInfo.setOffshoreCommunicationMessage(message11);
                    break;
                //12号报文
                case 12:
                    OffshoreCommunicationMessage12 message12 = offshoreCommunicationMessage12(encoder);
                    jhdInfo.setOffshoreCommunicationMessage(message12);
                    break;
                //13号报文
                case 13:
                    OffshoreCommunicationMessage13 message13 = offshoreCommunicationMessage13(encoder);
                    jhdInfo.setOffshoreCommunicationMessage(message13);
                    break;
                //14号报文
                case 14:
                    OffshoreCommunicationMessage14 message14 = offshoreCommunicationMessage14(encoder);
                    jhdInfo.setOffshoreCommunicationMessage(message14);
                    break;
                //15号报文
                case 15:
                    OffshoreCommunicationMessage15 message15 = offshoreCommunicationMessage15(encoder);
                    jhdInfo.setOffshoreCommunicationMessage(message15);
                    break;
                //16号报文
                case 16:
                    OffshoreCommunicationMessage16 message16 = offshoreCommunicationMessage16(encoder);
                    jhdInfo.setOffshoreCommunicationMessage(message16);
                    break;
                //17号报文
                case 17:
                    OffshoreCommunicationMessage17 message17 = offshoreCommunicationMessage17(encoder);
                    jhdInfo.setOffshoreCommunicationMessage(message17);
                    break;
                //18号报文
                case 18:
                    OffshoreCommunicationMessage18 message18 = offshoreCommunicationMessage18(encoder);
                    jhdInfo.setOffshoreCommunicationMessage(message18);
                    break;
                //19号报文
                case 19:
                    OffshoreCommunicationMessage19 message19 = offshoreCommunicationMessage19(encoder);
                    jhdInfo.setOffshoreCommunicationMessage(message19);
                    break;
                //20号报文
                case 20:
                    OffshoreCommunicationMessage20 message20 = offshoreCommunicationMessage20(encoder);
                    jhdInfo.setOffshoreCommunicationMessage(message20);
                    break;
                //21号报文
                case 21:
                    OffshoreCommunicationMessage21 message21 = offshoreCommunicationMessage21(encoder);
                    jhdInfo.setOffshoreCommunicationMessage(message21);
                    break;
                //22号报文
                case 22:
                    OffshoreCommunicationMessage22 message22 = offshoreCommunicationMessage22(encoder);
                    jhdInfo.setOffshoreCommunicationMessage(message22);
                    break;
                //23号报文
                case 23:
                    OffshoreCommunicationMessage23 message23 = offshoreCommunicationMessage23(encoder);
                    jhdInfo.setOffshoreCommunicationMessage(message23);
                    break;
                //24号报文
                case 24:
                    OffshoreCommunicationMessage24 message24 = offshoreCommunicationMessage24(encoder);
                    jhdInfo.setOffshoreCommunicationMessage(message24);
                    break;
                //25号报文
                case 25:
                    OffshoreCommunicationMessage25 message25 = offshoreCommunicationMessage25(encoder);
                    jhdInfo.setOffshoreCommunicationMessage(message25);
                    break;
                //26号报文
                case 26:
                    OffshoreCommunicationMessage26 message26 = offshoreCommunicationMessage26(encoder);
                    jhdInfo.setOffshoreCommunicationMessage(message26);
                    break;
                //27号报文
                case 27:
                    OffshoreCommunicationMessage27 message27 = offshoreCommunicationMessage27(encoder);
                    jhdInfo.setOffshoreCommunicationMessage(message27);
                    break;
                //27号报文
                case 28:
                    OffshoreCommunicationMessage28 message28 = offshoreCommunicationMessage28(encoder);
                    jhdInfo.setOffshoreCommunicationMessage(message28);
                    break;
                //29号报文
                case 29:
                    OffshoreCommunicationMessage29 message29 = offshoreCommunicationMessage29(encoder);
                    jhdInfo.setOffshoreCommunicationMessage(message29);
                    break;
                //30号报文
                case 30:
                    OffshoreCommunicationMessage30 message30 = offshoreCommunicationMessage30(encoder);
                    jhdInfo.setOffshoreCommunicationMessage(message30);
                    break;
                //31号报文
                case 31:
                    OffshoreCommunicationMessage31 message31 = offshoreCommunicationMessage31(encoder);
                    jhdInfo.setOffshoreCommunicationMessage(message31);
                    break;
                //32号报文
                case 32:
                    OffshoreCommunicationMessage32 message32 = offshoreCommunicationMessage32(encoder);
                    jhdInfo.setOffshoreCommunicationMessage(message32);
                    break;
                //33号报文
                case 33:
                    OffshoreCommunicationMessage33 message33 = offshoreCommunicationMessage33(encoder);
                    jhdInfo.setOffshoreCommunicationMessage(message33);
                    break;
                case 34:
                    OffshoreCommunicationMessage34 message34 = offshoreCommunicationMessage34(encoder);
                    jhdInfo.setOffshoreCommunicationMessage(message34);
                    break;
                //33号报文
                case 35:
                    OffshoreCommunicationMessage35 message35 = offshoreCommunicationMessage35(encoder);
                    jhdInfo.setOffshoreCommunicationMessage(message35);
                    break;
                //其他
                default:
                    Log.e(CommonConstant.LOGCAT_TAG, "接收语句异常: messageId:" + messageId);
                    Log.e(CommonConstant.LOGCAT_TAG, "接收语句异常: " + "JHD Message Verification Exception.");
                    throw new MessageNoException("JHD MessageNo Exception!");
            }

        } else {
            Log.e(CommonConstant.LOGCAT_TAG, "接收语句异常: " + "JHD Message Verification Exception.");
            throw new MessageVerificationException("JHD Message Verification Exception.");
        }
        return jhdInfo;
    }

    /*
     *
     * 根据参数生成BHM报文封装
     *
     */
    public static String messageBHM(BHMSentence bhmSentence) throws Exception {
        StringBuffer sb = new StringBuffer();
        sb.append("$--BHM");
        sb.append(comma);
        sb.append(dealEmptyObject(bhmSentence.getConfirmIdentifier()));
        sb.append(comma);
        sb.append(dealEmptyObject(bhmSentence.getMmsi()));
        sb.append(comma);
        sb.append(dealEmptyObject(bhmSentence.getSentencesNumber()));
        sb.append(comma);
        String data = bhmSentence.getData();
        String dataEncrypt = AesEncryptDecrypt.encryptHexStrByStr(data, "GBK", "AES/ECB/NoPadding");
        sb.append(dealEmptyObject(dataEncrypt));

        String verificationCode = verification(sb.toString().substring(1));
        sb.append("*");
        sb.append(verificationCode);
        sb.append("\r\n");
        return sb.toString();
    }

    /*
     *
     * 自动生成BHM报文封装
     *
     */
    public static String messageBHM(Long mmsi) throws Exception {
        //Long mmsi, Long longitude, Long latitude
        StringBuffer sb = new StringBuffer();
        sb.append("$--BHM");
        sb.append(comma);
        sb.append(0);
        sb.append(comma);
        sb.append(dealEmptyObject(mmsi));
        sb.append(comma);
        sb.append(getBhmNumber());
        sb.append(comma);
        String time = String.valueOf(System.currentTimeMillis());
        String dataEncrypt = AesEncryptDecrypt.encryptHexStrByStr(time + "000", "GBK", "AES/ECB/NoPadding");
        sb.append(dealEmptyObject(dataEncrypt));

        String verificationCode = verification(sb.toString().substring(1));
        sb.append("*");
        sb.append(verificationCode);
        sb.append("\r\n");
        return sb.toString();
    }

    /*
     *
     * BHM报文解析
     *
     *
     */
    public static BHMSentence reslvToBHM(String str) throws Exception {

        final int startIndex = str.indexOf("*");
        String verification = str.substring(startIndex + 1, startIndex + 3);
        String verificationCode = verification(str.substring(1, startIndex));

        BHMSentence bhmInfo = new BHMSentence();
        if (verification.equals(verificationCode)) {
            String[] strs = str.split(",");
            //确认标识
            String confirmIdentifier = Utils.notEmpty(strs[1]) ? strs[1] : null;
            bhmInfo.setConfirmIdentifier(confirmIdentifier);
            //连接号
            Long shoreMmsi = Utils.notEmpty(strs[2]) ? Long.parseLong(strs[2]) : null;
            bhmInfo.setMmsi(shoreMmsi);
            //序号
            Integer sentencesNumber = Utils.notEmpty(strs[3]) ? Integer.parseInt(strs[3]) : null;
            bhmInfo.setSentencesNumber(sentencesNumber);

            //身份验证
            String data = strs[4].substring(0, strs[4].indexOf("*"));
            //AES 解密
            String dataDecrypt = AesEncryptDecrypt.decryptStrByHexStr(data, "GBK", "AES/ECB/NoPadding");
            bhmInfo.setData(dataDecrypt);

            return bhmInfo;
        } else {
            throw new MessageVerificationException("BHM Message Verification Exception.");
        }
    }

    /*
     *
     * 生成应答BHM报文封装（data数据按规则处理,对校验数据进行替换）
     *
     */
    public static String messageBHMRes(String str) throws Exception {

        return messageBHMRes(reslvToBHM(str));

    }

    /*
     *
     * 生成应答BHM报文封装（data数据按规则处理,对校验数据进行替换）
     *
     */
    public static String messageBHMRes(BHMSentence bhmSentence) throws Exception {
        StringBuffer sb = new StringBuffer();
        sb.append("$--BHM");
        sb.append(comma);
        sb.append(1);
        sb.append(comma);
        sb.append(dealEmptyObject(bhmSentence.getMmsi()));
        sb.append(comma);
        sb.append(dealEmptyObject(bhmSentence.getSentencesNumber()));
        sb.append(comma);
        String data = bhmSentence.getData();
        SixbitEncoder encoder = new SixbitEncoder();
        encoder.addDataString(data);
        String data4Bit = encoder.encode4bit();
        String dataLeft = data4Bit.substring(0, 16);
        String dataRight = data4Bit.substring(16);

        String dataEncrypt = AesEncryptDecrypt.encryptHexStrByHexStr(dataRight + dataLeft, "GBK", "AES/ECB/NoPadding");
        sb.append(dealEmptyObject(dataEncrypt));

        String verificationCode = verification(sb.toString().substring(1));
        sb.append("*");
        sb.append(verificationCode);
        sb.append("\r\n");
        return sb.toString();
    }

}
