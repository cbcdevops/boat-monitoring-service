package com.cb.plcreader.executor.services;


import com.cb.plcreader.executor.models.*;
import com.cloudant.client.api.Database;
import com.cb.plcreader.executor.models.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Component
@Scope("prototype")
public class SocketService extends Thread {

    Byte[] b = {6, 48, 49, 48, 48, 53, 56, 55, 48, 48, 48, 48, 52, 50, 54, 67, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 52, 50, 50, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 52, 51, 50, 53, 51, 51, 51, 51, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 65, 49, 49, 48, 48, 48, 48, 48, 53, 56, 48, 48, 48, 48, 48, 52, 50, 54, 52, 48, 48, 48, 48, 51, 50, 13};

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private long startTime;
    private long endTime;
    private final int FLOAT_HEX_LENGTH = 8;
    private final int WORD_HEX_LENGTH = 4;
    private final int INTEGER_HEX_LENGTH = 4;
    private boolean ifNoError = true;
    private AssetInformation assetInformation;
    private CaptureInformation captureInformation;
    private ConnObject connObject;
    private List<SensorInfo> pmeSensorInfoList;
    private ArrayList<Sensors> sensors = new ArrayList<Sensors>();
    private String responseString = null;


    @Autowired
    Database database;

    @Override
    public  void run() {
        connObject = new ConnObject();
        startTime = System.currentTimeMillis();
        assetInformation = (AssetInformation) Globals.assetsToProcess.pop();
        if (this.openSocketConnection()) {
            startTime = System.currentTimeMillis();
            this.createCaptureDocument();
            sensors.clear();
            this.populateSensorValues();
            this.closeSocketConnection();
            endTime = System.currentTimeMillis();
            captureInformation.setSensors(sensors);
            captureInformation.setCaptureInfo(new CaptureInfo(endTime - startTime, responseString));
            this.database.save(captureInformation);
        } else {
            this.createCaptureDocument();
            this.writeErrorMsg("No Connectivity with Asset " + assetInformation.getAssetName() + ".");
        }
    }

    /**
     * @return result of the socket connection creation
     * @description Create the connection with the given asset
     */
    private boolean openSocketConnection() {

        try {
            connObject.setIp(assetInformation.getHostAddress1());
            connObject.setPort(assetInformation.getPort());
            this.createSocket();
            return true;
        } catch (IOException e) {
            log.error("Error opening connection with the asset using primary IP. " + connObject.getIp() + " " + e.getMessage());
            connObject.setIp(assetInformation.getHostAddress2());
            connObject.setPort(assetInformation.getPort());
            try {
                this.createSocket();
                return true;
            } catch (IOException e1) {
                this.createCaptureDocument();
                log.error("Error opening connection with the asset using secondary IP. " + connObject.getIp() + " " + e.getMessage());
                return false;
            }
        }
    }

    /**
     * @throws IOException
     * @description Open a socket with the asset
     */
    private void createSocket() throws IOException {

        Socket requestSocket = new Socket(connObject.getIp(), connObject.getPort());

        if (requestSocket.isConnected()) {
            log.debug("Connection opened with host " + connObject.getIp() + ":" + connObject.getPort());
            connObject.setInputStream(requestSocket.getInputStream());
            connObject.setOutputStream(requestSocket.getOutputStream());
            connObject.setOnline(requestSocket.isConnected());
        }

    }

    /**
     * @param dataRegister - Data register to be read
     * @param noOfDr       - Quanity of data register to be read
     * @return readResponse - Sesnor info as a Hex String.
     */
    private String readSensor(int dataRegister, int noOfDr) {

        //Building the Request Object
        byte[] requestObject = this.buildReadRequest(dataRegister, noOfDr);

        //Send the Read Request to PLC
        this.sendRequest(requestObject);

        //Read the response from the server
        return this.readResponse();
        //this.closeSocketConection(connObject);

    }

    /**
     * @description populate the portmain engine sensor information.
     * @Notes : Variable startIndex denotes the start position of the value in the responseString(Hex)
     */
    private boolean populateSensorValues() {

        //Todo Read all the Memory into one HexString.
        responseString = readSensor(10000, 64);
        //responseString = responseString + readSensor(10064, 64);
        //responseString = responseString + readSensor(10128, 18);
        //responseString = responseString + "0000000000000000000000000000000000000000000000";
        //responseString = responseString + readSensor(10200, 64);

        if (responseString == null) {
            return true;
        }

        Map sensorValue = new HashMap();
        String selectorJson = "{\"selector\":{\"docType\":{\"$eq\":\"sensor_info\"}},\"fields\":[\"_id\",\"_rev\"],\"sort\":[{\"docType\":\"asc\"}]}";
        pmeSensorInfoList = database.findByIndex(selectorJson, SensorInfo.class);
        List<String> sensorList = Arrays.asList(assetInformation.getReadSensorList());
        sensors.clear();
        for (SensorInfo sensorInfo : pmeSensorInfoList) {
            //Todo : The value 10000 changes based upon the boat's profile.
            //Todo: This info should be read from the assetDoc.
            int startIndex = (sensorInfo.getDataRegister() - 10000) * 4;
            switch (sensorInfo.getDataType()) {
                case "FLOAT":
                    //Todo get the offsets from the sensor object
                    sensors.add(new Sensors(sensorInfo.getSensorName(), getFloat(responseString.substring(startIndex, startIndex + FLOAT_HEX_LENGTH)), sensorInfo.getSensorGroup()));
                    break;

                case "WORD":
                    sensors.add(new Sensors(sensorInfo.getSensorName(), getWord(responseString.substring(startIndex, startIndex + WORD_HEX_LENGTH)), sensorInfo.getSensorGroup()));
                    break;

                case "INTEGER":
                    sensors.add(new Sensors(sensorInfo.getSensorName(), getWord(responseString.substring(startIndex, startIndex + INTEGER_HEX_LENGTH)), sensorInfo.getSensorGroup()));
                    break;
            }

        }
        return false;
    }

