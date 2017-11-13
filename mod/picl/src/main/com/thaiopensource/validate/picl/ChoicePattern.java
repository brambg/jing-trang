package com.thaiopensource.validate.picl;

class ChoicePattern extends Pattern {
  private final Pattern[] choices;

  ChoicePattern(Pattern[] choices) {
    this.choices = choices;
  }

  boolean matches(Path path, int rootDepth) {
    for (Pattern choice : choices)
      if (choice.matches(path, rootDepth))
        return true;
    return false;
  }

  public String toString() {
    StringBuilder buf = new StringBuilder();
    for (int i = 0; i < choices.length; i++) {
      if (i != 0)
        buf.append('|');
      buf.append(choices[i].toString());
    }
    return buf.toString();
  }
}
