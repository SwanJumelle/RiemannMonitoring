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

	public static void main( String[] args )
	{
		RiemannCommunicator riemannCommunicator = new RiemannCommunicator("10.42.2.6");
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
