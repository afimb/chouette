Chouette.Map.init = function(){
  // Call to function init in map.js
  Chouette.Map.initMap();

  //show marker layer
  var showMarkerLayer = Chouette.Map.createShowMarkerLayer();
  Chouette.Map.map.addLayers([showMarkerLayer]);
  	
  var connectionLinkId = $("connectionLink_idCorrespondance").value ;
  if(connectionLinkId != null){
    Chouette.Map.initShowMarkerLayer("../json/JSONConnectionLink?connectionLinkId="+connectionLinkId);
  }
};

window.onload = Chouette.Map.init;