<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<table>
  <thead>
    <%-- COURSES --%>
    <tr>
      <td style="font-weight:bold"><s:text name="table.title.vehicleJourney"/>&nbsp;&nbsp;</td>
      <s:iterator value="coursesPage" >
        <td>
          <s:url id="editCourse" action="edit" namespace="/vehicleJourney">
            <s:param name="idCourse" value="id" />
            <s:param name="idItineraire" value="idItineraire" />
            <s:param name="idLigne" value="idLigne" />
            <s:param name="idTableauMarche" value="idTableauMarche" />
            <s:param name="seuilDateDepartCourse">
              <s:if test="seuilDateDepartCourse != null">
                <s:date name="seuilDateDepartCourse" format="HH:mm"/>
              </s:if>
            </s:param>
            <s:param name="page" value="page" />
          </s:url>
          <s:url id="supprimerCourse" action="delete" namespace="/vehicleJourney">
            <s:param name="idCourse" value="id" />
            <s:param name="idItineraire" value="idItineraire" />
            <s:param name="idLigne" value="idLigne" />
            <s:param name="idTableauMarche" value="idTableauMarche" />
            <s:param name="seuilDateDepartCourse">
              <s:if test="seuilDateDepartCourse != null">
                <s:date name="seuilDateDepartCourse" format="HH:mm"/>
              </s:if>
            </s:param>
            <s:param name="operationMode" value="%{'STORE'}" />
            <s:param name="page" value="page" />
          </s:url>
          <s:a href="%{editCourse}">
            <s:if test="publishedJourneyName == null || publishedJourneyName == ''">
              <s:text name="horairesDePassage.anonyme"/>
            </s:if>
            <s:else>
              <s:property value="publishedJourneyName" />
            </s:else>
          </s:a>&nbsp;
          <s:a href="#" onclick="afficherBloqueDecalageTemps(%{id})">
            <img border="0" alt="Add" src="<s:url value='/images/ajouter.png'/>" title="<s:text name='tooltip.create'/>">
          </s:a>&nbsp;
          <s:a href="%{supprimerCourse}" preInvokeJS="confirm('%{getText('course.delete.confirmation')}');">
            <img border="0" alt="Delete" src="<s:url value='/images/supprimer.png'/>" title="<s:text name='tooltip.delete'/>">
          </s:a>
          <br>
        </td>
      </s:iterator>
    </tr>


    <%-- PARTICULARITES --%>
    <tr>
      <td style="font-weight:bold"><s:text name="table.title.peculiarities"/>&nbsp;&nbsp;</td>
      <s:iterator value="coursesPage" >
        <td>
          <s:if test="vehicleTypeIdentifier != null">
            <s:text name="vehicleTypeIdentifier"/>
          </s:if>
        </td>
      </s:iterator>
    </tr>

    <%-- MISSIONS --%>
    <tr>
      <td style="font-weight:bold"><s:text name="table.title.journeyPattern"/>&nbsp;&nbsp;</td>
      <s:iterator value="coursesPage" >
        <td>
          <s:url id="editMission" action="edit" namespace="/journeyPattern">
            <s:param name="idMission" value="idMission" />
            <s:param name="idLigne" value="idLigne" />
            <s:param name="idItineraire" value="idItineraire" />
            <s:param name="page" value="page" />
          </s:url>
          <s:a href="%{editMission}">
            <s:if test="missionParIdCourse[idMission].name == null || missionParIdCourse[idMission].name == ''">
              <s:text name="horairesDePassage.anonyme" />
            </s:if>
            <s:else>
              <s:property value="missionParIdCourse[idMission].name" />
            </s:else>
          </s:a>
        </td>
      </s:iterator>
    </tr>

    <%-- CALENDRIERS --%>
    <tr>
      <td style="font-weight:bold"><s:text name="table.title.vehicleJourneyAtStop"/>&nbsp;&nbsp;</td>
      <s:iterator value="coursesPage" var="vehicleJourney">
        <td>
          <%--  count="maxNbCalendriersParCourse" --%>
          <s:subset source="tableauxMarcheParIdCourse[#vehicleJourney.id]" var="test">
            <s:iterator>
              (<s:property/>)&nbsp;
            </s:iterator>
          </s:subset>
          <s:if test="tableauxMarcheParIdCourse[id].size > maxNbCalendriersParCourse">(...)</s:if>
        </td>
      </s:iterator>
    </tr>

    <%-- ARRETS --%>
    <tr>
      <td style="font-weight:bold"><s:text name="table.title.stoppointOnRoute"/></td>
      <s:iterator value="coursesPage" >
        <td>&nbsp;</td>
      </s:iterator>
    </tr>
  </thead>
  <tbody>
    <s:iterator id="arretItineraire" value="arretsItineraire" status="statutArret">
      <tr class="<s:if test="#statutArret.odd == true ">odd</s:if><s:else>even</s:else>">
        <%-- NOM ARRET --%>
        <td>
          <s:url action="edit" namespace="/boardingPosition" id="arretPhysique">
            <s:param name="idPositionGeographique" value="arretPhysiqueParIdArret[id].id" />
          </s:url>

          <s:a href="%{arretPhysique}" id="nomArretPhysique%{#statutArret.index}">
            <s:if test="arretPhysiqueParIdArret[id].name == null || arretPhysiqueParIdArret[id].name == ''">
              <s:text name="horairesDePassage.anonyme" />
            </s:if>
            <s:else>
              <s:property value="arretPhysiqueParIdArret[id].name" />
            </s:else>
          </s:a>
        </td>

        <%-- HEURES DEPART COURSES --%>
        <s:iterator id="horaire" value="heuresCourses" status="statutHoraire" >
          <s:set name="idx" value="%{(arretsItineraire.size * #statutHoraire.index) + #statutArret.index}"></s:set>
          <s:if test="%{#idx < heuresCourses.size}">
            <s:if test="idsHorairesInvalides.contains(#idx)">
              <s:set name="heureCourseBackgroundColor" value="'red'" />
            </s:if>
            <s:else>
              <s:set name="heureCourseBackgroundColor" value="''" />
            </s:else>
            <td>
              <s:textfield
                name="heuresCourses[%{#idx}]"
                id="heuresCourses%{#idx}"
                onfocus="onHoraireClicked(this)"
                onblur="onHoraireBlurred(this)"
                onchange="calculateTime(heuresCourses%{#idx})"
                value="%{heuresCourses[#idx]}"
                cssStyle="background-color:%{#heureCourseBackgroundColor}" />
            </td>
          </s:if>
        </s:iterator>
      </tr>
    </s:iterator>
  </tbody>
</table>

<s:if test="erreurHorairesInvalides == true">
  <s:submit action="editerHorairesCoursesConfirmation" value="%{getText('action.confirmUpdate')}" theme="simple" cssStyle="float:right; width:200px"/>
</s:if>
<s:else>
  <s:submit action="editerHorairesCourses" value="%{getText('action.update')}" theme="simple" cssStyle="float:right"/>
</s:else>
<s:submit action="cancel" value="%{getText('action.cancel')}" theme="simple" cssStyle="float:right"/>