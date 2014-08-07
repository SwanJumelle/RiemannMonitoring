package com.riemann.system.monitoring;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import parser.ProcParser;
import usage.CpuData;
import usage.DiskData;
import usage.MemData;
import utils.Utils;

import communication.RiemannCommunicator;

/**
 * Hello world!
 *
 */
public class App 
{
	
	public static double totalCpuUsage(CpuData c1, CpuData c2){
		double usage = (c2.getUser()-c1.getUser())+(c2.getNice()-c1.getNice())+(c2.getSysmode()-c1.getSysmode());
		double total = usage + (c2.getIdle()-c1.getIdle());
		return (100*usage)/total;
	}
	
	private static HashMap<String,ArrayList<Double>> getDiskStats(ArrayList<DiskData> dList1, ArrayList<DiskData> dList2) {
		int size = dList1.size();
		HashMap<String,ArrayList<Double>> map = new HashMap<String, ArrayList<Double>>();
		for(int i = 0; i<size;i++){
			double write = dList2.get(i).getWritesCompleted()-dList1.get(i).getWritesCompleted();
			double read = dList2.get(i).getReadsCompleted()-dList1.get(i).getReadsCompleted();
			ArrayList<Double> rw = new ArrayList<Double>();
			rw.add(write);
			rw.add(read);
			map.put(dList1.get(i).getName(), rw);
		}
			
		return map;
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
		RiemannCommunicator riemannCommunicator = new RiemannCommunicator("10.42.2.6");
		while(true){
			ProcParser procP = new ProcParser(Utils.getPid());
			ArrayList<CpuData> cpuDataList = new ArrayList<CpuData>();
			MemData memData = procP.gatherMemoryUsage(Utils.getPid());
			ArrayList<ArrayList<DiskData>> diskDataList = new ArrayList<ArrayList<DiskData>>();
			for(int i=0;i<2;i++){
				cpuDataList.add(procP.gatherCpuUsage());
				diskDataList.add(procP.gatherDiskUsage());
				Thread.sleep(500);
			}
			riemannCommunicator.send("cpu_total_usage", totalCpuUsage(cpuDataList.get(0), cpuDataList.get(1)),"cpuUsage");
			riemannCommunicator.send("mem_free", (memData.getMemFree()*100.0f)/memData.getMemTotal(),"mem_free");
			riemannCommunicator.send("mem_buffers", (memData.getMemBuffers()*100.0f)/memData.getMemTotal(),"mem_buffers");
			riemannCommunicator.send("mem_used", (memData.getMemUsed()*100.0f)/memData.getMemTotal(),"mem_used");
			HashMap<String,ArrayList<Double>> diskUsage = getDiskStats(diskDataList.get(0),diskDataList.get(1));
			Iterator<String> it = diskUsage.keySet().iterator();
			while(it.hasNext()) {
				String partitionName = it.next();
				riemannCommunicator.send("disk_write_" + partitionName, diskUsage.get(partitionName).get(0), "disk_write_" + partitionName);
				riemannCommunicator.send("disk_read_" + partitionName, diskUsage.get(partitionName).get(1), "disk_read_" + partitionName);
			}
			riemannCommunicator.send("loadavg", procP.gatherLoadAvg().getOneMinuteAvg(), "loadavg");
		}
	}
}
