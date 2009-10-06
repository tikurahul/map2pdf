package org.gis.pdf.servlets;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.gis.pdf.data.FeatureLayer;
import org.gis.pdf.data.MosaicCollection;
import org.gis.pdf.data.OverlayLayer;
import org.gis.pdf.data.Overlayable;
import org.gis.pdf.json.JSONArray;
import org.gis.pdf.json.JSONException;
import org.gis.pdf.json.JSONObject;
import org.gis.pdf.report.ReportConfig;
import org.gis.pdf.util.ImageUtil;
import org.gis.pdf.util.PDFEngine;

public class PDFServlet extends HttpServlet {
  
  private static final long serialVersionUID = 1L;

  private static final Logger logger = Logger.getLogger(PDFServlet.class.getName());
  
  private String path= null;
  private ReportConfig reportConf;
  
  public void init() throws ServletException {
    super.init();
    path = this.getServletContext().getRealPath("/");
    initReports();
  }
  
  public String getPath() {
    return path;
  }
  
  private void initReports() throws ServletException
  {
    String conf = getServletConfig().getInitParameter("report-config");
    InputStream inp = getServletContext().getResourceAsStream(conf);
    try { 
      reportConf = new ReportConfig(getServletContext().getResource(conf)); 
    }
    catch (MalformedURLException e) {
      throw new ServletException("Invalid report configuration.", e);
    }
  }
  
  protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    List<String> errors = new ArrayList<String> ();
    OutputStream stream = null;
    PrintWriter writer = null;
    //check request format
    String f = PDFMacros.param("f", request);
    Format format = Format.fromString(f);
    //check format
    if(!Format.isValidFormat(f)){
      errors.add("Invalid Request format.");
      request.setAttribute("errors", errors);
      this.getServletContext().getRequestDispatcher("/errors.jsp").forward(request, response);
    }
    
