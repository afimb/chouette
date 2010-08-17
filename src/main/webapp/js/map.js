//define global vars
var map;

//define Projection "Lambert II Etendu" 
Proj4js.defs['EPSG:27582'] = "+title=NTF (Paris) / France II (deprecated) +proj=lcc +lat_1=46.8 +lat_0=46.8 +lon_0=0 +k_0=0.99987742 +x_0=600000 +y_0=2200000 +a=6378249.2 +b=6356515 +towgs84=-168,-60,320,0,0,0,0 +pm=paris +units=m";


// === INIT MAP PROJECTIONS ===
var wgsProjection= new OpenLayers.Projection("EPSG:4326");
var lambertProjection= new OpenLayers.Projection("EPSG:27582");
var geoportalProjection= new OpenLayers.Projection("IGNF:GEOPORTALFXX");

function initMap(){
  var mapBounds = new OpenLayers.Bounds(-6, 41.3, 10, 51.6).transform(wgsProjection,geoportalProjection, true);
  // === INIT MAP ===
  map = new OpenLayers.Map('map', {
    resolutions: Geoportal.Catalogue.RESOLUTIONS,
    projection: geoportalProjection,
    maxExtent: mapBounds,
    restrictedExtent: mapBounds,
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
      units: geoportalProjection.getUnits(),
      GeoRM: Geoportal.GeoRMHandler.addKey(
        gGEOPORTALRIGHTSMANAGEMENT.apiKey,
        gGEOPORTALRIGHTSMANAGEMENT[gGEOPORTALRIGHTSMANAGEMENT.apiKey].tokenServer.url,
        gGEOPORTALRIGHTSMANAGEMENT[gGEOPORTALRIGHTSMANAGEMENT.apiKey].tokenServer.ttl,
        map
        )
    });
};


function createMarkerLayer(symbolizer,layerName){  
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


function createShowMarkerLayer(){
  var showMarkerSymbolizer = OpenLayers.Util.applyDefaults(
    {
      externalGraphic: "../js/openlayers/img/${thumbnail}.png",
      graphicYOffset: -38,
      pointRadius: 20,
      fillOpacity: 1
    },
    OpenLayers.Feature.Vector.style["default"]);
  
  return createMarkerLayer(showMarkerSymbolizer,"Show Marker Layer");
};


function createEditMarkerLayer(){
  var editMarkerSymbolizer = OpenLayers.Util.applyDefaults(
    {
      externalGraphic: "../js/openlayers/img/green_round_marker.png",
      pointRadius: 10,
      fillOpacity: 1
    },
    OpenLayers.Feature.Vector.style["default"]);
  
  return createMarkerLayer(editMarkerSymbolizer,"Show Marker Layer");
};


//////////////////////////////////
// SHOW MARKER LAYER MANAGEMENT //
//////////////////////////////////

function initShowMarkerLayer(url){
  var bounds = new OpenLayers.Bounds();
  var markPoints = new Array();
    
  new Ajax.Request(url, {
    method: 'get',
    onSuccess: function(transport) {
      var stopPlaces = eval(transport.responseText);
      stopPlaces.each(function(area){
        if(area.latitude != null && area.longitude != null)
        {
          var markPoint = new OpenLayers.Geometry.Point(area.longitude, area.latitude);
          var markPointXY = markPoint.transform(wgsProjection,geoportalProjection)

          bounds.extend(markPointXY);
          var mark = new OpenLayers.Feature.Vector(markPointXY, {
            'area':area,
            'thumbnail':area.areaType.toLowerCase()
          });
          markPoints.push(mark.geometry);
          showMarkerLayer.addFeatures([mark]);
        }
      });

      if(bounds.left != null) // If there is markers
      {
        // Hack : reduce zoom to see marker picture
        var zoom = map.getZoomForExtent(bounds, true) - 1;
        var point = barycentre(markPoints);
        map.setCenter(point, zoom);
      }
      else // If there is no markers
      {
        map.setCenter(new OpenLayers.LonLat(177169.0,5441595.0),20);
      }
    }
  });
}

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