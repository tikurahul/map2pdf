package org.gis.pdf.util;

import java.awt.Color;
import java.io.OutputStream;
import java.net.URL;

import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfWriter;

public class PDFEngine {
	
	protected URL imageUrl;
	protected OutputStream pdfStream;
	protected String pageTitle;
	
	public PDFEngine (URL imageURL, OutputStream pdfStream, String pageTitle){
		this.imageUrl = imageURL;
		this.pdfStream = pdfStream;
		this.pageTitle = pageTitle;
	}
	
	public void createPDF() throws Exception {
		//create a pdf with size A4 and margins of 0.5 inches all around
		Document document = new Document(PageSize.A3, 36, 36, 36, 36);
		PdfWriter.getInstance(document, pdfStream);
		//Adding Metadata
		document.addTitle("ArcGIS PrintToPDF Function - " + pageTitle);
		document.addAuthor("ArcGIS Server REST API - Map Printer");
		//creating title
		Paragraph titleParagraph = new Paragraph();
		titleParagraph.setAlignment(Element.ALIGN_CENTER);
		Chunk titleChunk = new Chunk(pageTitle, new Font(Font.TIMES_ROMAN, 14f, Font.BOLD)).setUnderline(0.2f, -3.5f);
		titleChunk.setAnchor(imageUrl);
		Phrase titlePhrase = new Phrase(titleChunk);
		titleParagraph.add(titlePhrase);
		//creating image
		Image imageToInsert = Image.getInstance(imageUrl);
		imageToInsert.setBorder(Image.BOX);
		imageToInsert.setBorderColor(Color.BLACK);
		imageToInsert.setBorderWidth(1.5f);
		imageToInsert.setAlignment(Image.ALIGN_CENTER);
		//create pdf
		document.open();
		document.add(titleParagraph);
		document.add(Chunk.NEWLINE);
		document.add(Chunk.NEWLINE);
		document.add(imageToInsert);
		document.close();
	}

}
