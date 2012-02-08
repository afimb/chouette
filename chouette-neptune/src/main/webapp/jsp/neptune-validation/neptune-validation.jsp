<%@ taglib prefix="s" uri="/struts-tags"%>

<title><s:text name="import.index.title" />
</title>
	<h4>
		<s:text name="neptune.description.announce"></s:text>
	</h4>

<s:url id="urlImportNeptuneValidation" action="execute"
	namespace="/neptune-validation" includeParams="none" />
<div style="width: 75%;">
	<s:form action="validation" namespace="/neptune-validation"
		method="post" theme="simple" enctype="multipart/form-data">
		<div class="panelDataSection">
			<s:text name="import.index.title" />
		</div>
		<div class="neptune-panel">
			<s:text name="fieldset.legend.import" />
			<s:file name="file" label="%{getText('action.browse')}" />
			<!--
		   <s:checkbox name="validate">Valider</s:checkbox>
		   <s:checkbox name="save">Sauvegarder</s:checkbox>
                   -->
			<s:submit action="validation" name="validation"
				value="%{getText('neptune.field.validation.sumit')}" />
		</div>
		<b><s:label value="%{report.getLocalizedMessage(getLocale())}" />
		</b>
		<s:div id="category1">
			<s:iterator value="%{report.items}" var="sheet" status="status">
				<div class="panelDataSection">
					<s:property value="getLocalizedMessage(getLocale())" />
				</div>
				<s:div cssClass="neptune-panel" id="%{status}">
					<s:iterator value="items" var="test" status="itemsStatus">
						<s:div cssClass="%{status}">
							<div>
								<!--s:a href="#-1" onclick="showIt('detail%{#status.index}_%{#itemsStatus.index}','showIt_%{#status.index}_%{#itemsStatus.index}');" 
						id="showIt_%{#status.index}_%{#itemsStatus.index}" title="%{getText('text.detail.show')}"-->
								<!--img src="<s:url value='/images/plus.png'/>" alt="%{getText('text.detail.show')}"/-->
								<!--/s:a-->
								<!--s:a href="#-1" onclick="hideIt('detail%{#status.index}_%{#itemsStatus.index}','hideIt_%{#status.index}_%{#itemsStatus.index}');" 
						id="hideIt_%{#status.index}_%{#itemsStatus.index}" title="%{getText('text.detail.hide')}"-->
								<!--img src="<s:url value='/images/moins.png'/>" alt="%{getText('text.detail.hide')}"/-->
								<!--/s:a-->
								<s:label value="%{report.order}.%{#sheet.order}.%{#test.order}" />
							</div>
							<s:div
								cssStyle="width: 700px; padding-left:100px; margin-top:-15px">
								<s:property value="getLocalizedMessage(getLocale())" />
							</s:div>
							<s:if
								test="%{getStatus().name() != 'OK' && getStatus().name() != 'UNCHECK' }">
								<s:div cssStyle="margin-left: 20px;">
								</s:div>
								<s:div cssClass="neptune-panel-inSide"
									id="detail%{#status.index}_%{#itemsStatus.index}">
									<!-- cssStyle="display:none;"-->
									<ol>
										<s:iterator value="items">
											<li><s:text name="validation.test.delimiter" /> <s:property
													value="getLocalizedMessage(getLocale())" /></li>
										</s:iterator>
									</ol>
								</s:div>
							</s:if>
						</s:div>
					</s:iterator>
				</s:div>
			</s:iterator>
		</s:div>
		<s:include value="/jsp/commun/messages.jsp" />
		<div class="panelDataSection">
			<s:text name="neptune.field.title" />
		</div>
		<div class="neptune-panel">
			<h4>
				<s:text name="neptune.field.units"></s:text>
			</h4>
			<s:div>
				<table>
					<tr>
						<td><s:text name="neptune.field.minimum.distance.3.1"></s:text>
						</td>
						<td><s:textfield
								name="validationParam.test3_1_MinimalDistance"
								id="test3_1_MinimalDistance" size="5" /></td>
					</tr>
					<tr>
						<td><s:text name="neptune.field.minimum.distance.3.2"></s:text>
						</td>
						<td><s:textfield
								name="validationParam.test3_2_MinimalDistance"
								id="test3_2_MinimalDistance" size="5" /></td>
					</tr>
					<tr>
						<td><s:text name="neptune.field.polygon.3.6"></s:text></td>
						<td><s:textfield name="validationParam.test3_2_PolygonPoints"
								size="63" /></td>
					</tr>
					<tr>
						<td><s:text
								name="neptune.field.minimum.distance.on.maximum.3.7"></s:text></td>
						<td><s:textfield
								name="validationParam.test3_7_MinimalDistance" size="5" />/ <s:textfield
								name="validationParam.test3_7_MaximalDistance" size="5" /></td>
					</tr>
					<tr>
						<td><s:text
								name="neptune.field.minimum.speed.on.maximum.3.8a"></s:text></td>
						<td><s:textfield name="validationParam.test3_8a_MinimalSpeed"
								label="%{getText('neptune.field.minimum.speed.on.maximum.3.8a')}"
								size="5" />/ <s:textfield
								name="validationParam.test3_8a_MaximalSpeed" size="5" label="/" />
						</td>
					</tr>
					<tr>
						<td><s:text
								name="neptune.field.minimum.speed.on.maximum.3.8b"></s:text></td>
						<td><s:textfield name="validationParam.test3_8b_MinimalSpeed"
								label="%{getText('neptune.field.minimum.speed.on.maximum.3.8b')}"
								size="5" />/ <s:textfield
								name="validationParam.test3_8b_MaximalSpeed" size="5" label="/" />
						</td>
					</tr>
					<tr>
						<td><s:text
								name="neptune.field.minimum.speed.on.maximum.3.8c"></s:text></td>
						<td><s:textfield name="validationParam.test3_8c_MinimalSpeed"
								label="%{getText('neptune.field.minimum.speed.on.maximum.3.8c')}"
								size="5" />/ <s:textfield
								name="validationParam.test3_8c_MaximalSpeed" size="5" label="/" />
						</td>
					</tr>
					<tr>
						<td><s:text
								name="neptune.field.minimum.speed.on.maximum.3.8d"></s:text></td>
						<td><s:textfield name="validationParam.test3_8d_MinimalSpeed"
								label="%{getText('neptune.field.minimum.speed.on.maximum.3.8d')}"
								size="5" />/ <s:textfield
								name="validationParam.test3_8d_MaximalSpeed" size="5" label="/" />
						</td>
					</tr>
					<tr>
						<td><s:text name="neptune.field.minimum.speed.on.maximum.3.9"></s:text>
						</td>
						<td><s:textfield name="validationParam.test3_9_MinimalSpeed"
								label="%{getText('neptune.field.minimum.speed.on.maximum.3.9')}"
								size="5" />/ <s:textfield
								name="validationParam.test3_9_MaximalSpeed" size="5" label="/" />
						</td>
					</tr>
					<tr>
						<td><s:text name="neptune.field.minimum.distance.3.10"></s:text>
						</td>
						<td><s:textfield
								name="validationParam.test3_10_MinimalDistance"
								label="%{getText('neptune.field.minimum.distance.3.10')}"
								size="5" /></td>
					</tr>
					<tr>
						<td><s:text name="neptune.field.minimum.time.3.15"></s:text>
						</td>
						<td><s:textfield name="validationParam.test3_15_MinimalTime"
								label="%{getText('neptune.field.minimum.time.3.15')}" size="5" />
						</td>
					</tr>
					<tr>
						<td><s:text name="neptune.field.maximum.time.3.16.1"></s:text>
						</td>
						<td><s:textfield
								name="validationParam.test3_16_1_MaximalTime"
								label="%{getText('neptune.field.maximum.time.3.16.1')}" size="5" />
						</td>
					</tr>
					<tr>
						<td><s:text name="neptune.field.maximum.time.3.16.3a"></s:text>
						</td>
						<td><s:textfield
								name="validationParam.test3_16_3a_MaximalTime"
								label="%{getText('neptune.field.maximum.time.3.16.3a')}"
								size="5" /></td>
					</tr>
					<tr>
						<td><s:text name="neptune.field.maximum.time.3.16.3b"></s:text>
						</td>
						<td><s:textfield
								name="validationParam.test3_16_3b_MaximalTime"
								label="%{getText('neptune.field.maximum.time.3.16.3b')}"
								size="5" /></td>
					</tr>
					<tr>
						<td><s:text
								name="neptune.field.minimum.speed.on.maximum.3.21a"></s:text></td>
						<td><s:textfield
								name="validationParam.test3_21a_MinimalSpeed"
								label="%{getText('neptune.field.minimum.speed.on.maximum.3.21a')}"
								size="5" />/ <s:textfield
								name="validationParam.test3_21a_MaximalSpeed" size="5" label="/" />
						</td>
					</tr>
					<tr>
						<td><s:text
								name="neptune.field.minimum.speed.on.maximum.3.21b"></s:text></td>
						<td><s:textfield
								name="validationParam.test3_21b_MinimalSpeed"
								label="%{getText('neptune.field.minimum.speed.on.maximum.3.21b')}"
								size="5" />/ <s:textfield
								name="validationParam.test3_21b_MaximalSpeed" size="5" label="/" />
						</td>
					</tr>
					<tr>
						<td><s:text
								name="neptune.field.minimum.speed.on.maximum.3.21c"></s:text></td>
						<td><s:textfield
								name="validationParam.test3_21c_MinimalSpeed"
								label="%{getText('neptune.field.minimum.speed.on.maximum.3.21c')}"
								size="5" />/ <s:textfield
								name="validationParam.test3_21c_MaximalSpeed" size="5" label="/" />
						</td>
					</tr>
					<tr>
						<td><s:text
								name="neptune.field.minimum.speed.on.maximum.3.21d"></s:text></td>
						<td><s:textfield
								name="validationParam.test3_21d_MinimalSpeed"
								label="%{getText('neptune.field.minimum.speed.on.maximum.3.21d')}"
								size="5" />/ <s:textfield
								name="validationParam.test3_21d_MaximalSpeed" size="5" label="/" />
						</td>
					</tr>
					<tr>
						<td><s:text name="neptune.projection_reference.label"></s:text>
						</td>
						<td><s:textfield name="validationParam.projection_reference"
								label="%{getTexneptune.projection_reference.label')}" size="5" />
						</td>
					</tr>
				</table>

				<br />
				<s:submit action="defaultValue" name="defaultValue"
					value="%{getText('neptune.field.restore.default.value')}" />
			</s:div>
		</div>
	</s:form>
	<h4>
		<s:text name="neptune.description.title"></s:text>
	</h4>

	<s:div cssStyle="margin-left: 40px;">
		<s:text name="Test1"></s:text><br/>
		<s:div cssStyle="width: 700px; padding-left:100px; margin-top:-15px">

			<a href="<s:url action='Sheet1.1'/>"> <s:text name="Test1_Sheet1" />
			</a><br />
		</s:div>

		<s:text name="Test2"></s:text><br/>
		<s:div cssStyle="width: 700px; padding-left:100px; margin-top:-15px">
			<a href="<s:url action='Sheet2.1'/>"> <s:text name="Test2_Sheet1" /></a><br/>
			<a href="<s:url action='Sheet2.2'/>"> <s:text name="Test2_Sheet2" /></a><br/>
			<a href="<s:url action='Sheet2.3'/>"> <s:text name="Test2_Sheet3" /></a><br/>
			<a href="<s:url action='Sheet2.4'/>"> <s:text name="Test2_Sheet4" /></a><br/>
			<a href="<s:url action='Sheet2.5'/>"> <s:text name="Test2_Sheet5" /></a><br/>
			<a href="<s:url action='Sheet2.6'/>"> <s:text name="Test2_Sheet6" /></a><br/>
			<a href="<s:url action='Sheet2.7'/>"> <s:text name="Test2_Sheet7" /></a><br/>
			<a href="<s:url action='Sheet2.8'/>"> <s:text name="Test2_Sheet8" /></a><br/>
			<a href="<s:url action='Sheet2.9'/>"> <s:text name="Test2_Sheet9" /></a><br/>
			<a href="<s:url action='Sheet2.10'/>"> <s:text name="Test2_Sheet10" /></a><br/>
			<a href="<s:url action='Sheet2.11'/>"> <s:text name="Test2_Sheet11" /></a><br/>
			<a href="<s:url action='Sheet2.12'/>"> <s:text name="Test2_Sheet12" /></a><br/>
			<a href="<s:url action='Sheet2.13'/>"> <s:text name="Test2_Sheet13" /></a><br/>
			<a href="<s:url action='Sheet2.14'/>"> <s:text name="Test2_Sheet14" /></a><br/>
			<a href="<s:url action='Sheet2.15'/>"> <s:text name="Test2_Sheet15" /></a><br/>
			<a href="<s:url action='Sheet2.16'/>"> <s:text name="Test2_Sheet16" /></a><br/>
			<a href="<s:url action='Sheet2.17'/>"> <s:text name="Test2_Sheet17" /></a><br/>
			<a href="<s:url action='Sheet2.18'/>"> <s:text name="Test2_Sheet18" /></a><br/>
			<a href="<s:url action='Sheet2.19'/>"> <s:text name="Test2_Sheet19" /></a><br/>
			<a href="<s:url action='Sheet2.20'/>"> <s:text name="Test2_Sheet20" /></a><br/>
			<a href="<s:url action='Sheet2.21'/>"> <s:text name="Test2_Sheet21" /></a><br/>
			<a href="<s:url action='Sheet2.22'/>"> <s:text name="Test2_Sheet22" /></a><br/>
			<a href="<s:url action='Sheet2.23'/>"> <s:text name="Test2_Sheet23" /></a><br/>
			<a href="<s:url action='Sheet2.24'/>"> <s:text name="Test2_Sheet24" /></a><br/>
			<a href="<s:url action='Sheet2.25'/>"> <s:text name="Test2_Sheet25" /></a><br/>
			<a href="<s:url action='Sheet2.26'/>"> <s:text name="Test2_Sheet26" /></a><br/>
			<a href="<s:url action='Sheet2.27'/>"> <s:text name="Test2_Sheet27" /></a><br/>
			<a href="<s:url action='Sheet2.28'/>"> <s:text name="Test2_Sheet28" /></a><br/>
		</s:div>
		<s:text name="Test3"></s:text><br/>
		<s:div cssStyle="width: 700px; padding-left:100px; margin-top:-15px">
			<a href="<s:url action='Sheet3.1'/>"> <s:text name="Test3_Sheet1" /></a><br/>
			<a href="<s:url action='Sheet3.2'/>"> <s:text name="Test3_Sheet2" /></a><br/>
			<a href="<s:url action='Sheet3.3'/>"> <s:text name="Test3_Sheet3" /></a><br/>
			<a href="<s:url action='Sheet3.4'/>"> <s:text name="Test3_Sheet4" /></a><br/>
			<a href="<s:url action='Sheet3.5'/>"> <s:text name="Test3_Sheet5" /></a><br/>
			<a href="<s:url action='Sheet3.6'/>"> <s:text name="Test3_Sheet6" /></a><br/>
			<a href="<s:url action='Sheet3.7'/>"> <s:text name="Test3_Sheet7" /></a><br/>
			<a href="<s:url action='Sheet3.8'/>"> <s:text name="Test3_Sheet8" /></a><br/>
			<a href="<s:url action='Sheet3.9'/>"> <s:text name="Test3_Sheet9" /></a><br/>
			<a href="<s:url action='Sheet3.10'/>"> <s:text name="Test3_Sheet10" /></a><br/>
			<a href="<s:url action='Sheet3.11'/>"> <s:text name="Test3_Sheet11" /></a><br/>
			<a href="<s:url action='Sheet3.12'/>"> <s:text name="Test3_Sheet12" /></a><br/>
			<%-- <a href="<s:url action='Sheet3.13'/>"> <s:text name="Test3_Sheet13" /></a><br/> --%>
			<%-- <a href="<s:url action='Sheet3.14'/>"> <s:text name="Test3_Sheet14" /></a><br/> --%>
			<a href="<s:url action='Sheet3.15'/>"> <s:text name="Test3_Sheet15" /></a><br/>
			<a href="<s:url action='Sheet3.16'/>"> <s:text name="Test3_Sheet16" /></a><br/>
			<a href="<s:url action='Sheet3.17'/>"> <s:text name="Test3_Sheet17" /></a><br/>
			<a href="<s:url action='Sheet3.18'/>"> <s:text name="Test3_Sheet18" /></a><br/>
			<a href="<s:url action='Sheet3.19'/>"> <s:text name="Test3_Sheet19" /></a><br/>
			<a href="<s:url action='Sheet3.20'/>"> <s:text name="Test3_Sheet20" /></a><br/>
			<a href="<s:url action='Sheet3.21'/>"> <s:text name="Test3_Sheet21" /></a><br/>
		</s:div>
	</s:div>

</div>
