<%@ taglib prefix="s" uri="/struts-tags"%>
<head>

	<s:include value="/jsp/commun/scriptaculous.jsp" />

	<SCRIPT type="text/javascript" >
		 
		 /**
		 * Fonction permettant de rendre disponible ou pas le bouton de validation des permutations : 
		 * - si 2 cases coches disponible
		 * - sinon indisponible  
		 */
		 function checkPermutation ()
		 {
		 	var length = $$('table tr td input').select(function(el) { return el.type == "checkbox" && el.checked }).length;
		 	if (length == 2) {
		 		$('Bpermutation').enable();
		 	} else 
		 	{
		 		$('Bpermutation').disable();
		 	}
		 }
		 
		var trSelectionne = undefined;
		
		function initialiserCreationArret (positionArretSelectionne, noeudSelectionne) {
			if (trSelectionne != undefined) $(trSelectionne).removeClassName('selected');
			trSelectionne = noeudSelectionne.parentNode.parentNode;
			$(trSelectionne).addClassName('selected');
			$('positionArret').value = positionArretSelectionne;
			$('insererArret').show();
		}
		
		function annulerCreationArret() {
			$('insererArret').hide();
			$(trSelectionne).removeClassName('selected');
		}
		
	</SCRIPT>	
</head>
<%-- Titre et barre de navigation --%>	
<title><s:text name="text.arretSurItineraire.list.title" /></title>
<s:url id="urlArretSurItineraires" action="liste_ArretSurItineraire" includeParams="none">
	<s:param name="idItineraire" value="%{idItineraire}" />
</s:url>
<s:property value="filAriane.addElementFilAriane(getText('text.arretSurItineraire.list.title'), itineraire.name, #urlArretSurItineraires)"/>
<div class="panelData">
	<s:property value="filAriane.texteFilAriane" escape="false"/>
</div>

<br/>

<%-- INSERTION D'UN ARRET --%>
<div id="insererArret" style="margin:0px; padding:0px; display:none; border:solid 0px black;">

	<s:form id="insererArretForm" theme="simple" onsubmit="TridentAutoComplete.beforeSubmit();">
	
		<s:hidden name="idItineraire" value="%{idItineraire}"/>
		<s:hidden name="idLigne" value="%{idLigne}" />
		<s:hidden name="positionArret" value="%{positionArret}" id="positionArret" />
				
		<div style="padding-left:2px">Veuillez saisir le nom de l'arr�t � ins�rer</div>
		<div>
			<s:textfield name="nomArretAInserer" id="nomArretAInserer" size="100" value="" />
			<s:hidden name="idArretAInserer" id="idArretAInserer" value=""/>
			<div id="listeArrets" class="stop_areas_auto_complete_list" style="display:none;"></div>
		</div>
		<div>
			<s:reset value="%{getText('action.cancel')}" onclick="annulerCreationArret();" />
			<s:submit formId="insererArretForm" action="ArretSurItineraire_insererArret" value="Ins�rer Un Nouvel Arr�t" />
			<s:submit formId="insererArretForm" action="ArretSurItineraire_insererArret" value="Ins�rer l'Arr�t Existant" />
		</div>
		
	</s:form>

