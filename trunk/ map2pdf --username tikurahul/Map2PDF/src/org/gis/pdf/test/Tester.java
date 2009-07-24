package org.gis.pdf.test;

import java.awt.Color;
import java.awt.Polygon;
import java.awt.Shape;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import org.gis.pdf.data.ClipOptions;
import org.gis.pdf.data.FeatureLayer;
import org.gis.pdf.data.MosaicCollection;
import org.gis.pdf.data.MosaicLayer;
import org.gis.pdf.data.OverlayLayer;
import org.gis.pdf.data.Overlayable;
import org.gis.pdf.util.ImageUtil;

public class Tester {
 
  public static void main(String[] args) throws Exception {
    testOverlays3();
  }

  private static File getFile() throws IOException {
    File temp = File.createTempFile("map",".png");
    return temp;
  }
  
  static void testOverlays() throws Exception{
    ImageUtil util = new ImageUtil();
    
    OverlayLayer baseLayer = new OverlayLayer(new URL("http://sampleserver1.arcgisonline.com/ArcGIS/rest/services/Portland/ESRI_LandBase_WebMercator/MapServer/tile/9/182/81"), 1.0f );
    OverlayLayer layer2 = new OverlayLayer(new URL("http://sampleserver1.arcgisonline.com/ArcGIS/rest/services/Portland/ESRI_LandBase_WebMercator/MapServer/tile/9/182/80"), 0.5f );
    List<Overlayable> overlays = new ArrayList<Overlayable>();
    overlays.add(baseLayer);
    overlays.add(layer2);
    BufferedImage overlay = util.overlayImages(overlays);
    if(overlay == null)
      System.out.println("Failed Test.");
    else {
      System.out.println("Done.");
      File outputFile = getFile();
      ImageIO.write(overlay, "PNG", outputFile);
    }
  }
  
  static void testMosaicImages() throws Exception{ 
    ImageUtil util = new ImageUtil();
    
    MosaicLayer layer = new MosaicLayer();
    layer.setRow(182);
    layer.setCol(80);
    layer.setUrl(new URL("http://sampleserver1.arcgisonline.com/ArcGIS/rest/services/Portland/ESRI_LandBase_WebMercator/MapServer/tile/9/182/80"));
    
    MosaicLayer layer2 = new MosaicLayer();
    layer2.setRow(182);
    layer2.setCol(81);
    layer2.setUrl(new URL("http://sampleserver1.arcgisonline.com/ArcGIS/rest/services/Portland/ESRI_LandBase_WebMercator/MapServer/tile/9/182/81"));
    
    MosaicLayer layer3 = new MosaicLayer();
    layer3.setRow(183);
    layer3.setCol(80);
    layer3.setUrl(new URL("http://sampleserver1.arcgisonline.com/ArcGIS/rest/services/Portland/ESRI_LandBase_WebMercator/MapServer/tile/9/183/80"));
    
    MosaicLayer layer4 = new MosaicLayer();
    layer4.setRow(183);
    layer4.setCol(81);
    layer4.setUrl(new URL("http://sampleserver1.arcgisonline.com/ArcGIS/rest/services/Portland/ESRI_LandBase_WebMercator/MapServer/tile/9/183/81"));
    
    List<MosaicLayer> layers = new ArrayList<MosaicLayer>();
    layers.add(layer);
    layers.add(layer2);
    layers.add(layer3);
    layers.add(layer4);
    
    ClipOptions clipOptions = new ClipOptions(0,256,256,256);
    
    MosaicCollection collection = new MosaicCollection(layers, 0, clipOptions);
    BufferedImage mosaic = util.mosaicImages(collection);
    if(mosaic == null)
      System.out.println("Failed Test.");
    else {
      System.out.println("Done.");
      File outputFile = getFile();
      ImageIO.write(mosaic, "PNG", outputFile);
    }
  }
  
  static void testOverlays2() throws Exception{
    ImageUtil util = new ImageUtil();
    
    OverlayLayer baseLayer = new OverlayLayer(new URL("http://sampleserver1.arcgisonline.com/ArcGIS/rest/services/Portland/ESRI_LandBase_WebMercator/MapServer/tile/9/182/81"), 1.0f );
    OverlayLayer layer2 = new OverlayLayer(new URL("http://sampleserver1.arcgisonline.com/ArcGIS/rest/services/Portland/ESRI_LandBase_WebMercator/MapServer/tile/9/182/80"), 0.5f );
    Shape [] shapes = new Shape[2];
    shapes[0] = new Polygon(new int[] {50, 100, 150, 100}, new int[] {200, 100, 200, 300}, 4);
    shapes[1] = new Line2D.Double(50, 200, 150, 200);
    List<Shape> listShapes = new ArrayList<Shape>();
    for(Shape shape: shapes){
      listShapes.add(shape);
    }
    Color [] colors = new Color [] {new Color(127, 127, 127), new Color(255, 127, 127)};
    List<Color> listColors = new ArrayList<Color>();
    for(Color color: colors){
      listColors.add(color);
    }
    FeatureLayer layer3 = new FeatureLayer(listShapes, listColors, 0.5f, 512, 512);
    
    List<Overlayable> overlays = new ArrayList<Overlayable>();
    overlays.add(baseLayer);
    overlays.add(layer2);
    overlays.add(layer3);
    
    BufferedImage overlay = util.overlayImages(overlays);
    if(overlay == null)
      System.out.println("Failed Test.");
    else {
      System.out.println("Done.");
      File outputFile = getFile();
      ImageIO.write(overlay, "PNG", outputFile);
    }
  }
  
