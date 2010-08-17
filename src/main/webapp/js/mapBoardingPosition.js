function init(){
  initMap();
	
  // edit marker layer
  var editMarkerLayer = createEditMarkerLayer();

  map.addLayers([editMarkerLayer]);
  map.setCenter(new OpenLayers.LonLat(177169.0,5441595.0),10);

  initEditMarkerLayer("boardingPosition");
	
  updateEditMarker();
}

window.onload = init;