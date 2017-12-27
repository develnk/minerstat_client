package com.minerstat.model.request;

public class SendLog {
    private Object tcp = null;
    private Object log = null;

    public SendLog() {}

    public Object getTcp() {
        return tcp;
    }

    public void setTcp(Object tcp) {
        this.tcp = tcp;
    }

    public Object getLog() {
        return log;
    }

    public void setLog(Object log) {
        this.log = log;
    }
}
