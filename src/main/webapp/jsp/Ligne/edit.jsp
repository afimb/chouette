<%@ taglib prefix="s" uri="/struts-tags" %>
<%-- Titre et barre de navigation --%>
<s:url id="urlUpdate" action="crud_Ligne!edit">
	<s:param name="idLigne" value="%{ligne.id}"/>
</s:url>
<s:if test="ligne.id != null">
	<title><s:text name="text.ligne.update.title" /></title>	
	<s:property value="filAriane.addElementFilAriane(getText('text.ligne.update.title'), '', #urlLigneUpdate)"/>
</s:if>
<s:else>
	<title><s:text name="text.ligne.create.title" /></title>
	<s:property value="filAriane.addElementFilAriane(getText('text.ligne.create.title'), '', #urlLigneUpdate)"/>	
</s:else>
<div class="panelData">
	<s:property value="filAriane.texteFilAriane" escape="false"/>
</div>
<br>
<s:form> 
	<s:if test="ligne.id != null">	
		<s:hidden name="idLigne" value="%{ligne.id}"/>
	</s:if>	
	<%-- Valeur sélectionné par défaut est contenue dans value (chaineIdReseau) et doit être une chaîne de caractère obligatoirement --%>
	<s:select name="ligne.idReseau" label="%{getText('ligne.idReseau')}" value="%{ligne.idReseau}" list="reseaux" listKey="id" listValue="name" headerKey="-1" headerValue="%{getText('ligne.aucunReseau')}">	
	</s:select>
	<%-- Valeur sélectionné par défaut est contenue dans value (chaineIdTransporteur) et doit être une chaîne de caractère obligatoirement --%>
	<s:select name="ligne.idTransporteur" label="%{getText('ligne.idTransporteur')}" value="%{ligne.idTransporteur}" list="transporteurs" listKey="id" listValue="name"  headerKey="-1" headerValue="%{getText('ligne.aucunTransporteur')}">		
	</s:select>
	<s:textfield key="ligne.name" required="true"/>
	<s:textfield key="ligne.publishedName" />
	<s:textfield key="ligne.registrationNumber" required="true"/>
	<s:textfield key="ligne.number" />	
	<s:if test="ligne.id != null">
		<s:select key="ligne.transportModeName" list="@fr.certu.chouette.ihm.enumeration.EnumerationApplication@getModeTransportEnum()" listKey="enumeratedTypeAccess" listValue="textePropriete"/>	
	</s:if>
	<s:else>
		<s:select key="ligne.transportModeName" list="@fr.certu.chouette.ihm.enumeration.EnumerationApplication@getModeTransportEnum()" listKey="enumeratedTypeAccess" listValue="textePropriete" value="@chouette.schema.types.TransportModeNameType@BUS"/>
	</s:else>
	<s:textfield key="ligne.comment"/>

	<%-- Actions --%>
	<s:if test="ligne.id != null">	
  		<tr>
  			<td colspan="2">
  				<s:submit value="%{getText('action.update')}" action="crud_Ligne!update" theme="simple" cssStyle="float: right;"/>
  				<s:submit value="%{getText('action.cancel')}" action="crud_Ligne!cancel" theme="simple" cssStyle="float: right;"/>
  			</td>
  		</tr>
  	</s:if>
  	<s:else>
  		<tr>
  			<td colspan="2">
  				<%--<s:submit value="%{getText('action.createAndEdit')}" action="crud_Ligne!createAndEdit"  theme="simple" cssStyle="float: right;"/>--%>  			
  				<s:submit value="%{getText('action.create')}" action="crud_Ligne!update" theme="simple" cssStyle="float: right;"/>
  				<s:submit value="%{getText('action.cancel')}" action="crud_Ligne!cancel" theme="simple" cssStyle="float: right;"/>
  			</td>
  		</tr>
  	</s:else>
  	<%-- Ajout des balises tr et td pour le faire apparaitre dans le tableau --%>
  	<tr><td colspan="2"><s:include value="/jsp/commun/asterisque.jsp" /></td></tr>	
</s:form>