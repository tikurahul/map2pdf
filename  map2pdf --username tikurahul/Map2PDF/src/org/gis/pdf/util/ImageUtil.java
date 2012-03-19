package org.gis.pdf.util;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.net.URL;
import java.util.List;
import java.util.logging.Logger;

import javax.imageio.ImageIO;

import org.gis.pdf.data.ClipOptions;
import org.gis.pdf.data.FeatureLayer;
import org.gis.pdf.data.MosaicCollection;
import org.gis.pdf.data.Overlayable;

import com.lowagie.text.pdf.internal.PolylineShape;

public class ImageUtil {
  
  private static final Logger logger = Logger.getLogger(ImageUtil.class.getName());
  
  public BufferedImage readImage(URL url) throws Exception {
    BufferedImage image = null;
    try{
      image = ImageIO.read(url);
    }catch (Exception e){
      throw new Exception("Error reading image (" + url + "), " + e.getMessage(), e);
    }
    return image;
  }
  
  public BufferedImage mosaicImages(MosaicCollection collection) throws Exception {
    BufferedImage mosaic = null;
    BufferedImage clipImage = null;
    BufferedImage [] [] orderedCollection = null;
    //no of images
    int rows = 0; 
    int cols = 0;
    //image properties
    int width = 0;
    int height = 0;
    int imageType = 0;
    //return clip
    boolean returnClip = false;
    try {
      if(collection != null){
        //check if minRow, minCol values are valid
        int minRow = collection.getMinRow(); int minCol = collection.getMinCol(); int maxRow = collection.getMaxRow(); int maxCol = collection.getMaxCol();
        if(minRow == Integer.MAX_VALUE || minCol == Integer.MAX_VALUE || maxRow == Integer.MIN_VALUE || maxCol == Integer.MIN_VALUE){
          throw new Exception("Mosaic Collection invalid.");
        }else {
          //read images
          rows = maxRow - minRow + 1;
          cols = maxCol - minCol + 1;
          orderedCollection = new BufferedImage[rows][cols];
          for(int i=0; i<rows; i++){
            for(int j=0; j<cols; j++){
              orderedCollection[i][j] = readImage(collection.getLayer(minRow + i, minCol + j).getUrl());
            }
          }
          //validate images
          if(orderedCollection != null){
            width = orderedCollection[0][0].getWidth();
            height = orderedCollection[0][0].getHeight();
            imageType = orderedCollection[0][0].getType();
            //writing imageType RGB Alpha
            imageType = imageType == 0 ? BufferedImage.TYPE_INT_ARGB : imageType;
            for(int i=0; i<rows; i++){
              for(int j=0; j<cols; j++){
                if(orderedCollection[i][j].getWidth() != width || orderedCollection[i][j].getHeight() != height){
                  throw new Exception("Images are of different sizes.");
                }
              }
            }
          }
          //validation complete, start mosaic
          mosaic = new BufferedImage(width*cols, height*rows, imageType);
          WritableRaster raster = mosaic.getRaster();
          int xOffset = 0;
          int yOffset = 0;
          for(int i=0; i<rows; i++){
            //reset xOffset
            xOffset = 0;
            for(int j=0; j<cols; j++){
              raster.setRect(xOffset, yOffset, orderedCollection[i][j].getData());
              xOffset += width;
            }
            yOffset += height;
          }
          //mosaic complete, clip based on clip options
          ClipOptions options = collection.getClipOptions();
          if(options != null){
            //clip
            int offsetX = options.getOffsetX();
            int offsetY = options.getOffsetY();
            int nWidth = options.getClipWidth();
            int nHeight = options.getClipHeight();
            if((offsetX + nWidth) > mosaic.getWidth() || (offsetY + nHeight) > mosaic.getHeight()){
              throw new Exception("Invalid Clip Options.");
            }
            clipImage = mosaic.getSubimage(offsetX, offsetY, nWidth, nHeight);
            returnClip = true;
          }
        }
      }
    }catch (Exception e){
      throw e;
    }
    if(returnClip){
      return clipImage;
    }else{
      return mosaic;
    }
  }
  
  public BufferedImage overlayImages(List<Overlayable> overlays) throws Exception{
    BufferedImage image = null;
    try{
        //validate overlayable images
        if(overlays != null && overlays.size() > 0){
          Overlayable ref = overlays.get(0);
          for(int i=1; i<overlays.size(); i++){
            if(!(ref.getWidth() == overlays.get(i).getWidth()) && (ref.getHeight() == overlays.get(i).getHeight())) {
              //overlays not of same size.
              throw new Exception("Images being overlaid are not in same size.");
            }
          }
        //validation complete
        image = new BufferedImage(ref.getWidth(), ref.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = image.createGraphics();
        for(Overlayable layer : overlays){
          graphics.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, layer.getTransparency()));
          //layer.getImage() calls the ImageUtil.readImage(url) on demand instead of allocating everything in memory beforehand.
          graphics.drawImage(layer.getImage(),0,0, null);
        }
        //dispose Graphics
        graphics.dispose();
      }
    }catch (Exception e){
      throw new Exception("Error Overlaying Images, " + e.getMessage(), e);
    }
    return image;
  }
  
  public BufferedImage generateFeatureRepresentation(FeatureLayer layer) throws Exception {
    BufferedImage image = null;
    try{
      image = new BufferedImage(layer.getWidth(), layer.getHeight(), BufferedImage.TYPE_INT_ARGB);
      Graphics2D graphics = image.createGraphics();
      graphics.setRenderingHint(
          RenderingHints.KEY_ANTIALIASING,
          RenderingHints.VALUE_ANTIALIAS_ON);
      graphics.setStroke(new BasicStroke(3.0f));
      List<Shape> shapes = layer.getShapes();
      List<Color> colors = layer.getColors();
      if(shapes != null && colors != null){
        for(int i=0; i<shapes.size(); i++){
          Color c = colors.get(i);
          Shape s = shapes.get(i);
          graphics.setColor(c);
          // ugly hack to prevent the polyline from being filled. 
          // I don't know enough about this to do a better fix for the moment.
          if (!(s instanceof PolylineShape)) {
            graphics.fill(s); 
          }
          graphics.draw(s);
        }
      }
      //dispose graphics
      graphics.dispose();
    } catch (Exception e){
      throw new Exception("Error Generating Feature Images, " + e.getMessage(), e);
    }
    return image;
  }
}
