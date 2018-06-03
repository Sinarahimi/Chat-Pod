package com.fanap.podchat.model;

import java.util.List;

public class ChatThread {

    private int type;
    private String ownerSsoId;
    private List<Invite> invitees;
    private List<Invite[]> ArrayInvitees;
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

    public List<Invite> getInvitees() {
        return invitees;
    }

    public void setInvitees(List<Invite> invitees) {
        this.invitees = invitees;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<Invite[]> getArrayInvitees() {
        return ArrayInvitees;
    }

    public void setArrayInvitees(List<Invite[]> arrayInvitees) {
        ArrayInvitees = arrayInvitees;
    }
}
