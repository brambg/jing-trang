package com.thaiopensource.datatype.xsd;

import com.thaiopensource.datatype.xsd.regex.RegexSyntaxException;
import com.thaiopensource.util.Localizer;
import org.relaxng.datatype.Datatype;
import org.relaxng.datatype.DatatypeBuilder;
import org.relaxng.datatype.DatatypeException;
import org.relaxng.datatype.ValidationContext;

class DatatypeBuilderImpl implements DatatypeBuilder {
  static final Localizer localizer = new Localizer(DatatypeBuilderImpl.class);

  private DatatypeBase base;
  private final DatatypeLibraryImpl library;

  DatatypeBuilderImpl(DatatypeLibraryImpl library, DatatypeBase base) throws DatatypeException {
    this.library = library;
    this.base = base;
  }

  public void addParameter(String name,
			   String value,
			   ValidationContext context) throws DatatypeException {
    switch (name) {
      case "pattern":
        addPatternParam(value);
        break;
      case "minInclusive":
        addMinInclusiveParam(value, context);
        break;
      case "maxInclusive":
        addMaxInclusiveParam(value, context);
        break;
      case "minExclusive":
        addMinExclusiveParam(value, context);
        break;
      case "maxExclusive":
        addMaxExclusiveParam(value, context);
        break;
      case "length":
        addLengthParam(value);
        break;
      case "minLength":
        addMinLengthParam(value);
        break;
      case "maxLength":
        addMaxLengthParam(value);
        break;
      case "fractionDigits":
        addScaleParam(value);
        break;
      case "totalDigits":
        addPrecisionParam(value);
        break;
      case "enumeration":
        error("enumeration_param");
        break;
      case "whiteSpace":
        error("whiteSpace_param");
        break;
      default:
        error("unrecognized_param", name);
        break;
    }
  }

  private void addPatternParam(String value) throws DatatypeException {
    try {
      base = new PatternRestrictDatatype(base,
					 library.getRegexEngine().compile(value),
                                         value);
    }
    catch (RegexSyntaxException e) {
      int pos = e.getPosition();
      if (pos == RegexSyntaxException.UNKNOWN_POSITION)
        pos = DatatypeException.UNKNOWN;
      error("invalid_regex", e.getMessage(), pos);
    }
  }

  private void addMinInclusiveParam(String value, ValidationContext context)
    throws DatatypeException {
    base = new MinInclusiveRestrictDatatype(base,
					    getLimit(value, context),
                                            value);
  }

  private void addMaxInclusiveParam(String value, ValidationContext context)
    throws DatatypeException {
    base = new MaxInclusiveRestrictDatatype(base,
					    getLimit(value, context),
                                            value);
  }

  private void addMinExclusiveParam(String value, ValidationContext context)
    throws DatatypeException {
    base = new MinExclusiveRestrictDatatype(base,
					    getLimit(value, context),
                                            value);
  }

  private void addMaxExclusiveParam(String value, ValidationContext context)
    throws DatatypeException {
    base = new MaxExclusiveRestrictDatatype(base,
					    getLimit(value, context),
                                            value);
  }

  private Object getLimit(String str, ValidationContext context)
    throws DatatypeException {
    if (base.getOrderRelation() == null)
      error("not_ordered");
    str = base.normalizeWhiteSpace(str);
    try {
      base.checkLexicallyAllows(str);
      return base.getValue(str, context);
    }
    catch (DatatypeException e) {
      throw new DatatypeException(localizer.message("invalid_limit", str, e.getMessage()));
    }
  }

  private void addLengthParam(String value) throws DatatypeException {
    base = new LengthRestrictDatatype(base, getLength(value));
  }

  private void addMinLengthParam(String value) throws DatatypeException {
    base = new MinLengthRestrictDatatype(base, getLength(value));
  }

  private void addMaxLengthParam(String value) throws DatatypeException {
    base = new MaxLengthRestrictDatatype(base, getLength(value));
  }

  private int getLength(String str) throws DatatypeException {
    if (base.getMeasure() == null)
      error("no_length");
    int len = convertNonNegativeInteger(str);
    if (len < 0)
      error("length_not_non_negative_integer");
    return len;
  }
    
  private void addScaleParam(String str) throws DatatypeException {
    if (!(base.getPrimitive() instanceof DecimalDatatype))
      error("scale_not_derived_from_decimal");
    int scale = convertNonNegativeInteger(str);
    if (scale < 0)
      error("scale_not_non_negative_integer");
    base = new ScaleRestrictDatatype(base, scale);
  }

  private void addPrecisionParam(String str) throws DatatypeException {
    if (!(base.getPrimitive() instanceof DecimalDatatype))
      error("precision_not_derived_from_decimal");
    int scale = convertNonNegativeInteger(str);
    if (scale <= 0)
      error("precision_not_positive_integer");
    base = new PrecisionRestrictDatatype(base, scale);
  }

  public Datatype createDatatype() {
    return base;
  }

  private static void error(String key) throws DatatypeException {
    throw new DatatypeException(localizer.message(key));
  }

  private static void error(String key, String arg) throws DatatypeException {
    throw new DatatypeException(localizer.message(key, arg));
  }

  private static void error(String key, String arg, int pos) throws DatatypeException {
    throw new DatatypeException(pos, localizer.message(key, arg));
  }

  // Return -1 for anything that is not a nonNegativeInteger
  // Return Integer.MAX_VALUE for values that are too big

  private int convertNonNegativeInteger(String str) {
    str = str.trim();
    DecimalDatatype decimal = new DecimalDatatype();
    if (!decimal.lexicallyAllows(str))
      return -1;
    // Canonicalize the value
    str = decimal.getValue(str, null).toString();
    // Reject negative and fractional numbers
    if (str.charAt(0) == '-' || str.indexOf('.') >= 0)
      return -1;
    try {
      return Integer.parseInt(str);
    }
    catch (NumberFormatException e) {
      // Map out of range integers to MAX_VALUE
      return Integer.MAX_VALUE;
    }
  }
}
