///////////////////
// SHOW-HIDE MAP //
///////////////////
Chouette.Map.showMap = function()
{
  if($('map').visible())
  {
    Cookie.setData('showMap', false)
    Element.relativize($('map-view'));
    $('map-view').setStyle({
      cssFloat: 'right'
    });
    $('map').hide();
    $('map-view-text').update('&laquo;');
  }
  else
  {
    Cookie.setData('showMap', true)
    $('map-view').setStyle({
      cssFloat: 'none'
    });
    Element.absolutize($('map-view'));
    $('map').show();
    $('map-view-text').update('&raquo;');
  }
};

Chouette.Map.initShowMap = function()
{
  // HIDE MAP IF PROPERTY IS FALSE IN THE COOKIE
  if(Cookie.getData('showMap') == false)
  {
    Element.relativize($('map-view'));
    $('map-view').setStyle({
      cssFloat: 'right'
    });
    $('map').hide();
    $('map-view-text').update('&laquo;');
  }
  else
  {
    $('map-view').setStyle({
      cssFloat: 'none'
    });
    Element.absolutize($('map-view'));
    $('map').show();
    $('map-view-text').update('&raquo;');
  }
};

new Event.observe(window, 'load', Chouette.Map.initShowMap);