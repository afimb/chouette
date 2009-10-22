<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>
<%-- Titre et barre de navigation --%>
<s:url id="urlPositionGeographiqueUpdate" action="crud_PositionGeographique!edit">
	<s:param name="idPositionGeographique" value="%{positionGeographique.id}"/>
</s:url>
<s:if test="positionGeographique.id != null">
	<s:if test="typePositionGeographique == 'arretPhysique'">
		<title><s:text name="text.arretPhysique.update.title" /></title>
		<s:property value="filAriane.addElementFilAriane(getText('text.arretPhysique.update.title'), '', #urlPositionGeographiqueUpdate)"/>	
	</s:if>
	<s:else>
		<title><s:text name="text.zone.update.title" /></title>
		<s:property value="filAriane.addElementFilAriane(getText('text.zone.update.title'), '', #urlPositionGeographiqueUpdate)"/>	
	</s:else>	
</s:if> 
<s:else>
	<s:if test="typePositionGeographique == 'arretPhysique'">
		<title><s:text name="text.arretPhysique.create.title" /></title>
		<s:property value="filAriane.addElementFilAriane(getText('text.arretPhysique.create.title'), '', #urlPositionGeographiqueUpdate)"/>	
	</s:if>
	<s:else>
		<title><s:text name="text.zone.create.title" /></title>
		<s:property value="filAriane.addElementFilAriane(getText('text.zone.create.title'), '', #urlPositionGeographiqueUpdate)"/>	
	</s:else>	
</s:else>
<div class="panelData">
	<s:property value="filAriane.texteFilAriane" escape="false"/>
</div>
<%-- Caractéristiques des PositionGeographiques --%>
<s:if test="typePositionGeographique eq 'arretPhysique'">
	<div class="panelDataSection"><s:text name="text.arretPhysique" /></div>
</s:if>
<s:else>
	<div class="panelDataSection"><s:text name="text.zone" /></div>
</s:else>	
<div class="panel">
	<s:form cssClass="panelDataInnerForm">
		<s:hidden name="idLigne" value="%{idLigne}"/>
		<s:hidden name="idItineraire" value="%{idItineraire}"/>
		<s:hidden name="actionSuivante" value="%{actionSuivante}"/>
		<s:hidden name="typePositionGeographique" value="%{typePositionGeographique}"/>		
		<s:if test="positionGeographique.id != null">
			<s:hidden name="idPositionGeographique" value="%{positionGeographique.id}" />
		</s:if>

		<s:textfield key="positionGeographique.objectId" readonly="true" cssClass="texteNonEditable" cssStyle="width: 300px;"/>
		<s:textfield key="positionGeographique.name" required="true" cssStyle="width: 300px;" />
		<s:textfield key="positionGeographique.comment"/>
		<s:textfield key="positionGeographique.nearestTopicName" />
		<s:textfield key="positionGeographique.streetName" />
		<s:textfield key="positionGeographique.countryCode" />
		<s:textfield key="positionGeographique.fareCode" />
		<s:textfield key="positionGeographique.registrationNumber" />
		
		<s:if test="typePositionGeographique eq 'zone'">
			<s:if test="positionGeographique.id != null">
				<s:select key="positionGeographique.areaType" required="true" list="@fr.certu.chouette.ihm.enumeration.EnumerationApplication@getStopAreaTypeEnum('CommercialStopStopPlace')" listKey="enumeratedTypeAccess" listValue="textePropriete" disabled="true"/>
			</s:if>
			<s:else>	
				<s:select key="positionGeographique.areaType" required="true" list="@fr.certu.chouette.ihm.enumeration.EnumerationApplication@getStopAreaTypeEnum('CommercialStopStopPlace')" listKey="enumeratedTypeAccess" listValue="textePropriete"/>
			</s:else>	
		</s:if>
		<s:else>
			<s:if test="positionGeographique.id != null">
				<s:select key="positionGeographique.areaType" required="true" list="@fr.certu.chouette.ihm.enumeration.EnumerationApplication@getStopAreaTypeEnum('QuayBoardingPosition')" listKey="enumeratedTypeAccess" listValue="textePropriete"  disabled="true"/>
			</s:if>
			<s:else>
				<s:select key="positionGeographique.areaType" required="true" list="@fr.certu.chouette.ihm.enumeration.EnumerationApplication@getStopAreaTypeEnum('QuayBoardingPosition')" listKey="enumeratedTypeAccess" listValue="textePropriete"/>
			</s:else>
		</s:else>
	
		<tr style="border: none;"><TD style="border: none; height: 40px;"></TD></tr>
		<tr><TD style="text-align: center;"><b><s:text name="text.positionGeographique.dataGeo.fieldset"/></b></TD></tr>
		<s:textfield key="positionGeographique.projectionType" />
		<s:textfield key="positionGeographique.x" /> 
		<s:textfield key="positionGeographique.y" />
		<s:if test="positionGeographique.id != null">	
			<s:select key="positionGeographique.longLatType" list="@fr.certu.chouette.ihm.enumeration.EnumerationApplication@getLongLatEnum()" listKey="enumeratedTypeAccess" listValue="textePropriete"/>
		</s:if>
		<s:else>
			<s:select key="positionGeographique.longLatType" list="@fr.certu.chouette.ihm.enumeration.EnumerationApplication@getLongLatEnum()" listKey="enumeratedTypeAccess" listValue="textePropriete" value="@fr.certu.chouette.service.validation.LongLatType@WGS84"/>
		</s:else>
		<s:textfield key="positionGeographique.latitude" />
		<s:textfield key="positionGeographique.longitude" />
		
		<%-- Actions --%>
		<s:if test="positionGeographique.id != null">
			<tr>
				<td colspan="2">
					<s:submit value="%{getText('action.update')}" action="PositionGeographique!update" theme="simple" cssStyle="float:right;"/>
					<s:submit value="%{getText('action.cancel')}" action="PositionGeographique!cancel" theme="simple" cssStyle="float:right;"/>
				</td>
			</tr>			
		</s:if>
		<s:else>
			<tr>
				<td colspan="2">	
  					<s:submit value="%{getText('action.createAndEdit')}" action="PositionGeographique!createAndEdit" theme="simple" cssStyle="float: right;"/>						
					<s:submit value="%{getText('action.create')}" action="PositionGeographique!update" theme="simple" cssStyle="float:right;"/>
					<s:submit value="%{getText('action.cancel')}" action="PositionGeographique!cancel" theme="simple" cssStyle="float:right;"/>
				</td>
			</tr>		
		</s:else>
	  	<%-- Ajout des balises tr et td pour le faire apparaitre dans le tableau --%>
	  	<tr><td colspan="2"><s:include value="/jsp/commun/asterisque.jsp" /></td></tr>
	</s:form>
