<%@ taglib prefix="s" uri="/struts-tags" %>
<%-- Titre et barre de navigation --%>	
<title><s:text name="reporting.title" /> <s:property value="exchangeFormat"/></title>

<!-- main mother action in breadcrumb -->
<s:url id="urlLinesComparisonReport" action="index" includeParams="none"/>
<s:property value="filAriane.addElementFilAriane(getText('index.title'), '', #urlLinesComparisonReport)"/>
<div class="panelData">
	<s:property value="filAriane.texteFilAriane" escape="false"/>
</div>

<s:include value="/jsp/commun/messages.jsp" />

<%-- Report backup --%>
<!-- <div style="width: 500px;" ><h2><s:text name="reporting.subtitle.availableActions"></s:text></h2>
		<s:form id="comparisonResultActionsForm" enctype="multipart/form-data" method="POST">
			<span>
				<s:submit 
					theme="simple" action="downloadReport" 
					name="saveReport" value="%{getText('reporting.submit.saveReport')}">					
				</s:submit>
			</span>
		</s:form>
</div>-->

<!-- Comparison Report -->
<s:if test="#request.comparisonReport != null">
	<div style="width: 500px;">
		<h2><s:text name="reporting.subtitle.report"></s:text></h2>
			<s:iterator id="reportItem" value="#request.comparisonReport">
				<fieldset >
					<legend> <b><s:property value="name" /> : <i><s:property value="state" /></i></b> </legend>
						<p><s:text name="reporting.info.sourceId" /><s:property value="sourceId" /></p>
						<p><s:text name="reporting.info.targetId" /><s:property value="targetId" /></p>								
						<s:if test="attributesStates.size > 0">
							<p><s:text name="reporting.info.attributesStates" /></p>
								<s:iterator id="dataObjectState" value="attributesStates">
									<p style="margin-left: 0.5em">
									<s:property value="name" /> : <s:property value="identical" /></p>
								</s:iterator>
						</s:if>
				</fieldset>
			</s:iterator>
		</div>
	</s:if>