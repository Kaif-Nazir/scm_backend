package com.smartcontactmanager.smart_contact_manager_backend;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ServerConnectionController {

    @GetMapping("/start-server")
    public ResponseEntity<?> startServer() {
        return  ResponseEntity.ok().build();
    }


}
