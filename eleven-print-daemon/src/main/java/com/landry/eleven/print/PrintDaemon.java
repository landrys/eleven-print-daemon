package com.landry.eleven.print;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import org.springframework.core.env.Environment;

@Component
public class PrintDaemon {

	Logger logger = LoggerFactory.getLogger(PrintDaemon.class);

	boolean firstTimeCalled = true;

	@Autowired
	Environment env;

	@Value("${nap.time:5000}")
	int napTime = 5000;

	@Value("${use.broadcast:false}")
	boolean useBroadcast;

//	@Value("${url.header:http://11nator.com:8011}")
	@Value("${url.header:http://localhost:8010}")
	String urlHeader;

	@Value("${allowed.inactivity.time.minutes:60}")
	Long allowedInactivityInMinutes;
	
	@Value("${label.printer.id:1}")
	private Integer labelPrinterId;

	static final int MAX_NUMBER_CONNECT_RETRYS = 10000;

	@Autowired
	PrintJobBroadcastConnector broadcastConnector;
	
	@Autowired
	ApplicationContext appContext;

	private int numOfTrys = 0;
//	private int numOfCalls = 0;

	private boolean keepRunning = true;

	private long ALLOWED_INACTIVITY;

	public void start() {
		if ( useBroadcast ) {
			ALLOWED_INACTIVITY = 1000l * 60l * allowedInactivityInMinutes; 
			logger.info(
					"Setting allowed inactivity time before we re-connect to: " + allowedInactivityInMinutes + " minutes.");
			broadcastConnector.connect();
			logger.info("The printer id for this daemon is: " + labelPrinterId);
			stayAwakeAndListen();
		} else {
			keepCheckingForPrintJobs();
		}
	}

	public void keepCheckingForPrintJobs() {

		while (keepRunning )
		{
			checkForPrintJobs();
			takeANap();
		}

	}


	public void stayAwakeAndListen() {

		while (keepRunning )
		{
			if (!broadcastConnector.isConnected())
				reconnect("Connection is down!. Trying to reconnect....");
			else if (hourOfInactivity())
				reconnect("Print Daemon has been sedentary for more than an hour. Reconnecting...");
			else
				broadcastConnector.status();

			takeANapYouHaveBeenWorkingTooHard();
		}

		logger.info("Closing connection to the broadcast.");
		broadcastConnector.close();

	}

	private void reconnect( String message )
	{
		try
		{
			logger.info(message);
			if (numOfTrys < MAX_NUMBER_CONNECT_RETRYS)
				broadcastConnector.connect();
			else
				keepRunning = false;
			numOfTrys = 0;
		}
		catch (Exception e)
		{
			numOfTrys++;
			e.printStackTrace();
			logger.info(e.getMessage());
		}

	}

	private boolean hourOfInactivity()
	{
		Date now = new Date();
		Long expiredTime = (now.getTime() - broadcastConnector.getLastInboundEventTime().getTime());
		if (expiredTime > ALLOWED_INACTIVITY) {
	    	return true;
		} else {
		    return false;
		}
	}

	private void takeANapYouHaveBeenWorkingTooHard() {
		try {
			if (firstTimeCalled)
				checkForAnyPrintJobsWhileWeWereDown();
			logger.debug("Daemon for printer id: " + labelPrinterId
					+ " is sleeping a bit before checking the connection to jersey broadcast...");
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	private void takeANap() {
		//numOfCalls++;
		try {
			Thread.sleep(napTime);
		//	if ( numOfCalls > 10 )
		//		keepRunning = false;
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}


	private void checkForPrintJobs() {
		try {
			logger.debug("Checking for print jobs...");
			String[] profiles = env.getActiveProfiles();
			
			if ( profiles[0].equalsIgnoreCase("debug") )
			{
				PrintGuyDebug printGuy = appContext.getBean(PrintGuyDebug.class);
				printGuy.doPrintWork();
				DeleteGuy deleteGuy = appContext.getBean(DeleteGuy.class);
				deleteGuy.doDeleteWork(printGuy.getPrintJobs());

			}else {
				PrintGuy printGuy = appContext.getBean(PrintGuy.class);
				printGuy.doPrintWork();
				DeleteGuy deleteGuy = appContext.getBean(DeleteGuy.class);
				deleteGuy.doDeleteWork(printGuy.getPrintJobs());
			}


		} catch (Exception e) {
			if (logger.isDebugEnabled())
				e.printStackTrace();
			logger.info(e.getMessage());
		}
	}


	private void checkForAnyPrintJobsWhileWeWereDown() {
		firstTimeCalled = false;
		checkForPrintJobs();
	}

	public Integer getPrinterId() {
		return labelPrinterId;
	}

	public String getUrlHeader() {
		return urlHeader;
	}

	public void clean() {
		logger.info("Closing connection to the broadcast.");
		broadcastConnector.close();
		
	}
}
