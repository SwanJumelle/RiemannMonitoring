package communication;

import java.io.IOException;

public class RiemannJmx {
	
	Process rJmxJar;
	private static final String riemannJmxJarPath = "lib/riemann-jmx-clj-0.1.0-SNAPSHOT-standalone.jar";
	private static final String configFilePath = "src/main/jmx_config/";
	
	public RiemannJmx(Process rJmxJar){
		this.rJmxJar = rJmxJar;
	}
	
	public void destroy(){
		rJmxJar.destroy();
	}
	
	public void gatherStats(String[] args) throws IOException{
		if(args.length < 1){
			//log no config files to load: no data will be tracked by jmx
			return;
		}
		String ymlFiles = "";
		for(int i = 0; i < args.length; i++){
			ymlFiles += " "+configFilePath+args[i];
		}
		rJmxJar = Runtime.getRuntime().exec("java -jar "+riemannJmxJarPath +ymlFiles);
	}

}
