<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>
<s:include value="/jsp/commun/scriptaculous.jsp" />

<%-- Titre et barre de navigation --%>
<s:url id="urlCorrespondanceUpdate" action="edit" namespace="/correspondance">
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
  <s:form id="connectionLinkForm"  namespace="/correspondance">
    <s:hidden name="idCorrespondance" value="%{id}"/>
    <s:hidden name="operationMode" value="STORE" />
    <s:hidden key="actionMethod" value="%{actionMethod}"/>
    <s:textfield key="connectionlink.name" name="name" required="true"/>
    <s:textfield key="connectionlink.comment" name="comment" required="false"/>
    <s:textfield maxlength="5" key="connectionlink.defaultDuration" name="defaultDuration" value="%{strutsDefaultDuration}" required="false"/>
    <s:textfield maxlength="5" key="connectionlink.mobilityRestrictedTravellerDuration" name="mobilityRestrictedTravellerDuration" value="%{strutsMobilityRestrictedTravellerDuration}" required="false"/>
    <s:textfield maxlength="5" key="connectionlink.occasionalTravellerDuration" name="occasionalTravellerDuration" value="%{strutsOccasionalTravellerDuration}" required="false"/>
    <s:textfield maxlength="5" key="connectionlink.frequentTravellerDuration" name="frequentTravellerDuration" value="%{strutsFrequentTravellerDuration}" required="false"/>
    <s:select emptyOption="false" key="connectionlink.linkType" list="@fr.certu.chouette.struts.enumeration.EnumerationApplication@getConnectionLinkTypeEnum()" listKey="enumeratedTypeAccess" listValue="textePropriete"/>
    <s:select emptyOption="false" key="connectionlink.liftAvailability" name="liftAvailability" list="#@java.util.HashMap@{'true':'Oui', 'false':'Non'}" value="liftAvailability"/>
    <s:select emptyOption="false" key="connectionlink.mobilityRestrictedSuitability" name="mobilityRestrictedSuitability" list="#@java.util.HashMap@{'true':'Oui', 'false':'Non'}" value="mobilityRestrictedSuitability"/>
    <s:select emptyOption="false" key="connectionlink.stairsAvailability" name="stairsAvailability" list="#@java.util.HashMap@{'true':'Oui', 'false':'Non'}" value="stairsAvailability"/>
    <s:textfield key="connectionlink.linkDistance" name="linkDistance" />

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
<div class="panelDataSection"><s:text name="Départ"/></div>
<div class="panel" id="displaytag"> 
  <s:if test="id != null">
    <table>
      <thead>
        <tr>
          <td><s:text name="action.title" /></td>
          <td><s:text name="positionGeographique.name" /></td>
          <td><s:text name="positionGeographique.areaType" /></td>
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
                <img border="0" alt="Edit" src="/images/editer.png" title="<s:text name="tooltip.edit"/>">&nbsp;
              </s:a>
            </s:if>

            <s:url id="createReplaceUrl" action="search" namespace="/correspondance">
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
<div class="panelDataSection"><s:text name="Arrivée"/></div>	
<div class="panel" id="displaytag">
  <s:if test="id != null">
    <table>
      <thead>
        <tr>
          <td><s:text name="action.title" /></td>
          <td><s:text name="positionGeographique.name" /></td>
          <td><s:text name="positionGeographique.areaType" /></td>
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
                <img border="0" alt="Edit" src="/images/editer.png" title="<s:text name="tooltip.edit"/>">&nbsp;
              </s:a>
            </s:if>

            <s:url id="createReplaceUrl" action="search" namespace="/correspondance">
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