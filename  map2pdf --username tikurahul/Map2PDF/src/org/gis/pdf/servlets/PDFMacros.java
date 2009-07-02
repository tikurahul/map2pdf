package org.gis.pdf.servlets;

import javax.servlet.http.HttpServletRequest;

public class PDFMacros {
  //                // 
  ///Not Type Safe///
  //                //
  @SuppressWarnings("unchecked")
  public static <T> T attr(String attribute, HttpServletRequest request){
    return (T) request.getAttribute(attribute);
  }
  
  public static boolean isEmpty(String str){
	  if(str != null && str.length() > 0)
		  return false;
	  else
		  return true;
  }
}
