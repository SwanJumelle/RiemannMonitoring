package parser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;

import usage.CpuData;
import usage.DiskData;
import usage.LoadAvgData;
import usage.MemData;
import utils.Utils;

/**
 * Extract information from the process information pseudo-file system called
 * "/proc".
 *
 * @author pmdusso
 * @version 1.0 @created 24-abr-2012 15:22:37
 */
public class ProcParser {

	/*
	 * The pid of the process
	 */
	private int processPid = -1;
	/*
	 * The line which the number of cpu cores is located in /proc/cpuinfo in
	 * kernel 2.6.32-34-generic.
	 */
	public static final int cpucoresline = 12;

	/*
	 * Constant access path string. Those with 'pid' before are in /proc/[pid]/;
	 * Those with 'net' are in /proc/net/. Those without are directly in /proc/.
	 */
	public static final String pidStatmPath = "/proc/#/statm";
	public static final String pidStatPath = "/proc/#/stat";
	public static final String statPath = "/proc/stat";
	public static final String cpuinfoPath = "/proc/cpuinfo";
	public static final String meminfoPath = "/proc/meminfo";
	public static final String netdevPath = "/proc/net/dev";
	public static final String partitionsPath = "/proc/partitions";
	public static final String diskstatsPath = "/proc/diskstats";
	public static final String loadavgPath = "/proc/loadavg";
	public static final String EMPTY = "";
	public static final String COLON = ":";
	public static final String SPACE = " ";
	public static final String SHARP = "#";
	public static final String LINE_SEPARATOR = "line.separator";

	/**
	 * @param usageType
	 */
	public ProcParser(int processPid) {
		this.processPid = processPid;
	}

	/**
	 * Gathers the usage statistic from the /proc file system for CPU, Memory,
	 * Disk and Network
	 */
	/*public ArrayList<String> gatherUsage(UsageType uType) {
        if ((uType == null) || (this.processPid < 0)) {
            throw new IllegalArgumentException();
        }
        ArrayList<String> usageData = null;

        switch (uType) {
            case CPU:
                usageData = this.gatherCpuUsage();
                break;
            case MEMORY:
                usageData = this.gatherMemoryUsage(this.processPid);
                break;
            case DISK:
                usageData = this.gatherDiskUsage();
                break;
            case NETWORK:
                usageData = this.gatherNetworkUsage();
                break;
            default:
                break;
        }
        return usageData;
    }*/

	/**
	 *
	 * @param _processPid
	 * @param _memberValues
	 * @throws IOException
	 */
	public CpuData gatherCpuUsage() {
		BufferedReader br = null;
		String[] tempData;
		ArrayList<String> data = new ArrayList<String>();
		try {
			int numberOfCores = getNumberofCores();
			// Parse /proc/stat file and fill the member values list
			// We gonna parse de first line (total) and each line corresponding to
			// one core
			//Line example: cpu0 311689 2102 654770 6755602 32431 38 4127 0 0 0
			br = getStream(statPath);
			tempData = br.readLine().split(SPACE);
			//Adds the first 9 fields.
			for (int field = 2; field < 12; field++) {
				data.add(tempData[field]);
			}
			br.close();
		} catch (IOException ex) {
			Logger.getLogger(ProcParser.class.getName()).log(Level.SEVERE, null, ex);
		}
		return new CpuData(data);
	}

	private int getNumberofCores() throws IOException{
		String s;
		Process p = Runtime.getRuntime().exec("nproc");

		BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
		return Integer.parseInt(stdInput.readLine());
	}

	/**
	 * Get memory usage information. Files: /proc/[pid]/statm /proc/[pid]/stat
	 *
	 * @param _processPid
	 * @param _memberValues
	 */
	public MemData gatherMemoryUsage(int _processPid) {
		BufferedReader br = null;
		ArrayList<String> data = new ArrayList<String>();
		String[] tempData = null;
		try {
			br = getStream(meminfoPath);
			for (int i = 0; i < 4; i++) {
				tempData = br.readLine().split(SPACE);
				for (String s : tempData) {
					if (!s.isEmpty() && Utils.tryParseInt(s)) {
						data.add(s);
					}
				}
			}
			br.close();
		} catch (FileNotFoundException ex) {
			Logger.getLogger(ProcParser.class.getName()).log(Level.SEVERE, null, ex);
		} catch (IOException ex) {
			Logger.getLogger(ProcParser.class.getName()).log(Level.SEVERE, null, ex);
		}
		return new MemData(data);
	}

