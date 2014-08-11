package utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import parser.YamlParser;
import usage.CpuData;
import usage.DiskData;
import usage.JMXData;

/**
 * 
 * @author pmdusso
 */
public class Utils {

	/**
	 * Gets the PID of the current process.
	 * 
	 * @return The integer value of the current process PID; -1 if something
	 *         goes wrong.
	 */
	public static int getPid() {
		try {
			byte[] bo = new byte[100];
			String[] cmd = { "bash", "-c", "echo $PPID" };
			Process p = Runtime.getRuntime().exec(cmd);
			p.getInputStream().read(bo);
			return Integer.parseInt(new String(bo).trim());
		} catch (IOException ex) {
			Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null, ex);
		}
		return -1;
	}

	/**
	 * Try to parse a string to a integer.
	 * 
	 * @return True if the string can be converted, false otherwise.
	 */
	public static boolean tryParseInt(String value) {
		try {
			Integer.parseInt(value);
			return true;
		} catch (NumberFormatException ex) {
			return false;
		}
	}

	/**
	 * Test when a string is empty or null.
	 * 
	 * @return True if the string is not null nor empty; false if is empty or
	 *         null.
	 * 
	 */
	public static boolean stringNotEmpty(String s) {
		return (s != null && s.length() > 0);
	}

	/**
	 * Get NetworkInterface for the current host and then read the hardware
	 * address.
	 * 
	 * @return The hashCode of the MAC address object; -1 if something goes
	 *         wrong.
	 */
	public static String getMacAddress() {
		InetAddress ip = null;
		try {
			ip = InetAddress.getLocalHost();
			ip.getHostAddress();
			NetworkInterface network = NetworkInterface.getByInetAddress(ip);

			if (network != null) {
				byte[] mac = network.getHardwareAddress();
				return macToString(mac);
			} else {
				return getMacAddress2();
			}
		} catch (UnknownHostException e2) {
			Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null, e2);
		} catch (SocketException e) {
			Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null, e);
		}
		return null;
	}

	private static String macToString(byte[] mac) {
		return mac.toString();
	}

	public static String getMacAddress2() {
		Process p;
		try {
			p = Runtime.getRuntime().exec("getmac /fo csv /nh");
			BufferedReader in = new BufferedReader(new InputStreamReader(
					p.getInputStream()));
			String line = in.readLine().split(",")[0].replace('"', ' ');
			return line;

		} catch (IOException e) {
			Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null, e);
		}
		return null;

	}

	public static double totalCpuUsage(CpuData c1, CpuData c2){
		double usage = (c2.getUser()-c1.getUser())+(c2.getNice()-c1.getNice())+(c2.getSysmode()-c1.getSysmode());
		double total = usage + (c2.getIdle()-c1.getIdle());
		return (100*usage)/total;
	}

	public static HashMap<String,ArrayList<Double>> getDiskStats(ArrayList<DiskData> dList1, ArrayList<DiskData> dList2) {
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

	public static ArrayList<JMXData> getJmxDataList(String path){
		ArrayList<JMXData> jmxDataList = new ArrayList<JMXData>();
		// Parse all the .yaml files
		YamlParser parsedYaml = null;
		File yamlFolder = new File(path);
		for (final File fileEntry : yamlFolder.listFiles()) {
			String fileExtension = fileEntry.getName().substring(fileEntry.getName().lastIndexOf('.'));
			if (fileExtension.equals(".yaml") || fileExtension.equals(".yml")) {
				try {
					parsedYaml = new YamlParser(path + fileEntry.getName());
					jmxDataList.addAll(parsedYaml.gatherJMXStats());
				} catch (FileNotFoundException e) {
				}
			}
		}

		return jmxDataList;
	}
}
