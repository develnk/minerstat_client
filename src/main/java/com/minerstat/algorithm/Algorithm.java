package com.minerstat.algorithm;

import org.json.simple.JSONObject;

public interface Algorithm {

    /**
     * Start point to execute algorithm.
     */
    void startAlgorithm();

    /**
     * To stop executing algorithm.
     */
    void stopAlgorithm();

    /**
     * Send received statistic to server.
     */
    void sendData(JSONObject data);
}
