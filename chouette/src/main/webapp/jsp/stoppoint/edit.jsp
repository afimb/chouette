<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>
<%-- Titre et barre de navigation --%>
<s:url id="urlPositionGeographiqueUpdate" action="edit" namespace="/stoppoint">
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

    <s:textfield key="positionGeographique.objectId" name="objectId" readonly="true" cssClass="texteNonEditable" cssStyle="width: 300px;"/>
    <s:textfield key="positionGeographique.name" name="name" required="true" cssStyle="width: 300px;" />
    <s:textfield key="positionGeographique.comment" name="comment"/>
    <s:textfield key="positionGeographique.nearestTopicName" name="nearestTopicName"/>
    <s:textfield key="positionGeographique.streetName" name="streetName"/>
    <s:textfield key="positionGeographique.countryCode" name="countryCode"/>
    <s:textfield key="positionGeographique.fareCode" name="fareCode"/>
    <s:textfield key="positionGeographique.registrationNumber" name="registrationNumber"/>

    <s:if test="id != null">
      <s:select key="positionGeographique.areaType" name="areaType" required="true" list="@fr.certu.chouette.struts.enumeration.EnumerationApplication@getStopAreaTypeEnum('QuayBoardingPosition')" listKey="enumeratedTypeAccess" listValue="textePropriete"  disabled="true"/>
    </s:if>
    <s:else>
      <s:select key="positionGeographique.areaType" name="areaType" required="true" list="@fr.certu.chouette.struts.enumeration.EnumerationApplication@getStopAreaTypeEnum('QuayBoardingPosition')" listKey="enumeratedTypeAccess" listValue="textePropriete"/>
    </s:else>

    <tr style="border: none;"><TD style="border: none; height: 40px;"></TD></tr>
    <tr><TD style="text-align: center;"><b><s:text name="text.positionGeographique.dataGeo.fieldset"/></b></TD></tr>
    <s:textfield key="positionGeographique.projectionType" name="projectionType"/>
    <s:textfield key="positionGeographique.x" name="x"/>
    <s:textfield key="positionGeographique.y" name="y"/>
    <s:if test="id != null">
      <s:select key="positionGeographique.longLatType" name="longLatType" list="@fr.certu.chouette.struts.enumeration.EnumerationApplication@getLongLatEnum()" listKey="enumeratedTypeAccess" listValue="textePropriete"/>
    </s:if>
    <s:else>
      <s:select key="positionGeographique.longLatType" name="longLatType" list="@fr.certu.chouette.struts.enumeration.EnumerationApplication@getLongLatEnum()" listKey="enumeratedTypeAccess" listValue="textePropriete" value="@fr.certu.chouette.service.validation.LongLatType@WGS84"/>
    </s:else>
    <s:textfield key="positionGeographique.latitude"  name="latitude" />
    <s:textfield key="positionGeographique.longitude"  name="longitude" />

    <%-- Actions --%>
    <tr>
      <td colspan="2">
        <s:if test="id != null">
          <s:submit key="action.update" action="%{actionMethod}"  theme="simple" cssStyle="float: right;"/>
        </s:if>
        <s:else>
          <s:submit key="action.create" action="%{actionMethod}" theme="simple" cssStyle="float: right;"/>
        </s:else>
        <s:submit key="action.cancel" action="%{actionMethod}" theme="simple" cssStyle="float: right;"/>
      </td>
    </tr>

    <%-- Ajout des balises tr et td pour le faire apparaitre dans le tableau --%>
    <tr><td colspan="2"><s:include value="/jsp/commun/asterisque.jsp" /></td></tr>
  </s:form>
</div>
<s:if test="id != null">
  <%-- Itinéraires liés à l'arrêt physique --%>
  <div class="panelDataSection">
			Itinéraires liés à l'arrêt physique
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
          <s:a href="%{horairesDePassage}">Horaires</s:a>
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