    try{
      //get tileLayers and dynamicLayers from 'rasters'
      
      //tileUrls
      List<MosaicCollection> mLayers = new ArrayList<MosaicCollection>();
      //dynamicUrls
      List<OverlayLayer> oLayers = new ArrayList<OverlayLayer>();
      
      String jRasters = request.getParameter("layers");
      if(!PDFMacros.isEmpty(jRasters)){
      //read all tileLayers and dynamicLayers
      try{
        JSONObject rasters = new JSONObject(jRasters);
        mLayers = MosaicCollection.fromJson(rasters.getJSONArray("tileLayers"));
        oLayers = OverlayLayer.fromJson(rasters.getJSONArray("dynamicLayers"));
      }catch(Exception e){
        String message = "Invalid input json 'layers': " + e.getMessage();
        throw new Exception(message, e);
      }
      }else {
        String message = "No Layers Specified.";
        errors.add(message);
        throw new Exception(message);
      }
      
      //get featureLayers (optional)
      List<FeatureLayer> fLayers = new ArrayList<FeatureLayer>();
      
      String jVectors = request.getParameter("features");
      if(!PDFMacros.isEmpty(jVectors)){
        try{
          JSONArray vectors = new JSONArray(jVectors);
          fLayers = FeatureLayer.fromJson(vectors);
        }catch(Exception e){
          String message = "No Features Specified.";
          errors.add(message);
          throw new Exception(message, e);
        }
      }
      
      //begin creating PDF
      List<Overlayable> overlayLayers = new ArrayList<Overlayable>();
      for(int i=0; i<mLayers.size(); i++){
        overlayLayers.add(mLayers.get(i));
      }
      for(int i=0; i<oLayers.size(); i++){
        overlayLayers.add(oLayers.get(i));
      }
      for(int i=0; i<fLayers.size(); i++){
        overlayLayers.add(fLayers.get(i));
      }
      
      //Begin Overlay
      ImageUtil util = new ImageUtil();
      BufferedImage image = util.overlayImages(overlayLayers);
      
      String pageTitle = request.getParameter("pageTitle");
      pageTitle = PDFMacros.isEmpty(pageTitle) ? "Map2PDF" : pageTitle;
      
      if(format == Format.IMAGE){
        //return image
        response.setContentType("image/png");
        stream = response.getOutputStream();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "PNG", baos);
        stream.write(baos.toByteArray());
        return;
      } else {
        //Generate PDF        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        
        PDFEngine engine = new PDFEngine(
            image, 
            baos, 
            pageTitle, 
            reportConf.getReportGenerator(request.getParameter("report")),
            determineReportParameters(request));
        engine.createPDF();

        if(format == Format.PDF){
          response.setContentType("application/pdf");
          response.setHeader("Content-Disposition", "attachment;filename=map.pdf");
          stream = response.getOutputStream();
          stream.write(baos.toByteArray());
          return;
        } else {
          //JSON / PJSON
          
          // Write out png / pdf files to disk...
          UUID imageId = UUID.randomUUID();
          ImageIO.write(image, "PNG", new File(path + "/images/" + imageId.toString() + ".png"));

          FileOutputStream fos = new FileOutputStream(path + "/pdf/" + imageId.toString() + ".pdf");
          fos.write(baos.toByteArray());
          fos.close();

          // Figure out the appropriate URLs...
          String requestUrl = 
            request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + "/" + request.getContextPath();
          
          String imageUrl = requestUrl + "/images/" + imageId.toString() + ".png";
          String pdfUrl = requestUrl + "/pdf/" + imageId.toString() + ".pdf";
          
          // Generate the response...
          response.setContentType("text/plain");
          String callback = request.getParameter("callback");
          writer = response.getWriter();
          JSONObject pdfJson = new JSONObject();
          pdfJson.put("pdfUrl", pdfUrl);
          pdfJson.put("imageUrl", imageUrl);
          if(format == Format.JSON){
            if(PDFMacros.isEmpty(callback)){
              writer.write(pdfJson.toString());
            }else {
              writer.write(callback + "(" + pdfJson.toString() + ");");
            }
          } else {
            if(PDFMacros.isEmpty(callback)){
              writer.write(pdfJson.toString(2));
            }else {
              writer.write(callback + "(" + pdfJson.toString(2) + ");");
            }
          }
          return;
        }
      }
    }catch (Exception e){
      logger.log(Level.SEVERE, e.getMessage(), e);
      if(!(format == Format.JSON || format == Format.PJSON)){
        //redirect to error.jsp
        errors.add(e.getMessage());
        request.setAttribute("errors", errors);
        this.getServletContext().getRequestDispatcher("/errors.jsp").forward(request, response);
        return;
      }
      else {
        //return errors as json
        response.setContentType("text/plain");
        writer = response.getWriter();
        JSONArray jerrors = new JSONArray(errors);
        String callback = request.getParameter("callback");
        if(format == Format.JSON){
          if(PDFMacros.isEmpty(callback)){
            writer.write(jerrors.toString());
          }else {
            writer.write(callback + "(" + jerrors.toString() + ");");
          }
        } else {
          if(PDFMacros.isEmpty(callback)){
            try {
              writer.write(jerrors.toString(2));
            } catch (JSONException jsonException) {
              //ignore
            }
          }else {
            try {
              writer.write(callback + "(" + jerrors.toString(2) + ");");
            } catch (JSONException ex) {
              //ignore
            }
          }
        }
      }
    } finally {
      if(stream != null){
        stream.close();
      }
      if(writer != null){
        writer.close();
      }
    }
  }
  
  private Map<String, String> determineReportParameters(HttpServletRequest request) throws Exception {
    Map<String, String> params = new HashMap<String, String>();
    String reqParams = request.getParameter("reportParams");
    if (!PDFMacros.isEmpty(reqParams)) {
      JSONObject json = new JSONObject(reqParams);
      Iterator<?> keys = json.keys();
      while (keys.hasNext()) {
        String key = (String) keys.next();
        String value = (String) json.get(key);
        params.put(key, value);
      }
    }
    return params;
  }
}
