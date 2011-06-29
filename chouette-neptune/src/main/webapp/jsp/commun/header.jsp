<%@ taglib uri="/struts-tags" prefix="s"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<div id="header">
    <table class="layout">
        <tr>
            <td class="ministere">
                <a href="http://www.equipement.gouv.fr/" target="_blank" class="medad">
                    <img alt="Visiter le site du Ministère de l'Ecologie, du Développement Durable, des Transports et du Logement" src="<%=request.getContextPath()%>/images/logoMedad.gif">
                </a>
            </td>
            <td class="partners">
                <a href="http://www.certu.fr" target="_blank" class="certu">
                    <img alt="Visiter le site du Centre d'Etudes sur les Réseaux, les Transports, l'Urbanisme et les constructions publiques" src="<%=request.getContextPath()%>/images/logoCertuSmall.gif">
                </a>
                <a href="http://www.predim.org" target="_blank" class="predim">
                    <img alt="Visiter le site de la Plate-forme de Recherche et d'Expérimentation pour le Développement de l'information Multimodale" src="<%=request.getContextPath()%>/images/logoPredim.gif">
                </a>
            </td>
            <td class="logo">
                <div class="chouette"><s:text name="app.title"/></div>
                <div class="definition"><s:text name="app.definition"/></div>
            </td>
            <td class="languages">
                <s:url id="language_fr" value="" includeParams="all">
                    <s:param name="request_locale" value="%{'fr'}" />
                </s:url>
                <s:url id="language_en" value="" includeParams="all">
                    <s:param name="request_locale" value="%{'en'}" />
                </s:url>
                <s:url action="AProposDe" id="aproposde"/>
                <ul>
                    <li><s:a href="%{aproposde}"><s:text name="app.aproposde.title"/></s:a></li>
                    </ul>
                    <ul>
                        <li><s:property value="principalProxy.remoteUser"/></li> |
                    <s:url action="deconnexion" id="deconnexion" includeParams="none"/>
                    <li><s:a href="%{deconnexion}" ><s:text name="app.deconnection"/></s:a></li>
                    </ul>
                    <ul>
                        <li class="french">
                        <s:a href="%{language_fr}">
                            <img  alt="French"  border="0" src="<s:url value='/images/french_flag.png'/>" title="<s:text name="tooltip.french.flag"/>">
                        </s:a>
                    </li>
                    <li class="english">
                        <s:a  href="%{language_en}">
                            <img  alt="English"  border="0" src="<s:url value='/images/european_flag.png'/>" title="<s:text name="tooltip.european.flag"/>">
                        </s:a>
                    </li>
                </ul>
            </td>
        </tr>
    </table>
</div>