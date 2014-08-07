package com.riemann.system.monitoring;

import java.io.IOException;

import communication.RiemannCommunicator;

/**
 * Hello world!
 *
 */
public class App 
{

	public static void main( String[] args )
	{

		RiemannCommunicator riemannCommunicator = new RiemannCommunicator("10.42.2.6");
		DataSender dataSender = new DataSender(riemannCommunicator);
		dataSender.printData();
		
		try {
			dataSender.sendData();
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
		
	}
}
