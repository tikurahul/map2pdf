package org.gis.pdf.data;

import java.awt.image.BufferedImage;

public interface Overlayable {
  //empty class that signifies overlayable layer
  public BufferedImage getImage();
  
  public float getTransparency();
  
  public int getWidth();
  
  public int getHeight();
  
  public int getType();
}
