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
        <display:column titleKey="route.title.name" sortable="true" headerClass="sortable">
          <s:url id="arretSurItineraire" action="list" namespace="/stoppointOnRoute" includeParams="none">
            <s:param name="idItineraire">%{#attr.itineraire.id}</s:param>
            <s:param name="idLigne">%{#attr.itineraire.idLigne}</s:param>
          </s:url>
          <s:a href="%{arretSurItineraire}"><s:property value="%{#attr.itineraire.name}"/></s:a>
        </display:column>
        <display:column titleKey="route.title.passingtime" sortable="true" headerClass="sortable">
          <s:url id="horairesDePassage" action="list" namespace="/vehicleJourneyAtStop" includeParams="none">
            <s:param name="idItineraire">%{#attr.itineraire.id}</s:param>
            <s:param name="idLigne">%{#attr.itineraire.idLigne}</s:param>
          </s:url>
          <s:a href="%{horairesDePassage}"><s:text name="text.vehicleJourneyAtStop"/></s:a>
        </display:column>
        <display:column titleKey="route.title.linename"sortable="true" headerClass="sortable">
          <s:url id="editLigne" action="edit" namespace="/line" includeParams="none">
            <s:param name="idLigne">%{#attr.itineraire.idLigne}</s:param>
          </s:url>
          <s:a href="%{editLigne}"><s:property value="%{getLigne(#attr.itineraire.id).name}" /></s:a>
        </display:column>
        <display:column titleKey="route.title.networkname" sortable="true" headerClass="sortable">
          <s:url id="editReseau" action="edit" namespace="/network" includeParams="none">
            <s:param name="idReseau">%{#attr.itineraire.idReseau}</s:param>
          </s:url>
          <s:a href="%{editReseau}"><s:property value="%{getReseau(#attr.itineraire.idLigne).name}" /></s:a>
        </display:column>
      </display:table>
    </s:div>
  </div>

  <%-- Zones parentes --%>
  <div class="panelDataSection">
    <s:text name="text.positionGeographique.fatherArea.title" />
  </div>
  <div class="panel">
    <s:div label="father" id="displaytag">
      <display:table name="father"  excludedParams="" sort="list" pagesize="10" export="false">
        <display:column titleKey="table.title.action">
          <s:url id="editUrl" action="edit" namespace="/boardingPosition">
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
    </s:div>
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
<script type="text/javascript"><!--
  // <![CDATA[

  var positionGeographiques = <%=request.getAttribute("jsonPositionGeographiques")%>;

  function autocompletion()
  {
    new Autocompleter.Local('positionGeographiques_father_auto_complete', 'positionGeographiques_auto_complete_list', Object.keys(positionGeographiques), {});
    $('positionGeographiques_auto_father_complete').focus();
  }

  Event.observe(window, 'load', autocompletion);

  var TridentAutoComplete =
    {
    beforeSubmit: function()
    {
      var value = positionGeographiques[$('positionGeographiques_father_auto_complete').value];
      if (value == null)
        $('positionGeographique_father').value="";
      else
        $('positionGeographique_father').value = value;
      return true;
    }
  };

  // ]]>
  --></script>