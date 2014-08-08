package com.yaml.parser;

import org.yaml.snakeyaml.Yaml;

/**
 * Created by antoinelavail on 08/08/14.
 */
public class YamlParser {

  private Yaml yaml;

  public YamlParser(String yamlFile) {
    yaml = new Yaml();
    Object obj = yaml.load(yamlFile);
    System.out.println(obj);
  }
}
