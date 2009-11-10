<%@ taglib uri="/struts-tags" prefix="s" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>

<%-- Titre et barre de navigation --%>	
<title><s:text name="text.reseau.list.title" /></title>
<s:url id="urlReseaux" action="liste_Reseau" includeParams="none"/>
<s:property value="filAriane.addElementFilAriane(getText('text.reseau.list.title'), '', #urlReseaux)"/>
<div class="panelData">
  <s:property value="filAriane.texteFilAriane" escape="false"/>
</div>
<br>
<%-- Ajouter réseau --%>
<div>
  <s:url value="/reseau/add" id="ajoutReseau"/>
  <s:a href="%{ajoutReseau}"><b><s:text name="text.reseau.create.button"/></b></s:a>
</div>
<br>
<%-- Tableau --%>
<div id="displaytag"> 
  <display:table name="reseaux" sort="list" pagesize="20" requestURI="" id="reseau" export="false">
    <display:column title="Action" sortable="false">
      <s:url id="removeUrl" action="delete" namespace="/reseau">
        <s:param name="idReseau">${reseau.id}</s:param>
      </s:url>
      <s:url id="editUrl" action="edit" namespace="/reseau">
        <s:param name="idReseau">${reseau.id}</s:param>
      </s:url>
      <s:url id="exportsChouettes" action="Export_exportsChouettes" namespace="/reseau">
        <s:param name="idReseau">${reseau.id}</s:param>
      </s:url>
      <s:a href="%{editUrl}">
        <img border="0" class="edit" alt="Edit" title="<s:text name="tooltip.edit"/>" >
      </s:a>&nbsp;&nbsp;
      <s:a href="%{removeUrl}" onclick="return confirm(getText('reseau.delete.confirmation'));">
        <img border="0" class="delete" alt="Delete" title="<s:text name="tooltip.delete"/>">
      </s:a>
    </display:column>
    <display:column title="Nom" property="ptNetwork.name" sortable="true" headerClass="sortable"/>
    <display:column title="Export Lignes">
      <s:a href="%{exportsChouettes}">Export CHOUETTE</s:a>
    </display:column>
  </display:table>
</div>