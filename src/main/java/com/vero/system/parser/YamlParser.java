package com.vero.system.parser;

import java.io.*;
import java.util.*;

import com.vero.system.monitoring.communication.JMXClient;
import com.vero.system.usage.JMXData;
import org.yaml.snakeyaml.Yaml;


public class YamlParser {

  private Map<String, Object> data;

  @SuppressWarnings("unchecked")
  public YamlParser(String path) throws FileNotFoundException{
    InputStream input = new FileInputStream(new File(path));
    Yaml yaml = new Yaml();
    data = (Map<String, Object>) yaml.load(input);
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

    List<JMXData> jmxDataList = null;
    if (jmxClient != null) {
      jmxDataList = new ArrayList<>();
      List<LinkedHashMap<String, Object>> queries = (List<LinkedHashMap<String, Object>>) data.get("queries");

      for (LinkedHashMap<String, Object> query : queries) {

        List<String> attributes = (List<String>) query.get("attr");
        Map<String,Object> values = jmxClient.getJMXValues((String) query.get("obj"), attributes);

        JMXData jmxData = new JMXData((String) query.get("name"), (String) query.get("obj"));
        jmxData.setValues(values);
        jmxData.setTags((List<String>) query.get("tags"));

        jmxDataList.add(jmxData);
      }
    }
    return jmxDataList;
  }
}
