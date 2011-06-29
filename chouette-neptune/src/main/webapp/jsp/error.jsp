<%@ taglib prefix="s" uri="/struts-tags" %>
<title><s:text name="error.title"/></title>
<h3><s:text name="error.text"/></h3>
<s:actionerror />
<p>
  <s:property value="%{exception.message}"/>
</p>
<hr />
<h3><s:text name="error.details"/></h3>
<p>
  <s:property value="%{exceptionStack}"/>
</p> 