package com.minerstat.service;

import com.google.gson.Gson;
import com.minerstat.MainApp;
import com.minerstat.model.request.AuthenticationRequest;
import com.minerstat.model.response.AuthenticationResponse;
import com.minerstat.settings.Settings;
import org.apache.http.Consts;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Common {

    public Common() {}

    // Login.
    public Boolean authorization(String user_name, String password ) {
        Boolean result = false;

        try {
            Gson body = new Gson();
            AuthenticationRequest authenticationRequest = new AuthenticationRequest(user_name, password);
            HttpResponse response = serverHttpPOSTSend("user/login", body.toJson(authenticationRequest));
            HttpEntity entity = response.getEntity();
            String responseString = EntityUtils.toString(entity, "UTF-8");
            Gson gson = new Gson();
            if (response.getStatusLine().getStatusCode() == 200 && !gson.fromJson(responseString, AuthenticationResponse.class).getToken().equals("")) {
                Settings.getInstance().saveProperties("user_name", user_name);
                Settings.getInstance().saveProperties("password", password);
                String token = gson.fromJson(responseString, AuthenticationResponse.class).getToken();
                Settings.getInstance().saveProperties("token", token);
                System.out.println(token);
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

    public static HttpResponse serverHttpPOSTSend(String url, String... data) {
        HttpResponse response = null;

        try {
            HttpPost request = new HttpPost(MainApp.serverUrl + url);
            if (data.length > 0) {
                Gson dataToSendObject = new Gson();
                String dataToSend = "";
                if (data.length == 1) {
                    dataToSend = data[0];
                }
                else {
                    dataToSend = dataToSendObject.toJson(data, String[].class);
                }
//                String dataToSend = "{\"workerId\":\"b792a33d-de9c-4f26-b1da-12041d8e7e29\",\"data\":{\"log\":{\"eth_total\":{\"total_hash\":\"104959\",\"number\":\"34\",\"rejected\":1,\"invalid\":\"\"},\"dec_total\":{\"total_hash\":\"1\",\"number\":\"34\",\"rejected\":\"1\",\"invalid\":\"\"},\"all_gpu\":[{\"eth_hash\":\"26251\",\"dec_hash\":\"249392\",\"temp\":\"63\",\"fan_speed\":\"71\"},{\"eth_hash\":\"26220\",\"dec_hash\":\"249099\",\"temp\":\"69\",\"fan_speed\":\"71\"},{\"eth_hash\":\"26240\",\"dec_hash\":\"249285\",\"temp\":\"59\",\"fan_speed\":\"50\"},{\"eth_hash\":\"26262\",\"dec_hash\":\"249489\",\"temp\":\"60\",\"fan_speed\":\"72\"}],\"work_time\":\"20\"}}}";
                ContentType contentType = ContentType.create("application/json", Consts.UTF_8);
                StringEntity params = new StringEntity(dataToSend, contentType);
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
                ArrayList arrayResult = gson.fromJson(buffer, ArrayList.class);
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
