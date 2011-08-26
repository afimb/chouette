<%@ taglib uri="http://www.opensymphony.com/sitemesh/decorator" prefix="decorator" %>
<%@ taglib uri="http://www.opensymphony.com/sitemesh/page" prefix="page" %>
<%@ taglib uri="/struts-tags" prefix="s"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
  "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
  <head>
    <%@ include file='/jsp/commun/meta.jsp' %>
    <link rel="stylesheet" type="text/css" href="<s:url value='/css/map.css' includeParams='none'/>"/>
    <link rel="stylesheet" type="text/css" href="<s:url value='/css/chouette_ninoxe.css' includeParams='none'/>"/>
    <script language="JavaScript" type="text/javascript" src="<s:url value='/js/prototype/prototype.js' includeParams='none'/>" ></script>
    <script language="JavaScript" type="text/javascript" src="<s:url value='/js/cookie.js' includeParams='none'/>" ></script>
    <script language="JavaScript" type="text/javascript" src="<s:url value='/js/chouette.js' includeParams='none'/>" ></script>
    <decorator:head />
    <title><s:text name="main.title"/><decorator:title default="Bienvenue" /></title>
  </head>
  <body>
    <div id="global">
      <%@ include file="/jsp/commun/neptune-validation-header.jsp" %>
      <div id="main">
          <br />
<%--Neptune validation --%>
<a href="<%=request.getContextPath()%>/neptune-validation/execute.action" style="font-size:24px;">
    <!--b>Validation Neptune(&#x2646;)</b-->
    <b><s:text name="text.neptune.import"/>(&#x2646;)</b>
</a>
<br/><br/>
      <div id="menu">
		<div>	<%-- Connection --%>
      	<s:url id="urlConnexionMenu" action ="list" namespace="/line" includeParams="none"/>
			<s:a href="%{urlConnexionMenu}" id="connexionMenu"><s:text name="text.connexion"/></s:a>
		</div>
			</div>
        <div id="help"></div>
        <div id="content">

          <decorator:body/>

        </div>
      </div>
      <%@ include file="/jsp/commun/footer.jsp" %>
    </div>
  </body>
</html>