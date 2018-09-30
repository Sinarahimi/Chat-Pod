package com.fanap.podchat.mainmodel;

import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.PrimaryKey;

import java.io.Serializable;

public class Participant implements Serializable {
    @PrimaryKey
    private long id;
    @Embedded(prefix = "participant_")
    private String name;
    @Embedded(prefix = "participant_")
    private String firstName;
    @Embedded(prefix = "participant_")
    private String lastName;
    @Embedded(prefix = "participant_")
    private String image;
    @Embedded(prefix = "participant_")
    private long notSeenDuration;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getNotSeenDuration() {
        return notSeenDuration;
    }

    public void setNotSeenDuration(long notSeenDuration) {
        this.notSeenDuration = notSeenDuration;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
}
