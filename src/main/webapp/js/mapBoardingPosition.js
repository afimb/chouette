Chouette.Map.init = function(){
  Chouette.Map.initMap();
	
  // edit marker layer
  var editMarkerLayer = Chouette.Map.createEditMarkerLayer();

  Chouette.Map.map.addLayers([editMarkerLayer]);
  Chouette.Map.map.setCenter(new OpenLayers.LonLat(177169.0,5441595.0),10);

  Chouette.Map.initEditMarkerLayer("boardingPosition");
	
  Chouette.Map.updateEditMarker();
}

window.onload = Chouette.Map.init;