<%@ taglib prefix="s" uri="/struts-tags" %>
<title>Erreur</title>
<h3>Messages d'erreur</h3>
<s:actionerror />
<p>
  <s:property value="%{exception.message}"/>
</p>
<hr />
<h3>Details Techniques</h3>
<p>
  <s:property value="%{exceptionStack}"/>
</p> 