package com.landry.eleven.print.exception;

@SuppressWarnings("serial")
public class TooManyPrintJobsException extends Exception {
	public TooManyPrintJobsException( String message) {
		super(message);
	}
}
