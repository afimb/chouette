var editMarkerLayer;

function createEditMarkerLayer(){
  var editMarkerSymbolizer = OpenLayers.Util.applyDefaults(
    {
      externalGraphic: "../js/openlayers/img/green_round_marker.png",
      pointRadius: 10,
      fillOpacity: 1
    },
    OpenLayers.Feature.Vector.style["default"]);
  
  return createMarkerLayer(editMarkerSymbolizer,"Show Marker Layer");
};


