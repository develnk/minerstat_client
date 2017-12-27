package com.minerstat.algorithm.claymore.model.request;

import java.util.List;

public class TCPDataFromClient {
    private Integer id;
    private String error;
    private List result;

    public TCPDataFromClient() {}

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public List getResult() {
        return result;
    }

    public void setResult(List result) {
        this.result = result;
    }
}
