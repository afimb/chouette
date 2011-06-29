<%@ taglib prefix="s" uri="/struts-tags" %>
<s:include value="/jsp/commun/jscalendar.jsp"></s:include>
<%-- Titre et barre de navigation --%>	
<title><s:text name="massiveExport.index.title" /></title>
<s:url id="urlMassiveExport" action="list" namespace="/massiveExport" includeParams="none"/>
<s:property value="filAriane.addElementFilAriane(getText('massiveExport.index.title'), '', #urlMassiveExport)"/>
<div class="panelData">
  <s:property value="filAriane.texteFilAriane" escape="false"/>
</div>

<s:include value="/jsp/commun/messages.jsp" />

<%-- Import de différents fichiers --%>
<div>
  <FIELDSET align="center" style="width: 500px;">
    <LEGEND><b><s:text name="fieldset.legend.massiveExport.network"/></b></LEGEND>
    <s:form id="networkMassiveExportForm" action="exportNetwork" namespace="/massiveExport" enctype="multipart/form-data" method="POST">
      <s:hidden name="operationMode" value="STORE" />
      <s:select	name="networkId"
                list="test"
                listKey="id"
                listValue="name"  />

      <s:textfield key="startDate"
                   id="startDate"
                   maxlength="10"/>
      <script type="text/javascript">
        <!--//
        Calendar.setup({
          singleClick : true,
          firstDay : 1,
          inputField : "startDate",	// ID of the input field
          ifFormat : "%d/%m/%Y"	// the date format
        }
      );
        //-->
      </script>

      <s:textfield key="endDate"
                   id="endDate"
                   maxlength="10"/>
      <script type="text/javascript">
        <!--//
        Calendar.setup({
          singleClick : true,
          firstDay : 1,
          inputField : "endDate",	// ID of the input field
          ifFormat : "%d/%m/%Y"	// the date format
        }
      );
        //-->
      </script>
      <s:checkbox name="excludeConnectionLinks"
                  label="%{getText('field.checkbox.massiveExport.excludeConnectionLinks')}"/>
      <s:submit value="%{getText('submit.massiveExport.network')}" formId="networkMassiveExportForm"/>
    </s:form>
  </FIELDSET>
  <br><br>
  <ul>
    <s:iterator value="exportFiles" var="file" status="test">
      <s:url id="fileUrl" namespace="/" action="downloadFile">
        <s:param name="fileName" value="#file.name"/>
        <s:param name="previousAction" value="%{'MassiveExportAction'}"/>
      </s:url>
      <s:if test="#file.file">
        <li><s:a  href="%{fileUrl}"><s:property value="name"/></s:a></li>
      </s:if>
    </s:iterator>
  </ul>
</div>	
