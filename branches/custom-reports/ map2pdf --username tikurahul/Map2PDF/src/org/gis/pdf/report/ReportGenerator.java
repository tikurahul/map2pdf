package org.gis.pdf.report;

import java.util.Map;

import com.lowagie.text.Document;
import com.lowagie.text.Image;

/**
 * Implement this interface in order to customize the PDF output produced by 
 * Map2PDF.  You'll need some knowledge of the iText PDF library.
 * <p>
 * @author corey@kelman.com
 */
public interface ReportGenerator
{
  public void generate(Document doc, Image map, String title, Map<String, String> params) 
    throws Exception;
}
