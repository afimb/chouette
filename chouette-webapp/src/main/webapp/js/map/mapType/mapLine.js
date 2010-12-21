Chouette.Map.init = function(){
  // Call to function init in map.js
  Chouette.Map.initMap();

  //show marker layer
  var showMarkerLayer = Chouette.Map.createShowMarkerLayer();
  Chouette.Map.map.addLayers([showMarkerLayer]);

  var lineId = $("line_idLigne").value ;
  if(lineId != null){
    Chouette.Map.initShowMarkerLayer("../json/JSONLine?lineId="+lineId);
  }
};

window.onload = Chouette.Map.init;