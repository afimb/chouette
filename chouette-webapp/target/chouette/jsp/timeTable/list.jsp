<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="/struts-tags" prefix="s"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>
<s:include value="/jsp/commun/jscalendar.jsp"></s:include>

<%-- Titre et barre de navigation --%>
<title><s:text name="text.tableauMarche.list.title" /></title>
<s:url id="urlTableauMarches" action="list" namespace="/timeTable" includeParams="none"/>
<s:property value="filAriane.addElementFilAriane(getText('text.tableauMarche.list.title'), '', #urlTableauMarches)"/>
<div class="panelData">
  <s:property value="filAriane.texteFilAriane" escape="false"/>
</div>

<s:include value="/jsp/commun/messages.jsp" />

<%-- Ajouter un tableau de marche --%>
<div class="actions">
  <s:url action="add" namespace="/timeTable" id="editTableauMarche"/>
  <s:a href="%{editTableauMarche}"><b><s:text name="text.tableauMarche.create.button"/></b></s:a>
</div>
<br>
<%-- FILTRE --%>
<div>
  <s:form action="list" namespace="/timeTable" >
    <s:select name="idReseau" label="%{getText('filter.network')}" value="%{idReseau}" list="reseaux" listKey="id" listValue="name" headerKey="" headerValue="%{getText('filtre.reseau.dropDownListItem.tous')}" />
    <s:textfield name="commentaire" label="%{getText('filter.comment')}"></s:textfield>
    <s:textfield id="dateDebutPeriode" name="dateDebutPeriode" label="%{getText('filter.begin.interval')}"></s:textfield>
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
    <s:textfield id="dateFinPeriode" name="dateFinPeriode" label="%{getText('filter.end.interval')}"></s:textfield>
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
    <s:submit value="%{getText('action.filtrer')}" />
  </s:form>
</div>
<br>
<div id="displaytag">
  <display:table name="tableauxMarche"  pagesize="20"  requestURI="" id="tableauMarche" export="false">
    <display:column titleKey="table.title.action" sortable="false">
      <s:url id="removeUrl" action="delete" namespace="/timeTable">
        <s:param name="idTableauMarche">${tableauMarche.id}</s:param>
        <s:param name="operationMode">STORE</s:param>
      </s:url>
      <s:url id="editUrl" action="edit" namespace="/timeTable">
        <s:param name="idTableauMarche">${tableauMarche.id}</s:param>
      </s:url>
      <s:a href="%{editUrl}">
        <img border="0" alt="Edit" src="<s:url value='/images/editer.png'/>" title="<s:text name="tooltip.edit"/>">
      </s:a>&nbsp;&nbsp;
      <s:a href="%{removeUrl}" onclick="return confirm('%{getText('tableauMarche.delete.confirmation')}');">
        <img border="0" alt="Delete" src="<s:url value='/images/supprimer.png'/>" title="<s:text name="tooltip.delete"/>">
      </s:a>
    </display:column>
      <display:column titleKey="table.title.route" sortable="false" headerClass="sortable">
      ${tableauMarche.comment}&nbsp;(${tableauMarche.objectId})
    </display:column>
  </display:table>
</div>
