<%@ taglib uri="/struts-tags" prefix="s"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>
<%-- Titre et barre de navigation --%>	
<title><s:text name="text.connectionlink.search.title" /></title>
<s:url id="urlCorrespondanceRecherche" action="search" namespace="/correspondance">
  <s:param name="idCorrespondance" value="%{idCorrespondance}"/>
</s:url>
<s:property value="filAriane.addElementFilAriane(getText('text.connectionlink.search.title'), '', #urlCorrespondanceRecherche)"/>
<div class="panelData">
  <s:property value="filAriane.texteFilAriane" escape="false"/>
</div>
<br>
<%-- Formulaire de recherche --%>
<div class="panel">
  <s:form cssClass="panelDataInnerForm" validate="true" namespace="/correspondance">
    <s:hidden name="typePositionGeographique" value="zone" />
    <s:hidden name="idCorrespondance" value="%{idCorrespondance}" />
    <s:hidden name="actionSuivante" value="%{actionSuivante}" />
    <s:textfield key="criteria.name"/>
    <s:select emptyOption="true" key="criteria.areaType" list="@fr.certu.chouette.struts.enumeration.EnumerationApplication@getStopAreaTypeEnum('%{#attr.authorizedType}')" listKey="enumeratedTypeAccess" listValue="textePropriete"/>
    <s:textfield key="criteria.countryCode" />
    <tr>
      <td colspan="2">
        <s:submit key="action.search" action="doSearch"  theme="simple" cssStyle="float: right;"/>
        <s:submit key="action.cancel" action="cancelSearch" theme="simple" cssStyle="float: right;"/>
      </td>
    </tr>
  </s:form>
</div>
<br>

<%-- Tableau résultat --%>
<div class="panel" id="displaytag"> 
  <display:table name="positionGeographiquesResultat" pagesize="15" requestURI="" id="positionGeographique">
    <display:column title="Action" sortable="false">
      <s:url id="addUrl" action="%{actionSuivante}" namespace="/correspondance">
        <s:param name="idCorrespondance" value="%{idCorrespondance}" />
        <s:param name="idPositionGeographique" value="%{#attr.positionGeographique.id}" />
      </s:url>
      <s:a href="%{addUrl}"><s:text name="action.select"/></s:a>&nbsp;&nbsp;
    </display:column>
    <display:column title="Name" property="name" />
    <display:column title="Type" >
      <s:text name="%{#attr.positionGeographique.areaType}"/>
    </display:column>
    <display:column title="Code INSEE" property="countryCode" />
  </display:table>
</div>