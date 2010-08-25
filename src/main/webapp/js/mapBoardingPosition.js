Chouette.Map.init = function(){
  Chouette.Map.initMap();
	
  // edit marker layer
  var editMarkerLayer = Chouette.Map.createEditMarkerLayer();

  Chouette.Map.map.addLayers([editMarkerLayer]);
  Chouette.Map.map.zoomToMaxExtent();
  
  Chouette.Map.initEditMarkerLayer("boardingPosition");
  Chouette.Map.updateEditMarker();
  Chouette.Map.zoomOnMarker();
}

window.onload = Chouette.Map.init;