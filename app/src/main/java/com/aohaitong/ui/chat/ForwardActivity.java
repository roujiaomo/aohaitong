package com.aohaitong.ui.chat;

import static com.aohaitong.constant.StatusConstant.ITEM_TYPE_CONTENT;
import static com.aohaitong.constant.StatusConstant.ITEM_TYPE_HEADER;
import static com.aohaitong.constant.StatusConstant.SEND_TYPE_SENDER;

import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aohaitong.MyApplication;
import com.aohaitong.R;
import com.aohaitong.base.BaseActivity;
import com.aohaitong.bean.ChatMsgBean;
import com.aohaitong.bean.FriendBean;
import com.aohaitong.bean.MsgEntity;
import com.aohaitong.business.transmit.BusinessController;
import com.aohaitong.business.transmit.ISendListener;
import com.aohaitong.constant.StatusConstant;
import com.aohaitong.db.DBManager;
import com.aohaitong.ui.adapter.FriendAdapter;
import com.aohaitong.ui.model.FriendLetterIndexModel;
import com.aohaitong.utils.DateUtil;
import com.aohaitong.utils.PinyinUtil;
import com.aohaitong.utils.dialog.MyQmuiDialog;
import com.aohaitong.widget.LetterIndexView;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//转发的activity
public class ForwardActivity extends BaseActivity {
    private List<FriendBean> data = new ArrayList<>();

    private RecyclerView recyclerView;
    private FriendAdapter adapter;
    private LetterIndexView indexView;
    private Map<String, Integer> indexMap = new HashMap<>();
    private List<FriendLetterIndexModel> starList = new ArrayList<>();
    private EditText searchEdit;
    String msg;


    public static void startForwardActivity(Context context, String msg) {
        Intent intent = new Intent(context, ForwardActivity.class);
        intent.putExtra("msg", msg);
        context.startActivity(intent);
    }

    @Override
    protected int getLayout() {
        return R.layout.activity_forward;
    }

    @Override
    protected void initView() {
        ((TextView) bindView(R.id.tv_title)).setText("转发");
        searchEdit = bindView(R.id.edit_search);
        recyclerView = bindView(R.id.recycler);
        indexView = bindView(R.id.vLetterIndex);
        indexView.addLetter(0, "#");
    }

    @Override
    protected void initData() {
        adapter = new FriendAdapter();
        msg = getIntent().getStringExtra("msg");
        doLoadData();
    }

    private void doLoadData() {
        indexMap = new HashMap<>();
        starList = new ArrayList<>();
        data = DBManager.getInstance(context).selectAllFriend(searchEdit.getText().toString());
        new Thread(this::loadData).start();
    }

    @Override
    protected void initEvent() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        indexView.setOnStateChangeListener((eventAction, position, letter, itemCenterY) -> {
            Integer pos = indexMap.get(letter);
            if (pos != null) {
                recyclerView.scrollToPosition(pos);
                LinearLayoutManager mLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                mLayoutManager.scrollToPositionWithOffset(pos, 0);
            }
        });
        adapter.setOnItemClickListener((adapter, view, position) -> {
            loadingDialog.show();
            ChatMsgBean bean = new ChatMsgBean();
            bean.setMsg(msg);
            bean.setNowLoginTel(MyApplication.TEL + "");
            bean.setSendType(SEND_TYPE_SENDER);
            bean.setStatus(StatusConstant.SEND_LOADING);
            bean.setMessageType(StatusConstant.TYPE_TEXT_MESSAGE);
            bean.setTelephone(((FriendLetterIndexModel) adapter.getData().get(position)).tel);
            bean.setTime(DateUtil.getInstance().getTime() + "");
            DBManager.getInstance(MyApplication.getContext()).createMsg(bean);
            EventBus.getDefault().post(new MsgEntity("", StatusConstant.TYPE_CHAT_REFRESH));
            BusinessController.sendMessage(bean, new ISendListener() {
                @Override
                public void sendSuccess() {
                    bean.setStatus(StatusConstant.SEND_SUCCESS);
                    DBManager.getInstance(MyApplication.getContext()).updateMsg(bean);
                    EventBus.getDefault().post(new MsgEntity("", StatusConstant.TYPE_CHAT_REFRESH));
                    loadingDialog.dismiss();
                    MyQmuiDialog.showSuccessDialog(ForwardActivity.this, "转发成功", ForwardActivity.this::finish);
                }

                @Override
                public void sendFail(String reason) {
                    bean.setStatus(StatusConstant.SEND_FAIL);
                    DBManager.getInstance(MyApplication.getContext()).updateMsg(bean);
                    EventBus.getDefault().post(new MsgEntity(reason, StatusConstant.TYPE_CHAT_REFRESH));
                    loadingDialog.dismiss();
                    MyQmuiDialog.showErrorDialog(ForwardActivity.this, reason);
                }
            });
        });

        searchEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                doLoadData();
            }
        });
    }

    private void loadData() {
        List<FriendLetterIndexModel> list = new ArrayList<>();
        for (int i = 0; i < data.size(); i++) {
            FriendLetterIndexModel model = new FriendLetterIndexModel();
            model.name = data.get(i).getName();
            model.nickName = data.get(i).getNickName();
            String pinyin = PinyinUtil.chineneToSpell(model.name);
            model.pinyin = "".equals(pinyin) ? "#" : pinyin;
            model.itemType = ITEM_TYPE_CONTENT;
            model.tel = data.get(i).getTelephone();
            list.add(model);
        }
        List<String> letterList = new ArrayList<>();
        List<FriendLetterIndexModel> tmpList = new ArrayList<>();
        for (FriendLetterIndexModel model : list) {
            String letter = model.pinyin.substring(0, 1).toUpperCase();
            if (!letterList.contains(letter)) {
                FriendLetterIndexModel newModel = new FriendLetterIndexModel();
                newModel.name = letter;
                newModel.pinyin = letter.toLowerCase();
                newModel.itemType = ITEM_TYPE_HEADER;
                if (!"#".equalsIgnoreCase(letter)) {
                    tmpList.add(newModel);
                }
                letterList.add(letter);
            }
        }
        list.addAll(tmpList);

        Collections.sort(list, new PinyinUtil.ContactsPinyinComparator());

        if (letterList.contains("#")) {
            FriendLetterIndexModel model = new FriendLetterIndexModel();
            model.name = "#";
            model.itemType = ITEM_TYPE_HEADER;
            starList.add(model);
            indexMap.put("#", 0);
        }

        for (int i = 0; i < list.size(); i++) {
            FriendLetterIndexModel model = list.get(i);
            if (model.itemType == ITEM_TYPE_HEADER)
                indexMap.put(model.name, i + starList.size());
        }


        list.addAll(0, starList);
        runOnUiThread(() -> adapter.setNewData(list));
    }


}
