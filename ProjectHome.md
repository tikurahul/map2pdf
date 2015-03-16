A lot of people have been wanting to print web maps authored with the ArcGIS JavaScript API. There have also been numerous requests for the ability to print web maps as a PDF. I wrote a simple RESTful service called Map2PDF, that exposes the ability to print web maps as PDF. Check out this sample, that uses the Map2PDF RESTful web service.

There is support for Tiled Map services, Dynamic Map Services as well as Graphic Features (limited support). The sample also includes a PrintMap.js script, that helps developers serialize the 'map state' in a format that the Map2PDF service expects.

The web service uses the Java Advanced Imaging API for mosaicing and overlay of images. Graphic Features are rendered on the server side using java.awt.Graphics2D. I have used the iText PDF engine to generate the PDF's. For source code check the ArcGIS Server Blog (coming soon).

For more help on how to effectively use Map2PDF check out the API <a href='http://orthogonal.esri.com/Map2PDF/help.html'>documentation</a> and the <a href='http://orthogonal.esri.com/Map2PDF/'>services directory page</a>.

<a href='http://map2pdf.googlecode.com/files/Map2PDF.war'>Download</a> the latest version here.