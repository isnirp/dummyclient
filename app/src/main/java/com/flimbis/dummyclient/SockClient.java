package com.flimbis.dummyclient;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class SockClient {
    private Socket socket = null;
    private PrintWriter out = null;
    private BufferedReader bf = null;
    private String TAG = SockClient.class.getName();
    private String host;
    private int port;
    public static String server;

    public SockClient(String host, int port) {
        this.host = host;
        this.port = port;
        this.server = "SERVER["+host+"]";
    }

    // open connection
    public int openConnection() {
        try {
            socket = new Socket(host, port);

            return 1; /* success */
        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0; /* fail */
    }

    // close connection
    public int close() {
        try {
            out.close();
            bf.close();
            socket.close();
            Log.d(TAG, "closed");

            return 1;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return 0;
    }

    // send data
    public int sendData(String msg) {
        if (out != null) {
            try {
                out = new PrintWriter(socket.getOutputStream(), true);
                out.println(TAG + ": " + msg);

                return 1;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return 0;
    }

    // receive data
    public String receiveData() {
        // read data from sockets input stream
        try {
            bf = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            return bf.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }
}
