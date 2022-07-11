package com.aohaitong.domain.usecase

import android.content.Context
import com.aohaitong.bean.ChatMsgBean
import com.aohaitong.bean.entity.DeleteChatParams
import com.aohaitong.data.DataBaseRepository
import com.aohaitong.db.DBManager
import com.aohaitong.di.IoDispatcher
import com.aohaitong.domain.FlowUseCase
import com.aohaitong.utils.FileUtils
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.*
import javax.inject.Inject

class DeleteChatUseCase @Inject constructor(
    @IoDispatcher dispatcher: CoroutineDispatcher,
    private val dataBaseRepository: DataBaseRepository,
    private val context: Context
) : FlowUseCase<DeleteChatParams, Unit>(dispatcher) {
    override fun execute(parameters: DeleteChatParams): Flow<Unit> {
        dataBaseRepository.deleteChatMessage(parameters.deleteParams, parameters.isGroup)
        val chatMsgBeanList: MutableList<ChatMsgBean> = if (!parameters.isGroup) {
            DBManager.getInstance(context).getNewsMsg(parameters.deleteParams)
        } else {
            DBManager.getInstance(context)
                .getGroupMessageByGroupId(parameters.deleteParams.toLong())
        }
        val pathList: MutableList<String> = ArrayList()
        for (chatMsgBean in chatMsgBeanList) {
            pathList.add(chatMsgBean.filePath)
        }
        FileUtils.deleteFiles(pathList)
        return flow {
            emit(Unit)
        }
    }
}