  static void testFeatureLayer() throws Exception{
    Shape [] shapes = new Shape[2];
    shapes[0] = new Polygon(new int[] {50, 100, 150, 100}, new int[] {200, 100, 200, 300}, 4);
    shapes[1] = new Line2D.Double(50, 200, 150, 200);
    List<Shape> listShapes = new ArrayList<Shape>();
    for(Shape shape: shapes){
      listShapes.add(shape);
    }
    Color [] colors = new Color [] {new Color(127, 127, 127), new Color(255, 127, 127)};
    List<Color> listColors = new ArrayList<Color>();
    for(Color color: colors){
      listColors.add(color);
    }
    FeatureLayer layer = new FeatureLayer(listShapes, listColors, 0.5f, 512, 512);
    BufferedImage featureRep = layer.getImage();
    if(featureRep == null)
      System.out.println("Failed Test.");
    else {
      System.out.println("Done.");
      File outputFile = getFile();
      ImageIO.write(featureRep, "PNG", outputFile);
    }
  }
  
  static void testMosaicImages2() throws Exception{ 
    ImageUtil util = new ImageUtil();
    
    MosaicLayer layer = new MosaicLayer();
    layer.setRow(0);
    layer.setCol(0);
    layer.setUrl(new URL("http://server.arcgisonline.com/ArcGIS/rest/services/ESRI_StreetMap_World_2D/MapServer/tile/0/0/0"));
    
    MosaicLayer layer2 = new MosaicLayer();
    layer2.setRow(0);
    layer2.setCol(1);
    layer2.setUrl(new URL("http://server.arcgisonline.com/ArcGIS/rest/services/ESRI_StreetMap_World_2D/MapServer/tile/0/0/1"));
    
    MosaicLayer layer3 = new MosaicLayer();
    layer3.setRow(1);
    layer3.setCol(0);
    layer3.setUrl(new URL("http://server.arcgisonline.com/ArcGIS/rest/services/ESRI_StreetMap_World_2D/MapServer/tile/0/1/0"));
    
    MosaicLayer layer4 = new MosaicLayer();
    layer4.setRow(1);
    layer4.setCol(1);
    layer4.setUrl(new URL("http://server.arcgisonline.com/ArcGIS/rest/services/ESRI_StreetMap_World_2D/MapServer/tile/0/1/1"));
    
    List<MosaicLayer> layers = new ArrayList<MosaicLayer>();
    layers.add(layer);
    layers.add(layer2);
    layers.add(layer3);
    layers.add(layer4);
    
    ClipOptions clipOptions = new ClipOptions(192,16,640,480);
    
    MosaicCollection collection = new MosaicCollection(layers, 0, clipOptions);
    BufferedImage mosaic = util.mosaicImages(collection);
    if(mosaic == null)
      System.out.println("Failed Test.");
    else {
      System.out.println("Done.");
      File outputFile = getFile();
      ImageIO.write(mosaic, "PNG", outputFile);
    }
  }
  
  static void testMosaicImages3() throws Exception{ 
    ImageUtil util = new ImageUtil();
    
    MosaicLayer layer = new MosaicLayer();
    layer.setRow(0);
    layer.setCol(1);
    layer.setUrl(new URL("http://server.arcgisonline.com/ArcGIS/rest/services/ESRI_StreetMap_World_2D/MapServer/tile/2/0/1"));
    
    MosaicLayer layer2 = new MosaicLayer();
    layer2.setRow(1);
    layer2.setCol(1);
    layer2.setUrl(new URL("http://server.arcgisonline.com/ArcGIS/rest/services/ESRI_StreetMap_World_2D/MapServer/tile/2/1/1"));
    
    MosaicLayer layer3 = new MosaicLayer();
    layer3.setRow(0);
    layer3.setCol(2);
    layer3.setUrl(new URL("http://server.arcgisonline.com/ArcGIS/rest/services/ESRI_StreetMap_World_2D/MapServer/tile/2/0/2"));
    
    MosaicLayer layer4 = new MosaicLayer();
    layer4.setRow(1);
    layer4.setCol(2);
    layer4.setUrl(new URL("http://server.arcgisonline.com/ArcGIS/rest/services/ESRI_StreetMap_World_2D/MapServer/tile/2/1/2"));
    
    MosaicLayer layer5 = new MosaicLayer();
    layer5.setRow(0);
    layer5.setCol(3);
    layer5.setUrl(new URL("http://server.arcgisonline.com/ArcGIS/rest/services/ESRI_StreetMap_World_2D/MapServer/tile/2/0/3"));
    
    MosaicLayer layer6 = new MosaicLayer();
    layer6.setRow(1);
    layer6.setCol(3);
    layer6.setUrl(new URL("http://server.arcgisonline.com/ArcGIS/rest/services/ESRI_StreetMap_World_2D/MapServer/tile/2/1/3"));
    
    List<MosaicLayer> layers = new ArrayList<MosaicLayer>();
    layers.add(layer);
    layers.add(layer2);
    layers.add(layer3);
    layers.add(layer4);
    layers.add(layer5);
    layers.add(layer6);
    
    ClipOptions clipOptions = new ClipOptions(88,310,640,480);
    
    MosaicCollection collection = new MosaicCollection(layers, 0, clipOptions);
    BufferedImage mosaic = util.mosaicImages(collection);
    if(mosaic == null)
      System.out.println("Failed Test.");
    else {
      System.out.println("Done.");
      File outputFile = getFile();
      ImageIO.write(mosaic, "PNG", outputFile);
    }
  }
  
