//////////////////////////////////
// SHOW MARKER LAYER MANAGEMENT //
//////////////////////////////////

function createShowMarkerLayer(){
  var showMarkerSymbolizer = OpenLayers.Util.applyDefaults(
    {
      externalGraphic: "../js/openlayers/img/${thumbnail}.png",
      graphicYOffset: -38,
      pointRadius: 20,
      fillOpacity: 1
    },
    OpenLayers.Feature.Vector.style["default"]);
  
  return createMarkerLayer(showMarkerSymbolizer,"Show Marker Layer");
};

function initShowMarkerLayer(url){
  var bounds = new OpenLayers.Bounds();
  var markPoints = new Array();
  var showMarkerLayer = map.getLayersByName("Show Marker Layer")[0];
  
  //init highlight control on Show Marker Layer
  var highlightCtrl = new OpenLayers.Control.SelectFeature(showMarkerLayer, {
    hover: true,
    highlightOnly: true,
    renderIntent: "",
    eventListeners: {
      featurehighlighted: showTooltipOnEvent,
      featureunhighlighted: hideTooltipOnEvent
    }
  });
	
  map.addControl(highlightCtrl);
  highlightCtrl.activate();
  
  //get data for Show Marker Layer
  new Ajax.Request(url, {
    method: 'get',
    onSuccess: function(transport) {
      var stopPlaces = eval(transport.responseText);
      stopPlaces.each(function(area){
        if(area.latitude != null && area.longitude != null)
        {
          var markPoint = new OpenLayers.Geometry.Point(area.longitude, area.latitude);
          var markPointXY = markPoint.transform(wgsProjection,geoportalProjection)

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
        var zoom = map.getZoomForExtent(bounds, true) - 1;
        var point = barycentre(markPoints);
        map.setCenter(point, zoom);
      }
      else // If there is no markers
      {
        map.setCenter(new OpenLayers.LonLat(177169.0,5441595.0),20);
      }
    }
  });
};

//////////////////////
// POPUP MANAGEMENT //
//////////////////////
function showTooltipOnEvent(event)
{
  var feature = event.feature;
  var text =  "<h2>"+feature.attributes.area.name + "</h2>";
  if(feature.attributes.area.streetName != null)
    text += "<p>"+feature.attributes.area.streetName + "</p>";
  if(feature.attributes.area.countryCode != null)
    text +="<p>"+feature.attributes.area.countryCode + "</p>";

  var popup = new OpenLayers.Popup.FramedCloud("featurePopup",
    feature.geometry.getBounds().getCenterLonLat(),
    new OpenLayers.Size(100,100),
    text,
    null, true, onPopupClose);
  feature.popup = popup;
  popup.feature = feature;
  map.addPopup(popup);
};

function hideTooltipOnEvent(event)
{
  var feature = event.feature;
  if (feature.popup) {
    popup.feature = null;
    map.removePopup(feature.popup);
    feature.popup.destroy();
    feature.popup = null;
  }
};

function onPopupClose(event)
{
  // 'this' is the popup.
  selectControl.unselect(this.feature);
};

