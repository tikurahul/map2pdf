package org.gis.pdf.servlets;

public enum Format {
   
  PDF("pdf"), JSON("json"), PJSON("pjson"), IMAGE("image");
   
   private final String format;
  
   Format(String format){
     this.format = format;
   }

   public String toString(){
     return format;
   }
   
   public static boolean isValidFormat(String format){
     boolean isValid = false;
     if(PDF.toString().equalsIgnoreCase(format) || JSON.toString().equalsIgnoreCase(format) || 
        PJSON.toString().equalsIgnoreCase(format) || IMAGE.toString().equalsIgnoreCase(format)){
       isValid = true;
     }
     return isValid;
   }
   
   public static Format fromString(String format){
	   //Format -> upper case
	   format = format.toUpperCase();
	   if(format == null || !isValidFormat(format))
		   return Format.PDF;
	   return Format.valueOf(format);
   }
   
}
