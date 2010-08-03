<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags" %>

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