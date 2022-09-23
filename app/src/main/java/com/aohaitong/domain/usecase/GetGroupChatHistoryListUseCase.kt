package com.aohaitong.domain.usecase

import android.content.Context
import com.aohaitong.MyApplication
import com.aohaitong.bean.entity.ChatMsgBusinessBean
import com.aohaitong.bean.entity.GetGroupChatListParams
import com.aohaitong.constant.StatusConstant
import com.aohaitong.data.DataBaseRepository
import com.aohaitong.db.DBManager
import com.aohaitong.domain.SingleUseCase
import com.aohaitong.utils.SPUtil
import com.aohaitong.utils.StringUtil
import com.aohaitong.utils.TimeUtil
import io.reactivex.Single
import javax.inject.Inject

class GetGroupChatHistoryListUseCase @Inject constructor(
    private val dataBaseRepository: DataBaseRepository,
    private val context: Context
) : SingleUseCase<GetGroupChatListParams, List<ChatMsgBusinessBean>>() {
    override fun execute(parameter: GetGroupChatListParams): Single<List<ChatMsgBusinessBean>> {
        return Single.just(
            dataBaseRepository.getGroupChatListByPage(
                parameter.groupId,
                parameter.pageNum
            ).map {
                val friendBean = DBManager.getInstance(context).selectFriend(it.telephone)
                var showName = it.telephone
                friendBean?.let {
                    showName = StringUtil.getFirstNotNullString(
                        arrayOf(
                            friendBean.nickName,
                            friendBean.name,
                            friendBean.telephone
                        )
                    )
                }
                if (it.telephone == MyApplication.TEL.toString()) {
                    showName = SPUtil.instance.getString(MyApplication.TEL.toString())
                }
                ChatMsgBusinessBean(
                    id = it.id,
                    msg = it.msg,
                    telephone = showName,
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
            })
    }
}
