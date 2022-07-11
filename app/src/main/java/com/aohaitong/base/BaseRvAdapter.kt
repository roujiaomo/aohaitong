package com.aohaitong.base

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.kotlindemo.interfaces.MultipleType

abstract class BaseRvAdapter<T>(var mContext: Context, var layoutId: Int) :
    RecyclerView.Adapter<BaseRvAdapter.BaseViewHolder>() {

    var mList = ArrayList<T>()
    private var multipleType: MultipleType<T>? = null

    /**
     * 次构造器, 用于item多布局
     * 默认给layoutId为 -1 用于判断
     */
    constructor(mContext: Context, multipleType: MultipleType<T>) : this(mContext, -1) {
        this.multipleType = multipleType
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        if (null != multipleType) {
            //多布局的viewType就是layoutId
            layoutId = viewType
        }
        var view: View = LayoutInflater.from(mContext).inflate(layoutId, parent, false)
        return BaseViewHolder(view)
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        bindData(holder, position, mList[position])
    }

    abstract fun bindData(holder: BaseViewHolder, position: Int, data: T)


    class BaseViewHolder(view: View) : RecyclerView.ViewHolder(view.rootView)

    /**
     * 多布局，用layoutId作为ItemViewType
     */
    override fun getItemViewType(position: Int): Int {
        return multipleType?.getLayoutId(mList[position], position) ?: super.getItemViewType(
            position
        )
    }

    override fun getItemCount(): Int {
        return mList.size
    }


    /**
     * 更新所有数据
     */
    fun updateAll(list: MutableList<T>, isClear: Boolean) {
        if (isClear) {
            mList.clear()
        }
        mList.addAll(list)
        notifyDataSetChanged()
    }

    /**
     * 适配器中添加单条数据，刷新UI
     *
     * @param position 要添加的数据所在适配器中的位置
     * @param data     要添加的数据
     */
    fun insertItem(position: Int, data: T) {
        mList.add(position, data)
        notifyItemInserted(position)
    }

    /**
     * 适配器中批量添加数据，刷新UI
     *
     * @param _list         批量添加的集合
     * @param positionStart 添加到适配器中的起始位置
     */
    fun insertItems(_list: List<T>, positionStart: Int) {
        mList.addAll(_list)
        notifyItemRangeInserted(positionStart, _list.size)
    }


    /**
     * 适配器中删除单条数据，刷新UI
     *
     * @param position 要删除的数据所在适配器中的位置
     */
    fun removeItem(position: Int) {
        mList.removeAt(position)
        notifyItemRemoved(position)
    }

    /**
     * 适配器中批量删除多条数据，刷新UI
     *
     * @param _list         要删除的数据集合
     * @param positionStart 删除的数据在适配器中的起始位置
     */
    fun removeItems(_list: List<T>, positionStart: Int) {
        mList.removeAll(_list)
        notifyItemRangeRemoved(positionStart, mList.size)
    }

    /**
     * 修改适配器中单条数据，刷新UI
     *
     * @param position 更新的数据所在适配器中的位置
     * @param data     更新后的数据集合
     */
    fun updateItem(position: Int, data: T) {
        mList.removeAt(position)
        mList.add(position, data)
        notifyItemChanged(position)
        // notifyItemRangeChanged(positionStart, itemCount);
    }
}