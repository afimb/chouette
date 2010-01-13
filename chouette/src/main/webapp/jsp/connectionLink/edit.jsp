<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>
<s:include value="/jsp/commun/scriptaculous.jsp" />


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
<br>	
<div class="panelDataSection"><s:text name="connectionlink"/></div>
<div class="panel">
  <s:form id="connectionLinkForm"  namespace="/connectionLink">
    <s:hidden name="idCorrespondance" value="%{id}"/>
    <s:hidden name="operationMode" value="STORE" />
    <s:hidden key="actionMethod" value="%{actionMethod}"/>
    <s:textfield key="name" required="true"/>
    <s:textfield key="comment" required="false"/>
    <s:textfield maxlength="5" key="defaultDuration" value="%{strutsDefaultDuration}" required="false"/>
    <s:textfield maxlength="5" key="mobilityRestrictedTravellerDuration" value="%{strutsMobilityRestrictedTravellerDuration}" required="false"/>
    <s:textfield maxlength="5" key="occasionalTravellerDuration" value="%{strutsOccasionalTravellerDuration}" required="false"/>
    <s:textfield maxlength="5" key="frequentTravellerDuration" value="%{strutsFrequentTravellerDuration}" required="false"/>
    <s:select emptyOption="false" key="linkType" list="connectionLinkTypeEnum" listKey="enumeratedTypeAccess" listValue="textePropriete"/>
    <s:select emptyOption="false" key="liftAvailability" list="#@java.util.HashMap@{'true':getText('text.yes'), 'false':getText('text.no')}" value="liftAvailability"/>
    <s:select emptyOption="false" key="mobilityRestrictedSuitability" list="#@java.util.HashMap@{'true':getText('text.yes'), 'false':getText('text.no')}" value="mobilityRestrictedSuitability"/>
    <s:select emptyOption="false" key="stairsAvailability"list="#@java.util.HashMap@{'true':getText('text.yes'), 'false':getText('text.no')}" value="stairsAvailability"/>
    <s:textfield key="linkDistance" />

    <%-- Actions --%>
    <tr>
      <td colspan="2">
        <s:if test="id != null">
          <s:submit key="action.update" action="%{actionMethod}"  theme="simple" cssStyle="float: right;"  validate="true"/>
        </s:if>
        <s:else>
          <s:submit key="action.create" action="%{actionMethod}" theme="simple" cssStyle="float: right;"  validate="true"/>
        </s:else>
        <s:submit key="action.cancel" action="cancel" theme="simple" cssStyle="float: right;"/>
      </td>
    </tr>

    <%-- Ajout des balises tr et td pour le faire apparaitre dans le tableau --%>
    <tr><td colspan="2"><s:include value="/jsp/commun/asterisque.jsp" /></td></tr>
  </s:form>

</div>

<%-- Zones depart --%>
<div class="panelDataSection"><s:text name="text.connectionlink.departure"/></div>
<div class="panel" id="displaytag"> 
  <s:if test="id != null">
    <table>
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
              <s:url id="editUrl" action="edit" namespace="/stoparea">
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
            <s:text name="%{#attr.start.areaType}"/>
          </td>
        </tr>
      </tbody>
    </table>
  </s:if>
</div>	

<%-- Zones arrivée --%>
<div class="panelDataSection"><s:text name="text.connectionlink.arrival"/></div>
<div class="panel" id="displaytag">
  <s:if test="id != null">
    <table>
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
              <s:url id="editUrl" action="edit" namespace="/stoparea">
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
            <s:text name="%{#attr.end.areaType}"/>
          </td>
        </tr>
      </tbody>
    </table>
  </s:if>

</div>		