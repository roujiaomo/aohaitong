package com.aohaitong.ui.main;

import static com.aohaitong.constant.StatusConstant.ITEM_TYPE_CONTENT;
import static com.aohaitong.constant.StatusConstant.ITEM_TYPE_HEADER;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aohaitong.R;
import com.aohaitong.base.BaseFragment;
import com.aohaitong.bean.FriendBean;
import com.aohaitong.bean.MsgEntity;
import com.aohaitong.business.transmit.BusinessController;
import com.aohaitong.business.transmit.ISendListener;
import com.aohaitong.constant.NumConstant;
import com.aohaitong.constant.StatusConstant;
import com.aohaitong.db.DBManager;
import com.aohaitong.ui.adapter.FriendAdapter;
import com.aohaitong.ui.chat.chat.NewChatActivity;
import com.aohaitong.ui.friend.FriendApplyActivity;
import com.aohaitong.ui.group.my_group.MyGroupActivity;
import com.aohaitong.ui.model.FriendLetterIndexModel;
import com.aohaitong.utils.NoDoubleClickListener;
import com.aohaitong.utils.PinyinUtil;
import com.aohaitong.utils.StringUtil;
import com.aohaitong.utils.dialog.MyQmuiDialog;
import com.aohaitong.widget.LetterIndexView;
import com.qmuiteam.qmui.skin.QMUISkinManager;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FriendFragment extends BaseFragment {
    private List<FriendBean> data = new ArrayList<>();

    private RecyclerView recyclerView;
    private FriendAdapter adapter;
    private LetterIndexView indexView;
    private Map<String, Integer> indexMap = new HashMap<>();
    private List<FriendLetterIndexModel> starList = new ArrayList<>();
    private EditText searchEdit;
    private ImageView unReadImg;
    private TextView unReadTv;
    private ConstraintLayout consMyGroup;

    @Override
    protected int setLayout() {
        return R.layout.fragment_friend;
    }

    @Override
    protected void initView() {
        searchEdit = bindView(R.id.edit_search);
        recyclerView = bindView(R.id.recycler);
        indexView = bindView(R.id.vLetterIndex);
        unReadImg = bindView(R.id.img_unread);
        unReadTv = bindView(R.id.tv_unread);
        consMyGroup = bindView(R.id.cons_my_group);
        indexView.addLetter(0, "#");
    }


    @SuppressLint("SetTextI18n")
    private void checkUnReadItems() {
        int size = DBManager.getInstance(this.getActivity()).selectFriendApplyCount();
        if (size == 0) {
            unReadImg.setVisibility(View.GONE);
            unReadTv.setVisibility(View.GONE);
        } else {
            unReadImg.setVisibility(View.VISIBLE);
            unReadTv.setVisibility(View.VISIBLE);
            unReadTv.setText(size + "");
        }
    }

    @Override
    protected void initData() {
        adapter = new FriendAdapter();
        doLoadData();
    }


    private void doLoadData() {
        checkUnReadItems();
        indexMap = new HashMap<>();
        starList = new ArrayList<>();
        data = DBManager.getInstance(context).selectAllFriend(searchEdit.getText().toString());
        new Thread(this::loadData).start();
    }

    @Override
    protected void initEvent() {
        bindView(R.id.line_add_friend).setOnClickListener(new NoDoubleClickListener() {
            @Override
            protected void onNoDoubleClick(View v) {
                unReadImg.setVisibility(View.GONE);
                unReadTv.setVisibility(View.GONE);
                FriendApplyActivity.startFriendApplyActivity(context);
            }
        });

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
            if (TextUtils.isEmpty(((FriendLetterIndexModel) adapter.getData().get(position)).tel)) {
                return;
            }
            NewChatActivity.Companion.startChatActivity(requireContext(),
                    ((FriendLetterIndexModel) adapter.getData().get(position)).name,
                    ((FriendLetterIndexModel) adapter.getData().get(position)).nickName,
                    ((FriendLetterIndexModel) adapter.getData().get(position)).tel, false, "", "");
        });
        adapter.setOnItemLongClickListener((adapter, view, position) -> {
            if (TextUtils.isEmpty(((FriendLetterIndexModel) adapter.getData().get(position)).tel)) {
                return false;
            }
            FriendLetterIndexModel model = (FriendLetterIndexModel) adapter.getData().get(position);
            new QMUIDialog.MessageDialogBuilder(getActivity())
                    .setTitle("删除好友")
                    .setMessage("确定要删除" + (StringUtil.getFirstNotNullString(new String[]{model.nickName, model.name, model.tel})) + "吗？")
                    .setSkinManager(QMUISkinManager.defaultInstance(requireContext()))
                    .addAction("取消", (dialog, index) -> dialog.dismiss())
                    .addAction(0, "删除", QMUIDialogAction.ACTION_PROP_NEGATIVE, (dialog, index) -> {
                        showLoading();
                        BusinessController.sendDelFriend(model.tel, new ISendListener() {
                            @Override
                            public void sendSuccess() {
                                loadingDialog.dismiss();
                                dialog.dismiss();
                                MyQmuiDialog.showSuccessDialog(getActivity(), "删除成功");
                                DBManager.getInstance(context).deleteFriend(model.tel);
                                doLoadData();
                            }

                            @Override
                            public void sendFail(String reason) {
                                loadingDialog.dismiss();
                                dialog.dismiss();
                                MyQmuiDialog.showErrorDialog((Activity) context, reason);
                            }
                        }, NumConstant.getJHDNum());
                    })
                    .create().show();
            return false;
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

        consMyGroup.setOnClickListener(new NoDoubleClickListener() {
            @Override
            protected void onNoDoubleClick(View v) {
                startActivity(new Intent(requireActivity(), MyGroupActivity.class));
            }
        });
    }

    private void loadData() {
        List<FriendLetterIndexModel> list = new ArrayList<>();
        for (int i = 0; i < data.size(); i++) {
            FriendLetterIndexModel model = new FriendLetterIndexModel();
            model.name = data.get(i).getName();
            model.nickName = data.get(i).getNickName();
            String pinyin = PinyinUtil.chineneToSpell(StringUtil.getFirstNotNullString(new String[]{model.nickName, model.name}));
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
        getActivity().runOnUiThread(() -> adapter.setNewData(list));
    }

    @Override
    public void onReceiveData(MsgEntity msgEntity) {
        if (msgEntity != null && msgEntity.getType() == StatusConstant.TYPE_FRIEND_REFRESH) {
            if (isAdded()) {
                requireActivity().runOnUiThread(this::doLoadData);
            }
        }
    }
}
