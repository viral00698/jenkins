package com.myapp.AuthController;

import java.util.List;

import lombok.Data;

@Data
public class RegisterRequest {
	private String username;
    private String password;
    private List<String> roles; // e.g. ["USER"]
}
