package com.cb.plcreader.executor.util;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class server {

    ServerSocket providerSocket;
    Socket connection = null;
    ObjectOutputStream out;
    ObjectInputStream in;
    byte[] responseByteArray = {(byte) 'R', (byte) 'F'};
    static byte[] b = {6, 48, 49, 48, 48, 53, 56, 51, 48, 48, 48, 48, 52, 50, 54, 67, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 52, 50, 50, 56, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 52, 54, 13};
    server() {
    }

    void run() {
        try {
            //1. creating a server socket
            providerSocket = new ServerSocket(2101, 10);
            //2. Wait for connection
            System.out.println("Waiting for connection");
            System.out.println();
            connection = providerSocket.accept();
            System.out.println("Connection received from " + connection.getInetAddress().getHostName());
            //3. get Input and Output streams
            out = new ObjectOutputStream(connection.getOutputStream());
            out.flush();
            out.write(b);
            out.flush();
            in = new ObjectInputStream(connection.getInputStream());

        } catch (IOException ioException) {
            ioException.printStackTrace();
        } finally {
            //4: Closing connection
            try {
                in.close();
                out.close();
                providerSocket.close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }

    public static void main(String args[]) {
        server server = new server();
        while (true) {
            server.run();
        }
    }
}