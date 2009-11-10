<%--
 % This is the main decorator for all Chouette pages.
 % It includes standard caching, style sheet, header, footer and copyright notice.
--%>
<%@ taglib uri="http://www.opensymphony.com/sitemesh/decorator" prefix="decorator" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@taglib prefix="s" uri="/struts-tags" %>
<html>
  <head>
    <%@ include file='/jsp/commun/meta.jsp' %>
    <title>Chouette - <decorator:title default="Bienvenue" /></title>
    <decorator:head />
    <link rel="stylesheet" type="text/css" href="<s:url value='/css/chouette_ninoxe.css' includeParams='none'/>"/>
    <script language="JavaScript" type="text/javascript" src="<s:url value='/js/chouette.js' includeParams='none'/>" ></script>
    <script language="JavaScript" type="text/javascript" src="<s:url value='/js/prototype/prototype.js' includeParams='none'/>" ></script>
  </head>
  <body>
  	<div id="global">
  		<%@ include file="/jsp/commun/header.jsp" %>
		<div id="main">
   			<%@ include file="/jsp/commun/menu.jsp" %>
   			<div id="help"></div>
   			<div id="content">
          <%@ include file="/jsp/commun/messages.jsp" %>
   				<decorator:body />
   			</div>
   		</div>
   		<%@ include file="/jsp/commun/footer.jsp" %>
  	</div>
  </body>
</html>