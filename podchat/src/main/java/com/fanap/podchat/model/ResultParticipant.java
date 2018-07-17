package com.fanap.podchat.model;

import com.fanap.podchat.mainmodel.Participant;

import java.util.List;

public class ResultParticipant {

    private List<Participant> participants;

    public List<Participant> getParticipants() {
        return participants;
    }

    public void setParticipants(List<Participant> participants) {
        this.participants = participants;
    }
}
