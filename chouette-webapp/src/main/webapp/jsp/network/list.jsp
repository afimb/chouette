<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="/struts-tags" prefix="s" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>

<%-- Titre et barre de navigation --%>	
<title><s:text name="text.reseau.list.title" /></title>
<s:url id="urlReseaux" action="list" namespace="/network" includeParams="none"/>
<s:property value="filAriane.addElementFilAriane(getText('text.reseau.list.title'), '', #urlReseaux)"/>
<div class="panelData">
  <s:property value="filAriane.texteFilAriane" escape="false"/>
</div>

<s:include value="/jsp/commun/messages.jsp" />

<%-- Ajouter réseau --%>
<div>
  <s:url action="add" namespace="/network" id="ajoutReseau"/>
  <s:a href="%{ajoutReseau}"><b><s:text name="text.reseau.create.button"/></b></s:a>
</div>
<br>
<%-- Tableau --%>
<div id="displaytag"> 
  <display:table name="reseaux" sort="list" pagesize="20" requestURI="" id="reseau" export="false">
    <display:column titleKey="table.title.action" sortable="false">
      <s:url id="removeUrl" action="delete" namespace="/network">
        <s:param name="idReseau">${reseau.id}</s:param>
        <s:param name="operationMode" value="%{'STORE'}"/>
      </s:url>
      <s:url id="editUrl" action="edit" namespace="/network">
        <s:param name="idReseau">${reseau.id}</s:param>
      </s:url>
      <s:url id="exportChouette" action="exportChouette" namespace="/network">
        <s:param name="idReseau">${reseau.id}</s:param>
        <s:param name="exportMode">${'CHOUETTE'}</s:param>
        <s:param name="operationMode">STORE</s:param>
      </s:url>
      <s:url id="exportNeptune" action="exportChouette" namespace="/network">
        <s:param name="idReseau">${reseau.id}</s:param>
        <s:param name="exportMode">${'NEPTUNE'}</s:param>
        <s:param name="operationMode">STORE</s:param>
      </s:url>
      <s:url id="exportGTFS" action="exportChouette" namespace="/network">
        <s:param name="idReseau">${reseau.id}</s:param>
        <s:param name="exportMode">${'GTFS'}</s:param>
        <s:param name="operationMode">STORE</s:param>
      </s:url>
      <s:url id="exportGeoportail" action="exportChouette" namespace="/network">
        <s:param name="idReseau">${reseau.id}</s:param>
        <s:param name="exportMode">${'GEOPORTAIL'}</s:param>
        <s:param name="operationMode">STORE</s:param>
      </s:url>
      <s:a href="%{editUrl}">
        <img border="0" src="<s:url value='/images/editer.png'/>" alt="Edit" title="<s:text name="tooltip.edit"/>" >
      </s:a>&nbsp;&nbsp;
      <s:a href="%{removeUrl}" onclick="return confirm('%{getText('reseau.delete.confirmation')}');">
        <img border="0" alt="Delete" src="<s:url value='/images/supprimer.png'/>" title="<s:text name="tooltip.delete"/>">
      </s:a>
    </display:column>
    <display:column titleKey="table.title.name" property="ptNetwork.name" sortable="true" headerClass="sortable"/>
    <display:column titleKey="table.title.export">
      <s:a href="%{exportChouette}"><s:text name="text.reseau.export.chouette"/></s:a><br>
      <s:a href="%{exportNeptune}"><s:text name="text.reseau.export.neptune"/></s:a>
      <s:if test="useGtfs == 'true'">
        <br><s:a href="%{exportGTFS}"><s:text name="text.reseau.export.gtfs"/></s:a>
      </s:if>
      <s:if test="useGeoportail == 'true'">
        <br><s:a href="%{exportGeoportail}"><s:text name="text.reseau.export.geoportail"/></s:a>
      </s:if>
    </display:column>
  </display:table>
</div>
