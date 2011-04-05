<%@ taglib prefix="s" uri="/struts-tags" %>

<title><s:text name="import.index.title" /></title>
<s:url id="urlImportNeptuneValidation" action="execute" namespace="/neptune-validation" includeParams="none"/>
<s:property value="filAriane.addElementFilAriane(getText('text.neptune.import'), '', #urlImportNeptuneValidation)"/>
	<div style="width: 75%;">
	  <s:form action="validation" namespace="/neptune-validation" method="POST" theme="simple" enctype="multipart/form-data">
		<div class="panelDataSection"><s:text name="import.index.title"/></div>
	  	<div class="neptune-panel">
		   <s:text name="fieldset.legend.import" />
		   <s:file name="file" label="%{getText('action.browse')}" /> 
		</div>
		<s:include value="/jsp/commun/messages.jsp" />
	<div class="panelDataSection"><s:text name="neptune.field.title" /></div>
	<div class="neptune-panel">
	<s:div>
	 	<table>
		 	<tr>
			 	<td> 
			 	<s:text name="neptune.field.minimum.distance.3.1"></s:text>
			 	</td>
			 	<td>
			 	 <s:textfield name="validationParam.test3_1_MinimalDistance" id="test3_1_MinimalDistance" size="5"/>
			 	</td>
		 	</tr>
		 	<tr>
			 	<td> 
			 	<s:text name="neptune.field.minimum.distance.3.2"></s:text>
			 	</td>
			 	<td>
			 	 <s:textfield name="validationParam.test3_2_MinimalDistance" id="test3_2_MinimalDistance" size="5"/>
			 	</td>
		 	</tr>
		 	<tr>
			 	<td> 
			 	 <s:text name="neptune.field.polygon.3.6"></s:text>
			 	</td>
			 	<td>
			 	 <s:textfield  name="polygonCoordinatesAsString" size="63"/>
			 	</td>
		 	</tr>
		 	<tr>
			 	<td> 
			 	 <s:text name="neptune.field.minimum.distance.on.maximum.3.7"></s:text>
			 	</td>
			 	<td>
			 	<s:textfield name="validationParam.test3_7_MinimalDistance"  size="5"/>/
				<s:textfield name="validationParam.test3_7_MaximalDistance" size="5"/>
			 	</td>
		 	</tr>
		 	<tr>
			 	<td> 
			 	 <s:text name="neptune.field.minimum.speed.on.maximum.3.8a"></s:text>
			 	</td>
			 	<td>
			 	<s:textfield name="validationParam.test3_8a_MinimalSpeed" label="%{getText('neptune.field.minimum.speed.on.maximum.3.8a')}" size="5"/>/
				<s:textfield name="validationParam.test3_8a_MaximalSpeed"  size="5" label="/"/>
			 	</td>
		 	</tr>
		 	<tr>
			 	<td> 
			 	  <s:text name="neptune.field.minimum.speed.on.maximum.3.8b"></s:text>
			 	</td>
			 	<td>
			 	<s:textfield name="validationParam.test3_8b_MinimalSpeed" label="%{getText('neptune.field.minimum.speed.on.maximum.3.8b')}" size="5"/>/
				<s:textfield name="validationParam.test3_8b_MaximalSpeed" size="5" label="/"/>
			 	</td>
		 	</tr>
		 	<tr>
			 	<td> 
			 	  <s:text name="neptune.field.minimum.speed.on.maximum.3.8c"></s:text>
			 	</td>
			 	<td>
			 	<s:textfield name="validationParam.test3_8c_MinimalSpeed" label="%{getText('neptune.field.minimum.speed.on.maximum.3.8c')}" size="5"/>/
				<s:textfield name="validationParam.test3_8c_MaximalSpeed" size="5" label="/"/>
			 	</td>
		 	</tr>
		 	<tr>
			 	<td> 
			 	 <s:text name="neptune.field.minimum.speed.on.maximum.3.8d"></s:text>
			 	</td>
			 	<td>
			 	<s:textfield name="validationParam.test3_8d_MinimalSpeed" label="%{getText('neptune.field.minimum.speed.on.maximum.3.8d')}" size="5"/>/
				<s:textfield name="validationParam.test3_8d_MaximalSpeed" size="5" label="/"/>
			 	</td>
		 	</tr>
		 	<tr>
			 	<td> 
			 	 <s:text name="neptune.field.minimum.speed.on.maximum.3.9"></s:text>
			 	</td>
			 	<td>
			 	<s:textfield name="validationParam.test3_9_MinimalSpeed" label="%{getText('neptune.field.minimum.speed.on.maximum.3.9')}" size="5"/>/
				<s:textfield name="validationParam.test3_9_MaximalSpeed" size="5" label="/"/>
			 	</td>
		 	</tr>
		 	<tr>
			 	<td> 
			 	 <s:text name="neptune.field.minimum.distance.3.10"></s:text>
			 	</td>
			 	<td>
			 	 <s:textfield name="validationParam.test3_10_MinimalDistance" label="%{getText('neptune.field.minimum.distance.3.10')}" size="5"/>
			 	</td>
		 	</tr>
		 	<tr>
			 	<td> 
			 	 <s:text name="neptune.field.minimum.time.3.15"></s:text>
			 	</td>
			 	<td>
			 	  <s:textfield name="validationParam.test3_15_MinimalTime" label="%{getText('neptune.field.minimum.time.3.15')}" size="5"/>
			 	</td>
		 	</tr>
		 	<tr>
			 	<td> 
			 	 <s:text name="neptune.field.minimum.time.3.16"></s:text>
			 	</td>
			 	<td>
			 	   <s:textfield name="validationParam.test3_16_3a_MinimalTime" label="%{getText('neptune.field.minimum.time.3.16')}" size="5"/>
			 	</td>
		 	</tr>
		 	<tr>
			 	<td> 
			 	 <s:text name="neptune.field.minimum.time.on.maximum.3.16c"></s:text>
			 	</td>
			 	<td>
			 	   <s:textfield name="validationParam.test3_16c_MinimalTime" label="%{getText('neptune.field.minimum.time.on.maximum.3.16c')}" size="5"/>/
				  	<s:textfield name="validationParam.test3_16c_MaximalTime" size="5" label="/"/>
			 	</td>
		 	</tr>
		 	<tr>
			 	<td> 
			 	 <s:text name="neptune.field.minimum.speed.on.maximum.3.21a"></s:text>
			 	</td>
			 	<td>
			 	<s:textfield name="validationParam.test3_21a_MinimalSpeed" label="%{getText('neptune.field.minimum.speed.on.maximum.3.21a')}" size="5"/>/
				<s:textfield name="validationParam.test3_21a_MaximalSpeed"  size="5" label="/"/>
			 	</td>
		 	</tr>
		 	<tr>
			 	<td> 
			 	  <s:text name="neptune.field.minimum.speed.on.maximum.3.21b"></s:text>
			 	</td>
			 	<td>
			 	<s:textfield name="validationParam.test3_21b_MinimalSpeed" label="%{getText('neptune.field.minimum.speed.on.maximum.3.21b')}" size="5"/>/
				<s:textfield name="validationParam.test3_21b_MaximalSpeed" size="5" label="/"/>
			 	</td>
		 	</tr>
		 	<tr>
			 	<td> 
			 	  <s:text name="neptune.field.minimum.speed.on.maximum.3.21c"></s:text>
			 	</td>
			 	<td>
			 	<s:textfield name="validationParam.test3_21c_MinimalSpeed" label="%{getText('neptune.field.minimum.speed.on.maximum.3.21c')}" size="5"/>/
				<s:textfield name="validationParam.test3_21c_MaximalSpeed" size="5" label="/"/>
			 	</td>
		 	</tr>
		 	<tr>
			 	<td> 
			 	 <s:text name="neptune.field.minimum.speed.on.maximum.3.21d"></s:text>
			 	</td>
			 	<td>
			 	<s:textfield name="validationParam.test3_21d_MinimalSpeed" label="%{getText('neptune.field.minimum.speed.on.maximum.3.21d')}" size="5"/>/
				<s:textfield name="validationParam.test3_21d_MaximalSpeed" size="5" label="/"/>
			 	</td>
		 	</tr> 	
		 	<tr>
			 	<td> 
			 	<s:text name="neptune.projection_reference.label"></s:text>
			 	</td>
			 	<td>
					<s:textfield name="validationParam.projection_reference" label="%{getTexneptune.projection_reference.label')}" size="5"/>
			 	</td>
		 	</tr>
	 	</table>
		
		 <br />	
		<s:submit action="defaultValue" name="defaultValue" value="%{getText('neptune.field.restore.default.value')}"/>
     	<s:submit action="validation" name="validation" value="%{getText('neptune.field.validation.sumit')}"/>
	</s:div>
	</div>
	</s:form>
</div>
