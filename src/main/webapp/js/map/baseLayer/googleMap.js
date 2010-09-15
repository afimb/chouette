// === INIT MAP PROJECTIONS ===
Chouette.Map.baseLayerProjection = new OpenLayers.Projection("EPSG:900913");

Chouette.Map.initBaseLayers = function(){
    var physicalLayer = new OpenLayers.Layer.Google(
        "Google Physical",
        {type: google.maps.MapTypeId.TERRAIN}
    );
    var streetLayer = new OpenLayers.Layer.Google(
        "Google Streets", // the default
        {numZoomLevels: 20}
    );
    var hybridLayer = new OpenLayers.Layer.Google(
        "Google Hybrid",
        {type: google.maps.MapTypeId.HYBRID, numZoomLevels: 20}
    );
    var satelliteLayer = new OpenLayers.Layer.Google(
        "Google Satellite",
        {type: google.maps.MapTypeId.SATELLITE, numZoomLevels: 22}
    );

  this.map.addLayers([physicalLayer, streetLayer, hybridLayer, satelliteLayer]);
};
