package com.aohaitong.utils;

import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;

import com.aohaitong.MyApplication;
import com.aohaitong.bean.ContactsBean;
import com.aohaitong.bean.FriendBean;
import com.aohaitong.db.DBManager;
import com.aohaitong.ui.model.ContactsDetailBean;

import java.util.List;

/**
 * 联系人工具类
 * 读取联系人并存在内存中(考虑在超过多少条后临时存于数据库中管理)
 */
public class ContactsUtil {
    public static final String[] PHONES_PROJECTION = new String[]{
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, //联系人姓名
            ContactsContract.CommonDataKinds.Phone.NUMBER, //电话号码
            ContactsContract.CommonDataKinds.Phone.CONTACT_ID //ID
    };

    public static void getAllContacts(Context context) {
        Cursor cursor;
        try {
            cursor = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, PHONES_PROJECTION, null, null, null);

        } catch (Exception e) {
            return;
        }
        while (cursor.moveToNext()) {
            //新建一个联系人实例
            ContactsBean temp = new ContactsBean();
//            String contactId = cursor.getString(cursor
//                    .getColumnIndex(ContactsContract.Contacts._ID));
            //获取联系人姓名
            String name = cursor.getString(0);
            temp.setName(name);

            String phoneNumber = cursor.getString(1);            //获取联系人电话号码
            temp.setTelephone(phoneNumber);
//            Cursor phoneCursor = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
//                    null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=" + contactId, null, null);
//            while (phoneCursor.moveToNext()) {
//                String phone = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
//                phone = phone.replace("-", "");
//                phone = phone.replace(" ", "");
//                phone = phone.replace("+86", "");
//                temp.setTelephone(phone);
//            }
            DBManager.getInstance(context).insertContacts(temp);
            //记得要把cursor给close掉
//            phoneCursor.close();
        }
        cursor.close();

    }

    public static List<ContactsDetailBean> searchContacts(String text) {
        return DBManager.getInstance(MyApplication.getContext()).selectContacts(text);
    }

    /**
     * 获取好友备注
     *
     * @return
     */
    public static String getFriendNickName(Context context, String tel) {
        FriendBean friendBean = DBManager.getInstance(context).selectFriend(tel);
        if (friendBean != null) {
            return friendBean.nickName;
        } else {
            return "";
        }
    }

    /**
     * 判断是好友
     *
     * @param context
     * @param tel
     * @return
     */
    public static Boolean getIsFriend(Context context, String tel) {
        FriendBean friendBean = DBManager.getInstance(context).selectFriend(tel);
        return friendBean != null;
    }
}
