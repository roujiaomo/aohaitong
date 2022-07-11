package com.aohaitong.utils;

import android.text.TextUtils;

import com.aohaitong.bean.entity.GroupFriendBean;
import com.aohaitong.ui.model.LetterIndexModel;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;

import java.util.Comparator;
import java.util.Random;

public class PinyinUtil {
    public static class ContactsPinyinComparator implements Comparator<LetterIndexModel> {
        public int compare(LetterIndexModel o1, LetterIndexModel o2) {
            if (TextUtils.isEmpty(o1.pinyin) || TextUtils.isEmpty(o2.pinyin)) {
                return 0;
            }
            return o1.pinyin.compareTo(o2.pinyin);
        }
    }

    public static class GroupFriendPinyinComparator implements Comparator<GroupFriendBean> {
        public int compare(GroupFriendBean o1, GroupFriendBean o2) {
            if (TextUtils.isEmpty(o1.getPinyin()) || TextUtils.isEmpty(o2.getPinyin())) {
                return 0;
            }
            return o1.getPinyin().compareTo(o2.getPinyin());
        }
    }

    /**
     * 使用PinYin4j.jar将汉字转换为拼音
     */
    private static HanyuPinyinOutputFormat format;

    public static String chineneToSpell(String chineseStr) {
        try {
            if (!TextUtils.isEmpty(chineseStr)) {
                if (format == null) {
                    format = new HanyuPinyinOutputFormat();
                    format.setCaseType(HanyuPinyinCaseType.LOWERCASE);
                    format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
                    format.setVCharType(HanyuPinyinVCharType.WITH_V);
                }
                return PinyinHelper.toHanYuPinyinString(chineseStr, format, "", false);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return chineseStr;
        }
        return "";
    }

    public static int getRandom(int min, int max) {
        if (max > min) {
            Random random = new Random();
            return random.nextInt(max - min) + min;
        }
        return 0;
    }
}
