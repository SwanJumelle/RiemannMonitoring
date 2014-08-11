package com.riemann.system.monitoring;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import parser.YamlParser;
import communication.RiemannCommunicator;
import communication.RiemannJmx;
import usage.JMXData;
import utils.Utils;

import javax.management.JMX;

/**
 * Hello world!
 *
 */
public class App 
{
	private static final String configFilePath = "resources/default/";
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
    //communication between the java process and riemann
		RiemannCommunicator riemannCommunicator = new RiemannCommunicator(riemannHost,riemannPort);
		
		//gather jmx stats
		//final RiemannJmx rJmx = new RiemannJmx();
		//rJmx.gatherStats();

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
