package com.fanap.podchat.model;

public class ForwardInfo {
    private Participant participant;
    private ConversationSummery conversation;

    public Participant getParticipant() {
        return participant;
    }

    public void setParticipant(Participant participant) {
        this.participant = participant;
    }

    public ConversationSummery getConversation() {
        return conversation;
    }

    public void setConversation(ConversationSummery conversation) {
        this.conversation = conversation;
    }
}
