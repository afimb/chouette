<%@ taglib prefix="s" uri="/struts-tags" %>
<%-- Titre et barre de navigation --%>	
<title><s:text name="text.import.list.title" /></title>
<s:url id="urlImports" action="Import_execute" includeParams="none"/>
<s:property value="filAriane.addElementFilAriane(getText('text.import.list.title'), '', #urlImports)"/>
<div class="panelData">
	<s:property value="filAriane.texteFilAriane" escape="false"/>
</div>
<br>
<%-- Import de différents fichiers --%>
<div>
	<s:if test="useAmivif == 'true'">
	<FIELDSET align="center" style="width: 500px;"> 
	  	<LEGEND><b>Import au format CSV</b></LEGEND> 
		<s:form id="uploadCSVForm" action="Import_importCSV" enctype="multipart/form-data" method="POST">
		     <s:file name="fichier" label="%{getText('action.browse')}"/>
		     <s:hidden name="fichierContentType" value="text/csv; charset=ISO-8859-1"/>
		     <s:submit value="Import du fichier CSV" formId="uploadCSVForm"/>    
		</s:form>
	 </FIELDSET> 
	 <br><br>
	</s:if>
	<FIELDSET align="center" style="width: 500px;"> 
	   <LEGEND><b>Import au format XML</b></LEGEND> 
		<s:form id="uploadXMLForm" action="Import_importXML" enctype="multipart/form-data" method="POST">
		   <s:file name="fichier" label="%{getText('action.browse')}" />
		   <s:hidden name="fichierContentType" value="text/xml; charset=ISO-8859-1"/>
		   <s:submit value="Import du fichier XML" formId="uploadXMLForm"/>
		</s:form>
	</FIELDSET>
	<br><br>
	<FIELDSET align="center" style="width: 500px;"> 
	   <LEGEND><b>Import multilignes au format XML</b></LEGEND> 
		<s:form id="uploadXMLForm" action="Import_importXMLs" enctype="multipart/form-data" method="POST">
		   <s:file name="fichier" label="%{getText('action.browse')}" />
		   <s:hidden name="fichierContentType" value="text/xml; charset=ISO-8859-1"/>
		   <s:submit value="Import du fichier zip" formId="uploadXMLForm"/>
		</s:form>
	</FIELDSET>
	<br><br>
	<s:if test="useAltibus == 'true'">
	<FIELDSET align="center" style="width: 500px;"> 
	  	<LEGEND><b>Import de données Altibus</b></LEGEND> 
		<s:form id="uploadAltibusForm" action="Import_importAltibus" enctype="multipart/form-data" method="POST">
		     <!--s:file name="fichier" label="%{getText('action.browse')}"/-->
		     <!--s:hidden name="fichierContentType" value="text/csv; charset=ISO-8859-1"/-->
		     <s:submit value="Import du fichier Altibus" formId="uploadAltibusForm"/>    
		</s:form>
	</FIELDSET> 
	<br><br>
	</s:if>
	<s:if test="useCSVGeneric == 'true'">
	<FIELDSET align="center" style="width: 500px;"> 
	   <LEGEND><b>Import au format CSV Multi Lignes</b></LEGEND> 
		<s:form id="uploadCSVGenericForm" action="Import_importCSVGeneric" enctype="multipart/form-data" method="POST">
		   <s:file name="fichier" label="%{getText('action.browse')}" />
		   <s:hidden name="fichierContentType" value="text/xml; charset=ISO-8859-1"/>
		   <s:submit value="Import du fichier CSV Generic" formId="uploadCSVGenericForm"/>
		</s:form>
	</FIELDSET>
	<br><br>
	</s:if>
	<s:if test="useHastus == 'true'">
	<FIELDSET align="center" style="width: 500px;"> 
	   <LEGEND><b>Import au format HASTUS</b></LEGEND> 
		<s:form id="uploadHastusForm" enctype="multipart/form-data" method="POST">
		   <s:file name="fichier" label="%{getText('action.browse')}" />
		   <s:hidden name="fichierContentType" value="text/xml; charset=ISO-8859-1"/>
		   <s:hidden name="incremental" value="false" />
		   <s:submit value="Import du fichier HASTUS" formId="uploadHastusForm" action="Import_importHastus" />
		</s:form>
		<br>
		<legend><b>Import Incrémental au format HASTUS</b></legend>
		<s:form id="uploadHastusForm2" enctype="multipart/form-data" method="POST">
		   <s:file name="fichier" label="%{getText('action.browse')}" />
		   <s:hidden name="fichierContentType" value="text/xml; charset=ISO-8859-1"/>
		   <s:hidden name="incremental" value="true" />
		   <s:submit value="Import Incrémental du fichier HASTUS" formId="uploadHastusForm2" action="Import_importHastus" />
		</s:form>
	</FIELDSET>
	<br><br>
	</s:if>
	<s:if test="useAmivif == 'true'">
	<FIELDSET align="center" style="width: 500px;"> 
	   <LEGEND><b>Import au format XML STIF</b></LEGEND> 
		<s:form id="uploadAMIVIFForm" action="Import_importAmivifXML" enctype="multipart/form-data" method="POST">
		   <s:file name="fichier" label="%{getText('action.browse')}" />
		   <s:hidden name="fichierContentType" value="text/xml; charset=ISO-8859-1"/>
		   <s:submit value="Import du fichier XML STIF" formId="uploadAMIVIFForm"/>
		</s:form>
	</FIELDSET>
	<br><br>
	</s:if>
	<s:if test="usePegase == 'true'">
	<FIELDSET align="center" style="width: 500px;"> 
	  	<LEGEND><b>Import au format PEGASE</b></LEGEND> 
		<s:form id="uploadPegaseForm" action="Import_importPegase" enctype="multipart/form-data" method="POST">
		     <s:file name="fichier" label="%{getText('action.browse')}"/>
		     <s:hidden name="fichierContentType" value="text/csv; charset=UTF-8"/>
		     <s:submit value="Import du fichier Pegase" formId="uploadPegaseForm"/>    
		</s:form>
	 </FIELDSET> 
	 <br><br>
	</s:if>
</div>	


