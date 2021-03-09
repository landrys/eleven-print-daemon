package com.landry.eleven.print;

import javax.annotation.PostConstruct;

import javax.print.PrintException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.landry.eleven.print.dto.Printer;
import com.landry.eleven.print.exception.PrinterNotConnectedException;
import com.landry.eleven.print.exception.PrinterNotFoundException;

@Profile("prod")
@Component
public class LocalPrinterManager {
	
	Logger logger = LoggerFactory.getLogger(LocalPrinterManager.class);
    
	// On MyMAC Zebra_Technologies_ZTC_TLP_2824_Plus

	@Value("${get.printer.uri:api/printers}")
	private String uri;

	String labelPrinterName;
	/* do not do this
	@Value("${label.printer.name:ZDesigner TLP 2824 Plus (ZPL)}")
	String labelPrinterName;
	*/

	@Autowired
	PrintDaemon printDaemon;

	@Autowired
	RestCaller restCaller;

	@Autowired
	LocalPrinter localPrinter;

    @Autowired
    LocalPrinterStatusTracker localPrinterStatusTracker;

    @PostConstruct
    private void initPrinter() {
    	tryToConnect(null);
    }

	private void tryToConnect( String data ) {
		try {
		    Printer printer = (Printer)restCaller.get(printDaemon.getUrlHeader() + uri + "/" + printDaemon.getPrinterId(), Printer.class);
		    labelPrinterName = printer.getName();
			localPrinter.connectTo(labelPrinterName);
			if (data != null )
				localPrinter.print(data);
		} catch (PrinterNotFoundException e) {
			localPrinterStatusTracker.updateStatus(LocalPrinterStatus.NOT_FOUND);
			logger.info(e.getMessage());
			e.printStackTrace();
		} catch (PrinterNotConnectedException e) {
			localPrinterStatusTracker.updateStatus(LocalPrinterStatus.NOT_CONNECTED);
			logger.info(e.getMessage());
			e.printStackTrace();
		} catch (PrintException e) {
			localPrinterStatusTracker.updateStatus(LocalPrinterStatus.OTHER);
			logger.info(e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			localPrinterStatusTracker.updateStatus(LocalPrinterStatus.OTHER);
			logger.info(e.getMessage());
			e.printStackTrace();
		}
		reportStatus();
	}

	private void reportStatus() {
		logger.info("\nPrinter " 
             	+ labelPrinterName + " is in status: "
				+ localPrinterStatusTracker.getLocalPrinterStatus());
	}

	public void printToLocallyConnectedLabelPrinter(String data) {

		if (localPrinterStatusTracker.getLocalPrinterStatus().equals(LocalPrinterStatus.READY))
			try {
				localPrinter.print(data);
			} catch (PrintException e) {
     			System.out.println(e.getMessage());
				e.printStackTrace();
				tryToConnect(data);
			}
		else
			tryToConnect(data);
	}
}
