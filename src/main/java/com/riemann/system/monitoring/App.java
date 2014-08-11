package com.riemann.system.monitoring;

import java.io.FileNotFoundException;
import parser.YamlParser;
import communication.RiemannCommunicator;
import communication.RiemannJmx;

/**
 * Hello world!
 *
 */
public class App 
{
	private static final String configFilePath = "src/main/jmx_config/";
	private static final String defaultConfig = "defaultConfig.yaml";

	public static void main( String[] args )
	{
		String riemannHost;
		int riemannPort;
		YamlParser defaultYaml = null;
		try {
			defaultYaml = new YamlParser(configFilePath+defaultConfig);
			riemannHost = defaultYaml.getHost();
			riemannPort = defaultYaml.getPort();
			//need to add interval
		} catch (FileNotFoundException e) {
			riemannHost = "10.42.2.6";
			riemannPort = 5555;
		}
    System.out.println("COUCOU");
    //communication between the java process and riemann
		RiemannCommunicator riemannCommunicator = new RiemannCommunicator(riemannHost,riemannPort);
		
		//gather jmx stats
		//final RiemannJmx rJmx = new RiemannJmx();
		//rJmx.gatherStats();

		DataSender dataSender = new DataSender(riemannCommunicator);

    dataSender.hackJMX();
		
		/*while(true){
			//dataSender.printData();
			try {
				dataSender.sendData();
			} catch (IOException | InterruptedException e) {
				e.printStackTrace();
			}
		}*/
	}
}