</div>
<s:if test="positionGeographique.id != null">
	<s:if test="typePositionGeographique == 'arretPhysique'">
		<%-- Itinéraires liés à l'arrêt physique --%>
		<div class="panelDataSection">
			Itinéraires liés à l'arrêt physique
		</div>
		<div class="panel">
			<s:div label="Iti" id="displaytag"> 
				<display:table name="iti" uid="iti" sort="list" pagesize="10" requestURI="crud_PositionGeographique!edit.action" export="false">
				  	<display:column title="Nom Itineraire" sortable="true" headerClass="sortable">
						<s:url id="arretSurItineraire" action="ArretSurItineraire" includeParams="none">
							<s:param name="idItineraire" value="%{iti[${iti_rowNum} - 1].id}" />
						</s:url>	
						<s:a href="%{arretSurItineraire}"><s:property value="%{iti[${iti_rowNum} - 1].name}"/></s:a>
				  	</display:column>	
				  	<display:column title="Horaires" sortable="true" headerClass="sortable">
						<s:url id="horairesDePassage" action="liste_HorairesDePassage" includeParams="none">
							<s:param name="idItineraire" value="%{iti[${iti_rowNum} - 1].id}" />
							<s:param name="idLigne" value="%{iti[${iti_rowNum} - 1].idLigne}" />
						</s:url>				  	
				  		<s:a href="%{horairesDePassage}">Horaires</s:a>
				  	</display:column>
				  	<display:column title="Nom Ligne" sortable="true" headerClass="sortable">
						<s:url id="editLigne" action="crud_Ligne!edit" includeParams="none">
							<s:param name="idLigne" value="%{iti[${iti_rowNum} - 1].idLigne}" />
						</s:url>			  		
				  		<s:a href="%{editLigne}"><s:property value="%{getLigne(iti[${iti_rowNum} - 1].id).name}" /></s:a>
				  	</display:column>
				  	<display:column title="Nom Reseau" sortable="true" headerClass="sortable">
						<s:url id="editReseau" action="crud_Reseau!edit" includeParams="none">
							<s:param name="idReseau" value="getReseau(%{iti[${iti_rowNum} - 1].idLigne}).id" />
						</s:url>			  		
				  		<s:a href="%{editReseau}"><s:property value="%{getReseau(iti[${iti_rowNum} - 1].idLigne).name}" /></s:a>
				  	</display:column>
				</display:table>
			</s:div>
		</div>
	</s:if>	

	<s:if test="typePositionGeographique == 'zone'">
		<%-- Zones filles --%>
		<div class="panelDataSection">
			<s:text name="text.positionGeographique.childArea.title" />
		</div>
		<div class="panel">
			<s:div label="Children" id="displaytag">
				<display:table uid="children" name="children" excludedParams="" requestURI="crud_PositionGeographique!edit.action" sort="list" pagesize="10" export="false">
					<display:column title="action">			
						<s:url id="editUrl" action="crud_PositionGeographique!edit">
							<s:param name="idPositionGeographique" value="%{children[${children_rowNum} - 1].id}" />
						</s:url>
						<s:a href="%{editUrl}"><s:text name="action.edit" /></s:a>&nbsp;&nbsp;
						<s:url id="editUrl" action="crud_PositionGeographique!edit">
							<s:param name="idPositionGeographique" value="%{children[${children_rowNum} - 1].id}" />
						</s:url>
						<s:url id="removeUrl" action="PositionGeographique_removeChildFromParent">
							<s:param name="idPositionGeographique" value="%{positionGeographique.id}" />
							<s:param name="idChild" value="%{children[${children_rowNum} - 1].id}" />
						</s:url>
						<s:a href="%{removeUrl}"><s:text name="action.remove" /></s:a>&nbsp;&nbsp;
					</display:column>
					<display:column title="Nom">
					Zone	<s:property value="%{children[${children_rowNum} - 1].name}" />
					</display:column>
					<display:column title="Type">
						<s:text name="%{children[${children_rowNum} - 1].areaType}"/>
					</display:column>
				</display:table>
			</s:div> 
			<%-- Formulaire de recherche de zone fille --%>
			<s:form id="areaSearchForm" action="search_PositionGeographique">
				<s:hidden name="idPositionGeographique" value="%{positionGeographique.id}"/>
				<s:hidden name="actionSuivante" value="addChild"/>						
				<s:submit key="action.add"/>
			</s:form>
		</div>
	</s:if>	
	
	<%-- Zones parentes --%>
	<div class="panelDataSection">
		<s:text name="text.positionGeographique.fatherArea.title" />
	</div>
	<div class="panel">
		<s:div label="father" id="displaytag">
			<s:if test="father.id != null">
				<s:div title="Parent" label="Parent">
					<table BORDER="1">
						<thead>
							<tr>
								<td><s:text name="action.title" /></td>
								<td><s:text name="positionGeographique.name" /></td>
								<td><s:text name="positionGeographique.areaType" /></td>
							</tr>
						</thead>
						<tbody>
							<tr>
								<td>
									<s:url id="editUrl" action="crud_PositionGeographique!edit">
										<s:param name="idPositionGeographique" value="%{father.id}" />
									</s:url> 
									<s:a href="%{editUrl}"><s:text name="action.edit" /></s:a>&nbsp;&nbsp; 
									<s:url id="removeUrl" action="PositionGeographique_removeChildFromParent">
										<s:param name="idChild" value="%{positionGeographique.id}" />
										<s:param name="idPositionGeographique" value="%{positionGeographique.id}" />
										<s:param name="idItineraire" value="%{idItineraire}"/> 
										<s:param name="idLigne" value="%{idLigne}"/>							
										<s:param name="actionSuivante" value="%{actionSuivante}"/>
										<s:param name="typePositionGeographique" value="%{typePositionGeographique}"/>										
									</s:url> 
									<s:a href="%{removeUrl}"><s:text name="action.remove" /></s:a>&nbsp;&nbsp;
								</td>
								<td><s:property value="father.name" /></td>
								<td>
									<s:text name="${father.areaType}"/>
								</td>
							</tr>
						</tbody>
					</table>
				</s:div>
			</s:if>
		</s:div>	
		<%-- Formulaire de recherche de zone parente --%>
		<div ID="father">
			<s:form id="areaSearchForm" action="search_PositionGeographique">
				<s:hidden name="idPositionGeographique" value="%{positionGeographique.id}"/>
				<s:hidden name="actionSuivante" value="addFather"/>						
				<s:if test="father.id != null">
					<s:submit key="action.replace" />
				</s:if>
				<s:else>
					<s:submit key="action.add" />
				</s:else>
			</s:form>
		</div>
	</div>		
