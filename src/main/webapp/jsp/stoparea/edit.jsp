<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>
<%-- Titre et barre de navigation --%>
<s:url id="urlPositionGeographiqueUpdate" action="edit" namespace="/stoparea">
  <s:param name="idPositionGeographique" value="%{id}"/>
</s:url>
<s:if test="id != null">
  <title><s:text name="text.zone.update.title" /></title>
  <s:property value="filAriane.addElementFilAriane(getText('text.zone.update.title'), '', #urlPositionGeographiqueUpdate)"/>
</s:if> 
<s:else>
  <title><s:text name="text.zone.create.title" /></title>
  <s:property value="filAriane.addElementFilAriane(getText('text.zone.create.title'), '', #urlPositionGeographiqueUpdate)"/>
</s:else>
<div class="panelData">
  <s:property value="filAriane.texteFilAriane" escape="false"/>
</div>

<%-- Caractéristiques des zones --%>
<div class="panelDataSection"><s:text name="text.zone" /></div>
<div class="panel">
  <s:form cssClass="panelDataInnerForm" validate="true">
    <s:hidden name="idLigne" value="%{idLigne}"/>
    <s:hidden name="idItineraire" value="%{idItineraire}"/>
    <s:hidden name="actionSuivante" value="%{actionSuivante}"/>
    <s:hidden name="typePositionGeographique" value="%{typePositionGeographique}"/>
    <s:hidden name="idPositionGeographique" value="%{id}" />

    <s:textfield key="positionGeographique.objectId" name="objectId" readonly="true" cssClass="texteNonEditable" cssStyle="width: 300px;"/>
    <s:textfield key="positionGeographique.name" name="name" required="true" cssStyle="width: 300px;" />
    <s:textfield key="positionGeographique.comment" name="comment"/>
    <s:textfield key="positionGeographique.nearestTopicName" name="nearestTopicName" />
    <s:textfield key="positionGeographique.streetName" name="streetName" />
    <s:textfield key="positionGeographique.countryCode" name="countryCode" />
    <s:textfield key="positionGeographique.fareCode" name="fareCode" />
    <s:textfield key="positionGeographique.registrationNumber" name="registrationNumber" />

    <s:if test="id != null">
      <s:select key="positionGeographique.areaType" name="areaType" required="true" list="@fr.certu.chouette.struts.enumeration.EnumerationApplication@getStopAreaTypeEnum('CommercialStopStopPlace')" listKey="enumeratedTypeAccess" listValue="textePropriete" disabled="true"/>
    </s:if>
    <s:else>
      <s:select key="positionGeographique.areaType" name="areaType" required="true" list="@fr.certu.chouette.struts.enumeration.EnumerationApplication@getStopAreaTypeEnum('CommercialStopStopPlace')" listKey="enumeratedTypeAccess" listValue="textePropriete"/>
    </s:else>

    <tr style="border: none;"><TD style="border: none; height: 40px;"></TD></tr>
    <tr><TD style="text-align: center;"><b><s:text name="text.positionGeographique.dataGeo.fieldset"/></b></TD></tr>
    <s:textfield key="positionGeographique.projectionType" name="projectionType" />
    <s:textfield key="positionGeographique.x" name="x" />
    <s:textfield key="positionGeographique.y" name="y" />
    <s:if test="id != null">
      <s:select key="positionGeographique.longLatType" name="longLatType" list="@fr.certu.chouette.struts.enumeration.EnumerationApplication@getLongLatEnum()" listKey="enumeratedTypeAccess" listValue="textePropriete"/>
    </s:if>
    <s:else>
      <s:select key="positionGeographique.longLatType" name="longLatType" list="@fr.certu.chouette.struts.enumeration.EnumerationApplication@getLongLatEnum()" listKey="enumeratedTypeAccess" listValue="textePropriete" value="@fr.certu.chouette.service.validation.LongLatType@WGS84"/>
    </s:else>
    <s:textfield key="positionGeographique.latitude" name="latitude" />
    <s:textfield key="positionGeographique.longitude" name="longitude" />

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
  <%-- Zones filles --%>
  <div class="panelDataSection">
    <s:text name="text.positionGeographique.childArea.title" />
  </div>
  <div class="panel">
    <s:div label="Children" id="displaytag">
      <display:table name="children" id="child"  excludedParams="" sort="list" pagesize="10" export="false">
        <display:column title="action">
          <s:url id="editUrl" action="edit" namespace="/stoparea">
            <s:param name="idPositionGeographique">${child.id}</s:param>
          </s:url>
          <s:a href="%{editUrl}">
            <img border="0" alt="Edit" src="/images/editer.png" title="<s:text name="tooltip.edit"/>">
          </s:a>&nbsp;&nbsp;
          <s:url id="removeUrl" action="removeChildFromParent" namespace="/stoparea">
            <s:param name="idPositionGeographique" value="%{positionGeographique.id}" />
            <s:param name="idChild">${child.id}</s:param>
          </s:url>
          <s:a href="%{removeUrl}">
            <img border="0" alt="Delete" src="/images/supprimer.png" title="<s:text name="tooltip.delete"/>">
          </s:a>
        </display:column>
        <display:column title="Nom">
					Zone	<s:property value="%{#attr.child.name}"/>
        </display:column>
        <display:column title="Type">
          <s:text name="%{#attr.child.areaType}"/>
        </display:column>
      </display:table>
    </s:div>
    <%-- Formulaire de recherche de zone fille --%>
    <s:form id="areaSearchForm" action="search"  namespace="/stoparea">
      <s:hidden name="idPositionGeographique" value="%{id}"/>
      <s:hidden name="actionSuivante" value="addChild"/>
      <s:submit key="action.add"/>
    </s:form>
  </div>
