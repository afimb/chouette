<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="/struts-tags" prefix="s"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>
<%-- Titre et barre de navigation --%>	
<title><s:text name="text.connectionlink.list.title" /></title>
<s:url id="urlCorrespondances" value="/correspondance/list" includeParams="none"/>
<s:property value="filAriane.addElementFilAriane(getText('text.connectionlink.list.title'), '', #urlCorrespondances)"/>
<div class="panelData">
  <s:property value="filAriane.texteFilAriane" escape="false"/>
</div>
<br>
<%-- Ajouter une correspondance --%>
<div class="actions">
  <s:url action="add" namespace="/correspondance" id="createCorrespondance"/>
  <s:a href="%{createCorrespondance}"><b><s:text name="text.connectionlink.create.button"/></b></s:a>
</div>

<br>
<div>
  <s:if test="useHastus == 'true'">
    <FIELDSET align="center" style="width: 500px;">
      <LEGEND><b>Import des Correspondances</b></LEGEND>
      <s:form id="uploadCorrespondancesForm1" enctype="multipart/form-data" method="POST">
        <s:file name="fichier" label="%{getText('action.browse')}" />
        <s:hidden name="fichierContentType" value="text/xml; charset=ISO-8859-1"/>
        <s:submit value="Import Correspondances" formId="uploadCorrespondancesForm1" action="Import_importCorrespondances" />
      </s:form>
    </FIELDSET>
    <FIELDSET align="center" style="width: 500px;">
      <LEGEND><b>Export des Correspondances</b></LEGEND>
      <s:form id="uploadCorrespondancesForm2" enctype="multipart/form-data" method="POST">
        <s:submit value="Export Correspondances" formId="uploadCorrespondancesForm2" action="Export_exportCorrespondances" />
      </s:form>
    </FIELDSET>
  </s:if>
</div>

<br>
<div class="panel" id="displaytag"> 
  <display:table name="correspondances" pagesize="20"  requestURI="" id="correspondance" export="false">
    <display:column title="Action" sortable="false">
      <s:url id="removeUrl" action="delete" namespace="/correspondance">
        <s:param name="idCorrespondance">${correspondance.id}</s:param>
        <s:param name="operationMode">STORE</s:param>
      </s:url>
      <s:url id="editUrl" action="edit" namespace="/correspondance">
        <s:param name="idCorrespondance">${correspondance.id}</s:param>
      </s:url>
      <s:a href="%{editUrl}">
        <img border="0" alt="Edit" src="images/editer.png" title="<s:text name="tooltip.edit"/>">
      </s:a>&nbsp;&nbsp;
      <s:a href="%{removeUrl}" onclick="return confirm('%{getText('connectionlink.delete.confirmation')}');">
        <img border="0" alt="Delete" src="images/supprimer.png" title="<s:text name="tooltip.delete"/>">
      </s:a>
    </display:column>
    <display:column title="Nom" property="name"/>
    <display:column title="Commentaire" property="comment"/>
    <display:column title="Type">
      <s:text name="%{#attr.correspondance.linkType}"/>
    </display:column>
  </display:table>
</div>