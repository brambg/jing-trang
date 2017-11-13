package com.thaiopensource.relaxng.edit;

import java.util.ArrayList;
import java.util.List;

public class ChoiceNameClass extends NameClass {
  private final List<NameClass> children = new ArrayList<>();

  public List<NameClass> getChildren() {
    return children;
  }

  public <T> T accept(NameClassVisitor<T> visitor) {
    return visitor.visitChoice(this);
  }

  public void childrenAccept(NameClassVisitor<?> visitor) {
    for (NameClass nc : children)
      nc.accept(visitor);
  }
}
