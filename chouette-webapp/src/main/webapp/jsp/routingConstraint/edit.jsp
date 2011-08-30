<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>

<%-- Titre et barre de navigation --%>
<s:url id="urlITLUpdate" action="edit" namespace="/routingConstraint">
  <s:param name="idITL" value="%{id}"/>
</s:url>
<s:if test="id != null">
  <title><s:text name="text.itl.update.title" /></title>
  <s:property value="filAriane.addElementFilAriane(getText('text.itl.update.title'), '', #urlITLUpdate)"/>
</s:if> 
<s:else>
  <title><s:text name="text.itl.create.title" /></title>
  <s:property value="filAriane.addElementFilAriane(getText('text.itl.create.title'), '', #urlITLUpdate)"/>
</s:else>
<div class="panelData">
  <s:property value="filAriane.texteFilAriane" escape="false"/>
</div>

<s:include value="/jsp/commun/messages.jsp" />

<div class="panelDataSection"><s:text name="itl"/></div>
<div class="panel">
  <s:form cssClass="panelDataInnerForm" namespace="/routingConstraint">
    <s:hidden name="idItl" value="%{id}"/>
    <s:hidden name="operationMode" value="STORE" />
    <s:hidden key="actionMethod" value="%{actionMethod}"/>
    <s:textfield size="50" key="nom" required="true"/>
    <s:if test="id != null">
      <tr>
        <td><s:text name="text.itl.line"/></td>
        <td>${ligneName}</td>
      </tr>
    </s:if>
    <s:else>
      <s:select key="idLigne"  list="lignes" listKey="id" listValue="name" required="true"/>
    </s:else>

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
</div>

<s:if test="id != null && idLigne != null">
  <div class="panelDataSection"><s:text name="itl.arretPhysiqueIds"/></div>
  <div class="panel">
    <div id="displaytag">
        <display:table uid="arretsDansITLList" name="arretsDansITLList" sort="list" pagesize="10" export="false" requestURI="">
        <display:column titleKey="table.title.action">
          <s:url id="removeUrl" action="removeStop" namespace="/routingConstraint">
            <s:param name="idItl">${id}</s:param>
            <s:param name="idAreaStop">%{arretsDansITLList[${arretsDansITLList_rowNum} - 1].id}</s:param>
          </s:url>
          <s:a href="%{removeUrl}" onclick="return confirm('%{getText('popup.confirmer')}')">
            <img border="0" alt="Delete" src="<s:url value='/images/supprimer.png'/>" title="<s:text name="tooltip.delete"/>">
          </s:a>
        </display:column>

        <display:column titleKey="table.title.name" property="name"/>
        <display:column titleKey="table.title.inseeCode" property="countryCode"/>
        <display:column titleKey="table.title.address" property="streetName"/>
      </display:table>
    </div>

    <%-- Bloc permettant la recherche d'un arret --%>
    <div ID="divSearchArret" STYLE="border: 1px; border-color: black;">
      <s:form cssClass="panelDataInnerForm" action="addStop" namespace="/routingConstraint" id="creerArretForm" theme="simple">
        <table><tr>
            <td>
              <s:hidden name="idItl" value="%{id}" id="idItl"/>
              <s:select name="saisieNomArretExistantKey" value="%{saisieNomArretExistant}" key="saisieNomArretExistantKey"  list="arrets" listKey="id" listValue="fullName"/>
            </td>
            <td>
              <s:submit name="action" key="action.add" formId="creerArretForm"  />
            </td>
          </tr></table>
        </s:form>
    </div>
  </div>
</s:if>