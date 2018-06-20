package com.fanap.podchat.model;

public class Invitee {

    private int id;
    private int idType;

    public Invitee(int id, int idType) {
        this.id = id;
        this.idType = idType;
    }

    public Invitee(){}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdType() {
        return idType;
    }

    public void setIdType(int idType) {
        this.idType = idType;
    }
}
