package com.minerstat.service;

import com.minerstat.entity.UserRig;
import org.apache.http.HttpResponse;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;

import static com.minerstat.MainApp.currentDir;


public class RigService {

    private final static String rigsFile = "rigs";

    private Common commonService;

    public RigService () {
        commonService = new Common();
    }

    public List<UserRig> getUserRigs() {
        List<UserRig> userRigList = new ArrayList<>();

        try {
            HttpResponse response = Common.serverHttpPOSTSend("rig/all_rigs");
            if (response.getStatusLine().getStatusCode() == 200) {
                BufferedReader br = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
                Gson gson = new Gson();
                ArrayList serverResponse = gson.fromJson(br, ArrayList.class);
                serverResponse.forEach(userRig -> {
                    UserRig object = new Gson().fromJson(new Gson().toJson(userRig), UserRig.class);
                    userRigList.add(object);
                });
            }
        }
        catch (IOException e) {
        }
        finally {
            commonService.saveUserRigs(currentDir + "/" + rigsFile, userRigList);
        }

        return userRigList;
    }

    public List<UserRig> readUserRigs() {
        String fullFilePath = currentDir  + "/" + rigsFile;
        return (List<UserRig>) commonService.readUserFileSettings(fullFilePath, UserRig.class);
    }

}
