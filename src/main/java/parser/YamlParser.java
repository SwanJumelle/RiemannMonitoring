package parser;

import java.io.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import communication.JMXClient;
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
    String jmxHost = ((Map<String, String>) data.get("jmx")).get("host");
    Integer jmxPort = ((Map<String, Integer>) data.get("jmx")).get("port");

    JMXClient jmxClient = null;
    try {
      jmxClient = new JMXClient(jmxHost, jmxPort);
    } catch (IOException e) {
      e.printStackTrace();
    }

    if (jmxClient != null) {
      List<JMXData> jmxDataList = new ArrayList<JMXData>();
      List<LinkedHashMap<String, Object>> queries = (List<LinkedHashMap<String, Object>>) data.get("queries");

      for (LinkedHashMap<String, Object> query : queries) {
        JMXData jmxData = new JMXData((String) query.get("name"), (String) query.get("service"), (String) query.get("obj"));
        Object jmxValue = jmxClient.getJMXValue(jmxData.getService(), jmxData.getObjectName(), null);
        jmxData.setValue(jmxValue);
      }
    }

    return null;
  }
}
