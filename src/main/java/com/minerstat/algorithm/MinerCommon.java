package com.minerstat.algorithm;

import com.minerstat.algorithm.claymore.CardInfo;
import org.json.simple.JSONObject;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public abstract class MinerCommon {

    /**
     * Formatting input data for send to remote node.
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

    protected JSONObject hashMapMap(Map<Integer, Map<String, Integer>> data) {
        JSONObject dataJson = new JSONObject();
        data.forEach((key, value) -> dataJson.put(key, value));
        return dataJson;
    }

    protected JSONObject hashMapCardInfo(Map<Integer, CardInfo> data) {
        JSONObject dataJson = new JSONObject();
        data.forEach((key, value) -> dataJson.put(key, value.toMap()));
        return dataJson;
    }

    protected JSONObject stringJson(String name, String data, String name2, String data2) {
        JSONObject dataJson = new JSONObject();
        dataJson.put(name, data);
        dataJson.put(name2, data2);
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

    /**
     * Send received statistic to server.
     */
    protected void sendData(JSONObject data) {
        JSONObject dataToSendObject = new JSONObject();
        String currentTime = ZonedDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        dataToSendObject.put("date", currentTime);
        dataToSendObject.put("data", data);
        String dataToSend = dataToSendObject.toString().replaceAll("\\\\", "");
    }

}
