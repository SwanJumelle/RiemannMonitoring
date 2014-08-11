package parser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import org.yaml.snakeyaml.Yaml;
import usage.JMXData;

public class YamlParser {
	
	private Map<String, Object> data;

	@SuppressWarnings("unchecked")
	public YamlParser(String path) throws FileNotFoundException{
		InputStream input = new FileInputStream(new File(path));
		Yaml yaml = new Yaml();
		data = (Map<String, Object>) yaml.load(input);
	}

	public Map<String, Object> getData() {
		return data;
	}
	
	@SuppressWarnings("unchecked")
	public String getHost(){
		return ((Map<String, String>) data.get("riemann")).get("host");
	}

	@SuppressWarnings("unchecked")
	public int getPort() {
		return ((Map<String, Integer>) data.get("riemann")).get("port");
	}

  public List<JMXData> gatherJMXStats() {
    return null;
  }
}
