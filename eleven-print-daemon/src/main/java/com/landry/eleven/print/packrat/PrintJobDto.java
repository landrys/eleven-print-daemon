package com.landry.eleven.print.packrat;

import java.io.Serializable;
import java.math.BigInteger;

public class PrintJobDto implements Serializable {

	private static final long serialVersionUID = 4522847087410280754L;

	BigInteger id;
	BigInteger printerId;
	String data;

	public BigInteger getId() {
		return id;
	}
	public void setId(BigInteger id) {
		this.id = id;
	}
	public BigInteger getPrinterId() {
		return printerId;
	}
	public void setPrinterId(BigInteger printerId) {
		this.printerId = printerId;
	}
	public String getData() {
		return data;
	}
	public void setData(String data) {
		this.data = data;
	}
	@Override
	public String toString() {
		return "PrintJobDto [id=" + id + ", printerId=" + printerId + ", data=" + data + "]";
	}
}