    /**
     * @param dataRegister
     * @param noOfDr
     * @return
     */
    private byte[] buildReadRequest(int dataRegister, int noOfDr) {

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
        String addressHexString = Integer.toHexString(dataRegister);
        char[] addressCharArray = addressHexString.toCharArray();
        for (char c : addressCharArray) {
            list.add((byte) c);
        }

        //Data Bytes to Read
        String dataBytesString = Integer.toHexString(noOfDr);
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

    /**
     * @param list
     * @return
     * @description add Bcc to the request Object
     */
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

    /**
     * @param requestBody
     * @return
     * @description Send the request to the asset
     */
    private boolean sendRequest(byte[] requestBody) {

        try {
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(connObject.getOutputStream());
            objectOutputStream.write(requestBody);
            objectOutputStream.flush();
            log.debug("Request send to host " + connObject.getIp());
        } catch (IOException e) {
            log.error("Error sending request to host " + connObject.getIp() + " " + e.getMessage());
            this.writeErrorMsg("Error sending request to host " + connObject.getIp() + " " + e.getMessage());
            return false;
        }
        return true;
    }

    /**
     * @return response String
     */
    private String readResponse() {

        String responseHexString = "";

        List<Byte> responseByteList = new ArrayList<Byte>();
        InputStream in = connObject.getInputStream();

        try {
            log.debug("Waiting for response from host" + connObject.getIp());
            //Todo Make the socket connection Async to reduce the waitime
            TimeUnit.SECONDS.sleep(4);
        } catch (InterruptedException e) {
            log.error("Waiting  interrupted. " + e.getMessage());
            this.writeErrorMsg("Waiting  interrupted. " + e.getMessage());
            return null;
        }

        try {
            if (in.available() == 0) {
                log.debug("No response recieved from host " + connObject.getIp() + ". Process will not attempt to read again automatically");
                this.writeErrorMsg("No Response recieved from host " + connObject.getIp() + ". Process will not attempt to read again automatically");
                return null;
            } else {
                log.debug("Response of length " + in.available() + " recieved from host " + connObject.getIp());
                do {
                    responseByteList.add((byte) in.read());
                } while (in.available() != 0);

/*                responseByteList.clear();
                for (Byte aByte : b) {
                    responseByteList.add(aByte);
                }*/
                responseByteList = responseByteList.subList(4, responseByteList.size() - 1);
                char[] responseCharArray = new char[responseByteList.size()];

                for (Byte aByte : responseByteList) {
                    int i = Integer.parseInt(aByte.toString());
                    responseHexString = responseHexString + Character.toString((char) i);
                }

            }
        } catch (IOException e) {
            log.error("Error reading InputStream of host " + connObject.getIp() + e.getMessage());
            this.writeErrorMsg("Error reading InputStream of host " + connObject.getIp() + e.getMessage());
            return null;
        }
        return responseHexString;
    }

    /**
     * @return Return true if the captureInformation object
     */
    private boolean createCaptureDocument() {
        captureInformation = new CaptureInformation(assetInformation.getAssetName(), assetInformation.get_id(), assetInformation.getAssetAbbreviation(), new Date(), connObject.getIp(), connObject.isOnline());
        return true;
    }

    /**
     * @param errMessage
     * @description write the error message into database
     */
    private void writeErrorMsg(String errMessage) {
        captureInformation.setErrMsg(errMessage);
        database.save(captureInformation);
    }

    /**
     * @param substring
     * @return
     * @description conver the given hex string into Float value
     */
    private float getFloat(String substring) {
        Long i = Long.parseLong(substring, 16);
        return Float.intBitsToFloat(i.intValue());

    }

    /**
     * @param substring
     * @return
     * @description Convert the given hex string into word
     */
    private int getWord(String substring) {
        return Integer.parseInt(substring.trim(), 16);
    }

    /**
     * @description Close the connection with the asset
     */
    private void closeSocketConnection() {

        try {
            connObject.getInputStream().close();
            connObject.getOutputStream().close();
            connObject.getRequestSocket().close();
        } catch (IOException e) {
            log.error("Error closing connection. " + e.getMessage());
            log.error("Is Still Connected" + connObject.getRequestSocket().isConnected());
            this.writeErrorMsg("Error closing connection. " + e.getMessage());

        } catch (NullPointerException e) {
            log.debug("Connection closed with host " + connObject.getIp());
        }

    }

}

