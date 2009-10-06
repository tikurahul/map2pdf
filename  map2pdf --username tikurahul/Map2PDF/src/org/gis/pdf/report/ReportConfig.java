package org.gis.pdf.report;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;

import org.gis.pdf.servlets.PDFMacros;

public class ReportConfig
{
  private static final Logger logger = Logger.getLogger(ReportConfig.class.getName());
  
  private final Map<String, Class<? extends ReportGenerator>> reports = 
    new HashMap<String, Class<? extends ReportGenerator>>();
  private Class<? extends ReportGenerator> defaultReport;

  public ReportConfig(URL config) throws ServletException {
    initReports(config);
  }
  
  public ReportGenerator getReportGenerator(String reportName) throws Exception {
    Class<? extends ReportGenerator> generator;
    
    if (PDFMacros.isEmpty(reportName)) {
      generator = defaultReport;
    } else {
      generator = reports.get(reportName);
    }
    
    if (generator == null) {
      throw new Exception(reportName + " is not a valid report.");
    }

    return generator.newInstance();
  }
  
  private void initReports(URL config) throws ServletException {
    Properties repProps = new Properties();
    try {
      InputStream inp = config.openStream();
      repProps.load(inp);
    }
    catch (IOException ioex) {
      logger.log(Level.WARNING, "Unable to read configuration file.  Using defaults.", ioex);
      defaultReport = SampleReport.class;
    }
    
    String repNames = repProps.getProperty("reports");
    if (repNames != null) {
      String[] names = repNames.split(",");
      for (String name : names) {
        String clsName = repProps.getProperty("report." + name);
        if (clsName != null) {
          try {
            Class<? extends ReportGenerator> clazzGen = 
              Class.forName(clsName).asSubclass(ReportGenerator.class);
            reports.put(name, clazzGen);
            // the first report becomes the default...
            if (defaultReport == null) { 
              defaultReport = clazzGen; 
              logger.info("Default report is " + defaultReport);
            }
          }
          catch (ClassCastException e) {
            throw new ServletException(clsName + " must implement the " + 
                ReportGenerator.class.getName() + " interface!", e);
          }
          catch (ClassNotFoundException e) {
            throw new ServletException(clsName + " not found.", e);
          }
        } else {
          throw new ServletException("Missing class name for report " + name);
        }
      }
    }
  }
}
