package com.thaiopensource.relaxng.match;

import com.thaiopensource.datatype.DatatypeLibraryLoader;
import com.thaiopensource.relaxng.parse.IllegalSchemaException;
import com.thaiopensource.relaxng.parse.Parseable;
import com.thaiopensource.relaxng.parse.compact.CompactParseable;
import com.thaiopensource.relaxng.parse.sax.SAXParseable;
import com.thaiopensource.relaxng.pattern.*;
import com.thaiopensource.resolver.Input;
import com.thaiopensource.resolver.xml.sax.SAXResolver;
import com.thaiopensource.util.VoidValue;
import org.relaxng.datatype.DatatypeLibraryFactory;
import org.xml.sax.ErrorHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

import java.io.IOException;

/**
 * Provides method to load a MatchablePattern by parsing.
 */
public class MatchablePatternLoader {
  public static final int COMPACT_SYNTAX_FLAG = 0x1;
  public static final int FEASIBLE_FLAG = 0x2;

  public MatchablePattern load(Input input,
                               SAXResolver saxResolver,
                               ErrorHandler eh,
                               DatatypeLibraryFactory dlf,
                               int flags) throws IOException, SAXException, IncorrectSchemaException {
    SchemaPatternBuilder spb = new SchemaPatternBuilder();
    Parseable<Pattern, com.thaiopensource.relaxng.pattern.NameClass, Locator, VoidValue, CommentListImpl, AnnotationsImpl> parseable;
    if ((flags & COMPACT_SYNTAX_FLAG) != 0)
      parseable = new CompactParseable<>(input, saxResolver.getResolver(), eh);
    else
      parseable = new SAXParseable<>(saxResolver.createSAXSource(input), saxResolver, eh);
    if (dlf == null)
      dlf = new DatatypeLibraryLoader();
    try {
      Pattern start = SchemaBuilderImpl.parse(parseable, eh, dlf, spb, false);
      if ((flags & FEASIBLE_FLAG) != 0)
        start = FeasibleTransform.transform(spb, start);
      return new MatchablePatternImpl(spb, start);
    } catch (IllegalSchemaException e) {
      throw new IncorrectSchemaException();
    }
  }
}
