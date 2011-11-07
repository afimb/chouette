<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>
<s:include value="/jsp/commun/mapBoardingPositionJavascript.jsp" />
<script language="JavaScript" type="text/javascript" src="<s:url value='/js/showMap.js' includeParams='none'/>" ></script>
<%-- Titre et barre de navigation --%>
<s:url id="urlPositionGeographiqueUpdate" action="edit" namespace="/boardingPosition">
  <s:param name="idPositionGeographique" value="%{id}"/>
</s:url>
<s:if test="id != null">
  <title><s:text name="text.arretPhysique.update.title" /></title>
  <s:property value="filAriane.addElementFilAriane(getText('text.arretPhysique.update.title'), '', #urlPositionGeographiqueUpdate)"/>
</s:if> 
<s:else>
  <title><s:text name="text.arretPhysique.create.title" /></title>
  <s:property value="filAriane.addElementFilAriane(getText('text.arretPhysique.create.title'), '', #urlPositionGeographiqueUpdate)"/>
</s:else>
<div class="panelData">
  <s:property value="filAriane.texteFilAriane" escape="false"/>
</div>

<s:include value="/jsp/commun/messages.jsp" />

<%-- Caractéristiques des PositionGeographiques --%>
<div class="panelDataSection"><s:text name="text.arretPhysique" /></div>
<div class="panel">
  <div class="editBoardingPosition">
    <s:form theme="css_xhtml" id="boardingPosition">
      <s:hidden name="idLigne" value="%{idLigne}"/>
      <s:hidden name="idItineraire" value="%{idItineraire}"/>
      <s:hidden name="actionSuivante" value="%{actionSuivante}"/>
      <s:hidden name="idPositionGeographique" value="%{id}" />
      <s:hidden name="operationMode" value="%{'STORE'}" />
      <s:hidden key="actionMethod" value="%{actionMethod}"/>

      <s:textfield key="objectId" readonly="true" cssClass="texteNonEditable"/>
      <s:textfield key="name" required="true" />
      <s:textfield key="comment" />
      <s:textfield key="nearestTopicName" />
      <s:textfield key="areaCentroid.address.streetName" />
      <s:textfield key="areaCentroid.address.countryCode" />
      <s:textfield key="fareCode" />
      <s:textfield key="registrationNumber" />

      <s:if test="id != null">
        <s:select key="areaType" required="true" list="%{getStopAreaEnum('QuayBoardingPosition')}" listKey="enumeratedTypeAccess" listValue="textePropriete" disabled="true"/>
      </s:if>
      <s:else>
        <s:select key="areaType" required="true" list="%{getStopAreaEnum('QuayBoardingPosition')}" listKey="enumeratedTypeAccess" listValue="textePropriete"/>
      </s:else>

      <fieldset>
        <legend><s:text name="text.positionGeographique.dataGeo.fieldset"/></legend>
        <p><s:text name="lambert.%{lambertSRID}"/></p>
        <s:textfield key="areaCentroid.projectedPoint.x"  onblur="Chouette.Map.updateCoordsFrom('x')"/>
        <s:textfield key="areaCentroid.projectedPoint.y" onblur="Chouette.Map.updateCoordsFrom('y')"/>
        <p><s:text name="wsg84"/></p>
        <s:textfield key="areaCentroid.longitude" onblur="Chouette.Map.updateCoordsFrom('lon')"/>
        <s:textfield key="areaCentroid.latitude" onblur="Chouette.Map.updateCoordsFrom('lat')"/>
      </fieldset>

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
  <div class="map-wrapper">
    <div id="map-view" onclick="Chouette.Map.showMap()"><div id="map-view-text"></div></div>
    <div id="map"></div>
  </div>
</div>

