Chouette.Map.EDIT_MARKER_TYPE;
Chouette.Map.editMarker;
Chouette.Map.drawControls;

Chouette.Map.createEditMarkerLayer = function(){
  var editMarkerSymbolizer = OpenLayers.Util.applyDefaults(
    {
      externalGraphic: "../js/openlayers/img/green_round_marker.png",
      pointRadius: 10,
      fillOpacity: 1
    },
    OpenLayers.Feature.Vector.style["default"]);
  
  return this.createMarkerLayer(editMarkerSymbolizer,"Edit Marker Layer");
};

Chouette.Map.initEditMarkerLayer = function(editMarkerType){
  this.EDIT_MARKER_TYPE = editMarkerType;
  var editMarkerLayer = this.map.getLayersByName("Edit Marker Layer")[0];
  
  // === INIT EVENTS MANAGEMENT ===
  editMarkerLayer.events.on({
    //		"beforefeaturemodified": report,
    //		"afterfeaturemodified": report,
    //		"sketchmodified": report,
    //		"sketchstarted": report,
    "vertexmodified": this.report,
    "featuremodified": this.report,
    "sketchcomplete": this.report
  });

  // === INIT CONTROLS ===

  this.drawControls = {
    draw: new OpenLayers.Control.DrawFeature(editMarkerLayer,OpenLayers.Handler.Point),
    modify: new OpenLayers.Control.ModifyFeature(editMarkerLayer,{
      clickout: false
    })
  };

  for(var key in this.drawControls)
  {
    this.map.addControl(this.drawControls[key]);
  }
};

/////////////////////////
// CONTROLS MANAGEMENT //
/////////////////////////

Chouette.Map.toggleDrawControl = function(element) {
  var editMarkerLayer = this.map.getLayersByName("Edit Marker Layer")[0];
  for(key in this.drawControls) {
    this.drawControls[key].deactivate();
  }
  if(element.value != 'none'){
    if(this.editMarker == null){
      this.drawControls.draw.activate();
    }
    else{
      this.drawControls.modify.activate();
      this.drawControls.modify.selectControl.select(editMarkerLayer.features[0]);
    }
  }
};


//////////////////////
// EVENT MANAGEMENT //
//////////////////////

Chouette.Map.report = function(event) {
  var editMarkerLayer = Chouette.Map.map.getLayersByName("Edit Marker Layer")[0];
  //console.log(event.type, event.feature ? event.feature.id : event.components);
  if(event.type == "sketchcomplete"){
    Chouette.Map.editMarker = event.feature;
    Chouette.Map.updateCoordsFieldsFromMarker();
    Chouette.Map.drawControls.draw.deactivate();
    Chouette.Map.drawControls.modify.activate();
    editMarkerLayer.addFeatures([Chouette.Map.editMarker]);
    Chouette.Map.drawControls.modify.selectControl.select(Chouette.Map.editMarker);
    //prevents API from adding feature to the layer a second time
    return false;
  }
  else if(event.type == "featuremodified" || event.type == "vertexmodified"){
    Chouette.Map.updateCoordsFieldsFromMarker();
  }
};


//////////////////////////////////
// STOP PLACE MARKER MANAGEMENT //
//////////////////////////////////

Chouette.Map.updateEditMarker = function(){
  var editMarkerLayer = this.map.getLayersByName("Edit Marker Layer")[0];
  if($(this.EDIT_MARKER_TYPE+"_areaCentroid_latitude").value != "" && $(this.EDIT_MARKER_TYPE+"_areaCentroid_longitude").value != ""){
    if(this.editMarker != null){
      var newCoords = new OpenLayers.LonLat($(this.EDIT_MARKER_TYPE+"_areaCentroid_longitude").value,$(this.EDIT_MARKER_TYPE+"_areaCentroid_latitude").value).transform(this.wgsProjection,this.baseLayerProjection);
      this.editMarker.move(newCoords);
    }
    else{
      this.editMarker = new OpenLayers.Feature.Vector(new OpenLayers.Geometry.Point($(this.EDIT_MARKER_TYPE+"_areaCentroid_longitude").value,$(this.EDIT_MARKER_TYPE+"_areaCentroid_latitude").value).transform(this.wgsProjection,this.baseLayerProjection));
      this.drawControls.draw.deactivate();
      this.drawControls.modify.activate();
      editMarkerLayer.addFeatures([this.editMarker]);
      this.drawControls.modify.selectControl.select(this.editMarker);
    }
  }
  else{
    if(this.editMarker != null){
      editMarkerLayer.removeFeatures([this.editMarker]);
      this.editMarker = null;
    }
    this.drawControls.modify.deactivate();
    this.drawControls.draw.activate();
  }

  this.centerOnMarker();

  //prevents API from adding feature to the layer a second time
  return false;
};

Chouette.Map.centerOnMarker = function(){
  if(this.editMarker != null){
    var geom = this.editMarker.geometry;
    var newCoords = new OpenLayers.LonLat(geom.x,geom.y);
    this.map.panTo(newCoords);
  }
};

Chouette.Map.zoomOnMarker = function(){
  if(this.editMarker != null){
    var geom = this.editMarker.geometry;
    var newCoords = new OpenLayers.LonLat(geom.x,geom.y);
    this.map.setCenter(newCoords,this.map.getNumZoomLevels()-1);
  }
};

