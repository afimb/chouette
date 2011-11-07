<%@ taglib prefix="s" uri="/struts-tags"%>
<%-- Titre et barre de navigation --%>
<title><s:text name="import.index.title" />
</title>
<s:url id="urlImports" action="execute" includeParams="none" />
<s:property
	value="filAriane.addElementFilAriane(getText('import.index.title'), '', #urlImports)" />
<div class="panelData">
	<s:property value="filAriane.texteFilAriane" escape="false" />
</div>

<s:include value="/jsp/commun/messages.jsp" />

<%-- Import de differents fichiers --%>
<div>
	<!-- <s:if test="useAmivif == 'true'"> -->
	<FIELDSET align="center" style="width: 500px;">
		<LEGEND>
			<b><s:text name="fieldset.legend.import.csv" />
			</b>
		</LEGEND>
		<s:form id="uploadCSVForm" action="importCSV" namespace="/upload"
			enctype="multipart/form-data" method="POST">
			<s:file name="fichier" label="%{getText('action.browse')}" />
			<s:hidden name="fichierContentType" value="text/csv; charset=UTF-8" />
			<s:submit value="%{getText('submit.import.csv')}"
				formId="uploadCSVForm" />
		</s:form>
	</FIELDSET>
	<br>
	<br>
	<!-- </s:if> -->
	<FIELDSET align="center" style="width: 500px;">
		<LEGEND>
			<b><s:text name="fieldset.legend.import.xml" />
			</b>
		</LEGEND>
		<s:form id="uploadXMLForm" action="importXML" namespace="/upload"
			enctype="multipart/form-data" method="POST">
			<s:file name="fichier" label="%{getText('action.browse')}" />
			<s:select emptyOption="false" key="skipXSDValidation"
				list="#@java.util.HashMap@{'true':getText('text.yes'), 'false':getText('text.no')}"
				value="skipXSDValidation" />
			<s:hidden name="fichierContentType" value="text/xml; charset=UTF-8" />
			<s:submit value="%{getText('submit.import.xml')}"
				formId="uploadXMLForm" />
		</s:form>
	</FIELDSET>
	<br>
	<br>
	<FIELDSET align="center" style="width: 500px;">
		<LEGEND>
			<b><s:text name="fieldset.legend.import.multi.xml" />
			</b>
		</LEGEND>
		<s:form id="uploadXMLForm" action="importXMLs" namespace="/upload"
			enctype="multipart/form-data" method="POST">
			<s:file name="fichier" label="%{getText('action.browse')}" />
			<s:hidden name="fichierContentType" value="text/xml; charset=UTF-8" />
			<s:submit value="%{getText('submit.import.multi.xml')}"
				formId="uploadXMLForm" />
		</s:form>
	</FIELDSET>
	<br>
	<br>
	<s:if test="useCSVGeneric == 'true'">
		<FIELDSET align="center" style="width: 500px;">
			<LEGEND>
				<b><s:text name="fieldset.legend.import.multi.csv" />
				</b>
			</LEGEND>
			<s:form id="uploadCSVGenericForm" action="importCSVGeneric"
				namespace="/upload" enctype="multipart/form-data" method="POST">
				<s:file name="fichier" label="%{getText('action.browse')}" />
				<s:hidden name="fichierContentType" value="text/xml; charset=UTF-8" />
				<s:textfield key="objectIdPrefix"  required="true"/>
				<s:submit value="%{getText('submit.import.multi.csv')}"
					formId="uploadCSVGenericForm" />
			</s:form>
		</FIELDSET>
		<br>
		<br>
	</s:if>
		<br>
	<s:if test="useHastus == 'true'">
		<FIELDSET align="center" style="width: 500px;">
			<LEGEND>
				<b><s:text name="fieldset.legend.import.hastus" />
				</b>
			</LEGEND>
			<s:form id="uploadHastusForm" action="importHastus"
				namespace="/upload" enctype="multipart/form-data" method="POST">
				<s:file name="fichier" label="%{getText('action.browse')}" />
				<s:hidden name="fichierContentType" value="text/xml; charset=UTF-8" />
				<s:textfield key="objectIdPrefix"  required="true"/>
				<s:submit value="%{getText('submit.import.hastus')}"
					formId="uploadHastusForm" />
			</s:form>
		</FIELDSET>
<!-- 		<br>
		<br>
		<legend><b><s:text name="fieldset.legend.import.incr.hastus"/></b></legend>
		<s:form id="uploadHastusForm2" enctype="multipart/form-data" method="POST" namespace="/">
		   <s:file name="fichier" label="%{getText('action.browse')}" />
		   <s:hidden name="fichierContentType" value="text/xml; charset=UTF-8"/>
		   <s:hidden name="incremental" value="true" />
		   <s:submit value="%{getText('submit.import.incr.hastus')}" formId="uploadHastusForm2" action="importHastusZip" namespace="/upload"/>
		</s:form>
                <br>
                <ul>
                    <s:url id="fileUrl" namespace="/" action="downloadFile">
                        <s:param name="fileName" value="%{importHastusLogFileName}"/>
                        <s:param name="previousAction" value="%{'ImportAction'}"/>
                    </s:url>
                        <li>Rapport d'import Hastus : <s:a  href="%{fileUrl}"><s:property value="%{importHastusLogFileName}"/></s:a></li>
                </ul> -->
        </FIELDSET>
                <br><br>
	</s:if>
	
</div>
