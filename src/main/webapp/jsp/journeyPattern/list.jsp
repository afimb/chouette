<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="/struts-tags" prefix="s" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>
<title><s:text name="title.journeyPattern"/></title>

<br><br>
<div id="displaytag"> 
  <display:table name="missions" sort="list" pagesize="20" requestURI="" id="mission" export="false">
    <display:column titleKey="table.title.action" sortable="false">
      <s:url id="editMission" action="edit" namespace="/journeyPattern">
        <s:param name="idMission">${mission.id}</s:param>
      </s:url>
      <s:a href="%{editMission}"><img border="0" alt="Edit" src="<s:url value='/images/editer.png'/>" title="<s:text name="tooltip.edit"/>"></s:a>&nbsp;&nbsp;
    </display:column>
    <display:column titleKey="table.title.name" property="name" sortable="true" headerClass="sortable"/>
  </display:table>
</div>