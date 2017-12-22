package com.minerstat.service;

import com.google.gson.Gson;
import com.minerstat.MainApp;
import com.minerstat.entity.UserRig;
import com.minerstat.settings.Settings;
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
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.minerstat.MainApp.currentDir;

public class Common {

    public Common() {}

    // Login.
    public Boolean authorization(String user_name, String password ) {
        Boolean result = false;

        try {
            HashMap<String, String> body = new HashMap<>();
            body.put("name", user_name);
            body.put("password", password);
            HttpResponse response = serverHttpSend("user/login", body);
            BufferedReader br = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            Gson gson = new Gson();
            JSONObject serverResponse = gson.fromJson(br, JSONObject.class);
            if (response.getStatusLine().getStatusCode() == 200 && !serverResponse.get("token").equals("")) {
                Settings.getInstance().saveProperties("user_name", user_name);
                Settings.getInstance().saveProperties("password", password);
                Settings.getInstance().saveProperties("token", String.valueOf(serverResponse.get("token")));
                System.out.println(serverResponse.get("token"));
                result = true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

    public boolean CheckAuthorizationSettings() {
        return Settings.getInstance().getProperties("token").isEmpty();
    }

    @SafeVarargs
    public static HttpResponse serverHttpSend(String url, HashMap<String, String>... data) {
        HttpResponse response = null;

        try {
            HttpPost request = new HttpPost(MainApp.serverUrl + url);
            if (data.length > 0) {
                JSONObject dataToSendObject = new JSONObject();
                data[0].forEach(dataToSendObject::put);
                String dataToSend = dataToSendObject.toString();
                StringEntity params = new StringEntity(dataToSend);
                request.setEntity(params);
            }

            String bearer = "Bearer " + Settings.getInstance().getProperties("token");
            Header authHeader = new BasicHeader("authorization", bearer);
            request.setHeader(authHeader);
            request.setHeader("Content-Type", "application/json;charset=UTF-8");
            HttpClient httpclient = HttpClients.custom().build();
            response = httpclient.execute(request);
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        return response;
    }

    public List readUserFileSettings(String filePath, Class className) {
        List result = new ArrayList<>();

        try {
            if (new File(filePath).exists()) {
                InputStream inputStream = new FileInputStream(filePath);
                long fileSize = new File(filePath).length();
                byte[] allBytes = new byte[(int) fileSize];
                inputStream.read(allBytes);
                String buffer = new String(allBytes, 0, (int) fileSize, "UTF-8");
                Gson gson = new Gson();
                JSONArray arrayResult = gson.fromJson(buffer, JSONArray.class);
                arrayResult.forEach((Object o) -> {
                    Object userRigTemp = new Gson().fromJson(new Gson().toJson(o), className);
                    result.add(userRigTemp);
                });
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

    public void saveUserRigs(String filePath, List<?> data) {
        try {
            Gson gson = new Gson();
            byte[] bytes = gson.toJson(data).getBytes();
            try (OutputStream outputStream = new FileOutputStream(filePath)) {
                outputStream.write(bytes);
                outputStream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
