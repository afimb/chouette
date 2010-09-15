// === INIT MAP PROJECTIONS ===
Chouette.Map.baseLayerProjection = new OpenLayers.Projection("IGNF:GEOPORTALFXX");

Chouette.Map.initBaseLayers = function(){
  var geoMapLayer= this.createGeoportalLayer("GEOGRAPHICALGRIDSYSTEMS.MAPS", "Plan");
  // orthophoto layer
  var orthoPhotoLayer= this.createGeoportalLayer("ORTHOIMAGERY.ORTHOPHOTOS", "Satellite");

   this.map.addLayers([geoMapLayer,orthoPhotoLayer]);
};

Chouette.Map.createGeoportalLayer = function(layerType, layerName)
{
  return new Geoportal.Layer.WMSC(
    layerName,
    gGEOPORTALRIGHTSMANAGEMENT[gGEOPORTALRIGHTSMANAGEMENT.apiKey].resources[layerType + ':WMSC'].url,
    {
      layers: layerType,
      format:'image/jpeg',
      exceptions:"text/xml"
    },
    {
      gridOrigin: new OpenLayers.LonLat(0,0),
      isBaseLayer: true,
      resolutions: Geoportal.Catalogue.RESOLUTIONS.slice(5,18),
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
