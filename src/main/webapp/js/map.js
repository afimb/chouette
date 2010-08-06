//define global vars
var map, drawControls, stopPlaceMarker, editMarkerLayer, showMarkerLayer, childrenAreas;

//define Projection "Lambert II Etendu" 
Proj4js.defs['EPSG:27582'] = "+title=NTF (Paris) / France II (deprecated) +proj=lcc +lat_1=46.8 +lat_0=46.8 +lon_0=0 +k_0=0.99987742 +x_0=600000 +y_0=2200000 +a=6378249.2 +b=6356515 +towgs84=-168,-60,320,0,0,0,0 +pm=paris +units=m";


// === INIT MAP PROJECTIONS ===
var wgsProjection= new OpenLayers.Projection("EPSG:4326");
var lambertProjection= new OpenLayers.Projection("EPSG:27582");
var geoportalProjection= new OpenLayers.Projection("IGNF:GEOPORTALFXX");

function init(){
	
	// === INIT MAP ===
	map = new OpenLayers.Map('map', {
		resolutions: Geoportal.Catalogue.RESOLUTIONS,
		projection: geoportalProjection,
		maxExtent: new OpenLayers.Bounds(-180, -57, 180, 72).transform(wgsProjection,geoportalProjection, true),
		units: geoportalProjection.getUnits(),
		controls:[
			new OpenLayers.Control.PanZoomBar(),
			new OpenLayers.Control.LayerSwitcher({'ascending':false}),
			new OpenLayers.Control.ScaleLine(),
			new OpenLayers.Control.MousePosition(),
			//new OpenLayers.Control.KeyboardDefaults(),
			new OpenLayers.Control.Navigation(),
			new OpenLayers.Control.LoadingPanel()
		]
	});
	
	// === INIT LAYERS ===
	
	//geographic map layer
	var geoMapLayer= new Geoportal.Layer.WMSC(
		"Plan",
		gGEOPORTALRIGHTSMANAGEMENT[gGEOPORTALRIGHTSMANAGEMENT.apiKey].resources['GEOGRAPHICALGRIDSYSTEMS.MAPS:WMSC'].url,
		{
			layers: 'GEOGRAPHICALGRIDSYSTEMS.MAPS',
			format:'image/jpeg',
			exceptions:"text/xml"
		},
		{
			gridOrigin: new OpenLayers.LonLat(0,0),
			isBaseLayer: true,
			resolutions: Geoportal.Catalogue.RESOLUTIONS.slice(5,18),
			alwaysInRange: true,
			projection: geoportalProjection,
			maxExtent: new OpenLayers.Bounds(-180, -57, 180, 72).transform(wgsProjection,geoportalProjection, true),
			units: geoportalProjection.getUnits(),
			GeoRM: Geoportal.GeoRMHandler.addKey(
				gGEOPORTALRIGHTSMANAGEMENT.apiKey,
				gGEOPORTALRIGHTSMANAGEMENT[gGEOPORTALRIGHTSMANAGEMENT.apiKey].tokenServer.url,
				gGEOPORTALRIGHTSMANAGEMENT[gGEOPORTALRIGHTSMANAGEMENT.apiKey].tokenServer.ttl,
				map
			)
		}
	);
	
	// orthophoto layer
	var orthoPhotoLayer= new Geoportal.Layer.WMSC(
		"Satellite",
		gGEOPORTALRIGHTSMANAGEMENT[gGEOPORTALRIGHTSMANAGEMENT.apiKey].resources['ORTHOIMAGERY.ORTHOPHOTOS:WMSC'].url,
		{
			layers: 'ORTHOIMAGERY.ORTHOPHOTOS',
			format:'image/jpeg',
			exceptions:"text/xml"
		},
		{
			gridOrigin: new OpenLayers.LonLat(0,0),
			isBaseLayer: true,
			resolutions: Geoportal.Catalogue.RESOLUTIONS.slice(5,18),
			alwaysInRange: true,
			projection: geoportalProjection,
			maxExtent: new OpenLayers.Bounds(-180, -57, 180, 72).transform(wgsProjection,geoportalProjection, true),
			units: geoportalProjection.getUnits(),
			GeoRM: Geoportal.GeoRMHandler.addKey(
				gGEOPORTALRIGHTSMANAGEMENT.apiKey,
				gGEOPORTALRIGHTSMANAGEMENT[gGEOPORTALRIGHTSMANAGEMENT.apiKey].tokenServer.url,
				gGEOPORTALRIGHTSMANAGEMENT[gGEOPORTALRIGHTSMANAGEMENT.apiKey].tokenServer.ttl,
				map
			)
		}
	);
	
	// edit marker layer
	var editMarkerSymbolizer = OpenLayers.Util.applyDefaults(
        {externalGraphic: "../js/openlayers/img/green_round_marker.png", pointRadius: 10, fillOpacity: 1},
        OpenLayers.Feature.Vector.style["default"]);
	var editMarkerStyleMap = new OpenLayers.StyleMap({"default": editMarkerSymbolizer, "select": {}});
	
	editMarkerLayer = new OpenLayers.Layer.Vector(
		"Edit Marker Layer",
		{
			styleMap: editMarkerStyleMap,
			displayInLayerSwitcher: false
		}
	);
	
	//show marker layer
	var showMarkerSymbolizer = OpenLayers.Util.applyDefaults(
        {externalGraphic: "../js/openlayers/img/marker-blue.png", pointRadius: 20, fillOpacity: 1},
        OpenLayers.Feature.Vector.style["default"]);
	var showMarkerStyleMap = new OpenLayers.StyleMap({"default": showMarkerSymbolizer, "select": {}});
	
	
	showMarkerLayer = new OpenLayers.Layer.Vector(
		"Show Marker Layer",
		{
			styleMap: showMarkerStyleMap,
			displayInLayerSwitcher: false
		}
	);

	map.addLayers([geoMapLayer,orthoPhotoLayer,editMarkerLayer,showMarkerLayer]);
	map.setCenter(new OpenLayers.LonLat(177169.0,5441595.0),10);

	// === INIT EVENTS MANAGEMENT ===

	editMarkerLayer.events.on({
//		"beforefeaturemodified": report,
//		"afterfeaturemodified": report,
//		"sketchmodified": report,
//		"sketchstarted": report,
		"vertexmodified": report,
		"featuremodified": report,
		"sketchcomplete": report
	});
		
	// === INIT CONTROLS ===
	
	var highlightCtrl = new OpenLayers.Control.SelectFeature(showMarkerLayer, { 
		hover: true, highlightOnly: true, renderIntent: "",
		eventListeners: { featurehighlighted: showTooltipOnEvent, featureunhighlighted: hideTooltipOnEvent}
	});
	
	map.addControl(highlightCtrl);
	highlightCtrl.activate();
	
	drawControls = {
		draw: new OpenLayers.Control.DrawFeature(editMarkerLayer,OpenLayers.Handler.Point),
		modify: new OpenLayers.Control.ModifyFeature(editMarkerLayer,{clickout: false})
	};
	
	for(var key in drawControls) {
		map.addControl(drawControls[key]);
	}
	
	initShowMarkerLayer();
	updateStopPlaceMarker();
};


