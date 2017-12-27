package com.minerstat.algorithm;

import com.google.gson.Gson;
import com.minerstat.model.request.SendLog;
import com.minerstat.service.Common;
import com.minerstat.settings.Settings;
import org.apache.http.HttpResponse;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;

public abstract class MinerCommon {

    private Gson gson = new Gson();

    /**
     *
     * @param data
     *   Data to send.
     * @param type
     *   Type of data.
     *
     * @return
     */
    protected SendLog prepareDataToSendToServer(String data, int type) {
        SendLog result = new SendLog();

        switch (type) {
            case 0:
                result.setTcp(data);
                break;

            case 1:
                result.setLog(data);
                break;
        }

        return result;
    }

    /**
     * Send received statistic to server.
     */
    protected void sendData(SendLog data) {
        String currentTime = ZonedDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        HashMap<String, Object> body = new HashMap<>();
        body.put("date", currentTime);
        body.put("data", data);
        body.put("workerId", Settings.getInstance().getProperties("workerId"));

        HttpResponse response = Common.serverHttpPOSTSend("worker/stat", gson.toJson(body));
        if (response.getStatusLine().getStatusCode() == 200) {

        }
        System.out.println(body);
    }

}
