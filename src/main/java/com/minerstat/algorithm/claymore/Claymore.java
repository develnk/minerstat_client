package com.minerstat.algorithm.claymore;

import com.google.gson.Gson;
import com.minerstat.algorithm.Algorithm;
import com.minerstat.algorithm.MinerCommon;
import com.minerstat.algorithm.claymore.model.request.TCPToServer;
import com.minerstat.model.request.SendLog;
import com.minerstat.settings.Settings;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import java.io.File;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

public class Claymore extends MinerCommon implements Algorithm {

    @Min(1) @Max(65535)
    private int port;

    @NotNull
    private String minerDirectory;
    private boolean useLogs;
    private Timer tcpTimer;
    private Timer logsTimer;

    private TimerTask tcpTask = new TimerTask() {
        @Override
        public void run() {
            try {
                Callable<String> tcp = new TcpRequest(port);
                FutureTask<String> task = new FutureTask<>(tcp);
                Thread t = new Thread(task);
                t.start();
                sendData(prepareDataToSendToServer(task.get(), 0));
            }
            catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
    };

    private TimerTask logsTask = new TimerTask() {
        @Override
        public void run() {
            try {
                File directory = new File(minerDirectory);
                Callable<String> log = new LogAnalytic(directory);
                FutureTask<String> task = new FutureTask<>(log);
                Thread t = new Thread(task);
                t.start();
                sendData(prepareDataToSendToServer(task.get(), 1));
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
    };

    public Claymore() {
        port = Integer.parseInt(Settings.getInstance().getProperties("remotePort"));
        minerDirectory = Settings.getInstance().getProperties("DirectoryText");
        useLogs = Boolean.parseBoolean(Settings.getInstance().getProperties("logsFlag"));
    }

    public void startAlgorithm() {
        tcpTimer = new Timer();
        tcpTimer.schedule(tcpTask, 0, 10000);
        if (useLogs) {
            logsTimer = new Timer();
            logsTimer.schedule(logsTask, 0, 60000);
        }
    }

    public void stopAlgorithm() {
        tcpTimer.cancel();
        if (useLogs) {
            logsTimer.cancel();
        }
    }

    protected SendLog prepareDataToSendToServer(String data, int type) {
        Gson gson = new Gson();
        SendLog result = new SendLog();

        switch (type) {
            case 0:
                result.setTcp(gson.fromJson(data, TCPToServer.class));
                break;

            case 1:
                result.setLog(gson.fromJson(data, LogData.class));
                break;
        }

        return result;
    }

}
