package com.thaiopensource.relaxng.output.xsd.basic;

import com.thaiopensource.relaxng.edit.SourceLocation;
import com.thaiopensource.util.Equal;

public class Annotated extends Located {
  private final Annotation annotation;

  public Annotated(SourceLocation location, Annotation annotation) {
    super(location);
    this.annotation = annotation;
  }

  public Annotation getAnnotation() {
    return annotation;
  }

  public boolean equals(Object obj) {
    if (obj == null)
      return false;
    return this.getClass() == obj.getClass() && Equal.equal(annotation, ((Annotated) obj).annotation);
  }

  public int hashCode() {
    int hc = getClass().hashCode();
    if (annotation != null)
      hc ^= annotation.hashCode();
    return hc;
  }
}