	/**
	 *
	 * @param _memberValues
	 */
	private ArrayList<String> gatherNetworkUsage() {
		ArrayList<String> data = new ArrayList<String>();
		String[] tempData = null;
		String[] tempFile = null;

		tempFile = getContents(netdevPath).split(System.getProperty(LINE_SEPARATOR));
		//Skip the first two lines (headers)
		for (int i = 2; i < tempFile.length; i++) {
			//Parse /proc/net/dev to obtain network statistics.
			//Line e.g.: 
			//lo: 4852 43 0 0 0 0 0 0 4852 43 0 0 0 0 0 0
			tempData = tempFile[i].replace(COLON, SPACE).split(SPACE);
			data.addAll(Arrays.asList(tempData));
			data.removeAll(Collections.singleton(EMPTY));
		}
		return data;
	}

	/**
	 *
	 * @param _memberValues
	 */
	private ArrayList<String> gatherPartitionUsage() {
		ArrayList<String> data = new ArrayList<String>();
		String[] tempData = null;
		String[] tempFile = null;

		tempFile = getContents(partitionsPath).split(System.getProperty(LINE_SEPARATOR));

		//parse the disk partitions
		for (int i = 2; i < tempFile.length; i++) {
			tempData = tempFile[i].split(SPACE);
			data.addAll(Arrays.asList(tempData));
			data.removeAll(Collections.singleton(EMPTY));
		}
		return data;
	}

	/*
	 * Create a list with the partitions name to be used to find their
	 * statistics in /proc/diskstats file
	 */
	private ArrayList<String> getPartitionNames(ArrayList<String> data) {

		ArrayList<String> partitionsName = new ArrayList<String>();
		for (String string : data) {
			if (!Utils.tryParseInt(string)) {
				partitionsName.add(string);
			}
		}
		return partitionsName;
	}

	/**
	 *
	 * @param _memberValues
	 */
	public ArrayList<DiskData> gatherDiskUsage() {
		ArrayList<String> partitionData = gatherPartitionUsage();
		
		ArrayList<DiskData> dataList = new ArrayList<DiskData>();
		String[] tempData = null;
		String[] tempFile = null;

		tempFile = getContents(diskstatsPath).split(System.getProperty(LINE_SEPARATOR));
		ArrayList<String> tempPart = getPartitionNames(partitionData);
		//Parse /proc/diskstats to obtain disk statistics

		for (String line : tempFile) {
			for (String partition : tempPart) {
				ArrayList<String> data = new ArrayList<String>();
				if (line.contains(SPACE + partition + SPACE)) {
					//split(SPACE);
					tempData = line.split(SPACE);
					//adds the rest of the disk statistics
					data.addAll(Arrays.asList(tempData));
					data.removeAll(Collections.singleton(EMPTY));
					dataList.add(new DiskData(data));
				}
			}
		}

		return dataList;
	}

	/**
	 * Fetch the entire contents of a text file, and return it in a String. This
	 * style of implementation does not throw Exceptions to the caller.
	 *
	 * @param path is a file which already exists and can be read.
	 * @throws IOException
	 */
	static private synchronized String getContents(String path) {
		//...checks on aFile are elided
		StringBuilder contents = new StringBuilder();

		try {
			//use buffering, reading one line at a time
			//FileReader always assumes default encoding is OK!
			BufferedReader input = new BufferedReader(new FileReader(new File(path)));
			try {
				String line = null; //not declared within while loop
				/*
				 * readLine is a bit quirky : it returns the content of a line
				 * MINUS the newline. it returns null only for the END of the
				 * stream. it returns an empty String if two newlines appear in
				 * a row.
				 */
				while ((line = input.readLine()) != null) {
					contents.append(line);
					contents.append(System.getProperty(LINE_SEPARATOR));
				}
			} finally {
				input.close();




			}
		} catch (IOException ex) {
			Logger.getLogger(ProcParser.class.getName()).log(Level.SEVERE, null, ex);
		}

		return contents.toString();
	}

	/**
	 * Opens a stream from a existing file and return it. This style of
	 * implementation does not throw Exceptions to the caller.
	 *
	 * @param path is a file which already exists and can be read.
	 * @throws IOException
	 */
	private synchronized BufferedReader getStream(String _path) throws IOException {
		BufferedReader br = null;
		File file = new File(_path);
		FileReader fileReader = null;
		try {
			fileReader = new FileReader(file);
			br = new BufferedReader(fileReader);




		} catch (FileNotFoundException ex) {
			Logger.getLogger(ProcParser.class.getName()).log(Level.SEVERE, null, ex);
		}
		return br;
	}
	
	public LoadAvgData gatherLoadAvg() {
		BufferedReader br = null;
		String[] tempData;
		ArrayList<String> data = new ArrayList<String>();
		try {
			br = getStream(loadavgPath);
			tempData = br.readLine().split(SPACE);
			//Adds the first 9 fields.
			for (int field = 0; field < 3; field++) {
				data.add(tempData[field]);
			}
			br.close();
		} catch (IOException ex) {
			Logger.getLogger(ProcParser.class.getName()).log(Level.SEVERE, null, ex);
		}
		return new LoadAvgData(data);
	}
	
}// end ProcInfoParser

