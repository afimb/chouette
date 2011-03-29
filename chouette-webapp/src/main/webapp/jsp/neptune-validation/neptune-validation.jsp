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
	<s:div id="category1">
		 <s:iterator value="report.items" var="sheet" status="status">
		 <div class="panelDataSection"><s:property value="getLocalizedMessage(getLocale())"/></div>
			 <s:div cssClass="neptune-panel" id="%{status}">
			 <s:iterator value="items" var="test" status="itemsStatus">
			 	<s:div cssClass="%{status}">
			 	<div>
					<s:a href="#-1" onclick="showIt('detail%{#status.index}_%{#itemsStatus.index}','showIt_%{#status.index}_%{#itemsStatus.index}');" 
						id="showIt_%{#status.index}_%{#itemsStatus.index}" title="%{getText('text.detail.show')}">
						<img src="<s:url value='/images/plus.png'/>" alt="%{getText('text.detail.show')}"/>						
					</s:a> 
					<s:a href="#-1" onclick="hideIt('detail%{#status.index}_%{#itemsStatus.index}','hideIt_%{#status.index}_%{#itemsStatus.index}');" 
						id="hideIt_%{#status.index}_%{#itemsStatus.index}" title="%{getText('text.detail.hide')}">
						<img src="<s:url value='/images/moins.png'/>" alt="%{getText('text.detail.hide')}"/>
					</s:a>
						<s:label value="%{report.order}.%{#sheet.order}.%{#test.order}" />
				</div>
			 		<s:div cssStyle="width: 700px; padding-left:100px; margin-top:-15px">
			 			<s:property value="getLocalizedMessage(getLocale())"/>
					</s:div>
			 		<s:if test="%{getStatus().name() != 'OK' && getStatus().name() != 'UNCHECK' }">
							<s:div cssStyle="margin-left: 20px;">
								</s:div>
									 <s:div cssClass="neptune-panel-inSide" id="detail%{#status.index}_%{#itemsStatus.index}" cssStyle="display:none;">
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
	
  <s:form action="validation" namespace="/neptune-validation" method="POST" theme="simple">
	 	<table>
		 	<tr>
			 	<td> 
			 	<s:text name="neptune.field.minimum.distance.3.1"></s:text>
			 	</td>
			 	<td>
			 	 <s:textfield name="validationParam.test3_1_MinimalDistance" id="test3_1_MinimalDistance" size="4" />
			 	</td>
		 	</tr>
		 	<tr>
			 	<td> 
			 	<s:text name="neptune.field.minimum.distance.3.2"></s:text>
			 	</td>
			 	<td>
			 	 <s:textfield name="validationParam.test3_2_MinimalDistance" id="test3_2_MinimalDistance" size="4"/>
			 	</td>
		 	</tr>
		 	<tr>
			 	<td> 
			 	 <s:text name="neptune.field.polygon.3.6"></s:text>
			 	</td>
			 	<td>
			 	 <s:textarea cols="20" name="polygonCoordinatesAsString" rows="8"></s:textarea>
			 	</td>
		 	</tr>
		 	<tr>
			 	<td> 
			 	 <s:text name="neptune.field.minimum.distance.on.maximum.3.7"></s:text>
			 	</td>
			 	<td>
			 	<s:textfield name="validationParam.test3_7_MinimalDistance"  size="4"/>
				<s:textfield name="validationParam.test3_7_MaximalDistance" size="4" label="/"/>
			 	</td>
		 	</tr>
		 	<tr>
			 	<td> 
			 	 <s:text name="neptune.field.minimum.speed.on.maximum.3.8a"></s:text>
			 	</td>
			 	<td>
			 	<s:textfield name="validationParam.test3_8a_MinimalSpeed" label="%{getText('neptune.field.minimum.speed.on.maximum.3.8a')}" size="4"/>
				<s:textfield name="validationParam.test3_8a_MaximalSpeed"  size="4" label="/"/>
			 	</td>
		 	</tr>
		 	<tr>
			 	<td> 
			 	  <s:text name="neptune.field.minimum.speed.on.maximum.3.8b"></s:text>
			 	</td>
			 	<td>
			 	<s:textfield name="validationParam.test3_8b_MinimalSpeed" label="%{getText('neptune.field.minimum.speed.on.maximum.3.8b')}" size="4"/>
				<s:textfield name="validationParam.test3_8b_MaximalSpeed" size="4" label="/"/>
			 	</td>
		 	</tr>
		 	<tr>
			 	<td> 
			 	  <s:text name="neptune.field.minimum.speed.on.maximum.3.8c"></s:text>
			 	</td>
			 	<td>
			 	<s:textfield name="validationParam.test3_8c_MinimalSpeed" label="%{getText('neptune.field.minimum.speed.on.maximum.3.8c')}" size="4"/>
				<s:textfield name="validationParam.test3_8c_MaximalSpeed" size="4" label="/"/>
			 	</td>
		 	</tr>
		 	<tr>
			 	<td> 
			 	 <s:text name="neptune.field.minimum.speed.on.maximum.3.8d"></s:text>
			 	</td>
			 	<td>
			 	<s:textfield name="validationParam.test3_8d_MinimalSpeed" label="%{getText('neptune.field.minimum.speed.on.maximum.3.8d')}" size="4"/>
				<s:textfield name="validationParam.test3_8d_MaximalSpeed" size="4" label="/"/>
			 	</td>
		 	</tr>
		 	<tr>
			 	<td> 
			 	 <s:text name="neptune.field.minimum.speed.on.maximum.3.9"></s:text>
			 	</td>
			 	<td>
			 	<s:textfield name="validationParam.test3_9_MinimalSpeed" label="%{getText('neptune.field.minimum.speed.on.maximum.3.9')}" size="4"/>
				<s:textfield name="validationParam.test3_9_MaximalSpeed" size="4" label="/"/>
			 	</td>
		 	</tr>
		 	<tr>
			 	<td> 
			 	 <s:text name="neptune.field.minimum.distance.3.10"></s:text>
			 	</td>
			 	<td>
			 	 <s:textfield name="validationParam.test3_10_MinimalDistance" label="%{getText('neptune.field.minimum.distance.3.10')}" size="4"/>
			 	</td>
		 	</tr>
		 	<tr>
			 	<td> 
			 	 <s:text name="neptune.field.minimum.time.3.15"></s:text>
			 	</td>
			 	<td>
			 	  <s:textfield name="validationParam.test3_15_MinimalTime" label="%{getText('neptune.field.minimum.time.3.15')}" size="4"/>
			 	</td>
		 	</tr>
		 	<tr>
			 	<td> 
			 	 <s:text name="neptune.field.minimum.time.3.16"></s:text>
			 	</td>
			 	<td>
			 	   <s:textfield name="validationParam.test3_16_3a_MinimalTime" label="%{getText('neptune.field.minimum.time.3.16')}" size="4"/>
			 	</td>
		 	</tr>
		 	<tr>
			 	<td> 
			 	 <s:text name="neptune.field.minimum.time.on.maximum.3.16c"></s:text>
			 	</td>
			 	<td>
			 	   <s:textfield name="validationParam.test3_16c_MinimalTime" label="%{getText('neptune.field.minimum.time.on.maximum.3.16c')}" size="8"/>
				  	<s:textfield name="validationParam.test3_16c_MaximalTime" size="8" label="/"/>
			 	</td>
		 	</tr>
		 	<tr>
			 	<td> 
			 	<s:text name="neptune.projection_reference.label"></s:text>
			 	</td>
			 	<td>
					<s:textfield name="validationParam.projection_reference" label="%{getTexneptune.projection_reference.label')}" size="8"/>
			 	</td>
		 	</tr>
	 	</table>
		
		 <br />	
		
     	<s:submit action="validation" name="validation" value="%{getText('neptune.field.validation.sumit')}" disabled="#session.imported != true"/>
   		<s:submit action="defaultValue" name="defaultValue" value="%{getText('neptune.field.restore.default.value')}" disabled="#session.imported != true" /> 
  
  </s:form>
  
</s:div>
</div>
</div>
