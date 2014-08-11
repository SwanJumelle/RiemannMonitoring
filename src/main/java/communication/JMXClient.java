package communication;

import javax.management.MBeanServerConnection;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;

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

  public Object getJMXValue(String domain, String mbeanName, ArrayList<String> attributes) {
    return null;
  }
}
