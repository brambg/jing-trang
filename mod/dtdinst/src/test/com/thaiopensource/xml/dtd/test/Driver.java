package com.thaiopensource.xml.dtd.test;

import java.io.IOException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.BufferedInputStream;
import java.util.Hashtable;

import com.thaiopensource.xml.out.XmlWriter;
import com.thaiopensource.xml.dtd.om.DtdParser;
import com.thaiopensource.xml.dtd.om.Dtd;
import com.thaiopensource.xml.dtd.parse.DtdParserImpl;
import com.thaiopensource.xml.dtd.app.SchemaWriter;
import com.thaiopensource.xml.dtd.app.XmlOutputStreamWriter;
import com.thaiopensource.xml.em.FileEntityManager;

public class Driver {
  public static void main (String args[]) throws IOException, TestFailException {
    String dir = args[0];
    String failDir = args[1];
    String[] files = new File(dir).list();
    Hashtable fileTable = new Hashtable();
    for (String file1 : files) fileTable.put(file1, file1);
    StringBuilder failures = null;
    for (String file : files)
      if (file.endsWith(".dtd")) {
        String inFile = file;
        String outFile = inFile.substring(0, inFile.length() - 4) + ".xml";
        if (fileTable.get(outFile) != null) {
          try {
            System.err.println("Running test " + inFile);
            runCompareTest(new File(dir, inFile), new File(dir, outFile));
          } catch (CompareFailException e) {
            System.err.println(inFile + " failed at byte " + e.getByteIndex());
            if (failures == null)
              failures = new StringBuilder(inFile);
            else
              failures.append(" ").append(inFile);
            runOutputTest(new File(dir, inFile), new File(failDir, outFile));
          }
        }
      }
    if (failures != null)
      throw new TestFailException(failures.toString());
  }

  public static void runCompareTest(File inFile, File outFile) throws IOException {
    runTest(inFile,
	    new CompareOutputStream(new BufferedInputStream(new FileInputStream(outFile))));

  }

  public static void runOutputTest(File inFile, File outFile) throws IOException {
    runTest(inFile, new FileOutputStream(outFile));
  }

  private static void runTest(File inFile, OutputStream out) throws IOException {
    DtdParser dtdParser = new DtdParserImpl();
    Dtd dtd = dtdParser.parse(inFile.toString(), new FileEntityManager());
    XmlWriter w = new XmlOutputStreamWriter(out, dtd.getEncoding());
    new SchemaWriter(w).writeDtd(dtd);
    w.close();
  }
}
