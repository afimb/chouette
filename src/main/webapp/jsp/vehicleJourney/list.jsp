<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="/struts-tags" prefix="s"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>
<title><s:text name="title.vehicleJourney"/></title>
<s:url action="/course/add" id="editCourse" includeParams="none">
  <s:param name="idItineraire" value="%{idItineraire}" />
</s:url>

<%-- Ajouter une course --%>
<div class="actions">
  <s:url action="add" namespace="/vehicleJourney" id="createCourse"/>
	<s:a href="%{createCourse}"><b><s:text name="text.course.create.button"/></b></s:a>
</div>
<br>
<%-- Tableau de courses --%>
<div id="displaytag"> 
  <display:table name="courses" sort="list" pagesize="5" requestURI="" id="course" export="false">
    <display:column titleKey="table.title.action" sortable="false">
      <s:url id="removeUrl" action="/course/delete">
        <s:param name="idCourse">${id}</s:param>
      </s:url>
      <s:url id="editUrl" action="/course/edit">
        <s:param name="idCourse">${id}</s:param>
        <s:param name="idItineraire">${idItineraire}</s:param>
      </s:url>
      <s:a href="%{editUrl}">
        <img border="0" alt="Edit" src="<s:url value='/images/editer.png'/>" title="<s:text name="tooltip.edit"/>">
      </s:a>&nbsp;&nbsp;
      <s:a href="%{removeUrl}" onclick="return confirm('%{getText('popup.confirmer')}');">
        <img border="0" alt="Delete" src="<s:url value='/images/supprimer.png'/>" title="<s:text name="tooltip.delete"/>">
      </s:a>
    </display:column>
    <display:column titleKey="table.title.name" property="vehicleJourney.publishedJourneyName" sortable="true" headerClass="sortable"/>
    <display:column titleKey="table.title.index" property="vehicleJourney.publishedJourneyIdentifier" sortable="true" headerClass="sortable"/>
  </display:table>
</div>