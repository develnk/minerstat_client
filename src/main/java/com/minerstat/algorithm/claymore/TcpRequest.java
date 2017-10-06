package com.minerstat.algorithm.claymore;

import java.net.*;
import java.io.*;
import java.util.HashMap;
import java.util.concurrent.*;

import com.minerstat.algorithm.MinerCommon;
import org.json.simple.JSONObject;

public class TcpRequest extends MinerCommon implements Callable<String> {

    private int port;

    TcpRequest(int port) {
        this.port = port;
    }

    public String call() {
        String result = "";
        Socket socket;
        try {
            socket = new Socket("localhost", port);
            DataInputStream is = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
            DataOutputStream os = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
            String request = newRequest();
            send(os, request.getBytes());

            byte[] byteData = receive(is);
            result = new String(byteData);
            // Removes NULL chars.
            result = result.replaceAll("\u0000", "");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    private String newRequest() {
        HashMap<Object, Object> map = new HashMap<>();
        map.put("id", 0);
        map.put("jsonrpc", "2.0");
        map.put("method", "miner_getstat1");
        String x = map.toString();
        JSONObject result = dataToSend(map);
        return result.toString();
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
