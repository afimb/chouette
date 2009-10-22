<%@ taglib uri="/struts-tags" prefix="s"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>
<title>Les itineraires</title>
<s:url action="crud_Course!edit" id="editCourse" includeParams="none">
	<s:param name="idItineraire" value="%{idItineraire}" />
</s:url>
<button onclick="location.href='<s:property value="editCourse"/>'">Ajouter une Course</button>
<br><br>
<div id="displaytag"> 
	<display:table name="courses" sort="list" pagesize="5" requestURI="" id="course" export="false">
	  	<display:column title="Action" sortable="false">
			<s:url id="removeUrl" action="crud_Course!delete">
				<s:param name="idCourse" value="${course.id}" />
			</s:url>
			<s:url id="editUrl" action="crud_Course!edit">
				<s:param name="idCourse" value="${course.id}" />
				<s:param name="idItineraire" value="${course.idItineraire}" />
			</s:url>
			<s:a href="%{editUrl}"><img border="0" src="images/editer.png" title="<s:text name="tooltip.edit"/>"></s:a>&nbsp;&nbsp;
	    	<s:a href="%{removeUrl}" onclick="return confirm('%{getText('popup.confirmer')}');"><img border="0" src="images/supprimer.png" title="<s:text name="tooltip.delete"/>"></s:a> 	
	  	</display:column>	
	  	<display:column title="Nom" property="vehicleJourney.publishedJourneyName" sortable="true" headerClass="sortable"/>
	  	<display:column title="Indice" property="vehicleJourney.publishedJourneyIdentifier" sortable="true" headerClass="sortable"/>
	</display:table>
</div>