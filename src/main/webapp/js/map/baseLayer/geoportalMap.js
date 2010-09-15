// === INIT MAP PROJECTIONS ===
Chouette.Map.baseLayerProjection = new OpenLayers.Projection("IGNF:GEOPORTALFXX");

Chouette.Map.initBaseLayers = function(){
  //map layer 
  var geoMapLayer= this.createGeoportalBaseLayer("GEOGRAPHICALGRIDSYSTEMS.MAPS", "Plan");
  //orthophoto layer
  var orthoPhotoLayer= this.createGeoportalBaseLayer("ORTHOIMAGERY.ORTHOPHOTOS", "Satellite");

  var railwaysLayer= this.createGeoportalOptionalLayer("TRANSPORTNETWORKS.RAILWAYS", "RailsWays");
  var roadsLayer= this.createGeoportalOptionalLayer("TRANSPORTNETWORKS.ROADS", "Roads");
  var buildingsLayer= this.createGeoportalOptionalLayer("BUILDINGS.BUILDINGS", "Buildings");


   this.map.addLayers([geoMapLayer,orthoPhotoLayer,railwaysLayer,roadsLayer,buildingsLayer]);
};

Chouette.Map.createGeoportalBaseLayer = function(layerType, layerName){
	return this.createGeoportalLayer(layerType, layerName, true, false, true,0);
};

Chouette.Map.createGeoportalOptionalLayer = function(layerType, layerName){
	return this.createGeoportalLayer(layerType, layerName, false, true, false,10);
};

Chouette.Map.createGeoportalLayer = function(layerType, layerName, isBaseLayer, isTransparent, isVisible)
{
  return new Geoportal.Layer.WMSC(
    layerName,
    gGEOPORTALRIGHTSMANAGEMENT[gGEOPORTALRIGHTSMANAGEMENT.apiKey].resources[layerType + ':WMSC'].url,
    {
      layers: layerType,
      exceptions:"text/xml",
      transparent:isTransparent
    },
    {
      gridOrigin: new OpenLayers.LonLat(0,0),
      isBaseLayer: isBaseLayer,
      resolutions: Geoportal.Catalogue.RESOLUTIONS.slice(5,18),
      visibility: isVisible,
      alwaysInRange: true,
      projection: this.baseLayerProjection,
      units: this.baseLayerProjection.getUnits(),
      GeoRM: Geoportal.GeoRMHandler.addKey(
        gGEOPORTALRIGHTSMANAGEMENT.apiKey,
        gGEOPORTALRIGHTSMANAGEMENT[gGEOPORTALRIGHTSMANAGEMENT.apiKey].tokenServer.url,
        gGEOPORTALRIGHTSMANAGEMENT[gGEOPORTALRIGHTSMANAGEMENT.apiKey].tokenServer.ttl,
        this.map
        )
    });
};
