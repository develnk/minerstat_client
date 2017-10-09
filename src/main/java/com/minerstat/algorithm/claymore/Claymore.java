package com.minerstat.algorithm.claymore;

import com.minerstat.algorithm.Algorithm;
import com.minerstat.algorithm.MinerCommon;
import com.minerstat.settings.Settings;
import org.json.simple.JSONObject;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import java.io.File;
import java.util.*;
import java.util.concurrent.Callable;
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
                sendData(sendToServer(task.get(), 0));
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    private TimerTask logsTask = new TimerTask() {
        @Override
        public void run() {
            File directory = new File(minerDirectory);
            Callable<String> log = new LogAnalytic(directory);
            FutureTask<String> task = new FutureTask<>(log);
            Thread t = new Thread(task);
            t.start();

        }
    };

    public Claymore() {
        port = Integer.parseInt(Settings.getInstance().getProperties("remotePort"));
        minerDirectory = Settings.getInstance().getProperties("DirectoryText");
        useLogs = Boolean.parseBoolean(Settings.getInstance().getProperties("logsFlag"));
    }

    public void startAlgorithm() {
        tcpTimer = new Timer();
        tcpTimer.schedule(tcpTask, 0, 1000);
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

//    public void sendData(JSONObject data) {
//        super.sendData(data);
//    }

}
