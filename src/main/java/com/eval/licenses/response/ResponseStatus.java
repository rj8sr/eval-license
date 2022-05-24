package com.eval.licenses.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(value = Include.NON_NULL)
public class ResponseStatus {
	
	private  int statusCode;
	
	public ResponseStatus(int statusCode) {
	this.statusCode=statusCode;
	}
	

	public int getStatusCode() {
		return statusCode;
	}

	

}