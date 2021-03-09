package com.landry.eleven.print;

import org.springframework.stereotype.Component;

@Component
public class LocalPrinterStatusTracker {
	
	String runningStatus = LocalPrinterStatus.READY;

	public LocalPrinterStatusTracker( ){}
	
	public void updateStatus( String status )
	{
		switch( status)
		{
			case LocalPrinterStatus.READY:
				runningStatus = LocalPrinterStatus.READY;
				break;
				
			case LocalPrinterStatus.NOT_CONNECTED:
				runningStatus = LocalPrinterStatus.NOT_CONNECTED;
				break;
				
			case LocalPrinterStatus.NOT_FOUND:
				runningStatus = LocalPrinterStatus.NOT_FOUND;
				
				break;
			case LocalPrinterStatus.OTHER:
				runningStatus = LocalPrinterStatus.OTHER;
				break;
				
			default:
				break;
		}
	}
	
	public String getLocalPrinterStatus( )
	{
		return runningStatus;
	}
}
