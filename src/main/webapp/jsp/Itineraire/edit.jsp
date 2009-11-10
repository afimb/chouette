<%@ taglib prefix="s" uri="/struts-tags" %>
<%-- Titre et barre de navigation --%>	
<s:url id="urlItineraireUpdate" action="crud_Itineraire!edit">
	<s:param name="idItineraire" value="%{itineraire.id}"/>
</s:url>
<s:if test="itineraire.id != null">
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
	<s:if test="itineraire.id != null">
		<s:hidden name="idItineraire" value="%{itineraire.id}"/>	
	</s:if>
	<s:hidden name="idLigne" value="%{idLigne}"/>
  <s:select name="idRetour" value="%{idRetour}" label="%{getText('itineraire.idRetour')}" list="itineraire.itinerairesSansItineraireEdite" listKey="id" listValue="publishedName" headerKey="-1" headerValue="%{getText('itineraire.aucunRetour')}" />
	<s:textfield key="itineraire.name" required="true"/>
	<s:textfield key="itineraire.publishedName" />
	<s:select key="itineraire.direction" list="@fr.certu.chouette.ihm.enumeration.EnumerationApplication@getDirectionEnum()" listKey="enumeratedTypeAccess" listValue="textePropriete"/>
	<s:textfield key="itineraire.number" />
	<s:radio name="itineraire.wayBack" value="%{sensItineraire}" list="sensItineraires" label="%{getText('itineraire.wayBack')}" />
	<s:textfield key="itineraire.comment"/>
	<br>
	
	<%-- Actions --%>
	<s:if test="ligne.id != null">	
  		<tr>
  			<td colspan="2">
  				<s:submit action="creer_Itineraire_sur_la_ligne" key="action.update" theme="simple" cssStyle="float: right;"/>
  				<s:submit action="annuler_Itineraire_sur_la_ligne" value="%{getText('action.cancel')}" theme="simple" cssStyle="float: right;" />
  			</td>
  		</tr>
  	</s:if>
  	<s:else>
  		<tr>
  			<td colspan="2">
  				<s:submit key="action.create" theme="simple" cssStyle="float: right;"/>
  				<s:submit href="annuler_Itineraire_sur_la_ligne" value="%{getText('action.cancel')}" theme="simple" cssStyle="float: right;"/> 
  			</td>
  		</tr>
  	</s:else>
  	<%-- Ajout des balises tr et td pour le faire apparaitre dans le tableau --%>
  	<tr><td colspan="2"><s:include value="/jsp/commun/asterisque.jsp" /></td></tr>	
</s:form>