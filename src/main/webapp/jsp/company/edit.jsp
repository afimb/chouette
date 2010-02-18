<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags" %>

<%-- Titre et barre de navigation --%>
<s:url id="urlTransporteurUpdate" action="edit" namespace="/company">
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
  <s:textfield key="name" required="true"/>
  <s:textfield key="shortName"/>
  <s:textfield key="organisationalUnit"/>
  <s:textfield key="operatingDepartmentName"/>
  <s:textfield key="code"/>
  <s:textfield key="phone"/>
  <s:textfield key="fax"/>
  <s:textfield key="email"/>
  <s:textfield key="registrationNumber" required="true"/>
	
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