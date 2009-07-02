package org.gis.pdf.data;

import java.awt.image.BufferedImage;

public abstract class Overlayable {
  //empty interface that signifies overlayable layer
  public abstract BufferedImage getImage();
  
  public abstract float getTransparency();
  
  public abstract int getWidth();
  
  public abstract int getHeight();
  
  public abstract int getType();
}
