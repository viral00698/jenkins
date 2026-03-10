package com.myapp.profile;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

@Profile("dev")
@Component
public class DevConfig {
    @PostConstruct
    public void init() {
        System.err.println("------------------------------------------------------------------DEV configuration loaded---------------------------------------------");
    }
}

