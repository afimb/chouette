<%@ taglib prefix="s" uri="/struts-tags"%>

<s:if test="baseLayerSource != ''">
  <script language="JavaScript" type="text/javascript" src="<s:url value='/js/openlayers/OpenLayers.js' includeParams='none'/>" ></script>
  <script language="JavaScript" type="text/javascript" src="<s:url value='/js/openlayers/LoadingPanel.js' includeParams='none'/>" ></script>
  <script language="JavaScript" type="text/javascript" src="<s:url value='/js/openlayers/proj4js-compressed.js' includeParams='none'/>" ></script>
</s:if>

<s:if test="baseLayerSource == 'geoportal' && geoportalApiKey != '' ">
  <script language="JavaScript" type="text/javascript" src="<s:url value='http://api.ign.fr/geoportail/api?v=1.1-m&key=%{geoportalApiKey}&includeEngine=true' includeParams='none'/>" ></script>
  <script language="JavaScript" type="text/javascript" src="<s:url value='/js/geoportalMap.js' includeParams='none'/>" ></script>
</s:if>
<s:elseif test="baseLayerSource == 'osm'">
  <script language="JavaScript" type="text/javascript" src="<s:url value='/js/osmMap.js' includeParams='none'/>" ></script>
</s:elseif>
<s:elseif test="baseLayerSource == 'google'">
  <script language="JavaScript" type="text/javascript" src="<s:url value='http://maps.google.com/maps/api/js?sensor=false' includeParams='none'/>" ></script>
  <script language="JavaScript" type="text/javascript" src="<s:url value='/js/googleMap.js' includeParams='none'/>" ></script>
</s:elseif>

<s:if test="baseLayerSource != '' && ( baseLayerSource != 'geoportal' || geoportalApiKey != '')">
  <script language="JavaScript" type="text/javascript" src="<s:url value='/js/map.js' includeParams='none'/>" ></script>
  <script language="JavaScript" type="text/javascript" src="<s:url value='/js/map/showMarkerLayer.js' includeParams='none'/>" ></script>
  <script language="JavaScript" type="text/javascript" src="<s:url value='/js/mapConnectionLink.js' includeParams='none'/>" ></script>
</s:if>