// === INIT MAP PROJECTIONS ===
Chouette.Map.baseLayerProjection = new OpenLayers.Projection("IGNF:GEOPORTALFXX");

OpenLayers.Util.extend(OpenLayers.Lang.en,{
  'geoportal.maps':"Geoportal Maps",
  'geoportal.orthophotos':"Geoportal Orthophotos",
  'geoportal.railways':"Railways",
  'geoportal.roads':"Roads",
  'geoportal.buildings':"Buildings"
  });

OpenLayers.Util.extend(OpenLayers.Lang.fr,{
  'geoportal.maps':"Cartes Géoportail",
  'geoportal.orthophotos':"Orthophotos Géoportail",
  'geoportal.railways':"Voies Ferrées",
  'geoportal.roads':"Routes",
  'geoportal.buildings':"Bâtiments"
  });
  
Chouette.Map.initBaseLayers = function(){
  //map layer 
  var geoMapLayer= this.createGeoportalBaseLayer("GEOGRAPHICALGRIDSYSTEMS.MAPS", OpenLayers.i18n('geoportal.maps'));
  //orthophoto layer
  var orthoPhotoLayer= this.createGeoportalBaseLayer("ORTHOIMAGERY.ORTHOPHOTOS", OpenLayers.i18n('geoportal.orthophotos'));

  var railwaysLayer= this.createGeoportalOptionalLayer("TRANSPORTNETWORKS.RAILWAYS", OpenLayers.i18n('geoportal.railways'));
  var roadsLayer= this.createGeoportalOptionalLayer("TRANSPORTNETWORKS.ROADS", OpenLayers.i18n('geoportal.roads'));
  var buildingsLayer= this.createGeoportalOptionalLayer("BUILDINGS.BUILDINGS", OpenLayers.i18n('geoportal.buildings'));


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
      attribution : "Data by Geoportal",
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
