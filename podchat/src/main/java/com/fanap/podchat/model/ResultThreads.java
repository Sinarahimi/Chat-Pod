package com.fanap.podchat.model;

import com.fanap.podchat.mainmodel.ThreadVo;

import java.util.List;

public class ResultThreads {
    private List<ThreadVo> threadVos;
    private long contentCount;
    private boolean hasNext;
    private long nextOffset;

    public long getContentCount() {
        return contentCount;
    }

    public void setContentCount(long contentCount) {
        this.contentCount = contentCount;
    }

    public boolean isHasNext() {
        return hasNext;
    }

    public void setHasNext(boolean hasNext) {
        this.hasNext = hasNext;
    }

    public long getNextOffset() {
        return nextOffset;
    }

    public void setNextOffset(long nextOffset) {
        this.nextOffset = nextOffset;
    }

    public List<ThreadVo> getThreadVos() {
        return threadVos;
    }

    public void setThreadVos(List<ThreadVo> threadVos) {
        this.threadVos = threadVos;
    }
}
