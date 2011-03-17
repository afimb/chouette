<%@ taglib prefix="s" uri="/struts-tags" %>

<title><s:text name="import.index.title" /></title>
<s:url id="urlImportNeptuneValidation" action="execute" namespace="/neptune-validation" includeParams="none"/>
<s:property value="filAriane.addElementFilAriane(getText('text.neptune.import'), '', #urlImportNeptuneValidation)"/>
	<div style="width: 70%;">
	<div class="panelDataSection"><s:text name="import.index.title"/></div>
	  	<div class="neptune-panel">
		<s:form id="NeptuneValidationUploadForm" action="importNeptune" namespace="/neptune-validation" enctype="multipart/form-data" method="POST">
		   <s:text name="fieldset.legend.import" />
		   <s:file name="file" label="%{getText('action.browse')}" />
		   <s:submit value="%{getText('submit.import.xml')}"/> 
		</s:form>
		<s:include value="/jsp/commun/messages.jsp" />
	</div>
	<b><s:label value="%{report.getLocalizedMessage(getLocale())}"/></b>
	<s:div cssStyle="margin-left: 40px;">
		 <s:iterator value="report.items" var="sheet">
		 <div class="panelDataSection"><s:property value="getLocalizedMessage(getLocale())"/></div>
			 <s:div cssClass="neptune-panel" id="%{status}">
			 <s:iterator value="items" var="test">
			 	<s:div cssClass="%{status}">
			 	<s:label value="%{report.order}.%{#sheet.order}.%{#test.order}" />
			 		<s:property value="getLocalizedMessage(getLocale())" /><br />
			 		<s:if test="%{getStatus().name() != 'OK' && getStatus().name() != 'UNCHECK' }">
							<s:div cssStyle="margin-left: 20px;">
								</s:div>
									 <s:div cssClass="neptune-panel-inSide" id="detail">
										<ol>
										 <s:iterator value="items">
										     <li>
										    	<s:text name="validation.test.delimiter" />
										    	 <s:property value="getLocalizedMessage(getLocale())"/>
										   	 </li>
										    </s:iterator>
								 		 </ol>
								 </s:div>
						</s:if>
			 		</s:div>
			 	</s:iterator>
			 	</s:div>
		 </s:iterator>
		 </s:div>
	<div class="panelDataSection"><s:text name="neptune.field.title" /></div>
	<div class="neptune-panel">
	<s:div>
	
  <s:form action="validation" namespace="/neptune-validation" method="POST">
	   <s:textfield name="validationParam.test3_1_MinimalDistance" id="test3_1_MinimalDistance" label="%{getText('neptune.field.minimum.distance.3.1')}" size="4" />
	   <s:textfield name="validationParam.test3_2_MinimalDistance" id="test3_2_MinimalDistance" label="%{getText('neptune.field.minimum.distance.3.2')}" size="4"/>
	   
	   <s:textarea cols="40" name="polygonCoordinatesAsString" label="%{getText('neptune.field.polygon.3.6')}" rows="9"></s:textarea>
		
		<s:textfield name="validationParam.test3_7_MinimalDistance" label="%{getText('neptune.field.minimum.distance.on.maximum.3.7')}" size="4"/>
		<s:textfield name="validationParam.test3_7_MaximalDistance" size="4" label="/"/>
		
		<s:textfield name="validationParam.test3_8a_MinimalSpeed" label="%{getText('neptune.field.minimum.speed.on.maximum.3.8a')}" size="4"/>
		<s:textfield name="validationParam.test3_8a_MaximalSpeed"  size="4" label="/"/>
		
		<s:textfield name="validationParam.test3_8b_MinimalSpeed" label="%{getText('neptune.field.minimum.speed.on.maximum.3.8b')}" size="4"/>
		<s:textfield name="validationParam.test3_8b_MaximalSpeed" size="4" label="/"/>
		
		<s:textfield name="validationParam.test3_8c_MinimalSpeed" label="%{getText('neptune.field.minimum.speed.on.maximum.3.8c')}" size="4"/>
		<s:textfield name="validationParam.test3_8c_MaximalSpeed" size="4" label="/"/>
		
		<s:textfield name="validationParam.test3_8d_MinimalSpeed" label="%{getText('neptune.field.minimum.speed.on.maximum.3.8d')}" size="4"/>
		<s:textfield name="validationParam.test3_8d_MaximalSpeed" size="4" label="/"/>
		
		
		<s:textfield name="validationParam.test3_9_MinimalSpeed" label="%{getText('neptune.field.minimum.speed.on.maximum.3.9')}" size="4"/>
		<s:textfield name="validationParam.test3_9_MaximalSpeed" size="4" label="/"/>
		
		 <s:textfield name="validationParam.test3_10_MinimalDistance" label="%{getText('neptune.field.minimum.distance.3.10')}" size="4"/>
		 <s:textfield name="validationParam.test3_15_MinimalTime" label="%{getText('neptune.field.minimum.time.3.15')}" size="4"/>
		 
		  <s:textfield name="validationParam.test3_16_3a_MinimalTime" label="%{getText('neptune.field.minimum.time.3.16')}" size="4"/>
		   
		<s:textfield name="validationParam.test3_16c_MinimalTime" label="%{getText('neptune.field.minimum.time.on.maximum.3.16c')}" size="8"/>
		<s:textfield name="validationParam.test3_16c_MaximalTime" size="8" label="/"/>	
	
     	<s:submit action="validation" name="validation" value="%{getText('neptune.field.validation.sumit')}" disabled="#session.imported != true"/>
   		<s:submit action="defaultValue" name="defaultValue" value="%{getText('neptune.field.restore.default.value')}" disabled="#session.imported != true" /> 
  
  </s:form>
  
</s:div>
</div>
</div>