</s:if>	

<%-- Zones parentes --%>
<div class="panelDataSection">
  <s:text name="text.positionGeographique.fatherArea.title" />
</div>
<div class="panel">
  <s:div label="father" id="displaytag">
    <display:table name="father"  excludedParams="" sort="list" pagesize="10" export="false">
      <display:column title="action">
        <s:url id="editUrl" action="edit" namespace="/stoparea">
          <s:param name="idPositionGeographique" value="%{father.id}" />
        </s:url>
        <s:a href="%{editUrl}">
          <img border="0" alt="Edit" src="/images/editer.png" title="<s:text name="tooltip.edit"/>">
        </s:a>&nbsp;&nbsp;
        <s:url id="removeUrl" action="removeChildFromParent" namespace="/stoparea">
          <s:param name="idChild" value="%{positionGeographique.id}" />
          <s:param name="idPositionGeographique" value="%{positionGeographique.id}" />
          <s:param name="idItineraire" value="%{idItineraire}"/>
          <s:param name="idLigne" value="%{idLigne}"/>
          <s:param name="actionSuivante" value="%{actionSuivante}"/>
          <s:param name="typePositionGeographique" value="%{typePositionGeographique}"/>
        </s:url>
        <s:a href="%{removeUrl}">
          <img border="0" alt="Delete" src="/images/supprimer.png" title="<s:text name="tooltip.delete"/>">
        </s:a>
      </display:column>
      <display:column title="Nom">
					Zone	<s:property value="%{#attr.child.name}"/>
      </display:column>
      <display:column title="Type">
        <s:text name="%{#attr.child.areaType}"/>
      </display:column>
    </display:table>
  </s:div>
  <%-- Formulaire de recherche de zone parente --%>
  <div ID="father">
    <s:form id="areaSearchForm" action="search" namespace="/stoparea">
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
<script type="text/javascript"><!--
  // <![CDATA[
	
  var positionGeographiques = <%=request.getAttribute("jsonPositionGeographiques")%>;
	
  function autocompletion()
  {
    new Autocompleter.Local('positionGeographiques_father_auto_complete', 'positionGeographiques_auto_complete_list', Object.keys(positionGeographiques), {});
    new Autocompleter.Local('positionGeographiques_child_auto_complete', 'positionGeographiques_auto_complete_list2', Object.keys(positionGeographiques), {});
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
	
  var TridentAutoComplete2 =
    {
    beforeSubmitChild: function()
    {
      var valueEnd = positionGeographiques[$('positionGeographiques_child_auto_complete').value];
      if (valueEnd == null)
        $('positionGeographique_child').value="";
      else
        $('positionGeographique_child').value = valueEnd;
      return true;
    }
  };
	
	
  // ]]>
  --></script>


















