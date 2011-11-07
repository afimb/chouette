<%@ taglib prefix="s" uri="/struts-tags"%>
<s:include value="/jsp/commun/jscalendar.jsp"></s:include>
<%-- Titre et barre de navigation --%>
<title><s:text name="text.validation.list.title" /></title>
<s:url id="urlValidations" action="Validation_execute" includeParams="none" />
<s:property value="filAriane.addElementFilAriane(getText('text.validation.list.title'), '', #urlValidations)" />
<div class="panelData">
	<s:property value="filAriane.texteFilAriane" escape="false" />
</div>

<s:include value="/jsp/commun/messages.jsp" />

<%-- Import files --%>
<div>
	<s:if test="useValidation == 'true'">
		<FIELDSET style="width: 500px;">
			<LEGEND><b><s:text name="title.validate.data" /></b></LEGEND>
			<s:form id="validationForm" action="Validation_valider"
				enctype="multipart/form-data" method="POST">
				<s:submit value="%{getText('action.validate')}"
					formId="validationForm" />
			</s:form>
			<br>
			<s:text name="text.goto.bateri" />
			<s:a href="http://www.bateri.fr/">http://www.bateri.fr/</s:a>
		</FIELDSET>
	</s:if>
	
	<s:if test="useGeometry == 'true'">
		<br><br>
		<FIELDSET style="width: 500px;">
			<LEGEND>
				<b><s:text name="title.calculate.coordinates" />
				</b>
			</LEGEND>
			<s:form id="barycentreForm" action="Validation_barycentre"
				enctype="multipart/form-data" method="POST">
				<s:submit value="%{getText('action.calculate.barycentre')}"
					formId="barycentreForm" 
					onclick="return confirm('%{getText('calcul.barycentre.confirmation')}');"/>
			</s:form>
		</FIELDSET>
		<br>
		<br>
		<FIELDSET style="width: 500px;">
			<LEGEND>
				<b><s:text name="title.convert1" />
					<s:text name="lambert.%{lambertSRID}"/> 
						<s:text name="title.convert2"/>
				</b>
			</LEGEND>
			<s:form id="conversionForm" action="Validation_convertir"
				enctype="multipart/form-data" method="POST">
				<s:submit value="%{getText('action.convert')}"
					formId="convertionForm" 
					onclick="return confirm('%{getText('conversion.coordonnee.confirmation')}');"/>
			</s:form>
		</FIELDSET>
	</s:if>
	<br>
	<br>
	<FIELDSET style="width: 500px;">
		<LEGEND>
			<b><s:text name="title.purge" />
			</b>
		</LEGEND>
		<s:form validate="true" id="purgeForm" action="Validation_purger"
			enctype="multipart/form-data" method="POST">
			<s:textfield maxlength="10" id="purgeBoundaryDate"
				name="purgeBoundaryDate" key="purgeBoundaryDate" required="true" />
			<script type="text/javascript">
			<!--//
				Calendar.setup({
					singleClick : true,
					firstDay : 1,
					inputField : "purgeBoundaryDate", // ID of the input field
					ifFormat : "%d/%m/%Y" // the date format
				});
			//-->
			</script>
			<s:radio name="beforeDatePurge" id="beforeDatePurge"
				list="#{'true': getText('beforeDatePurge.beforeDate'),'false': getText('beforeDatePurge.afterDate') }"
				value="true" label="%{getText('beforeDatePurge.label')}" />
			<s:submit value="%{getText('action.purge')}" formId="purgeForm" 
				onclick="return confirm('%{getText('purge.donnee.confirmation')}');"/>
		</s:form>
	</FIELDSET>
</div>
