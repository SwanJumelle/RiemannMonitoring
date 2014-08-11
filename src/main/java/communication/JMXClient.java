package communication;

import sun.jvm.hotspot.HelloWorld;

import javax.management.*;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import java.io.IOException;
import java.util.List;

/**
 * Created by antoinelavail on 11/08/14.
 */
public class JMXClient {

  private JMXConnector jmxc;

  private MBeanServerConnection mbsc;

  public JMXClient(String jmxHost, Integer jmxPort) throws IOException {
    String url = "service:jmx:rmi:///jndi/rmi://" + jmxHost + ":" + jmxPort + "/jmxrmi";
    JMXServiceURL jmxUrl = new JMXServiceURL(url);
    jmxc = JMXConnectorFactory.connect(jmxUrl, null);
    mbsc = jmxc.getMBeanServerConnection();
  }

  public Object getJMXValue(String mbeanStringName, String attribute) {

    ObjectName mbeanName = null;
    try {
      mbeanName = new ObjectName(mbeanStringName);
    } catch (MalformedObjectNameException e) {
      e.printStackTrace();
    }
    Object value = null;
      try {
        value = mbsc.getAttribute(mbeanName, attribute);
      } catch (Exception e) {
        e.printStackTrace();
      }
    System.out.println(value.toString());
    return value;
  }
}
