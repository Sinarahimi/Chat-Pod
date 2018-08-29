package com.fanap.podchat.model;


import java.util.List;

public class ResultsHistory {

    private List<MessageVO> history;

    public List<MessageVO> getHistory() {
        return history;
    }

    public void setHistory(List<MessageVO> history) {
        this.history = history;
    }
}
