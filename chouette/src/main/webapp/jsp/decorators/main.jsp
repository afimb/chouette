<%@taglib prefix="decorator" uri="http://www.opensymphony.com/sitemesh/decorator" %>
<%@taglib prefix="page" uri="http://www.opensymphony.com/sitemesh/page" %>
<%@taglib prefix="s" uri="/struts-tags" %>
<html>
  <head>
    <s:include value="/jsp/commun/meta.jsp"/>
    <title>Chouette - <decorator:title default="Bienvenue" /></title>
    <link rel="stylesheet" type="text/css" href="<s:url value='/css/chouette_ninoxe.css' includeParams='none'/>"/>
    <script language="JavaScript" type="text/javascript" src="<s:url value='/js/chouette.js' includeParams='none'/>" ></script>
    <script language="JavaScript" type="text/javascript" src="<s:url value='/js/prototype/prototype.js' includeParams='none'/>" ></script>
    <decorator:head />
  </head>
  <body>
  	<div id="global">
  		<s:include value="/jsp/commun/header.jsp"/>
		<div id="main">  	
   			<s:include value="/jsp/commun/menu.jsp"/>
   			<div id="help"></div>
   			<div id="content">
				<s:include value="/jsp/commun/messages.jsp"/>
   				<decorator:body />
   			</div>	
   		</div>
   		<s:include value="/jsp/commun/footer.jsp"/>
  	</div>
  </body>
</html>