package com.example.samples.controller;

import com.example.samples.service.AmqpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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