/////////////////////////
// CONTROLS MANAGEMENT //
/////////////////////////

function toggleDrawControl(element) {
	for(key in controls) {
		drawControls[key].deactivate();
	}
	if(element.value != 'none'){
		if(stopPlaceMarker == null){
			drawControls.draw.activate();
		}
		else{
			drawControls.modify.activate();
			drawControls.modify.selectControl.select(editMarkerLayer.features[0]);
		}
	}
};


//////////////////////
// EVENT MANAGEMENT //
//////////////////////

function report(event) {
	//console.log(event.type, event.feature ? event.feature.id : event.components);
	if(event.type == "sketchcomplete"){
		stopPlaceMarker = event.feature;
		updateCoordsFieldsFromMarker(stopPlaceMarker);
		drawControls.draw.deactivate();
		drawControls.modify.activate();
		editMarkerLayer.addFeatures([stopPlaceMarker]);
		drawControls.modify.selectControl.select(stopPlaceMarker);
		//prevents API from adding feature to the layer a second time
		return false;
	}
	else if(event.type == "featuremodified" || event.type == "vertexmodified"){
		updateCoordsFieldsFromMarker();
	}
};

function showTooltipOnEvent(event){
	var feature = event.feature;
	console.log("test event");
	if(feature.renderIntent != "select"){
		map.addPopup(feature.popup);
	}
};

function hideTooltipOnEvent(event){
	var feature = event.feature;
	console.log("youhou");
	map.removePopup(feature.popup);
};


//////////////////////////////////
// STOP PLACE MARKER MANAGEMENT // 
//////////////////////////////////

function updateStopPlaceMarker(){
	if($("stoparea_latitude").value != "" && $("stoparea_longitude").value != ""){
		if(stopPlaceMarker != null){
			var newCoords = new OpenLayers.LonLat($("stoparea_longitude").value,$("stoparea_latitude").value).transform(wgsProjection,geoportalProjection);
			stopPlaceMarker.move(newCoords);
		}
		else{
			stopPlaceMarker = new OpenLayers.Feature.Vector(new OpenLayers.Geometry.Point($("stoparea_longitude").value,$("stoparea_latitude").value).transform(wgsProjection,geoportalProjection));
			drawControls.draw.deactivate();
			drawControls.modify.activate();
			editMarkerLayer.addFeatures([stopPlaceMarker]);
			drawControls.modify.selectControl.select(stopPlaceMarker);
		}
	}
	else{
		if(stopPlaceMarker != null){
			editMarkerLayer.removeFeatures([stopPlaceMarker]);
			stopPlaceMarker = null;
		}
		drawControls.modify.deactivate();
		drawControls.draw.activate();
	}
	
	centerOnMarker();
	
	//prevents API from adding feature to the layer a second time
	return false;
};

function centerOnMarker(){
	if(stopPlaceMarker != null){
		var geom = stopPlaceMarker.geometry;
		var newCoords = new OpenLayers.LonLat(geom.x,geom.y);
		map.panTo(newCoords);
	}
};

