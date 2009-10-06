package org.gis.pdf.report;

import java.awt.Color;
import java.util.Map;

import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;

/**
 * A sample ReportGenerator implementation.
 */
public class SampleReport implements ReportGenerator
{
  public void generate(Document doc, Image map, String title, Map<String, String> params) throws Exception
  {
    doc.setPageSize(PageSize.LETTER);
    doc.setMargins(36, 36, 36, 36); // 1/2 inch margins...
    
    // Add some meta-data
    doc.addTitle(title);
    doc.addAuthor("ArcGIS Server REST API - Map Printer");
    
    //creating title
    Paragraph titleParagraph = new Paragraph();
    titleParagraph.setAlignment(Element.ALIGN_CENTER);
    Chunk titleChunk = new Chunk(title, new Font(Font.TIMES_ROMAN, 14f, Font.BOLD))
      .setUnderline(0.2f, -3.5f);
    Phrase titlePhrase = new Phrase(titleChunk);
    titleParagraph.add(titlePhrase);
    
    map.setBorder(Image.BOX);
    map.setBorderColor(Color.BLACK);
    map.setBorderWidth(1.5f);
    map.setAlignment(Image.ALIGN_CENTER);

    doc.open();
    doc.add(titleParagraph);
    doc.add(Chunk.NEWLINE);
    doc.add(Chunk.NEWLINE);
    doc.add(map);
    doc.close();
  }
}
