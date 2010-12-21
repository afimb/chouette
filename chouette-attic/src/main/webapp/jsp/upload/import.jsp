<%@ taglib prefix="s" uri="/struts-tags" %>
<%-- Titre et barre de navigation --%>	
<title><s:text name="import.index.title" /></title>
<s:url id="urlImports" action="execute" namespace="/upload" includeParams="none"/>
<s:property value="filAriane.addElementFilAriane(getText('import.index.title'), '', #urlImports)"/>
<div class="panelData">
	<s:property value="filAriane.texteFilAriane" escape="false"/>
</div>

<s:include value="/jsp/commun/messages.jsp" />

<%-- Import de différents fichiers --%>
<div>
	<s:if test="useAmivif == 'true'">
	<FIELDSET align="center" style="width: 500px;"> 
	  	<LEGEND><b><s:text name="fieldset.legend.import.csv"/></b></LEGEND> 
		<s:form id="uploadCSVForm" action="importCSV" namespace="/upload" enctype="multipart/form-data" method="POST">
		     <s:file name="fichier" label="%{getText('action.browse')}"/>
		     <s:hidden name="fichierContentType" value="text/csv; charset=UTF-8"/>
		     <s:submit value="%{getText('submit.import.csv')}" formId="uploadCSVForm"/>    
		</s:form>
	 </FIELDSET> 
	 <br><br>
	</s:if>
	<FIELDSET align="center" style="width: 500px;"> 
	   <LEGEND><b><s:text name="fieldset.legend.import.xml"/></b></LEGEND> 
		<s:form id="uploadXMLForm" action="importXML" namespace="/upload" enctype="multipart/form-data" method="POST">
		   <s:file name="fichier" label="%{getText('action.browse')}" />
		   <s:hidden name="fichierContentType" value="text/xml; charset=UTF-8"/>
		   <s:submit value="%{getText('submit.import.xml')}" formId="uploadXMLForm"/>
		</s:form>
	</FIELDSET>
	<br><br>
	<FIELDSET align="center" style="width: 500px;"> 
	   <LEGEND><b><s:text name="fieldset.legend.import.multi.xml"/></b></LEGEND> 
		<s:form id="uploadXMLForm" action="importXMLs" namespace="/upload" enctype="multipart/form-data" method="POST">
		   <s:file name="fichier" label="%{getText('action.browse')}" />
		   <s:hidden name="fichierContentType" value="text/xml; charset=UTF-8"/>
		   <s:submit value="%{getText('submit.import.multi.xml')}" formId="uploadXMLForm"/>
		</s:form>
	</FIELDSET>
	<br><br>
	<!-- 
	<s:if test="useAltibus == 'true'">
	<FIELDSET align="center" style="width: 500px;"> 
	  	<LEGEND><b><s:text name="fieldset.legend.import.altibus"/></b></LEGEND> 
		<s:form id="uploadAltibusForm" action="importAltibus" namespace="/upload" enctype="multipart/form-data" method="POST">
		     <s:file name="fichier" label="%{getText('action.browse')}"/>
		     <s:hidden name="fichierContentType" value="text/csv; charset=UTF-8"/>
		     <s:submit value="%{getText('submit.import.altibus')}" formId="uploadAltibusForm"/>    
		</s:form>
	</FIELDSET> 
	<br><br>
	</s:if>
	 -->
	<s:if test="useCSVGeneric == 'true'">
	<FIELDSET align="center" style="width: 500px;"> 
	   <LEGEND><b><s:text name="fieldset.legend.import.multi.csv"/></b></LEGEND> 
		<s:form id="uploadCSVGenericForm" action="importCSVGeneric" namespace="/upload" enctype="multipart/form-data" method="POST">
		   <s:file name="fichier" label="%{getText('action.browse')}" />
		   <s:hidden name="fichierContentType" value="text/xml; charset=UTF-8"/>
		   <s:submit value="%{getText('submit.import.multi.csv')}" formId="uploadCSVGenericForm"/>
		</s:form>
	</FIELDSET>
	<br><br>
	</s:if>
	<s:if test="useHastus == 'true'">
	<FIELDSET align="center" style="width: 500px;"> 
	   <LEGEND><b><s:text name="fieldset.legend.import.hastus"/></b></LEGEND> 
		<s:form id="uploadHastusForm" enctype="multipart/form-data" method="POST" namespace="/">
		   <s:file name="fichier" label="%{getText('action.browse')}" />
		   <s:hidden name="fichierContentType" value="text/xml; charset=UTF-8"/>
		   <s:hidden name="incremental" value="false" />
		   <s:submit value="%{getText('submit.import.hastus')}" formId="uploadHastusForm" action="importHastus" namespace="/upload"/>
		</s:form>
		<br>
		<legend><b><s:text name="fieldset.legend.import.incr.hastus"/></b></legend>
		<s:form id="uploadHastusForm2" enctype="multipart/form-data" method="POST" namespace="/">
		   <s:file name="fichier" label="%{getText('action.browse')}" />
		   <s:hidden name="fichierContentType" value="text/xml; charset=UTF-8"/>
		   <s:hidden name="incremental" value="true" />
		   <s:submit value="%{getText('submit.import.incr.hastus')}" formId="uploadHastusForm2" action="importHastus" namespace="/upload"/>
		</s:form>
	</FIELDSET>
	<br><br>
	</s:if>
	<s:if test="useAmivif == 'true'">
	<FIELDSET align="center" style="width: 500px;"> 
	   <LEGEND><b><s:text name="fieldset.legend.import.xml.stif"/></b></LEGEND> 
		<s:form id="uploadAMIVIFForm" action="importAmivifXML" namespace="/upload" enctype="multipart/form-data" method="POST">
		   <s:file name="fichier" label="%{getText('action.browse')}" />
		   <s:hidden name="fichierContentType" value="text/xml; charset=UTF-8"/>
		   <s:submit value="%{getText('submit.import.xml.stif')}" formId="uploadAMIVIFForm"/>
		</s:form>
	</FIELDSET>
	<br><br>
	</s:if>
	<s:if test="usePegase == 'true'">
	<FIELDSET align="center" style="width: 500px;"> 
	  	<LEGEND><b><s:text name="fieldset.legend.import.pegase"/></b></LEGEND> 
		<s:form id="uploadPegaseForm" action="importPegase" namespace="/upload" enctype="multipart/form-data" method="POST">
		     <s:file name="fichier" label="%{getText('action.browse')}"/>
		     <s:hidden name="fichierContentType" value="text/csv; charset=UTF-8"/>
		     <s:submit value="%{getText('submit.import.pegase')}" formId="uploadPegaseForm"/>    
		</s:form>
	 </FIELDSET> 
	 <br><br>
	</s:if>
</div>	
