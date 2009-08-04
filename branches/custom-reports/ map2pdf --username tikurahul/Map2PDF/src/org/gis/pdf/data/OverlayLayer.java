package org.gis.pdf.data;

import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.gis.pdf.json.JSONArray;
import org.gis.pdf.json.JSONObject;
import org.gis.pdf.util.ImageUtil;

public class OverlayLayer implements Overlayable {
  
  public static final Logger logger = Logger.getLogger(OverlayLayer.class.getName());
  
    protected URL url;
    protected BufferedImage image;
    protected float transparency;
    
    public OverlayLayer(URL imageUrl, float transparency){
      this.url = imageUrl;
      this.transparency = transparency;
      //a little housekeeping
      image = null;
      try{
        ImageUtil util = new ImageUtil();
        image = util.readImage(url);
      }catch(Exception e){
        logger.log(Level.SEVERE, "Error reading image, " + e.getMessage());
      }
    }
    
    public URL getImageUrl(){
      return url;
    }
    
    public float getTransparency(){
      return transparency;
    }

    public BufferedImage getImage(){
      return image; 
    }

    public int getHeight() {
      return image != null ? image.getHeight() : 0;
    }

    public int getWidth() {
      return image != null ? image.getWidth() : 0;
    }
    
    public static OverlayLayer fromJson(JSONObject json) throws Exception{
      OverlayLayer layer = null;
      try{
        String url = json.getString("url");
        float transparency = (float) json.getDouble("transparency");
        layer = new OverlayLayer(new URL(url), transparency);
      }catch (Exception e){
        logger.log(Level.SEVERE, "Invalid input json, " + e.getMessage());
        throw e;
      }
      return layer;
    }
    
    public static List<OverlayLayer> fromJson(JSONArray json) throws Exception {
      List<OverlayLayer> layers = new ArrayList<OverlayLayer>();
        try{
    	  for(int i=0; i<json.length(); i++){
    	    OverlayLayer layer = fromJson(json.getJSONObject(i));
    		layers.add(layer);
    	  }	
    	}catch (Exception e){
    	  logger.log(Level.SEVERE, "Invalid input json, " + e.getMessage());
          throw e;
    	}
      return layers;
    }
    
    public int getType() {
      return image.getType();
    }
}
