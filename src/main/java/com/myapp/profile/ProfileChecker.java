package com.myapp.profile;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

import jakarta.annotation.PostConstruct;

public class ProfileChecker {

	@Autowired
	private Environment environment;

	@PostConstruct
	public void logActiveProfile() {
		System.out.println("Active profiles: " + Arrays.toString(environment.getActiveProfiles()));
	}
}
