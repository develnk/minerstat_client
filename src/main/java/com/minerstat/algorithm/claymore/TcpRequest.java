package com.minerstat.algorithm.claymore;

import java.net.*;
import java.io.*;
import java.util.concurrent.*;

import com.google.gson.Gson;
import com.minerstat.algorithm.MinerCommon;
import com.minerstat.algorithm.claymore.model.request.BaseRequest;
import com.minerstat.algorithm.claymore.model.request.TCPDataFromClient;

public class TcpRequest extends MinerCommon implements Callable<String> {

    private int port;

    public TcpRequest(int port) {
        this.port = port;
    }

    public String call() {
        String result = null;
        Gson gson = new Gson();
        Socket socket;
        try {
            socket = new Socket("192.168.1.156", port);
            DataInputStream is = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
            DataOutputStream os = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
            String request = newRequest();
            send(os, request.getBytes());

            byte[] byteData = receive(is);
            result = new String(byteData);
            // Removes NULL chars.
            result = result.replaceAll("\u0000", "");
            TCPDataFromClient tcpData = gson.fromJson(result, TCPDataFromClient.class);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    private String newRequest() {
        Gson gson = new Gson();
        BaseRequest baseRequest = new BaseRequest();
        baseRequest.setId(0);
        baseRequest.setMethod("miner_getstat1");
        baseRequest.setJsonrpc("2.0");
        return gson.toJson(baseRequest);
    }

    private static byte[] receive(DataInputStream is) throws Exception {
        try {
            byte[] inputData = new byte[1024];
            is.read(inputData);
            return inputData;
        }
        catch (Exception e) {
            throw e;
        }
    }

    private static void send(DataOutputStream os, byte[] byteData) throws Exception {
        try {
            os.write(byteData);
            os.flush();
        }
        catch (Exception e) {
            throw e;
        }
    }

}
