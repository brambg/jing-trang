package com.thaiopensource.relaxng.pattern;

import com.thaiopensource.relaxng.parse.IllegalSchemaException;
import org.xml.sax.SAXException;

import java.io.IOException;

public interface PatternFuture {
  Pattern getPattern(boolean isAttributesPattern) throws IllegalSchemaException, SAXException, IOException;
}
