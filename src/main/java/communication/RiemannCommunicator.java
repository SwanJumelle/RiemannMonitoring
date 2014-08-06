package communication;

import java.io.IOException;

import com.aphyr.riemann.client.RiemannClient;

public class RiemannCommunicator {

	private String address;

	public RiemannCommunicator(String address){
		this.address = address;
	}

	public void send(String fieldName, double metricValue, String tag) throws IOException, InterruptedException{
		RiemannClient c = RiemannClient.tcp(address, 5555);
		c.connect();
		c.event().service(fieldName).state("running").metric(metricValue).tags("appliance", tag).send();
		c.query("tagged \""+tag+"\" and metric > 0"); // => List<Event>;

		c.disconnect();
	}

}
