package com.soulcurator.backend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {
    
    @GetMapping("/api/hello")
    public String hello() {
        return "{\"message\": \"Hello from SoulCurator! 🎭\"}";
    }
    
    @GetMapping("/api/health")
    public String health() {
        return "{\"status\": \"healthy\", \"service\": \"soul-curator-backend\"}";
    }
}