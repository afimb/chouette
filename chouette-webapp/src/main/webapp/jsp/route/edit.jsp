<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%-- Titre et barre de navigation --%>	
<s:url id="urlItineraireUpdate" action="edit" namespace="/route">
  <s:param name="idItineraire" value="%{id}"/>
  <s:param name="idLigne" value="%{idLigne}"/>
</s:url>
<s:if test="id != null">
  <title><s:text name="text.itineraire.update.title" /></title>
  <s:property value="filAriane.addElementFilAriane(getText('text.itineraire.update.title'), '', #urlItineraireUpdate)"/>
</s:if> 
<s:else>
  <title><s:text name="text.transporteur.create.title" /></title>
  <s:property value="filAriane.addElementFilAriane(getText('text.itineraire.create.title'), '', #urlItineraireUpdate)"/>
</s:else>
<div class="panelData">
  <s:property value="filAriane.texteFilAriane" escape="false"/>
</div>

<s:include value="/jsp/commun/messages.jsp" />

<s:form > 
  <s:hidden name="idItineraire" value="%{id}"/>
  <s:hidden name="idLigne" value="%{idLigne}"/>
  <s:hidden name="operationMode" value="STORE" />
  <s:hidden key="actionMethod" value="%{actionMethod}"/>
  <s:select name="idRetour" label="%{getText('idRetour')}" list="itinerairesSansItineraireEdite" listKey="id" listValue="publishedName" headerValue="%{getText('aucunRetour')}" />
  <s:textfield key="name" required="true"/>
  <s:textfield key="publishedName" />
  <s:select key="direction" list="directionsEnum" listKey="enumeratedTypeAccess" listValue="textePropriete"/>
  <s:textfield key="number" />
  <s:radio key="wayBack" value="%{sensItineraire}" list="#{getText('text.route.outward.journey'):getText('text.route.outward.journey'), getText('text.route.return.journey'):getText('text.route.return.journey')}" />
  <s:textfield key="comment"/>
  <br>

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