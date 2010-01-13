<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>
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
<%-- Caractéristiques des PositionGeographiques --%>
<div class="panelDataSection"><s:text name="text.arretPhysique" /></div>
<div class="panel">
  <s:form cssClass="panelDataInnerForm">
    <s:hidden name="idLigne" value="%{idLigne}"/>
    <s:hidden name="idItineraire" value="%{idItineraire}"/>
    <s:hidden name="actionSuivante" value="%{actionSuivante}"/>
    <s:hidden name="idPositionGeographique" value="%{id}" />
    <s:hidden name="operationMode" value="STORE" />
    <s:hidden key="actionMethod" value="%{actionMethod}"/>

    <s:textfield key="objectId" readonly="true" cssClass="texteNonEditable" cssStyle="width: 300px;"/>
    <s:textfield key="name" required="true" cssStyle="width: 300px;" />
    <s:textfield key="comment" />
    <s:textfield key="nearestTopicName" />
    <s:textfield key="streetName" />
    <s:textfield key="countryCode" />
    <s:textfield key="fareCode" />
    <s:textfield key="registrationNumber" />

    <s:if test="id != null">
      <s:select key="areaType" required="true" list="%{getStopAreaEnum('QuayBoardingPosition')}" listKey="enumeratedTypeAccess" listValue="textePropriete" disabled="true"/>
    </s:if>
    <s:else>
      <s:select key="areaType" required="true" list="%{getStopAreaEnum('QuayBoardingPosition')}" listKey="enumeratedTypeAccess" listValue="textePropriete"/>
    </s:else>

    <tr style="border: none;"><TD style="border: none; height: 40px;"></TD></tr>
    <tr><TD style="text-align: center;"><b><s:text name="text.positionGeographique.dataGeo.fieldset"/></b></TD></tr>
    <s:textfield key="projectionType" name="projectionType"/>
    <s:textfield key="x" />
    <s:textfield key="y" />
    <s:if test="id != null">
      <s:select key="longLatType" list="longLatEnum" listKey="enumeratedTypeAccess" listValue="textePropriete"/>
    </s:if>
    <s:else>
      <s:select key="longLatType" list="longLatEnum" listKey="enumeratedTypeAccess" listValue="textePropriete" value="%{'WGS84'}"/>
    </s:else>
    <s:textfield key="latitude" />
    <s:textfield key="longitude" />

    <%-- Actions --%>
    <tr>
      <td colspan="2">
        <s:if test="id != null">
          <s:submit key="action.update" action="%{actionMethod}"  theme="simple" cssStyle="float: right;"/>
        </s:if>
        <s:else>
          <s:submit key="action.create" action="%{actionMethod}" theme="simple" cssStyle="float: right;"/>
        </s:else>
        <s:submit key="action.cancel" action="cancel" theme="simple" cssStyle="float: right;"/>
      </td>
    </tr>

    <%-- Ajout des balises tr et td pour le faire apparaitre dans le tableau --%>
    <tr><td colspan="2"><s:include value="/jsp/commun/asterisque.jsp" /></td></tr>
  </s:form>
</div>
<s:if test="id != null">
  <%-- Itinéraires liés à l'arrêt physique --%>
  <div class="panelDataSection">
    <s:text name="text.stopPlace.routes"/>
  </div>
  <div class="panel">
    <s:div label="Iti" id="displaytag">
      <display:table name="itineraires" uid="itineraire" sort="list" pagesize="10" export="false">
        <display:column title="Nom Itineraire" sortable="true" headerClass="sortable">
          <s:url id="arretSurItineraire" action="edit" namespace="arretSurItineraire" includeParams="none">
            <s:param name="idItineraire">%{#attr.itineraire.id}</s:param>
          </s:url>
          <s:a href="%{arretSurItineraire}"><s:property value="%{#attr.itineraire.name}"/></s:a>
        </display:column>
        <display:column title="Horaires" sortable="true" headerClass="sortable">
          <s:url id="horairesDePassage" action="list" namespace="/horairesDePassage" includeParams="none">
            <s:param name="idItineraire">%{#attr.itineraire.id}</s:param>
            <s:param name="idLigne">%{#attr.itineraire.idLigne}</s:param>
          </s:url>
          <s:a href="%{horairesDePassage}"><s:text name="text.vehicleJourneyAtStop"/></s:a>
        </display:column>
        <display:column title="Nom Ligne" sortable="true" headerClass="sortable">
          <s:url id="editLigne" action="edit" namespace="/line" includeParams="none">
            <s:param name="idLigne">%{#attr.itineraire.idLigne}</s:param>
          </s:url>
          <s:a href="%{editLigne}"><s:property value="%{getLigne(#attr.itineraire.id).name}" /></s:a>
        </display:column>
        <display:column title="Nom Reseau" sortable="true" headerClass="sortable">
          <s:url id="editReseau" action="edit" namespace="/network" includeParams="none">
            <s:param name="idReseau">%{#attr.itineraire.idReseau}</s:param>
          </s:url>
          <s:a href="%{editReseau}"><s:property value="%{getReseau(#attr.itineraire.idLigne).name}" /></s:a>
        </display:column>
      </display:table>
    </s:div>
  </div>
</s:if>