  static void testOverlays3() throws Exception{
    ImageUtil util = new ImageUtil();
    
    MosaicLayer layer = new MosaicLayer();
    layer.setRow(0);
    layer.setCol(1);
    layer.setUrl(new URL("http://server.arcgisonline.com/ArcGIS/rest/services/ESRI_StreetMap_World_2D/MapServer/tile/2/0/1"));
    
    MosaicLayer layer2 = new MosaicLayer();
    layer2.setRow(1);
    layer2.setCol(1);
    layer2.setUrl(new URL("http://server.arcgisonline.com/ArcGIS/rest/services/ESRI_StreetMap_World_2D/MapServer/tile/2/1/1"));
    
    MosaicLayer layer3 = new MosaicLayer();
    layer3.setRow(0);
    layer3.setCol(2);
    layer3.setUrl(new URL("http://server.arcgisonline.com/ArcGIS/rest/services/ESRI_StreetMap_World_2D/MapServer/tile/2/0/2"));
    
    MosaicLayer layer4 = new MosaicLayer();
    layer4.setRow(1);
    layer4.setCol(2);
    layer4.setUrl(new URL("http://server.arcgisonline.com/ArcGIS/rest/services/ESRI_StreetMap_World_2D/MapServer/tile/2/1/2"));
    
    MosaicLayer layer5 = new MosaicLayer();
    layer5.setRow(0);
    layer5.setCol(3);
    layer5.setUrl(new URL("http://server.arcgisonline.com/ArcGIS/rest/services/ESRI_StreetMap_World_2D/MapServer/tile/2/0/3"));
    
    MosaicLayer layer6 = new MosaicLayer();
    layer6.setRow(1);
    layer6.setCol(3);
    layer6.setUrl(new URL("http://server.arcgisonline.com/ArcGIS/rest/services/ESRI_StreetMap_World_2D/MapServer/tile/2/1/3"));
    
    List<MosaicLayer> layers = new ArrayList<MosaicLayer>();
    layers.add(layer);
    layers.add(layer2);
    layers.add(layer3);
    layers.add(layer4);
    layers.add(layer5);
    layers.add(layer6);
    
    ClipOptions clipOptions = new ClipOptions(88,310,640,480);
    
    MosaicCollection oLayer1 = new MosaicCollection(layers, 1f, clipOptions);
    OverlayLayer oLayer2 = new OverlayLayer(new URL("http://sampleserver1.arcgisonline.com/ArcGIS/rest/services/Demographics/ESRI_Census_USA/MapServer/export?bbox=-127.2216796875,20.56640625,-70.9716796875,62.75390625&size=640,480&transparent=true&format=png8&f=image"), 0.5f );
    
    Shape [] shapes = new Shape[2];
    shapes[0] = new Polygon(new int[] {50, 100, 150, 100}, new int[] {200, 100, 200, 300}, 4);
    shapes[1] = new Line2D.Double(50, 200, 150, 200);
    List<Shape> listShapes = new ArrayList<Shape>();
    for(Shape shape: shapes){
      listShapes.add(shape);
    }
    Color [] colors = new Color [] {new Color(127, 127, 127), new Color(255, 127, 127)};
    List<Color> listColors = new ArrayList<Color>();
    for(Color color: colors){
      listColors.add(color);
    }
    
    FeatureLayer oLayer3 = new FeatureLayer(listShapes, listColors, 0.5f, 640, 480);
    
    List<Overlayable> overlays = new ArrayList<Overlayable>();
    
    overlays.add(oLayer1);
    overlays.add(oLayer2);
    overlays.add(oLayer3);
    
    BufferedImage overlay = util.overlayImages(overlays);
    if(overlay == null)
      System.out.println("Failed Test.");
    else {
      System.out.println("Done.");
      File outputFile = getFile();
      ImageIO.write(overlay, "PNG", outputFile);
    }
  }
  
}
