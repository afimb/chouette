<%@ taglib prefix="s" uri="/struts-tags" %>

<%-- COURSES --%>

<tr>
  <td style="font-weight:bold">Courses&nbsp;&nbsp;</td>
  <s:iterator value="coursesPage" >
    <td>
      <s:url id="editCourse" action="crud_Course!edit">
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
      <s:url id="supprimerCourse" action="supprimer_Course_sur_son_itineraire">
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
      <s:a href="%{editCourse}">
        <s:if test="publishedJourneyName == null || publishedJourneyName == ''">
          <s:text name="horairesDePassage.anonyme"/>
        </s:if>
        <s:else>
          <s:property value="publishedJourneyName" />
        </s:else>
      </s:a>&nbsp;
      <s:a href="#" onclick="afficherBloqueDecalageTemps(%{id})">
        <img border="0" alt="Add" src="images/ajouter.png" title="<s:text name="tooltip.create_by_translation"/>">
      </s:a>&nbsp;
      <s:a href="%{supprimerCourse}" preInvokeJS="confirm('%{getText('course.delete.confirmation')}');">
        <img border="0" alt="Delete" src="images/supprimer.png" title="<s:text name="tooltip.delete"/>">
      </s:a>
      <br>
    </td>
  </s:iterator>
</tr>


<%-- MISSIONS --%>
<tr>
  <td style="font-weight:bold">Missions&nbsp;&nbsp;</td>
  <s:iterator value="coursesPage" >
    <td>
      <s:url id="editMission" action="/mission/edit">
        <s:param name="idMission" value="idMission" />
        <s:param name="idLigne" value="idLigne" />
        <s:param name="idItineraire" value="idItineraire" />
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
  <td style="font-weight:bold">Calendriers&nbsp;&nbsp;</td>
  <s:iterator value="coursesPage" >
    <td>
      <s:subset source="tableauxMarcheParIdCourse[id]" count="maxNbCalendriersParCourse">
        <s:iterator>
					(<s:property value="referenceTableauMarcheParIdTableauMarche[id]" />)&nbsp;
        </s:iterator>
      </s:subset>
      <s:if test="tableauxMarcheParIdCourse[id].size > maxNbCalendriersParCourse">(...)</s:if>
    </td>
  </s:iterator>
</tr>

<%-- ARRETS --%>
<tr>
  <td style="font-weight:bold">Arr&ecirc;ts</td>
  <s:iterator value="coursesPage" >
    <td>&nbsp;</td>
  </s:iterator>
</tr>