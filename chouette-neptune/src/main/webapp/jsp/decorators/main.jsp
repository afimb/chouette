<%@ taglib uri="http://www.opensymphony.com/sitemesh/decorator" prefix="decorator" %>
<%@ taglib uri="http://www.opensymphony.com/sitemesh/page" prefix="page" %>
<%@ taglib uri="/struts-tags" prefix="s"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
  "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
  <head>
    <%@ include file='/jsp/common/meta.jsp' %>
    <link rel="stylesheet" type="text/css" href="<s:url value='/css/chouette.css' includeParams='none'/>"/>
    <decorator:head />
    <title><s:text name="main.title"/><decorator:title default="Bienvenue" /></title>
  </head>
  <body>
    <div id="global">
      <%@ include file="/jsp/common/header.jsp" %>
      <div id="main">
        <%@ include file="/jsp/common/menu.jsp" %>
        <div id="help"></div>
        <div id="content">

          <decorator:body/>

        </div>
      </div>
      <%@ include file="/jsp/common/footer.jsp" %>
    </div>
  </body>
</html>