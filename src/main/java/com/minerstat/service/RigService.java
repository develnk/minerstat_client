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

    public RigService () {}

    // Login.
    public Boolean ServiceAuthorization(String user_name, String password ) {
        Boolean result = false;

        try {
            HttpPost request = new HttpPost(MainApp.serverUrl + "user/login");
            JSONObject dataToSendObject = new JSONObject();
            dataToSendObject.put("name", user_name);
            dataToSendObject.put("password", password);
            String dataToSend = dataToSendObject.toString();
            StringEntity params = new StringEntity(dataToSend);
            request.setHeader("Content-Type", "application/json;charset=UTF-8");
            request.setEntity(params);
            HttpClient httpclient = HttpClients.custom().build();
            HttpResponse response = httpclient.execute(request);
            BufferedReader br = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            Gson gson = new Gson();
            JSONObject serverResponse = gson.fromJson(br, JSONObject.class);
            if (response.getStatusLine().getStatusCode() == 200 && !serverResponse.get("token").equals("")) {
                Settings.getInstance().saveProperties("user_name", user_name);
                Settings.getInstance().saveProperties("password", password);
                Settings.getInstance().saveProperties("token", String.valueOf(serverResponse.get("token")));
                System.out.println(serverResponse.get("token"));
                result = true;
//                Settings.getInstance().saveProperties("ridId", serverResponce.get("rigId").toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

    public List<UserRig> getUserRigs() {
        List<UserRig> userRigList = new ArrayList<>();

        try {
            HttpPost request = new HttpPost(MainApp.serverUrl + "rig/all_rigs");
            String bearer = "Bearer " + Settings.getInstance().getProperties("token");
            Header authHeader = new BasicHeader("authorization", bearer);
            request.setHeader(authHeader);
            request.setHeader("Content-Type", "application/json;charset=UTF-8");
            HttpClient httpclient = HttpClients.custom().build();
            HttpResponse response = httpclient.execute(request);
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
            saveUserRigs(userRigList);
        }

        return userRigList;
    }

    private void saveUserRigs(List<UserRig> userRigs) {
        try {
            Gson gson = new Gson();
            byte[] userRigsString = gson.toJson(userRigs).getBytes();
            try (OutputStream outputStream = new FileOutputStream(currentDir + "/" + rigsFile)) {
                outputStream.write(userRigsString);
                outputStream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<UserRig> readUserRigs() {
        List<UserRig> result = new ArrayList<>();

        try {
            String fullFilePath = currentDir  + "/" + rigsFile;
            if (new File(fullFilePath).exists()) {
                InputStream inputStream = new FileInputStream(fullFilePath);
                long fileSize = new File(fullFilePath).length();
                byte[] allBytes = new byte[(int) fileSize];
                inputStream.read(allBytes);
                String buffer = new String(allBytes, 0, (int) fileSize, "UTF-8");
                Gson gson = new Gson();
                JSONArray g = gson.fromJson(buffer, JSONArray.class);
                g.forEach(userRig -> {
                    UserRig object2 = new Gson().fromJson(new Gson().toJson(((LinkedTreeMap<String, Object>) userRig)), UserRig.class);
                    result.add(object2);
                });
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

    public boolean CheckAuthorizationSettings() {
        return Settings.getInstance().getProperties("token").isEmpty();
    }

}
