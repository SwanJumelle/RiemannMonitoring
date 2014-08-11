package usage;

import java.util.List;

/**
 * Created by antoinelavail on 11/08/14.
 */
public class JMXData {

  private String name;

  private String service;

  private String objectName;

  private List<String> attributes;

  private List<String> tags;

  private Object value;

  /**
   * Build a JMXData from a
   */
  public JMXData() {

  }

  public String getName() {
    return name;
  }

  public String getService() {
    return service;
  }

  public String getObjectName() {
    return objectName;
  }

  public List<String> getAttributes() {
    return attributes;
  }

  public List<String> getTags() {
    return tags;
  }

  public String getTagsAsString() {
    return tags.toString();
  }

  public Object getValue() {
    return value;
  }
}
