package com.landry.eleven.print;

import javax.print.Doc;
import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintException;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.SimpleDoc;
import javax.print.attribute.Attribute;
import javax.print.attribute.PrintServiceAttributeSet;
import javax.print.attribute.standard.PrinterState;
import javax.print.event.PrintServiceAttributeEvent;
import javax.print.event.PrintServiceAttributeListener;

import org.springframework.stereotype.Component;

import com.landry.eleven.print.exception.PrinterNotConnectedException;
import com.landry.eleven.print.exception.PrinterNotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;;

@Component
public class LocalPrinter {

	Logger logger = LoggerFactory.getLogger(LocalPrinter.class);

	private static final String DEFAULT_LABEL_PRINTER = "ZDesigner TLP 2824 Plus (ZPL)";
    public LocalPrinter() {}

    private PrintService printer = null;

	public void connectTo(String printerName) throws PrinterNotFoundException, PrintException, PrinterNotConnectedException {

		if (printerName == null || printerName.isEmpty())
			printerName = DEFAULT_LABEL_PRINTER;

		PrintService[] services = PrintServiceLookup.lookupPrintServices(null, null);
		for (int i = 0; i < services.length; i++) {
			logger.debug(services[i].getName());
			if (services[i].getName().equalsIgnoreCase(printerName)) {
				printer = services[i];
				break;
			}
		}

		if (printer == null) {
			logger.info("Printer was not found.");
			throw new PrinterNotFoundException("Printer: " + printerName + " was not found.");
		} else {
			if ( connected() )
			  setUpPrinter(); 
		}

	}

	private boolean connected() throws PrinterNotConnectedException {
		/* I do not think this is valid...
		PrintServiceAttributeSet attributes = printer.getAttributes();
	    PrinterState printerState = (PrinterState)attributes.get(PrinterState.class);
			if ( printerState != null ) {
				System.out.println("Printer is connected!");
				System.out.println(printerState.toString());
			} else {
				System.out.println("PrinterState is null. Printer is probably not connected.");
				throw new PrinterNotConnectedException("The printer state is null. Printer is not connected.");
			}

*/

		return true;
	}

	private void setUpPrinter() throws PrintException {
		// For now just set up as direct thermal printer.
		String zpl = "^XA^MTD^XZ"; // Direct thermal
		DocPrintJob job = printer.createPrintJob();
		byte[] by = zpl.getBytes();
		DocFlavor flavor = DocFlavor.BYTE_ARRAY.AUTOSENSE;
		Doc doc = new SimpleDoc(by, flavor, null);
		job.print(doc, null);
		if (logger.isDebugEnabled())
			listenForEvents();
	}

	public void print(String data) throws PrintException {
		DocPrintJob job = printer.createPrintJob();
		byte[] by = data.getBytes();
		DocFlavor flavor = DocFlavor.BYTE_ARRAY.AUTOSENSE;
		Doc doc = new SimpleDoc(by, flavor, null);
		job.print(doc, null);
		logger.info("Printed data: " + data );
    }

	private void listenForEvents() {
		printer.addPrintServiceAttributeListener(new PrintServiceListener());
	}

	private class PrintServiceListener implements PrintServiceAttributeListener {
		@Override
		public void attributeUpdate(PrintServiceAttributeEvent psae) {
			PrintServiceAttributeSet attributeSet = psae.getAttributes();
			PrinterState printerState = (PrinterState)attributeSet.get(PrinterState.class);
			if ( printerState != null )
				logger.debug(printerState.toString());
			else
				logger.debug("PrinterState is null. Not sure what this means.");

			Attribute[] attrs = attributeSet.toArray();

			for (int j=0; j<attrs.length; j++) {

				// Get the name of the category of which this attribute value is an instance. 
				String attrName = attrs[j].getName();
			    // get the attribute value
			    String attrValue = attrs[j].toString();
			    logger.debug("Found attribute: " + attrName + " with value: " + attrValue);
			}

		}
		
	}
	

}
