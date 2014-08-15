package com.vero.system.usage;

import java.util.ArrayList;

public class LoadAvgData {

	private double oneMinuteAvg;
	private double fiveMinuteAvg;
	private double fithteenMinuteAvg;
	
	public LoadAvgData(ArrayList<String> data) {
		oneMinuteAvg = Double.parseDouble(data.get(0));
		fiveMinuteAvg = Double.parseDouble(data.get(1));
		fithteenMinuteAvg = Double.parseDouble(data.get(2));
	}

	public double getOneMinuteAvg() {
		return oneMinuteAvg;
	}

	public void setOneMinuteAvg(double oneMinuteAvg) {
		this.oneMinuteAvg = oneMinuteAvg;
	}

	public double getFiveMinuteAvg() {
		return fiveMinuteAvg;
	}

	public void setFiveMinuteAvg(double fiveMinuteAvg) {
		this.fiveMinuteAvg = fiveMinuteAvg;
	}

	public double getFithteenMinuteAvg() {
		return fithteenMinuteAvg;
	}

	public void setFithteenMinuteAvg(double fithteenMinuteAvg) {
		this.fithteenMinuteAvg = fithteenMinuteAvg;
	}

}
