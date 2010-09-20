<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="/struts-tags" prefix="s"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>

<%-- Titre et barre de navigation --%> 
<title><s:text name="text.ligne.list.title" /></title>
<s:url id="urlLignes" action="list" namespace="/line" includeParams="none"/>
<s:property value="filAriane.addElementFilAriane(getText('text.ligne.list.title'), '', #urlLignes)"/>
<div class="panelData">
  <s:property value="filAriane.texteFilAriane" escape="false"/>
</div>

<s:include value="/jsp/commun/messages.jsp" />

<%-- Filtre sur les réseaux et les transporteurs --%>
<div>
  <s:form action="list">

    <s:select	name="filterNetworkId"
              label="%{getText('filtre.select.reseau')}"
              list="reseaux"
              listKey="id"
              listValue="name"
              headerKey=""
              headerValue="%{getText('filtre.reseau.dropDownListItem.tous')}" />

    <s:select 	name="filterCompanyId"
               label="%{getText('filtre.select.transporteur')}"
               list="transporteurs"
               listKey="id"
               listValue="name"
               headerKey=""
               headerValue="%{getText('filtre.transporteur.dropDownListItem.tous')}" />

    <s:textfield name="filterLineName" label="%{getText('filtre.select.nomLigne')}"></s:textfield>

    <s:submit value="%{getText('action.filtrer')}"/>

  </s:form>

</div>

<br>

<%-- Ajouter ligne --%>
<div>
  <s:url action="add" namespace="/line" id="ajoutLigne"/>
  <s:a href="%{ajoutLigne}"><b><s:text name="text.ligne.create.button"/></b></s:a>
</div>

<br>

