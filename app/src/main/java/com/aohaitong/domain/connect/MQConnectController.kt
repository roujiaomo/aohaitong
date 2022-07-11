package com.aohaitong.domain.connect

import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkRequest
import com.aohaitong.MyApplication
import com.aohaitong.constant.IPAddress
import com.aohaitong.utils.ThreadPoolManager
import com.aohaitong.worker.mq.MQReceiveMessageWorker
import com.aohaitong.worker.mq.MQSendMessageWorker
import org.apache.activemq.ActiveMQConnection
import org.apache.activemq.ActiveMQConnectionFactory
import java.util.*
import javax.jms.Session

class MQConnectController private constructor() {
    companion object {
        val instance: MQConnectController by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            MQConnectController()
        }

        fun newInstance(): MQConnectController {
            return instance
        }

        const val SEND_TOPIC = "offshoreServerTopic"
        const val RECEIVE_TOPIC = "offshoreClientTopic"
        const val HEARTBEAT_TOPIC = "offshoreHeartbeatTopic"
    }

    private var connectionFactory: ActiveMQConnectionFactory? = null
    private var connection: ActiveMQConnection? = null
    private var session: Session? = null
    private val mqAddress = "failover:" + "(tcp://" + IPAddress.getMqAddress() + ")"
    private lateinit var sendMessageWorkerId: UUID
    private lateinit var receiveMessageWorkerId: UUID

    fun initMQConnect(mqConnectLister: MQConnectListener) {
        val mqInitRunnable = Runnable {
            try {
                connectionFactory = ActiveMQConnectionFactory(mqAddress)
                connection = connectionFactory?.createConnection() as ActiveMQConnection
                connection?.let {
                    it.clientID = MyApplication.TEL.toString()
                    it.start()
                    session = it.createSession(false, Session.AUTO_ACKNOWLEDGE) //以事务模式获取会话
                }
                if (connection != null && connection!!.isStarted) {
                    MyApplication.session = session
                    val sendMessageWorker: WorkRequest =
                        OneTimeWorkRequestBuilder<MQSendMessageWorker>().build()
                    val receiveMessageWorker: WorkRequest =
                        OneTimeWorkRequestBuilder<MQReceiveMessageWorker>().build()
                    WorkManager.getInstance(MyApplication.getContext()).enqueue(sendMessageWorker)
                    WorkManager.getInstance(MyApplication.getContext())
                        .enqueue(receiveMessageWorker)
                    sendMessageWorkerId = sendMessageWorker.id
                    receiveMessageWorkerId = receiveMessageWorker.id
                    mqConnectLister.connectSuccess()
                } else {
                    mqConnectLister.connectFailed()
                }
            } catch (e: Exception) {
                mqConnectLister.connectFailed()
            }
        }
        ThreadPoolManager.getInstance().execute(mqInitRunnable)
    }

    fun stopConnect() {
        session?.close()
        session = null
        connection?.close()
        connection = null
        if (::sendMessageWorkerId.isInitialized) {
            WorkManager.getInstance(MyApplication.getContext()).cancelWorkById(sendMessageWorkerId)
        }
        if (::receiveMessageWorkerId.isInitialized) {
            WorkManager.getInstance(MyApplication.getContext())
                .cancelWorkById(receiveMessageWorkerId)
        }
    }
}

interface MQConnectListener {
    fun connectSuccess()
    fun connectFailed()
}
