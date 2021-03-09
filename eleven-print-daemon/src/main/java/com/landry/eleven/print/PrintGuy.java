package com.landry.eleven.print;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanDefinition;

import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.landry.eleven.print.dto.PrintJob;
import com.landry.eleven.print.exception.TooManyPrintJobsException;

@Profile("prod")
@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class PrintGuy {

	Logger logger = LoggerFactory.getLogger(PrintGuy.class);

	@Value("${get.print.jobs.uri:/api/print-jobs}")
	private String uri;

	@Value("${max.number.print.jobs:20}")
	private Integer maxNumberOfPrintJobs;

	private PrintJob[] printJobs;

	@Autowired
	RestCaller restCaller;

	@Autowired
	LocalPrinterManager localPrinterManager;

	@Autowired
	PrintDaemon printDaemon;

	public PrintGuy() {
		super();
	}

	public void doPrintWork() throws TooManyPrintJobsException {

		logger.debug("Getting print jobs via REST call:" 
				+ printDaemon.getUrlHeader() + uri + "?" + printDaemon.getPrinterId() );

		printJobs = (PrintJob[]) restCaller.getList(
				printDaemon.getUrlHeader() + uri + "?" + "printerId=" + printDaemon.getPrinterId(), PrintJob[].class);

		logger.debug("Got " + printJobs.length + " to process.");
		if ( printJobs.length > maxNumberOfPrintJobs )
			throw new TooManyPrintJobsException("Got " + printJobs.length 
					+ " labels to print which is over " 
					+ "the max. Please re-launch the daemon and " 
					+ "set max.number.print.jobs to higher value if "
					+ "this is not a bug or mistake.");
		/*
			for (PrintJob pj : printJobs) {
				Integer qty = pj.getQuantity();
				if (qty == null || qty == 1)
					System.out.println(pj.getData());
				else
					for (int i = 0; i < qty; i++)
						System.out.println(pj.getData());
				pj.setDone(true);
			}
			*/

			for (PrintJob pj : printJobs) {
				Integer qty = pj.getQuantity();
				if (qty == null || qty == 1)
					localPrinterManager.printToLocallyConnectedLabelPrinter(pj.getData());
				else
					for (int i = 0; i < qty; i++)
						localPrinterManager.printToLocallyConnectedLabelPrinter(pj.getData());
				pj.setDone(true);
			}

	}

	public PrintJob[] getPrintJobs() {
		return printJobs;
	}

}
