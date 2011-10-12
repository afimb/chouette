Chouette.Map.init = function(){
  // Call to function init in map.js
  Chouette.Map.initMap();
	
  // edit marker layer
  var editMarkerLayer = Chouette.Map.createEditMarkerLayer();

  Chouette.Map.map.addLayers([editMarkerLayer]);
  Chouette.Map.zoomToMaxDataExtent();
  
  Chouette.Map.initEditMarkerLayer("boardingPosition");
  Chouette.Map.updateEditMarker();
  Chouette.Map.zoomOnMarker();
};

window.onload = Chouette.Map.init;