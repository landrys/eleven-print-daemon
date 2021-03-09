package com.landry.eleven.print.packrat;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;

// NOT USING THIS HTTPINVOKER STUFF
@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class PrintGuyWithHttpClient {

	@Autowired
	PrintJobApi printJobApi;

	Integer forPrinterId;

	public PrintGuyWithHttpClient(Integer forPrinterId) {
		super();
		this.forPrinterId = forPrinterId;
	}

	public void doPrintWork() {
		/*
		if (PrintDaemon.printerId == forPrinterId)
			doWork();
			*/
	}

	private void doWork() {
		/*
		 * 1. Get all jobs for this printer 2. Print to connected printer. 3.
		 * ...
		 */

		List<PrintJobDto> res = printJobApi.findPrintJobsByPrinterId(forPrinterId);
		for (PrintJobDto bean : res)
			System.out.println(res.toString());

	}

}
