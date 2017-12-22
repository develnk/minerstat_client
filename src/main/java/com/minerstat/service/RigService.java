package com.minerstat.service;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.internal.LinkedTreeMap;
import com.minerstat.MainApp;
import com.minerstat.settings.Settings;
import com.minerstat.entity.UserRig;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.gson.Gson;

import static com.minerstat.MainApp.currentDir;


public class RigService {

    final static String rigsFile = "rigs";

    private Common commonService;

    public RigService () {
        commonService = new Common();
    }

    public List<UserRig> getUserRigs() {
        List<UserRig> userRigList = new ArrayList<>();

        try {
            HttpResponse response = Common.serverHttpSend("rig/all_rigs");
            if (response.getStatusLine().getStatusCode() == 200) {
                BufferedReader br = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
                Gson gson = new Gson();
                JSONArray serverResponse = gson.fromJson(br, JSONArray.class);
                serverResponse.forEach(userRig -> {
                    UserRig object = new Gson().fromJson(new Gson().toJson(((LinkedTreeMap<String, Object>) userRig)), UserRig.class);
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
