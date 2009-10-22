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
<s:url action="crud_Reseau!edit" id="ajoutReseau"/>
<s:a href="%{ajoutReseau}"><b><s:text name="text.reseau.create.button"/></b></s:a>
</div>
<br>
<%-- Tableau --%>
<div id="displaytag"> 
	<display:table name="reseaux" sort="list" pagesize="20" requestURI="" id="reseau" export="false">
	  	<display:column title="Action" sortable="false">
			<s:url id="removeUrl" action="crud_Reseau!delete">
				<s:param name="idReseau" value="${reseau.id}" />
			</s:url>
			<s:url id="editUrl" action="crud_Reseau!edit">
				<s:param name="idReseau" value="${reseau.id}" />
			</s:url>
			<s:url id="exportsChouettes" action="Export_exportsChouettes">
				<s:param name="idReseau" value="${reseau.id}" />
			</s:url>
			<s:a href="%{editUrl}"><img border="0" src="images/editer.png" title="<s:text name="tooltip.edit"/>"></s:a>&nbsp;&nbsp;
	    	<s:a href="%{removeUrl}" onclick="return confirm('%{getText('reseau.delete.confirmation')}');"><img border="0" src="images/supprimer.png" title="<s:text name="tooltip.delete"/>"></s:a> 	
	  	</display:column>	
	  	<display:column title="Nom" property="ptNetwork.name" sortable="true" headerClass="sortable"/>
	  	<display:column title="Export Lignes">
	  	    <s:a href="%{exportsChouettes}">Export CHOUETTE</s:a>
	  	</display:column>	  	  	
	</display:table>
</div>