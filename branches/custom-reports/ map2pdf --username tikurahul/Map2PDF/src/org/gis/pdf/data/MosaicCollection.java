package org.gis.pdf.data;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.gis.pdf.json.JSONArray;
import org.gis.pdf.json.JSONObject;
import org.gis.pdf.util.ImageUtil;

public class MosaicCollection implements Overlayable {
  
  public static final Logger logger = Logger.getLogger(MosaicCollection.class.getName());
  
  protected List<MosaicLayer> layers;
  protected ClipOptions clipOptions;
  protected float transparency;
  protected int minRow;
  protected int maxRow;
  protected int minCol;
  protected int maxCol;
  
  protected BufferedImage mosaic;
  
  public MosaicCollection(List<MosaicLayer> layers, float transparency, ClipOptions clipOptions){
    this.layers = layers;
    this.transparency = transparency;
    this.clipOptions = clipOptions;
    //a little housekeeping
    minRow = Integer.MAX_VALUE;
    minCol = Integer.MAX_VALUE;
    maxRow = Integer.MIN_VALUE;
    maxCol = Integer.MIN_VALUE;
    if(layers != null){
      for(MosaicLayer layer : layers){
        if(minRow >= layer.getRow()){
          minRow = layer.getRow();
        }
        if(minCol >= layer.getCol()){
          minCol = layer.getCol();
        }
        if(maxRow <= layer.getRow()){
          maxRow = layer.getRow();
        }
        if(maxCol <= layer.getCol()){
          maxCol = layer.getCol();
        }
      }
    }
    //mosaic images
    mosaic = null;
    try{
      ImageUtil util = new ImageUtil();
      mosaic =  util.mosaicImages(this);
    }catch (Exception e){
      logger.log(Level.SEVERE, "Error Mosaicing Images, " + e.getMessage());
    }
  }
  
  public List<MosaicLayer> getLayers() {
    return layers;
  }
  public void setLayers(List<MosaicLayer> layers) {
    this.layers = layers;
  }
  
  public float getTransparency() {
    return transparency;
  }
  
  public int getMinRow() {
    return minRow;
  }
  
  public int getMinCol() {
    return minCol;
  }
  
  public int getMaxRow(){
    return maxRow;
  }
 
  public int getMaxCol(){
    return maxCol;
  }
  
  public MosaicLayer getLayer(int row, int col){
    MosaicLayer layer = null;
    if(layers != null){
      for(MosaicLayer l : layers){
        if(l.getRow() == row && l.getCol() == col){
          layer = l;
          break;
        }
      }
    }
    return layer;
  }
   
  public ClipOptions getClipOptions(){
    return clipOptions;
  }

  public BufferedImage getImage(){
    return mosaic;
  }

  public int getHeight() {
    return mosaic != null ? mosaic.getHeight() : 0;
  }

  public int getWidth() {
    return mosaic != null ? mosaic.getWidth() : 0;
  }
  
  public int getType() {
    return mosaic.getType();
  }
  
  public static MosaicCollection fromJson(JSONObject json) throws Exception{
    MosaicCollection collection = null;
    List<MosaicLayer> layers = new ArrayList<MosaicLayer>();
    try{
      JSONArray jlayers = json.getJSONArray("tiles");
      for(int i=0; i<jlayers.length(); i++){
        JSONObject jlayer = jlayers.getJSONObject(i);
        MosaicLayer layer = MosaicLayer.fromJson(jlayer);
        layers.add(layer);
      }
      //color
      float transparency = (float)json.getDouble("transparency");
      //clip options - optional
      ClipOptions options = ClipOptions.fromJson(json.optJSONObject("clipOptions"));
      collection = new MosaicCollection(layers, transparency, options);
    }catch(Exception e){
      logger.log(Level.SEVERE, "Invalid input json, " + e.getMessage());
      throw e;
    }
    return collection;
  }
  
  public static List<MosaicCollection> fromJson(JSONArray json) throws Exception {
	List<MosaicCollection> collections = new ArrayList<MosaicCollection>();
	try{
	 for(int i=0; i<json.length(); i++){
	   MosaicCollection collection = fromJson(json.getJSONObject(i)); 
	   collections.add(collection);
	  }  
	}catch(Exception e){
	  logger.log(Level.SEVERE, "Invalid input json, " + e.getMessage());
	  throw e; 
	}
	return collections;
  }
}