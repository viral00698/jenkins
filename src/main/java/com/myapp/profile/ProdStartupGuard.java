package com.myapp.profile;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

@Component
@Profile("prod")
public class ProdStartupGuard {

    @Value("${app.prod.confirm:false}")
    private boolean confirmed;

    @PostConstruct
    public void check() {
        if (!confirmed) {
            throw new IllegalStateException(
                "--------------------🚨 PROD startup blocked! Set app.prod.confirm=true----------------------------"
            );
        }
        System.err.println("------------------------------------------------🔥 PROD mode confirmed. Starting safely.--------------------------------");
    }
}