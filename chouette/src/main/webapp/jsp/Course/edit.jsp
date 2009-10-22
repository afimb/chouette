<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>
<s:include value="/jsp/commun/scriptaculous.jsp" />
<%-- Titre et barre de navigation --%>
<s:url id="urlCourseUpdate" action="crud_Course!edit">
	<s:param name="idCourse" value="%{course.id}"/>
</s:url>
<s:if test="course.id != null">
	<title><s:text name="text.course.update.title" /></title>	
	<s:property value="filAriane.addElementFilAriane(getText('text.course.update.title'), '', #urlCourseUpdate)"/>
</s:if> 
<s:else>
	<title><s:text name="text.course.create.title" /></title>
	<s:property value="filAriane.addElementFilAriane(getText('text.course.create.title'), '', #urlCourseUpdate)"/>
</s:else>
<div class="panelData">
	<s:property value="filAriane.texteFilAriane" escape="false"/>
</div>
<br>
<div>
<s:form id="creerCourseForm" method="POST">

	<s:if test="course.id != null">
		<s:hidden name="idCourse" value="%{course.id}"/>	
	</s:if>
	
	<%-- Remplissage des paramètres cachés utiles pour l'action--%>
	<s:hidden name="idItineraire" value="%{idItineraire}"/>
	<s:hidden name="idLigne" value="%{idLigne}" />	
	<s:hidden name="idTableauMarche" value="%{idTableauMarche}" />
	<s:hidden name="seuilDateDepartCourse" value="%{seuilDateDepartCourse}" />
	<s:hidden name="page" value="%{page}" />

	<s:textfield key="course.publishedJourneyName" />
	<s:textfield key="course.publishedJourneyIdentifier" />
	<s:if test="course.id != null">
	<s:select key="course.transportMode" list="@fr.certu.chouette.ihm.enumeration.EnumerationApplication@getModeTransportEnum()" listKey="enumeratedTypeAccess" listValue="textePropriete"/>
	</s:if>
	<s:else>
	<s:select name="course.transportMode" label="%{getText('course.transportMode')}" list="@fr.certu.chouette.ihm.enumeration.EnumerationApplication@getModeTransportEnum()" listKey="enumeratedTypeAccess" listValue="textePropriete" value="%{modeTransportLigne}"/>
	</s:else>
	<s:textfield key="course.comment" />			
	<%-- Actions --%>
	<s:if test="course.id != null">	
  		<tr>
  			<td colspan="2">
  				<s:submit value="%{getText('action.update')}" action="creer_Course_sur_son_itineraire" theme="simple" cssStyle="float: right;"/>
  				<s:submit value="%{getText('action.cancel')}" action="annuler_Course_sur_son_itineraire" theme="simple" cssStyle="float: right;"/>
  			</td>
  		</tr>
  	</s:if>
  	<s:else>
  		<tr>
  			<td colspan="2">
  				<s:submit value="%{getText('action.create')}" action="creer_Course_sur_son_itineraire" theme="simple" cssStyle="float: right;"/>
  				<s:submit value="%{getText('action.cancel')}" action="annuler_Course_sur_son_itineraire" theme="simple" cssStyle="float: right;"/>
  			</td>
  		</tr>
  	</s:else>
  	<%-- Ajout des balises tr et td pour le faire apparaitre dans le tableau --%>
  	<tr><td colspan="2"><s:include value="/jsp/commun/asterisque.jsp" /></td></tr>	
</s:form>

<%-- Association des tableaux de marche--%>
</div>
<br><br>
<s:if test="course.id != null">
	<div>
		<%-- Affichage de la liste des tableaux de marche--%>
		<b>Calendriers d'application</b>
	</div>
	<br>
	<div>
	<s:form action="creerAssociationTableauMarche" id="associerTableauMarcheForm" onsubmit="TridentAutoComplete.beforeSubmit();">
		<s:if test="course.id != null">
			<s:hidden name="idCourse" value="%{course.id}"/>	
		</s:if>
	
		<%-- Remplissage des paramètres cachés utiles pour l'action--%>
		<s:hidden name="idItineraire" value="%{idItineraire}"/>
		<s:hidden name="idLigne" value="%{idLigne}" />			

		<s:textfield name="saisieTableauMarche" id="tableauMarches_auto_complete" size="60" value="" /> 
		<s:hidden name="saisieTableauMarcheKey" id="saisieTableauMarcheKey" value=""/>
		<div id="tableauMarches_auto_complete_list" class="tableauMarches_auto_complete_list" style="display:none;"></div>
				 
		 <s:submit value="%{getText('course.add.timetable')}" formId="associerTableauMarcheForm" />
	</s:form >
	</div>
	<div id="displaytag"> 
	<display:table name="tableauxMarche" sort="list" pagesize="10" requestURI="" id="tableauMarche" export="false">	
	  	<display:column title="Actions" sortable="false">
	  		<s:url id="supprimerAssociationTableauMarche" action="supprimerAssociationTableauMarche">
				<s:param name="idTableauMarche" value="${tableauMarche.id}" />
			</s:url>
			<s:url id="editTableauMarche" action="crud_TableauMarche!edit">
				<s:param name="idCourse" value="%{idCourse}" />
				<s:param name="idTableauMarche" value="${tableauMarche.id}" />
			</s:url>
			<s:a href="%{editTableauMarche}"><img border="0" src="images/editer.png" title="<s:text name="tooltip.edit"/>"></s:a>&nbsp;&nbsp;
	    	<s:a href="%{supprimerAssociationTableauMarche}" onclick="return confirm('%{getText('popup.confirmer')}'"><img border="0" src="images/supprimer.png" title="<s:text name="tooltip.delete"/>"></s:a> 	
	  	</display:column>
	  	<display:column title="Commentaire" property="comment" sortable="false"/>
	</display:table>
	</div>
</s:if>
<script type="text/javascript">
	var tableauMarches = <%= request.getAttribute("jsonTableauMarches") %>;
	
	function autocompletion() 
	{ 
		new Autocompleter.Local('tableauMarches_auto_complete', 'tableauMarches_auto_complete_list', Object.keys(tableauMarches), {}); 
		$('tableauMarches_auto_complete').focus(); 
	}
	
	Event.observe(window, 'load', autocompletion);
	
	var TridentAutoComplete = 
	{
		beforeSubmit: function() 
		{
			var value = tableauMarches[$('tableauMarches_auto_complete').value];
			if (value == null)
				$('saisieTableauMarcheKey').value="";
			else	
				$('saisieTableauMarcheKey').value = value;
			return true;
		}
	};
</script>
