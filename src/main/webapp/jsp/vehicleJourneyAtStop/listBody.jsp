<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>

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


<tr>
  <td colspan="${maxNbCoursesParPage}">
    <s:if test="erreurHorairesInvalides == true">
      <s:submit action="editerHorairesCoursesConfirmation" value="%{getText('action.confirmUpdate')}" theme="simple" cssStyle="float:right; width:200px"/>
    </s:if>
    <s:else>
      <s:submit action="editerHorairesCourses" value="%{getText('action.update')}" theme="simple" cssStyle="float:right"/>
    </s:else>
    <s:submit action="cancel" value="%{getText('action.cancel')}" theme="simple" cssStyle="float:right"/>
  </td>
</tr>