</div>

	<script type="text/javascript">$('insererArret').hide();</script>

	<br>

	<div id="displaytag">
	
		<s:form id="deplacementArretForm">
		<s:hidden name="idItineraire" value="%{idItineraire}"/>
		<s:hidden name="idLigne" value="%{idLigne}"/>	
		<table>
			<THEAD>
				<TR>
					<TH>Permutations</TH>
					<TH>Actions</TH>
					<TH>Arrets</TH>
				</TR>
			</THEAD>
			<TBODY>
				<%  String TRParityClass = ""; %>
				<s:if test="arrets.size > 0">
				<s:iterator value="arrets" status="rangArret" id="arret">			
				<s:if test="#rangArret.odd == true">
				<%
					TRParityClass = "odd";
				%>
			</s:if>
			<s:else>
				<%
					TRParityClass = "even";
				%>
			</s:else>
				<TR class="${TRParityClass}">
					<TD>
						<%-- It�ration sur la liste des checkbox pour le d�placement d'arr�t  --%>
					    <s:checkbox name="deplacementsArret[%{id}]" theme="simple" onclick="checkPermutation()" id="checkbox"/>
					</TD>
					<TD>
						<%-- It�ration sur la liste des actions : Insertion d'un arr�t nouveau ou existant ou suppression d'un arr�t  --%>
						<s:url action="ArretSurItineraire_supprimerArret" id="supprimerArret">
							<s:param name="idArret" value="%{id}" />
							<s:param name="positionArret" value="%{position}" />
						</s:url>
						<%-- BOUTON INSERER ARRET --%>
						<s:a href="#" onclick="initialiserCreationArret(${arret.position}, this)">
							<img border="0" src="images/ajouter.png" title="<s:text name="tooltip.inserer"/>">
						</s:a>&nbsp;&nbsp;
						<s:a href="%{supprimerArret}" onclick="return confirm('%{getText('arretSurItineraire.delete.confirmation')}')">
							<img border="0" src="images/supprimer.png" title="<s:text name="tooltip.delete"/>">
						</s:a>
					</TD>
					<TD>
						<%-- It�ration sur la liste des arrets --%> 
						<s:url action="crud_PositionGeographique!edit" id="arretPhysiqueUrl">
							<s:param name="idPositionGeographique" value="%{idPhysique}" />
							<s:param name="idLigne" value="idLigne" />
							<s:param name="idItineraire" value="idItineraire" />
							<s:param name="typePositionGeographique" value="%{'arretPhysique'}" />
						</s:url>
						<s:a href="%{arretPhysiqueUrl}" id="nomArretPhysique%{#rangArret.index}">
							<s:if test="getArretPhysique(id).name != null && getArretPhysique(id).name != ''">
								<s:property value="getArretPhysique(id).name"/>
							</s:if>
							<s:else>
								<s:text name="arretSurItineraire.anonyme"></s:text> (<s:text name="getArretPhysique(id).objectId"/>)
							</s:else>
						</s:a>
					</TD>
				</TR>
				</s:iterator>
				</s:if>
				<s:else>
					<TR class="odd">
					<TD>
						&nbsp;&nbsp;
					</TD>
					<TD>
						<%-- BOUTON INSERER ARRET --%>
						<s:a href="#" onclick="initialiserCreationArret(0, this)">
							<img border="0" src="images/ajouter.png" title="<s:text name="tooltip.inserer"/>">
						</s:a>
					</TD>
					<TD>
						&nbsp;&nbsp;
					</TD>
				</TR>
				</s:else>
			</TBODY>
		</table>
		<s:if test="arrets.size > 0">
		<s:submit name="action" id="Bpermutation" action="ArretSurItineraire_deplacerArret" value="Valider Permutation" formId="deplacementArretForm"/>
		</s:if>
		</s:form>	
	</div>

<script type="text/javascript"><!--
// <![CDATA[

	var arretsPhysiques = <%=request.getAttribute("jsonArrets")%>;
	var arretsVide = <%= request.getAttribute("arretsVide")%>;

	if(!arretsVide) checkPermutation ();
	
	function autocompletion() {
		new Autocompleter.Local('nomArretAInserer', 'listeArrets', Object.keys(arretsPhysiques), {}); 
		$('nomArretAInserer').focus(); 
	}
	
	Event.observe(window, 'load', autocompletion);
	
	var TridentAutoComplete = {
		beforeSubmit : function() {
			var value = arretsPhysiques[$('nomArretAInserer').value];
			if (value == null) $('idArretAInserer').value="";
			else $('idArretAInserer').value = value;
			return true;
		}
	};
	
// ]]>
--></script>