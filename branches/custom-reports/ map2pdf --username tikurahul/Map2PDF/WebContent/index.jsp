<%@ page language="java" contentType="text/html" pageEncoding="UTF-8" session="false"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <title>Print Map as PDF</title>
    <link rel="stylesheet" type="text/css" href="http://serverapi.arcgisonline.com/jsapi/arcgis/1.4/js/dojo/dijit/themes/tundra/tundra.css" />
    <style type="text/css">
		@import "<%=request.getContextPath()%>/pdf.css";
	</style>
    <script type="text/javascript" src="http://serverapi.arcgisonline.com/jsapi/arcgis/?v=1.4"></script>
    <script type="text/javascript">
    	var djConfig = {
		  parseOnLoad:true
    	};
    </script>
    <script type="text/javascript" src="PrintMap.js"></script>
    <script type="text/javascript">
        dojo.require("esri.map");
        dojo.require("dijit.layout.BorderContainer");
        dojo.require("dijit.layout.ContentPane");
        dojo.addOnLoad(function() {
            console.debug("Init Complete.");
            var map = new esri.Map("mapDiv");
            //adding a tiled layer basemap
            var tiledLayer = new esri.layers.ArcGISTiledMapServiceLayer("http://server.arcgisonline.com/ArcGIS/rest/services/ESRI_StreetMap_World_2D/MapServer");
            map.addLayer(tiledLayer);
            //adding a dynamic layer
            var dynamicLayer = new esri.layers.ArcGISDynamicMapServiceLayer("http://sampleserver1.arcgisonline.com/ArcGIS/rest/services/Demographics/ESRI_Census_USA/MapServer", { "opacity": 0.5 });
            map.addLayer(dynamicLayer);
            //adding graphics complete
            var mapState = dojo.byId("layers");
            var printMap = new PrintMap(map, mapState);
            dojo.connect(map, "onExtentChange", dojo.hitch(printMap, printMap.updateExtent));
            //adding features
        });
    </script>
</head>
<body class="tundra">
	<div dojotype="dijit.layout.BorderContainer" design="headline" gutters="false" style="width: 96%; height: 95%; margin: 2%; border=0px;">
		<p>
			<h2>Map2PDF</h2>
			Look at <a href="<%=request.getContextPath() %>/help.html">help</a> on how to use this service. 
		</p>
		<div id="mapDiv" style="width:640px; height:480px; border:1px solid #000;margin-left: auto;margin-right: auto" dojotype="dijit.layout.ContentPane"></div> 
		<br />
        <form id="printMapForm" action="<%=request.getContextPath()%>/pdf" method="post">
            <div class="pdf" style="margin-left: auto; margin-right: auto">
                <table border="0px">
                    <tr>
                        <td><b>Layers : </b></td>
                        <td><textarea id="layers" name="layers" rows="10" cols="100" ></textarea></td>
                    </tr>
                    <tr>
                        <td width="20px" colspan="2">&nbsp;</td>
                    </tr>
                    <tr>
                        <td><b>Features : </b><i>(Optional)</i></td>
                        <td><textarea id="features" name="features" rows="10" cols="100" ></textarea></td>
                    </tr>
                    <tr>
                        <td><b>Format : </b></td>
                        <td>
                            <select name="f">
                            	<option value="pdf">PDF</option>
                               	<option value="pjson">JSON</option>
                                	<option value="image">IMAGE</option>
                           	</select>
                       	</td>
                   	</tr>
                    <tr>	
                    	<td><b> PDF Title : </b></td>
                    	<td><input type="text" name="pageTitle" value=""/></td>
                    </tr>
                     <tr>
                    	<td><b> Callback : </b> <i> (Optional) </i></td>
                    	<td><input type="text" name="callback" value=""/></td>
                    </tr>
                </table>
                <br />
                <table border="0px">
                    <tr>
                        <td><input type="submit" value="Print Map to PDF"/></td>
                    </tr>
                </table>
           	</div>
       	</form>
	</div>
</body>
</html>
