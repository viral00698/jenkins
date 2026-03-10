package com.myapp.Test;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.myapp.Response.ApiResponse;

@RestController
@RequestMapping("api")
public class Testing {

	
	@GetMapping("test")
	public ResponseEntity<Object> test(){
		
		
		 ApiResponse<Object> rsp = ApiResponse.builder()
		            .success(true) // success because it's a test success response
		            .data("Testing")
		            .message("Testinggg")
		            .status(HttpStatus.CREATED.value())
		            .build();

		    return ResponseEntity.status(HttpStatus.CREATED).body(rsp);
	}
}
