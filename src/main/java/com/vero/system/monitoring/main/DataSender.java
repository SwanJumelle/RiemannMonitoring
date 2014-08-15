package com.vero.system.monitoring.main;

import com.vero.system.monitoring.communication.RiemannCommunicator;
import com.vero.system.parser.ProcParser;
import com.vero.system.usage.CpuData;
import com.vero.system.usage.DiskData;
import com.vero.system.usage.JMXData;
import com.vero.system.usage.MemData;
import com.vero.system.utils.Utils;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.*;
import java.util.logging.Logger;

public class DataSender {

	private static final int SEND_INTERVAL_TIME = 500;

	private static final String DEFAULT_DATA_FILES = "resources/data/";

	private RiemannCommunicator riemannCommunicator;

	public DataSender(RiemannCommunicator riemannCommunicator){
		this.riemannCommunicator = riemannCommunicator;
	}

	public void sendData() throws IOException, InterruptedException{
		ProcParser procP = new ProcParser();
		ArrayList<CpuData> cpuDataList = new ArrayList<>();
		MemData memData = procP.gatherMemoryUsage();
		ArrayList<ArrayList<DiskData>> diskDataList = new ArrayList<>();

		for(int i = 0; i < 2; i++){
			cpuDataList.add(procP.gatherCpuUsage());
			diskDataList.add(procP.gatherDiskUsage());
			try{
				if(i % 2 == 0)
					Thread.sleep(SEND_INTERVAL_TIME);
			}catch(InterruptedException e){
        Logger.getLogger(DataSender.class.getName()).severe("Failed to wait " + SEND_INTERVAL_TIME + "ms.");
			}
		}

		List<JMXData> jmxDataList = Utils.getJmxDataList(DEFAULT_DATA_FILES);

		riemannCommunicator.send("cpu_total_usage", Utils.totalCpuUsage(cpuDataList.get(0), cpuDataList.get(1)),"cpuUsage");
		riemannCommunicator.send("mem_free", (memData.getMemFree()*100.0f)/memData.getMemTotal(),"mem_free");
		riemannCommunicator.send("mem_buffers", (memData.getMemBuffers()*100.0f)/memData.getMemTotal(),"mem_buffers");
		riemannCommunicator.send("mem_used", (memData.getMemUsed()*100.0f)/memData.getMemTotal(),"mem_used");
		HashMap<String,ArrayList<Double>> diskUsage = Utils.getDiskStats(diskDataList.get(0),diskDataList.get(1));
    for (String partitionName : diskUsage.keySet()) {
      riemannCommunicator
          .send("disk_write_" + partitionName, diskUsage.get(partitionName).get(0), "disk_write_" + partitionName);
      riemannCommunicator
          .send("disk_read_" + partitionName, diskUsage.get(partitionName).get(1), "disk_read_" + partitionName);
    }
		riemannCommunicator.send("loadavg", procP.gatherLoadAvg().getOneMinuteAvg(), "loadavg");

		for (JMXData jmxData : jmxDataList) {
      for (Map.Entry<String, Object> entry : jmxData.getValues().entrySet()) {
        riemannCommunicator
            .send(jmxData.getName() + '.' + entry.getKey(), (Float) entry.getValue(), jmxData.getTagsAsString());
      }
		}
	}

	public void printData(){
		Timestamp currentTimestamp = new java.sql.Timestamp(Calendar.getInstance().getTime().getTime());
		ProcParser procP = new ProcParser();
		ArrayList<CpuData> cpuDataList = new ArrayList<>();
		MemData memData = procP.gatherMemoryUsage();
		ArrayList<ArrayList<DiskData>> diskDataList = new ArrayList<>();

		for(int i = 0; i < 2; i++){
			cpuDataList.add(procP.gatherCpuUsage());
			diskDataList.add(procP.gatherDiskUsage());
			try{
				if(i % 2 == 0)
					Thread.sleep(SEND_INTERVAL_TIME);
			}catch(InterruptedException e){
        Logger.getLogger(DataSender.class.getName()).severe("Failed to wait " + SEND_INTERVAL_TIME + "ms.");
			}
		}
		System.out.println("***********************************\n     " + currentTimestamp + "\n***********************************");
		System.out.println("cpu_total_usage " + Utils.totalCpuUsage(cpuDataList.get(0), cpuDataList.get(1)));
		System.out.println("mem_free " + (memData.getMemFree()*100.0f)/memData.getMemTotal());
		System.out.println("mem_buffers " +(memData.getMemBuffers()*100.0f)/memData.getMemTotal());
		System.out.println("mem_used " + (memData.getMemUsed()*100.0f)/memData.getMemTotal());
		HashMap<String,ArrayList<Double>> diskUsage = Utils.getDiskStats(diskDataList.get(0),diskDataList.get(1));
    for (String partitionName : diskUsage.keySet()) {
      System.out.println("disk_write_" + partitionName + " " + diskUsage.get(partitionName).get(0));
      System.out.println("disk_read_" + partitionName + " " + diskUsage.get(partitionName).get(1));
    }
		System.out.println("loadavg " + procP.gatherLoadAvg().getOneMinuteAvg());

    List<JMXData> jmxDataList = Utils.getJmxDataList(DEFAULT_DATA_FILES);

    for (JMXData jmxData : jmxDataList) {
      for (Object value : jmxData.getValues().values()) {
        System.out.println("Name: " + jmxData.getName() + "Value: " + value);
      }
    }
	}
}
