<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="/struts-tags" prefix="s"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>

<%-- Titre et barre de navigation --%>	
<title><s:text name="text.boardingPosition.search.title" /></title>
<s:url id="urlPositionGeographiqueRecherche" action="search" namespace="/boardingPosition">
	<s:param name="idPositionGeographique" value="%{idPositionGeographique}"/>
</s:url>
<s:property value="filAriane.addElementFilAriane(getText('text.boardingPosition.search.title'), '', #urlPositionGeographiqueRecherche)"/>
<div class="panelData">
	<s:property value="filAriane.texteFilAriane" escape="false"/>
</div>

<s:include value="/jsp/commun/messages.jsp" />

<%-- Formulaire de recherche --%>
<div class="panel">
  <s:form cssClass="panelDataInnerForm" action="searchResults" namespace="/boardingPosition">
		<s:hidden name="idPositionGeographique" value="%{idPositionGeographique}" />
		<s:hidden name="authorizedType" value="%{authorizedType}" />
		<s:hidden name="actionSuivante" value="%{actionSuivante}" />
		<s:textfield key="searchCriteria.name"/>
		<s:select emptyOption="true" key="searchCriteria.areaType" list="%{getStopAreaEnum(#attr.authorizedType)}"  listKey="enumeratedTypeAccess" listValue="textePropriete"/>
		<s:textfield key="searchCriteria.areaCentroid.address.countryCode" />
		<s:submit key="action.search" />
  </s:form>
</div>

<%-- Tableau résultat --%>
<div id="displaytag"> 
	<display:table name="positionGeographiquesResultat" pagesize="15" requestURI="" id="positionGeographique" export="false">
	  	<display:column titleKey="table.title.action" sortable="false">
        <s:url id="addUrl" action="%{actionSuivante}" namespace="/boardingPosition">
				<s:param name="idChild" value="%{#attr.positionGeographique.id}" />
				<s:param name="idFather" value="%{#attr.positionGeographique.id}" />
				<s:param name="idPositionGeographique" value="%{idPositionGeographique}" />
			</s:url>
			<s:a href="%{addUrl}"><s:text name="action.select"/></s:a>&nbsp;&nbsp;
	  	</display:column>	  	
	  	<display:column titleKey="table.title.name" property="name" />
	  	<display:column titleKey="table.title.type" >
	  		<s:text name="%{#attr.positionGeographique.areaType}"/>
	  	</display:column>	
	  	<display:column titleKey="table.title.inseeCode" property="areaCentroid.address.countryCode" />
	</display:table>
</div>