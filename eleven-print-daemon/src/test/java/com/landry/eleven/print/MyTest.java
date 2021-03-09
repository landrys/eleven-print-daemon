import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.test.context.ActiveProfiles;

import com.landry.eleven.print.PrintDaemon;
import com.landry.eleven.print.PrintJobBroadcastConnector;

@SpringBootTest(properties = {"label.printer.id=1", "url.header=http://testnator.com:8010"}, classes = {PrintJobBroadcastConnector.class, PrintDaemon.class})
@ActiveProfiles("test") // This wili prevent the print jobs from being deleted
class MyTest {

	@Autowired
	PrintJobBroadcastConnector broadcastConnector;

	@Autowired
	PrintDaemon printDaemon;

	@Test
	void testBroadcastConnector() throws Exception{
		broadcastConnector.connect();
		System.out.println(String.valueOf(broadcastConnector.isConnected()));
		broadcastConnector.close();
		Thread.sleep(2000);
		System.out.println(String.valueOf(broadcastConnector.isConnected()));
	}

	@Test
	void testPrintDaemon() throws Exception{
		printDaemon.start();
	}

}
