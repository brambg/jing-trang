package com.thaiopensource.relaxng.output.xsd;

import com.thaiopensource.relaxng.edit.AnyNameNameClass;
import com.thaiopensource.relaxng.edit.ChoiceNameClass;
import com.thaiopensource.relaxng.edit.NameClass;
import com.thaiopensource.relaxng.edit.NameClassVisitor;
import com.thaiopensource.relaxng.edit.NameNameClass;
import com.thaiopensource.relaxng.edit.NsNameNameClass;
import com.thaiopensource.util.VoidValue;
import com.thaiopensource.relaxng.output.xsd.basic.Wildcard;
import com.thaiopensource.xml.util.Name;

import java.util.*;

class WildcardBuilder implements NameClassVisitor<VoidValue> {
  private boolean inExcept = false;
  private final String inheritedNamespace;
  private Wildcard wildcard = null;
  private Set<Name> excludedNames;
  private Set<String> namespaces;
  private String inNs = null;

  static Wildcard createWildcard(NameClass nc, String inheritedNamespace) {
    WildcardBuilder builder = new WildcardBuilder(inheritedNamespace);
    nc.accept(builder);
    return builder.wildcard;
  }

  private void combineWildcard(Wildcard wc) {
    if (wildcard == null)
      wildcard = wc;
    else
      wildcard = Wildcard.union(wildcard, wc);
  }

  private WildcardBuilder(String inheritedNamespace) {
    this.inheritedNamespace = inheritedNamespace;
  }

  public VoidValue visitChoice(ChoiceNameClass nc) {
    List<NameClass> list = nc.getChildren();
    for (NameClass aList : list) aList.accept(this);
    return VoidValue.VOID;
  }

  public VoidValue visitAnyName(AnyNameNameClass nc) {
    if (!inExcept) {
      if (nc.getExcept() != null) {
        namespaces = new HashSet<>();
        excludedNames = new HashSet<>();
        inExcept = true;
        nc.getExcept().accept(this);
        inExcept = false;
      }
      else {
        namespaces = Collections.emptySet();
        excludedNames = Collections.emptySet();
      }
      combineWildcard(new Wildcard(false, namespaces, excludedNames));
    }
    return VoidValue.VOID;
  }

  public VoidValue visitNsName(NsNameNameClass nc) {
    String ns = resolve(nc.getNs());
    if (!inExcept) {
      if (nc.getExcept() != null) {
        namespaces = null;
        excludedNames = new HashSet<>();
        inNs = ns;
        inExcept = true;
        nc.getExcept().accept(this);
        inExcept = false;
        inNs = null;
      }
      else
        excludedNames = Collections.emptySet();
      namespaces = new HashSet<>();
      namespaces.add(ns);
      combineWildcard(new Wildcard(true, namespaces, excludedNames));
    }
    else if (inNs == null)
      namespaces.add(ns);
    return VoidValue.VOID;
  }

  public VoidValue visitName(NameNameClass nc) {
    if (inExcept) {
      String ns = resolve(nc.getNamespaceUri());
      if (inNs == null || inNs.equals(ns))
        excludedNames.add(new Name(ns, nc.getLocalName()));
    }
    return VoidValue.VOID;
  }

  private String resolve(String ns) {
    if (Objects.equals(ns, NameNameClass.INHERIT_NS))
      return inheritedNamespace;
    return ns;
  }
}
