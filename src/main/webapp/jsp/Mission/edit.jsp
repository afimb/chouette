<%@ taglib prefix="s" uri="/struts-tags"%>
<%-- Titre et barre de navigation --%>
<s:url id="urlMissionUpdate" action="crud_Mission!edit">
	<s:param name="idMission" value="%{mission.id}"/>
</s:url>
<s:if test="mission.id != null">
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
<br>
<%-- Formulaire --%>
<s:form id="" method="POST">
	<s:if test="mission.id != null">
		<s:hidden name="idMission" value="%{mission.id}" />
	</s:if>
	
	<%-- Remplissage des paramètres cachés utiles pour l'action--%>
	<s:hidden name="idItineraire" value="%{idItineraire}"/>
	<s:hidden name="idLigne" value="%{idLigne}" />		
	<s:hidden name="idTableauMarche" value="%{idTableauMarche}" />
	<s:hidden name="seuilDateDepartCourse" value="%{seuilDateDepartCourse}" />
	<s:hidden name="page" value="%{page}" />

	<s:textfield key="mission.name" />
	<s:textfield key="mission.registrationNumber" />
	<s:textfield key="mission.publishedName" />
	<s:textfield key="mission.comment"/>
	
	<%-- Actions --%>
	<s:if test="mission.id != null">	
  		<tr>
  			<td colspan="2">
  				<s:submit value="%{getText('action.update')}" action="creer_Mission_sur_son_itineraire" theme="simple" cssStyle="float: right;"/>
  				<s:submit value="%{getText('action.cancel')}" action="annuler_Mission_sur_son_itineraire" theme="simple" cssStyle="float: right;"/>
  			</td>
  		</tr>
  	</s:if>
  	<s:else>
  		<tr>
  			<td colspan="2">
  				<s:submit value="%{getText('action.create')}" action="creer_Mission_sur_son_itineraire" theme="simple" cssStyle="float: right;"/>
  				<s:submit value="%{getText('action.cancel')}" action="annuler_Mission_sur_son_itineraire" theme="simple" cssStyle="float: right;"/>
  			</td>
  		</tr>
  	</s:else>
  	<%-- Ajout des balises tr et td pour le faire apparaitre dans le tableau --%>
  	<tr><td style="text-align:center;"><s:include value="/jsp/commun/asterisque.jsp" /></td></tr>  	
</s:form>
