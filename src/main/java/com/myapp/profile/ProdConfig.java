package com.myapp.profile;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

@Profile("prod")
@Component
public class ProdConfig {

	@PostConstruct
	public void init() {
		System.err.println("-----------------------------------------------------------PROD configuration loaded--------------------------------------------------");
	}

}
