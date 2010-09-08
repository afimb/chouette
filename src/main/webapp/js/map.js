//define Projection "Lambert II Etendu" 
Proj4js.defs['EPSG:27582'] = "+title=NTF (Paris) / France II (deprecated) +proj=lcc +lat_1=46.8 +lat_0=46.8 +lon_0=0 +k_0=0.99987742 +x_0=600000 +y_0=2200000 +a=6378249.2 +b=6356515 +towgs84=-168,-60,320,0,0,0,0 +pm=paris +units=m";

//define global vars
Chouette.Map.map = null ;


// === INIT MAP PROJECTIONS ===
Chouette.Map.wgsProjection = new OpenLayers.Projection("EPSG:4326");
Chouette.Map.lambertProjection = new OpenLayers.Projection("EPSG:27582");

Chouette.Map.initMap = function(){
  OpenLayers.ImgPath = "../images/map/"
  var mapBounds = new OpenLayers.Bounds(-6, 41.3, 10, 51.6).transform(this.wgsProjection,this.baseLayerProjection, true);
  // === INIT MAP ===
  this.map = new OpenLayers.Map('map', {
    projection: this.baseLayerProjection,
    maxExtent: mapBounds,
    restrictedExtent: mapBounds,
    units: this.baseLayerProjection.getUnits(),
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
    ],
    theme : null
  });

  // === INIT LAYERS ===
  Chouette.Map.initBaseLayers();
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
