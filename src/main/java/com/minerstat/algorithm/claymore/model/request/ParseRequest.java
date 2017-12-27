package com.minerstat.algorithm.claymore.model.request;

import com.minerstat.algorithm.claymore.CardInfo;

import java.util.Map;

public class ParseRequest {
    private Map<Integer, Map<String, Integer>> log;
    private Map<Integer, CardInfo> info;

    public ParseRequest() {}

    public Map<Integer, Map<String, Integer>> getLog() {
        return log;
    }

    public void setLog(Map<Integer, Map<String, Integer>> log) {
        this.log = log;
    }

    public Map<Integer, CardInfo> getInfo() {
        return info;
    }

    public void setInfo(Map<Integer, CardInfo> info) {
        this.info = info;
    }
}
