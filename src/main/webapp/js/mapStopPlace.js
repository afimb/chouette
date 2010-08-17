function init(){
  initMap();

  // Add button for barycentre
  var barycentre = new OpenLayers.Control.Button({
    displayClass: "mapBarycentre",
    trigger: barycentreStopPlaceMarker
  });
  var panel = new OpenLayers.Control.Panel();
  panel.addControls([barycentre]);
  var pixel = new OpenLayers.Pixel(15, 260);
  map.addControl(panel, pixel);
  
  // edit marker layer
  var editMarkerLayer = createEditMarkerLayer();

  //show marker layer
  var showMarkerLayer = createShowMarkerLayer();

  map.addLayers([editMarkerLayer,showMarkerLayer]);
  map.setCenter(new OpenLayers.LonLat(177169.0,5441595.0),10);

  initEditMarkerLayer("stoparea");

  var stopPlaceId = $("stoparea_idPositionGeographique").value ;
  if(stopPlaceId != null){
    initShowMarkerLayer("../json/JSONStopPlace?stopPlaceId="+stopPlaceId);
  }
  updateEditMarker();
}

function barycentreStopPlaceMarker()
{
  var showMarkerLayer = map.getLayersByName("Show Marker Layer")[0];
  var childrenPoints = showMarkerLayer.features.collect(function(feature){
    return feature.geometry;
  });
  var point = barycentre(childrenPoints).transform(geoportalProjection, lambertProjection);

  $("stoparea_x").value=point.lon;
  $("stoparea_y").value=point.lat;

  updateLatLonFieldsCoordsFromXY();
  updateEditMarker();
}

window.onload = init;