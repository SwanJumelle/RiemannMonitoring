package com.riemann.system.monitoring;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;

import communication.RiemannCommunicator;
import parser.ProcParser;
import usage.CpuData;
import usage.DiskData;
import usage.MemData;
import utils.Utils;

public class DataSender {
	
	private static final int SEND_INTERVAL_TIME = 2000;
	private RiemannCommunicator riemannCommunicator;

	public DataSender(RiemannCommunicator riemannCommunicator){
		this.riemannCommunicator = riemannCommunicator;
	}

	public void sendData() throws IOException, InterruptedException{
		while(true){
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
		}
	}

	public void printData(){
		while(true){
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
		}
	}

}
