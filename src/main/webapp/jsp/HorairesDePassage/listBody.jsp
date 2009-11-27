<%@ taglib prefix="s" uri="/struts-tags"%>

<s:iterator id="arretItineraire" value="arretsItineraire" status="statutArret">
  <s:if test="#statutArret.index % 2 == 0" >
    <s:set name="rowClass" value="'odd'" />
  </s:if>
  <s:else>
    <s:set name="rowClass" value="'even'" />
  </s:else>

  <tr class="<s:property value="rowClass" />">
    <%-- NOM ARRET --%>
    <td>
      <s:url value="/stoppoint/edit" id="arretPhysique">
        <s:param name="idPositionGeographique" value="arretPhysiqueParIdArret[id].id" />
        <s:param name="typePositionGeographique" value="%{'arretPhysique'}" />
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


<tr>
  <td colspan="${maxNbCoursesParPage}">
    <s:if test="erreurHorairesInvalides == true">
      <s:submit action="HorairesDePassage_editerHorairesCoursesConfirmation" value="%{getText('action.confirmUpdate')}" theme="simple" cssStyle="float:right; width:200px"/>
    </s:if>
    <s:else>
      <s:submit action="HorairesDePassage_editerHorairesCourses" value="%{getText('action.update')}" theme="simple" cssStyle="float:right"/>
    </s:else>
    <s:submit action="HorairesDePassage_cancel" value="%{getText('action.cancel')}" theme="simple" cssStyle="float:right"/>
  </td>
</tr>