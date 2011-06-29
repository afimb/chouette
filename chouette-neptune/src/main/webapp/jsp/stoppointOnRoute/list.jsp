<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<s:include value="/jsp/commun/autocompleteJavascript.jsp" />

<%-- Titre et barre de navigation --%>
<title><s:text name="text.arretSurItineraire.list.title" /></title>
<s:url id="urlArretSurItineraires" action="list" namespace="/stoppointOnRoute" includeParams="none">
  <s:param name="idItineraire" value="%{idItineraire}" />
</s:url>
<s:property value="filAriane.addElementFilAriane(getText('text.arretSurItineraire.list.title'), itineraire.name, #urlArretSurItineraires)"/>
<div class="panelData">
  <s:property value="filAriane.texteFilAriane" escape="false"/>
</div>

<s:include value="/jsp/commun/messages.jsp" />

<%-- INSERTION D'UN ARRET --%>
<div id="insererArret" style="margin:0px; padding:0px; display:none; border:solid 0px black;">

  <s:form id="insererArretForm" namespace="/stoppointOnRoute" theme="simple">

    <s:hidden name="idItineraire" value="%{idItineraire}"/>
    <s:hidden name="idLigne" value="%{idLigne}" />
    <s:hidden name="positionArret" value="%{positionArret}" id="positionArret" />
    <s:hidden name="operationMode" value="%{'STORE'}"/>

    <div style="padding-left:2px"><s:text name=""/></div>
    <div>
      <s:hidden name="idArretAInserer" id="idArretAInserer" value=""/>
      <s:hidden name="operationMode" value="%{'STORE'}" />
      <p>
        <s:textfield name="nomArretAInserer" id="nomArretAInserer"  size="75" value="" />
        <span id="indicator" style="display: none">
          <img src="<s:url value='/images/ajax-loader.gif'/>" alt="Working..." />
        </span>
      </p>
      <div id="listeArrets" class="autocomplete"></div>
    </div>
    <div>
      <s:reset value="%{getText('action.cancel')}" onclick="annulerCreationArret();" />
      <s:submit formId="insererArretForm" action="insererArret" value="%{getText('stoppointOnRoute.insert.new')}" />
      <s:submit formId="insererArretForm" action="insererArret" value="%{getText('stoppointOnRoute.insert.existing')}" />
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
          <TH><s:text name="table.title.rotate"/></TH>
          <TH><s:text name="table.title.action"/></TH>
          <TH><s:text name="table.title.stoppointOnRoute"/></TH>
        </TR>
      </THEAD>
      <TBODY>
        <s:if test="arrets.size > 0">
          <s:iterator value="arrets" status="rangArret" id="arret">
            <TR class="<s:if test="#rangArret.odd == true ">odd</s:if><s:else>even</s:else>">
              <TD>
                <%-- Itération sur la liste des checkbox pour le déplacement d'arrèt  --%>
                <s:checkbox name="deplacementsArret[%{id}]" theme="simple" onclick="checkPermutation()" id="checkbox"/>
              </TD>
              <TD>
                <%-- Itération sur la liste des actions : Insertion d'un arrèt nouveau ou existant ou suppression d'un arrèt  --%>
                <s:url action="supprimerArret" namespace="/stoppointOnRoute" id="supprimerArret">
                  <s:param name="idArret" value="%{id}" />
                  <s:param name="positionArret" value="%{position}" />
                  <s:param name="idLigne" value="idLigne" />
                  <s:param name="idItineraire" value="idItineraire" />
                  <s:param name="operationMode">STORE</s:param>
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
                <s:url action="edit"  namespace="/boardingPosition" id="arretPhysiqueUrl">
                  <s:param name="idPositionGeographique" value="%{idPhysique}" />
                  <s:param name="idLigne" value="idLigne" />
                  <s:param name="idItineraire" value="idItineraire" />
                </s:url>
                <s:a href="%{arretPhysiqueUrl}" id="nomArretPhysique%{#rangArret.index}">
                  <s:if test="getArretPhysique(id).name != null && getArretPhysique(id).name != ''">
                    <s:property value="getArretPhysique(id).name"/>
                  </s:if>
                  <s:else>
                    <s:text name="arretSurItineraire.anonyme"/> (<s:property value="getArretPhysique(id).objectId"/>)
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
      <s:hidden name="operationMode" value="%{'STORE'}"/>
      <s:submit name="action" id="Bpermutation" action="deplacerArret" value="%{getText('stoppointOnRoute.validate.exchange')}" formId="deplacementArretForm"/>
    </s:if>
  </s:form>
</div>

<script type="text/javascript">
  <!--
  // <![CDATA[

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

  function hiddenFields(text, li)
  {
    $('idArretAInserer').value = li.id;
  }

  var url = '<%=request.getContextPath()%>' + "/boardingPosition/ajaxBoardingPositions";
  new Ajax.Autocompleter(
  "nomArretAInserer",   // id du champ de formulaire
  "listeArrets",  // id de l'élément utilisé pour les propositions
  url,  // URL du script côté serveur
  {
    paramName: 'boardingPositionName',  // Nom du paramètre reçu par le script serveur
    minChars: 1,   // Nombre de caractères minimum avant que des appels serveur ne soient effectués
    method:'get',
    indicator: 'indicator',
    afterUpdateElement: hiddenFields
  });

  $('nomArretAInserer').focus();
  document.observe("dom:loaded", function() {
     checkPermutation();
  });

  // ]]>
  -->
</script>