<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="/struts-tags" prefix="s"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>
<%-- Titre et barre de navigation --%>	
<title><s:text name="text.itineraire.list.title" /></title>
<s:url id="urlItineraires" action="list" namespace="/route" includeParams="none">
  <s:param name="idLigne" value="%{idLigne}" />
</s:url>
<s:property value="filAriane.addElementFilAriane(getText('text.itineraire.list.title'), lineName, #urlItineraires)"/>
<div class="panelData">
  <s:property value="filAriane.texteFilAriane" escape="false"/>
</div>

<s:include value="/jsp/commun/messages.jsp" />

<%-- Ajouter itinéraire --%>
<div>
  <s:url id="editItineraire" action="add" namespace="/route" includeParams="none">
    <s:param name="idLigne" value="%{idLigne}" />
  </s:url>
  <s:a href="%{editItineraire}"><b><s:text name="text.itineraire.create.button"/></b></s:a>
  <br><br>
   
<%--   <FIELDSET style="width: 500px;">
    <LEGEND><b><s:text name="title.import.vehicleJourneyAtStop"/></b></LEGEND>
    <s:form id="uploadCSVForm" action="importHorairesItineraire" namespace="/upload" enctype="multipart/form-data" method="POST">
      <s:file label="%{getText('text.route.file')}" name="fichier" accept="text/csv"/>
      <s:hidden name="fichierContentType" value="text/csv; charset=UTF-8"/>
      <s:hidden name="idLigne" value="%{idLigne}"/>
      <s:hidden name="operationMode" value="%{'STORE'}" />
      <s:submit value="%{getText('action.import.csv')}" formId="uploadCSVForm"/>
    </s:form>
  </FIELDSET>
 --%>   
</div>
<br>
<%-- Tableau --%>
<div id="displaytag"> 
  <TABLE>
    <THEAD>
      <TR>
        <TH><s:text name="table.itineraires.actions"/> </TH>
        <TH><s:text name="table.itineraires.nom"/></TH>
        <TH><s:text name="table.itineraires.nomPublie"/></TH>
        <TH><s:text name="table.itineraires.sens"/></TH>
        <TH><s:text name="table.itineraires.horairesDePassage"/></TH>
        <TH><s:text name="table.itineraires.arrets"/></TH>
        <%-- <TH><s:text name="table.itineraires.export"/></TH> --%>
      </TR>
    </THEAD>
    <TBODY>
      <s:iterator value="#request.itineraires" var="itineraire" status="rangItineraire">
        <TR class="<s:if test="#rangItineraire.odd == true ">odd</s:if><s:else>even</s:else>">
          <TD class="<s:if test='(wayBack == "R") || (oppositeRouteId == null)'>itineraire</s:if>">
            <s:url id="editUrl" action="edit"  namespace="/route">
              <s:param name="idItineraire" value="id" />
              <s:param name="idLigne" value="idLigne" />
            </s:url>
            <s:a href="%{editUrl}">
              <img border="0" alt="Edit" src="<s:url value='/images/editer.png'/>" title="<s:text name="tooltip.edit"/>">
            </s:a>&nbsp;&nbsp;
            <s:url id="removeUrl" action="delete" namespace="/route">
              <s:param name="idItineraire" value="id" />
              <s:param name="idLigne" value="idLigne"/>
              <s:param name="operationMode">STORE</s:param>
            </s:url>
            <s:a href="%{removeUrl}" onclick="return confirm('%{getText('itineraire.delete.confirmation')}');">
              <img border="0" alt="Delete" src="<s:url value='/images/supprimer.png'/>" title="<s:text name="tooltip.delete"/>">
            </s:a>
          </TD>
          <TD class="<s:if test='(wayBack == "R") || (oppositeRouteId == null)'>itineraire</s:if>">
            <s:property value="name"/>
          </TD>
          <TD class="<s:if test='(wayBack == "R") || (oppositeRouteId == null)'>itineraire</s:if>">
            <s:property value="publishedName"/>
          </TD>
          <TD class="<s:if test='(wayBack == "R") || (oppositeRouteId == null)'>itineraire</s:if>">
            <s:if test="#itineraire.direction != null">
              <s:text name="%{#itineraire.direction}"/>
            </s:if>
            <s:else>
              <s:text name="no.direction"/>
            </s:else>
          </TD>
          <TD class="<s:if test='(wayBack == "R") || (oppositeRouteId == null)'>itineraire</s:if>">
            <s:if test="!isArretsVide(id)">
              <s:url id="horairesDePassage" action="list" namespace="/vehicleJourneyAtStop">
                <s:param name="idItineraire" value="id" />
                <s:param name="idLigne" value="idLigne" />
                <s:param name="page" value="1" />
              </s:url>
              <s:a href="%{horairesDePassage}"><s:text name="title.vehicleJourneyAtStop"/></s:a>
            </s:if>
          </TD>
          <TD class="<s:if test='(wayBack == "R") || (oppositeRouteId == null)'>itineraire</s:if>">
            <s:url id="arretSurItineraire" action="list" namespace="/stoppointOnRoute">
              <s:param name="idItineraire" value="%{id}" />
              <s:param name="idLigne" value="idLigne" />
            </s:url>
            <s:a href="%{arretSurItineraire}"><s:text name="title.stoppointOnRoute"/></s:a>
          </TD>
<%--           <TD class="<s:if test='(wayBack == "R") || (oppositeRouteId == null)'>itineraire</s:if>">
            <s:url id="exportHorairesItineraire" action="exportHorairesItineraire" namespace="/export">
              <s:param name="idItineraire" value="id"/>
              <s:param name="idLigne" value="idLigne" />
              <s:param name="origin" value="itinerary"/>
            </s:url>
            <s:a href="%{exportHorairesItineraire}"><s:text name="text.export.vehicleJourneyAtStop"/></s:a>
          </TD> --%>
        </TR>
      </s:iterator>
    </TBODY>
  </TABLE>
</div>