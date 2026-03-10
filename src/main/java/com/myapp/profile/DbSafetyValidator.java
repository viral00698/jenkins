package com.myapp.profile;

import java.net.URI;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;

import jakarta.annotation.PostConstruct;

//@Component
public class DbSafetyValidator {

	
	@Value("${spring.datasource.url}")
	private String datasourceUrl;

	@Value("${app.allowed-db-hosts}")
	private List<String> allowedHosts;
 
	 
	private final Environment env;

	public DbSafetyValidator(Environment env) {
		this.env = env;
	}

	@PostConstruct
	public void validateDbHost() throws Exception {
		String profile = env.getActiveProfiles().length > 0 ? env.getActiveProfiles()[0] : "default";

		String host = URI.create(datasourceUrl.replace("jdbc:", "")).getHost();

		if (!allowedHosts.contains(host)) {
			throw new IllegalStateException("🚫 BLOCKED: " + profile + " profile cannot connect to DB host: " + host);
		}

		System.out.println("✅ DB host validated for profile: " + profile);
	}
}
