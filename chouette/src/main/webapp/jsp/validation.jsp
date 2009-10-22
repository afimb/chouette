<%@ taglib prefix="s" uri="/struts-tags" %>
<s:include value="/jsp/commun/jscalendar.jsp"></s:include>
<%-- Titre et barre de navigation --%>	
<title><s:text name="text.validation.list.title" /></title>
<s:url id="urlValidations" action="Validation_execute" includeParams="none"/>
<s:property value="filAriane.addElementFilAriane(getText('text.validation.list.title'), '', #urlValidations)"/>
<div class="panelData">
	<s:property value="filAriane.texteFilAriane" escape="false"/>
</div>
<br>
<%-- Import de différents fichiers --%>
<div>
	<FIELDSET align="center" style="width: 500px;"> 
	  	<LEGEND><b>Validation des données</b></LEGEND> 
		<s:form id="validationForm" action="Validation_valider" enctype="multipart/form-data" method="POST">
		<s:submit value="Valider" formId="validationForm"/>    
		</s:form>
	 </FIELDSET> 
	 <br><br>
	<FIELDSET align="center" style="width: 500px;"> 
	  	<LEGEND><b>Decalage des horaires d'arrivées par rapport aux horaires de départ</b></LEGEND>
		<s:form validate="true" id="decalageForm" action="Validation_decaler" enctype="multipart/form-data" method="POST">
		<s:textfield maxlength="5" id="decalage" name="decalage" key="decalage" required="true"/>
		<s:submit value="Decaler" formId="decalageForm"/>    
		</s:form>
	 </FIELDSET> 
<s:if test="useGeometry == 'true'">
	 <br><br>
	 <FIELDSET align="center" style="width: 500px;"> 
	  	<LEGEND><b>Calcul des coordonnées GPS des arrêts en fonction de ceux de leurs sous-arrêts</b></LEGEND> 
		<s:form id="barycentreForm" action="Validation_barycentre" enctype="multipart/form-data" method="POST">
		<s:submit value="Calculer Barycentre" formId="barycentreForm"/>    
		</s:form>
	 </FIELDSET> 
	 <br><br>
	 <FIELDSET align="center" style="width: 500px;"> 
	  	<LEGEND><b>Convertion des coordonnées Lambert II en WGS 84</b></LEGEND> 
		<s:form id="conversionForm" action="Validation_convertir" enctype="multipart/form-data" method="POST">
		<s:submit value="Convertir" formId="convertionForm"/>    
		</s:form>
	 </FIELDSET> 
</s:if>
	 <br><br>
	<FIELDSET align="center" style="width: 500px;"> 
	  	<LEGEND><b>Purge des données obsolètes</b></LEGEND>
		<s:form validate="true" id="purgeForm" action="Validation_purger" enctype="multipart/form-data" method="POST">
		<s:textfield maxlength="10" id="purge" name="purge" key="purge" required="true"/>
		<script type="text/javascript">
		<!--//
		Calendar.setup(
			{
       			singleClick : true,
				firstDay : 1,
				inputField : "purge",    	// ID of the input field
				ifFormat : "%Y-%m-%d"  			// the date format
			}
		);
		//-->
		</script>
		<s:submit value="Purger" formId="purgeForm"/>
		</s:form>
	 </FIELDSET> 
	 
	 
</div>	
