package com.minerstat.algorithm.claymore;

import com.minerstat.algorithm.MinerCommon;
import org.json.simple.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class CardInfo extends MinerCommon {
    private String modelName;
    private String value;

    CardInfo() {
        this.modelName = "";
        this.value = "";
    }

    void setModelName(String modelName) {
        this.modelName = modelName;
    }

    void setValue(String value) {
        this.value = value;
    }

    public Map toMap() {
        HashMap<Object, Object> map = new HashMap<>();
        map.put("Model", modelName);
        map.put("Information", value);
        return map;
    }

    @Override
    public String toString() {
        HashMap<Object, Object> map = new HashMap<>();
        map.put("Model", modelName);
        map.put("Information", value);
        JSONObject result = dataToSend(map);
        return result.toString();
    }
}
