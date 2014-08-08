package com.riemann.system.monitoring;

import java.io.IOException;

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
		String riemannHost = args[0];
		int riemannPort = Integer.parseInt(args[1]);
		//processus of rieamann-jmx
		final Process rJmxJar = null;
		//communication between the java process and riemann
		RiemannCommunicator riemannCommunicator = new RiemannCommunicator(riemannHost,riemannPort);

		final RiemannJmx rJmx = new RiemannJmx(rJmxJar);

		try {
			rJmx.gatherStats(args);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		//hook on kill, to also kill riemann-jmx
		Runtime.getRuntime().addShutdownHook(new Thread()
        {
            @Override
            public void run()
            {
                rJmx.destroy();
            }
        });

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
