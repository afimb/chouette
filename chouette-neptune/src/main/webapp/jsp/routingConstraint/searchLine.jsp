<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="/struts-tags" prefix="s"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>

<%-- Titre et barre de navigation --%>	
<title><s:text name="text.line.search.title" /></title>
<s:url id="urlLigneRecherche" action="searchLine" namespace="/routingConstraint">
	<s:param name="idPositionGeographique" value="%{idPositionGeographique}"/>
</s:url>
<s:property value="filAriane.addElementFilAriane(getText('text.line.search.title'), '', #urlLigneRecherche)"/>
<div class="panelData">
	<s:property value="filAriane.texteFilAriane" escape="false"/>
</div>

<s:include value="/jsp/commun/messages.jsp" />

<%-- Formulaire de recherche --%>
<div class="panel">
  <s:form cssClass="panelDataInnerForm" action="searchLineResults" namespace="/routingConstraint">
		<s:hidden name="idPositionGeographique" value="%{idPositionGeographique}" />
		<s:hidden name="authorizedType" value="%{authorizedType}" />
		<s:hidden name="actionSuivante" value="%{actionSuivante}" />
		    <s:select	key="lineCriteria.ptNetwork.id"
              label="%{getText('filtre.select.reseau')}"
              list="networks"
              listKey="id"
              listValue="name"
              headerKey=""
              headerValue="%{getText('filtre.reseau.dropDownListItem.tous')}" />

    <s:select 	key="lineCriteria.company.id"
               label="%{getText('filtre.select.transporteur')}"
               list="companies"
               listKey="id"
               listValue="name"
               headerKey=""
               headerValue="%{getText('filtre.transporteur.dropDownListItem.tous')}" />
		
		<s:textfield key="lineCriteria.name"/>
		<s:textfield key="lineCriteria.number"/>
		<s:submit key="action.search" />
  </s:form>
</div>

<%-- Tableau résultat --%>
<div id="displaytag"> 
	<display:table name="linesResultat" pagesize="15" requestURI="" id="line" export="false">
	  	<display:column titleKey="table.title.action" sortable="false">
        <s:url id="addUrl" action="%{actionSuivante}" namespace="/routingConstraint">
				<s:param name="idLine" value="%{#attr.line.id}" />
				<s:param name="idPositionGeographique" value="%{idPositionGeographique}" />
			</s:url>
			<s:a href="%{addUrl}"><s:text name="action.select"/></s:a>&nbsp;&nbsp;
	  	</display:column>	  	
	  	<display:column titleKey="table.title.name" property="name" />
	  	<display:column titleKey="table.title.number" property="number" />
	</display:table>
</div>