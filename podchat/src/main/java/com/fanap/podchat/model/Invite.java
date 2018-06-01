package com.fanap.podchat.model;

public class Invite {

    private int id;
    private int idType;

    public Invite(int id, int idType) {
        this.id = id;
        this.idType = idType;
    }

    public Invite(){}

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
