package com.cb.plcreader.executor.models;

import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Date;


public class ConnObject {

    Socket requestSocket;
    OutputStream outputStream;
    InputStream inputStream;
    boolean isOnline;
    String ip;
    int port;

    public ConnObject(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    @Override
    public String toString() {
        return "ConnObject{" +
                "requestSocket=" + requestSocket +
                ", outputStream=" + outputStream +
                ", inputStream=" + inputStream +
                ", isOnline=" + isOnline +
                ", ip='" + ip + '\'' +
                ", port=" + port +
                '}';
    }

    public boolean isOnline() {
        return isOnline;
    }

    public void setOnline(boolean online) {
        isOnline = online;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public Socket getRequestSocket() {
        return requestSocket;
    }

    public void setRequestSocket(Socket requestSocket) {
        this.requestSocket = requestSocket;
    }

    public OutputStream getOutputStream() {
        return outputStream;
    }

    public void setOutputStream(OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    public InputStream getInputStream() {
        return inputStream;
    }

    public void setInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    public ConnObject() {

    }
}

