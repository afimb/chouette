<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<ul>
  <s:iterator value="#request.boardingPositions" var="boardingPosition" >
    <li id="${boardingPosition.id}"><s:property value="name" /> (<s:property value="countryCode" /><s:if test='!#boardingPosition.countryCode.equals("")'>, </s:if><s:property value="streetName" /><s:if test='!#boardingPosition.streetName.equals("")'>, </s:if><s:property value="objectId" /> )</li>
  </s:iterator>
</ul>