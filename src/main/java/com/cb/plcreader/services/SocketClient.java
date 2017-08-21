package com.cb.plcreader.services;

import com.cb.plcreader.models.CaptureInformation;
import com.cb.plcreader.models.ConnObject;
import com.cb.plcreader.models.MetaData;
import com.cb.plcreader.models.Sensor;
import com.cloudant.client.api.Database;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
public class SocketClient {

    String message;
    Byte[] b = {6, 48, 49, 48, 48, 53, 56, 55, 48, 48, 48, 48, 52, 50, 54, 67, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 52, 50, 50, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 52, 51, 50, 53, 51, 51, 51, 51, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 65, 49, 49, 48, 48, 48, 48, 48, 53, 56, 48, 48, 48, 48, 48, 52, 50, 54, 52, 48, 48, 48, 48, 51, 50, 13};

    @Autowired
    Database database;

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    SocketClient() {
    }

    public void readData() {

        long startTime = System.currentTimeMillis();
        String host = "127.0.0.1";
        //String host = "166.251.157.66";
        int port = 2101;
        //Connecting to the PLC board
        ConnObject connObject = new ConnObject(host, port);
        try {
              connObject = this.connect(connObject);
        } catch (IOException e) {
            log.error("Error opening connection with the host. " + connObject.getIp() + e.getMessage());
            this.writeErrorMsg("Error opening connection with the host. " + connObject.getIp() + " " +e.getMessage(), connObject);
            return;
        }

        //Building the Request Object
        byte[] requestObject = this.buildReadRequest();

        //Send the Read Request to PLC
        this.sendRequest(connObject, requestObject);

        //Read the response from the server
        String responseString = this.getResponse(connObject);

        this.closeConnection(connObject);

        this.createCaptureDocument(responseString, connObject, startTime);

        //Todo Implement the logic the created document into Cloudant
        //this.writeCaptureDocument();
    }

    private ConnObject connect(ConnObject connObject) throws IOException {

        Socket requestSocket = new Socket(connObject.getIp(), connObject.getPort());

        if (requestSocket.isConnected()) {
            log.info("Connection opened with host " + connObject.getIp());
            connObject.setInputStream(requestSocket.getInputStream());
            connObject.setOutputStream(requestSocket.getOutputStream());
            connObject.setOnline(requestSocket.isConnected());
        }

        return connObject;
    }

    private byte[] buildReadRequest() {

        List<Byte> list = new ArrayList<Byte>();

        //ENQ
        list.add((byte) 5);

        //Ethernet Address
        list.add((byte) 'F');
        list.add((byte) 'F');

        //Fixed Value
        list.add((byte) '0');

        //Read Operation
        list.add((byte) 'R');

        //Data Register
        list.add((byte) 'A');

        //Data Register Address
        String addressHexString = Integer.toHexString(10000);
        char[] addressCharArray = addressHexString.toCharArray();
        for (char c : addressCharArray) {
            list.add((byte) c);
        }

        //Data Bytes to Read
        String dataBytesString = Integer.toHexString(32);
        char[] dataBytesArray = dataBytesString.toCharArray();
        for (char c : dataBytesArray) {
            list.add((byte) c);
        }
        List<Byte> request = addBcc(list);

        //Carriage Return
        request.add(Byte.valueOf("13"));
        byte[] requestByteArray = new byte[request.size()];
        for (int index = 0; index < request.size(); index++) {
            requestByteArray[index] = request.get(index);
        }

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

    private boolean sendRequest(ConnObject connObject, byte[] requestBody) {

        try {
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(connObject.getOutputStream());
            objectOutputStream.write(requestBody);
            objectOutputStream.flush();
            log.info("Request send to host " +connObject.getIp() );
        } catch (IOException e) {
            log.error("Error sending request to host " + connObject.getIp() + " " + e.getMessage());
            this.writeErrorMsg("Error sending request to host " + connObject.getIp() + " " + e.getMessage(), connObject);
            return false;
        }
        return true;
    }

    private String getResponse(ConnObject connObject) {

        String responseHexString = "";

        List<Byte> responseByteList = new ArrayList<Byte>();
        InputStream in = connObject.getInputStream();

        try {
            log.info("Waiting for response from host" + connObject.getIp());
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            log.error("Waiting  interrupted. " + e.getMessage());
            this.writeErrorMsg("Waiting  interrupted. " + e.getMessage(), connObject);
            return null;
        }

        try {
            if (in.available() == 0) {
                log.info("No Response recieved from host " + connObject.getIp() + ". Process will not attempt to read again automatically");
                this.writeErrorMsg("No Response recieved from host " + connObject.getIp() + ". Process will not attempt to read again automatically",connObject);
                return null;
            } else {
                log.info("Response of length " + in.available() + " recieved from host " + connObject.getIp());
                do {
                    responseByteList.add((byte) in.read());
                } while (in.available() != 0);

              responseByteList.clear();
                for (Byte aByte : b) {
                    responseByteList.add(aByte);
                }
                responseByteList = responseByteList.subList(4, responseByteList.size() - 1);
                char[] responseCharArray = new char[responseByteList.size()];

                for (Byte aByte : responseByteList) {
                    int i = Integer.parseInt(aByte.toString());
                    responseHexString = responseHexString + Character.toString((char) i);
                }

            }
        } catch (IOException e) {
            log.error("Error reading InputStream of host " + connObject.getIp() + e.getMessage());
            this.writeErrorMsg("Error reading InputStream of host " + connObject.getIp() + e.getMessage(), connObject);
            return null;
        }
        return responseHexString;
    }

    private boolean createCaptureDocument(String responseString, ConnObject connObject, long startTime) {


        CaptureInformation captureInformation = new CaptureInformation(new Date(), connObject.getIp(), connObject.isOnline(), connObject.getIp(), connObject.getPort());
        Sensor sensor = new Sensor();

        sensor.setPortEngineRpm(getWord(responseString.substring(0, 4)));
        sensor.setPortEngineOilPsr(getFloat(responseString.substring(8, 16)));

        MetaData metaData = new MetaData(responseString, System.currentTimeMillis() - startTime);
        captureInformation.setSensors(sensor);
        captureInformation.setMetaData(metaData);
        ObjectMapper mapper = new ObjectMapper();
        database.save(captureInformation);
        return true;

    }

    private float getFloat(String substring) {
        Long i = Long.parseLong(substring, 16);
        return Float.intBitsToFloat(i.intValue());

    }

    private void writeErrorMsg(String errMessage, ConnObject connObject) {
        CaptureInformation captureInformation = new CaptureInformation(new Date(), connObject.getIp(), connObject.isOnline(), connObject.getIp(), connObject.getPort());
        captureInformation.setErrMsg(errMessage);
        database.save(captureInformation);
    }

    private int getWord(String substring) {
        return Integer.parseInt(substring.trim(), 16);
    }

    private void closeConnection(ConnObject connObject) {

        try {
            connObject.getInputStream().close();
            connObject.getOutputStream().close();
            connObject.getRequestSocket().close();
        } catch (IOException e) {
            log.error("Error closing connection. " + e.getMessage());
            log.error("Is Still Connected" + connObject.getRequestSocket().isConnected());
            this.writeErrorMsg("Error closing connection. " + e.getMessage(), connObject);

        } catch (NullPointerException e) {
            log.info("Connection closed with host " + connObject.getIp());
        }

    }

}

