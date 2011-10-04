<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>
<%-- <s:include value="/jsp/commun/mapStopPlaceJavascript.jsp" />
<script language="JavaScript" type="text/javascript" src="<s:url value='/js/showMap.js' includeParams='none'/>" ></script>
 --%>
<%-- Titre et barre de navigation --%>
<s:url id="urlPositionGeographiqueUpdate" action="edit" namespace="/routingConstraint">
  <s:param name="idPositionGeographique" value="%{id}"/>
</s:url>
<s:if test="id != null">
  <title><s:text name="text.routingConstraint.update.title" /></title>
  <s:property value="filAriane.addElementFilAriane(getText('text.routingConstraint.update.title'), '', #urlPositionGeographiqueUpdate)"/>
</s:if> 
<s:else>
  <title><s:text name="text.routingConstraint.create.title" /></title>
  <s:property value="filAriane.addElementFilAriane(getText('text.routingConstraint.create.title'), '', #urlPositionGeographiqueUpdate)"/>
</s:else>
<div class="panelData">
  <s:property value="filAriane.texteFilAriane" escape="false"/>
</div>

<s:include value="/jsp/commun/messages.jsp" />

<%-- Caractéristiques des zones --%>
<div class="panelDataSection"><s:text name="text.routingConstraint" /></div>
<div class="panel">
  <div class="editRoutingConstraint">
    <s:form  theme="css_xhtml" id="stoparea">
      <s:hidden name="idLigne" value="%{idLigne}"/>
      <s:hidden name="idItineraire" value="%{idItineraire}"/>
      <s:hidden name="actionSuivante" value="%{actionSuivante}"/>
      <s:hidden name="idPositionGeographique" value="%{id}" />
      <s:hidden name="operationMode" value="STORE" />
      <s:hidden key="actionMethod" value="%{actionMethod}"/>

      <s:textfield key="objectId" readonly="true" cssClass="texteNonEditable"/>
      <s:textfield key="name" required="true"/>
      <s:textfield key="comment"/>
<%--       <s:textfield key="nearestTopicName" />
      <s:textfield key="areaCentroid.address.streetName" />
      <s:textfield key="areaCentroid.address.countryCode" />
      <s:textfield key="fareCode" />
      <s:textfield key="registrationNumber" />
 --%>
      <s:if test="id != null">
        <s:select key="areaType" required="true" list="%{getStopAreaEnum('RoutingConstraint')}" listKey="enumeratedTypeAccess" listValue="textePropriete" disabled="true"/>
      </s:if>
      <s:else>
        <s:select key="areaType" required="true" list="%{getStopAreaEnum('RoutingConstraint')}" listKey="enumeratedTypeAccess" listValue="textePropriete"/>
      </s:else>

<%--       <fieldset>
        <legend><s:text name="text.positionGeographique.dataGeo.fieldset"/></legend>
        <p><s:text name="lambert.%{lambertSRID}"/></p>
        <s:textfield key="areaCentroid.projectedPoint.x"  onblur="Chouette.Map.updateCoordsFrom('x')"/>
        <s:textfield key="areaCentroid.projectedPoint.y" onblur="Chouette.Map.updateCoordsFrom('y')"/>
        <p><s:text name="wsg84"/></p>
        <s:textfield key="areaCentroid.longitude" onblur="Chouette.Map.updateCoordsFrom('lon')"/>
        <s:textfield key="areaCentroid.latitude" onblur="Chouette.Map.updateCoordsFrom('lat')"/>
      </fieldset>
 --%>
      <%-- Ajout des balises tr et td pour le faire apparaitre dans le tableau --%>
      <s:include value="/jsp/commun/asterisque.jsp" />
      <%-- Actions --%>
      <div class="submit">
        <s:if test="id != null">
          <s:submit key="action.update" action="%{actionMethod}"  theme="simple" cssClass="right"/>
        </s:if>
        <s:else>
          <s:submit key="action.create" action="%{actionMethod}" theme="simple" cssClass="right"/>
        </s:else>
        <s:submit key="action.cancel" action="cancel" theme="simple" cssClass="right"/>
      </div>
    </s:form>
  </div>
<!--   <div class="map-wrapper">
    <div id="map-view" onclick="Chouette.Map.showMap()"><div id="map-view-text"></div></div>
    <div id="map"></div>
  </div> -->
</div>

<s:if test="id != null">
  <%-- Zones filles --%>
  <div class="panelDataSection">
    <s:text name="text.positionGeographique.childArea.title" />
  </div>
  <div class="panel">
    <s:div label="Children" id="displaytag">
      <display:table name="children" id="child"  excludedParams="" sort="list" pagesize="10" export="false">
        <display:column titleKey="table.title.action">
           <s:url id="removeUrl" action="removeChildFromParent" namespace="/routingConstraint">
            <s:param name="idPositionGeographique" value="%{id}" />
            <s:param name="idChild">${child.id}</s:param>
          </s:url>
          <s:a href="%{removeUrl}">
            <img border="0" alt="Delete" src="<s:url value='/images/supprimer.png'/>" title="<s:text name="tooltip.delete"/>">
          </s:a>
        </display:column>
        <display:column titleKey="table.title.name">
			<s:property value="%{#attr.child.name}"/>
        </display:column>
        <display:column titleKey="table.title.type">
          <s:text name="%{#attr.child.areaType}"/>
        </display:column>
      </display:table>
    </s:div>
    <%-- Formulaire de recherche de zone fille --%>
    <s:form id="areaSearchForm" action="search"  namespace="/routingConstraint">
      <s:hidden name="idPositionGeographique" value="%{id}"/>
      <s:hidden name="actionSuivante" value="addChild"/>
      <s:submit key="action.add"/>
    </s:form>
  </div>

  <%-- Lignes affectées --%>
  <div class="panelDataSection">
    <s:text name="text.positionGeographique.line.title" />
  </div>
  <div class="panel">
    <s:div label="Lines" id="displaytag">
      <display:table name="lines" id="line"  excludedParams="" sort="list" pagesize="10" export="false">
        <display:column titleKey="table.title.action">
          
          <s:url id="removeUrl" action="removeLineFromRoutingConstraint" namespace="/routingConstraint">
            <s:param name="idPositionGeographique" value="%{id}" />
            <s:param name="idLine">${line.id}</s:param>
          </s:url>
          <s:a href="%{removeUrl}">
            <img border="0" alt="Delete" src="<s:url value='/images/supprimer.png'/>" title="<s:text name="tooltip.delete"/>">
          </s:a>
        </display:column>
        <display:column titleKey="table.title.name">
					<s:property value="%{#attr.line.name}"/>
        </display:column>
        <display:column titleKey="table.title.number">
          <s:text name="%{#attr.line.number}"/>
        </display:column>
      </display:table>
    </s:div>
    <%-- Formulaire de recherche de zone fille --%>
    <s:form id="areaSearchForm" action="search"  namespace="/routingConstraint">
      <s:hidden name="idPositionGeographique" value="%{id}"/>
      <s:hidden name="actionSuivante" value="addLine"/>
      <s:submit key="action.add"/>
    </s:form>
  </div>


</s:if>	