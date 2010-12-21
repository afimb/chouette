//define Projection "Lambert II Etendu" 

//Lambert I
Proj4js.defs['EPSG:27561'] = "+proj=lcc +lat_1=49.50000000000001 +lat_0=49.50000000000001 +lon_0=0 +k_0=0.999877341 +x_0=600000 +y_0=200000 +a=6378249.2 +b=6356515 +towgs84=-168,-60,320,0,0,0,0 +pm=paris +units=m +no_defs ";

//Lambert II
Proj4js.defs['EPSG:27562'] = "+proj=lcc +lat_1=46.8 +lat_0=46.8 +lon_0=0 +k_0=0.99987742 +x_0=600000 +y_0=200000 +a=6378249.2 +b=6356515 +towgs84=-168,-60,320,0,0,0,0 +pm=paris +units=m +no_defs ";

//Lambert III
Proj4js.defs['EPSG:27563'] = "+proj=lcc +lat_1=44.10000000000001 +lat_0=44.10000000000001 +lon_0=0 +k_0=0.999877499 +x_0=600000 +y_0=200000 +a=6378249.2 +b=6356515 +towgs84=-168,-60,320,0,0,0,0 +pm=paris +units=m +no_defs ";
 
//Lambert IV
Proj4js.defs['EPSG:27564'] = "+proj=lcc +lat_1=42.16500000000001 +lat_0=42.16500000000001 +lon_0=0 +k_0=0.99994471 +x_0=234.358 +y_0=185861.369 +a=6378249.2 +b=6356515 +towgs84=-168,-60,320,0,0,0,0 +pm=paris +units=m +no_defs ";

//Lambert II etendu
Proj4js.defs['EPSG:27572'] = "+proj=lcc +lat_1=46.8 +lat_0=46.8 +lon_0=0 +k_0=0.99987742 +x_0=600000 +y_0=2200000 +a=6378249.2 +b=6356515 +towgs84=-168,-60,320,0,0,0,0 +pm=paris +units=m +no_defs";

//RGF93 / Lambert93
Proj4js.defs['EPSG:2154'] = "+proj=lcc +lat_1=49 +lat_2=44 +lat_0=46.5 +lon_0=3 +x_0=700000 +y_0=6600000 +ellps=GRS80 +towgs84=0,0,0,0,0,0,0 +units=m +no_defs ";
 
//define global vars
Chouette.Map.map = null ;

// === INIT MAP PROJECTIONS ===
Chouette.Map.wgsProjection = new OpenLayers.Projection("EPSG:4326");
Chouette.Map.lambertProjection = new OpenLayers.Projection("EPSG:"+$("lambertSRID").value);

Chouette.Map.initMap = function(){
  OpenLayers.Lang.setCode($("currentLocale").value);
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
    new OpenLayers.Control.Attribution(),
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

Chouette.Map.zoomToMaxDataExtent = function(){
  if($('minLat').value != ""){
    var bounds = new OpenLayers.Bounds($('minLong').value,$('minLat').value,$('maxLong').value,$('maxLat').value).transform(this.wgsProjection,this.baseLayerProjection, true);
    var zoom = this.map.getZoomForExtent(bounds, true) - 1;
    var point = bounds.getCenterLonLat();
    this.map.setCenter(point, zoom);
  }
  else{
    this.map.zoomToMaxExtent();
  }
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
	});
	x = x/points.length;
	y = y/points.length;
	return new OpenLayers.LonLat(x,y);
};
