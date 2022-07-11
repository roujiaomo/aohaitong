package com.aohaitong.ui.friend;

import static com.aohaitong.constant.StatusConstant.ITEM_TYPE_CONTENT;
import static com.aohaitong.constant.StatusConstant.ITEM_TYPE_HEADER;

import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aohaitong.R;
import com.aohaitong.base.BaseActivity;
import com.aohaitong.bean.MsgEntity;
import com.aohaitong.constant.StatusConstant;
import com.aohaitong.ui.adapter.ContactsAdapter;
import com.aohaitong.ui.model.ContactsDetailBean;
import com.aohaitong.ui.model.ContactsLetterIndexModel;
import com.aohaitong.utils.ContactsUtil;
import com.aohaitong.utils.PinyinUtil;
import com.aohaitong.widget.LetterIndexView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//联系人界面
public class ContactsActivity extends BaseActivity {
    private List<ContactsDetailBean> data = new ArrayList<>();

    private RecyclerView recyclerView;
    private ContactsAdapter adapter;
    private LetterIndexView indexView;
    private Map<String, Integer> indexMap = new HashMap<>();
    private List<ContactsLetterIndexModel> starList = new ArrayList<>();

    private EditText searchEdit;

    public static void startContactsActivity(Context context) {
        Intent intent = new Intent(context, ContactsActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected int getLayout() {
        return R.layout.activity_friend_contacts;
    }

    @Override
    protected void initView() {
        ((TextView) bindView(R.id.tv_title)).setText("手机联系人");
        searchEdit = bindView(R.id.edit_search);
        recyclerView = bindView(R.id.recycler);
        indexView = bindView(R.id.vLetterIndex);
        indexView.addLetter(0, "#");
    }

    @Override
    protected void initData() {
        adapter = new ContactsAdapter();
        doLoadData();
    }

    private void doLoadData() {
        indexMap = new HashMap<>();
        starList = new ArrayList<>();
        if (!loadingDialog.isShowing()) {
            loadingDialog.show();
        }
        new Thread(() -> {
            ContactsUtil.getAllContacts(context);
            data = ContactsUtil.searchContacts(searchEdit.getText().toString());
            loadData();
        }).start();
    }

    @Override
    protected void initEvent() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        indexView.setOnStateChangeListener((eventAction, position, letter, itemCenterY) -> {
            Integer pos = indexMap.get(letter);
            if (pos != null) {
                recyclerView.scrollToPosition(pos);
                LinearLayoutManager mLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                mLayoutManager.scrollToPositionWithOffset(pos, 0);
            }
        });
        adapter.setOnItemClickListener((adapter, view, position) ->
                FriendDetailActivity.startFriendDetailActivity(context,
                        ((ContactsLetterIndexModel) adapter.getData().get(position)).name,
                        ((ContactsLetterIndexModel) adapter.getData().get(position)).tel,
                        ((ContactsLetterIndexModel) adapter.getData().get(position)).nickName,
                        ((ContactsLetterIndexModel) adapter.getData().get(position)).isFriend
                                == StatusConstant.TYPE_IS_FRIEND)
        );
        adapter.setClick(model -> {
            FriendCommitActivity.startFriendCommitActivity(context, false, model.name, model.tel);
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
        List<ContactsLetterIndexModel> list = new ArrayList<>();
        for (int i = 0; i < data.size(); i++) {
            ContactsLetterIndexModel model = new ContactsLetterIndexModel();
            model.name = data.get(i).getName();
            String pinyin = PinyinUtil.chineneToSpell(model.name);
            model.pinyin = "".equals(pinyin) ? "#" : pinyin;
            model.itemType = ITEM_TYPE_CONTENT;
            model.tel = data.get(i).getTelephone();
            model.isFriend = data.get(i).getFriendType();
            model.type = data.get(i).getType();
            model.hasFriendApply = data.get(i).getFriendApplyType();
            model.name = data.get(i).getName();
            model.nickName = data.get(i).getNickName();
            list.add(model);
        }
        List<String> letterList = new ArrayList<>();
        List<ContactsLetterIndexModel> tmpList = new ArrayList<>();
        for (ContactsLetterIndexModel model : list) {
            String letter = model.pinyin.substring(0, 1).toUpperCase();
            if (!letterList.contains(letter)) {
                ContactsLetterIndexModel newModel = new ContactsLetterIndexModel();
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
            ContactsLetterIndexModel model = new ContactsLetterIndexModel();
            model.name = "#";
            model.itemType = ITEM_TYPE_HEADER;
            starList.add(model);
            indexMap.put("#", 0);
        }

        for (int i = 0; i < list.size(); i++) {
            ContactsLetterIndexModel model = list.get(i);
            if (model.itemType == ITEM_TYPE_HEADER)
                indexMap.put(model.name, i + starList.size());
        }


        list.addAll(0, starList);
        runOnUiThread(() -> {
            EventBus.getDefault().post(new MsgEntity("", StatusConstant.TYPE_DISMISS_DIALOG));
            adapter.setNewData(list);
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(MsgEntity entity) {
        if (entity.getType() == StatusConstant.TYPE_FRIEND_APPLY_REFRESH || entity.getType() == StatusConstant.TYPE_FRIEND_REFRESH) {
            doLoadData();
        } else if (entity.getType() == StatusConstant.TYPE_DISMISS_DIALOG) {
            if (loadingDialog.isShowing()) {
                loadingDialog.dismiss();
            }
        }
    }
}
