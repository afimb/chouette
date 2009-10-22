<%@ taglib prefix="s" uri="/struts-tags" %>
<%-- Titre et barre de navigation --%>
<s:url id="urlTransporteurUpdate" action="crud_Transporteur!edit">
	<s:param name="idTransporteur" value="%{transporteur.id}"/>
</s:url>
<s:if test="transporteur.id != null">
	<title><s:text name="text.transporteur.update.title" /></title>
	<s:property value="filAriane.addElementFilAriane(getText('text.transporteur.update.title'), '', #urlTransporteurUpdate)"/>		
</s:if> 
<s:else>
	<title><s:text name="text.transporteur.create.title" /></title>	
	<s:property value="filAriane.addElementFilAriane(getText('text.transporteur.create.title'), '', #urlTransporteurUpdate)"/>	
</s:else>
<div class="panelData">
	<s:property value="filAriane.texteFilAriane" escape="false"/>
</div>
<br>
<%-- Formulaire --%>
<s:form> 
	<s:hidden name="idTransporteur" value="%{transporteur.id}"/>
	<s:textfield key="transporteur.company.name" required="true"/>
	<s:textfield key="transporteur.company.shortName"/>
	<s:textfield key="transporteur.company.organisationalUnit"/>	
	<s:textfield key="transporteur.company.operatingDepartmentName"/>
	<s:textfield key="transporteur.company.code"/>
	<s:textfield key="transporteur.company.phone"/>
	<s:textfield key="transporteur.company.fax"/>
	<s:textfield key="transporteur.company.email"/>
	<s:textfield key="transporteur.company.registration.registrationNumber" required="true"/>			
	
	<%-- Actions --%>
	<s:if test="transporteur.id != null">	
  		<tr>
  			<td colspan="2">
  				<s:submit value="%{getText('action.update')}" action="crud_Transporteur!update" theme="simple" cssStyle="float: right;"/>
  				<s:submit value="%{getText('action.cancel')}" action="crud_Transporteur!cancel" theme="simple" cssStyle="float: right;"/>
  			</td>
  		</tr>
  	</s:if>
  	<s:else>
  		<tr>
  			<td colspan="2">
  				<%--<s:submit value="%{getText('action.createAndEdit')}" action="crud_Transporteur!createAndEdit"  theme="simple" cssStyle="float: right;"/> --%> 			
  				<s:submit value="%{getText('action.create')}" action="crud_Transporteur!update" theme="simple" cssStyle="float: right;"/>
  				<s:submit value="%{getText('action.cancel')}" action="crud_Transporteur!cancel" theme="simple" cssStyle="float: right;"/>
  			</td>
  		</tr>
  	</s:else>
  	<%-- Ajout des balises tr et td pour le faire apparaitre dans le tableau --%>
  	<tr><td colspan="2"><s:include value="/jsp/commun/asterisque.jsp" /></td></tr>	
</s:form>