package com.riemann.system.monitoring;

import java.io.IOException;
import org.apache.commons.cli.Options;
import communication.RiemannCommunicator;
import communication.RiemannJmx;
/**
 * Hello world!
 *
 */
public class App 
{
	private static Options options = null;

    static
    {
        options = new Options();

        options.addOption("riemann_host", true, "hostname for riemann server");
        options.addOption("riemann_port", true, "port number for riemann server");
        options.addOption("interval_seconds", true, "number of seconds between updates");
    }

	public static void main( String[] args )
	{
		//processus of rieamann-jmx
		final Process rJmxJar = null;
		//communication between the java process and riemann
		RiemannCommunicator riemannCommunicator = new RiemannCommunicator("10.42.2.6",5555);

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