</s:if>
<script type="text/javascript"><!--
// <![CDATA[
	
	var positionGeographiques = <%=request.getAttribute("jsonPositionGeographiques")%>;
	
	function autocompletion() 
	{ 
		new Autocompleter.Local('positionGeographiques_father_auto_complete', 'positionGeographiques_auto_complete_list', Object.keys(positionGeographiques), {}); 
		new Autocompleter.Local('positionGeographiques_child_auto_complete', 'positionGeographiques_auto_complete_list2', Object.keys(positionGeographiques), {});
		$('positionGeographiques_auto_father_complete').focus(); 
	}
	
	Event.observe(window, 'load', autocompletion);
	
	var TridentAutoComplete = 
	{
		beforeSubmit: function() 
		{
			var value = positionGeographiques[$('positionGeographiques_father_auto_complete').value];
			if (value == null)
				$('positionGeographique_father').value="";
			else	
				$('positionGeographique_father').value = value;				
			return true;
		}
	};
	
	var TridentAutoComplete2 = 
	{
		beforeSubmitChild: function() 
		{		
			var valueEnd = positionGeographiques[$('positionGeographiques_child_auto_complete').value];
			if (valueEnd == null)
				$('positionGeographique_child').value="";
			else	
				$('positionGeographique_child').value = valueEnd;				
			return true;
		}
	};
	
	
// ]]>
--></script>


















