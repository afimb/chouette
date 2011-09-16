<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%-- Titre et barre de navigation --%>
<s:url id="urlMissionUpdate" action="edit" namespace="/journeyPattern">
  <s:param name="idMission" value="%{id}"/>
</s:url>
<s:if test="id != null">
  <title><s:text name="text.mission.update.title" /></title>
  <s:property value="filAriane.addElementFilAriane(getText('text.mission.update.title'), '', #urlMissionUpdate)"/>
</s:if> 
<s:else>
  <title><s:text name="text.mission.create.title" /></title>
  <s:property value="filAriane.addElementFilAriane(getText('text.mission.create.title'), '', #urlMissionUpdate)"/>
</s:else>
<div class="panelData">
  <s:property value="filAriane.texteFilAriane" escape="false"/>
</div>

<s:include value="/jsp/commun/messages.jsp" />

<%-- Formulaire --%>
<s:form id="" method="POST" namespace="/journeyPattern">
  <s:if test="id != null">
    <s:hidden name="idMission" value="%{id}" />
  </s:if>

  <%-- Remplissage des paramètres cachés utiles pour l'action--%>
  <s:hidden name="idItineraire" value="%{idItineraire}"/>
  <s:hidden name="idLigne" value="%{idLigne}" />
  <s:hidden name="idTableauMarche" value="%{idTableauMarche}" />
  <s:hidden name="seuilHeureDepartCourse" value="%{seuilHeureDepartCourse}" />
  <s:hidden name="page" value="%{page}" />
  <s:hidden key="actionMethod" value="%{actionMethod}"/>

  <s:textfield key="name" />
  <s:textfield key="registrationNumber" />
  <s:textfield key="publishedName" />
  <s:textfield key="comment"/>

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
  <tr><td style="text-align:center;"><s:include value="/jsp/commun/asterisque.jsp" /></td></tr>
</s:form>
