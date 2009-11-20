<%@ taglib prefix="s" uri="/struts-tags" %>

<%-- Titre et barre de navigation --%>
<s:url id="urlTransporteurUpdate" value="/company/edit">
  <s:param name="idTransporteur" value="%{id}"/>
</s:url>
<s:if test="id != null">
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
  <s:hidden name="idTransporteur" value="%{id}"/>
  <s:hidden name="operationMode" value="STORE" />
  <s:hidden key="actionMethod" value="%{actionMethod}"/>
  <s:textfield key="transporteur.company.name" name="name" required="true"/>
  <s:textfield key="transporteur.company.shortName" name="shortName"/>
  <s:textfield key="transporteur.company.organisationalUnit" name="organisationalUnit"/>
  <s:textfield key="transporteur.company.operatingDepartmentName" name="operatingDepartmentName"/>
  <s:textfield key="transporteur.company.code" name="code"/>
  <s:textfield key="transporteur.company.phone" name="phone"/>
  <s:textfield key="transporteur.company.fax" name="fax"/>
  <s:textfield key="transporteur.company.email" name="email"/>
  <s:textfield key="transporteur.company.registration.registrationNumber" name="registrationNumber" required="true"/>
	
	<%-- Actions --%>
  		<tr>
  			<td colspan="2">
      <s:if test="id != null">
        <s:submit key="action.update" action="%{actionMethod}"  theme="simple" cssStyle="float: right;"/>
  	</s:if>
  	<s:else>
        <s:submit key="action.create" action="%{actionMethod}" theme="simple" cssStyle="float: right;"/>
      </s:else>
      <s:submit key="action.cancel" action="cancel" theme="simple" cssStyle="float: right;"/>
  			</td>
  		</tr>

  	<%-- Ajout des balises tr et td pour le faire apparaitre dans le tableau --%>
  	<tr><td colspan="2"><s:include value="/jsp/commun/asterisque.jsp" /></td></tr>	
</s:form>