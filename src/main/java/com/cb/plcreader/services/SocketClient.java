package com.cb.plcreader.services;

import com.cb.plcreader.models.*;
import com.cloudant.client.api.Database;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Component
public class SocketClient {

    Byte[] b = {6, 48, 49, 48, 48, 53, 56, 55, 48, 48, 48, 48, 52, 50, 54, 67, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 52, 50, 50, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 52, 51, 50, 53, 51, 51, 51, 51, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 65, 49, 49, 48, 48, 48, 48, 48, 53, 56, 48, 48, 48, 48, 48, 52, 50, 54, 52, 48, 48, 48, 48, 51, 50, 13};
    private ConnObject connObject = new ConnObject();
    private long startTime;
    private AssetInformation assetInformation;
    private List<SensorInfo> pmeSensorInfoList;
    private CaptureInformation captureInformation;
    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private ArrayList<Sensors> sensors = new ArrayList<Sensors>();
    private ArrayList<Values> values = new ArrayList<Values>();

    @Autowired
    Database database;

    /**
     * @description Created a default constructor of SocketClient
     */
    public SocketClient() {
    }

    /**
     * @param assetDoc
     * @return null
     * @description Read the register for the given asset
     */
    public void readAssetRegisters(AssetInformation assetDoc) {

        assetInformation = assetDoc;
        if (this.createConnection()) {
            startTime = System.currentTimeMillis();
            this.createCaptureDocument();
            sensors.clear();
            values.clear();
            this.populateSenorGroup("PORT_MAIN_ENGINE");
            captureInformation.setSensors(sensors);
            this.database.save(captureInformation);
            this.closeConnection();
        }
    }

    /**
     * @return result of the socket connection creation
     * @description Create the connection with the given asset
     */
    private boolean createConnection() {

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
                log.error("Error opening connection with the asset using secondary IP. " + connObject.getIp() + " "+ e.getMessage());
                this.writeErrorMsg("No Connectivity with Asset " + assetInformation.getAssetName() + ".");
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
            log.debug("Connection opened with host " + connObject.getIp());
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
        //this.closeConnection(connObject);

    }

    /**
     * @param sensorGroupName
     * @description populate the portmain engine sensor information.
     */
    private void populateSenorGroup(String sensorGroupName) {

        String responseString = null;

        switch (sensorGroupName) {
            case "PORT_MAIN_ENGINE":
                responseString = readSensor(10000, 32);
                break;
            default:
                log.error("Unknown Sensor Group");
                break;
        }

        if (responseString == null) {
            this.writeErrorMsg("No response for " + sensorGroupName + " sensors read ");
            return;
        }

        Map sensorValue = new HashMap();
        String selectorJson = "{\"selector\":{\"docType\":{\"$eq\":\"sensor_info\"},\"sensorGroup\":{\"$eq\":" + sensorGroupName + "}},\"fields\":[],\"sort\":[{\"_id\":\"asc\"}]}";
        pmeSensorInfoList = database.findByIndex(selectorJson, SensorInfo.class);
        List<String> sensorList = Arrays.asList(assetInformation.getReadSensorList());
        values.clear();

        //S0001,PORT_MAIN_ENGINE_RPM,D10000,WORD,PME
        if (sensorList.contains(pmeSensorInfoList.get(0).get_id()))
            values.add(new Values(pmeSensorInfoList.get(0).getSensorName(), Integer.toString(getWord(responseString.substring(0, 4)))));
        //S0002,PORT_MAIN_ENGINE_OIL_PRESSURE,D10002,FLOAT,PME
        if (sensorList.contains(pmeSensorInfoList.get(1).get_id()))
            values.add(new Values(pmeSensorInfoList.get(1).getSensorName(), Float.toString(getFloat(responseString.substring(8, 16)))));
        //S0003,PORT_MAIN_ENGINE_TURBO_OIL_PRESSURE,D10004,FLOAT,PME
        if (sensorList.contains(pmeSensorInfoList.get(2).get_id()))
            values.add(new Values(pmeSensorInfoList.get(2).getSensorName(), "NO_DATA"));
        //S0004,PORT_MAIN_ENGINE_FUEL_OIL_PRESSURE,D10006,FLOAT,PME
        if (sensorList.contains(pmeSensorInfoList.get(3).get_id()))
            values.add(new Values(pmeSensorInfoList.get(3).getSensorName(), Float.toString(getFloat(responseString.substring(24, 32)))));
        //S0005,PORT_MAIN_ENIGNE_PISTON_COOL_PSI,D10008,FLOAT,PME
        if (sensorList.contains(pmeSensorInfoList.get(4).get_id()))
            values.add(new Values(pmeSensorInfoList.get(4).getSensorName(), "NO_DATA"));
        //S0006,PORT_MAIN_ENGINE_RIGHT_BANK_WATER_PRESSURE,D10010,FLOAT,PME
        if (sensorList.contains(pmeSensorInfoList.get(5).get_id()))
            values.add(new Values(pmeSensorInfoList.get(5).getSensorName(), "NO_DATA"));
        //S0007,PORT_MAIN_ENGINE_LEFT_BANK_WATER_PRESSURE,D10012,FLOAT,PME
        if (sensorList.contains(pmeSensorInfoList.get(6).get_id()))
            values.add(new Values(pmeSensorInfoList.get(6).getSensorName(), "NO_DATA"));
        //S0008,PORT_MAIN_ENGINE_OIL_TEMPERATURE,D10014,FLOAT,PME
        if (sensorList.contains(pmeSensorInfoList.get(7).get_id()))
            values.add(new Values(pmeSensorInfoList.get(7).getSensorName(), "NO_DATA"));
        //S0009,PORT_MAIN_ENGINE_WATER_TEMPERATURE,D10016,FLOAT,PME
        if (sensorList.contains(pmeSensorInfoList.get(8).get_id()))
            values.add(new Values(pmeSensorInfoList.get(3).getSensorName(), Float.toString(getFloat(responseString.substring(64, 72)))));
        sensors.add(new Sensors(sensorGroupName, values));
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
            TimeUnit.SECONDS.sleep(1);
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
            this.writeErrorMsg("Error reading InputStream of host " + connObject.getIp() + e.getMessage());
            return null;
        }
        return responseHexString;
    }

    /**
     * @return Return true if the captureInformation object
     */
    private boolean createCaptureDocument() {
        captureInformation = new CaptureInformation(assetInformation.getAssetName(), assetInformation.get_id(), assetInformation.getAssetAbbreviation(), new Date(), connObject.getIp());
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
    private void closeConnection() {

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

