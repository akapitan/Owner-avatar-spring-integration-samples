package com.example.basic.amqp.controller;

import com.example.basic.amqp.service.AmqpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/amqp")
public class AmqpController {

    private final AmqpService amqpService;

    @Autowired
    public AmqpController(AmqpService amqpService) {
        this.amqpService = amqpService;
    }

    @PostMapping("/send")
    public ResponseEntity<String> sendMessage(@RequestBody String message) {
        boolean sent = amqpService.sendMessage(message);
        if (sent) {
            return ResponseEntity.ok("Message sent successfully");
        } else {
            return ResponseEntity.badRequest().body("Failed to send message");
        }
    }
}