//define global vars
var map;

//define Projection "Lambert II Etendu" 
Proj4js.defs['EPSG:27582'] = "+title=NTF (Paris) / France II (deprecated) +proj=lcc +lat_1=46.8 +lat_0=46.8 +lon_0=0 +k_0=0.99987742 +x_0=600000 +y_0=2200000 +a=6378249.2 +b=6356515 +towgs84=-168,-60,320,0,0,0,0 +pm=paris +units=m";


// === INIT MAP PROJECTIONS ===
var wgsProjection= new OpenLayers.Projection("EPSG:4326");
var lambertProjection= new OpenLayers.Projection("EPSG:27582");
var geoportalProjection= new OpenLayers.Projection("IGNF:GEOPORTALFXX");

function initMap(){

  // === INIT MAP ===
  map = new OpenLayers.Map('map', {
    resolutions: Geoportal.Catalogue.RESOLUTIONS,
    projection: geoportalProjection,
    maxExtent: new OpenLayers.Bounds(-180, -57, 180, 72).transform(wgsProjection,geoportalProjection, true),
    units: geoportalProjection.getUnits(),
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
  var geoMapLayer= createGeoportalLayer("GEOGRAPHICALGRIDSYSTEMS.MAPS", "Plan");
  // orthophoto layer
  var orthoPhotoLayer= createGeoportalLayer("ORTHOIMAGERY.ORTHOPHOTOS", "Satellite");

   map.addLayers([geoMapLayer,orthoPhotoLayer]);
}

function createGeoportalLayer(layerType, layerName)
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
      projection: geoportalProjection,
      //maxExtent: new OpenLayers.Bounds(-180, -57, 180, 72).transform(wgsProjection,geoportalProjection, true),
      units: geoportalProjection.getUnits(),
      GeoRM: Geoportal.GeoRMHandler.addKey(
        gGEOPORTALRIGHTSMANAGEMENT.apiKey,
        gGEOPORTALRIGHTSMANAGEMENT[gGEOPORTALRIGHTSMANAGEMENT.apiKey].tokenServer.url,
        gGEOPORTALRIGHTSMANAGEMENT[gGEOPORTALRIGHTSMANAGEMENT.apiKey].tokenServer.ttl,
        map
        )
    });
};

function createShowMarkerLayer(){
  var showMarkerSymbolizer = OpenLayers.Util.applyDefaults(
  {
    externalGraphic: "../js/openlayers/img/marker-blue.png",
    pointRadius: 20,
    fillOpacity: 1
  },
  OpenLayers.Feature.Vector.style["default"]);
  
  var showMarkerStyleMap = new OpenLayers.StyleMap({
    "default": showMarkerSymbolizer,
    "select": {}
  });


  return new OpenLayers.Layer.Vector(
    "Show Marker Layer",
    {
      styleMap: showMarkerStyleMap,
      displayInLayerSwitcher: false
    });
};

////////////////
// MATH TOOLS //
////////////////
function barycentre(points)
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
}