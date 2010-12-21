<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="/struts-tags" prefix="s" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>

<%-- Titre et barre de navigation --%>	
<title><s:text name="text.transporteur.list.title" /></title>
<s:url id="urlTransporteurs" action="list" namespace="/company" includeParams="none"/>
<s:property value="filAriane.addElementFilAriane(getText('text.transporteur.list.title'), '', #urlTransporteurs)"/>

<div class="panelData">
  <s:property value="filAriane.texteFilAriane" escape="false"/>
</div>

<s:include value="/jsp/commun/messages.jsp" />

<%-- Ajouter transporteur --%>
<div>
  <s:url action="add" namespace="/company" id="ajoutTransporteur"/>
  <s:a href="%{ajoutTransporteur}"><b><s:text name="text.transporteur.create.button"/></b></s:a>
</div>

<br>
<div id="displaytag"> 
  <display:table name="transporteurs" sort="list" pagesize="20" requestURI="" id="transporteur" export="false">
    <display:column titleKey="table.title.action" sortable="false">
      <s:url id="removeUrl" action="delete" namespace="/company">
        <s:param name="idTransporteur">${transporteur.id}</s:param>
        <s:param name="operationMode">STORE</s:param>
      </s:url>
      <s:url id="editUrl" action="edit" namespace="/company">
        <s:param name="idTransporteur">${transporteur.id}</s:param>
      </s:url>
      <s:url id="exportChouette" action="exportChouette" namespace="/company">
        <s:param name="idTransporteur">${transporteur.id}</s:param>
        <s:param name="exportMode">${'CHOUETTE'}</s:param>
        <s:param name="operationMode">STORE</s:param>
      </s:url>
      <s:url id="exportNeptune" action="exportChouette" namespace="/company">
        <s:param name="idTransporteur">${transporteur.id}</s:param>
        <s:param name="exportMode">${'NEPTUNE'}</s:param>
        <s:param name="operationMode">STORE</s:param>
      </s:url>
      <s:a href="%{editUrl}">
        <img border="0" alt="Edit" src="<s:url value='/images/editer.png'/>" title="<s:text name="tooltip.edit"/>">
      </s:a>&nbsp;&nbsp;
      <s:a href="%{removeUrl}" onclick="return confirm('%{getText('transporteur.delete.confirmation')}');">
        <img border="0" alt="Delete" src="<s:url value='/images/supprimer.png'/>" title="<s:text name="tooltip.delete"/>">
      </s:a>
    </display:column>
    <display:column titleKey="table.title.name" property="company.name" sortable="true" headerClass="sortable"/>
    <display:column titleKey="table.title.postalCode" property="company.code" sortable="true" headerClass="sortable"/>
    <display:column titleKey="table.title.export">
      <s:a href="%{exportChouette}"><s:text name="text.transporteur.export.chouette"/></s:a><br>
      <s:a href="%{exportNeptune}"><s:text name="text.transporteur.export.neptune"/></s:a>
    </display:column>
  </display:table>
</div>
