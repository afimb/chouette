<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<s:include value="/jsp/commun/jscalendar.jsp"></s:include>

<%-- Titre et barre de navigation --%>
<s:url id="networkEdit" action="edit" namespace="/network">
  <s:param name="idReseau" value="%{id}"/>
</s:url>

<s:if test="id != null">
  <title><s:text name="text.reseau.update.title" /></title>
  <s:property value="filAriane.addElementFilAriane(getText('text.reseau.update.title'), '', #networkEdit)"/>
</s:if> 
<s:else>
  <title><s:text name="text.reseau.create.title" /></title>
  <s:property value="filAriane.addElementFilAriane(getText('text.reseau.create.title'), '', #networkEdit)"/>
</s:else>
<div class="panelData">
  <s:property value="filAriane.texteFilAriane" escape="false"/>
</div>	
<br>

<%-- Formulaire --%>	
<s:form id="network_form">
  <s:hidden name="idReseau" value="%{id}" />
  <s:hidden name="operationMode" value="STORE" />
  <s:hidden key="actionMethod" value="%{actionMethod}"/>
  <s:textfield key="name"  required="true" />
  <s:textfield key="registrationNumber" required="true" />
  <s:textfield key="comment" />
  <s:textfield key="versionDate" id="versionDate" required="true"/>
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
  <s:textfield key="description" />
  <s:textfield key="sourceName" />
  <s:textfield key="sourceIdentifier"/>

  <%-- Actions --%>
  <tr>
    <td colspan="2">
      <s:if test="id != null">
        <s:submit key="action.update" action="%{actionMethod}" theme="simple" cssStyle="float: right;"/>
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