<s:if test="id != null">
  <%-- Itinéraires liés à l'arrêt physique --%>
  <div class="panelDataSection">
    <s:text name="text.stopPlace.routes"/>
  </div>
  <div class="panel">
    <div label="Iti" id="displaytag">
      <display:table name="itineraires" uid="itineraire" sort="list" pagesize="10" export="false">
        <display:column titleKey="route.title.name" sortable="true" headerClass="sortable">
          <s:url id="arretSurItineraire" action="list" namespace="/stoppointOnRoute" includeParams="none">
            <s:param name="idItineraire">%{#attr.itineraire.id}</s:param>
            <s:param name="idLigne">%{#attr.itineraire.line.id}</s:param>
          </s:url>
          <s:a href="%{arretSurItineraire}"><s:property value="%{#attr.itineraire.name}"/></s:a>
        </display:column>
        <display:column titleKey="route.title.passingtime" sortable="true" headerClass="sortable">
          <s:url id="horairesDePassage" action="list" namespace="/vehicleJourneyAtStop" includeParams="none">
            <s:param name="idItineraire">%{#attr.itineraire.id}</s:param>
            <s:param name="idLigne">%{#attr.itineraire.line.id}</s:param>
          </s:url>
          <s:a href="%{horairesDePassage}"><s:text name="text.vehicleJourneyAtStop"/></s:a>
        </display:column>
        <display:column titleKey="route.title.linename"sortable="true" headerClass="sortable">
          <s:url id="editLigne" action="edit" namespace="/line" includeParams="none">
            <s:param name="idLigne">%{#attr.itineraire.line.id}</s:param>
          </s:url>
          <s:a href="%{editLigne}"><s:property value="%{getLigne(#attr.itineraire.id).name}" /></s:a>
        </display:column>
        <display:column titleKey="route.title.networkname" sortable="true" headerClass="sortable">
          <s:url id="editReseau" action="edit" namespace="/network" includeParams="none">
            <s:param name="idReseau">%{#attr.itineraire.line.ptNetwork.id}</s:param>
          </s:url>
          <s:a href="%{editReseau}"><s:property value="%{getReseau(#attr.itineraire.line.id).name}" /></s:a>
        </display:column>
      </display:table>
    </div>
  </div>


  <%-- Zones parentes --%>
  <div class="panelDataSection">
    <s:text name="text.positionGeographique.fatherArea.title" />
  </div>

  <div class="panel">
    <div label="father" id="displaytag">
      <display:table name="father"  excludedParams="" sort="list" pagesize="10" export="false">
        <display:column titleKey="table.title.action">
          <s:url id="editUrl" action="edit" namespace="/stopPlace">
            <s:param name="idPositionGeographique" value="%{father.id}" />
          </s:url>
          <s:a href="%{editUrl}">
            <img border="0" alt="Edit" src="<s:url value='/images/editer.png'/>" title="<s:text name="tooltip.edit"/>">
          </s:a>&nbsp;&nbsp;
          <s:url id="removeUrl" action="removeChildFromParent" namespace="/boardingPosition">
            <s:param name="idChild" value="%{id}" />
            <s:param name="idPositionGeographique" value="%{id}" />
            <s:param name="idItineraire" value="%{idItineraire}"/>
            <s:param name="idLigne" value="%{idLigne}"/>
            <s:param name="actionSuivante" value="%{actionSuivante}"/>
          </s:url>
          <s:a href="%{removeUrl}">
            <img border="0" alt="Delete" src="<s:url value='/images/supprimer.png'/>" title="<s:text name="tooltip.delete"/>">
          </s:a>
        </display:column>
        <display:column titleKey="table.title.name">
          <s:text name="text.zone"/>	<s:property value="%{#attr.father.name}"/>
        </display:column>
        <display:column titleKey="table.title.type">
          <s:text name="%{#attr.father.areaType}"/>
        </display:column>
      </display:table>
    </div>
    <%-- Formulaire de recherche de zone parente --%>
    <div ID="father">
      <s:form id="areaSearchForm" action="search" namespace="/boardingPosition">
        <s:hidden name="idPositionGeographique" value="%{id}"/>
        <s:hidden name="actionSuivante" value="addFather"/>
        <s:hidden name="authorizedType" value="%{authorizedType}" />
        <s:if test="father.id != null">
          <s:submit key="action.replace" />
        </s:if>
        <s:else>
          <s:submit key="action.add" />
        </s:else>
      </s:form>
    </div>
  </div>
</s:if>
