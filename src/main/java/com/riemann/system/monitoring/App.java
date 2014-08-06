package com.riemann.system.monitoring;

import java.io.IOException;
import java.util.ArrayList;

import parser.ProcParser;
import usage.CpuData;
import usage.MemData;
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
	
	public static double totalUsage(CpuData c1, CpuData c2){
		double usage = (c2.getUser()-c1.getUser())+(c2.getNice()-c1.getNice())+(c2.getSysmode()-c1.getSysmode());
		double total = usage + (c2.getIdle()-c1.getIdle());
		return (100*usage)/total;
	}
	
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
			ArrayList<CpuData> cpuDataList = new ArrayList<CpuData>();
			MemData memData = procP.gatherMemoryUsage(Utils.getPid());
			for(int i=0;i<2;i++){
				cpuDataList.add(procP.gatherCpuUsage());
				Thread.sleep(2000);
			}
			System.out.println(totalUsage(cpuDataList.get(0), cpuDataList.get(1)));
			riemannCommunicator.send("cpu_total_usage", totalUsage(cpuDataList.get(0), cpuDataList.get(1)),"cpuUsage");
			riemannCommunicator.send("mem_free", (memData.getMemFree()*100.0f)/memData.getMemTotal(),"mem_free");
			riemannCommunicator.send("mem_buffers", (memData.getMemBuffers()*100.0f)/memData.getMemTotal(),"mem_buffers");
			riemannCommunicator.send("mem_used", (memData.getMemUsed()*100.0f)/memData.getMemTotal(),"mem_used");
			
		}
	}
}
