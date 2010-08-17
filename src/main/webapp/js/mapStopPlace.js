//define global vars
var drawControls, stopPlaceMarker, editMarkerLayer, showMarkerLayer, childrenAreas;

function init(){
  initMap();

  // Add button for barycentre
  var barycentre = new OpenLayers.Control.Button({
    displayClass: "mapBarycentre",
    trigger: barycentreStopPlaceMarker
  });
  var panel = new OpenLayers.Control.Panel();
  panel.addControls([barycentre]);
  var pixel = new OpenLayers.Pixel(15, 260);
  map.addControl(panel, pixel);
  
  // edit marker layer
  editMarkerLayer = createEditMarkerLayer();

  //show marker layer
  showMarkerLayer = createShowMarkerLayer();

  map.addLayers([editMarkerLayer,showMarkerLayer]);
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
    hover: true,
    highlightOnly: true,
    renderIntent: "",
    eventListeners: {
      featurehighlighted: showTooltipOnEvent,
      featureunhighlighted: hideTooltipOnEvent
    }
  });

  map.addControl(highlightCtrl);
  highlightCtrl.activate();

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

  var stopPlaceId = $("stoparea_idPositionGeographique").value ;
  if(stopPlaceId != null){
    initShowMarkerLayer("../json/JSONStopPlace?stopPlaceId="+stopPlaceId);
  }
  updateStopPlaceMarker();
}


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
}


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
}


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
}

function centerOnMarker(){
  if(stopPlaceMarker != null){
    var geom = stopPlaceMarker.geometry;
    var newCoords = new OpenLayers.LonLat(geom.x,geom.y);
    map.panTo(newCoords);
  }
}

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
}

function updateLatLonFieldsCoordsFromXY(){
  var coords = new OpenLayers.LonLat($("stoparea_x").value,$("stoparea_y").value).transform(lambertProjection,wgsProjection);
  $("stoparea_latitude").value=coords.lat.toFixed(6);
  $("stoparea_longitude").value=coords.lon.toFixed(6);
}

function updateXYFieldsCoordsFromLatLon(){
  var coords = new OpenLayers.LonLat($("stoparea_longitude").value,$("stoparea_latitude").value).transform(wgsProjection,lambertProjection);
  $("stoparea_x").value=coords.lon.toFixed(2);
  $("stoparea_y").value=coords.lat.toFixed(2);
}

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
}

//////////////////////
// POPUP MANAGEMENT //
//////////////////////

function showTooltipOnEvent(event)
{
  feature = event.feature;
  popup = new OpenLayers.Popup.FramedCloud("featurePopup",
    feature.geometry.getBounds().getCenterLonLat(),
    new OpenLayers.Size(100,100),
    "<h2>"+feature.attributes.area.name + "</h2>",
    null, true, onPopupClose);
  feature.popup = popup;
  popup.feature = feature;
  map.addPopup(popup);
}

function hideTooltipOnEvent(event)
{
  feature = event.feature;
  if (feature.popup) {
    popup.feature = null;
    map.removePopup(feature.popup);
    feature.popup.destroy();
    feature.popup = null;
  }
}

function onPopupClose(event)
{
  // 'this' is the popup.
  selectControl.unselect(this.feature);
}

function barycentreStopPlaceMarker()
{
  var childrenPoints = showMarkerLayer.features.collect(function(feature){
    return feature.geometry;
  });
  var point = barycentre(childrenPoints).transform(geoportalProjection, lambertProjection);

  $("stoparea_x").value=point.lon;
  $("stoparea_y").value=point.lat;

  updateLatLonFieldsCoordsFromXY();
  updateStopPlaceMarker();
}

window.onload = init;