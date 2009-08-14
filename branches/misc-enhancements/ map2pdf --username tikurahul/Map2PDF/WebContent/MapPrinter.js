﻿dojo.provide("MapPrinter");
dojo.require("dojo.io.iframe");

dojo.declare("MapPrinter", "Object", {
  map : null,
  pdfUrl : null,
  deferredPrint : null,

  constructor : function(/*esri.Map*/map, url) {
    this.map = map;
    this.pdfUrl = url;
  },
  
  _convertUrlToAbsolute: function(url) {
    if (url) {
      if (url.indexOf("http") != 0) {
        var link = document.createElement('a');
        document.body.appendChild(link);
        link.setAttribute("href", url);
        url = link.href;
        document.body.removeChild(link);
      }
    }
    return url;
  },
  
  layersJson : function() {
    var tileUrls = [];
    var dynamicUrls = [];
    
    var extent = this.map.extent;
    var height = this.map.height;
    var width = this.map.width;
    
    dojo.forEach(this.map.layerIds, dojo.hitch(this, function(layerId) {
      var layer = this.map.getLayer(layerId);
      
      if (layer instanceof esri.layers.ArcGISTiledMapServiceLayer) {
        var candidateTileInfo = esri.TileUtils.getCandidateTileInfo(this.map,
            layer.tileInfo, extent);
        var layerTileUrls = [];
        var tileXOffset = Math.ceil(width / layer.tileInfo.width);
        var tileYOffset = Math.ceil(height / layer.tileInfo.height);
        for ( var x = 0; x <= tileXOffset; x++) {
          for ( var y = 0; y <= tileYOffset; y++) {
            var tileUrl = layer.url + "/tile/" + this.map.getLevel() + "/"
                + (candidateTileInfo.tile.coords.row + y) + "/"
                + (candidateTileInfo.tile.coords.col + x);
            layerTileUrls.push( {
              "url" : this._convertUrlToAbsolute(tileUrl),
              "row" : candidateTileInfo.tile.coords.row + y,
              "col" : candidateTileInfo.tile.coords.col + x
            });
          }
        }
        tileUrls.push( {
          "tiles" : layerTileUrls,
          "transparency" : layer.opacity,
          "clipOptions" : {
            "offsetX" : candidateTileInfo.tile.offsets.x
                - this.map._visibleDelta.x,
            "offsetY" : candidateTileInfo.tile.offsets.y
                - this.map._visibleDelta.y,
            "width" : width,
            "height" : height
          }
        });
      } else if (layer instanceof esri.layers.ArcGISDynamicMapServiceLayer) {
        var dynamicUrl = layer.url + "/export?bbox=" + extent.xmin + ","
            + extent.ymin + "," + extent.xmax + "," + extent.ymax
            + "&size=" + width + "," + height
            + "&transparent=true&format=png24&f=image";
        dynamicUrls.push( {
          "url" : this._convertUrlToAbsolute(dynamicUrl),
          "width" : this.map.width,
          "height" : this.map.height,
          "transparency" : layer.opacity
        });
      }
    }));
    
    return dojo.toJson({
      "tileLayers": tileUrls,
      "dynamicLayers": dynamicUrls
    });
  },
  
  featuresJson : function() {
    var features = [];
    
    var extent = this.map.extent;
    var height = this.map.height;
    var width = this.map.width;

    dojo.forEach(this.map.graphicsLayerIds, dojo.hitch(this, function(layerId) {
      var layer = this.map.getLayer(layerId);
      
      if (layer instanceof esri.layers.GraphicsLayer) {
        var geometries = [];
        var colors = [];
        
        var graphics = layer.graphics;
        for ( var i = 0; i < graphics.length; i++) {
          var geometry = esri.geometry.toScreenGeometry(
            extent, width, height, graphics[i].geometry);
          if (geometry instanceof esri.geometry.Polygon) {
            geometries.push({
              "geometryType" : "esriGeometryPolygon",
              "rings" : geometry.rings,
              "symbol" : graphics[i].symbol.toJson()
            });
          }
          else if (geometry instanceof esri.geometry.Point) {
            geometries.push({
              "geometryType" : "esriGeometryPoint",
              "x" : geometry.x,
              "y" : geometry.y,
              "symbol" : graphics[i].symbol.toJson()
            });
          }
          else if (geometry instanceof esri.geometry.Polyline) {
            geometries.push({
              "geometryType" : "esriGeometryPolyline",
              "paths" : geometry.paths,
              "symbol" : graphics[i].symbol.toJson()
            });
          }
          else {
            console.log("Unknown geometry: ", geometry);
          }
          
          colors.push( graphics[i].symbol.color);
        }
        
        features.push({
          "geometries" : geometries,
          "colors" : colors,
          "transparency" : 0.5,
          "width" : width,
          "height" : height
        });
      }
    }));

    return dojo.toJson(features);
  },
  
  generatePdf : function(title, reportname, params) {

    // cancel the previous request. since we're returning pdf data
    // the request never really completes (or at least dojo is unable
    // to detect completion).  if we don't cancel we are only able to 
    // generate a report once...
    if (this.deferredPrint) {
      this.deferredPrint.cancel();
    }
    
    // need to create this temporary form since dojo.io.iframe.send() doesn't
    // honour the POST method without using a form for some reason...
    var form = document.createElement('form');
    dojo.attr(form, 'method', 'POST');
    document.body.appendChild(form);
    
    this.deferredPrint = dojo.io.iframe.send({
      url: this.pdfUrl,
      form: form,
      method: "POST",
      content: { 
        layers : this.layersJson(),
        features : this.featuresJson(),
        f : "pdf",
        pageTitle : title,
        callback : "",
        report : reportname,
        reportParams : params
      },
      error: function(response, ioArgs) {
        // ignore cancellations... (see above for why we need to cancel
        // prior requests)
        if (response.dojoType === "cancel") {
          return response;
        }
        
        console.log("Help!  Blew up!");
        return response;
      }
    });
    
    document.body.removeChild(form);
  }

});