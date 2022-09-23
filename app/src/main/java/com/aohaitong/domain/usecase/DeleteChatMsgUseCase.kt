package com.aohaitong.domain.usecase

import com.aohaitong.bean.entity.DeleteChatMsgParams
import com.aohaitong.data.DataBaseRepository
import com.aohaitong.domain.SingleUseCase
import io.reactivex.Single
import javax.inject.Inject

class DeleteChatMsgUseCase @Inject constructor(
    private val dataBaseRepository: DataBaseRepository,
) : SingleUseCase<DeleteChatMsgParams, Long>() {
    override fun execute(parameter: DeleteChatMsgParams): Single<Long> {
        dataBaseRepository.deleteChatMsgById(parameter.chatMsgId)
        return Single.just(parameter.chatMsgId)
    }
}
