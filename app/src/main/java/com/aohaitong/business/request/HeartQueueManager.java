package com.aohaitong.business.request;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * 待发送消息队列
 */
public class HeartQueueManager {

    LinkedBlockingQueue<MsgRequest> mQueueList;

    private static final class Holder {
        public static final HeartQueueManager sINSTANCE = new HeartQueueManager();
    }

    private HeartQueueManager() {
        mQueueList = new LinkedBlockingQueue<>();
    }

    public static HeartQueueManager getInstance() {
        return Holder.sINSTANCE;
    }

    public MsgRequest poll() {
        MsgRequest entity = null;
        if (mQueueList != null) {
            entity = mQueueList.poll();
        }
        return entity;
    }

    public void pollAll() {
        if (mQueueList != null) {
            mQueueList.clear();
        }
    }

    public void push(MsgRequest entity) {
        try {
            mQueueList.put(entity);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
