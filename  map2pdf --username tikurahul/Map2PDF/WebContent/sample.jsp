<%@ page language="java" contentType="text/html" pageEncoding="UTF-8" session="false"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
  <title>Print Map as PDF</title>
  <meta http-equiv=”X-UA-Compatible” content=”IE=EmulateIE7″ />  
  <link rel="stylesheet" type="text/css" href="http://serverapi.arcgisonline.com/jsapi/arcgis/1.4/js/dojo/dijit/themes/tundra/tundra.css" />
  <style type="text/css">
    @import "<%=request.getContextPath()%>/sample.css";
  </style>
  <script type="text/javascript">
    var djConfig = { 
      parseOnLoad: true,
      dojoBlankHtmlUrl: "<%=request.getContextPath()%>/dojo/resources/blank.html" 
    };
  </script>
  <script type="text/javascript" src="http://serverapi.arcgisonline.com/jsapi/arcgis/?v=1.4"></script>
  <script type="text/javascript" src="MapPrinter.js"></script>
  <script type="text/javascript">
    dojo.require("esri.map");
    dojo.require("dijit.form.Button");
        
    dojo.addOnLoad(function() {
                
      var map = new esri.Map("mapDiv");
          
      var tiledLayer = new esri.layers.ArcGISTiledMapServiceLayer("http://server.arcgisonline.com/ArcGIS/rest/services/ESRI_StreetMap_World_2D/MapServer");
      var dynamicLayer = new esri.layers.ArcGISDynamicMapServiceLayer("http://sampleserver1.arcgisonline.com/ArcGIS/rest/services/Demographics/ESRI_Census_USA/MapServer", { "opacity": 0.5 });

      map.addLayer(tiledLayer);
      map.addLayer(dynamicLayer);

      // add a couple of graphics layers...
      doCountyQuery(map);
      doRoadQuery(map);
          
      var mapPrinter = new MapPrinter(map, "<%=request.getContextPath()%>/pdf");

      var print = function() {
        var title = dojo.byId("reportTitle").value;
        var report = dojo.byId("report").value;
        var params = dojo.byId("reportParams").value;
        mapPrinter.generatePdf(title, report, params);
      };
          
      new dijit.form.Button({ label: "Generate PDF", onClick: print }, "printButton");
    });

    // for testing rendering of polygons...
    function doCountyQuery(map) {
      //Query all counties in Kansas
      var countyQueryTask = new esri.tasks.QueryTask("http://sampleserver1.arcgisonline.com/ArcGIS/rest/services/Demographics/ESRI_Census_USA/MapServer/3");
      var countyQuery = new esri.tasks.Query();
      countyQuery.outFields = ["*"];
      countyQuery.returnGeometry = true;
      countyQuery.outSpatialReference = map.spatialReference;
      countyQuery.where = "STATE_NAME = 'Kansas'";
      countyQueryTask.execute(countyQuery, function(featureSet) {
        var symbol = new esri.symbol.SimpleFillSymbol();
        symbol.setColor(new dojo.Color([150,0,150,0.5]));
        var countyLayer = new esri.layers.GraphicsLayer();
        map.addLayer(countyLayer);

        //Add counties to the graphics layer
        dojo.forEach(featureSet.features, function(feature) {
          countyLayer.add(feature.setSymbol(symbol));
        });
      });
    }

    // for testing rendering of polylines...
    function doRoadQuery(map) {
      //Query a road or two...
      var roadQueryTask = new esri.tasks.QueryTask("http://sampleserver1.arcgisonline.com/ArcGIS/rest/services/Specialty/ESRI_StateCityHighway_USA/MapServer/0");
      var roadQuery = new esri.tasks.Query();
      roadQuery.outFields = ["*"];
      roadQuery.returnGeometry = true;
      roadQuery.outSpatialReference = map.spatialReference;
      roadQuery.where = "OBJECTID = 1 OR OBJECTID = 2";
      roadQueryTask.execute(roadQuery, function(featureSet) {
        var symbol = new esri.symbol.SimpleLineSymbol();
        symbol.setColor(new dojo.Color([255,0,0,1.0]));
        var roadLayer = new esri.layers.GraphicsLayer();
        map.addLayer(roadLayer);

        //Add roads to the graphics layer
        dojo.forEach(featureSet.features, function(feature) {
          roadLayer.add(feature.setSymbol(symbol));
        });
      });
    }
  </script>
</head>
<body class="tundra">
  <h2>Generate a PDF from a Map</h2>
    
  <div id="mapDiv"></div>
  <br /> 
  <table class="pdf">
    <col style="width: 200px"/>
    <col style="width: 440px"/>
    <tr>  
      <td>Title:</td>
      <td><input id="reportTitle" type="text" name="pageTitle" value=""/></td>
    </tr>
    <tr>
      <td>Report Name: <i>(Optional)</i></td>
      <td><input id="report" type="text" name="report" value=""/></td>
    </tr>
    <tr>
      <td>Report Parameters: <i>(Optional)</i></td>
      <td>
        <textarea id="reportParams" style="width:100%" name="reportParams" rows="3" cols="20" ></textarea>
      </td>
    </tr>
    <tr>
      <td><button id="printButton"></button>
      </td>
    </tr>
  </table>
</body>
</html>
