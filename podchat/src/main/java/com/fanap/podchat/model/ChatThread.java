package com.fanap.podchat.model;

import java.util.List;

public class ChatThread {

    private int type;
    private String ownerSsoId;
    private List<Invitee> invitees;
    private List<Invitee[]> arrayInvitees;
    private String title;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getOwnerSsoId() {
        return ownerSsoId;
    }

    public void setOwnerSsoId(String ownerSsoId) {
        this.ownerSsoId = ownerSsoId;
    }

    public List<Invitee> getInvitees() {
        return invitees;
    }

    public void setInvitees(List<Invitee> invitees) {
        this.invitees = invitees;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<Invitee[]> getArrayInvitees() {
        return arrayInvitees;
    }

    public void setArrayInvitees(List<Invitee[]> arrayInvitees) {
        this.arrayInvitees = arrayInvitees;
    }
}
