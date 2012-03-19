package org.gis.pdf.data;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Logger;

import org.gis.pdf.json.JSONException;
import org.gis.pdf.json.JSONObject;

public class MosaicLayer {
  
  private static final Logger logger = Logger.getLogger(MosaicLayer.class.getName());
  
  protected URL url;
  protected int row;
  protected int col;
   
  public URL getUrl() {
   return url;
  }
  public void setUrl(URL url) {
    this.url = url;
  }
  public int getRow() {
    return row;
  }
  public void setRow(int row) {
    this.row = row;
  }
  public int getCol() {
    return col;
  }
  public void setCol(int col) {
    this.col = col;
  }
  
  public static MosaicLayer fromJson(JSONObject json) throws JSONException{
    MosaicLayer layer = null;
   try {
       layer = new MosaicLayer();
       layer.setUrl(new URL(json.getString("url")));
       layer.setRow(json.getInt("row"));
       layer.setCol(json.getInt("col"));
   } catch (MalformedURLException e) {
     throw (JSONException)new JSONException("Invalid image url, " + e.getMessage()).initCause(e);
   }
   return layer;
  }
}