<%@ taglib prefix="s" uri="/struts-tags" %>
<%-- Titre et barre de navigation --%>	
<title><s:text name="comparator.action.title.index" /></title>
<s:url id="urlLinesComparison" action="LineFilesComparisonService_index" includeParams="none"/>
<s:property value="filAriane.addElementFilAriane(getText('comparator.action.title.index'), '', #urlLinesComparison)"/>
<div class="panelData">
	<s:property value="filAriane.texteFilAriane" escape="false"/>
</div>
<br>
<p>TMP PUSH TEST</p>
<div>
	<fieldset style="width: 500px;">
		<legend>XML Files to compare</legend>
			<s:form id="compareLineFilesForm" action="LineFilesComparisonService_compare" enctype="multipart/form-data" method="POST">
				<s:select label="Format d'echange" name="exchangeFormat" list="exchangeFormats" listKey="key" listValue="value" /> 
		   		<s:file name="sourceFile" label="Referential Amivif file" />
		   		<s:hidden name="referenceFileContentType" value="text/xml; charset=UTF-8"/>
		   		<s:file name="targetFile" label="File to be compared" />
		   		<s:hidden name="comparedFileContentType" value="text/xml; charset=UTF-8"/>
		   		<s:submit value="Comparer"/>
		   	</s:form>
	</fieldset>
</div>
