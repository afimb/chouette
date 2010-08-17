function init(){
  // Call to function init in map.js
  initMap();

  //show marker layer
  var showMarkerLayer = createShowMarkerLayer();
  map.addLayers([showMarkerLayer]);

  var lineId = $("line_idLigne").value ;
  if(lineId != null){
    initShowMarkerLayer("../json/JSONLine?lineId="+lineId);
  }
}

window.onload = init;