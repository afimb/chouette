<%@ taglib uri="/struts-tags" prefix="s"%>
<head>
<s:include value="/jsp/commun/jscalendar.jsp"></s:include>
</head>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>
<%-- Titre et barre de navigation --%>	
<title><s:text name="text.tableauMarche.list.title" /></title>
<s:url id="urlTableauMarches" action="liste_TableauMarche" includeParams="none"/>
<s:property value="filAriane.addElementFilAriane(getText('text.tableauMarche.list.title'), '', #urlTableauMarches)"/>
<div class="panelData">
  <s:property value="filAriane.texteFilAriane" escape="false"/>
</div>
<br>
<%-- Ajouter un tableau de marche --%>
<div class="actions">
  <s:url action="crud_TableauMarche!edit" id="editTableauMarche"/>
  <s:a href="%{editTableauMarche}"><b><s:text name="text.tableauMarche.create.button"/></b></s:a>
</div>

<%-- FILTRE --%>
<div>
  <s:form action="liste_TableauMarche">
    <s:select name="idReseau" label="%{getText('filtre.select.reseau')}" value="%{idReseau}" list="reseaux" listKey="id" listValue="name" headerKey="" headerValue="%{getText('filtre.reseau.dropDownListItem.tous')}" />
    <s:textfield name="commentaire" label="%{getText('tableauMarche.comment')}"></s:textfield>
    <s:textfield id="dateDebutPeriode" name="dateDebutPeriode" label="%{getText('filtre.select.periode.debut')}"></s:textfield>
    <script type="text/javascript">
      <!--//
      Calendar.setup(
      {
        singleClick : true,
        firstDay : 1,
        inputField : "dateDebutPeriode",   	// ID of the input field
        ifFormat : "%d/%m/%Y"  				// the date format
      }
    );
      //-->
    </script>
    <s:textfield id="dateFinPeriode" name="dateFinPeriode" label="%{getText('filtre.select.periode.fin')}"></s:textfield>
    <script type="text/javascript">
      <!--//
      Calendar.setup(
      {
        singleClick : true,
        firstDay : 1,
        inputField : "dateFinPeriode",    	// ID of the input field
        ifFormat : "%d/%m/%Y"  				// the date format
      }
    );
      //-->
    </script>
    <s:submit value="%{getText('action.filtrer')}"/>
  </s:form>
</div>

<div class="panel" id="displaytag"> 
  <display:table name="tableauxMarche"  pagesize="20"  requestURI="" id="tableauMarche" export="false">
    <display:column title="Action" sortable="false">
      <s:url id="removeUrl" action="crud_TableauMarche!delete">
        <s:param name="idTableauMarche">${tableauMarche.id}</s:param>
      </s:url>
      <s:url id="editUrl" action="crud_TableauMarche!edit">
        <s:param name="idTableauMarche">${tableauMarche.id}</s:param>
      </s:url>
      <s:a href="%{editUrl}"><img border="0" src="images/editer.png" title="<s:text name="tooltip.edit"/>"></s:a>&nbsp;&nbsp;
      <s:a href="%{removeUrl}" onclick="return confirm('%{getText('tableauMarche.delete.confirmation')}');">
        <img border="0" src="images/supprimer.png" title="<s:text name="tooltip.delete"/>">
      </s:a>
    </display:column>
    <display:column title="Commentaire et Identifiant de l'objet" sortable="false" headerClass="sortable">
      ${tableauMarche.comment}&nbsp;(${tableauMarche.objectId})
    </display:column>
  </display:table>
</div>