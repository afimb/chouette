var map, drawControls, stopPlaceMarker, vectorLayer;

Proj4js.defs['EPSG:27582'] = "+title=NTF (Paris) / France II (deprecated) +proj=lcc +lat_1=46.8 +lat_0=46.8 +lon_0=0 +k_0=0.99987742 +x_0=600000 +y_0=2200000 +a=6378249.2 +b=6356515 +towgs84=-168,-60,320,0,0,0,0 +pm=paris +units=m";

var wgsProjection= new OpenLayers.Projection("EPSG:4326");
var lambertProjection= new OpenLayers.Projection("EPSG:27582");
var geoportalProjection= new OpenLayers.Projection("IGNF:GEOPORTALFXX");

function init(){
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
			resolutions: Geoportal.Catalogue.RESOLUTIONS.slice(5,15),
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
			resolutions: Geoportal.Catalogue.RESOLUTIONS.slice(5,15),
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
	
	vectorLayer = new OpenLayers.Layer.Vector("Vector Layer");

	OpenLayers.Feature.Vector.style['default']['strokeWidth'] = '2';
	
	vectorLayer.events.on({
//		"beforefeaturemodified": report,
//		"afterfeaturemodified": report,
//		"sketchmodified": report,
//		"sketchstarted": report,
		"vertexmodified": report,
		"featuremodified": report,
		"sketchcomplete": report
	});
	
	map.addLayers([geoMapLayer,orthoPhotoLayer,vectorLayer]);
	map.setCenter(new OpenLayers.LonLat(177169.0,5441595.0),10);
	
	
	drawControls = {
		draw: new OpenLayers.Control.DrawFeature(vectorLayer,OpenLayers.Handler.Point),
		modify: new OpenLayers.Control.ModifyFeature(vectorLayer,{clickout: false})
	};
	
	for(var key in drawControls) {
		map.addControl(drawControls[key]);
	}
	
	updateStopPlaceMarker();
};

function report(event) {
	//console.log(event.type, event.feature ? event.feature.id : event.components);
	if(event.type == "sketchcomplete"){
		stopPlaceMarker = event.feature;
		updateCoordsFields(stopPlaceMarker);
		drawControls.draw.deactivate();
		drawControls.modify.activate();
		vectorLayer.addFeatures([stopPlaceMarker]);
		drawControls.modify.selectControl.select(stopPlaceMarker);
		//prevents API from adding feature to the layer a second time
		return false;
	}
	else if(event.type == "featuremodified" || event.type == "vertexmodified"){
		updateCoordsFields();
	}
};

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
			drawControls.modify.selectControl.select(vectorLayer.features[0]);
		}
	}
};

function updateStopPlaceMarker(){
	if($("edit_latitude").value != "" && $("edit_longitude").value != ""){
		if(stopPlaceMarker != null){
			var newCoords = new OpenLayers.LonLat($("edit_longitude").value,$("edit_latitude").value).transform(wgsProjection,geoportalProjection);
			stopPlaceMarker.move(newCoords);
		}
		else{
			stopPlaceMarker = new OpenLayers.Feature.Vector(new OpenLayers.Geometry.Point($("edit_longitude").value,$("edit_latitude").value).transform(wgsProjection,geoportalProjection));
			drawControls.draw.deactivate();
			drawControls.modify.activate();
			vectorLayer.addFeatures([stopPlaceMarker]);
			drawControls.modify.selectControl.select(stopPlaceMarker);
		}
	}
	else{
		if(stopPlaceMarker != null){
			vectorLayer.removeFeatures([stopPlaceMarker]);
			stopPlaceMarker = null;
		}
		drawControls.modify.deactivate();
		drawControls.draw.activate();
	}
	
	centerOnMarker();
	
	//prevents API from adding feature to the layer a second time
	return false;
};

function updateCoordsFields(){
	var point = stopPlaceMarker.geometry.clone();
	
	point.transform(geoportalProjection,wgsProjection);
	$("edit_latitude").value=point.y.toFixed(6);
	$("edit_longitude").value=point.x.toFixed(6);
	
	point.transform(wgsProjection,lambertProjection);
	$("edit_x").value=point.x.toFixed(2);
	$("edit_y").value=point.y.toFixed(2);
};

function updateLatLngFieldsCoords(){
	var coords = new OpenLayers.LonLat($("edit_x").value,$("edit_y").value).transform(lambertProjection,wgsProjection);
	$("edit_latitude").value=coords.lat.toFixed(6);
	$("edit_longitude").value=coords.lon.toFixed(6);
};

function updateXYFieldsCoords(){
	var coords = new OpenLayers.LonLat($("edit_longitude").value,$("edit_latitude").value).transform(wgsProjection,lambertProjection);
	$("edit_x").value=coords.lon.toFixed(2);
	$("edit_y").value=coords.lat.toFixed(2);
};

function updateCoordsFrom(field){
	switch(field){
		case 'x' :
			console.log("x : "+$("edit_x").value);
			var x = parseFloat($("edit_x").value);
			if(isNaN(x)){
				$("edit_x").value = "";
				$("edit_longitude").value = "";
			}
			else{
				$("edit_x").value = x.toFixed(2);
				if($("edit_y").value != ""){
					updateLatLngFieldsCoords();
				}
			}
			break;
		case 'y' :
			console.log("y : "+$("edit_y").value);
			var y = parseFloat($("edit_y").value);
			if(isNaN(y)){
				$("edit_y").value = "";
				$("edit_latitude").value = "";
			}
			else{
				$("edit_y").value = y.toFixed(2);
				if($("edit_x").value != ""){
					updateLatLngFieldsCoords();
				}
			}
			break;
		case 'lat' :
			console.log("lat : "+$("edit_latitude").value);
			var lat = parseFloat($("edit_latitude").value);
			if(isNaN(lat)){
				$("edit_latitude").value = "";
				$("edit_y").value = "";
			}
			else{
				$("edit_latitude").value = lat.toFixed(6);
				if($("edit_longitude").value != ""){
					updateXYFieldsCoords();;
				}
			}
			break;
		case 'lon' :
			console.log("lon : "+$("edit_longitude").value);
			var lon = parseFloat($("edit_longitude").value);
			if(isNaN(lon)){
				$("edit_longitude").value = "";
				$("edit_x").value = "";
			}
			else{
				$("edit_longitude").value = lon.toFixed(6);
				if($("edit_latitude").value != ""){
					updateXYFieldsCoords();
				}
			}
			break;
		default :
			alert("error !!!");
			break;
	}
	updateStopPlaceMarker();	
};

function centerOnMarker(){
	if(stopPlaceMarker != null){
		var geom = stopPlaceMarker.geometry;
		var newCoords = new OpenLayers.LonLat(geom.x,geom.y);
		map.panTo(newCoords);
	}
};


window.onload = init;