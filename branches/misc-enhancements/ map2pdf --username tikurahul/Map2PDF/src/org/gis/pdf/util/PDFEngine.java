package org.gis.pdf.util;

import java.io.OutputStream;
import java.net.URL;
import java.util.Map;

import org.gis.pdf.report.ReportGenerator;

import com.lowagie.text.Document;
import com.lowagie.text.Image;
import com.lowagie.text.pdf.PdfWriter;

public class PDFEngine {
  
  protected URL imageUrl;
  protected OutputStream pdfStream;
  protected String pageTitle;
  protected ReportGenerator gen;
  protected Map<String, String> params;
  
  public PDFEngine (URL imageURL, OutputStream pdfStream, String pageTitle, ReportGenerator gen, Map<String, String> params) {
    this.imageUrl = imageURL;
    this.pdfStream = pdfStream;
    this.pageTitle = pageTitle;
    this.gen = gen;
    this.params = params;
  }
  
  public void createPDF() throws Exception {
    Document document = new Document();
    PdfWriter.getInstance(document, pdfStream);
    Image map = Image.getInstance(imageUrl);
    // this doesn't automatically cause the image to be scaled unfortunately,
    // but let's set it to the correct value anyways...(assuming ESRI default
    // 96 dpi)...
    map.setDpi(96, 96);
    // ESRI defaults to 96 dpi, iText defaults to 72 dpi so scale the image
    // down by 75% to match...
    map.scalePercent(75);

    gen.generate(document, map, pageTitle, params);
  }
}
