package org.gis.pdf.data;

import java.awt.Color;
import java.awt.Polygon;
import java.awt.Shape;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import org.gis.pdf.json.JSONArray;
import org.gis.pdf.json.JSONObject;
import org.gis.pdf.util.ImageUtil;

import com.lowagie.text.pdf.internal.PolylineShape;

public class FeatureLayer implements Overlayable {
  
  //shapes
  protected List<Shape> shapes;
  //image properties
  protected BufferedImage image;
  protected int width;
  protected int height;
  //symbology
  List<Color> colors;
  float transparency;
  
  public FeatureLayer(List<Shape> shapes, List<Color> colors, float transparency, int width, int height) throws Exception{
    ImageUtil util = new ImageUtil();
    this.shapes = shapes;
    this.width = width;
    this.height = height;
    this.transparency = transparency;
    this.colors = colors;
    //based on the shapes generate the buffered image
    try{
      image = util.generateFeatureRepresentation(this);
    }catch(Exception e){
      throw new Exception("Error generating Feature Image", e);
    }
  }
  

  public BufferedImage getImage() {
    return image;
  }
  

  public int getWidth() {
    return width;
  }
  

  public int getHeight() {
    return height;
  }
  

  public float getTransparency() {
    return transparency;
  }
  
  public List<Color> getColors() {
    return colors;
  }
  
  public List<Shape> getShapes(){
    return shapes;
  }
  
  //geometries supported are points and polygons (simple with one ring)
  public static FeatureLayer fromJson(JSONObject json) throws Exception{
    FeatureLayer layer = null;
    List<Shape> shapes = new ArrayList<Shape>();
    List<Color> colors = new ArrayList<Color>();
    try {
      JSONArray jshapes = json.getJSONArray("geometries");
      JSONArray jcolors = json.getJSONArray("colors");
      if(jshapes == null || jcolors == null || !(jshapes.length() == jcolors.length())) {
        throw new Exception("Invalid Input.");
      }
      for(int i=0; i<jshapes.length(); i++) {
        //get geometry info
        JSONObject jshape = jshapes.getJSONObject(i);
        String geometryType = jshape.getString("geometryType");
        if("esriGeometryPoint".equalsIgnoreCase(geometryType)) {
          double x = jshape.getDouble("x");
          double y = jshape.getDouble("y");
          Line2D.Double point = new Line2D.Double(x, y, x, y);
          shapes.add(point);
        } else if("esriGeometryPolygon".equalsIgnoreCase(geometryType)) {
          JSONArray rings = jshape.getJSONArray("rings");
          if(rings.length() > 0) {
            //take the first ring
            JSONArray fRing = rings.getJSONArray(0);
            int noPoints = fRing.length();
            int [] xcoords = new int[noPoints];
            int [] ycoords = new int[noPoints];
            for(int j=0; j<noPoints; j++){
              JSONArray point = fRing.getJSONArray(j);
              if(point.length() == 2){
                xcoords[j] = Math.round((float) point.getDouble(0));
                ycoords[j] = Math.round((float)point.getDouble(1));
              } else {
                throw new Exception("Invalid Input Geometry: Malformed point.");
              }
            }
            Polygon polygon = new Polygon(xcoords, ycoords, noPoints);
            shapes.add(polygon);
          } else {
            throw new Exception("Invalid Input Geometry: Multiple rings.");
          }
        } else if ("esriGeometryPolyline".equalsIgnoreCase(geometryType)) {
          
          JSONArray paths = jshape.getJSONArray("paths");
          for (int j = 0; j < paths.length(); j++) 
          {
            JSONArray path = paths.getJSONArray(j);
            int numPoints = path.length();
            int xcoords[] = new int[numPoints];
            int ycoords[] = new int[numPoints];
            for (int k = 0; k < numPoints; k++) 
            {
              JSONArray point = path.getJSONArray(k);
              if (point.length() == 2) {
                xcoords[k] = Math.round((float)point.getDouble(0));
                ycoords[k] = Math.round((float)point.getDouble(1));
              } else {
                throw new Exception("Invalid Input Geometry: Malformed point.");
              }
            }
            // an internal iText class... probably should find an alternative...
            PolylineShape polyline = new PolylineShape(xcoords, ycoords, numPoints);
            shapes.add(polyline);
          }
          
        } else {
          throw new Exception("Unsupported feature type.");
        }
        //get color info
        JSONObject jcolor = jcolors.getJSONObject(i);
        int r = jcolor.getInt("r"); int g = jcolor.getInt("g"); int b = jcolor.getInt("b");
        Color c = new Color(r, g, b);
        colors.add(c);
      }
      float transparency = (float) json.getDouble("transparency");
      int width = json.getInt("width");
      int height = json.getInt("height");
      layer = new FeatureLayer(shapes, colors,transparency, width, height);
    }catch(Exception e){
      throw new Exception("Invalid input json, " + e.getMessage(), e);
    }
    return layer;
  }
  
  public static List<FeatureLayer> fromJson(JSONArray json) throws Exception{
    List<FeatureLayer> layers = new ArrayList<FeatureLayer>();
    try{
      for(int i=0; i<json.length(); i++){
        FeatureLayer layer = fromJson(json.getJSONObject(i));
        layers.add(layer);
      }
    }catch(Exception e){
      throw new Exception("Invalid input json, " + e.getMessage(), e);
    }
    return layers;
  }

  public int getType() {
    return image.getType();
  }
  
}
