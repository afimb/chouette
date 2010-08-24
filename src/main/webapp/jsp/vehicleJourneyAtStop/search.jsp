<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<title><s:text name="text.horairesDePassage.recherche.title"/></title>
<s:url id="urlHorairesDePassage" action="search" namespace="/vehicleJourneyAtStop" includeParams="none"/>
<s:property value="filAriane.addElementFilAriane(getText('text.horairesDePassage.recherche.title'), '', #urlHorairesDePassage)"/>
<div class="panelData">
  <s:property value="filAriane.texteFilAriane" escape="false"/>
</div>

<s:include value="/jsp/commun/messages.jsp" />

<s:text name="text.horairesDePassage.recherche.indication"/>