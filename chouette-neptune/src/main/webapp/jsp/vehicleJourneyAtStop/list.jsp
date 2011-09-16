<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags" %>

<head>
  <script type="text/javascript">
		var erreurAjoutCourseAvecDecalageTemps = <s:property value="erreurAjoutCourseAvecDecalageTemps"/>;
  </script>
  <script language="JavaScript" type="text/javascript" src="<s:url value='/js/HorairesDePassage.js' includeParams='none'/>" ></script>
</head>


<%-- TITRE ET BARRE DE NAVIGATION --%>

<title><s:text name="text.horairesDePassage.list.title" /></title>
<s:url id="urlHorairesDePassages" action="list" namespace="/vehicleJourneyAtStop" includeParams="none">
  <s:param name="idItineraire" value="%{idItineraire}" />
</s:url>
<s:property value="filAriane.addElementFilAriane(getText('text.horairesDePassage.list.title'), routeName, #urlHorairesDePassages)"/>
<div class="panelData">
  <s:property value="filAriane.texteFilAriane" escape="false"/>
</div>

<s:include value="/jsp/commun/messages.jsp" />

<%-- BOUTON AJOUT NOUVELLE COURSE --%>
<div>
  <s:url id="editCourse" action="add" namespace="/vehicleJourney">
    <s:param name="idCourse" value="id" />
    <s:param name="idItineraire" value="idItineraire" />
    <s:param name="idLigne" value="idLigne" />
    <s:param name="idTableauMarche" value="idTableauMarche" />
    <s:param name="seuilHeureDepartCourse">
      <s:if test="seuilHeureDepartCourse != null">
        <s:date name="seuilHeureDepartCourse" format="HH:mm"/>
      </s:if>
    </s:param>
    <s:param name="page" value="page" />
  </s:url>
  <s:a href="%{editCourse}"><b><s:text name="text.course.create.button"/></b></s:a>
</div>
<br>

<s:if test="arretsItineraire.size > 0 && coursesPage.size >= 0">
  <%-- BLOC FILTRE --%>
  <div id="filtrageTableauMarche">
    <s:form namespace="/vehicleJourneyAtStop" id="filtrageTableauMarcheForm">
      <s:select	name="idTableauMarche"
                label="%{getText('horairesDePassage.tableauMarcheFiltre')}"
                list="tableauxMarche"
                headerKey=""
                headerValue="%{getText('horairesDePassage.tableauMarcheTous')}" />
      <s:textfield name="seuilHeureDepartCourse" label="%{getText('filtre.select.seuilHeureDepartCourse')}"></s:textfield>
      <s:hidden name="idItineraire" value="%{idItineraire}" />
      <s:hidden name="idLigne" value="%{idLigne}" />
      <%-- FORCER LE NUMERO DE PAGE A 1 CAR UNE NOUVELLE RECHERCHE NECESSITE D'ALLER SUR LA PREMIERE PAGE DE RESULTAT DE RECHERCHE --%>
      <s:hidden name="page" value="1" />
      <tr>
        <td colspan="2">
          <s:submit value="%{getText('action.filtrer')}" action="list" theme="simple" cssClass="right" />
          <s:submit value="%{getText('action.cancel.filter')}" action="cancel" theme="simple" cssClass="right"/>
        </td>
      </tr>
    </s:form>
  </div>

  <%-- BLOC AJOUT COURSE AVEC DECALAGE TEMPS --%>
  <div id="decalageTemps">
    <s:form namespace="/vehicleJourneyAtStop">
      <s:textfield name="tempsDecalage" label="%{getText('horairesDePassage.tempsDecalage')}" value="00:00"/>
      <s:textfield name="nbreCourseDecalage" label="%{getText('horairesDePassage.nbreCourseDecalage')}" value="1"/>
      <s:hidden name="idCourseADecaler" id="idCourseADecaler"/>
      <s:hidden name="idItineraire" value="%{idItineraire}"/>
      <s:hidden name="idLigne" value="%{idLigne}"/>
      <s:hidden name="idTableauMarche" value="%{idTableauMarche}" />
      <s:hidden name="seuilHeureDepartCourse" value="%{seuilHeureDepartCourse}" />
      <s:hidden name="page" value="%{page}"/>
      <tr>
        <td colspan="2">
          <s:submit value="%{getText('action.validate')}" action="ajoutCourseAvecDecalageTemps" theme="simple" cssClass="right" onclick="cacherDecalageTemps()"/>
          <s:submit value="%{getText('action.cancel')}" action="cancel" theme="simple" cssClass="right" onclick="cacherDecalageTemps()"/>
        </td>
      </tr>
    </s:form>
  </div>
  <script type="text/javascript" >
    cacherBloqueDecalageTemps();
  </script>

  <%-- TABLEAU HORAIRES --%>
  <div>
    <s:form theme="simple" id="updateForm" method="POST">
      <s:hidden name="idItineraire" value="%{idItineraire}" />
      <s:hidden name="idLigne" value="%{idLigne}" />
      <s:hidden name="idTableauMarche" value="%{idTableauMarche}" />
      <s:hidden name="seuilHeureDepartCourse" value="%{seuilHeureDepartCourse}" />
      <s:hidden name="page" value="%{page}" />
      <div ALIGN="center"></div>
      <br/>
      <div>
        <div id="displaytag">
          <s:include value="/jsp/vehicleJourneyAtStop/listBody.jsp" />
        </div>
        <br/>
        <div ALIGN="center">
          <s:include value="/jsp/vehicleJourneyAtStop/listPagination.jsp" />
        </div>
      </div>
    </s:form>
  </div>

</s:if>