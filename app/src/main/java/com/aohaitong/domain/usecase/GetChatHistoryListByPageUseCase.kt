package com.aohaitong.domain.usecase

import com.aohaitong.bean.entity.ChatMsgBusinessBean
import com.aohaitong.bean.entity.GetChatListParams
import com.aohaitong.constant.StatusConstant
import com.aohaitong.data.DataBaseRepository
import com.aohaitong.domain.SingleUseCase
import com.aohaitong.utils.TimeUtil
import io.reactivex.Single
import javax.inject.Inject

class GetChatHistoryListByPageUseCase @Inject constructor(
    private val dataBaseRepository: DataBaseRepository,
) : SingleUseCase<GetChatListParams, List<ChatMsgBusinessBean>>() {
    override fun execute(parameter: GetChatListParams): Single<List<ChatMsgBusinessBean>> {
        return Single.just(
            dataBaseRepository.getChatHistoryListByPage(
                parameter.userTel,
                parameter.pageNum
            ).map {
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
            }.filterNot {
                it.isGroup
            })
    }
}
