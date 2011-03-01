<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags" %>

<title><s:text name="validation.result.title" /></title>
<s:url id="urlImportNeptuneValidation" action="execute" namespace="/neptune-validation" includeParams="none"/>
<s:include value="/jsp/commun/messages.jsp" />
	<div style="width: 90%;">
	 <div class="panelDataSection"><s:text name="validation.result.title"/></div>
	  	<div class="neptune-panel">
	  	<br />
	  	<s:label label="%{getText('validation.title')}" value="%{#session.fileFileName}"/>
		<table border="0" cellpadding="20">
			<tr>
				<s:iterator value="countMap">
					<td>						
						<img src="<s:url value='/images/%{key}.png'/>" alt="%{key}" />					
						<s:property value="value"/>
		 				<s:property value="%{getText(key)}"/>
		 				<br />
		 				<s:a href="#" onclick="showThem('%{key}');" id="%{key}_show">
							<s:text name="text.detail.show" />
						</s:a> /
						<s:a href="#" onclick="hideThem('%{key}');" id="%{key}_hide">
							<s:text name="text.detail.hide" />
						</s:a>	
	   				</td>	
 				</s:iterator>
			</tr>
		</table>
		<s:text name="text.detail.title" />:
		<s:a href="#" onclick="showThem('neptune-panel-inSide')" id="detail">
			<s:text name="text.detail.show.all" />
		</s:a> /
		<s:a href="#" onclick="hideThem('neptune-panel-inSide')" id="hide">
			<s:text name="text.detail.hide.all" />
		</s:a>	
	</div>
	<br />
		<s:iterator value="reportValidation.items" id="categorie">
		 	<b><s:property value="getLocalizedMessage(getLocale())" /></b>
		 	
		 	<div id="panelValidation" style="margin-left: 20px;">
				<s:iterator value="items" status="status">
					<div class="panelDataSection"><s:property value="getLocalizedMessage(getLocale())"/></div>
					      <s:div cssClass="neptune-panel" id="%{status}">
					     	 <br />
						     <s:iterator value="items" id="test" status="itemsStatus">
							     	<s:div cssClass="%{status}" cssStyle="display:%{status} == 'OK' ? none : block;">
							     		<s:div cssStyle="width: 800px;"><s:property value="getLocalizedMessage(getLocale())"/></s:div>
							     	
							     	<br />
							     	<s:div cssClass="detail">
							     	
							     	<s:a href="#" onclick="showIt('detail_%{#status.index}_%{#itemsStatus.index}','showIt_%{#itemsStatus.index}');" 
							     		id="showIt_%{#itemsStatus.index}" title="%{getText('text.detail.show')}">
											<img src="<s:url value='/images/plus.png'/>" alt="%{getText('text.detail.show')}" />	
									</s:a> 
									<s:a href="#" onclick="hideIt('detail_%{#status.index}_%{#itemsStatus.index}','hideIt_%{#itemsStatus.index}');" 
										id="hideIt_%{#itemsStatus.index}" title="%{getText('text.detail.hide')}">
											<img src="<s:url value='/images/moins.png'/>" alt="%{getText('text.detail.hide')}" />	
									</s:a>	
									</s:div>
								
							     	<s:div cssClass="neptune-panel-inSide" id="detail_%{#status.index}_%{#itemsStatus.index}" cssStyle="display:none;">
								     	 <s:iterator value="items">
								     		<s:text name="validation.test.delimiter" />
								     		 <s:property value="getLocalizedMessage(getLocale())"/>
								     		<br />
								     	</s:iterator>
							     	</s:div>
							     	</s:div>
						     </s:iterator>
						     <br />
			  			</s:div>
				</s:iterator>
				<br />	
  			</div>
		 </s:iterator>
	
  	<br />
  	<div class="panelDataSection"><s:text name="message.parameter.title"/>
		</div>
	      <div class="neptune-panel">
	      <br />
	     <s:label label="%{getText('neptune.field.minimum.distance.3.1')}" value="%{#session.validationParam.test3_1_MinimalDistance}" /><br />
	     <s:label label="%{getText('neptune.field.minimum.distance.3.2')}" value="%{#session.validationParam.test3_2_MinimalDistance}"/><br />
	 	<s:label label="%{getText('neptune.field.polygon.3.6')}" value="%{#session.polygonCoordinatesAsString}"/><br />
	 	<s:label value="%{#session.validationParam.test3_7_MinimalDistance}" label="%{getText('neptune.field.minimum.distance.on.maximum.3.7')}" />
	 	<s:label value="%{#session.validationParam.test3_7_MaximalDistance}" label="/" />
	 	<br />
	 	
	 	<s:label value="%{#session.validationParam.test3_8a_MinimalSpeed}" label="%{getText('neptune.field.minimum.speed.on.maximum.3.8a')}" />
	 	<s:label value="%{#session.validationParam.test3_8a_MaximalSpeed}" label="/" />
	 	<br />
	 	<s:label value="%{#session.validationParam.test3_8b_MinimalSpeed}" label="%{getText('neptune.field.minimum.speed.on.maximum.3.8b')}" />
	 	<s:label value="%{#session.validationParam.test3_8b_MaximalSpeed}" label="/" />
	 	<br />
	 	<s:label value="%{#session.validationParam.test3_8c_MinimalSpeed}" label="%{getText('neptune.field.minimum.speed.on.maximum.3.8c')}" />
	 	<s:label value="%{#session.validationParam.test3_8c_MaximalSpeed}" label="/" />
	 	<br />
	 	<s:label value="%{#session.validationParam.test3_8d_MinimalSpeed}" label="%{getText('neptune.field.minimum.speed.on.maximum.3.8d')}" />
	 	<s:label value="%{#session.validationParam.test3_8d_MaximalSpeed}" label="/" />
	 	<br />
	 	<s:label value="%{#session.validationParam.test3_9_MinimalSpeed}" label="%{getText('neptune.field.minimum.speed.on.maximum.3.9')}" />
	 	<s:label value="%{#session.validationParam.test3_9_MaximalSpeed}" label="/" />
	 	<br />
	 	<s:label value="%{#session.validationParamtest3_10_MinimalDistance}" label="%{getText('neptune.field.minimum.distance.3.10')}" /><br />
	 	<s:label value="%{#session.validationParam.test3_16c_MinimalTime}" label="%{getText('neptune.field.minimum.time.on.maximum.3.16c')}" />
	 	<s:label value="%{#session.validationParam.test3_16c_MaximalTime}" label="/" />
	 	<br />
	 	
  		</div>
	
</div>
 <script type="text/javascript">

 
 function showIt(idToShow, idOrigin) {
    $(idToShow).show();
    //$(idOrigin).hide();
  }
 
 function hideIt(idTohide, idOrigin) {
	    $(idTohide).hide();
	    //$(idOrigin).show();
	  }
 
 function showThem(cssClass){ 
	 for (var index = 0; index < $$('div[class='+cssClass+']').length; ++index) {
		  var item = $$('div[class='+cssClass+']')[index];
		  item.show();
		}	 
 }
 
 function hideThem(cssClass){
	 for (var index = 0; index < $$('div[class='+cssClass+']').length; ++index) {
		  var item = $$('div[class='+cssClass+']')[index];
		  item.hide();
		}		
 }
 
 function changeImage(element) { 	 
	  element.src = (element.src == "<s:url value='/images/plus.png'/>") ? 
			  "<s:url value='/images/moins.png'/>" : "<s:url value='/images/plus.png'/>"; 
	} 
 </script> 