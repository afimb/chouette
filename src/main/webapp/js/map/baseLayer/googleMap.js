// === INIT MAP PROJECTIONS ===
Chouette.Map.baseLayerProjection = new OpenLayers.Projection("EPSG:900913");

OpenLayers.Util.extend(OpenLayers.Lang.en,{
  'google.physical': "Google Physical",
  'google.streets': "Google Streets",
  'google.hybrid':"Google Hybrid",
  'google.satellite':"Google Satellite"});

OpenLayers.Util.extend(OpenLayers.Lang.fr,{
  'google.physical': "Carte Physique",
  'google.streets': "Carte des Rues",
  'google.hybrid':"Carte Hybride",
  'google.satellite':"Carte Satellite"});

Chouette.Map.initBaseLayers = function(){
    var physicalLayer = new OpenLayers.Layer.Google(
        OpenLayers.i18n('google.physical'),
        {type: google.maps.MapTypeId.TERRAIN}
    );
    var streetLayer = new OpenLayers.Layer.Google(
        OpenLayers.i18n('google.streets'), // the default
        {numZoomLevels: 20}
    );
    var hybridLayer = new OpenLayers.Layer.Google(
        OpenLayers.i18n('google.hybrid'),
        {type: google.maps.MapTypeId.HYBRID, numZoomLevels: 20}
    );
    var satelliteLayer = new OpenLayers.Layer.Google(
        OpenLayers.i18n('google.satellite'),
        {type: google.maps.MapTypeId.SATELLITE, numZoomLevels: 22}
    );

  this.map.addLayers([physicalLayer, streetLayer, hybridLayer, satelliteLayer]);
};
