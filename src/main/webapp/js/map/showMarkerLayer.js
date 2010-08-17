//////////////////////////////////
// SHOW MARKER LAYER MANAGEMENT //
//////////////////////////////////

//define global vars
var showMarkerLayer;

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
}
