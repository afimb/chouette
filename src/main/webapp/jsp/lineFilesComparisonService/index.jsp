<%@ taglib prefix="s" uri="/struts-tags" %>
<%-- Titre et barre de navigation --%>	
<title><s:property value="title"/></title>

<div class="panelData"><s:property value="title"/></div>

<%-- Import de différents fichiers --%>
<div>
	<fieldset style="width: 500px;">
		<legend><h2>XML Files to compare</h2></legend>
			<s:form id="compareLineFilesForm" action="compareLineFiles" enctype="multipart/form-data" method="POST">
				<s:select label="Format d'echange" name="exchangeFormat" list="exchangeFormats" listKey="key" listValue="value" /> 
		   		<s:file name="sourceFile" label="Referential Amivif file" />
		   		<s:hidden name="referenceFileContentType" value="text/xml; charset=ISO-8859-1"/>
		   		<s:file name="targetFile" label="File to be compared" />
		   		<s:hidden name="comparedFileContentType" value="text/xml; charset=ISO-8859-1"/>
		   		<s:submit value="Comparer" formId="uploadXMLFilesForm"/>
		   	</s:form>
	</fieldset>
</div>