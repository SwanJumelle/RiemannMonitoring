package communication;

import java.io.IOException;
import java.util.ArrayList;

import utils.Utils;

public class RiemannJmx {
	
	Process rJmxJar;
	private String riemannJmxJarPath = "lib/riemann-jmx-clj-0.1.0-SNAPSHOT-standalone.jar";
	private static final String configFilePath = "src/main/jmx_config/";
	
	public RiemannJmx(Process rJmxJar){
		this.rJmxJar = rJmxJar;
	}
	
	public RiemannJmx(){
		super();
	}
	
	public void destroy(){
		rJmxJar.destroy();
	}
	
	public void gatherStatsDepreciated(String[] args) throws IOException{
		if(args.length < 3){
			//log no config files to load: no data will be tracked by jmx
			return;
		}
		String ymlFiles = "";
		for(int i = 2; i < args.length; i++){
			ymlFiles += " "+configFilePath+args[i];
		}
		System.out.println(riemannJmxJarPath+ymlFiles);
		rJmxJar = Runtime.getRuntime().exec("java -jar "+riemannJmxJarPath +ymlFiles);
	}

	public void gatherStats() {
		ArrayList<String> ymlFiles = Utils.getYamlFiles(configFilePath);
		if(ymlFiles.isEmpty()){
			//log no config files to load: no data will be tracked by jmx
			return;
		}
		
	}

}
