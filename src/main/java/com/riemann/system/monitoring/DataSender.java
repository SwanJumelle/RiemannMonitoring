package com.riemann.system.monitoring;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import parser.ProcParser;
import usage.CpuData;
import usage.DiskData;
import usage.JMXData;
import usage.MemData;
import utils.Utils;

import communication.RiemannCommunicator;

public class DataSender {

	private static final int SEND_INTERVAL_TIME = 2000;

	private static final String DEFAULT_DATA_FILES = "resources/data/";

	private RiemannCommunicator riemannCommunicator;

	public DataSender(RiemannCommunicator riemannCommunicator){
		this.riemannCommunicator = riemannCommunicator;
	}

	public void sendData() throws IOException, InterruptedException{
		ProcParser procP = new ProcParser(Utils.getPid());
		ArrayList<CpuData> cpuDataList = new ArrayList<CpuData>();
		MemData memData = procP.gatherMemoryUsage(Utils.getPid());
		ArrayList<ArrayList<DiskData>> diskDataList = new ArrayList<ArrayList<DiskData>>();
		for(int i=0;i<2;i++){
			cpuDataList.add(procP.gatherCpuUsage());
			diskDataList.add(procP.gatherDiskUsage());
			try{
				if(i%2 == 0)
					Thread.sleep(SEND_INTERVAL_TIME);
			}catch(InterruptedException e){

			}
		}

		List<JMXData> jmxDataList = Utils.getJmxDataList(DEFAULT_DATA_FILES);

		riemannCommunicator.send("cpu_total_usage", Utils.totalCpuUsage(cpuDataList.get(0), cpuDataList.get(1)),"cpuUsage");
		riemannCommunicator.send("mem_free", (memData.getMemFree()*100.0f)/memData.getMemTotal(),"mem_free");
		riemannCommunicator.send("mem_buffers", (memData.getMemBuffers()*100.0f)/memData.getMemTotal(),"mem_buffers");
		riemannCommunicator.send("mem_used", (memData.getMemUsed()*100.0f)/memData.getMemTotal(),"mem_used");
		HashMap<String,ArrayList<Double>> diskUsage = Utils.getDiskStats(diskDataList.get(0),diskDataList.get(1));
		Iterator<String> it = diskUsage.keySet().iterator();
		while(it.hasNext()) {
			String partitionName = it.next();
			riemannCommunicator.send("disk_write_" + partitionName, diskUsage.get(partitionName).get(0), "disk_write_" + partitionName);
			riemannCommunicator.send("disk_read_" + partitionName, diskUsage.get(partitionName).get(1), "disk_read_" + partitionName);
		}
		riemannCommunicator.send("loadavg", procP.gatherLoadAvg().getOneMinuteAvg(), "loadavg");

		for (JMXData jmxData : jmxDataList) {
      Iterator iterator = jmxData.getValues().values().iterator();
      while (iterator.hasNext()) {
        Object value = iterator.next();
        riemannCommunicator.send(jmxData.getName(), (Double) value, jmxData.getTagsAsString());
      }
		}
	}

	public void printData(){
		Timestamp currentTimestamp = new java.sql.Timestamp(Calendar.getInstance().getTime().getTime());
		ProcParser procP = new ProcParser(Utils.getPid());
		ArrayList<CpuData> cpuDataList = new ArrayList<CpuData>();
		MemData memData = procP.gatherMemoryUsage(Utils.getPid());
		ArrayList<ArrayList<DiskData>> diskDataList = new ArrayList<ArrayList<DiskData>>();
		for(int i=0;i<2;i++){
			cpuDataList.add(procP.gatherCpuUsage());
			diskDataList.add(procP.gatherDiskUsage());
			try{
				if(i%2 == 0)
					Thread.sleep(SEND_INTERVAL_TIME);
			}catch(InterruptedException e){

			}
		}
		System.out.println("***********************************\n     " + currentTimestamp + "\n***********************************");
		System.out.println("cpu_total_usage " + Utils.totalCpuUsage(cpuDataList.get(0), cpuDataList.get(1)));
		System.out.println("mem_free " + (memData.getMemFree()*100.0f)/memData.getMemTotal());
		System.out.println("mem_buffers " +(memData.getMemBuffers()*100.0f)/memData.getMemTotal());
		System.out.println("mem_used " + (memData.getMemUsed()*100.0f)/memData.getMemTotal());
		HashMap<String,ArrayList<Double>> diskUsage = Utils.getDiskStats(diskDataList.get(0),diskDataList.get(1));
		Iterator<String> it = diskUsage.keySet().iterator();
		while(it.hasNext()) {
			String partitionName = it.next();
			System.out.println("disk_write_" + partitionName + " " + diskUsage.get(partitionName).get(0));
			System.out.println("disk_read_" + partitionName + " " + diskUsage.get(partitionName).get(1));
		}
		System.out.println("loadavg " + procP.gatherLoadAvg().getOneMinuteAvg());

    List<JMXData> jmxDataList = Utils.getJmxDataList(DEFAULT_DATA_FILES);

    for (JMXData jmxData : jmxDataList) {
      Iterator iterator = jmxData.getValues().values().iterator();
      while (iterator.hasNext()) {
        Object value = iterator.next();
        System.out.println("Name: " + jmxData.getName() + "Value: " + (Double) value);
      }
    }
	}
}
