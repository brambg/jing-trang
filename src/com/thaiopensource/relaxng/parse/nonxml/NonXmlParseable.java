package com.thaiopensource.relaxng.parse.nonxml;

import com.thaiopensource.relaxng.parse.Parseable;
import com.thaiopensource.relaxng.parse.ParsedPattern;
import com.thaiopensource.relaxng.parse.SchemaBuilder;
import com.thaiopensource.relaxng.parse.BuildException;
import com.thaiopensource.relaxng.parse.IllegalSchemaException;
import com.thaiopensource.relaxng.parse.IncludedGrammar;
import com.thaiopensource.relaxng.parse.Scope;
import org.xml.sax.InputSource;
import org.xml.sax.ErrorHandler;

import java.io.Reader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.net.URL;

public class NonXmlParseable implements Parseable {
  private final InputSource in;
  private final ErrorHandler eh;

  public NonXmlParseable(InputSource in, ErrorHandler eh) {
    this.in = in;
    this.eh = eh;
  }

  public ParsedPattern parse(SchemaBuilder sb) throws BuildException, IllegalSchemaException {
    return new NonXmlSyntax(makeReader(in), in.getSystemId(), sb, eh).parse(null);
  }

  public void parseInclude(String uri, SchemaBuilder sb, IncludedGrammar g)
          throws BuildException, IllegalSchemaException {
    InputSource tem = new InputSource(uri);
    tem.setEncoding(in.getEncoding());
    new NonXmlSyntax(makeReader(tem), uri, sb, eh).parseInclude(g);
  }

  public ParsedPattern parseExternal(String uri, SchemaBuilder sb, Scope scope)
          throws BuildException, IllegalSchemaException {
    InputSource tem = new InputSource(uri);
    tem.setEncoding(in.getEncoding());
    return new NonXmlSyntax(makeReader(tem), uri, sb, eh).parse(scope);
  }

  private final String DEFAULT_ENCODING = "iso-8859-1";

  private Reader makeReader(InputSource is) throws BuildException {
    try {
      Reader r = is.getCharacterStream();
      if (r == null) {
        InputStream in = is.getByteStream();
        if (in == null) {
          String systemId = is.getSystemId();
          in = new URL(systemId).openStream();
        }
        String encoding = is.getEncoding();
        if (encoding == null)
          encoding = DEFAULT_ENCODING;
        r = new InputStreamReader(in, encoding);
      }
      return r;
    }
    catch (IOException e) {
      throw new BuildException(e);
    }
  }


}