package com.landry.eleven.print.dto;

import java.math.BigInteger;


public class Printer {

	BigInteger id;
	String name;

	public BigInteger getId() {
		return id;
	}
	public void setId(BigInteger id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
}
