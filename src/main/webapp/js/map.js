var map;
function init(){
	var epsg4326= new OpenLayers.Projection("EPSG:4326");
	var geoportalProjection= new OpenLayers.Projection("IGNF:GEOPORTALFXX");
	map = new OpenLayers.Map('map', {
		resolutions: Geoportal.Catalogue.RESOLUTIONS,
		projection: geoportalProjection,
		maxExtent: new OpenLayers.Bounds(-180, -57, 180, 72).transform(epsg4326,geoportalProjection, true),
		units: geoportalProjection.getUnits(),
		controls:[
			new OpenLayers.Control.PanZoomBar(),
			new OpenLayers.Control.LayerSwitcher({'ascending':false}),
			new OpenLayers.Control.ScaleLine(),
			new OpenLayers.Control.MousePosition(),
			new OpenLayers.Control.KeyboardDefaults(),
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
			maxExtent: new OpenLayers.Bounds(-180, -57, 180, 72).transform(epsg4326,geoportalProjection, true),
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
			maxExtent: new OpenLayers.Bounds(-180, -57, 180, 72).transform(epsg4326,geoportalProjection, true),
			units: geoportalProjection.getUnits(),
			GeoRM: Geoportal.GeoRMHandler.addKey(
				gGEOPORTALRIGHTSMANAGEMENT.apiKey,
				gGEOPORTALRIGHTSMANAGEMENT[gGEOPORTALRIGHTSMANAGEMENT.apiKey].tokenServer.url,
				gGEOPORTALRIGHTSMANAGEMENT[gGEOPORTALRIGHTSMANAGEMENT.apiKey].tokenServer.ttl,
				map
			)
		}
	);
	
	map.addLayers([geoMapLayer,orthoPhotoLayer]);
	map.setCenter(new OpenLayers.LonLat(177169.0,5441595.0),10);
};

window.onload = init;