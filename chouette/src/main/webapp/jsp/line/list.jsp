<%@ taglib uri="/struts-tags" prefix="s"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>

<%-- Titre et barre de navigation --%> 
<title><s:text name="text.ligne.list.title" /></title>
<s:url id="urlLignes" value="/line/list" includeParams="none"/>
<s:property value="filAriane.addElementFilAriane(getText('text.ligne.list.title'), '', #urlLignes)"/>
<div class="panelData">
  <s:property value="filAriane.texteFilAriane" escape="false"/>
</div>

<br>

<%-- Filtre sur les réseaux et les transporteurs --%>
<div>

  <s:form action="list">

    <s:select	name="idReseau"
              label="%{getText('filtre.select.reseau')}"
              list="reseaux"
              listKey="id"
              listValue="name"
              headerKey=""
              headerValue="%{getText('filtre.reseau.dropDownListItem.tous')}" />

    <s:select 	name="idTransporteur"
               label="%{getText('filtre.select.transporteur')}"
               list="transporteurs"
               listKey="id"
               listValue="name"
               headerKey=""
               headerValue="%{getText('filtre.transporteur.dropDownListItem.tous')}" />

    <s:textfield name="nomLigne" label="%{getText('filtre.select.nomLigne')}"></s:textfield>

    <s:submit value="%{getText('action.filtrer')}"/>

  </s:form>

</div>

<br>

<%-- Ajouter ligne --%>
<div>
  <s:url action="crud_Ligne!edit" id="ajoutLigne"/>
  <s:a href="%{ajoutLigne}"><b><s:text name="text.ligne.create.button"/></b></s:a>
</div>

<br>

<%-- Tableau --%>
<div id="displaytag">
  <display:table name="lignes" sort="list" pagesize="20" requestURI="" id="ligne" export="false">
    <display:column title="Action" sortable="false">
      <s:url id="editUrl" value="/line/edit">
        <s:param name="idLigne">${ligne.id}</s:param>
      </s:url>
      <s:url id="itinerary" value="/itinerary/list">
        <s:param name="idLigne">${ligne.id}</s:param>
      </s:url>
      <s:url id="removeLigne" value="/line/delete">
        <s:param name="idLigne">${ligne.id}</s:param>
      </s:url>
      <s:url id="removeLigneAmivif" value="/export/exportSupprimerAmivif">
        <s:param name="idLigne">${ligne.id}</s:param>
        <s:param name="origin">line</s:param>
      </s:url>
      <s:url id="removeLigneChouette" value="/export/exportSupprimerChouette">
        <s:param name="idLigne">${ligne.id}</s:param>
        <s:param name="origin">line</s:param>
      </s:url>
      <s:url id="exportAmivif" value="/export/exportAmivif">
        <s:param name="idLigne">${ligne.id}</s:param>
        <s:param name="origin">line</s:param>
      </s:url>
      <s:url id="exportChouette" value="/export/exportChouette">
        <s:param name="idLigne">${ligne.id}</s:param>
        <s:param name="origin">line</s:param>
      </s:url>
      <s:url id="exportCSV" value="/export/exportCSV">
        <s:param name="idLigne">${ligne.id}</s:param>
        <s:param name="origin">line</s:param>
      </s:url>
      <div style="float: left; margin-right: 7px;">
        <s:a href="%{editUrl}">
          <img border="0" src="/images/editer.png" title="<s:text name="tooltip.edit"/>">
        </s:a>
      </div>
      <div style="float: left; text-align: left;">
        <s:a href="%{removeLigneChouette}" onclick="return confirm('%{getText('ligne.delete.confirmation')}');">
          <img border="0" src="/images/supprimer.png" alt="Delete" title="<s:text name="tooltip.delete"/>">(CHOUETTE)
        </s:a>
        <br>
        <s:a href="%{removeLigne}" onclick="return confirm('%{getText('ligne.delete.confirmation')}');">
          <img border="0" src="/images/supprimer.png" alt="Delete" title="<s:text name="tooltip.delete"/>">(DU RESEAU)
        </s:a>
        <s:if test="useAmivif == 'true'">
          <br>
          <s:a href="%{removeLigneAmivif}" onclick="return confirm('%{getText('ligne.delete.confirmation')}');">
            <img border="0" src="/images/supprimer.png" alt="Delete" title="<s:text name="tooltip.delete"/>">(STIF)
          </s:a>
        </s:if>
      </div>
    </display:column>
    <display:column title="Nom" sortable="true" headerClass="sortable" property="line.name"/>
    <display:column title="Indice" property="line.number" sortable="true" headerClass="sortable"/>
    <display:column title="Nom Reseau" sortable="true" headerClass="sortable">
      <s:url id="editReseau" value="/network/edit">
        <s:param name="idReseau">${ligne.idReseau}</s:param>
      </s:url>
      <s:a href="%{editReseau}"><s:property value="%{getReseau(#attr.ligne.idReseau)}"/></s:a>
    </display:column>
    <display:column title="Nom Transporteur" sortable="true" headerClass="sortable">
      <s:url id="editTransporteur" value="/company/edit">
        <s:param name="idTransporteur"> ${ligne.idTransporteur}</s:param>
      </s:url>
      <s:a href="%{editTransporteur}" ><s:property value="%{getTransporteur(#attr.ligne.idTransporteur)}"/></s:a>
    </display:column>
    <display:column title="Accès Itinéraires"><s:a href="%{itinerary}">Itinéraires</s:a></display:column>
    <display:column title="Export Ligne">
      <s:a href="%{exportChouette}">Export CHOUETTE</s:a>
      <s:if test="useAmivif == 'true'">
        <br>
        <s:a href="%{exportAmivif}">Export STIF</s:a>
        <br>
        <s:a href="%{exportCSV}">Export CSV</s:a>
      </s:if>
    </display:column>
  </display:table>
</div>

