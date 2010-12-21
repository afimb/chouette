<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="/struts-tags" prefix="s"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>
<s:include value="/jsp/commun/mapConnectionLinkJavascript.jsp" />
<script language="JavaScript" type="text/javascript" src="<s:url value='/js/showMap.js' includeParams='none'/>" ></script>

<%-- Titre et barre de navigation --%>
<s:url id="urlCorrespondanceUpdate" action="edit" namespace="/connectionLink">
  <s:param name="idCorrespondance" value="%{id}"/>
</s:url>
<s:if test="id != null">
  <title><s:text name="text.connectionlink.update.title" /></title>
  <s:property value="filAriane.addElementFilAriane(getText('text.connectionlink.update.title'), '', #urlCorrespondanceUpdate)"/>
</s:if>
<s:else>
  <title><s:text name="text.connectionlink.create.title" /></title>
  <s:property value="filAriane.addElementFilAriane(getText('text.connectionlink.create.title'), '', #urlCorrespondanceUpdate)"/>
</s:else>
<div class="panelData">
  <s:property value="filAriane.texteFilAriane" escape="false"/>
</div>

<s:include value="/jsp/commun/messages.jsp" />

<div class="panelDataSection"><s:text name="connectionlink"/></div>
<div class="panel">
  <div class="editConnectionLink">
    <s:form theme="css_xhtml" id="connectionLink"  namespace="/connectionLink">
      <s:hidden name="idCorrespondance" value="%{id}"/>
      <s:hidden name="operationMode" value="STORE" />
      <s:hidden key="actionMethod" value="%{actionMethod}"/>
      <s:textfield key="name" required="true"/>
      <s:textfield key="comment" required="false"/>
      <s:textfield name="strutsDefaultDuration" value="%{strutsDefaultDuration}" label="%{getText('defaultDuration')}" />
      <s:textfield name="strutsMobilityRestrictedTravellerDuration" value="%{strutsMobilityRestrictedTravellerDuration}" label="%{getText('mobilityRestrictedTravellerDuration')}"/>
      <s:textfield name="strutsOccasionalTravellerDuration" value="%{strutsOccasionalTravellerDuration}" label="%{getText('occasionalTravellerDuration')}"/>
      <s:textfield name="strutsFrequentTravellerDuration" value="%{strutsFrequentTravellerDuration}" label="%{getText('frequentTravellerDuration')}"/>
      <s:select emptyOption="false" key="linkType" list="connectionLinkTypeEnum" listKey="enumeratedTypeAccess" listValue="textePropriete"/>
      <s:select emptyOption="false" key="liftAvailability" list="#@java.util.HashMap@{'true':getText('text.yes'), 'false':getText('text.no')}" value="liftAvailability"/>
      <s:select emptyOption="false" key="mobilityRestrictedSuitability" list="#@java.util.HashMap@{'true':getText('text.yes'), 'false':getText('text.no')}" value="mobilityRestrictedSuitability"/>
      <s:select emptyOption="false" key="stairsAvailability"list="#@java.util.HashMap@{'true':getText('text.yes'), 'false':getText('text.no')}" value="stairsAvailability"/>
      <s:textfield key="linkDistance" />

      <%-- Ajout des balises tr et td pour le faire apparaitre dans le tableau --%>
      <s:include value="/jsp/commun/asterisque.jsp" />
      <%-- Actions --%>
      <div class="submit">
        <s:if test="id != null">
          <s:submit key="action.update" action="%{actionMethod}"  theme="simple" cssClass="right"/>
        </s:if>
        <s:else>
          <s:submit key="action.create" action="%{actionMethod}" theme="simple" cssClass="right"/>
        </s:else>
        <s:submit key="action.cancel" action="cancel" theme="simple" cssClass="right"/>
      </div>
    </s:form>
  </div>
  <div class="map-wrapper">
    <div id="map-view" onclick="Chouette.Map.showMap()"><div id="map-view-text"></div></div>
    <div id="map"></div>
  </div>
</div>

<%-- Zones depart --%>
<s:if test="id != null">
  <div class="panelDataSection"><s:text name="text.connectionlink.departure"/></div>
  <div class="panel" id="displaytag">
    <table id="departure">
      <thead>
        <tr>
          <td><s:text name="action.title" /></td>
          <td><s:text name="name" /></td>
          <td><s:text name="areaType" /></td>
        </tr>
      </thead>
      <tbody>
        <tr>
          <td>
            <s:if test="idDepart != null">
              <s:url id="editUrl" action="edit" namespace="/stopPlace">
                <s:param name="idPositionGeographique" value="%{idDepart}" />
              </s:url>
              <s:a href="%{editUrl}">
                <img border="0" alt="Edit" src="<s:url value='/images/editer.png'/>" title="<s:text name="tooltip.edit"/>">&nbsp;
              </s:a>
            </s:if>

            <s:url id="createReplaceUrl" action="search" namespace="/connectionLink">
              <s:param name="idCorrespondance" value="%{id}" />
              <s:param name="actionSuivante" value="%{'addStart'}" />
            </s:url>
            <s:a href="%{createReplaceUrl}">
              <s:if test="idDepart != null">
                <s:text name="action.replace" />
              </s:if>
              <s:else>
                <s:text name="action.add" />
              </s:else>
            </s:a>
          </td>
          <td><s:property value="start.name" /></td>
          <td>
            <s:text name="%{start.areaType}"/>
          </td>
        </tr>
      </tbody>
    </table>
  </div>
</s:if>


<%-- Zones arrivée --%>
<s:if test="id != null">
  <div class="panelDataSection"><s:text name="text.connectionlink.arrival"/></div>
  <div class="panel" id="displaytag">
    <table id="arrival">
      <thead>
        <tr>
          <td><s:text name="action.title" /></td>
          <td><s:text name="name" /></td>
          <td><s:text name="areaType" /></td>
        </tr>
      </thead>
      <tbody>
        <tr>
          <td>
            <s:if test="idArrivee != null">
              <s:url id="editUrl" action="edit" namespace="/stopPlace">
                <s:param name="idPositionGeographique" value="%{idArrivee}" />
              </s:url>
              <s:a href="%{editUrl}">
                <img border="0" alt="Edit" src="<s:url value='/images/editer.png'/>" title="<s:text name="tooltip.edit"/>">&nbsp;
              </s:a>
            </s:if>

            <s:url id="createReplaceUrl" action="search" namespace="/connectionLink">
              <s:param name="idCorrespondance" value="%{id}" />
              <s:param name="actionSuivante" value="%{'addEnd'}" />
            </s:url>
            <s:a href="%{createReplaceUrl}">
              <s:if test="idArrivee != null">
                <s:text name="action.replace" />
              </s:if>
              <s:else>
                <s:text name="action.add" />
              </s:else>
            </s:a>
          </td>
          <td><s:property value="end.name" /></td>
          <td>
            <s:text name="%{end.areaType}"/>
          </td>
        </tr>
      </tbody>
    </table>
  </div>
</s:if>


