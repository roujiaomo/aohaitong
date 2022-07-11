package com.aohaitong.domain.usecase

import android.text.TextUtils
import com.aohaitong.bean.entity.GroupFriendBean
import com.aohaitong.constant.StatusConstant
import com.aohaitong.data.DataBaseRepository
import com.aohaitong.di.IoDispatcher
import com.aohaitong.domain.FlowUseCase
import com.aohaitong.utils.PinyinUtil
import com.aohaitong.utils.StringUtil
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.*
import javax.inject.Inject

class GetFriendListUseCase @Inject constructor(
    @IoDispatcher dispatcher: CoroutineDispatcher,
    private val dataBaseRepository: DataBaseRepository,
) : FlowUseCase<String, List<GroupFriendBean>>(dispatcher) {
    override fun execute(parameters: String): Flow<List<GroupFriendBean>> {
        val groupFriendBeanList = mutableListOf<GroupFriendBean>()

        groupFriendBeanList.addAll(dataBaseRepository.getFriendList(parameters).map {
            val realName = if (it.name.isNullOrEmpty()) "" else it.name
            val realNickName = if (it.nickName.isNullOrEmpty()) "" else it.nickName
            val realTelephone = if (it.telephone.isNullOrEmpty()) "" else it.telephone
            val groupFriendBean =
                GroupFriendBean(name = realName, nickName = realNickName, tel = realTelephone)
            groupFriendBean.showName = StringUtil.getFirstNotNullString(
                arrayOf<String>(
                    realNickName,
                    realName,
                    realTelephone
                )
            )
            groupFriendBean
        })
        //名字转换成拼音
        groupFriendBeanList.map {
            val pinYin = PinyinUtil.chineneToSpell(
                StringUtil.getFirstNotNullString(
                    arrayOf(
                        it.nickName,
                        it.name
                    )
                )
            )
            it.pinyin = if (TextUtils.isEmpty(pinYin)) "#" else pinYin
            it.itemType = StatusConstant.ITEM_FRIEND_LIST_CONTENT
        }

        //加入名字首字母
        val firstPinYinList: MutableList<String?> = ArrayList() //首字母集合
        val tempList = mutableListOf<GroupFriendBean>()
        tempList.addAll(groupFriendBeanList)
        tempList.map {
            val firstPinYin = it.pinyin?.substring(0, 1)?.uppercase(Locale.getDefault())
            if (!firstPinYinList.contains(firstPinYin)) {
                //加入首字母 header 数据
                firstPinYinList.add(firstPinYin)
                if (firstPinYin.equals("#")) {
                    groupFriendBeanList.add(
                        0,
                        GroupFriendBean(
                            name = firstPinYin,
                            pinyin = firstPinYin?.lowercase(Locale.getDefault()),
                            itemType = StatusConstant.ITEM_FRIEND_LIST_HEADER
                        )
                    )
                } else {
                    groupFriendBeanList.add(
                        GroupFriendBean(
                            name = firstPinYin,
                            pinyin = firstPinYin?.lowercase(Locale.getDefault()),
                            itemType = StatusConstant.ITEM_FRIEND_LIST_HEADER
                        )
                    )
                }
            }
        }
        Collections.sort(groupFriendBeanList, PinyinUtil.GroupFriendPinyinComparator())
        return flow {
            emit(groupFriendBeanList)
        }
    }
}
