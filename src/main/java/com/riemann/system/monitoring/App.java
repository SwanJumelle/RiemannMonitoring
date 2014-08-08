package com.riemann.system.monitoring;

import java.io.IOException;
import java.util.ArrayList;

import communication.RiemannCommunicator;
import communication.RiemannJmx;

/**
 * Hello world!
 *
 */
public class App 
{

	public static void main( String[] args )
	{
		String riemannHost = "10.42.2.6";
		int riemannPort = 5555;
		//communication between the java process and riemann
		RiemannCommunicator riemannCommunicator = new RiemannCommunicator(riemannHost,riemannPort);
		//gather jmx stats
		final RiemannJmx rJmx = new RiemannJmx();
		rJmx.gatherStats();

		DataSender dataSender = new DataSender(riemannCommunicator);
		
		while(true){
			//dataSender.printData();
			try {
				dataSender.sendData();
			} catch (IOException | InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
