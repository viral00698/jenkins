package com.myapp.Response;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

	 	private boolean success;
	    private int status;
	    private String message;
	    private T data;
	    private Object errors;
	    
	    @Builder.Default
	    private LocalDateTime timestamp = LocalDateTime.now(ZoneOffset.UTC);
}
