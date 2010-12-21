//////////////////////////////////
// SHOW MARKER LAYER MANAGEMENT //
//////////////////////////////////

Chouette.Map.createShowMarkerLayer = function(){
  var showMarkerSymbolizer = OpenLayers.Util.applyDefaults(
  {
    externalGraphic: "../images/map/${thumbnail}.png",
    pointRadius: 10,
    fillOpacity: 1
  },
  OpenLayers.Feature.Vector.style["default"]);
  
  return this.createMarkerLayer(showMarkerSymbolizer,"Show Marker Layer");
};

Chouette.Map.initShowMarkerLayer = function(url){
  var bounds = new OpenLayers.Bounds();
  var markPoints = new Array();
  var showMarkerLayer = this.map.getLayersByName("Show Marker Layer")[0];
  
  //init highlight control on Show Marker Layer
  var highlightCtrl = new OpenLayers.Control.SelectFeature(showMarkerLayer, {
    hover: true,
    highlightOnly: true,
    renderIntent: "",
    eventListeners: {
      featurehighlighted: this.showTooltipOnEvent,
      featureunhighlighted: this.hideTooltipOnEvent
    }
  });
	
  this.map.addControl(highlightCtrl);
  highlightCtrl.activate();
  
  //get data for Show Marker Layer
  new Ajax.Request(url, {
    method: 'get',
    onSuccess: function(transport) {
      var stopPlaces = eval(transport.responseText);
      if(stopPlaces == null){ //if stopplaces returns null, stoplaces.each won't work...
      	Chouette.Map.zoomToMaxDataExtent();
      }
      stopPlaces.each(function(area){
        if(area.latitude != null && area.longitude != null)
        {
          var showMarkerLayer = Chouette.Map.map.getLayersByName("Show Marker Layer")[0];
          var markPoint = new OpenLayers.Geometry.Point(area.longitude, area.latitude);
          var markPointXY = markPoint.transform(Chouette.Map.wgsProjection,Chouette.Map.baseLayerProjection)

          bounds.extend(markPointXY);
          var mark = new OpenLayers.Feature.Vector(markPointXY, {
            'area':area,
            'thumbnail':area.areaType.toLowerCase()
          });
          markPoints.push(mark.geometry);
          showMarkerLayer.addFeatures([mark]);
        }
      });

      if(bounds.left != null) // If there is markers
      {
        // Hack : reduce zoom to see marker picture
        var zoom = Chouette.Map.map.getZoomForExtent(bounds, true) - 1;
        var point = Chouette.Map.barycentre(markPoints);
        Chouette.Map.map.setCenter(point, zoom);
      }
      else // If there is no markers
      {
        Chouette.Map.zoomToMaxDataExtent();
      }
    }
  });
};

//////////////////////
// POPUP MANAGEMENT //
//////////////////////
Chouette.Map.showTooltipOnEvent = function(event)
{
  var feature = event.feature;
  var text =  "<p class='popup_title'>"+feature.attributes.area.name + "</p>";
  if(feature.attributes.area.streetName != null)
    text += "<p>"+feature.attributes.area.streetName + "</p>";
  if(feature.attributes.area.countryCode != null)
    text +="<p>"+feature.attributes.area.countryCode + "</p>";
  
  var anchoredBubble = OpenLayers.Class(OpenLayers.Popup.AnchoredBubble, {
    'autoSize': true,
    'minSize': new OpenLayers.Size(10,10)
  });
  
  var popup = new anchoredBubble("featurePopup",
    feature.geometry.getBounds().getCenterLonLat(),
    null,
    text,
    null, false, null);
  feature.popup = popup;
  this.map.addPopup(popup);
};

Chouette.Map.hideTooltipOnEvent = function(event)
{
  var feature = event.feature;
  if (feature.popup) {
    this.map.removePopup(feature.popup);
    feature.popup.destroy();
    feature.popup = null;
  }
};
