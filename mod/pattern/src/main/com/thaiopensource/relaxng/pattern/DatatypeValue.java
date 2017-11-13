package com.thaiopensource.relaxng.pattern;

import org.relaxng.datatype.Datatype;

class DatatypeValue {
  private final Object value;
  private final Datatype dt;

  DatatypeValue(Object value, Datatype dt) {
    this.value = value;
    this.dt = dt;
  }

  public int hashCode() {
    return dt.hashCode() ^ dt.valueHashCode(value);
  }

  public boolean equals(Object obj) {
    if (!(obj instanceof DatatypeValue))
      return false;
    DatatypeValue other = (DatatypeValue) obj;
    return other.dt == dt && dt.sameValue(value, other.value);
  }
}
