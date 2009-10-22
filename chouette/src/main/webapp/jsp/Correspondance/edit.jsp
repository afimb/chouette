<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>
<head> 
	<s:include value="/jsp/commun/scriptaculous.jsp" />
</head>
<%-- Titre et barre de navigation --%>
<s:url id="urlCorrespondanceUpdate" action="crud_Correspondance!edit">
	<s:param name="idCorrespondance" value="%{connectionlink.id}"/>
</s:url>
<s:if test="connectionlink.id != null">
	<title><s:text name="text.connectionlink.update.title" /></title>
	<s:property value="filAriane.addElementFilAriane(getText('text.connectionlink.update.title'), '', #urlCorrespondanceUpdate)"/>			
</s:if> 
<s:else>
	<title><s:text name="text.connectionlink.create.title" /></title>
	<s:property value="filAriane.addElementFilAriane(getText('text.connectionlink.create.title'), '', #urlCorrespondanceUpdate)"/>
</s:else>
<div class="panelData">
	<s:property value="filAriane.texteFilAriane" escape="false"/>
</div>
<br>	
	<div class="panelDataSection"><s:text name="connectionlink"/></div>
	<div class="panel">
		<s:form validate="true" id="connectionLinkForm"  action="crud_Correspondance!update">
			<s:if test="connectionlink.id != null">
				<s:hidden name="idCorrespondance" value="%{connectionlink.id}"/>
			</s:if>		
			
			<s:textfield key="connectionlink.name" required="true"/> 			
			<s:textfield key="connectionlink.comment" required="false"/>
			<s:textfield maxlength="5" id="defaultDuration" name="defaultDuration" key="defaultDuration" required="false"/>				
			<s:textfield maxlength="5" id="mobilityRestrictedTravellerDuration" key="mobilityRestrictedTravellerDuration" required="false"/>
			<s:textfield maxlength="5" id="occasionalTravellerDuration" key="occasionalTravellerDuration" required="false"/>
			<s:textfield maxlength="5" id="frequentTravellerDuration" key="frequentTravellerDuration" required="false"/>			 	
			<s:select emptyOption="false" key="connectionlink.linkType" list="@fr.certu.chouette.ihm.enumeration.EnumerationApplication@getConnectionLinkTypeEnum()" listKey="enumeratedTypeAccess" listValue="textePropriete"/>					
			<s:select emptyOption="false" key="connectionlink.liftAvailability" list="#@java.util.HashMap@{'true':'Oui', 'false':'Non'}" value="connectionlink.liftAvailability"/>
			<s:select emptyOption="false" key="connectionlink.mobilityRestrictedSuitability" list="#@java.util.HashMap@{'true':'Oui', 'false':'Non'}" value="connectionlink.mobilityRestrictedSuitability"/>
			<s:select emptyOption="false" key="connectionlink.stairsAvailability" list="#@java.util.HashMap@{'true':'Oui', 'false':'Non'}" value="connectionlink.stairsAvailability"/>
			<s:textfield key="connectionlink.linkDistance" />

 		<!-- 
			<div ID="start" STYLE="display: block; align: right;">
				<s:textfield key="connectionlink.idDepart" name="saisieZoneExistante" id="stop_areas_start_auto_complete" size="40" value="%{zoneDepartText}" /> 
				
				<div id="stop_areas_auto_complete_list" class="stop_areas_auto_complete_list" ></div>							
				<s:if test="connectionlink.idDepart != null">											
					<s:url id="editUrl" action="crud_PositionGeographique!edit">
						<s:param name="idPositionGeographique" value="%{connectionlink.idDepart}" />
					</s:url>
					<s:a href="%{editUrl}"><img border="0" src="images/editer.png" title="<s:text name="tooltip.edit"/>">&nbsp;<s:text name="connectionlink.idDepart"/></s:a>&nbsp;&nbsp;
				
				</s:if>
			</div>
			-->	
			<!-- 
			<div ID="end" STYLE="display: block; align: right;">
				<s:textfield key="connectionlink.idArrivee" name="saisieZoneExistante" id="stop_areas_end_auto_complete" size="40" value="%{zoneArriveeText}" />
				<s:hidden name="connectionlink.idArrivee" id="stop_area_end" value=""/>
				<div id="stop_areas_auto_complete_list2" class="stop_areas_auto_complete_list" ></div>
				<s:if test="connectionlink.idDepart != null">					
					<s:url id="editUrlArrivee" action="crud_PositionGeographique!edit">
						<s:param name="idPositionGeographique" value="%{connectionlink.idArrivee}" />
					</s:url>
					<s:a href="%{editUrlArrivee}"><img border="0" src="images/editer.png" title="<s:text name="tooltip.edit"/>">&nbsp;<s:text name="connectionlink.idArrivee"/></s:a>&nbsp;&nbsp;					 					
				</s:if>
			</div>
			-->
			
			<%-- Actions --%>
			<s:url action="crud_Correspondance!cancel" id="cancelCorrespondance"/>
			<tr>
		  		<td colspan="2">
				<s:if test="connectionlink.id != null">			  		
	  				<s:submit value="%{getText('action.update')}" theme="simple" cssStyle="float: right;"/>
	  				<input type="button" onclick="location.href='<s:property value="cancelCorrespondance"/>';return false;"  style="float: right;" value='<s:text name="action.cancel"/>'>		  			
			  	</s:if>
		  		<s:else>		  			
		  			<s:submit value="%{getText('action.create')}" theme="simple" cssStyle="float: right;"/>
					<input type="button" onclick="location.href='<s:property value="cancelCorrespondance"/>';return false;"  style="float: right;" value='<s:text name="action.cancel"/>'>	  				
		  		</s:else>
				</td>
			</tr>
			
			<%-- Ajout des balises tr et td pour le faire apparaitre dans le tableau --%>
  			<tr><td colspan="2"><s:include value="/jsp/commun/asterisque.jsp" /></td></tr>
		</s:form>
		
	
	</div>

	
		
	<%-- Zones depart --%>
	<div class="panelDataSection"><s:text name="Départ"/></div>
	<div class="panel" id="displaytag"> 
			<s:if test="connectionlink.id != null">						
				<table>
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
								<s:if test="connectionlink.idDepart != null">
									<s:url id="editUrl" action="crud_PositionGeographique!edit">
										<s:param name="idPositionGeographique" value="%{connectionlink.idDepart}" />
									</s:url>
									<s:a href="%{editUrl}">
										<img border="0" src="images/editer.png" title="<s:text name="tooltip.edit"/>">&nbsp;
									</s:a>
								</s:if>
								
								<s:url id="createReplaceUrl" action="search_Correspondance">
									<s:param name="idCorrespondance" value="%{connectionlink.id}" />
									<s:param name="actionSuivante" value="%{'addStart'}" />																											
								</s:url> 
								<s:a href="%{createReplaceUrl}">
									<s:if test="connectionlink.idDepart != null">	
										<s:text name="action.replace" />
									</s:if>
									<s:else>
										<s:text name="action.add" />
									</s:else>
								</s:a>
							</td>
							<td><s:property value="start.name" /></td>
							<td>
								<s:text name="${start.areaType}"/>
							</td>
						</tr>
					</tbody>
				</table>
			</s:if>
		</div>	
		
		<%-- Zones arrivée --%>
		<div class="panelDataSection"><s:text name="Arrivée"/></div>	
		<div class="panel" id="displaytag">
				<s:if test="connectionlink.id != null">
				<table>
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
								<s:if test="connectionlink.idArrivee != null">
									<s:url id="editUrl" action="crud_PositionGeographique!edit">
										<s:param name="idPositionGeographique" value="%{connectionlink.idArrivee}" />
									</s:url>
									<s:a href="%{editUrl}">
										<img border="0" src="images/editer.png" title="<s:text name="tooltip.edit"/>">&nbsp;
									</s:a>
								</s:if>
								
								<s:url id="createReplaceUrl" action="search_Correspondance">
									<s:param name="idCorrespondance" value="%{connectionlink.id}" />
									<s:param name="actionSuivante" value="%{'addEnd'}" />																											
								</s:url> 
								<s:a href="%{createReplaceUrl}">
									<s:if test="connectionlink.idArrivee != null">	
										<s:text name="action.replace" />
									</s:if>
									<s:else>
										<s:text name="action.add" />
									</s:else>
								</s:a>
							</td>
							<td><s:property value="end.name" /></td>
							<td>
								<s:text name="${end.areaType}"/>
							</td>
						</tr>
					</tbody>
				</table>
				</s:if>
				
			</div>		
		
			

		



 






	
	
	



