package com.minerstat.entity;

import java.io.Serializable;

public class UserRig implements Serializable{
    private String rigId;
    private String name;

    public UserRig() {}

    public String getRigId() {
        return rigId;
    }

    public void setRigId(String rigId) {
        this.rigId = rigId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
