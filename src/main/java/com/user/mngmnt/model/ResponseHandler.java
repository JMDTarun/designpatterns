package com.user.mngmnt.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ResponseHandler {
	private int errorCode;
	private String errorCause;
	private String statusCode;
	private String message;
	private Object anyReason;
}
