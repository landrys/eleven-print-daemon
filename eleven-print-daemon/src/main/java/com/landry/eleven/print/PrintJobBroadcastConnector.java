package com.landry.eleven.print;

import java.util.Date;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;

import org.glassfish.jersey.media.sse.EventListener;
import org.glassfish.jersey.media.sse.EventSource;
import org.glassfish.jersey.media.sse.InboundEvent;
import org.glassfish.jersey.media.sse.SseFeature;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import org.springframework.core.env.Environment;

@Component
public class PrintJobBroadcastConnector implements EventListener
{

	Logger logger = LoggerFactory.getLogger(PrintJobBroadcastConnector.class);

	private int statusOutputCounter = 0;

	@Autowired
	PrintDaemon printDaemon;


	@Autowired
	Environment env;

	@Autowired
	ApplicationContext context;

	@Value("${server:/jersey-api/listen/print-job/broadcast}")
	private String server;

	@Value("${username}")
	private String username;

	@Value("${password}")
	private String password;

	@Value("${use.persitent.connection:false}")
	private Boolean usePersistentConnection;

	private EventSource eventSource;

	private Date lastInboundEventTime = new Date();

	public void connect() {

		close();
		logger.info("Trying to connect to the jersey broadcast\n"
				+  printDaemon.getUrlHeader() + server + "...");
//		ClientConfig configuration = new ClientConfig();
//		configuration.property(ClientProperties.CONNECT_TIMEOUT, 10000);
//		configuration.property(ClientProperties.READ_TIMEOUT, 10000);
//		Client client = ClientBuilder.newBuilder().register(SseFeature.class).withConfig(configuration).build();

		Client client = ClientBuilder.newBuilder().register(SseFeature.class).build();
		WebTarget target = client.target(printDaemon.getUrlHeader() + server);
		if (usePersistentConnection) {
			logger.info("Using a persistent connection via eventSource.");
			eventSource = EventSource.target(target).usePersistentConnections().build();
		} else {
			logger.info("Using a default connection via eventSource.");
			eventSource = EventSource.target(target).build();
		}

		eventSource.register(this);
		eventSource.open();
    	lastInboundEventTime = new Date();
		logger.info("Connected to Jersey broadcast\n"
				+  printDaemon.getUrlHeader() + server + ".");

	}

	public void close()
	{
		if (eventSource != null)
			eventSource.close();

	}

	public void status()
	{
		if (eventSource.isOpen())
			if (statusOutputCounter > 36 || statusOutputCounter == 0)
			{
				logger.info("Listening to: " + printDaemon.getUrlHeader() + server + "...");
				statusOutputCounter = 1;
			}
			else
			{
				statusOutputCounter++;
			}
		else
			logger.info("Not connected  to: " + printDaemon.getUrlHeader() + server + ".");
	}

	public boolean isConnected()
	{
		return eventSource.isOpen();
	}

	// TODO put this in its own class EventSourceEventHandler or PrintEventHandler or something like that....
	@Override
	public void onEvent( InboundEvent inboundEvent )
	{
		
		lastInboundEventTime = new Date();
		Integer forPrinterId = inboundEvent.readData(Integer.class);
		logger.info("Got Message to get print jobs for printer: " + forPrinterId);
		logger.info("The printerId for this daemon is: " +  printDaemon.getPrinterId());
		if (printDaemon.getPrinterId() == forPrinterId) {
			String[] profiles = env.getActiveProfiles();
			try {
				if ( profiles[0].equalsIgnoreCase("debug") ) {
					PrintGuyDebug printGuy = context.getBean(PrintGuyDebug.class);
					printGuy.doPrintWork();
					DeleteGuy deleteGuy = context.getBean(DeleteGuy.class);
					deleteGuy.doDeleteWork(printGuy.getPrintJobs());
				} else {
					PrintGuy printGuy = context.getBean(PrintGuy.class);
					printGuy.doPrintWork();
					DeleteGuy deleteGuy = context.getBean(DeleteGuy.class);
					deleteGuy.doDeleteWork(printGuy.getPrintJobs());
				}
			} catch (Exception e) {
				logger.debug("Problem getting beans or doing print work." + e.getMessage());
				e.printStackTrace();
			}
		}
		/*
		try
		{
			Integer forPrinterId = inboundEvent.readData(Integer.class);
			logger.info("Got Message to get print jobs for printer: " + forPrinterId);
			if (printDaemon.getPrinterId() == forPrinterId)
			{
				PrintGuy printGuy = context.getBean(PrintGuy.class);
				printGuy.doPrintWork();
			}
		}
		catch (Exception e)
		{
			if (logger.isDebugEnabled())
				e.printStackTrace();
			logger.info(e.getMessage());
		}
		*/

	}

	public Date getLastInboundEventTime()
	{
		return lastInboundEventTime;
	}
}

		/*
		eventSource = new EventSource(target) {
			@Override
			public void onEvent(InboundEvent inboundEvent) {
				Integer forPrinterId = inboundEvent.readData(Integer.class);
				logger.info("Got Message to get print jobs for printer: " + forPrinterId);
				logger.info("The printerId for this daemon is: " +  printDaemon.getPrinterId());
				if (printDaemon.getPrinterId() == forPrinterId) {
					try {
						PrintGuy printGuy = context.getBean(PrintGuy.class);
						printGuy.doPrintWork();
						DeleteGuy deleteGuy = context.getBean(DeleteGuy.class);
						deleteGuy.doDeleteWork(printGuy.getPrintJobs());
					} catch (Exception e) {
						logger.debug("Problem getting beans or doing print work." + e.getMessage());
						e.printStackTrace();
					}
				}
			}
		};
		*/

	/*
	 * public void connectWithSecurityNotWorkingOnWindows() {
	 * 
	 * System.out.println("Trying to connect to: " + printDaemon.getUrlHeader()
	 * + server + "...");
	 * 
	 * HttpAuthenticationFeature feature =
	 * HttpAuthenticationFeature.basic(username, password); Client client =
	 * ClientBuilder.newBuilder().register(SseFeature.class).build();
	 * client.register(feature); target =
	 * client.target(printDaemon.getUrlHeader() + server);
	 * 
	 * eventSource = new EventSource(target) {
	 * 
	 * @Override public void onEvent(InboundEvent inboundEvent) { try { Integer
	 * forPrinterId = inboundEvent.readData(Integer.class); logger.info(
	 * "Got Message to get print jobs for printer: " + forPrinterId); if
	 * (printDaemon.getPrinterId() == forPrinterId) { PrintGuy printGuy =
	 * context.getBean(PrintGuy.class); printGuy.doPrintWork(); } } catch
	 * (Exception e) { if (logger.isDebugEnabled()) e.printStackTrace();
	 * logger.info(e.getMessage()); } } }; }
	 */

