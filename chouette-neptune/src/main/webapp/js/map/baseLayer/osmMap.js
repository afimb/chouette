// === INIT MAP PROJECTIONS ===
Chouette.Map.baseLayerProjection = new OpenLayers.Projection("EPSG:900913");
Chouette.Map.wgsProjection = new OpenLayers.Projection("EPSG:4326");

Chouette.Map.mapBounds = new OpenLayers.Bounds(-179.999, -89.999, 179.999, 89.999).transform(Chouette.Map.wgsProjection,Chouette.Map.baseLayerProjection, true);


Chouette.Map.initBaseLayers = function(){
  var osmLayer= new OpenLayers.Layer.OSM();

  this.map.addLayers([osmLayer]);
};
