function init(){
  // Call to function init in map.js
  initMap();

  //show marker layer
  var showMarkerLayer = createShowMarkerLayer();
  map.addLayers([showMarkerLayer]);
  	
  var connectionLinkId = $("connectionLink_idCorrespondance").value ;
  if(connectionLinkId != null){
    initShowMarkerLayer("../json/JSONConnectionLink?connectionLinkId="+connectionLinkId);
  }
}

window.onload = init;