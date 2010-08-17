//define global vars
var drawControls, boardingPositionMarker;

function init(){
  initMap();
	
  // edit marker layer
  editMarkerLayer = createEditMarkerLayer();

  map.addLayers([editMarkerLayer]);
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
	
  updateboardingPositionMarker();
}


/////////////////////////
// CONTROLS MANAGEMENT //
/////////////////////////

function toggleDrawControl(element) {
  for(key in controls) {
    drawControls[key].deactivate();
  }
  if(element.value != 'none'){
    if(boardingPositionMarker == null){
      drawControls.draw.activate();
    }
    else{
      drawControls.modify.activate();
      drawControls.modify.selectControl.select(editMarkerLayer.features[0]);
    }
  }
}

//////////////////////
// EVENT MANAGEMENT //
//////////////////////

function report(event) {
  //console.log(event.type, event.feature ? event.feature.id : event.components);
  if(event.type == "sketchcomplete"){
    boardingPositionMarker = event.feature;
    updateCoordsFieldsFromMarker(boardingPositionMarker);
    drawControls.draw.deactivate();
    drawControls.modify.activate();
    editMarkerLayer.addFeatures([boardingPositionMarker]);
    drawControls.modify.selectControl.select(boardingPositionMarker);
    //prevents API from adding feature to the layer a second time
    return false;
  }
  else if(event.type == "featuremodified" || event.type == "vertexmodified"){
    updateCoordsFieldsFromMarker();
  }
}

//////////////////////////////////
// STOP PLACE MARKER MANAGEMENT // 
//////////////////////////////////

function updateboardingPositionMarker()
{
  if($("boardingPosition_latitude").value != "" && $("boardingPosition_longitude").value != "")
  {
    if(boardingPositionMarker != null)
    {
      var newCoords = new OpenLayers.LonLat($("boardingPosition_longitude").value,$("boardingPosition_latitude").value).transform(wgsProjection,geoportalProjection);
      boardingPositionMarker.move(newCoords);
    }
    else
    {
      boardingPositionMarker = new OpenLayers.Feature.Vector(new OpenLayers.Geometry.Point($("boardingPosition_longitude").value,$("boardingPosition_latitude").value).transform(wgsProjection,geoportalProjection));
      drawControls.draw.deactivate();
      drawControls.modify.activate();
      editMarkerLayer.addFeatures([boardingPositionMarker]);
      drawControls.modify.selectControl.select(boardingPositionMarker);
    }
  }
  else
  {
    if(boardingPositionMarker != null)
    {
      editMarkerLayer.removeFeatures([boardingPositionMarker]);
      boardingPositionMarker = null;
    }
    drawControls.modify.deactivate();
    drawControls.draw.activate();
  }
	
  centerOnMarker();
	
  //prevents API from adding feature to the layer a second time
  return false;
}

function centerOnMarker()
{
  if(boardingPositionMarker != null){
    var geom = boardingPositionMarker.geometry;
    var newCoords = new OpenLayers.LonLat(geom.x,geom.y);
    map.panTo(newCoords);
  }
}

////////////////////////////
// FORM FIELDS MANAGEMENT //
////////////////////////////

function updateCoordsFieldsFromMarker()
{
  var point = boardingPositionMarker.geometry.clone();
	
  point.transform(geoportalProjection,wgsProjection);
  $("boardingPosition_latitude").value=point.y.toFixed(6);
  $("boardingPosition_longitude").value=point.x.toFixed(6);
	
  point.transform(wgsProjection,lambertProjection);
  $("boardingPosition_x").value=point.x.toFixed(2);
  $("boardingPosition_y").value=point.y.toFixed(2);
}

function updateLatLonFieldsCoordsFromXY()
{
  var coords = new OpenLayers.LonLat($("boardingPosition_x").value,$("boardingPosition_y").value).transform(lambertProjection,wgsProjection);
  $("boardingPosition_latitude").value=coords.lat.toFixed(6);
  $("boardingPosition_longitude").value=coords.lon.toFixed(6);
}

function updateXYFieldsCoordsFromLatLon()
{
  var coords = new OpenLayers.LonLat($("boardingPosition_longitude").value,$("boardingPosition_latitude").value).transform(wgsProjection,lambertProjection);
  $("boardingPosition_x").value=coords.lon.toFixed(2);
  $("boardingPosition_y").value=coords.lat.toFixed(2);
}

function updateCoordsFrom(field)
{
  switch(field){
    case 'x' :
      console.log("x : "+$("boardingPosition_x").value);
      var x = parseFloat($("boardingPosition_x").value);
      if(isNaN(x)){
        $("boardingPosition_x").value = "";
        $("boardingPosition_longitude").value = "";
      }
      else{
        $("boardingPosition_x").value = x.toFixed(2);
        if($("boardingPosition_y").value != ""){
          updateLatLonFieldsCoordsFromXY();
        }
      }
      break;
    case 'y' :
      console.log("y : "+$("boardingPosition_y").value);
      var y = parseFloat($("boardingPosition_y").value);
      if(isNaN(y)){
        $("boardingPosition_y").value = "";
        $("boardingPosition_latitude").value = "";
      }
      else{
        $("boardingPosition_y").value = y.toFixed(2);
        if($("boardingPosition_x").value != ""){
          updateLatLonFieldsCoordsFromXY();
        }
      }
      break;
    case 'lat' :
      console.log("lat : "+$("boardingPosition_latitude").value);
      var lat = parseFloat($("boardingPosition_latitude").value);
      if(isNaN(lat)){
        $("boardingPosition_latitude").value = "";
        $("boardingPosition_y").value = "";
      }
      else{
        $("boardingPosition_latitude").value = lat.toFixed(6);
        if($("boardingPosition_longitude").value != ""){
          updateXYFieldsCoordsFromLatLon();
        }
      }
      break;
    case 'lon' :
      console.log("lon : "+$("boardingPosition_longitude").value);
      var lon = parseFloat($("boardingPosition_longitude").value);
      if(isNaN(lon)){
        $("boardingPosition_longitude").value = "";
        $("boardingPosition_x").value = "";
      }
      else{
        $("boardingPosition_longitude").value = lon.toFixed(6);
        if($("boardingPosition_latitude").value != ""){
          updateXYFieldsCoordsFromLatLon();
        }
      }
      break;
    default :
      alert("error !!!");
      break;
  }
  updateboardingPositionMarker();
}

window.onload = init;