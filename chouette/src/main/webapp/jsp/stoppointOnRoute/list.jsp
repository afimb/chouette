<%@ taglib prefix="s" uri="/struts-tags"%>

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

<%-- Titre et barre de navigation --%>	
<title><s:text name="text.arretSurItineraire.list.title" /></title>
<s:url id="urlArretSurItineraires" action="list" namespace="/stoppointOnRoute" includeParams="none">
  <s:param name="idItineraire" value="%{idItineraire}" />
</s:url>
<s:property value="filAriane.addElementFilAriane(getText('text.arretSurItineraire.list.title'), itineraire.name, #urlArretSurItineraires)"/>
<div class="panelData">
  <s:property value="filAriane.texteFilAriane" escape="false"/>
</div>

<br/>

<%-- INSERTION D'UN ARRET --%>
<div id="insererArret" style="margin:0px; padding:0px; display:none; border:solid 0px black;">

  <s:form id="insererArretForm" namespace="/stoppointOnRoute" theme="simple" onsubmit="TridentAutoComplete.beforeSubmit();">

    <s:hidden name="idItineraire" value="%{idItineraire}"/>
    <s:hidden name="idLigne" value="%{idLigne}" />
    <s:hidden name="positionArret" value="%{positionArret}" id="positionArret" />

    <div style="padding-left:2px"><s:text name=""/></div>
    <div>
      <s:textfield name="nomArretAInserer" id="nomArretAInserer" size="100" value="" />
      <s:hidden name="idArretAInserer" id="idArretAInserer" value=""/>
      <div id="listeArrets" class="stop_areas_auto_complete_list" style="display:none;"></div>
    </div>
    <div>
      <s:reset value="%{getText('action.cancel')}" onclick="annulerCreationArret();" />
      <s:submit formId="insererArretForm" action="insererArret" value="Insérer Un Nouvel Arrèt" />
      <s:submit formId="insererArretForm" action="insererArret" value="Insérer l'Arrèt Existant" />
    </div>

  </s:form>

</div>

<script type="text/javascript">$('insererArret').hide();</script>

<br>

<div id="displaytag">
  <s:form id="deplacementArretForm" namespace="/stoppointOnRoute">
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
                <%-- Itération sur la liste des checkbox pour le déplacement d'arrèt  --%>
                <s:checkbox name="deplacementsArret[%{id}]" theme="simple" onclick="checkPermutation()" id="checkbox"/>
              </TD>
              <TD>
                <%-- Itération sur la liste des actions : Insertion d'un arrèt nouveau ou existant ou suppression d'un arrèt  --%>
                <s:url action="supprimerArret" namespace="/stoppointOnRoute" id="supprimerArret">
                  <s:param name="idArret" value="%{id}" />
                  <s:param name="positionArret" value="%{position}" />
                </s:url>
                <%-- BOUTON INSERER ARRET --%>
                <s:a href="#"  onclick="initialiserCreationArret(%{#arret.position}, this)">
                  <img border="0" alt="Add" src="<s:url value='/images/ajouter.png'/>" title="<s:text name="tooltip.inserer"/>">
                </s:a>&nbsp;&nbsp;
                <s:a href="%{supprimerArret}" onclick="return confirm('%{getText('arretSurItineraire.delete.confirmation')}')">
                  <img border="0" alt="Delete" src="<s:url value='/images/supprimer.png'/>" title="<s:text name="tooltip.delete"/>">
                </s:a>
              </TD>
              <TD>
                <%-- Itération sur la liste des arrèts --%>
                <s:url action="edit"  namespace="/stoppoint" id="arretPhysiqueUrl">
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
                <img border="0" src="<s:url value='/images/ajouter.png'/>" title="<s:text name="tooltip.inserer"/>">
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
      <s:submit name="action" id="Bpermutation" action="deplacerArret" value="Valider Permutation" formId="deplacementArretForm"/>
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