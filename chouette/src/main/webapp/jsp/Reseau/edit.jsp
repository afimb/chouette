<%@ taglib prefix="s" uri="/struts-tags"%>
<s:include value="/jsp/commun/jscalendar.jsp"></s:include>
<%-- Titre et barre de navigation --%>
<s:url id="urlReseauUpdate" action="crud_Reseau!edit">
	<s:param name="idReseau" value="%{reseau.id}"/>
</s:url>
<s:if test="reseau.id != null">
	<title><s:text name="text.reseau.update.title" /></title>
	<s:property value="filAriane.addElementFilAriane(getText('text.reseau.update.title'), '', #urlReseauUpdate)"/>	
</s:if> 
<s:else>
	<title><s:text name="text.reseau.create.title" /></title>
	<s:property value="filAriane.addElementFilAriane(getText('text.reseau.create.title'), '', #urlReseauUpdate)"/>		
</s:else>
<div class="panelData">
	<s:property value="filAriane.texteFilAriane" escape="false"/>
</div>	
<br>
<%-- Formulaire --%>	
<s:form>
	<s:if test="reseau.id != null">
		<s:hidden name="idReseau" value="%{reseau.id}" />
	</s:if>
	<s:textfield key="reseau.name" required="true" />
	<s:textfield key="reseau.registrationNumber" required="true" />
	<s:textfield key="reseau.comment" />
	<s:textfield key="reseau.versionDate" id="versionDate" required="true"/>
	<script type="text/javascript">
	<!--//
	Calendar.setup(
		{
       		singleClick : true,
			firstDay : 1,
			inputField : "versionDate",    	// ID of the input field
			ifFormat : "%d/%m/%Y"  			// the date format
		}
	);
	//-->
	</script>
	<s:textfield key="reseau.description" />
	<s:textfield key="reseau.sourceName" />
	<s:textfield key="reseau.sourceIdentifier" />
	
	<%-- Actions --%>
	<s:if test="reseau.id != null">	
  		<tr>
  			<td colspan="2">
  				<s:submit value="%{getText('action.update')}" action="crud_Reseau!update" theme="simple" cssStyle="float: right;"/>
  				<s:submit value="%{getText('action.cancel')}" action="crud_Reseau!cancel" theme="simple" cssStyle="float: right;"/>
  			</td>
  		</tr>
  	</s:if>
  	<s:else>
  		<tr>
  			<td colspan="2">
  				<%--<s:submit value="%{getText('action.createAndEdit')}" action="crud_Reseau!createAndEdit"  theme="simple" cssStyle="float: right;"/>  --%>			
  				<s:submit value="%{getText('action.create')}" action="crud_Reseau!update" theme="simple" cssStyle="float: right;"/>
  				<s:submit value="%{getText('action.cancel')}" action="crud_Reseau!cancel" theme="simple" cssStyle="float: right;"/>
  			</td>
  		</tr>
  	</s:else>
  	<%-- Ajout des balises tr et td pour le faire apparaitre dans le tableau --%>
  	<tr><td colspan="2"><s:include value="/jsp/commun/asterisque.jsp" /></td></tr>
</s:form>
