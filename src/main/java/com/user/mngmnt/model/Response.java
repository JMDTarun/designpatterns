package com.user.mngmnt.model;

import lombok.Data;

@Data
public class Response {

	private int errorCode;
	private String errorCause;
	private String statusCode;
}
