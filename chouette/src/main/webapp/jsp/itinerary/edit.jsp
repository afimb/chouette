<%@ taglib prefix="s" uri="/struts-tags" %>
<%-- Titre et barre de navigation --%>	
<s:url id="urlItineraireUpdate" action="edit" namespace="/itinerary">
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
<br>
<s:form > 
  <s:hidden name="idItineraire" value="%{id}"/>
  <s:hidden name="idLigne" value="%{idLigne}"/>
  <s:hidden name="operationMode" value="STORE" />
  <s:hidden key="actionMethod" value="%{actionMethod}"/>
  <s:select name="idRetour" label="%{getText('itineraire.idRetour')}" list="#request.itinerairesSansItineraireEdite" listKey="id" listValue="publishedName" headerKey="-1" headerValue="%{getText('itineraire.aucunRetour')}" />
  <s:textfield key="itineraire.name" name="name" required="true"/>
  <s:textfield key="itineraire.publishedName" name="publishedName" />
  <s:select key="itineraire.direction" name="direction" list="@fr.certu.chouette.struts.enumeration.EnumerationApplication@getDirectionEnum()" listKey="enumeratedTypeAccess" listValue="textePropriete"/>
  <s:textfield key="itineraire.number" name="number" />
  <s:radio name="wayBack" value="%{sensItineraire}" list="sensItineraires" label="%{getText('itineraire.wayBack')}" />
  <s:textfield key="itineraire.comment" name="comment"/>
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
      <s:submit key="action.cancel" action="%{actionMethod}" theme="simple" cssStyle="float: right;"/>
    </td>
  </tr>
  
  <%-- Ajout des balises tr et td pour le faire apparaitre dans le tableau --%>
  <tr><td colspan="2"><s:include value="/jsp/commun/asterisque.jsp" /></td></tr>
</s:form>