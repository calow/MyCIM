package com.example.cim.exception;

import java.io.Serializable;

public class NetWorkDisableException extends Exception implements Serializable{

	private static final long serialVersionUID = 1L;

	public NetWorkDisableException() {
		super();
	}

	public NetWorkDisableException(String detailMessage) {
		super(detailMessage);
	}

}