function barycentreStopPlaceMarker(){
	var childrenPoints = showMarkerLayer.features.collect(function(feature){
		return feature.geometry;
	});
	var point = barycentre(childrenPoints);
	
	$("stoparea_latitude").value=point.y.toFixed(6);
	$("stoparea_longitude").value=point.x.toFixed(6);
	
	updateXYFieldsCoordsFromLatLon();
	updateStopPlaceMarker();
};

////////////////////////////
// FORM FIELDS MANAGEMENT //
////////////////////////////

function updateCoordsFieldsFromMarker(){
	var point = stopPlaceMarker.geometry.clone();
	
	point.transform(geoportalProjection,wgsProjection);
	$("stoparea_latitude").value=point.y.toFixed(6);
	$("stoparea_longitude").value=point.x.toFixed(6);
	
	point.transform(wgsProjection,lambertProjection);
	$("stoparea_x").value=point.x.toFixed(2);
	$("stoparea_y").value=point.y.toFixed(2);
};

function updateLatLonFieldsCoordsFromXY(){
	var coords = new OpenLayers.LonLat($("stoparea_x").value,$("stoparea_y").value).transform(lambertProjection,wgsProjection);
	$("stoparea_latitude").value=coords.lat.toFixed(6);
	$("stoparea_longitude").value=coords.lon.toFixed(6);
};

function updateXYFieldsCoordsFromLatLon(){
	var coords = new OpenLayers.LonLat($("stoparea_longitude").value,$("stoparea_latitude").value).transform(wgsProjection,lambertProjection);
	$("stoparea_x").value=coords.lon.toFixed(2);
	$("stoparea_y").value=coords.lat.toFixed(2);
};

function updateCoordsFrom(field){
	switch(field){
		case 'x' :
			console.log("x : "+$("stoparea_x").value);
			var x = parseFloat($("stoparea_x").value);
			if(isNaN(x)){
				$("stoparea_x").value = "";
				$("stoparea_longitude").value = "";
			}
			else{
				$("stoparea_x").value = x.toFixed(2);
				if($("stoparea_y").value != ""){
					updateLatLonFieldsCoordsFromXY();
				}
			}
			break;
		case 'y' :
			console.log("y : "+$("stoparea_y").value);
			var y = parseFloat($("stoparea_y").value);
			if(isNaN(y)){
				$("stoparea_y").value = "";
				$("stoparea_latitude").value = "";
			}
			else{
				$("stoparea_y").value = y.toFixed(2);
				if($("stoparea_x").value != ""){
					updateLatLonFieldsCoordsFromXY();
				}
			}
			break;
		case 'lat' :
			console.log("lat : "+$("stoparea_latitude").value);
			var lat = parseFloat($("stoparea_latitude").value);
			if(isNaN(lat)){
				$("stoparea_latitude").value = "";
				$("stoparea_y").value = "";
			}
			else{
				$("stoparea_latitude").value = lat.toFixed(6);
				if($("stoparea_longitude").value != ""){
					updateXYFieldsCoordsFromLatLon();
				}
			}
			break;
		case 'lon' :
			console.log("lon : "+$("stoparea_longitude").value);
			var lon = parseFloat($("stoparea_longitude").value);
			if(isNaN(lon)){
				$("stoparea_longitude").value = "";
				$("stoparea_x").value = "";
			}
			else{
				$("stoparea_longitude").value = lon.toFixed(6);
				if($("stoparea_latitude").value != ""){
					updateXYFieldsCoordsFromLatLon();
				}
			}
			break;
		default :
			alert("error !!!");
			break;
	}
	updateStopPlaceMarker();	
};

//////////////////////////////////
// SHOW MARKER LAYER MANAGEMENT //
//////////////////////////////////

function initShowMarkerLayer(){
	if(	$("stoparea_idPositionGeographique").value != null){
		var url = "../json/JSONStopPlace?stopPlaceId="+$("stoparea_idPositionGeographique").value;
		
		new Ajax.Request(url, {
			method: 'get',
			onSuccess: function(transport) {
				childrenAreas = eval(transport.responseText);
				childrenAreas.each(function(area){
					if(area.latitude != null && area.longitude != null){
						var markPoint = new OpenLayers.Geometry.Point(area.longitude, area.latitude);
						var popup = new OpenLayers.Popup("tooltip", 
							markPoint.getBounds().getCenterLonLat(),
							null,
							"<div class='tooltip'>"+area.name+"</div>",
							false);
						popup.autoSize = true;
						var mark = new OpenLayers.Feature.Vector(markPoint.transform(wgsProjection,geoportalProjection), {'area':area, 'popup' : popup});
						showMarkerLayer.addFeatures([mark]);
					}
				});
			}
		});
	}
};


////////////////
// MATH TOOLS //
////////////////

function barycentre(points){
	var x = 0;
	var y = 0;
	points.each(function(point){
		x+=point.x;
		y+=point.y;
		console.log(x+" "+y);
	});
	x = x/points.length;
	y = y/points.length;
	return new OpenLayers.Geometry.Point(x,y);
};

window.onload = init;