package com.vero.system.monitoring.main;

import com.vero.system.monitoring.communication.RiemannCommunicator;
import com.vero.system.parser.YamlParser;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Logger;

public class Main
{
	private static final String CONFIG_FILE_PATH = "resources/default/";
	private static final String DEFAULT_CONFIG = "default.yaml";
  private static final String DEFAULT_HOST = "10.42.2.1";
  private static final int DEFAULT_PORT = 5555;
  private static final int WAIT_TIME = 5000;

	public static void main( String[] args )
	{
		String riemannHost;
		int riemannPort;
		try {
			YamlParser defaultYaml = new YamlParser(CONFIG_FILE_PATH + DEFAULT_CONFIG);
			riemannHost = defaultYaml.getHost();
			riemannPort = defaultYaml.getPort();
			//need to add interval
		} catch (FileNotFoundException e) {
			riemannHost = DEFAULT_HOST;
			riemannPort = DEFAULT_PORT;
		}
    //communication between the java process and riemann
		RiemannCommunicator riemannCommunicator = new RiemannCommunicator(riemannHost,riemannPort);

		DataSender dataSender = new DataSender(riemannCommunicator);
		while(true){
			//dataSender.printData();
			try {
				dataSender.sendData();
			} catch (IOException | InterruptedException e) {
				e.printStackTrace();
        try {
          Thread.sleep(WAIT_TIME);
        } catch (InterruptedException e1) {
          Logger.getLogger(Main.class.getName()).severe("Failed to wait " + WAIT_TIME + "ms.");
        }
      }
		}
	}
}
