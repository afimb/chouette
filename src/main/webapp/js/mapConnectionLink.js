function init(){
  // Call to function init in map.js
  initMap();

  //show marker layer
  showMarkerLayer = createShowMarkerLayer();
  map.addLayers([showMarkerLayer]);
  
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
	
  var connectionLinkId = $("connectionLink_idCorrespondance").value ;
  if(connectionLinkId != null){
    initShowMarkerLayer("../json/JSONConnectionLink?connectionLinkId="+connectionLinkId);
  }
}

//////////////////////
// POPUP MANAGEMENT //
//////////////////////

function showTooltipOnEvent(event)
{
  var feature = event.feature;
  var text =  "<h2>"+feature.attributes.area.name + "</h2>";
  if(feature.attributes.area.streetName != null)
    text += "<p>"+feature.attributes.area.streetName + "</p>";
  if(feature.attributes.area.countryCode != null)
    text +="<p>"+feature.attributes.area.countryCode + "</p>";

  var popup = new OpenLayers.Popup.FramedCloud("featurePopup",
    feature.geometry.getBounds().getCenterLonLat(),
    new OpenLayers.Size(100,100),
    text,
    null, true, onPopupClose);
  feature.popup = popup;
  popup.feature = feature;
  map.addPopup(popup);
}

function hideTooltipOnEvent(event)
{
  var feature = event.feature;
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

window.onload = init;