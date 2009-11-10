<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>

<head>

	<s:include value="/jsp/commun/scriptaculous.jsp" />

	<SCRIPT type="text/javascript" >

		var trSelectionne = undefined;
			 
		function initialiserFusionArret(idArretSource, noeudSelectionne) {
			if (trSelectionne != undefined) $(trSelectionne).removeClassName('selected');
			trSelectionne = noeudSelectionne.parentNode.parentNode;
			$(trSelectionne).addClassName('selected');
			$('idArretSource').value = idArretSource;
			$('fusionnerArret').show();
		}
	 
		function annulerFusionArret() {
			$('fusionnerArret').hide();
			$(trSelectionne).removeClassName('selected');
		}
			 
	</SCRIPT>
		
</head>

<%-- Titre et barre de navigation --%>
<s:url id="urlPositionGeographiques" action="liste_PositionGeographique" includeParams="none">
	<s:param name="typePositionGeographique" value="%{typePositionGeographique}"/>
</s:url>
<s:if test="typePositionGeographique == 'arretPhysique'">
	<title><s:text name="text.arretPhysique.list.title" /></title>
	<s:property value="filAriane.addElementFilAriane(getText('text.arretPhysique.list.title'), '', #urlPositionGeographiques)"/>
</s:if>
<s:else>
	<title><s:text name="text.zone.list.title" /></title>
	<s:property value="filAriane.addElementFilAriane(getText('text.zone.list.title'), '', #urlPositionGeographiques)"/>
</s:else>		
<div class="panelData">
	<s:property value="filAriane.texteFilAriane" escape="false"/>
</div>

<br>

<%-- Filtre --%>
<div>
	<s:form action="liste_PositionGeographique">
		<s:hidden name="typePositionGeographique" value="%{typePositionGeographique}" />
		<s:select name="idReseau" label="%{getText('filtre.select.reseau')}" value="%{idReseau}" list="reseaux" listKey="id" listValue="name" headerKey="" headerValue="%{getText('filtre.reseau.dropDownListItem.tous')}" />
		<s:textfield name="nomArret" label="%{getText('filtre.select.nomArret')}"></s:textfield>
		<s:textfield name="codeInsee" label="%{getText('filtre.select.codeInsee')}"></s:textfield>
		<s:submit value="%{getText('action.filtrer')}"/>
	</s:form>
</div>

</br>

<%-- Actions --%>
<div class="actions">
	<s:url action="PositionGeographique!edit" id="editPositionGeographique">
		<s:param name="typePositionGeographique" value="%{typePositionGeographique}" />	
	</s:url>
	<s:if test="typePositionGeographique == 'arretPhysique'">
		<s:a href="%{editPositionGeographique}"><b><s:text name="text.arretPhysique.create.button"/></b></s:a>
	</s:if>
	<s:else>
		<s:a href="%{editPositionGeographique}"><b><s:text name="text.zone.create.button"/></b></s:a>
	</s:else>		
</div>

<br/>

<%-- FUSION D'UN ARRET --%>
<div id="fusionnerArret" style="margin:0px; padding:0px; display:none; border:solid 0px black;">

	<s:form id="fusionnerArretForm" theme="simple" onsubmit="TridentAutoComplete.beforeSubmit();">
		<s:hidden name="typePositionGeographique" value="arretPhysique" />
		<div style="padding-left:2px">Veuillez saisir le nom de l'arrêt vers lequel vous souhaitez fusionner votre arrêt de départ</div>
		<div>
			<s:textfield name="nomArretDestination" id="nomArretDestination" size="100" value="" />
			<s:hidden name="idArretDestination" id="idArretDestination" value=""/>
			<s:hidden name="idArretSource" id="idArretSource" value=""/>
			<div id="listeArrets" class="stop_areas_auto_complete_list" style="display:none;"></div>
		</div>
		<div>
			<s:reset value="%{getText('action.cancel')}" onclick="annulerFusionArret();" />
			<s:submit formId="fusionnerArretForm" action="PositionGeographique_fusionnerArrets" value="%{getText('action.validate')}" />
		</div>
		
	</s:form>

</div>

<br/>

<%-- Tableau --%>
<div id="displaytag"> 

	<display:table name="positionGeographiques" pagesize="20" requestURI="" id="positionGeographique" export="false">

	  	<display:column title="Action" sortable="false">
	  	
	  		<%-- BOUTON EDITER --%>
			<s:url id="editUrl" action="PositionGeographique!edit">
        <s:param name="idPositionGeographique">${positionGeographique.id}</s:param>
			</s:url>
			<s:a href="%{editUrl}"><img border="0" src="images/editer.png" title="<s:text name="tooltip.edit"/>"></s:a>&nbsp;&nbsp;
			
			<%-- BOUTON FUSIONNER --%>
			<s:if test="typePositionGeographique == 'arretPhysique'">
				<s:a href="#" onclick="javascript:initialiserFusionArret('${positionGeographique.id}', this);">
					<img border="0" src="images/ajouter.png" title="<s:text name="tooltip.fusionner"/>">
				</s:a>&nbsp;&nbsp;
			</s:if>

			<%-- BOUTON SUPPRIMER --%>
			<s:url id="deletePositionGeographique" action="PositionGeographique!delete">
        <s:param name="idPositionGeographique">${positionGeographique.id}</s:param>
			</s:url>			
			<s:if test="typePositionGeographique == 'arretPhysique'">
	    		<s:a href="%{deletePositionGeographique}" onclick="return confirm('%{getText('arretPhysique.delete.confirmation')}');" cssStyle="visibility:%{getLiaisonItineraire(${positionGeographique.id})};"><img border="0" src="images/supprimer.png" title="<s:text name="tooltip.delete"/>"></s:a> 	
			</s:if>	  	
			<s:else>
				<s:a href="%{deletePositionGeographique}" onclick="return confirm('%{getText('zone.delete.confirmation')}');"><img border="0" src="images/supprimer.png" title="<s:text name="tooltip.delete"/>"></s:a>
			</s:else>
			
	  	</display:column>	  	

	  	<display:column title="Nom" property="name" />

	  	<display:column title="Identifiant de l'objet" property="objectId" />

	  	<display:column title="Type" >
	  		<s:text name="${positionGeographique.areaType}"/>
	  	</display:column>	  	

	</display:table>

</div>

<script type="text/javascript"><!--
// <![CDATA[

	var arretsPhysiques = <%=request.getAttribute("jsonArrets")%>;
	
	function autocompletion() { 
		new Autocompleter.Local('nomArretDestination', 'listeArrets', Object.keys(arretsPhysiques), {}); 
		$('nomArretDestination').focus(); 
	}
	
	Event.observe(window, 'load', autocompletion);
	
	var TridentAutoComplete = {
		beforeSubmit : function() {
			var value = arretsPhysiques[$('nomArretDestination').value];
			if (value == null) $('idArretDestination').value="";
			else $('idArretDestination').value = value;
			return true;
		}
	};
	
// ]]>
--></script>