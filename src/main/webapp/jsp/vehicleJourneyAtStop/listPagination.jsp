<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>

<table id="pagination">
  <thead>
    <tr>
      <th><span style="color:#2700ec; font-weight:bold">Ch</span></th>
      <s:iterator value="pagination.pages" status="rangPage" id="page">
        <th>
          <s:if test="#page.intValue() == pagination.numeroPage">
            <span style="color:red">o</span>
          </s:if>
          <s:else>
            <span style="color:#fffc16">o</span>
          </s:else>
        </th>
      </s:iterator>
      <th><span style="color:#2700ec">ue</span><span style="color:green">tt</span><span style="color:red">e</span></th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td>
        <s:url id="pagePrecedente" action="list" namespace="/vehicleJourneyAtStop">
          <s:param name="seuilDateDepartCourse">
            <s:if test="seuilDateDepartCourse != null">
              <s:date name="seuilDateDepartCourse" format="HH:mm"/>
            </s:if>
          </s:param>
          <s:param name="idTableauMarche" value="%{idTableauMarche}"></s:param>
          <s:param name="page" value="%{pagination.numeroPagePrecedente}" />
          <s:param name="idItineraire" value="%{idItineraire}" />
          <s:param name="idLigne" value="%{idLigne}" />
        </s:url>
        <s:a href="%{pagePrecedente}"><s:text name="pagination.precedent"/></s:a>&nbsp;&nbsp;
      </td>
      <s:iterator value="pagination.pages" status="rangPage" id="page">
        <td>
          <s:url id="pageIterateur" action="list" namespace="/vehicleJourneyAtStop">
            <s:param name="seuilDateDepartCourse">
              <s:if test="seuilDateDepartCourse != null">
                <s:date name="seuilDateDepartCourse" format="HH:mm"/>
              </s:if>
            </s:param>
            <s:param name="idTableauMarche" value="%{idTableauMarche}"></s:param>
            <s:param name="page" value="#page.intValue()" />
            <s:param name="idItineraire" value="%{idItineraire}" />
            <s:param name="idLigne" value="%{idLigne}" />
          </s:url>
          <s:if test="#page.intValue() == pagination.numeroPage">
            <s:a href="%{pageIterateur}"><span style="color:red"><s:property value="#page.intValue()"/></span></s:a>&nbsp;&nbsp;
          </s:if>
          <s:else>
            <s:a href="%{pageIterateur}"><s:property value="#page.intValue()"/></s:a>&nbsp;&nbsp;
          </s:else>
        </td>
      </s:iterator>
      <td>
        <s:url id="pageSuivante" action="list" namespace="/vehicleJourneyAtStop">
          <s:param name="seuilDateDepartCourse">
            <s:if test="seuilDateDepartCourse != null">
              <s:date name="seuilDateDepartCourse" format="HH:mm"/>
            </s:if>
          </s:param>
          <s:param name="idTableauMarche" value="%{idTableauMarche}"></s:param>
          <s:param name="page" value="%{pagination.numeroPageSuivante}" />
          <s:param name="idItineraire" value="%{idItineraire}" />
          <s:param name="idLigne" value="%{idLigne}" />
        </s:url>
        <s:a href="%{pageSuivante}"><s:text name="pagination.suivant"/></s:a>
      </td>
    </tr>
  </tbody>
</table>