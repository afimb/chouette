//define global vars
var showMarkerLayer;

function init(){
  // Call to function init in map.js
  initMap();

  //show marker layer
  showMarkerLayer = createShowMarkerLayer();

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
	
  initLineLayer();
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

//////////////////////////////////
// SHOW MARKER LAYER MANAGEMENT //
//////////////////////////////////

function initLineLayer(){
  if(	$("line_idLigne").value != null)
  {
    var url = "../json/JSONLine?lineId="+$("line_idLigne").value;
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
              'area':area
            });
            markPoints.push(mark.geometry);
            showMarkerLayer.addFeatures([mark]);
          }
        });

        map.addLayers([showMarkerLayer]);
        if(bounds.left != null) // If line has stop areas
        {
          // Hack : reduce zoom to see marker picture
          var zoom = map.getZoomForExtent(bounds, true) - 1;
          var point = barycentre(markPoints);
          map.setCenter(point, zoom);
        }
        else // If line has no stop areas
        {
          map.setCenter(new OpenLayers.LonLat(177169.0,5441595.0),20);
        }
      }
    });
  }
}

window.onload = init;