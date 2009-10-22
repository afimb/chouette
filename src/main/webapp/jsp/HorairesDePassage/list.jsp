<%@ taglib prefix="s" uri="/struts-tags" %>

<head>
	<script type="text/javascript">
		var erreurAjoutCourseAvecDecalageTemps = <s:property value="erreurAjoutCourseAvecDecalageTemps"/>;
	</script>
	<script language="JavaScript" type="text/javascript" src="<s:url value='/js/HorairesDePassage.js' includeParams='none'/>" ></script>
</head>


<%-- TITRE ET BARRE DE NAVIGATION --%>	

<title><s:text name="text.horairesDePassage.list.title" /></title>
<s:url id="urlHorairesDePassages" action="liste_HorairesDePassage" includeParams="none">
	<s:param name="idItineraire" value="%{idItineraire}" />
</s:url>
<s:property value="filAriane.addElementFilAriane(getText('text.horairesDePassage.list.title'), itineraire.name, #urlHorairesDePassages)"/>
<div class="panelData">
	<s:property value="filAriane.texteFilAriane" escape="false"/>
</div>


<br/>


	<%-- BOUTON AJOUT NOUVELLE COURSE --%>
	
	<div>
		<s:url id="editCourse" action="crud_Course!edit"/>
		<s:a href="%{editCourse}"><b><s:text name="text.course.create.button"/></b></s:a>
	</div>
	
<s:if test="arretsItineraire.size > 0 && coursesPage.size >= 0">	
	
	<br/>	
	
	
	<%-- BLOQUE FILTRE --%>
	
	<div id="filtrageTableauMarche">
		<s:form action="liste_HorairesDePassage" id="filtrageTableauMarcheForm">
			<s:select	name="idTableauMarche" 
						label="%{getText('horairesDePassage.tableauMarcheFiltre')}" 
						list="tableauxMarche" 
						listKey="id" 
						listValue="comment" 
						headerKey="" 
						headerValue="%{getText('horairesDePassage.tableauMarcheTous')}" />
			<s:textfield name="seuilDateDepartCourse" label="%{getText('filtre.select.seuilDateDepartCourse')}"></s:textfield>
			<s:hidden name="idItineraire" value="%{idItineraire}" />
			<s:hidden name="idLigne" value="%{idLigne}" />
			<%-- FORCER LE NUMERO DE PAGE A 1 --%>
			<%-- CAR UNE NOUVELLE RECHERCHE NECESSITE --%>
			<%-- D'ALLER SUR LA PREMIERE PAGE DE RESULTAT DE RECHERCHE --%>
			<s:hidden name="page" value="1" />
			<tr>
  				<td colspan="2">
					<s:submit value="%{getText('action.filtrer')}" theme="simple" cssStyle="float:right;" />	  				
					<s:submit value="%{getText('action.cancel')} Filtre" action="HorairesDePassage_cancel" theme="simple" cssStyle="float:right;"/>
				</td>
			</tr>
		</s:form>
	</div>
	 
	<%-- BLOQUE AJOUT COURSE AVEC DECALAGE TEMPS --%>
	
	<span id="decalageTemps">
	<br/>
	<div>
		<s:form id="ajoutCourseAvecDecalageTempsForm">
			<s:textfield name="tempsDecalage" label="%{getText('horairesDePassage.tempsDecalage')}" value="00:00"/>
			<s:textfield name="nbreCourseDecalage" label="%{getText('horairesDePassage.nbreCourseDecalage')}" value="1"/>
			<s:hidden name="idCourseADecaler" id="idCourseADecaler"/>
			<s:hidden name="idItineraire" value="%{idItineraire}"/>
			<s:hidden name="idLigne" value="%{idLigne}"/>
			<s:hidden name="idTableauMarche" value="%{idTableauMarche}" />
			<s:hidden name="seuilDateDepartCourse" value="%{seuilDateDepartCourse}" />	
			<s:hidden name="page" value="%{page}"/>
			<tr>
  				<td colspan="2">			
					<s:submit value="%{getText('action.validate')}" action="HorairesDePassage_ajoutCourseAvecDecalageTemps" theme="simple" cssStyle="float: right;" formId="ajoutCourseAvecDecalageTempsForm" onclick="cacherDecalageTemps()"/>
  					<s:submit value="%{getText('action.cancel')}" action="HorairesDePassage_cancel" theme="simple" cssStyle="float: right;" formId="ajoutCourseAvecDecalageTempsForm" onclick="cacherDecalageTemps()"/> 
				</td>
			</tr>				
		</s:form>
	</div>
	</span>
	<script type="text/javascript" >
		cacherBloqueDecalageTemps();
	</script>
	
	<%-- TABLEAU HORAIRES --%>
	
	<div>
		<s:form theme="simple" id="updateForm" method="POST">
			<s:hidden name="idItineraire" value="%{idItineraire}" />
			<s:hidden name="idLigne" value="%{idLigne}" />
			<s:hidden name="idTableauMarche" value="%{idTableauMarche}" />
			<s:hidden name="seuilDateDepartCourse" value="%{seuilDateDepartCourse}" />		
			<s:hidden name="page" value="%{page}" />		
			<div ALIGN="center"></div>
			<br/>
			<div>
				<div id="displaytag">
					<table>
						<thead><s:include value="/jsp/HorairesDePassage/listHead.jsp" /></thead>
						<tbody><s:include value="/jsp/HorairesDePassage/listBody.jsp" /></tbody>
					</table>
				</div>
				<br/>
				<div ALIGN="center">
					<s:include value="/jsp/HorairesDePassage/listPagination.jsp" />
				</div>
			</div>	
		</s:form>
	</div>
	
</s:if>