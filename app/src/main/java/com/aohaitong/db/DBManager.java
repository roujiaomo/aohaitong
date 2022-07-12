package com.aohaitong.db;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;

import com.aohaitong.MyApplication;
import com.aohaitong.bean.BroadBean;
import com.aohaitong.bean.ChatMsgBean;
import com.aohaitong.bean.ContactsBean;
import com.aohaitong.bean.FriendApplyBean;
import com.aohaitong.bean.FriendBean;
import com.aohaitong.bean.GroupBean;
import com.aohaitong.bean.MessageBean;
import com.aohaitong.constant.StatusConstant;
import com.aohaitong.ui.model.ContactsDetailBean;
import com.aohaitong.utils.DateUtil;
import com.aohaitong.utils.StringUtil;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class DBManager {
    private final static String dbName = "AOHAI";
    @SuppressLint("StaticFieldLeak")
    private static DBManager mInstance;
    private GreenDaoUpdateHelper openHelper;
    private final Context context;

    public DBManager(Context context) {
        this.context = context;
        openHelper = new GreenDaoUpdateHelper(context, dbName, null);
    }

    /**
     * 获取单例引用
     */
    public static DBManager getInstance(Context context) {
        if (mInstance == null) {
            synchronized (DBManager.class) {
                if (mInstance == null) {
                    mInstance = new DBManager(context);
                }
            }
        }
        return mInstance;
    }

    /**
     * 获取可读数据库
     */
    private SQLiteDatabase getReadableDatabase() {
        if (openHelper == null) {
            openHelper = new GreenDaoUpdateHelper(context, dbName, null);
        }
        return openHelper.getReadableDatabase();
    }

    /**
     * 获取可写数据库
     */
    private SQLiteDatabase getWritableDatabase() {
        if (openHelper == null) {
            openHelper = new GreenDaoUpdateHelper(context, dbName, null);
        }
        return openHelper.getWritableDatabase();
    }

    /**
     * 插入用户集合
     */
    public void insertContactsList(List<ContactsBean> contacts) {
        if (contacts == null || contacts.isEmpty()) {
            return;
        }
        DaoMaster daoMaster = new DaoMaster(getWritableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        ContactsBeanDao myDao = daoSession.getContactsBeanDao();
        myDao.insertInTx(contacts);
    }

    /**
     * 插入用户集合
     */
    public void insertContacts(ContactsBean contacts) {
        DaoMaster daoMaster = new DaoMaster(getWritableDatabase());
        try {
            String sql = "replace into  CONTACTS_BEAN(TELEPHONE,NAME)values('" + contacts.getTelephone() + "'," +
                    "'" + contacts.getName() + "')";
            daoMaster.newSession().getDatabase().execSQL(sql);
        } catch (SQLException ignored) {
        }
    }

    /**
     * 查询用户列表
     */
    public List<ContactsBean> queryContactsList() {
        DaoMaster daoMaster = new DaoMaster(getReadableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        ContactsBeanDao myDao = daoSession.getContactsBeanDao();
        QueryBuilder<ContactsBean> qb = myDao.queryBuilder();
        return qb.list();
    }

    /**
     * 进入聊天页面调用
     * ChatActivity
     * 查询同一号码的消息
     * 并且批量更新阅读状态
     *
     * @param telNum 电话号码
     */
    public List<ChatMsgBean> getNewsMsg(String telNum) {
        DaoMaster daoMaster = new DaoMaster(getReadableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        ChatMsgBeanDao myDao = daoSession.getChatMsgBeanDao();
        List<ChatMsgBean> beans = myDao.queryBuilder().where(ChatMsgBeanDao.Properties.Telephone.eq(telNum),
                ChatMsgBeanDao.Properties.NowLoginTel.eq(MyApplication.TEL))
                .orderDesc(ChatMsgBeanDao.Properties.Time).list();

        Collections.reverse(beans);
        updateAll(telNum);
        return beans;
    }

    /**
     * 进入聊天页面调用
     * ChatActivity
     * 查询同一号码的消息
     * 并且批量更新阅读状态
     *
     * @param telNum 电话号码
     */
    public List<ChatMsgBean> getNewsMsg1(String telNum) {
        DaoMaster daoMaster = new DaoMaster(getReadableDatabase());
        List<ChatMsgBean> beans = new ArrayList<>();
        try {
            String sql = "select * from CHAT_MSG_BEAN a where a.TELEPHONE = ? " +
                    " and NOW_LOGIN_TEL = '" + MyApplication.TEL + "' order by a.TIME desc";/* limit 20*/
            Cursor cursor = daoMaster.newSession().getDatabase().rawQuery(sql, new String[]{telNum});
            addChatMsg(beans, cursor);
            Collections.reverse(beans);
            updateAll(telNum);
        } catch (SQLException exception) {
            exception.printStackTrace();
            return beans;
        }
        return beans;
    }

    /**
     * 接收新消息使用，用来过滤重复消息
     *
     * @param telNum 电话号码
     */
    public List<ChatMsgBean> getNewsMsgLimit(String telNum) {
        DaoMaster daoMaster = new DaoMaster(getReadableDatabase());
        List<ChatMsgBean> beans = new ArrayList<>();
        Calendar newTime = Calendar.getInstance();
        newTime.setTime(new Date());
        newTime.add(Calendar.SECOND, -6);
        long queryTime = newTime.getTime().getTime();
        try {
            String sql = "select * from CHAT_MSG_BEAN a where a.TELEPHONE = ? " +
                    " and NOW_LOGIN_TEL = '" + MyApplication.TEL + "' and a.INSERT_TIME >=" + queryTime + " order by a.TIME desc";
            Cursor cursor = daoMaster.newSession().getDatabase().rawQuery(sql, new String[]{telNum});
            addChatMsg(beans, cursor);
            Collections.reverse(beans);
//            updateAll(telNum);
        } catch (SQLException ignored) {
        }
        return beans;
    }


    /**
     * 更新消息为已读
     *
     * @param tel 号码
     */
    private void updateAll(String tel) {
        String sql = "update CHAT_MSG_BEAN set STATUS ='" + StatusConstant.READ_READED + "' where TELEPHONE='" + tel + "' and SEND_TYPE ='1'";
        DaoMaster daoMaster = new DaoMaster(getReadableDatabase());
        try {
            daoMaster.newSession().getDatabase().execSQL(sql);
        } catch (SQLException ignored) {
        }
    }

    //将游标里面查询出来的对象转化到list中
    private void addChatMsg(List<ChatMsgBean> beans, Cursor cursor) {
        int idIndex = cursor.getColumnIndex(ChatMsgBeanDao.Properties.Id.columnName);
        int msgIndex = cursor.getColumnIndex(ChatMsgBeanDao.Properties.Msg.columnName);
        int sendTypeIndex = cursor.getColumnIndex(ChatMsgBeanDao.Properties.SendType.columnName);
        int timeIndex = cursor.getColumnIndex(ChatMsgBeanDao.Properties.Time.columnName);
        int statusIndex = cursor.getColumnIndex(ChatMsgBeanDao.Properties.Status.columnName);
        int nowLoginIndex = cursor.getColumnIndex(ChatMsgBeanDao.Properties.NowLoginTel.columnName);
        int telephoneIndex = cursor.getColumnIndex(ChatMsgBeanDao.Properties.Telephone.columnName);
        int messageTypeIndex = cursor.getColumnIndex(ChatMsgBeanDao.Properties.MessageType.columnName);
        int filePathIndex = cursor.getColumnIndex(ChatMsgBeanDao.Properties.FilePath.columnName);
        int recordTimeIndex = cursor.getColumnIndex(ChatMsgBeanDao.Properties.RecordTime.columnName);
        Log.d("zzzzz", "cursor getCount: " + cursor.getCount());
        while (cursor.moveToNext()) {
            Log.d("zzzzz", "cursor addChatMsg: " + cursor.getPosition());

            ChatMsgBean bean = new ChatMsgBean();
            bean.setId(cursor.getLong(idIndex));
            bean.setMsg(cursor.getString(msgIndex));
            bean.setSendType(cursor.getInt(sendTypeIndex));
            bean.setTime(cursor.getString(timeIndex));
            bean.setStatus(cursor.getInt(statusIndex));
            bean.setNowLoginTel(cursor.getString(nowLoginIndex));
            bean.setTelephone(cursor.getString(telephoneIndex));
            bean.setMessageType(cursor.getInt(messageTypeIndex));
            bean.setFilePath(cursor.getString(filePathIndex));
            bean.setRecordTime(cursor.getInt(recordTimeIndex));
            if (!beans.contains(bean)) {
                beans.add(bean);
            }
        }
    }

    private void addMsg(List<MessageBean> beans, Cursor cursor) {
        int msgIndex = cursor.getColumnIndex(ChatMsgBeanDao.Properties.Msg.columnName);
        int timeIndex = cursor.getColumnIndex(ChatMsgBeanDao.Properties.Time.columnName);
        int telephoneIndex = cursor.getColumnIndex(ChatMsgBeanDao.Properties.Telephone.columnName);
        int messageType = cursor.getColumnIndex(ChatMsgBeanDao.Properties.MessageType.columnName);
        int groupId = cursor.getColumnIndex(ChatMsgBeanDao.Properties.GroupId.columnName);
        int nameIndex = cursor.getColumnIndex(ContactsBeanDao.Properties.Name.columnName);
        int countIndex = cursor.getColumnIndex("COUNT");
        int friendNameIndex = cursor.getColumnIndex("FRIEND_NAME");
        int nickNameIndex = cursor.getColumnIndex("NICK_NAME");

        while (cursor.moveToNext()) {
            MessageBean bean = new MessageBean();
            bean.setMessage(cursor.getString(msgIndex));
            bean.setName(StringUtil.getFirstNotNullString(new String[]{cursor.getString(friendNameIndex), cursor.getString(nameIndex)}));
            bean.setNickName(cursor.getString(nickNameIndex));
            bean.setTelephone(cursor.getString(telephoneIndex));
            bean.setTime(cursor.getString(timeIndex));
            bean.setUnReadCount(cursor.getString(countIndex));
            bean.setMessageType(cursor.getInt(messageType));
            bean.setGroupId(cursor.getString(groupId));
            bean.setIsGroup(!TextUtils.isEmpty(cursor.getString(groupId)));
            boolean isExistChat = false;
            for (MessageBean messageBean : beans) {
                if (bean.getIsGroup()) {
                    if (messageBean.getGroupId().equals(bean.getGroupId())) {
                        isExistChat = true;
                    }
                } else {
                    if (messageBean.getTelephone().equals(bean.getTelephone())) {
                        isExistChat = true;
                    }
                }
            }
            if (!isExistChat) {
                beans.add(bean);
            }
//            boolean isExistChat = false;
//            for (MessageBean messageBean : beans) {
//                //已经存在该私聊
//                if (messageBean.getTelephone().equals(bean.getTelephone()) ) {
//                    isExistChat = true;
//                }
//                if (messageBean.getGroupId() != null && messageBean.getGroupId().equals(bean.getGroupId())) {
//                    isExistChat = true;
//                }
//            }

        }
    }

    /**
     * 首页聊天页面调用
     * MessageFragment
     * 获取所有聊天记录,按号码分类
     */
    public List<MessageBean> getMsgList(String searchStr) {
        List<MessageBean> beans = new ArrayList<>();
        String chatSQL = "select s.COUNT, b.* ,c.NAME,d.NAME as FRIEND_NAME,d.NICK_NAME as NICK_NAME from\n" +
                "(select TELEPHONE,GROUP_ID,IS_GROUP, max(TIME) as TIME, MESSAGE_TYPE from CHAT_MSG_BEAN where IS_GROUP= '0' group by TELEPHONE order by TIME) a \n" +
                "left join  (select * from CHAT_MSG_BEAN   group by TIME)  b on b.TIME = a.TIME \n" +
                "left join CONTACTS_BEAN c on c.TELEPHONE = b.TELEPHONE\n" +
                "left join FRIEND_BEAN d on d.TELEPHONE=b.TELEPHONE\n" +
                "left join(select TELEPHONE,count(TIME) as COUNT from CHAT_MSG_BEAN where SEND_TYPE='1' and STATUS = '0' and IS_GROUP = '0' group by TELEPHONE) " + " s on s.TELEPHONE = b.TELEPHONE" +
                " where b.NOW_LOGIN_TEL=" + MyApplication.TEL;
        String searchSQL = TextUtils.isEmpty(searchStr) ? "" : "  and b.TELEPHONE like '%" + searchStr + "%' or c.NAME like '%" + searchStr + "%' or d.NAME like '%" + searchStr + "%' or d.NICK_NAME like '%" + searchStr + "%' ";
        String descSQL = " order by b.TIME desc";
        DaoMaster daoMaster = new DaoMaster(getReadableDatabase());
        try {
            Cursor cursor = daoMaster.newSession().getDatabase().rawQuery(chatSQL + searchSQL + descSQL, null);
            addMsg(beans, cursor);
        } catch (SQLException ignored) {
        }
        String sql = "select s.COUNT, b.* ,c.NAME,d.NAME as FRIEND_NAME,d.NICK_NAME as NICK_NAME from\n" +
                "(select GROUP_ID,IS_GROUP, max(TIME) as TIME, MESSAGE_TYPE from CHAT_MSG_BEAN where IS_GROUP= '1' group by GROUP_ID order by TIME) a \n" +
                "left join  (select * from CHAT_MSG_BEAN group by TIME)  b on b.TIME = a.TIME  \n" +
                "left join CONTACTS_BEAN c on c.TELEPHONE = b.TELEPHONE\n" +
                "left join FRIEND_BEAN d on d.TELEPHONE=b.TELEPHONE\n" +
                "left join(select GROUP_ID,count(TIME) as COUNT from CHAT_MSG_BEAN where SEND_TYPE='" + StatusConstant.SEND_TYPE_RECEIVER +
                "' and STATUS='" + StatusConstant.READ_UNREAD + "' group by GROUP_ID) " + " s on s.GROUP_ID = b.GROUP_ID" +
                " where b.NOW_LOGIN_TEL=" + MyApplication.TEL;
        try {
            Cursor cursor = daoMaster.newSession().getDatabase().rawQuery(sql + searchSQL + descSQL, null);
            addMsg(beans, cursor);
        } catch (SQLException ignored) {
        }
        return beans;
    }

    /**
     * 更新消息
     */
    public void updateMsg(ChatMsgBean bean) {
        if (bean.getMessageType() == 1 || bean.getMessageType() == 2 || bean.getMessageType() == 3) {
            bean.setMsg("");
        }
//        if (bean.getMessageType() != 0) {
//            bean.setMsg("");
//        }
        DaoMaster daoMaster = new DaoMaster(getWritableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        ChatMsgBeanDao myDao = daoSession.getChatMsgBeanDao();
        myDao.update(bean);
    }

    /**
     * 批量更新
     */
    public void updateMsgList(List<ChatMsgBean> list) {
        DaoMaster daoMaster = new DaoMaster(getWritableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        ChatMsgBeanDao myDao = daoSession.getChatMsgBeanDao();
        myDao.updateInTx(list);
    }

    /**
     * 消息表中插入新数据
     * 收到新消息和发送消息时候调用
     */
    public void createMsg(ChatMsgBean msgBean) {
        if (msgBean.getMsg() != null && msgBean.getMsg().length() > 100) {
            msgBean.setMsg("");
        }
        if (msgBean.getMessageType() == 1 || msgBean.getMessageType() == 2 || msgBean.getMessageType() == 3) {
            msgBean.setMsg("");
        }
        DaoMaster daoMaster = new DaoMaster(getWritableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        ChatMsgBeanDao myDao = daoSession.getChatMsgBeanDao();
        long s = myDao.insert(msgBean);
        if (s <= 0) {
            createMsg(msgBean);
        }
    }

    public ChatMsgBean queryChatById(Long id) {
        DaoMaster daoMaster = new DaoMaster(getWritableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        ChatMsgBeanDao myDao = daoSession.getChatMsgBeanDao();
        List list = myDao.queryBuilder()
                .where(ChatMsgBeanDao.Properties.Id.eq(id))
                .list();
        return (list != null && list.size() > 0) ? (ChatMsgBean) list.get(0) : null;
    }

    /**
     * 删除全部联系人
     */
    public void deleteContacts() {
        DaoMaster daoMaster = new DaoMaster(getWritableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        ContactsBeanDao myDao = daoSession.getContactsBeanDao();
        myDao.deleteAll();
    }


//    /**
//     * 搜索聊天记录
//     *
//     * @param text      搜索用语句
//     * @param telephone 电话,为空的时候全局搜索
//     */
//    public List<MessageBean> searchByMsg(String text, String telephone) {
//        String sql = "select a.* from CHAT_MSG_BEAN a \n" +
//                "left join CONTACTS_BEAN c on c.TELEPHONE = a.TELEPHONE\n" +
//                " where a.NOW_LOGIN_TEL=" + MyApplication.TEL;
//        String str = TextUtils.isEmpty(telephone) ? "and a.MSG like '%" + text + "%'" :
//                "and a.MSG like '%\" + text + \"%'  and a.TELEPHONE like '%" + telephone + "%'";
//        String endStr = " order by b.TIME desc";
//        DaoMaster daoMaster = new DaoMaster(getReadableDatabase());
//        List<MessageBean> beans = new ArrayList<>();
//        try {
//            Cursor cursor = daoMaster.newSession().getDatabase().rawQuery(sql + str + endStr, null);
//            addMsg(beans, cursor);
//        } catch (SQLException ignored) {
//        }
//        return beans;
//    }

    /**
     * 删除单条消息
     *
     * @param id 单条消息的id
     */
    public void delMsgById(Long id) {
        DaoMaster daoMaster = new DaoMaster(getWritableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        ChatMsgBeanDao myDao = daoSession.getChatMsgBeanDao();
        myDao.deleteByKey(id);
    }

    /**
     * 删除某个号码/群组的所有消息
     *
     * @param telOrGroupId 电话号码/群聊ID
     */
    public void delMsgByTel(String telOrGroupId, boolean isGroup) {
        DaoMaster daoMaster = new DaoMaster(getWritableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        ChatMsgBeanDao myDao = daoSession.getChatMsgBeanDao();
        if (!isGroup) { //私聊
            myDao.queryBuilder().where(ChatMsgBeanDao.Properties.Telephone.eq(telOrGroupId)).buildDelete().executeDeleteWithoutDetachingEntities();
        } else { //群聊
            myDao.queryBuilder().where(ChatMsgBeanDao.Properties.GroupId.eq(telOrGroupId)).buildDelete().executeDeleteWithoutDetachingEntities();
        }
    }

    /**
     * 删除全部消息(清除数据)
     */
    public void delAllMsg() {
        DaoMaster daoMaster = new DaoMaster(getWritableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        ChatMsgBeanDao myDao = daoSession.getChatMsgBeanDao();
        myDao.deleteAll();
    }

    /**
     * 查询好友
     *
     * @param text 查询内容
     */
    public List<FriendBean> selectAllFriend(String text) {
        DaoMaster daoMaster = new DaoMaster(getWritableDatabase());
        String sql = "select a.*,b.NAME as CONTACTS_NAME from FRIEND_BEAN a " +
                "left join CONTACTS_BEAN b on a.TELEPHONE =b.TELEPHONE" +
                " where (a.TELEPHONE like '%" + text + "%' " +
                "or a.NAME like '%" + text + "%' " +
                "or a.NICK_NAME like '%" + text + "%' )" +
                "and a.NOW_LOGIN_TEL ='" + MyApplication.TEL + "'";
        List<FriendBean> beans = new ArrayList<>();
        try {
            Cursor cursor = daoMaster.newSession().getDatabase().rawQuery(sql, null);
            addFriend(beans, cursor);
        } catch (SQLException ignored) {
        }
        return beans;
    }

    /**
     * 查询单个好友信息
     */
    public FriendBean selectFriend(String tel) {
        DaoMaster daoMaster = new DaoMaster(getWritableDatabase());
        String sql = "select * from FRIEND_BEAN  where TELEPHONE = '" + tel + "' " +
                "and NOW_LOGIN_TEL ='" + MyApplication.TEL + "'";
        FriendBean bean = null;
        try {
            Cursor cursor = daoMaster.newSession().getDatabase().rawQuery(sql, null);
            int nameIndex = cursor.getColumnIndex(FriendBeanDao.Properties.Name.columnName);
            int nickNameIndex = cursor.getColumnIndex(FriendBeanDao.Properties.NickName.columnName);
            while (cursor.moveToNext()) {
                bean = new FriendBean();
                bean.setTelephone(tel);
                bean.setName(cursor.getString(nameIndex));
                bean.setNickName(cursor.getString(nickNameIndex));
            }
        } catch (SQLException ignored) {
        }
        return bean;
    }

    private void addFriend(List<FriendBean> beans, Cursor cursor) {
        int nameIndex = cursor.getColumnIndex(FriendBeanDao.Properties.Name.columnName);
        int nickNameIndex = cursor.getColumnIndex(FriendBeanDao.Properties.NickName.columnName);
        int telephoneIndex = cursor.getColumnIndex(FriendBeanDao.Properties.Telephone.columnName);
        int contactsNameIndex = cursor.getColumnIndex("CONTACTS_NAME");
        while (cursor.moveToNext()) {
            FriendBean bean = new FriendBean();
            bean.setName(TextUtils.isEmpty(cursor.getString(nameIndex)) ? cursor.getString(contactsNameIndex) : cursor.getString(nameIndex));
            bean.setTelephone(cursor.getString(telephoneIndex));
            bean.setNickName(cursor.getString(nickNameIndex));
            beans.add(bean);
        }
    }

    /**
     * 批量更新好友信息
     *
     * @param data 从服务器来的好友信息
     */
    public void updateFriendList(List<FriendBean> data) {
       /* DaoMaster daoMaster = new DaoMaster(getWritableDatabase());
        try {
            for (FriendBean datum : data) {
                String sql = "replace into  FRIEND_BEAN(NICK_NAME,TELEPHONE,NAME,NOW_LOGIN_TEL)values('" + datum.getNickName() + "'," +
                        "'" + datum.getTelephone() + "','" + datum.getName() + "','" + datum.getNowLoginTel() + "')";
                daoMaster.newSession().getDatabase().execSQL(sql);
            }
        } catch (SQLException ignored) {
        }*/
        DaoMaster daoMaster = new DaoMaster(getWritableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        FriendBeanDao myDao = daoSession.getFriendBeanDao();
        myDao.deleteAll();
        myDao.insertInTx(data);
    }

    /**
     * 修改好友昵称
     *
     * @param telephone 好友电话号码
     * @param nickName  好友新昵称
     */
    public void updateFriendNickName(String telephone, String nickName) {
        DaoMaster daoMaster = new DaoMaster(getWritableDatabase());
        String sql = "update FRIEND_BEAN set NICK_NAME ='" + nickName + "' where TELEPHONE='" + telephone + "'";
        try {
            daoMaster.newSession().getDatabase().execSQL(sql);
        } catch (SQLException ignored) {
        }
    }

    /**
     * 修改好友姓名
     *
     * @param telephone 好友电话号码
     * @param name      好友新名字
     */
    public void updateFriendName(String telephone, String name) {
        DaoMaster daoMaster = new DaoMaster(getWritableDatabase());
        String sql = "update FRIEND_BEAN set NAME ='" + name + "' where TELEPHONE='" + telephone + "'";
        try {
            daoMaster.newSession().getDatabase().execSQL(sql);
        } catch (SQLException ignored) {
        }
    }

    /**
     * 新增好友
     *
     * @param bean 好友信息
     */
    public void insertFriend(FriendBean bean) {
        DaoMaster daoMaster = new DaoMaster(getWritableDatabase());
        try {
            String sql = "replace into  FRIEND_BEAN(TELEPHONE,NICK_NAME,NAME,NOW_LOGIN_TEL)values('" + bean.getTelephone() + "'," +
                    "'" + bean.getNickName() + "'," +
                    "'" + bean.getName() + "'," +
                    "'" + bean.getNowLoginTel() + "')";
            daoMaster.newSession().getDatabase().execSQL(sql);
            updateFriendApply(bean.getTelephone(), StatusConstant.TYPE_PASS);
        } catch (SQLException ignored) {
        }
    }

    public void deleteFriendApply(String tel) {
        DaoMaster daoMaster = new DaoMaster(getWritableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        FriendApplyBeanDao myDao = daoSession.getFriendApplyBeanDao();
        myDao.queryBuilder().where(FriendApplyBeanDao.Properties.Telephone.eq(tel)).buildDelete().executeDeleteWithoutDetachingEntities();
    }

    /**
     * 删除好友
     *
     * @param tel 好友信息
     */
    public void deleteFriend(String tel) {
        DaoMaster daoMaster = new DaoMaster(getWritableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        FriendBeanDao myDao = daoSession.getFriendBeanDao();
        myDao.queryBuilder().where(FriendBeanDao.Properties.Telephone.eq(tel)).buildDelete().executeDeleteWithoutDetachingEntities();
    }

    /**
     * 新增好友申请信息
     *
     * @param bean 好友申请信息
     */
    public void insertFriendApply(FriendApplyBean bean) {
        DaoMaster daoMaster = new DaoMaster(getWritableDatabase());
        try {
            String sql = "replace into  FRIEND_APPLY_BEAN(TELEPHONE,NAME,NICK_NAME,DESCRIPTION,TIME,TYPE,NOW_LOGIN_TEL,SEND_TYPE,STATUS)values('"
                    + bean.getTelephone() + "'," +
                    "'" + bean.getName() + "'," +
                    "'" + bean.getNickName() + "'," +
                    "'" + bean.getDescription() + "'," +
                    "'" + bean.getTime() + "'," +
                    "" + bean.getType() + "," +
                    "'" + bean.getNowLoginTel() + "'," +
                    "" + bean.getSendType() + "," +
                    "" + bean.getStatus() + ")";
            daoMaster.newSession().getDatabase().execSQL(sql);
        } catch (SQLException ignored) {
        }
    }

    /**
     * 更新好友申请
     *
     * @param tel  电话号码
     * @param type 是否通过
     */
    public void updateFriendApply(String tel, int type) {
        DaoMaster daoMaster = new DaoMaster(getWritableDatabase());
        String sql = "update FRIEND_APPLY_BEAN set TYPE ='" + type + "' where TELEPHONE='" + tel + "'";
        try {
            daoMaster.newSession().getDatabase().execSQL(sql);
        } catch (SQLException ignored) {
        }
    }

    /**
     * 获取所有好友申请
     * 并更新状态为已读
     */
    public List<FriendApplyBean> selectFriendApply(String text) {
        DaoMaster daoMaster = new DaoMaster(getWritableDatabase());
        String sql = "select * from FRIEND_APPLY_BEAN  where (TELEPHONE like '%" + text + "%' " +
                "or NAME like '%" + text + "%' " +
                "or NICK_NAME like '%" + text + "%' )" +
                "and NOW_LOGIN_TEL ='" + MyApplication.TEL + "'";
        List<FriendApplyBean> beans = new ArrayList<>();
        try {
            Cursor cursor = daoMaster.newSession().getDatabase().rawQuery(sql, null);
            addFriendApply(beans, cursor);
        } catch (SQLException ignored) {
        }
        updateFriendApplyStatus();
        return beans;
    }

    /**
     * 获取所有好友申请的时候设置的昵称
     */
    public String selectFriendApplyNickName(String tel) {
        DaoMaster daoMaster = new DaoMaster(getWritableDatabase());
        String sql = "select * from FRIEND_APPLY_BEAN  where TELEPHONE = '" + tel + "' " +
                "and NOW_LOGIN_TEL ='" + MyApplication.TEL + "'";
        try {
            Cursor cursor = daoMaster.newSession().getDatabase().rawQuery(sql, null);
            int nickNameIndex = cursor.getColumnIndex(FriendApplyBeanDao.Properties.NickName.columnName);
            if (cursor.moveToNext()) {
                return cursor.getString(nickNameIndex);
            }
        } catch (SQLException ignored) {
        }
        return "";
    }

    /**
     * 更新所有好友申请状态为已读
     */
    private void updateFriendApplyStatus() {
        String sql = "update FRIEND_APPLY_BEAN set STATUS = " + StatusConstant.READ_READED +
                " where NOW_LOGIN_TEL ='" + MyApplication.TEL + "'";
        DaoMaster daoMaster = new DaoMaster(getReadableDatabase());
        try {
            daoMaster.newSession().getDatabase().execSQL(sql);
        } catch (SQLException ignored) {
        }
    }

    /**
     * 获取等待处理的好友申请数量
     */
    public int selectFriendApplyCount() {
        DaoMaster daoMaster = new DaoMaster(getWritableDatabase());
        String sql = "select count(*) as COUNT from FRIEND_APPLY_BEAN  where " +
                " SEND_TYPE = '" + StatusConstant.SEND_TYPE_RECEIVER + "' " +
                "and STATUS = " + StatusConstant.READ_UNREAD +
                " and NOW_LOGIN_TEL ='" + MyApplication.TEL + "'";
        try {
            Cursor cursor = daoMaster.newSession().getDatabase().rawQuery(sql, null);
            int countIndex = cursor.getColumnIndex("COUNT");
            cursor.moveToNext();
            return cursor.getInt(countIndex);
        } catch (SQLException ignored) {
            return 0;
        }
    }

    private void addFriendApply(List<FriendApplyBean> beans, Cursor cursor) {
        int nameIndex = cursor.getColumnIndex(FriendApplyBeanDao.Properties.Name.columnName);
        int descriptionIndex = cursor.getColumnIndex(FriendApplyBeanDao.Properties.Description.columnName);
        int nickNameIndex = cursor.getColumnIndex(FriendApplyBeanDao.Properties.NickName.columnName);
        int telephoneIndex = cursor.getColumnIndex(FriendApplyBeanDao.Properties.Telephone.columnName);
        int timeIndex = cursor.getColumnIndex(FriendApplyBeanDao.Properties.Time.columnName);
        int typeIndex = cursor.getColumnIndex(FriendApplyBeanDao.Properties.Type.columnName);
        int sendTypeIndex = cursor.getColumnIndex(FriendApplyBeanDao.Properties.SendType.columnName);
        while (cursor.moveToNext()) {
            FriendApplyBean bean = new FriendApplyBean();
            bean.setName(cursor.getString(nameIndex));
            bean.setTelephone(cursor.getString(telephoneIndex));
            bean.setDescription(cursor.getString(descriptionIndex));
            bean.setNickName(cursor.getString(nickNameIndex));
            bean.setSendType(cursor.getInt(sendTypeIndex));
            bean.setTime(cursor.getString(timeIndex));
            bean.setType(cursor.getInt(typeIndex));
            beans.add(bean);
        }
    }

    /**
     * 搜索联系人信息
     */
    public List<ContactsDetailBean> selectContacts(String text) {
        DaoMaster daoMaster = new DaoMaster(getWritableDatabase());
        String sql = "select a.*,b.NAME as friendName,b.NICK_NAME as nickName,b.TELEPHONE as isFriend,c.TELEPHONE as hasApply,c.TYPE as type from CONTACTS_BEAN a " +
                "left join FRIEND_BEAN b on b.TELEPHONE=a.TELEPHONE " +
                " and b.NOW_LOGIN_TEL ='" + MyApplication.TEL + "' " +
                "left join FRIEND_APPLY_BEAN c on c.TELEPHONE=a.TELEPHONE " +
                " and c.NOW_LOGIN_TEL ='" + MyApplication.TEL + "' " +
                "where (a.NAME like '%" + text + "%' " +
                "or a.TELEPHONE like '%" + text + "%' )" +
                " and a.TELEPHONE !='" + MyApplication.TEL + "' ";
        List<ContactsDetailBean> beans = new ArrayList<>();
        try {
            Cursor cursor = daoMaster.newSession().getDatabase().rawQuery(sql, null);
            addContacts(beans, cursor);
        } catch (SQLException ignored) {
        }
        return beans;
    }

    private void addContacts(List<ContactsDetailBean> beans, Cursor cursor) {
        int nameIndex = cursor.getColumnIndex(FriendApplyBeanDao.Properties.Name.columnName);
        int telephoneIndex = cursor.getColumnIndex(FriendApplyBeanDao.Properties.Telephone.columnName);
        int friendIndex = cursor.getColumnIndex("isFriend");
        int friendApplyIndex = cursor.getColumnIndex("hasApply");
        int typeIndex = cursor.getColumnIndex("type");
        int friendNameIndex = cursor.getColumnIndex("friendName");
        int nickNameIndex = cursor.getColumnIndex("nickName");
        while (cursor.moveToNext()) {
            ContactsDetailBean bean = new ContactsDetailBean();
            bean.setName(cursor.getString(nameIndex));
            bean.setTelephone(cursor.getString(telephoneIndex));
            bean.setFriendApplyType(TextUtils.isEmpty(cursor.getString(friendApplyIndex)) ?
                    StatusConstant.TYPE_NOT_HAVE_APPLY : StatusConstant.TYPE_HAVE_APPLY);
            bean.setFriendType(TextUtils.isEmpty(cursor.getString(friendIndex)) ?
                    StatusConstant.TYPE_IS_NOT_FRIEND : StatusConstant.TYPE_IS_FRIEND);
            bean.setType(cursor.getInt(typeIndex));
            String nickName = cursor.getString(nickNameIndex);
            String friendName = cursor.getString(friendNameIndex);
            bean.setNickName(TextUtils.isEmpty(nickName) ? "" : nickName);
            bean.setFriendName(TextUtils.isEmpty(friendName) ? "" : friendName);
            beans.add(bean);
        }
    }

    /*-----------------广播----------------------*/

    public void addBroad(String msg, int businessType, long time) {
        DaoMaster daoMaster = new DaoMaster(getWritableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        BroadBeanDao myDao = daoSession.getBroadBeanDao();
        BroadBean broadBean = new BroadBean();
        broadBean.setContent(msg);
        broadBean.setBusinessType(businessType);
        broadBean.setReceiveTime(time);
        myDao.insert(broadBean);
    }

    /**
     * 获取近三天的广播
     */
    public List<BroadBean> selectBroadList() {
        List<BroadBean> list = new ArrayList<>();
        DaoMaster daoMaster = new DaoMaster(getWritableDatabase());
        String sql = "select * from BROAD_BEAN  where RECEIVE_TIME >" + (DateUtil.getInstance().getTime() - 60 * 1000 * 60 * 24 * 3) + " order by RECEIVE_TIME";
        try {
            Cursor cursor = daoMaster.newSession().getDatabase().rawQuery(sql, null);
            addBroadList(list, cursor);
        } catch (SQLException ignored) {
        }
        deleteBroadThreeDaysAgo();
        return list;
    }

    private void addBroadList(List<BroadBean> list, Cursor cursor) {
        int receiveTimeIndex = cursor.getColumnIndex(BroadBeanDao.Properties.ReceiveTime.columnName);
        int businessTypeIndex = cursor.getColumnIndex(BroadBeanDao.Properties.BusinessType.columnName);
        int contentIndex = cursor.getColumnIndex(BroadBeanDao.Properties.Content.columnName);
        while (cursor.moveToNext()) {
            BroadBean bean = new BroadBean();
            bean.setBusinessType(cursor.getInt(businessTypeIndex));
            bean.setReceiveTime(cursor.getLong(receiveTimeIndex));
            bean.setContent(cursor.getString(contentIndex));
            list.add(bean);
        }
    }

    /**
     * 删除三天前的所有广播
     */
    private void deleteBroadThreeDaysAgo() {
        DaoMaster daoMaster = new DaoMaster(getWritableDatabase());
        String sql = "delete * from BROAD_BEAN  where RECEIVE_TIME <" + (DateUtil.getInstance().getTime() - 60 * 1000 * 60 * 24 * 3);
        try {
            daoMaster.newSession().getDatabase().execSQL(sql);
        } catch (SQLException ignored) {
        }
    }

    /**
     * 获取手机号是否为好友,是好友则返回相应显示的名称
     */
    public String getTelephoneShowName(String tel) {
        FriendBean friendBean = selectFriend(tel);
        if (friendBean != null) {
            return StringUtil.getFirstNotNullString(
                    new String[]{
                            friendBean.getNickName(),
                            friendBean.getName(),
                            friendBean.getTelephone()
                    }
            );
        } else {
            return tel;
        }
    }

    /**
     * 检验手机号是否已经是好友或者有了好友申请
     *
     * @return 只要有值就返回1
     */
    public int checkInFriendList(String tel) {
        DaoMaster daoMaster = new DaoMaster(getWritableDatabase());
        String sql = "select count(*) as COUNT from (select NAME,TELEPHONE from FRIEND_BEAN " +
                " where TELEPHONE = '" + tel + "' " +
                " and NOW_LOGIN_TEL ='" + MyApplication.TEL + "' " +
                " union select NAME,TELEPHONE from FRIEND_APPLY_BEAN" +
                " where TELEPHONE = '" + tel + "' " +
                " and TYPE =" + StatusConstant.TYPE_UN_PASS + "" +
                " and NOW_LOGIN_TEL ='" + MyApplication.TEL + "')";
        try {
            Cursor cursor = daoMaster.newSession().getDatabase().rawQuery(sql, null);
            int countIndex = cursor.getColumnIndex("COUNT");
            while (cursor.moveToNext()) {
                if (cursor.getInt(countIndex) != 0) {
                    return 1;
                }
            }
            return 0;
        } catch (SQLException ignored) {
            return 0;
        }
    }


    /**
     * 将所有状态为发送中的消息状态改为发送失败
     */
    public void updateAllMsgFail() {
        DaoMaster daoMaster = new DaoMaster(getWritableDatabase());
        String sql = "update CHAT_MSG_BEAN set STATUS ='" + StatusConstant.SEND_FAIL + "' where STATUS='" + StatusConstant.SEND_LOADING + "' and SEND_TYPE ='" + StatusConstant.SEND_TYPE_SENDER + "'";
        try {
            daoMaster.newSession().getDatabase().execSQL(sql);
        } catch (SQLException ignored) {
        }
    }


    /**
     *  ---------------------------------- 群聊 --------------------------------------
     */


    /**
     * 创建群聊
     *
     * @param groupId      群聊Id
     * @param groupName    群聊名称
     * @param groupMembers 群聊人员
     */
    public void createGroup(String groupId, String groupName, String groupMembers, String groupOwner) {
        GroupBeanDao groupBeanDao = new DaoMaster(getWritableDatabase()).newSession().getGroupBeanDao();
        GroupBean groupBean = new GroupBean(
                null, groupId, groupName, groupMembers, groupOwner
        );
        groupBeanDao.insert(groupBean);
    }

    /**
     * 更新群组成员信息
     */
    public void updateGroupMember(String groupId, String groupMember) {
        DaoMaster daoMaster = new DaoMaster(getWritableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        GroupBeanDao myDao = daoSession.getGroupBeanDao();
        GroupBean groupBean = myDao.queryBuilder().where(GroupBeanDao.Properties.GroupId.eq(groupId)).unique();
        String existGroupMember = groupBean.getGroupMemberTel();
        String newGroupMember = existGroupMember + groupMember;
        groupBean.setGroupMemberTel(newGroupMember);
        myDao.save(groupBean);
    }

    /**
     * 批量更新群组信息
     */
    public void updateGroupList(List<GroupBean> serviceGroupInfoList) {
        DaoMaster daoMaster = new DaoMaster(getWritableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        GroupBeanDao groupBeanDao = daoSession.getGroupBeanDao();
        for (GroupBean serviceGroupInfo : serviceGroupInfoList) {
            List<GroupBean> list = groupBeanDao.queryBuilder().where(GroupBeanDao.Properties.GroupId.eq(serviceGroupInfo.getGroupId())).list();
            if (list == null || list.size() == 0) {
                groupBeanDao.insert(
                        new GroupBean(
                                serviceGroupInfo.getId(),
                                serviceGroupInfo.getGroupId(),
                                serviceGroupInfo.getGroupName(),
                                serviceGroupInfo.getGroupManagerTel(),
                                serviceGroupInfo.getGroupMemberTel()
                        )
                );
            } else {
                GroupBean groupBean = list.get(0);
                groupBean.setGroupName(serviceGroupInfo.getGroupName());
                groupBean.setGroupManagerTel(serviceGroupInfo.getGroupManagerTel());
                groupBean.setGroupMemberTel(serviceGroupInfo.getGroupMemberTel());
                groupBeanDao.update(groupBean);
            }
        }
    }

    /**
     * 移除群组成员
     */
    public void removeGroupMember(String groupId, String groupMember) {
        DaoMaster daoMaster = new DaoMaster(getWritableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        GroupBeanDao myDao = daoSession.getGroupBeanDao();
        GroupBean groupBean = myDao.queryBuilder().where(GroupBeanDao.Properties.GroupId.eq(groupId)).unique();
        ArrayList<String> existGroupMembers = new ArrayList<>(groupBean.getGroupMemberTel().split("/").length);
        Collections.addAll(existGroupMembers, groupBean.getGroupMemberTel().split("/"));
        ArrayList<String> removeGroupMembers = new ArrayList<>(groupMember.split("/").length);
        Collections.addAll(removeGroupMembers, groupMember.split("/"));

        for (int i = existGroupMembers.size() - 1; i >= 0; i--) {
            for (int r = 0; r < removeGroupMembers.size(); r++) {
                if (existGroupMembers.get(i).equals(removeGroupMembers.get(r))) {
                    existGroupMembers.remove(i);
                    break;
                }
            }
        }
        StringBuilder stringBuilder = new StringBuilder();
        for (String existGroupMember : existGroupMembers) {
            stringBuilder.append(existGroupMember).append("/");
        }
        groupBean.setGroupMemberTel(stringBuilder.toString());
        myDao.update(groupBean);
    }

    /**
     * 删除群聊
     */
    public void deleteGroup(String groupId) {
        DaoMaster daoMaster = new DaoMaster(getWritableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        GroupBeanDao myDao = daoSession.getGroupBeanDao();
        myDao.queryBuilder().where(GroupBeanDao.Properties.GroupId.eq(groupId)).buildDelete().executeDeleteWithoutDetachingEntities();
    }

    /**
     * 更新群组成员信息
     */
    public void updateGroupName(String groupId, String newGroupName) {
        DaoMaster daoMaster = new DaoMaster(getWritableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        GroupBeanDao myDao = daoSession.getGroupBeanDao();
        GroupBean groupBean = myDao.queryBuilder().where(GroupBeanDao.Properties.GroupId.eq(groupId)).unique();
        groupBean.setGroupName(newGroupName);
        myDao.update(groupBean);
    }

    /**
     * 获取群组名称
     */
    public String getGroupManagerById(String groupId) {
        DaoMaster daoMaster = new DaoMaster(getWritableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        GroupBeanDao myDao = daoSession.getGroupBeanDao();
        GroupBean groupBean = myDao.queryBuilder().where(GroupBeanDao.Properties.GroupId.eq(groupId)).unique();
        return groupBean.getGroupManagerTel();
    }

    /**
     * 获取群组名称
     */
    public String getGroupNameById(String groupId) {
        DaoMaster daoMaster = new DaoMaster(getWritableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        GroupBeanDao myDao = daoSession.getGroupBeanDao();
        List<GroupBean> list = myDao.queryBuilder().where(GroupBeanDao.Properties.GroupId.eq(groupId)).list();
        if (list.size() > 0) {
            GroupBean groupBean = list.get(0);
            return groupBean.getGroupName();
        } else {
            return "群聊";
        }
    }


    /**
     * 获取群组信息
     */
    public GroupBean getGroupInfoById(String groupId) {
        DaoMaster daoMaster = new DaoMaster(getWritableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        GroupBeanDao myDao = daoSession.getGroupBeanDao();
        GroupBean groupBean = myDao.queryBuilder().where(GroupBeanDao.Properties.GroupId.eq(groupId)).unique();
        return groupBean;
    }


    /**
     * 获取我的全部群聊
     * (我创建的群聊和我加入的群聊)
     * 群组表本地储存 线上也会储存
     * 删除本地数据不影响线上数据 删除后收到该群消息 再在本地插入该表
     */
    public List<GroupBean> getGroupListByTel(String ownerTel) {
        GroupBeanDao groupBeanDao = new DaoMaster(getWritableDatabase()).newSession().getGroupBeanDao();
        return groupBeanDao.queryBuilder().whereOr(GroupBeanDao.Properties.GroupManagerTel.eq(ownerTel)
                , GroupBeanDao.Properties.GroupMemberTel.like("%" + ownerTel + "%")).list();
    }


    /**
     * 获取群聊聊天记录
     */
    public List<ChatMsgBean> getGroupMessageByGroupId(Long groupId) {
        ChatMsgBeanDao chatMsgBeanDao = new DaoMaster(getWritableDatabase()).newSession().getChatMsgBeanDao();
        List<ChatMsgBean> chatMsgBeanList = chatMsgBeanDao.queryBuilder().where(ChatMsgBeanDao.Properties.GroupId.eq(groupId)).list();
        for (ChatMsgBean chatMsgBean : chatMsgBeanList) {
            if (chatMsgBean.getSendType() == StatusConstant.SEND_TYPE_RECEIVER) {
                chatMsgBean.setStatus(StatusConstant.READ_READED);
            }
        }
        chatMsgBeanDao.updateInTx(chatMsgBeanList);
        return chatMsgBeanList;
    }

    /**
     * 获取群聊聊天记录
     */
    public List<ChatMsgBean> getGroupMessageLimitByGroupId(Long groupId) {
        ChatMsgBeanDao chatMsgBeanDao = new DaoMaster(getWritableDatabase()).newSession().getChatMsgBeanDao();
        return chatMsgBeanDao.queryBuilder().where(ChatMsgBeanDao.Properties.GroupId.eq(groupId)).limit(20).list();
    }


}
