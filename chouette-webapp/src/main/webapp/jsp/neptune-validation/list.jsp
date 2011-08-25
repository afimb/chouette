<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags" %>

<title><s:text name="validation.result.title" /></title>
<s:url id="urlImportNeptuneValidation" action="execute" namespace="/neptune-validation" includeParams="get"/>
<s:include value="/jsp/commun/messages.jsp" />
<div style="width: 90%;">
    <div class="panelDataSection"><s:text name="validation.result.title"/></div>
    <div class="neptune-panel">
        <s:label label="%{getText('validation.title')}" value="%{#session.fileFileName}"/>
        <br /><br />
        <s:text name="text.save" />
        <table border="0" cellpadding="20">
            <tr>
                <s:iterator value="countMap">
                    <td> 						
                        <img src="<s:url value='/images/%{key}.png'/>" alt="%{key}" />					
                        <s:property value="value"/>
                        <s:property value="%{getText(key)}"/>
                        <!--br /-->
                        <!--s:a href="#" onclick="showThem('%{key}');" id="%{key}_show"-->
                            <!--s:text name="text.detail.show" /-->
                        <!--/s:a> / -->
                        <!--s:a href="#" onclick="hideThem('%{key}');" id="%{key}_hide"-->
                            <!--s:text name="text.detail.hide" /-->
                        <!--/s:a-->	
                    </td>	
                </s:iterator>
            </tr>
        </table>
        <s:text name="text.detail.title" />:
        <!--s:a href="#" onclick="showThem('neptune-panel-inSide')" id="detail"-->
            <!--s:text name="text.detail.show.all" /-->
        <!--/s:a> / -->
        <!--s:a href="#" onclick="hideThem('neptune-panel-inSide')" id="hide"-->
            <!--s:text name="text.detail.hide.all" /-->
        <!--/s:a-->	
    </div>
    <br />
    <!--s:a href="#-1" onclick="showIt('category1','show_Itcategory1');" 
         id="showIt_category1}" title="%{getText('text.detail.show')}"-->
        <!--img src="<s:url value='/images/plus.png'/>" alt="%{getText('text.detail.show')}" /-->	
    <!--/s:a--> 
    <!--s:a href="#-1" onclick="hideIt('category1','hideIt_category1');" 
         id="hideIt_category1" title="%{getText('text.detail.hide')}"-->
        <!--img src="<s:url value='/images/moins.png'/>" alt="%{getText('text.detail.hide')}" /-->	
    <!--/s:a-->	
    <b><s:label value="%{#session.report.getLocalizedMessage(getLocale())}"/></b>
    <s:div cssStyle="margin-left: 40px;" id="category1">
        <s:iterator value="%{#session.report.items}" var="sheet" status="status">
            <div class="panelDataSection"><s:property value="getLocalizedMessage(getLocale())"/></div>
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
                            <s:label value="%{#session.report.order}.%{#sheet.order}.%{#test.order}" />
                        </div>
                        <s:div cssStyle="width: 700px; padding-left:100px; margin-top:-15px">
                            <a href="<s:url action='Sheet%{#sheet.order}.%{#test.order}'/>">
                                <s:property value="getLocalizedMessage(getLocale())"/>
                            </a>
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
    <br />
    <s:iterator value="reportValidation.items" id="categorie" status="category" var="categorie">
        <!--s:a href="#-1" onclick="showIt('%{#category.index}','show_It%{#category.index}');" 
             id="show_It%{#category.index}" title="%{getText('text.detail.show')}"-->
            <!--img src="<s:url value='/images/plus.png'/>" alt="%{getText('text.detail.show')}" /-->	
        <!--/s:a--> 
        <!--s:a href="#-1" onclick="hideIt('%{#category.index}','hideIt_%{#category.index}');" 
             id="hideIt_%{#category.index}" title="%{getText('text.detail.hide')}"-->
            <!--img src="<s:url value='/images/moins.png'/>" alt="%{getText('text.detail.hide')}" /-->	
        <!--/s:a-->	
        <b><s:property value="getLocalizedMessage(getLocale())" /></b>

        <s:div cssStyle="margin-left: 40px;" id="%{#category.index}">
            <s:iterator value="items" status="status" var="sheet">
                <div class="panelDataSection"><s:property value="getLocalizedMessage(getLocale())"/></div>
                <s:div cssClass="neptune-panel" id="%{status}">
                    <s:iterator value="items" id="test" status="itemsStatus" var="test">
                        <s:div cssClass="%{status}">
                            <div>
                                <!--s:a href="#-1" onclick="showIt('detail_%{#category.index}_%{#status.index}_%{#itemsStatus.index}','showIt_%{#itemsStatus.index}');" 
                                     id="showIt_%{itemsStatus.index}" title="%{getText('text.detail.show')}"-->
                                    <!--img src="<s:url value='/images/plus.png'/>" alt="%{getText('text.detail.show')}" /-->	
                                <!--/s:a--> 
                                <!--s:a href="#-1" onclick="hideIt('detail_%{#category.index}_%{#status.index}_%{#itemsStatus.index}','hideIt_%{#itemsStatus.index}');" 
                                     id="hideIt_%{#itemsStatus.index}" title="%{getText('text.detail.hide')}"-->
                                    <!--img src="<s:url value='/images/moins.png'/>" alt="%{getText('text.detail.hide')}" /-->	
                                <!--/s:a-->
                                <s:label value="%{#categorie.order}.%{#sheet.order}.%{#test.order}" />
                            </div>
                            <s:div cssStyle="width: 700px; padding-left:100px; margin-top:-15px">
                                <a href="<s:url action='Sheet%{#categorie.order}.%{#sheet.order}'/>">
                                    <s:property value="getLocalizedMessage(getLocale())"/>
                                </a>
                            </s:div>
                            <s:if test="%{getStatus().name() != 'OK' && getStatus().name() != 'UNCHECK' }">
                                <s:div cssClass="detail">

                                </s:div>
                                <s:div cssClass="neptune-panel-inSide" id="detail_%{#category.index}_%{#status.index}_%{#itemsStatus.index}" ><!--cssStyle="display:none;"-->
                                    <ol>
                                        <s:iterator value="items">
                                            <li>
                                                <s:text name="validation.test.delimiter" />
                                                <s:property value="getLocalizedMessage(getLocale())"/>
                                            </li>
                                            <br />
                                        </s:iterator>
                                    </ol>
                                </s:div>
                            </s:if>
                        </s:div>
                    </s:iterator>
                </s:div>
            </s:iterator>
            <br />	
        </s:div>
        <br />
    </s:iterator>
    <div class="panelDataSection"><s:text name="message.parameter.title"/>
    </div>
    <div class="neptune-panel">
        <s:label label="%{getText('neptune.field.minimum.distance.3.1')}" value="%{validationParam.test3_1_MinimalDistance}" /><br />
        <s:label label="%{getText('neptune.field.minimum.distance.3.2')}" value="%{validationParam.test3_2_MinimalDistance}"/><br />
        <s:label label="%{getText('neptune.field.polygon.3.6')}" value="%{validationParam.test3_2_PolygonPoints}"/><br />
        <s:label value="%{validationParam.test3_7_MinimalDistance}" label="%{getText('neptune.field.minimum.distance.on.maximum.3.7')}" />
        <s:label value="%{validationParam.test3_7_MaximalDistance}" label="/" />
        <br />

        <s:label value="%{validationParam.test3_8a_MinimalSpeed}" label="%{getText('neptune.field.minimum.speed.on.maximum.3.8a')}" />
        <s:label value="%{validationParam.test3_8a_MaximalSpeed}" label="/" />
        <br />
        <s:label value="%{validationParam.test3_8b_MinimalSpeed}" label="%{getText('neptune.field.minimum.speed.on.maximum.3.8b')}" />
        <s:label value="%{validationParam.test3_8b_MaximalSpeed}" label="/" />
        <br />
        <s:label value="%{validationParam.test3_8c_MinimalSpeed}" label="%{getText('neptune.field.minimum.speed.on.maximum.3.8c')}" />
        <s:label value="%{validationParam.test3_8c_MaximalSpeed}" label="/" />
        <br />
        <s:label value="%{validationParam.test3_8d_MinimalSpeed}" label="%{getText('neptune.field.minimum.speed.on.maximum.3.8d')}" />
        <s:label value="%{validationParam.test3_8d_MaximalSpeed}" label="/" />
        <br />
        <s:label value="%{validationParam.test3_9_MinimalSpeed}" label="%{getText('neptune.field.minimum.speed.on.maximum.3.9')}" />
        <s:label value="%{validationParam.test3_9_MaximalSpeed}" label="/" />
        <br />
        <s:label value="%{validationParam.test3_10_MinimalDistance}" label="%{getText('neptune.field.minimum.distance.3.10')}" /><br />
        <s:label value="%{validationParam.test3_15_MinimalTime}" label="%{getText('neptune.field.minimum.time.3.15')}" /><br />
        <s:label value="%{validationParam.test3_16_3a_MinimalTime}" label="%{getText('neptune.field.minimum.time.3.16')}" /><br />

        <s:label value="%{validationParam.test3_16c_MinimalTime}" label="%{getText('neptune.field.minimum.time.on.maximum.3.16c')}" />
        <s:label value="%{validationParam.test3_16c_MaximalTime}" label="/" /><br />

        <s:label value="%{validationParam.test3_21a_MinimalSpeed}" label="%{getText('neptune.field.minimum.speed.on.maximum.3.21a')}" />
        <s:label value="%{validationParam.test3_21a_MaximalSpeed}" label="/" />
        <br />
        <s:label value="%{validationParam.test3_21b_MinimalSpeed}" label="%{getText('neptune.field.minimum.speed.on.maximum.3.21b')}" />
        <s:label value="%{validationParam.test3_21b_MaximalSpeed}" label="/" />
        <br />
        <s:label value="%{validationParam.test3_21c_MinimalSpeed}" label="%{getText('neptune.field.minimum.speed.on.maximum.3.21c')}" />
        <s:label value="%{validationParam.test3_21c_MaximalSpeed}" label="/" />
        <br />
        <s:label value="%{validationParam.test3_21d_MinimalSpeed}" label="%{getText('neptune.field.minimum.speed.on.maximum.3.21d')}" />
        <s:label value="%{validationParam.test3_21d_MaximalSpeed}" label="/" />
        <br />
        <s:label value="%{validationParam.projection_reference}" label="%{getText('neptune.projection_reference.label')}"/>


    </div>

</div> 