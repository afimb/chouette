// === INIT MAP PROJECTIONS ===
Chouette.Map.baseLayerProjection = new OpenLayers.Projection("EPSG:900913");

Chouette.Map.initBaseLayers = function(){
  var osmLayer= new OpenLayers.Layer.OSM();

  this.map.addLayers([osmLayer]);
};
