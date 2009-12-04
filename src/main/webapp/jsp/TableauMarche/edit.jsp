<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>
<head> 
	<s:include value="/jsp/commun/jscalendar.jsp"></s:include>
</head>
<%-- Titre et barre de navigation --%>
<s:url id="urlTableauMarcheUpdate" action="crud_TableauMarche!edit">
	<s:param name="idTableauMarche" value="%{tableauMarche.id}"/>
</s:url>
<s:if test="tableauMarche.id != null">
	<title><s:text name="text.tableauMarche.update.title"/></title>
	<s:property value="filAriane.addElementFilAriane(getText('text.tableauMarche.update.title'), '', #urlTableauMarcheUpdate)"/>			
</s:if> 
<s:else>
	<title><s:text name="text.tableauMarche.create.title"/></title>
	<s:property value="filAriane.addElementFilAriane(getText('text.tableauMarche.create.title'), '', #urlTableauMarcheUpdate)"/>		
</s:else>
<div class="panelData">
	<s:property value="filAriane.texteFilAriane" escape="false"/>
</div>
<br>
<%-- Formulaire --%>
<div class="panelDataSection"><s:text name="tableauMarche"/></div>
<div class="panel"> 
	<s:form cssClass="panelDataInnerForm" method="POST">
		<s:if test="tableauMarche.id != null">
			<s:hidden name="idTableauMarche" value="%{tableauMarche.id}"/>	
		</s:if>
		<s:textfield key="tableauMarche.objectId" readonly="true" cssClass="texteNonEditable" cssStyle="width: 250px;"/>
		<s:textfield key="tableauMarche.comment" required="true" cssStyle="width: 250px;"/>
		
		<s:if test="tableauMarche.id != null">	
			<div class="editTableauMarche" >
				<s:checkboxlist name="joursTypes" list="@fr.certu.chouette.ihm.enumeration.EnumerationApplication@getJoursTypesEnum()" listKey="enumeratedTypeAccess" listValue="textePropriete" template="checkboxlist.ftl"></s:checkboxlist>
			</div>	
		</s:if>			
		
		<%-- Actions --%>
		<s:if test="tableauMarche.id != null">	
	  		<tr>
	  			<td colspan="2">
	  				<s:submit value="%{getText('action.update')}" action="crud_TableauMarche!update" theme="simple" cssStyle="float: right;"/>
	  				<s:submit value="%{getText('action.cancel')}" action="crud_TableauMarche!cancel" theme="simple" cssStyle="float: right;"/>
	  			</td>
	  		</tr>
	  	</s:if>
	  	<s:else>
	  		<tr>
	  			<td colspan="2">
  					<%--<s:submit value="%{getText('action.createAndEdit')}" action="crud_TableauMarche!createAndEdit"  theme="simple" cssStyle="float: right;"/>--%>	  			
	  				<s:submit value="%{getText('action.create')}" action="crud_TableauMarche!update" theme="simple" cssStyle="float: right;"/>
	  				<s:submit value="%{getText('action.cancel')}" action="crud_TableauMarche!cancel" theme="simple" cssStyle="float: right;"/>
	  			</td>
	  		</tr>
	  	</s:else>
	  	<%-- Ajout des balises tr et td pour le faire apparaitre dans le tableau --%>
	  	<tr><td colspan="2"><s:include value="/jsp/commun/asterisque.jsp" /></td></tr>			
	</s:form> 
</div>
	
