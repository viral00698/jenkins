package com.myapp.Test;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("legion")
public class TestController {
	
	
	@GetMapping("test")
	public String test() {
		return "Hello I`m Working";
	}

	@GetMapping("getAll")
	public String getAll() {
		return "Hello I`m Working With Jenkins";
	}
}
