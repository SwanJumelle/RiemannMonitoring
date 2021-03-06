package com.vero.system.monitoring.communication;

import java.io.IOException;

import com.aphyr.riemann.client.RiemannClient;

public class RiemannCommunicator {

	private String address;
	private int port;

	public RiemannCommunicator(String address, int port){
		this.address = address;
		this.port = port;
	}

	public void send(String fieldName, double metricValue, String tag) throws IOException, InterruptedException{
		RiemannClient c = RiemannClient.tcp(address, port);
		c.connect();
		c.event().service(fieldName).state("running").metric(metricValue).tags("appliance", tag).send();
		c.query("tagged \""+tag+"\" and metric > 0"); // => List<Event>;

		c.disconnect();
	}

}
