package com.cb.plcreader.controllers;


import com.cb.plcreader.services.SocketClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ReadController {

    @Autowired
    private SocketClient socketClient;

    @GetMapping
    @RequestMapping("/read")
    public void readValue() {
        socketClient.readData();
    }
}
