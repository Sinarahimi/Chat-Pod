package com.fanap.podchat.model;

public class ResultFile {
    private int id;
    private String name;
    private String hashCode;
    private DescriptionFile description;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHashCode() {
        return hashCode;
    }

    public void setHashCode(String hashCode) {
        this.hashCode = hashCode;
    }

    public DescriptionFile getDescription() {
        return description;
    }

    public void setDescription(DescriptionFile description) {
        this.description = description;
    }
}
