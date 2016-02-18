package com.example.cim.exception;

import java.io.Serializable;

public class CIMSessionDisableException extends Exception implements Serializable{

	private static final long serialVersionUID = 1L;

	public CIMSessionDisableException() {
		super();
	}

	public CIMSessionDisableException(String detailMessage) {
		super(detailMessage);
	}

	
}
