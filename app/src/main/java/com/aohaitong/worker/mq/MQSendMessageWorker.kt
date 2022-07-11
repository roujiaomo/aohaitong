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
import android.text.TextUtils
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.aohaitong.MyApplication
import com.aohaitong.business.request.RequestQueueManager
import com.aohaitong.constant.CommonConstant
import com.aohaitong.domain.connect.MQConnectController.Companion.SEND_TOPIC
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.util.*
import javax.jms.DeliveryMode
import javax.jms.Destination
import javax.jms.JMSException
import javax.jms.MessageProducer

class MQSendMessageWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    private var isRun = true
    private var destination: Destination? = null
    private var producer: MessageProducer? = null
    private var gson = Gson()

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            destination = MyApplication.session.createTopic(SEND_TOPIC)
            producer = MyApplication.session.createProducer(destination)
            while (isRun) {
                delay(20)
                val request = RequestQueueManager.getInstance().poll() ?: continue
                val callback = request.sendCallBack
                val param = request.msgParam ?: continue
                if (producer == null || TextUtils.isEmpty(param)) {
                    continue
                }
                try {
                    val map: MutableMap<String, String> = HashMap<String, String>()
                    map["sourceAccount"] = MyApplication.TEL.toString()
                    map["message"] = param.replace("\r\n", "")
                    val text =
                        MyApplication.session.createTextMessage(gson.toJson(map)) //session用来生产消息
                    producer?.send(
                        destination,
                        text,
                        DeliveryMode.PERSISTENT,
                        4,
                        (1000 * 10).toLong()
                    )
                    callback?.onFinish()
                } catch (e: JMSException) {
                    if (callback != null) {
                        Log.e(CommonConstant.LOGCAT_TAG, "MQ发送消息异常" + e.message)
                        callback.onError(e.message)
                    }
                }
            }
            Result.success()
        } catch (ex: Exception) {
            Result.failure()
        }
    }
}