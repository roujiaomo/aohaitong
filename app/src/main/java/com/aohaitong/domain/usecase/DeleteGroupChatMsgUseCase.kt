package com.aohaitong.domain.usecase

import com.aohaitong.bean.entity.ChatMsgBusinessBean
import com.aohaitong.bean.entity.DeleteChatMsgParams
import com.aohaitong.constant.StatusConstant
import com.aohaitong.data.DataBaseRepository
import com.aohaitong.di.IoDispatcher
import com.aohaitong.domain.SingleUseCase
import com.aohaitong.utils.TimeUtil
import io.reactivex.Single
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

class DeleteGroupChatMsgUseCase @Inject constructor(
    @IoDispatcher dispatcher: CoroutineDispatcher,
    private val dataBaseRepository: DataBaseRepository,
) : SingleUseCase<DeleteChatMsgParams, List<ChatMsgBusinessBean>>() {
    override fun execute(parameter: DeleteChatMsgParams): Single<List<ChatMsgBusinessBean>> {
        dataBaseRepository.deleteChatMsgById(parameter.chatMsgId)
        val chatMsgBusinessBean = mutableListOf<ChatMsgBusinessBean>()
        dataBaseRepository.getGroupChatHistoryList(parameter.groupId.toLong()).map {
            chatMsgBusinessBean.add(
                ChatMsgBusinessBean(
                    id = it.id,
                    msg = it.msg,
                    telephone = it.telephone,
                    currentUserTel = it.nowLoginTel,
                    sendType = it.sendType,
                    time = it.time,
                    status = it.status,
                    messageType = it.messageType,
                    filePath = it.filePath,
                    recordTime = it.recordTime,
                    isRecording = false,
                    isLoading = it.status == StatusConstant.SEND_LOADING,
                    isFailed = it.status == StatusConstant.SEND_FAIL,
                    showTime = TimeUtil.stampToDateMinWithOutDay(it.time),
                    isGroup = it.isGroup,
                    groupId = it.groupId
                )
            )
        }
        return Single.just(chatMsgBusinessBean)
    }
}
