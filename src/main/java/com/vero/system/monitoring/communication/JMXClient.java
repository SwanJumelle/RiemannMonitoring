package com.vero.system.monitoring.communication;

import javax.management.*;
import javax.management.openmbean.CompositeDataSupport;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;

public class JMXClient {

  private MBeanServerConnection mbsc;

  public JMXClient(String jmxHost, Integer jmxPort) throws IOException {
    String url = "service:jmx:rmi:///jndi/rmi://" + jmxHost + ":" + jmxPort + "/jmxrmi";
    JMXServiceURL jmxUrl = new JMXServiceURL(url);
    JMXConnector jmxc = JMXConnectorFactory.connect(jmxUrl, null);
    mbsc = jmxc.getMBeanServerConnection();
  }

  public Map<String, Object> getJMXValues(String queryMBeanName, List<String> attributes) {

    Map<String, Object> jmxValues = new HashMap<>();
    final List<String> matchedMBeans = new ArrayList<>();
    try {
      // Query the mbean to get all the instances
      final Set<ObjectInstance> matchingMBeans = mbsc.queryMBeans(new ObjectName(queryMBeanName), null);
      for (final ObjectInstance mbean : matchingMBeans ) {
        // Gather the mbeans found
        matchedMBeans.add(mbean.getObjectName().getCanonicalName());
      }

      // For all attributes, retrieve the corresponding MBean value
      for (String attr : attributes) {
        Float valf = 0.0f;
        for (String strMBean : matchedMBeans) {
          // Special case for memory usage
          if (attr.equals("HeapMemoryUsage") || attr.equals("NonHeapMemoryUsage")) {
            CompositeDataSupport memoryUsage = (CompositeDataSupport) mbsc.getAttribute(new ObjectName(strMBean),attr);
            valf += Float.valueOf(memoryUsage.get("used").toString());
          }
          else {
            Object val = mbsc.getAttribute(new ObjectName(strMBean), attr);
            valf += Float.valueOf(val.toString());
          }
        }
        jmxValues.put(attr, valf);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return jmxValues;
  }
}
