package com.myapp.profile;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

@Component
@Profile("prod")
public class ProdBanner {
	
    @PostConstruct
    public void warn() {
        System.out.println(
            "🚨🚨🚨 RUNNING IN PRODUCTION MODE 🚨🚨🚨"
        );
    }
}
