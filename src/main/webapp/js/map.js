var Chouette = {};
Chouette.Map = {};

//define Projection "Lambert II Etendu" 
Proj4js.defs['EPSG:27582'] = "+title=NTF (Paris) / France II (deprecated) +proj=lcc +lat_1=46.8 +lat_0=46.8 +lon_0=0 +k_0=0.99987742 +x_0=600000 +y_0=2200000 +a=6378249.2 +b=6356515 +towgs84=-168,-60,320,0,0,0,0 +pm=paris +units=m";

//define global vars
Chouette.Map.map = null ;



// === INIT MAP PROJECTIONS ===
Chouette.Map.wgsProjection = new OpenLayers.Projection("EPSG:4326");
Chouette.Map.lambertProjection = new OpenLayers.Projection("EPSG:27582");
Chouette.Map.geoportalProjection = new OpenLayers.Projection("IGNF:GEOPORTALFXX");

Chouette.Map.initMap = function(){
  var mapBounds = new OpenLayers.Bounds(-6, 41.3, 10, 51.6).transform(this.wgsProjection,this.geoportalProjection, true);
  // === INIT MAP ===
  this.map = new OpenLayers.Map('map', {
    resolutions: Geoportal.Catalogue.RESOLUTIONS,
    projection: this.geoportalProjection,
    maxExtent: mapBounds,
    restrictedExtent: mapBounds,
    units: this.geoportalProjection.getUnits(),
    controls:[
    new OpenLayers.Control.PanZoomBar(),
    new OpenLayers.Control.LayerSwitcher({
      'ascending':false
    }),
    new OpenLayers.Control.ScaleLine(),
    new OpenLayers.Control.MousePosition(),
    //new OpenLayers.Control.KeyboardDefaults(),
    new OpenLayers.Control.Navigation(),
    new OpenLayers.Control.LoadingPanel()
    ]
  });

  // === INIT LAYERS ===
  //geographic map layer
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
      projection: this.geoportalProjection,
      units: this.geoportalProjection.getUnits(),
      GeoRM: Geoportal.GeoRMHandler.addKey(
        gGEOPORTALRIGHTSMANAGEMENT.apiKey,
        gGEOPORTALRIGHTSMANAGEMENT[gGEOPORTALRIGHTSMANAGEMENT.apiKey].tokenServer.url,
        gGEOPORTALRIGHTSMANAGEMENT[gGEOPORTALRIGHTSMANAGEMENT.apiKey].tokenServer.ttl,
        this.map
        )
    });
};


Chouette.Map.createMarkerLayer = function(symbolizer,layerName){  
  var styleMap = new OpenLayers.StyleMap({
    "default": symbolizer,
    "select": {}
  });

  return new OpenLayers.Layer.Vector(
    layerName,
    {
      styleMap: styleMap,
      displayInLayerSwitcher: false
    });	
};


////////////////
// MATH TOOLS //
////////////////
Chouette.Map.barycentre = function(points)
{
	var x = 0;
	var y = 0;
	points.each(function(point){
		x+=point.x;
		y+=point.y;
		console.log(x+" "+y);
	});
	x = x/points.length;
	y = y/points.length;
	return new OpenLayers.LonLat(x,y);
};
