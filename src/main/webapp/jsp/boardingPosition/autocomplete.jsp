<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<ul>
  <s:iterator value="#request.boardingPositions" var="boardingPosition" >
    <li ><s:property value="name" /> (<s:property value="countryCode" />, <s:property value="streetName" />, <s:property value="objectId" /> )</li>
  </s:iterator>
</ul>