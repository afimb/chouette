<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>

<%-- Titre et barre de navigation --%>
<s:url id="urlPositionGeographiques" action="list" namespace="/stopPlace" includeParams="none">
  <s:param name="typePositionGeographique" value="%{typePositionGeographique}"/>
</s:url>
<title><s:text name="text.zone.list.title" /></title>
<s:property value="filAriane.addElementFilAriane(getText('text.zone.list.title'), '', #urlPositionGeographiques)"/>
<div class="panelData">
  <s:property value="filAriane.texteFilAriane" escape="false"/>
</div>

<s:include value="/jsp/commun/messages.jsp" />

<%-- Filtre --%>
<div>
  <s:form action="list" namespace="/stopPlace">
    <s:select name="idReseau" label="%{getText('filtre.select.reseau')}" value="%{idReseau}" list="reseaux" listKey="id" listValue="name" headerKey="" headerValue="%{getText('filtre.reseau.dropDownListItem.tous')}" />
    <s:textfield name="nomArret" label="%{getText('filtre.select.nomArret')}"></s:textfield>
    <s:textfield name="codeInsee" label="%{getText('filtre.select.codeInsee')}"></s:textfield>
    <s:submit value="%{getText('action.filtrer')}"/>
  </s:form>
</div>

<br>

<%-- Actions --%>
<div class="actions">
  <s:url action="add" namespace="/stopPlace" id="editPositionGeographique">
    <s:param name="typePositionGeographique" value="%{typePositionGeographique}" />
  </s:url>
  <s:a href="%{editPositionGeographique}"><b><s:text name="text.zone.create.button"/></b></s:a>
</div>

<br>

<%-- Tableau --%>
<div id="displaytag"> 
  <display:table name="positionGeographiques" pagesize="20" requestURI="" id="positionGeographique" export="false">
    <display:column titleKey="table.title.action" sortable="false">
      <%-- BOUTON EDITER --%>
      <s:url id="editUrl" action="edit" namespace="/stopPlace">
        <s:param name="idPositionGeographique">${positionGeographique.id}</s:param>
      </s:url>
      <s:a href="%{editUrl}">
        <img border="0" alt="Edit" src="<s:url value='/images/editer.png'/>" title="<s:text name="tooltip.edit"/>">
      </s:a>&nbsp;&nbsp;
      <%-- BOUTON SUPPRIMER --%>
      <s:url id="deletePositionGeographique" action="delete" namespace="/stopPlace">
        <s:param name="idPositionGeographique">${positionGeographique.id}</s:param>
        <s:param name="operationMode" value="%{'STORE'}" />
      </s:url>
      <s:a href="%{deletePositionGeographique}" onclick="return confirm('%{getText('zone.delete.confirmation')}');">
        <img border="0" alt="Delete" src="<s:url value='/images/supprimer.png'/>" title="<s:text name="tooltip.delete"/>">
      </s:a>
    </display:column>
    <display:column titleKey="table.title.name" property="name" />
    <display:column titleKey="table.title.id" property="objectId" />
    <display:column titleKey="table.title.type" >
      <s:text name="%{#attr.positionGeographique.areaType}"/>
    </display:column>
  </display:table>
</div>