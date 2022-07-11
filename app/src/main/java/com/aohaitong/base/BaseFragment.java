package com.aohaitong.base;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import com.aohaitong.bean.MsgEntity;
import com.aohaitong.constant.CommonConstant;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;

import timber.log.Timber;


public abstract class BaseFragment extends cn.feng.skin.manager.base.BaseFragment {
    protected String TAG;

    protected Context context;
    protected QMUITipDialog loadingDialog;

    protected void showLoading() {
        if (!loadingDialog.isShowing())
            loadingDialog.show();
    }

    /**
     * 当fragment与activity发生关联时调用
     *
     * @param context 与之相关联的activity
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(setLayout(), null);
        return view;
    }

    /**
     * 绑定布局
     *
     * @return
     */
    protected abstract int setLayout();

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Timber.tag(CommonConstant.LOGCAT_TAG);
        initView();
        loadingDialog = new QMUITipDialog.Builder(getContext())
                .setIconType(QMUITipDialog.Builder.ICON_TYPE_LOADING)
                .setTipWord("正在请求")
                .create();
        initData();
        initEvent();
    }


    /**
     * 初始化组件
     */
    protected abstract void initView();

    /**
     * 设置数据
     */
    protected abstract void initData();

    /**
     * 设置各种事件
     */
    protected abstract void initEvent();

    /**
     * 简化findViewById
     *
     * @param resId
     * @param <T>
     * @return
     */
    protected <T extends View> T bindView(int resId) {
        return getView().findViewById(resId);
    }

    /**
     * intent跳转
     *
     * @param context
     * @param clazz
     */
    protected void toClass(Context context, Class<? extends BaseActivity> clazz) {
        toClass(context, clazz, null);
    }

    /**
     * intent带值跳转
     *
     * @param context
     * @param clazz
     * @param bundle
     */
    protected void toClass(Context context, Class<? extends BaseActivity> clazz, Bundle bundle) {
        Intent intent = new Intent(context, clazz);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    /**
     * 带返回值的跳转
     *
     * @param context
     * @param clazz
     * @param bundle
     * @param requestCode
     */
    protected void toClass(Context context, Class<? extends BaseActivity> clazz, Bundle bundle, int requestCode) {
        Intent intent = new Intent(context, clazz);
        intent.putExtras(bundle);
        getActivity().startActivityForResult(intent, requestCode);
    }


    /**
     * eventbus就不在fragment里注册了,由mainactivity判断并且下发下来
     */
    public abstract void onReceiveData(MsgEntity msgEntity);


}