package com.vero.system.usage;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class JMXData {

  private String name;

  private String objectName;

  private List<String> tags;

  /**
   * key: attribute name
   * value: metric value
   */
  private Map<String, Object> values;

  /**
   * Build a JMXData from a
   */
  public JMXData(String name, String objectName) {
    this.name = name;
    this.objectName = objectName;
    this.values = new HashMap<>();
  }

  public String getName() {
    return name;
  }

  public String getObjectName() {
    return objectName;
  }

  public void addValue(String attr, Object value) {
    values.put(attr, value);
  }

  public void setValues(Map<String, Object> vals) {
    values = vals;
  }

  public Map<String, Object> getValues() {
    return values;
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

  @Override
  public String toString() {
    String str = "name: " + name + "\nobjectName: " + objectName;
    for (Map.Entry<String, Object> entry : values.entrySet()) {
      str += "\nattribute: " + entry.getKey() + "\t value: " + entry.getValue();
    }
    return str;
  }

}
