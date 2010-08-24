<%@ taglib prefix="s" uri="/struts-tags" %>
<%-- Titre et barre de navigation --%>	

<title><s:text name="index.title" /></title>

<s:url id="urlLinesComparison" action="index" includeParams="none"/>
<s:property value="filAriane.addElementFilAriane(getText('index.title'), '', #urlLinesComparison)"/>
<div class="panelData">
	<s:property value="filAriane.texteFilAriane" escape="false"/>
</div>

<s:include value="/jsp/commun/messages.jsp" />

<div>
	<fieldset style="width: 500px;">
		<legend><s:text name="index.fieldset.legend" /></legend>
			<s:form id="compareLineFilesForm" action="compare" enctype="multipart/form-data" method="POST">
				<s:select 
					label="%{getText('index.select.label.formatEchange')}" name="exchangeFormat" 
					list="exchangeFormats" listKey="key" listValue="value" />
					 
		   		<s:file name="sourceFile" label="%{getText('index.file.source.label')}" />
		   		<s:hidden name="referenceFileContentType" value="text/xml; charset=ISO-8859-1"/>
		   		
		   		<s:file name="targetFile" label="%{getText('index.file.target.label')}" />
		   		<s:hidden name="comparedFileContentType" value="text/xml; charset=ISO-8859-1"/>
		   		
		   		<s:submit value="%{getText('index.submit.compare')}"/>
		   	</s:form>
	</fieldset>
</div>
