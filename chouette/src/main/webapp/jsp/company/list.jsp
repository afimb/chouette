<%@ taglib uri="/struts-tags" prefix="s" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>

<%-- Titre et barre de navigation --%>	
<title><s:text name="text.transporteur.list.title" /></title>
<s:url id="urlTransporteurs" value="/company/list" includeParams="none"/>
<s:property value="filAriane.addElementFilAriane(getText('text.transporteur.list.title'), '', #urlTransporteurs)"/>

<div class="panelData">
	<s:property value="filAriane.texteFilAriane" escape="false"/>
</div>

<br>

<%-- Ajouter transporteur --%>
<div>
<s:url value="/company/add" id="ajoutTransporteur"/>
<s:a href="%{ajoutTransporteur}"><b><s:text name="text.transporteur.create.button"/></b></s:a>
</div>

<br>

<div id="displaytag"> 
	<display:table name="transporteurs" sort="list" pagesize="20" requestURI="" id="transporteur" export="false"> 
	  	<display:column title="Action" sortable="false">
			<s:url id="removeUrl" value="/company/delete">
				<s:param name="idTransporteur">${transporteur.id}</s:param>
			</s:url>
			<s:url id="editUrl" value="/company/edit">
        <s:param name="idTransporteur">${transporteur.id}</s:param>
			</s:url>
			<s:url id="exportsChouette" value="/company/export">
        <s:param name="idTransporteur">${transporteur.id}</s:param>
			</s:url>
			<s:a href="%{editUrl}">
        <img border="0" src="/images/editer.png" title="<s:text name="tooltip.edit"/>">
      </s:a>&nbsp;&nbsp;
	    <s:a href="%{removeUrl}" onclick="return confirm('%{getText('transporteur.delete.confirmation')}');">
        <img border="0" src="/images/supprimer.png" title="<s:text name="tooltip.delete"/>">
      </s:a>
	  	</display:column>	
	  	<display:column title="Nom" property="company.name" sortable="true" headerClass="sortable"/>
	  	<display:column title="Code Postal" property="company.code" sortable="true" headerClass="sortable"/>
	  	<display:column title="Export Lignes">
	  	    <s:a href="%{exportsChouette}">Export CHOUETTE</s:a>
	  	</display:column>	  	  	
	</display:table>
</div>
