package com.minerstat.service;

import com.google.gson.Gson;
import com.minerstat.entity.Worker;
import org.apache.http.HttpResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import static com.minerstat.MainApp.currentDir;

public class WorkerService {

    private Common commonService;

    final static String workersFile = "workers";

    public WorkerService() {
        commonService = new Common();
    }

    public List<Worker> getUserWorkers() {
        List<Worker> result = new ArrayList<>();
        try {
            HttpResponse response = Common.serverHttpPOSTSend("worker/all_workers");
            if (response.getStatusLine().getStatusCode() == 200) {
                BufferedReader br = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
                Gson serverGson = new Gson();
                ArrayList serverResponse = serverGson.fromJson(br, ArrayList.class);
                serverResponse.forEach(userRig -> {
                    Worker object = new Gson().fromJson(new Gson().toJson(userRig), Worker.class);
                    result.add(object);
                });
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            commonService.saveUserRigs(currentDir + "/" + workersFile, result);
        }

        return result;
    }

    public List<Worker> readWorkers() {
        String fullFilePath = currentDir  + "/" + workersFile;
        return (List<Worker>) commonService.readUserFileSettings(fullFilePath, Worker.class);
    }

}
