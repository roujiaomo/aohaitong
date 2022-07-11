/*
 * Copyright 2018 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.aohaitong.worker.mq

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.aohaitong.MyApplication
import com.aohaitong.domain.connect.MQConnectController.Companion.HEARTBEAT_TOPIC
import com.aohaitong.domain.connect.MQConnectController.Companion.RECEIVE_TOPIC
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.greenrobot.eventbus.EventBus
import javax.jms.Destination
import javax.jms.JMSException
import javax.jms.Message
import javax.jms.TextMessage

class MQReceiveMessageWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {
    private lateinit var messageDestination: Destination
    private lateinit var heartBeatDestination: Destination

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            messageDestination = MyApplication.session.createTopic(RECEIVE_TOPIC)
            heartBeatDestination = MyApplication.session.createTopic(HEARTBEAT_TOPIC)
            val messageConsumer = MyApplication.session.createConsumer(messageDestination)
            val heartBeatConsumer = MyApplication.session.createConsumer(heartBeatDestination)
            messageConsumer.setMessageListener {
                handleMessage(it)
            }
            heartBeatConsumer.setMessageListener {
                handleMessage(it)
            }
            Result.success()
        } catch (ex: Exception) {
            Result.failure()
        }
    }


    fun handleMessage(msg: Message?) {
        val text = msg as TextMessage?
        if (text != null) {
            try {
                val s = text.text
                if (!s.startsWith("{")) {
                    EventBus.getDefault().post(text.text)
                }
            } catch (e: JMSException) {
                e.printStackTrace()
            }
        }
    }
}
