<<<<<<< HEAD:chouette/src/main/webapp/jsp/connectionLink/list.jsp
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="/struts-tags" prefix="s"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>
<%-- Titre et barre de navigation --%>	
<title><s:text name="text.connectionlink.list.title" /></title>
<s:url id="urlCorrespondances" value="/connectionLink/list" includeParams="none"/>
<s:property value="filAriane.addElementFilAriane(getText('text.connectionlink.list.title'), '', #urlCorrespondances)"/>
<div class="panelData">
  <s:property value="filAriane.texteFilAriane" escape="false"/>
</div>
<br>
<%-- Ajouter une correspondance --%>
<div class="actions">
  <s:url action="add" namespace="/connectionLink" id="createCorrespondance"/>
  <s:a href="%{createCorrespondance}"><b><s:text name="text.connectionlink.create.button"/></b></s:a>
</div>

<br>
<div>
  <s:if test="useHastus == 'true'">
    <FIELDSET align="center" style="width: 500px;">
      <LEGEND><b>Import des Correspondances</b></LEGEND>
      <s:form id="uploadCorrespondancesForm1" enctype="multipart/form-data" method="POST" action="importCorrespondances" namespace="/upload">
        <s:file name="fichier" label="%{getText('action.browse')}" />
        <s:hidden name="fichierContentType" value="text/xml; charset=UTF-8"/>
        <s:hidden name="origin" value="connectionLink" />
        <s:submit value="Import Correspondances" formId="uploadCorrespondancesForm1" />
      </s:form>
    </FIELDSET>
    <FIELDSET align="center" style="width: 500px;">
      <LEGEND><b>Export des Correspondances</b></LEGEND>
      <s:form id="downloadCorrespondancesForm2" enctype="multipart/form-data" method="POST" action="exportCorrespondances" namespace="/export">
      	<s:hidden name="origin" value="connectionLink" />
        <s:submit value="Export Correspondances" formId="downloadCorrespondancesForm2" />
      </s:form>
    </FIELDSET>
  </s:if>
</div>

<br>
<div class="panel" id="displaytag"> 
  <display:table name="correspondances" pagesize="20"  requestURI="" id="correspondance" export="false">
    <display:column titleKey="table.title.action" sortable="false">
      <s:url id="removeUrl" action="delete" namespace="/connectionLink">
        <s:param name="idCorrespondance">${correspondance.id}</s:param>
        <s:param name="operationMode">STORE</s:param>
      </s:url>
      <s:url id="editUrl" action="edit" namespace="/connectionLink">
        <s:param name="idCorrespondance">${correspondance.id}</s:param>
      </s:url>
      <s:a href="%{editUrl}">
        <img border="0" alt="Edit" src="<s:url value='/images/editer.png'/>" title="<s:text name="tooltip.edit"/>">
      </s:a>&nbsp;&nbsp;
      <s:a href="%{removeUrl}" onclick="return confirm('%{getText('connectionlink.delete.confirmation')}');">
        <img border="0" alt="Delete" src="<s:url value='/images/supprimer.png'/>" title="<s:text name="tooltip.delete"/>">
      </s:a>
    </display:column>
    <display:column titleKey="table.title.name" property="name"/>
    <display:column titleKey="table.title.comment" property="comment"/>
    <display:column titleKey="table.title.type">
      <s:text name="%{#attr.correspondance.linkType}"/>
    </display:column>
  </display:table>
</div>
