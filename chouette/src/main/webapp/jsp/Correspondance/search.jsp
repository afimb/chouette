<%@ taglib uri="/struts-tags" prefix="s"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>
<%-- Titre et barre de navigation --%>	
<title><s:text name="text.connectionlink.search.title" /></title>
<s:url id="urlCorrespondanceRecherche" action="search_Correspondance">
	<s:param name="idCorrespondance" value="%{correspondance.id}"/>
</s:url>
<s:property value="filAriane.addElementFilAriane(getText('text.connectionlink.search.title'), '', #urlCorrespondanceRecherche)"/>
<div class="panelData">
	<s:property value="filAriane.texteFilAriane" escape="false"/>
</div>
<br>
<%-- Formulaire de recherche --%>
<div class="panel">
	<s:form cssClass="panelDataInnerForm" action="Correspondance_searchResults">
		<s:hidden name="typePositionGeographique" value="zone" />
		<s:hidden name="idCorrespondance" value="%{idCorrespondance}" />
		<s:hidden name="actionSuivante" value="%{actionSuivante}" />
		<s:textfield key="criteria.name"/>		
		<s:select emptyOption="true" key="criteria.areaType" list="@fr.certu.chouette.ihm.enumeration.EnumerationApplication@getStopAreaTypeEnum('${authorizedType}')" listKey="enumeratedTypeAccess" listValue="textePropriete"/>
		<s:textfield key="criteria.countryCode" />
		
		<tr>
  			<td colspan="2">
  				<s:submit value="%{getText('action.search')}" theme="simple" cssStyle="float: right;"/>
  				<s:url id="editCorrespondance" action="crud_Correspondance!edit" includeParams="none">
					<s:param name="idCorrespondance" value="%{idCorrespondance}" />
				</s:url>
  				<input type="button" onclick="location.href='<s:property value="editCorrespondance"/>';"  style="float: right;" value='<s:text name="action.cancel"/>'>
  			</td>
  		</tr>
	</s:form>
</div>
<br>
<%-- Tableau résultat --%>
<div class="panel" id="displaytag"> 
	<display:table name="positionGeographiquesResultat" pagesize="15" requestURI="" id="positionGeographique">
	  	<display:column title="Action" sortable="false">
			<s:url id="addUrl" action="Correspondance_%{actionSuivante}">
				<s:param name="idCorrespondance" value="%{idCorrespondance}" />
				<s:param name="idPositionGeographique" value="${positionGeographique.id}" />
			</s:url>
			<s:a href="%{addUrl}"><s:text name="action.select"/></s:a>&nbsp;&nbsp;
	  	</display:column>	  	
	  	<display:column title="Name" property="name" />
	  	<display:column title="Type" >
	  		<s:text name="${positionGeographique.areaType}"/>
	  	</display:column>	
	  	<display:column title="Code INSEE" property="countryCode" />	  		  	
	</display:table>
</div>