<s:if test="tableauMarche.id != null">	
	<div class="panelDataSection"><s:text name="tableauMarche.dates"/></div>
	<div class="panel">
		<s:div label="Dates" id="displaytag">
			<display:table uid="datesTable" name="tableauMarche.dates" excludedParams="debut fin date tableauMarche.comment jour" requestURI="crud_TableauMarche!edit.action" sort="list" pagesize="10" defaultorder="ascending" defaultsort="1"  export="false">			  		  	 				  			  	
			  	<display:column title="Date" sortable="true" headerClass="sortable" comparator="fr.certu.chouette.ihm.util.DateComparator">
			  		<s:property value="%{tableauMarche.dates[${datesTable_rowNum} - 1]}" />
			  	</display:column>		  			  	  
			  	<display:column title="Action">
				  	<s:url id="deleteUrl" action="crud_TableauMarche!deleteDate">			  		
						<s:param name="idTableauMarche" value="%{tableauMarche.id}"/>
						<s:param name="idxDate" value="${datesTable_rowNum}"/>												
					</s:url>			  			  
			  		<s:a href="%{deleteUrl}" onclick="return confirm('%{getText('popup.confirmer')}'">
              <img border="0" src="<s:url value='images/supprimer.png'/>" title="<s:text name="tooltip.delete"/>"></s:a>
			  	</display:column>
			</display:table>
		</s:div>
		<s:form cssClass="panelDataInnerForm" action="crud_TableauMarche!addDate">
			<s:if test="tableauMarche.id != null">
				<s:hidden name="idTableauMarche" value="%{tableauMarche.id}"/>	
			</s:if>
			<s:textfield label="Date" name="jour" id="jour" />					
			<s:submit key="text.tableauMarche.addDate.button"/>	
			<br>
							
			<script type="text/javascript"> 
			<!--//
			Calendar.setup(
				{
		       		singleClick : true,
					firstDay : 1,
					inputField : "jour",    	// ID of the input field
					ifFormat : "%d/%m/%Y"  			// the date format
				}
			);
			//-->
			</script>			
		</s:form> 	
	</div>					
</s:if>

		
<s:if test="tableauMarche.id != null">
	<div class="panelDataSection"><s:text name="tableauMarche.periods"/></div>
	<div class="panel">
		<!-- Affichage liste des périodes -->
		<div id="displaytag">		
			<display:table uid="periodsTable" name="tableauMarche.periodes" excludedParams="debut fin date tableauMarche.comment jour" requestURI="crud_TableauMarche!edit.action" sort="list" pagesize="10" defaultorder="ascending" defaultsort="2" export="false">						
				<display:column title="Debut" sortable="true" headerClass="sortable">
			  		<s:property value="%{tableauMarche.periodes[${periodsTable_rowNum} - 1].debut}" />
			  	</display:column>	  	
				<display:column title="Fin" sortable="true" headerClass="sortable">
			  		<s:property value="%{tableauMarche.periodes[${periodsTable_rowNum} - 1].fin}" />
			  	</display:column>			  	 				  					  	  
			  	<display:column title="Action">
				  	<s:url id="deleteUrl" action="crud_TableauMarche!deletePeriod">			  		
						<s:param name="idTableauMarche" value="%{tableauMarche.id}"/>
						<s:param name="idxPeriod" value="${periodsTable_rowNum}"/>											
					</s:url>
			  			  
			  		<s:a href="%{deleteUrl}" onclick="return confirm('%{getText('popup.confirmer')}'">
              <img border="0" src="<s:url value='/images/supprimer.png'/>" title="<s:text name="tooltip.delete"/>"></s:a>
			  	</display:column>				  		  	  
			</display:table>
		</div>
		<!-- Affichage formulaire création nouvelle période -->
		<s:form cssClass="panelDataInnerForm" action="crud_TableauMarche!addPeriode">
			<s:if test="tableauMarche.id != null">
				<s:hidden name="idTableauMarche" value="%{tableauMarche.id}"/>	
			</s:if>
			<s:textfield key="tableauMarche.period.startDate" name="debut" id="debut"/>
			<s:textfield key="tableauMarche.period.endDate" name="fin" id="fin"/>			
			<s:submit key="text.tableauMarche.addPeriod.button"/>	
			<br>								
			<script type="text/javascript"> 
			<!--//
			Calendar.setup(
				{
		       		singleClick : true,
					firstDay : 1,
					inputField : "debut",    	// ID of the input field
					ifFormat : "%d/%m/%Y"  			// the date format
				}
			);
			
			Calendar.setup(
				{
		       		singleClick : true,
					firstDay : 1,
					inputField : "fin",    	// ID of the input field
					ifFormat : "%d/%m/%Y"  			// the date format
				}
			);
			//-->
			</script>				
		</s:form> 
	</div>	
</s:if>




	
	
	



