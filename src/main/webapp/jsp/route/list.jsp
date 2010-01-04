<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="/struts-tags" prefix="s"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>
<%-- Titre et barre de navigation --%>	
<title><s:text name="text.itineraire.list.title" /></title>
<s:url id="urlItineraires" action="list" namespace="/route" includeParams="none">
  <s:param name="idLigne" value="%{idLigne}" />
</s:url>
<s:property value="filAriane.addElementFilAriane(getText('text.itineraire.list.title'), ligne.name, #urlItineraires)"/>
<div class="panelData">
  <s:property value="filAriane.texteFilAriane" escape="false"/>
</div>
<br>
<%-- Ajouter itinéraire --%>
<div>
  <s:url id="editItineraire" action="add" namespace="/route" includeParams="none">
    <s:param name="idLigne" value="%{idLigne}" />
  </s:url>
  <s:a href="%{editItineraire}"><b><s:text name="text.itineraire.create.button"/></b></s:a>
  <br><br>
  <FIELDSET align="center" style="width: 500px;">
    <LEGEND><b><s:text name="title.import.vehicleJourneyAtStop"/></b></LEGEND>
    <s:form id="uploadCSVForm" action="importHorairesItineraire" namespace="/import" enctype="multipart/form-data" method="POST">
      <s:file name="fichier" label="%{getText('action.browse')}"/>
      <s:hidden name="fichierContentType" value="text/csv; charset=UTF-8"/>
      <s:hidden name="idLigne" value="%{idLigne}"/>
      <s:submit value="Import fichier CSV" formId="uploadCSVForm"/>
    </s:form>
  </FIELDSET>
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
        <TH>exports</TH>
      </TR>
    </THEAD>
    <TBODY>
      <s:iterator value="#request.itineraires" var="itineraire" status="rangItineraire">
        <%
    String cssBordure = "";
    String TRParityClass = "";
        %>
        <s:if test='(wayBack == "R") || (idRetour == null)'>
          <%
    cssBordure = "itineraire";
          %>
        </s:if>
        <%
    pageContext.setAttribute("cssBordure", cssBordure);
        %>

        <s:if test="#rangItineraire.odd == true">
          <%
    TRParityClass = "odd";
          %>
        </s:if>
        <s:else>
          <%
    TRParityClass = "even";
          %>
        </s:else>
        <TR class="${TRParityClass}">
          <TD class="${cssBordure}">
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
              <s:param name="operationMode" value="STORE" />
            </s:url>
            <s:a href="%{removeUrl}" onclick="return confirm('%{getText('itineraire.delete.confirmation')}');">
              <img border="0" alt="Delete" src="<s:url value='/images/supprimer.png'/>" title="<s:text name="tooltip.delete"/>">
            </s:a>
          </TD>
          <TD class="${cssBordure}">
            <s:property value="name"/>
          </TD>
          <TD class="${cssBordure}">
            <s:property value="publishedName"/>
          </TD>
          <TD class="${cssBordure}">
            <s:if test="#itineraire.direction != null">
              <s:text name="%{#itineraire.direction}"/>
            </s:if>
            <s:else>
              <s:text name="no.direction"/>
            </s:else>
          </TD>
          <TD class="${cssBordure}">
            <s:if test="!isArretsVide(id)">
              <s:url id="horairesDePassage" action="list" namespace="/vehicleJourneyAtStop">
                <s:param name="idItineraire" value="id" />
                <s:param name="idLigne" value="idLigne" />
                <s:param name="page" value="1" />
              </s:url>
              <s:a href="%{horairesDePassage}"><s:text name="title.vehicleJourneyAtStop"/></s:a>
            </s:if>
          </TD>
          <TD class="${cssBordure}">
            <s:url id="arretSurItineraire" action="list" namespace="/stoppointOnRoute">
              <s:param name="idItineraire" value="%{id}" />
              <s:param name="idLigne" value="idLigne" />
            </s:url>
            <s:a href="%{arretSurItineraire}"><s:text name="title.stoppointOnRoute"/></s:a>
          </TD>
          <TD class="${cssBordure}">
            <s:url id="exportHorairesItineraire" action="exportHorairesItineraire" namespace="/export">
              <s:param name="idItineraire" value="id"/>
              <s:param name="idLigne" value="idLigne" />
              <s:param name="origin" value="itinerary"/>
            </s:url>
            <s:a href="%{exportHorairesItineraire}"><s:text name="title.export.vehicleJourneyAtStop"/></s:a>
          </TD>
        </TR>
      </s:iterator>
    </TBODY>
  </TABLE>
</div>