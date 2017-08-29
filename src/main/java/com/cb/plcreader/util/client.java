package com.cb.plcreader.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class client {
    Socket requestSocket;
    ObjectOutputStream out;
    InputStream in;
    String message;
    List<Byte> responseByteList = new ArrayList<Byte>();
    static Byte[] b = {6, 48, 49, 48, 48, 53, 69, 50, 48, 48, 48, 48, 52, 50, 55, 67, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 52, 50, 51, 52, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 51, 54, 13};


    client() {
    }

    void run() {
        try {
            String hostIp= "localhost";
            //String hostIp = "166.251.157.66";
            //String hostIp = "166.184.21.35";
            //1. creating a socket to connect to the server
            System.out.println("Running");
            try {
                requestSocket = new Socket(hostIp, 2101);
            } catch (Exception e) {
                System.out.println(e.getCause());
                System.out.println("Retrying to connect to Host" + hostIp);
                requestSocket = new Socket(hostIp, 2101);
            } finally {
                System.out.println("requestSocket = " + requestSocket.isConnected());
                System.out.println("Connected to Host" + requestSocket.getRemoteSocketAddress());
            }
            //2. get Input and Output streams
            out = new ObjectOutputStream(requestSocket.getOutputStream());
            out.flush();
            in = requestSocket.getInputStream();
            //3: Communicating with the server
            sendMessage();
            System.out.println("Sleeping for response from server");
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("Awake");
            //in = new ObjectInputStream(requestSocket.getInputStream());
            do {
                System.out.println("reading Inputs");
                System.out.println("in.available() = " + in.available());
                responseByteList.add((byte) in.read());
                System.out.println("Read a Byte");
            } while (in.available() != 0);
            System.out.println("out of the read loop");
            byte[] requestByteArray = new byte[responseByteList.size()];
            for (int index = 0; index < responseByteList.size(); index++) {
                requestByteArray[index] = responseByteList.get(index);
            }
            //bytesToHex(requestByteArray);
            decode(responseByteList);
            System.out.println("responseByteList.toString() = " + responseByteList.toString());

        } catch (UnknownHostException unknownHost) {
            System.err.println("CBC Assert with the requested ID not found");
        } catch (IOException ioException) {
            ioException.printStackTrace();
        } finally {
            //4: Closing connection
            try {
                in.close();
                out.close();
                requestSocket.close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }

    void sendMessage() {
        System.out.println("Sending Request.....");
        try {
            out.write(GetNByteRequestCommand());
            out.flush();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    public static void main(String args[]) {
        client client = new client();
        client.run();
        // bytesToHex(b);
    }

    private static void decode() {

        List<Byte> lb = new ArrayList<Byte>();
        for (Byte aByte : b) {
            lb.add(aByte);
        }

        for (int i = 0; i < 5; i++) {
            int tmp = 0;
            for (int j = 0; j < 4; j++) {
                int lok = Integer.parseInt(lb.get(4 * (i + 1) + j).toString());
                tmp = tmp * 16 + Character.getNumericValue((char) lok);
            }
            System.out.println("tmp = " + tmp);
        }
    }

    private static void decode(List<Byte> responseByteList) {

        List<Byte> lb = responseByteList;

        for (int i = 0; i < 16; i++) {
            int tmp = 0;
            for (int j = 0; j < 4; j++) {
                int lok = Integer.parseInt(lb.get(4 * (i + 1) + j).toString());
                System.out.print((char) lok);
                tmp = tmp * 16 + Character.getNumericValue((char) lok);
            }
            System.out.println("tmp = " + tmp);
        }
    }


    public static String bytesToHex(Byte[] in) {
        System.out.println("in.length = " + in.length);
        final StringBuilder builder = new StringBuilder();
        for (Byte aByte : in) {
            int lok = Integer.parseInt(aByte.toString());
            System.out.println((char) lok);
            //System.out.println(Character.getNumericValue((char) lok));
            builder.append(String.format("%02x", aByte));
        }
        //System.out.println(builder.toString());
        return builder.toString();
    }


    public byte[] GetNByteRequestCommand() {
        List<Byte> list = new ArrayList<Byte>();
        /*ENQ*/
        list.add((byte) 5);
        /*Ethernet Address*/
        list.add((byte) 'F');
        list.add((byte) 'F');
        /*Fixed Value*/
        list.add((byte) '0');
        /*Read Operation*/
        list.add((byte) 'R');
        /*Data Register*/
        list.add((byte) 'A');
       /*Data Register Address*/
        String addressHexString = Integer.toHexString(10000);
        char[] addressCharArray = addressHexString.toCharArray();
        for (char c : addressCharArray) {
            list.add((byte) c);
        }
        /*Data Bytes to Read*/
        String dataBytesString = Integer.toHexString(64);
        char[] dataBytesArray = dataBytesString.toCharArray();
        for (char c : dataBytesArray) {
            list.add((byte) c);
        }
        List<Byte> request = addBcc(list);
        /*Carriage Return*/
        request.add(Byte.valueOf("13"));
        byte[] requestByteArray = new byte[request.size()];
        for (int index = 0; index < request.size(); index++) {
            requestByteArray[index] = request.get(index);
        }
        System.out.println("request = " + request);
        return requestByteArray;
    }

    private List<Byte> addBcc(List<Byte> list) {
        int bcc = 0;
        for (Byte aByte : list) {
            bcc = bcc ^ aByte;
        }
        String bccString = Integer.toHexString(bcc);
        char[] bccArray = bccString.toCharArray();
        for (char c : bccArray) {
            list.add((byte) c);
        }
        return list;
    }


}