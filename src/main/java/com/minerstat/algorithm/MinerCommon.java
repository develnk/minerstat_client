package com.minerstat.algorithm;

import org.json.simple.JSONObject;
import java.util.HashMap;

public abstract class MinerCommon {

    /**
     *
     *
     * @param data
     *
     * @return JSONObject
     */
    protected JSONObject dataToSend(HashMap<Object, Object > data) {
        JSONObject dataJson = new JSONObject();
        data.forEach((key, value) -> dataJson.put(key, value));
        return dataJson;
    }

    protected JSONObject sendToServer(String data, int type) {
        JSONObject result = new JSONObject();
        switch (type) {
            case 0:
                result.put("tcp", data);
                break;

            case 1:
                result.put("log", data);
                break;
        }

        return result;
    }
}
