<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>
<s:include value="/jsp/commun/autocompleteJavascript.jsp" />
<%-- Titre et barre de navigation --%>
<s:url id="urlCourseUpdate" action="edit" namespace="/vehicleJourney">
  <s:param name="idCourse" value="%{id}"/>
</s:url>
<s:if test="id != null">
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

<s:include value="/jsp/commun/messages.jsp" />

<div>
  <s:form id="creerCourseForm" method="POST">

    <s:hidden name="idCourse" value="%{id}"/>

    <%-- Remplissage des paramètres cachés utiles pour l'action--%>
    <s:hidden name="idItineraire" value="%{idItineraire}"/>
    <s:hidden name="idLigne" value="%{idLigne}" />
    <s:hidden name="idTableauMarche" value="%{idTableauMarche}" />
    <s:hidden name="seuilDateDepartCourse" value="%{seuilDateDepartCourse}" />
    <s:hidden name="page" value="%{page}" />
    <s:hidden key="actionMethod" value="%{actionMethod}"/>
    <s:hidden name="operationMode" value="%{'STORE'}" />

    <s:textfield key="publishedJourneyName" />
    <s:textfield key="publishedJourneyIdentifier" />
    <s:if test="id != null">
      <s:select key="transportMode" list="modesOfTransportEnum" listKey="enumeratedTypeAccess" listValue="textePropriete"/>
    </s:if>
    <s:else>
      <s:select key="transportMode" list="modesOfTransportEnum" listKey="enumeratedTypeAccess" listValue="textePropriete" value="%{modeTransportLigne}"/>
    </s:else>
    
    <%-- hack to be able to unselect all items of the checkboxlist--%>
    <s:hidden id="hack_peculiarities" name="particularites" value="" />
    <s:checkboxlist key="peculiarities" list="particularitesValides" name="particularites" />
    
    <s:textfield key="comment" />
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
  </s:form>
</div>
<br><br>

<%-- Association des tableaux de marche--%>
<s:if test="id != null">
  <div>
    <%-- Affichage de la liste des tableaux de marche--%>
    <b><s:text name="title.timeTable"/></b>
  </div>
  <br>
  <div>
    <s:form action="creerAssociationTableauMarche" namespace="/vehicleJourney" id="associerTableauMarcheForm" onsubmit="TridentAutoComplete.beforeSubmit();">
      <s:hidden name="idCourse" value="%{id}"/>

      <%-- Remplissage des paramètres cachés utiles pour l'action--%>
      <s:hidden name="idItineraire" value="%{idItineraire}"/>
      <s:hidden name="idLigne" value="%{idLigne}" />
      <s:hidden name="operationMode" value="%{'STORE'}" />
      <s:textfield name="saisieTableauMarche" id="tableauMarches_auto_complete" size="60" value="" />
      <s:hidden name="saisieTableauMarcheKey" id="saisieTableauMarcheKey" value=""/>
      <div id="tableauMarches_auto_complete_list" class="autocomplete"></div>

      <s:submit value="%{getText('course.add.timetable')}" formId="associerTableauMarcheForm" />
    </s:form>
  </div>
  <div id="displaytag">
    <display:table name="tableauxMarche" sort="list" pagesize="10" requestURI="" id="tableauMarche" export="false">
      <display:column titleKey="table.title.action" sortable="false">
        <s:url id="supprimerAssociationTableauMarche" action="supprimerAssociationTableauMarche" namespace="/vehicleJourney">
          <s:param name="idTableauMarche">${tableauMarche.id}</s:param>
          <s:param name="idCourse" value="%{idCourse}"/>
          <s:param name="idItineraire" value="%{idItineraire}"/>
          <s:param name="idLigne" value="%{idLigne}" />
          <s:param name="operationMode" value="%{'STORE'}" />
        </s:url>
        <s:url id="editTableauMarche" action="edit" namespace="/timeTable">
          <s:param name="idCourse" value="%{idCourse}" />
          <s:param name="idTableauMarche">${tableauMarche.id}</s:param>
        </s:url>
        <s:a href="%{editTableauMarche}">
          <img border="0" alt="Edit" src="<s:url value='/images/editer.png'/>" title="<s:text name="tooltip.edit"/>">
        </s:a>&nbsp;&nbsp;
        <s:a href="%{supprimerAssociationTableauMarche}" onclick="return confirm('%{getText('popup.confirmer')}'">
          <img border="0" alt="Delete" src="<s:url value='/images/supprimer.png'/>" title="<s:text name="tooltip.delete"/>">
        </s:a>
      </display:column>
      <display:column titleKey="table.title.comment" property="comment" sortable="false"/>
    </display:table>
  </div>
</s:if>

<script type="text/javascript">
  var tableauMarches = <%= request.getAttribute("jsonTableauMarches")%>;
	
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
