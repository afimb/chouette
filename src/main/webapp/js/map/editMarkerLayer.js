var EDIT_MARKER_TYPE;
var editMarker, drawControls;

function createEditMarkerLayer(){
  var editMarkerSymbolizer = OpenLayers.Util.applyDefaults(
    {
      externalGraphic: "../js/openlayers/img/green_round_marker.png",
      pointRadius: 10,
      fillOpacity: 1
    },
    OpenLayers.Feature.Vector.style["default"]);
  
  return createMarkerLayer(editMarkerSymbolizer,"Edit Marker Layer");
};

function initEditMarkerLayer(editMarkerType){
  EDIT_MARKER_TYPE = editMarkerType;
  var editMarkerLayer = map.getLayersByName("Edit Marker Layer")[0];
  
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

  drawControls = {
    draw: new OpenLayers.Control.DrawFeature(editMarkerLayer,OpenLayers.Handler.Point),
    modify: new OpenLayers.Control.ModifyFeature(editMarkerLayer,{
      clickout: false
    })
  };

  for(var key in drawControls)
  {
    map.addControl(drawControls[key]);
  }
};

/////////////////////////
// CONTROLS MANAGEMENT //
/////////////////////////

function toggleDrawControl(element) {
  var editMarkerLayer = map.getLayersByName("Edit Marker Layer")[0];
  for(key in controls) {
    drawControls[key].deactivate();
  }
  if(element.value != 'none'){
    if(editMarker == null){
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
  var editMarkerLayer = map.getLayersByName("Edit Marker Layer")[0];
  //console.log(event.type, event.feature ? event.feature.id : event.components);
  if(event.type == "sketchcomplete"){
    editMarker = event.feature;
    updateCoordsFieldsFromMarker(editMarker);
    drawControls.draw.deactivate();
    drawControls.modify.activate();
    editMarkerLayer.addFeatures([editMarker]);
    drawControls.modify.selectControl.select(editMarker);
    //prevents API from adding feature to the layer a second time
    return false;
  }
  else if(event.type == "featuremodified" || event.type == "vertexmodified"){
    updateCoordsFieldsFromMarker();
  }
};


//////////////////////////////////
// STOP PLACE MARKER MANAGEMENT //
//////////////////////////////////

function updateEditMarker(){
  var editMarkerLayer = map.getLayersByName("Edit Marker Layer")[0];
  if($(EDIT_MARKER_TYPE+"_latitude").value != "" && $(EDIT_MARKER_TYPE+"_longitude").value != ""){
    if(editMarker != null){
      var newCoords = new OpenLayers.LonLat($(EDIT_MARKER_TYPE+"_longitude").value,$(EDIT_MARKER_TYPE+"_latitude").value).transform(wgsProjection,geoportalProjection);
      editMarker.move(newCoords);
    }
    else{
      editMarker = new OpenLayers.Feature.Vector(new OpenLayers.Geometry.Point($(EDIT_MARKER_TYPE+"_longitude").value,$(EDIT_MARKER_TYPE+"_latitude").value).transform(wgsProjection,geoportalProjection));
      drawControls.draw.deactivate();
      drawControls.modify.activate();
      editMarkerLayer.addFeatures([editMarker]);
      drawControls.modify.selectControl.select(editMarker);
    }
  }
  else{
    if(editMarker != null){
      editMarkerLayer.removeFeatures([editMarker]);
      editMarker = null;
    }
    drawControls.modify.deactivate();
    drawControls.draw.activate();
  }

  centerOnMarker();

  //prevents API from adding feature to the layer a second time
  return false;
};

function centerOnMarker(){
  if(editMarker != null){
    var geom = editMarker.geometry;
    var newCoords = new OpenLayers.LonLat(geom.x,geom.y);
    map.panTo(newCoords);
  }
};

////////////////////////////
// FORM FIELDS MANAGEMENT //
////////////////////////////

function updateCoordsFieldsFromMarker(){
  var point = editMarker.geometry.clone();

  point.transform(geoportalProjection,wgsProjection);
  $(EDIT_MARKER_TYPE+"_latitude").value=point.y.toFixed(6);
  $(EDIT_MARKER_TYPE+"_longitude").value=point.x.toFixed(6);

  point.transform(wgsProjection,lambertProjection);
  $(EDIT_MARKER_TYPE+"_x").value=point.x.toFixed(2);
  $(EDIT_MARKER_TYPE+"_y").value=point.y.toFixed(2);
};

function updateLatLonFieldsCoordsFromXY(){
  var coords = new OpenLayers.LonLat($(EDIT_MARKER_TYPE+"_x").value,$(EDIT_MARKER_TYPE+"_y").value).transform(lambertProjection,wgsProjection);
  $(EDIT_MARKER_TYPE+"_latitude").value=coords.lat.toFixed(6);
  $(EDIT_MARKER_TYPE+"_longitude").value=coords.lon.toFixed(6);
};

function updateXYFieldsCoordsFromLatLon(){
  var coords = new OpenLayers.LonLat($(EDIT_MARKER_TYPE+"_longitude").value,$(EDIT_MARKER_TYPE+"_latitude").value).transform(wgsProjection,lambertProjection);
  $(EDIT_MARKER_TYPE+"_x").value=coords.lon.toFixed(2);
  $(EDIT_MARKER_TYPE+"_y").value=coords.lat.toFixed(2);
};

function updateCoordsFrom(field){
  switch(field){
    case 'x' :
      console.log("x : "+$(EDIT_MARKER_TYPE+"_x").value);
      var x = parseFloat($(EDIT_MARKER_TYPE+"_x").value);
      if(isNaN(x)){
        $(EDIT_MARKER_TYPE+"_x").value = "";
        $(EDIT_MARKER_TYPE+"_longitude").value = "";
      }
      else{
        $(EDIT_MARKER_TYPE+"_x").value = x.toFixed(2);
        if($(EDIT_MARKER_TYPE+"_y").value != ""){
          updateLatLonFieldsCoordsFromXY();
        }
      }
      break;
    case 'y' :
      console.log("y : "+$(EDIT_MARKER_TYPE+"_y").value);
      var y = parseFloat($(EDIT_MARKER_TYPE+"_y").value);
      if(isNaN(y)){
        $(EDIT_MARKER_TYPE+"_y").value = "";
        $(EDIT_MARKER_TYPE+"_latitude").value = "";
      }
      else{
        $(EDIT_MARKER_TYPE+"_y").value = y.toFixed(2);
        if($(EDIT_MARKER_TYPE+"_x").value != ""){
          updateLatLonFieldsCoordsFromXY();
        }
      }
      break;
    case 'lat' :
      console.log("lat : "+$(EDIT_MARKER_TYPE+"_latitude").value);
      var lat = parseFloat($(EDIT_MARKER_TYPE+"_latitude").value);
      if(isNaN(lat)){
        $(EDIT_MARKER_TYPE+"_latitude").value = "";
        $(EDIT_MARKER_TYPE+"_y").value = "";
      }
      else{
        $(EDIT_MARKER_TYPE+"_latitude").value = lat.toFixed(6);
        if($(EDIT_MARKER_TYPE+"_longitude").value != ""){
          updateXYFieldsCoordsFromLatLon();
        }
      }
      break;
    case 'lon' :
      console.log("lon : "+$(EDIT_MARKER_TYPE+"_longitude").value);
      var lon = parseFloat($(EDIT_MARKER_TYPE+"_longitude").value);
      if(isNaN(lon)){
        $(EDIT_MARKER_TYPE+"_longitude").value = "";
        $(EDIT_MARKER_TYPE+"_x").value = "";
      }
      else{
        $(EDIT_MARKER_TYPE+"_longitude").value = lon.toFixed(6);
        if($(EDIT_MARKER_TYPE+"_latitude").value != ""){
          updateXYFieldsCoordsFromLatLon();
        }
      }
      break;
    default :
      alert("error !!!");
      break;
  }
  updateEditMarker();
};
