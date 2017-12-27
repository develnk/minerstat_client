package com.minerstat.algorithm.claymore;

import com.google.gson.annotations.SerializedName;

public class CardInfo {

    @SerializedName("Model")
    private String model;

    @SerializedName("Information")
    private String information;

    CardInfo() {
        this.model = "";
        this.information = "";
    }

    void setModel(String model) {
        this.model = model;
    }

    void setInformation(String information) {
        this.information = information;
    }

    public String getModel() {
        return model;
    }

    public String getInformation() {
        return information;
    }
}
