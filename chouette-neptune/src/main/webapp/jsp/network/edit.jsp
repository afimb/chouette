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

<s:include value="/jsp/commun/messages.jsp" />

<div class="edit">
  <%-- Formulaire --%>
  <s:form theme="css_xhtml" id="network">
    <s:hidden name="idReseau" value="%{id}" />
    <s:hidden name="operationMode" value="%{'STORE'}" />
    <s:hidden key="actionMethod" value="%{actionMethod}"/>
    <s:textfield key="objectId" readonly="true" cssClass="texteNonEditable"/>
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

    <s:include value="/jsp/commun/asterisque.jsp" />
    <%-- Actions --%>
    <div class="submit">
      <s:if test="id != null">
        <s:submit key="action.update" action="%{actionMethod}"  theme="simple" cssClass="right"/>
      </s:if>
      <s:else>
        <s:submit key="action.create" action="%{actionMethod}" theme="simple" cssClass="right"/>
      </s:else>
      <s:submit key="action.cancel" action="cancel" theme="simple" cssClass="right"/>
    </div>
  </s:form>
</div>

