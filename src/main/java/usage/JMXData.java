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
  public JMXData(String name, String service, String objectName) {

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

  public void addAttribute(String attr) {
    attributes.add(attr);
  }

  public List<String> getAttributes() {
    return attributes;
  }

  public void setAttributes(List<String> attrs) {
    attributes = attrs;
  }

  public void addTag(String tag) {
    tags.add(tag);
  }

  public List<String> getTags() {
    return tags;
  }

  public void setTags(List<String> tags) {
    this.tags = tags;
  }

  public String getTagsAsString() {
    return tags.toString();
  }

  public Object getValue() {
    return value;
  }

  public void setValue(Object val) {
    value = val;
  }
}
