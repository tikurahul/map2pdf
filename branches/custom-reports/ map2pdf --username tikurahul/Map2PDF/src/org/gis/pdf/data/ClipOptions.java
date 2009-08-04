package org.gis.pdf.data;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.gis.pdf.json.JSONObject;

public class ClipOptions {
  
  public static final Logger logger = Logger.getLogger(ClipOptions.class.getName());
  
  protected int offsetX;
  protected int offsetY;
  protected int clipWidth;
  protected int clipHeight;
  
  public ClipOptions(int offsetX, int offsetY, int clipWidth, int clipHeight){
    this.offsetX = offsetX;
    this.offsetY = offsetY;
    this.clipWidth = clipWidth;
    this.clipHeight = clipHeight;
  }
  public int getOffsetX() {
    return offsetX;
  }
  public int getOffsetY() {
    return offsetY;
  }
  public int getClipWidth() {
    return clipWidth;
  }
  public int getClipHeight() {
    return clipHeight;
  }
  
  public static ClipOptions fromJson(JSONObject json) throws Exception{
    ClipOptions options = null;
    try{
      int offsetX = json.getInt("offsetX");
      int offsetY = json.getInt("offsetY");
      int clipWidth = json.getInt("width");
      int clipHeight = json.getInt("height");
      options = new ClipOptions(offsetX, offsetY, clipWidth, clipHeight);
    }catch (Exception e){
      logger.log(Level.SEVERE, "Invalid input json, " + e.getMessage());
      throw e;
    }
    return options;
  }
}
