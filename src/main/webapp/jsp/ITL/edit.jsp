<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>
<head> 
	<s:include value="/jsp/commun/scriptaculous.jsp" />
</head>
 <%-- Titre et barre de navigation --%>
<s:url id="urlITLUpdate" action="crud_ITL!edit">
	<s:param name="idITL" value="%{itl.id}"/>
</s:url>
<s:if test="itl.id != null">
	<title><s:text name="text.itl.update.title" /></title>		
	<s:property value="filAriane.addElementFilAriane(getText('text.itl.update.title'), '', #urlITLUpdate)"/>
</s:if> 
<s:else>
	<title><s:text name="text.itl.create.title" /></title>
	<s:property value="filAriane.addElementFilAriane(getText('text.itl.create.title'), '', #urlITLUpdate)"/>
</s:else>
<div class="panelData">
	<s:property value="filAriane.texteFilAriane" escape="false"/>
</div>
<br>
	<div class="panelDataSection"><s:text name="itl"/></div>
	<div class="panel">
		<s:form cssClass="panelDataInnerForm" action="crud_ITL!update">
			<s:if test="itl.id != null">
				<s:hidden name="idItl" value="%{itl.id}"/>
			</s:if>		
			<s:textfield size="50" key="itl.nom" required="true"/>
			<s:if test="itl.id != null">
				<tr>
					<td><s:text name="text.itl.line"/></td>
					<td><s:text name="${ligneName}"/></td>
				</tr>
				
			</s:if>
			<s:else>
				<s:select name="itl.idLigne" value="%{chaineIdLigne}" key="itl.idLigne"  list="lignes" listKey="id" listValue="fullName" required="true"/>			
			</s:else>			
			
						
			<%-- Actions --%>
			<tr><td colspan="2">
				<s:if test="itl.id != null">						
					<s:submit key="action.update" theme="simple" cssStyle="float: right;"/>	
					<s:submit value="%{getText('action.cancel')}" name="redirect-action:crud_ITL!cancel" theme="simple" cssStyle="float: right;"/>						
				</s:if>
				<s:else>
					<s:submit key="action.create" theme="simple" cssStyle="float: right;"/>	
					<s:submit value="%{getText('action.cancel')}" name="redirect-action:crud_ITL!cancel" theme="simple" cssStyle="float: right;"/>
				</s:else>
			</td></tr>
			
			<%-- Ajout des balises tr et td pour le faire apparaitre dans le tableau --%>
  			<tr><td colspan="2"><s:include value="/jsp/commun/asterisque.jsp" /></td></tr>
		</s:form>
	</div>
	
	<div class="panelDataSection"><s:text name="itl.arretPhysiqueIds"/></div>
	<div class="panel">
		<s:if test="itl.id != null && itl.idLigne != null">
			<div id="displaytag">
				<display:table uid="arretsDansITLList" name="arretsDansITLList" excludedParams="action saisieNomArretExistant saisieNomArretExistantKey" requestURI="crud_ITL!edit.action" sort="list" pagesize="10" export="false">		
					<display:column title="Action">
				  		<s:url id="removeUrl" action="ITL_removeStop">
							<s:param name="idItl" value="${itl.id}" />
							<s:param name="idAreaStop" value="%{arretsDansITLList[${arretsDansITLList_rowNum} - 1].id}" />
						</s:url>
						<s:a href="%{removeUrl}" onclick="return confirm('%{getText('popup.confirmer')}')"><img border="0" src="images/supprimer.png" title="<s:text name="tooltip.delete"/>"></s:a> 
				  	</display:column>
	
				  	<display:column title="Nom" property="name"/>
				  	<display:column title="Code INSEE" property="countryCode"/>
				  	<display:column title="Adresse" property="streetName"/>
				  </display:table>	 
				
			</div>
			
		
			<%-- Bloc permettant la recherche d'un arret --%>
			<div ID="divSearchArret" STYLE="border: 1px; border-color: black;">
				<s:form cssClass="panelDataInnerForm" action="ITL_addStop" id="creerArretForm" theme="simple">
					<table><tr>
						<td>
							<s:if test="itl.id != null">
								<s:hidden name="idItl" value="%{itl.id}" id="idItl"/>
							</s:if>
							<s:select name="saisieNomArretExistantKey" value="%{saisieNomArretExistant}" key="saisieNomArretExistantKey"  list="arrets" listKey="id" listValue="fullName"/>
						</td>
						<td>
							<s:submit name="action" key="action.add" formId="creerArretForm"  />
						</td>
					</tr></table>
				</s:form>
			</div>
		</s:if>
	</div>
			
	
					
		



 






	
	
	



