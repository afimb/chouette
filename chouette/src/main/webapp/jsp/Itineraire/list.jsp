<%@ taglib uri="/struts-tags" prefix="s"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>
<%-- Titre et barre de navigation --%>	
<title><s:text name="text.itineraire.list.title" /></title>
<s:url id="urlItineraires" action="liste_Itineraire" includeParams="none">
  <s:param name="idLigne" value="%{idLigne}" />
</s:url>
<s:property value="filAriane.addElementFilAriane(getText('text.itineraire.list.title'), ligne.name, #urlItineraires)"/>
<div class="panelData">
  <s:property value="filAriane.texteFilAriane" escape="false"/>
</div>
<br>
<%-- Ajouter itin�raire --%>
<div>
  <s:url id="editItineraire" action="crud_Itineraire!edit" includeParams="none">
    <s:param name="idLigne" value="%{idLigne}" />
  </s:url>
  <s:a href="%{editItineraire}"><b><s:text name="text.itineraire.create.button"/></b></s:a>
  <br><br>
  <FIELDSET align="center" style="width: 500px;">
    <LEGEND><b>Import des horaires d'un itineraire</b></LEGEND>
    <s:form id="uploadCSVForm" action="Import_importHorairesItineraire" enctype="multipart/form-data" method="POST">
      <s:file name="fichier" label="%{getText('action.browse')}"/>
      <s:hidden name="fichierContentType" value="text/csv; charset=ISO-8859-1"/>
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
      <s:iterator value="itineraires" id="itineraire" status="rangItineraire">
        <%
      String cssBordure = "";
      String TRParityClass = "";
        %>
        <s:if test='(#itineraire.wayBack == "R") || (#itineraire.idRetour == null)'>
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
            <s:url id="editUrl" action="crud_Itineraire!edit">
              <s:param name="idItineraire" value="id" />
              <s:param name="idLigne" value="idLigne" />
            </s:url>
            <s:a href="%{editUrl}">
              <img border="0" src="images/editer.png" title="<s:text name="tooltip.edit"/>">
            </s:a>&nbsp;&nbsp;
            <s:url id="removeUrl" action="supprimer_Itineraire_de_la_ligne">
              <s:param name="idItineraire" value="id" />
            </s:url>
            <s:a href="%{removeUrl}" onclick="return confirm('%{getText('itineraire.delete.confirmation')}');">
              <img border="0" src="images/supprimer.png" title="<s:text name="tooltip.delete"/>">
            </s:a>
            </TD>
            <TD class="${cssBordure}">
            <s:property value="name"/>
          </TD>
          <TD class="${cssBordure}">
            <s:property value="publishedName"/>
          </TD>
          <TD class="${cssBordure}">
            <s:text name="itineraire.direction"/>
          </TD>
          <TD class="${cssBordure}">
            <s:if test="!isArretsVide(id)">
              <s:url id="horairesDePassage" action="liste_HorairesDePassage">
                <s:param name="idItineraire" value="id" />
                <s:param name="page" value="1" />
              </s:url>
              <s:a href="%{horairesDePassage}">Horaires</s:a>
            </s:if>
          </TD>
          <TD class="${cssBordure}">
            <s:url id="arretSurItineraire" action="liste_ArretSurItineraire">
              <s:param name="idItineraire" value="id" />
            </s:url>
            <s:a href="%{arretSurItineraire}">Arr&ecirc;ts</s:a>
          </TD>
          <TD class="${cssBordure}">
            <s:url id="exportHorairesItineraire" action="Export_exportHorairesItineraire">
              <s:param name="idItineraire" value="id"/>
            </s:url>
            <s:a href="%{exportHorairesItineraire}">Export horaires</s:a>
          </TD>
        </TR>
      </s:iterator>
    </TBODY>
  </TABLE>
</div>