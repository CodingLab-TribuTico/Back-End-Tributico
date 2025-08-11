package com.project.demo.rest.webSocket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Controller
public class WebSocketController {


    @MessageMapping("/test")
    @SendTo("/topic/notifications")
    public String handleTestMessage(String message) {
        System.out.println("Mensaje recibido del cliente: " + message);
        return "Mensaje de vuelta: " + message;
    }
}
