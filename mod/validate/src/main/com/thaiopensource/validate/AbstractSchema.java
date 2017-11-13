package com.thaiopensource.validate;

import com.thaiopensource.util.PropertyMap;
import com.thaiopensource.util.PropertyId;
import com.thaiopensource.util.PropertyMapBuilder;

public abstract class AbstractSchema implements Schema {
  private final PropertyMap properties;

  public AbstractSchema() {
    this(PropertyMap.EMPTY);
  }

  public AbstractSchema(PropertyMap properties) {
    this.properties = properties;
  }

  public AbstractSchema(PropertyMap properties, PropertyId<?>[] supportedPropertyIds) {
    this(filterProperties(properties, supportedPropertyIds));
  }

  public PropertyMap getProperties() {
    return properties;
  }

  static public PropertyMap filterProperties(PropertyMap properties, PropertyId<?>[] supportedPropertyIds) {
    PropertyMapBuilder builder = new PropertyMapBuilder();
    for (PropertyId<?> supportedPropertyId : supportedPropertyIds) copy(builder, supportedPropertyId, properties);
    return builder.toPropertyMap();
  }

  static private <T> void copy(PropertyMapBuilder builder, PropertyId<T> pid, PropertyMap properties) {
    T value = properties.get(pid);
    if (value != null)
      builder.put(pid, value);
  }
}