////////////////////////////
// FORM FIELDS MANAGEMENT //
////////////////////////////

Chouette.Map.updateCoordsFieldsFromMarker = function(){
  var point = this.editMarker.geometry.clone();

  point.transform(this.baseLayerProjection,this.wgsProjection);
  $(this.EDIT_MARKER_TYPE+"_areaCentroid_latitude").value=point.y.toFixed(6);
  $(this.EDIT_MARKER_TYPE+"_areaCentroid_longitude").value=point.x.toFixed(6);

  point.transform(this.wgsProjection,this.lambertProjection);
  $(this.EDIT_MARKER_TYPE+"_areaCentroid_projectedPoint_x").value=point.x.toFixed(2);
  $(this.EDIT_MARKER_TYPE+"_areaCentroid_projectedPoint_y").value=point.y.toFixed(2);
};

Chouette.Map.updateLatLonFieldsCoordsFromXY = function(){
  var coords = new OpenLayers.LonLat($(this.EDIT_MARKER_TYPE+"_areaCentroid_projectedPoint_x").value,$(this.EDIT_MARKER_TYPE+"_areaCentroid_projectedPoint_y").value).transform(this.lambertProjection,this.wgsProjection);
  $(this.EDIT_MARKER_TYPE+"_areaCentroid_latitude").value=coords.lat.toFixed(6);
  $(this.EDIT_MARKER_TYPE+"_areaCentroid_longitude").value=coords.lon.toFixed(6);
};

Chouette.Map.updateXYFieldsCoordsFromLatLon = function(){
  var coords = new OpenLayers.LonLat($(this.EDIT_MARKER_TYPE+"_areaCentroid_longitude").value,$(this.EDIT_MARKER_TYPE+"_areaCentroid_latitude").value).transform(this.wgsProjection,this.lambertProjection);
  $(this.EDIT_MARKER_TYPE+"_areaCentroid_projectedPoint_x").value=coords.lon.toFixed(2);
  $(this.EDIT_MARKER_TYPE+"_areaCentroid_projectedPoint_y").value=coords.lat.toFixed(2);
};

Chouette.Map.updateCoordsFrom = function(field){
  switch(field){
    case 'x' :
      var x = parseFloat($(this.EDIT_MARKER_TYPE+"_areaCentroid_projectedPoint_x").value);
      if(isNaN(x)){
        $(this.EDIT_MARKER_TYPE+"_areaCentroid_projectedPoint_x").value = "";
        $(this.EDIT_MARKER_TYPE+"_areaCentroid_longitude").value = "";
      }
      else{
        $(this.EDIT_MARKER_TYPE+"_areaCentroid_projectedPoint_x").value = x.toFixed(2);
        if($(this.EDIT_MARKER_TYPE+"_areaCentroid_projectedPoint_y").value != ""){
          this.updateLatLonFieldsCoordsFromXY();
        }
      }
      break;
    case 'y' :
      var y = parseFloat($(this.EDIT_MARKER_TYPE+"_areaCentroid_projectedPoint_y").value);
      if(isNaN(y)){
        $(this.EDIT_MARKER_TYPE+"_areaCentroid_projectedPoint_y").value = "";
        $(this.EDIT_MARKER_TYPE+"_areaCentroid_latitude").value = "";
      }
      else{
        $(this.EDIT_MARKER_TYPE+"_areaCentroid_projectedPoint_y").value = y.toFixed(2);
        if($(this.EDIT_MARKER_TYPE+"_areaCentroid_projectedPoint_x").value != ""){
          this.updateLatLonFieldsCoordsFromXY();
        }
      }
      break;
    case 'lat' :
      var lat = parseFloat($(this.EDIT_MARKER_TYPE+"_areaCentroid_latitude").value);
      if(isNaN(lat)){
        $(this.EDIT_MARKER_TYPE+"_areaCentroid_latitude").value = "";
        $(this.EDIT_MARKER_TYPE+"_areaCentroid_projectedPoint_y").value = "";
      }
      else{
        $(this.EDIT_MARKER_TYPE+"_areaCentroid_latitude").value = lat.toFixed(6);
        if($(this.EDIT_MARKER_TYPE+"_areaCentroid_longitude").value != ""){
          this.updateXYFieldsCoordsFromLatLon();
        }
      }
      break;
    case 'lon' :
      var lon = parseFloat($(this.EDIT_MARKER_TYPE+"_areaCentroid_longitude").value);
      if(isNaN(lon)){
        $(this.EDIT_MARKER_TYPE+"_areaCentroid_longitude").value = "";
        $(this.EDIT_MARKER_TYPE+"_areaCentroid_projectedPoint_x").value = "";
      }
      else{
        $(this.EDIT_MARKER_TYPE+"_areaCentroid_longitude").value = lon.toFixed(6);
        if($(this.EDIT_MARKER_TYPE+"_areaCentroid_latitude").value != ""){
          this.updateXYFieldsCoordsFromLatLon();
        }
      }
      break;
    default :
      alert("error !!!");
      break;
  }
  this.updateEditMarker();
};
