package com.aohaitong.domain.usecase

import android.content.Context
import android.text.TextUtils
import com.aohaitong.R
import com.aohaitong.bean.entity.MessageBusinessBean
import com.aohaitong.constant.StatusConstant
import com.aohaitong.data.DataBaseRepository
import com.aohaitong.db.DBManager
import com.aohaitong.di.IoDispatcher
import com.aohaitong.domain.FlowUseCase
import com.aohaitong.utils.StringUtil
import com.aohaitong.utils.TimeUtil
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetMessageListUseCase @Inject constructor(
    @IoDispatcher dispatcher: CoroutineDispatcher,
    private val dataBaseRepository: DataBaseRepository,
    private val context: Context
) : FlowUseCase<String, List<MessageBusinessBean>>(dispatcher) {
    override fun execute(parameters: String): Flow<List<MessageBusinessBean>> {
        val messageBusinessBeanList = mutableListOf<MessageBusinessBean>()
        dataBaseRepository.getMessageList(parameters).map {
            var showName = StringUtil.getFirstNotNullString(
                arrayOf(
                    it.nickName,
                    it.name, it.telephone
                )
            )
            if (!it.groupId.isNullOrEmpty()) {
                showName = DBManager.getInstance(context).getGroupNameById(it.groupId)
            }
            messageBusinessBeanList.add(
                MessageBusinessBean(
                    name = it.name,
                    nickName = it.nickName,
                    telephone = it.telephone ?: "",
                    showName = showName,
                    message = when (it.messageType) {
                        StatusConstant.TYPE_RECORD_MESSAGE -> context.getString(R.string.message_record)
                        StatusConstant.TYPE_PHOTO_MESSAGE -> context.getString(R.string.message_pic)
                        StatusConstant.TYPE_VIDEO_MESSAGE -> context.getString(R.string.message_video)
                        else -> it.message
                    },
                    messageType = it.messageType,
                    showTime = TimeUtil.stampToDateMinWithOutDay(it.time),
                    realTime = it.time,
                    unReadCount = when {
                        TextUtils.isEmpty(it.unReadCount) -> ""
                        it.unReadCount.length > 2 -> "···"
                        else -> it.unReadCount
                    },
                    isShowUnRead = !TextUtils.isEmpty(it.unReadCount),
                    groupId = it.groupId ?: "",
                    isGroup = it.isGroup
                )
            )
        }
        return flow {
            emit(messageBusinessBeanList.sortedByDescending {
                it.realTime
            })
        }
    }
}
