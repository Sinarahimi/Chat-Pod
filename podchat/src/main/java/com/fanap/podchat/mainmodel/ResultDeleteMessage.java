package com.fanap.podchat.mainmodel;

import com.fanap.podchat.model.DeleteMessageContent;

public class ResultDeleteMessage {
    private DeleteMessageContent deleteMessage;

    public DeleteMessageContent getDeleteMessageContent() {
        return deleteMessage;
    }

    public void setDeleteMessageContent(DeleteMessageContent deleteMessageContent) {
        this.deleteMessage = deleteMessageContent;
    }
}