<%-- Tableau --%>
<div id="displaytag">
  <display:table name="lignes" sort="list" pagesize="20" requestURI="" id="ligne"  export="false">
    <display:column titleKey="table.title.action" sortable="false">
      <s:url id="editUrl" action="edit" namespace="/line">
        <s:param name="idLigne">${ligne.id}</s:param>
      </s:url>
      <s:url id="removeLigne" action="delete" namespace="/line">
        <s:param name="idLigne">${ligne.id}</s:param>
        <s:param name="operationMode">STORE</s:param>
      </s:url>
      <s:url id="itinerary" action="list" namespace="/route">
        <s:param name="idLigne">${ligne.id}</s:param>
        <s:param name="operationMode">STORE</s:param>
      </s:url>
      <s:url id="removeLigneAmivif" action="exportSupprimerAmivif" namespace="/export">
        <s:param name="idLigne">${ligne.id}</s:param>
        <s:param name="origin">line</s:param>
        <s:param name="operationMode">STORE</s:param>
      </s:url>
      <s:url id="removeLigneChouette" action="deleteChouette" namespace="/line">
        <s:param name="idLigne">${ligne.id}</s:param>
        <s:param name="exportMode">${'CHOUETTE'}</s:param>
        <s:param name="operationMode">STORE</s:param>
      </s:url>
      <s:url id="removeLigneNeptune" action="deleteChouette" namespace="/line">
        <s:param name="idLigne">${ligne.id}</s:param>
        <s:param name="exportMode">${'NEPTUNE'}</s:param>
        <s:param name="operationMode">STORE</s:param>
      </s:url>
      <s:url id="exportAmivif" action="exportAmivif" namespace="/export">
        <s:param name="idLigne">${ligne.id}</s:param>
        <s:param name="origin">line</s:param>
        <s:param name="operationMode">STORE</s:param>
      </s:url>
      <s:url id="exportChouette" action="exportChouette" namespace="/line">
        <s:param name="idLigne">${ligne.id}</s:param>
        <s:param name="exportMode">${'CHOUETTE'}</s:param>
        <s:param name="operationMode">STORE</s:param>
      </s:url>
      <s:url id="exportNeptune" action="exportChouette" namespace="/line">
        <s:param name="idLigne">${ligne.id}</s:param>
        <s:param name="exportMode">${'NEPTUNE'}</s:param>
        <s:param name="operationMode">STORE</s:param>
      </s:url>
      <s:url id="exportCSV" action="exportCSV" namespace="/export">
        <s:param name="idLigne">${ligne.id}</s:param>
        <s:param name="origin">line</s:param>
        <s:param name="operationMode">STORE</s:param>
      </s:url>
      <div style="float: left; margin-right: 7px;">
        <s:a href="%{editUrl}">
          <img border="0" alt="Edit" src="<s:url value='/images/editer.png'/>" title="<s:text name="tooltip.edit"/>">
        </s:a>
      </div>
      <div style="float: left; text-align: left;">
        <s:a href="%{removeLigneChouette}" onclick="return confirm('%{getText('ligne.delete.confirmation')}');">
          <img border="0" src="<s:url value='/images/supprimer.png'/>" alt="Delete" title="<s:text name="tooltip.delete"/>"><s:text name="text.ligne.delete.chouette"/>
        </s:a>
        <br>
        <s:a href="%{removeLigneNeptune}" onclick="return confirm('%{getText('ligne.delete.confirmation')}');">
          <img border="0" src="<s:url value='/images/supprimer.png'/>" alt="Delete" title="<s:text name="tooltip.delete"/>"><s:text name="text.ligne.delete.neptune"/>
        </s:a>
        <br>
        <s:a href="%{removeLigne}" onclick="return confirm('%{getText('ligne.delete.confirmation')}');">
          <img border="0" src="<s:url value='/images/supprimer.png'/>" alt="Delete" title="<s:text name="tooltip.delete"/>"><s:text name="text.ligne.delete.network"/>
        </s:a>
        <s:if test="useAmivif == 'true'">
          <br>
          <s:a href="%{removeLigneAmivif}" onclick="return confirm('%{getText('ligne.delete.confirmation')}');">
            <img border="0" src="<s:url value='/images/supprimer.png'/>" alt="Delete" title="<s:text name="tooltip.delete"/>"><s:text name="text.ligne.delete.stif"/>
          </s:a>
        </s:if>
      </div>
    </display:column>
    <display:column titleKey="table.title.name" sortable="true" headerClass="sortable" property="line.name"/>
    <display:column titleKey="table.title.index" property="line.number" sortable="true" headerClass="sortable"/>
    <display:column titleKey="table.title.network" sortable="true" headerClass="sortable">
      <s:url id="editReseau" action="edit" namespace="/network">
        <s:param name="idReseau">${ligne.idReseau}</s:param>
      </s:url>
      <s:a href="%{editReseau}"><s:property value="getReseau(#attr.ligne.idReseau)"/></s:a>
    </display:column>
    <display:column titleKey="table.title.company" sortable="true" headerClass="sortable">
      <s:url id="editTransporteur" action="edit"  namespace="/company">
        <s:param name="idTransporteur"> ${ligne.idTransporteur}</s:param>
      </s:url>
      <s:a href="%{editTransporteur}" ><s:property value="getTransporteur(#attr.ligne.idTransporteur)"/></s:a>
    </display:column>
    <display:column titleKey="table.title.route">
        <s:a href="%{itinerary}"><s:text name="table.text.route"/></s:a>
    </display:column>
    <display:column titleKey="table.title.export">
      <s:a href="%{exportChouette}"><s:text name="text.ligne.export.chouette" /></s:a>
      <br>
      <s:a href="%{exportNeptune}"><s:text name="text.ligne.export.neptune" /></s:a>
      <s:if test="useAmivif == 'true'">
        <br>
        <s:a href="%{exportAmivif}"><s:text name="text.ligne.export.stif" /></s:a>
      </s:if>
      <br>
      <s:a href="%{exportCSV}"><s:text name="text.ligne.export.csv" /></s:a>
    </display:column>
  </display:table>
</div>

