package com.riemann.system.monitoring;

import java.io.IOException;

import com.aphyr.riemann.client.RiemannClient;

/**
 * Hello world!
 *
 */
public class App 
{
	public static void main( String[] args ) throws IOException
	{
		RiemannClient c = RiemannClient.tcp("10.42.2.4", 5555);
		c.connect();
		c.event().service("fridge").state("running").metric(5.3).tags("appliance", "cold").send();

		c.query("tagged \"cold\" and metric > 0"); // => List<Event>;
		c.disconnect();
	}
}
