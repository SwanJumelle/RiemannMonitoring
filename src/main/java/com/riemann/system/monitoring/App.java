package com.riemann.system.monitoring;

import java.io.IOException;
import java.util.ArrayList;

import parser.ProcParser;
import usage.CpuData;
import usage.UsageType;
import utils.Utils;

import com.aphyr.riemann.client.RiemannClient;
import communication.RiemannCommunicator;

/**
 * Hello world!
 *
 */
public class App 
{
	public static void main( String[] args ) throws IOException, InterruptedException
	{
		/*RiemannClient c = RiemannClient.tcp("10.42.2.4", 5555);
		c.connect();
		for(int i=0;i<20;i++){
			c.event().service("fridge").state("running").metric(5.3).tags("appliance", "cold").send();

			c.query("tagged \"cold\" and metric > 0"); // => List<Event>;
			Thread.sleep(1000);
		}
		c.disconnect();*/
		RiemannCommunicator riemannCommunicator = new RiemannCommunicator("10.42.2.4");
		while(true){
			ProcParser procP = new ProcParser(Utils.getPid());
			ArrayList<CpuData> cpuDataList = procP.gatherCpuUsage();
			for(CpuData cpuData : cpuDataList){
				System.out.println(cpuData);
				riemannCommunicator.send("cpu", cpuData.getIdle());
			}
			Thread.sleep(1000);
		}
	}
}
