<%@ taglib uri="/struts-tags" prefix="s"%>
<div id="header">

  <div id="title">
    <table cellpadding="0" cellspacing="0" border="0"><tr>
        <td height="108" style="vertical-align:top;">
          <a href="http://www.ecologie.gouv.fr/developpement-durable" target="_blank">
            <img  alt="Visiter le site du Ministère de l'Ecologie, du Développement et de l'Aménagement Durable"  border="0" src="<s:url value='/images/logoMedad.gif'/>">
          </a>
        </td>
        <td style="vertical-align:top;">
          <table cellpadding="0" cellspacing="0" border="0"><tr><td style="vertical-align:top;">
                <a href="http://www.certu.fr" target="_blank">
                  <img alt="Visiter le site du Centre d'Etudes sur les Réseaux, les Transports, l'Urbanisme et les constructions publiques"  border="0" src="<s:url value='/images/logoCertuSmall.gif'/>">
                </a>
              </td></tr>
            <tr><td>
                <a href="http://www.predim.org" target="_blank">
                  <img border="0" alt="Visiter le site de la Plate-forme de Recherche et d'Expérimentation pour le Développement de l'information Multimodale"  border="0" src="<s:url value='/images/logoPredimSmall.gif'/>">
                </a>
              </td></tr></table>
        </td>
        <td height="108" style="vertical-align:top;">
          <table id="logo" height="108">
            <tr><td id="chouette"><s:text name="app.title"/></td></tr>
            <tr><td id="definition">
                <s:text name="app.definition"/>
              </td></tr>
          </table>
        </td>
      </tr></table>

  </div>

  <div id="tools">
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
    <br>
    <ul>
      <li class="french">
        <s:a href="%{language_fr}">
          <img  alt="French"  border="0" src="<s:url value='/images/french_flag.png'/>">
        </s:a>
      </li>
      <li class="english">
        <s:a  href="%{language_en}">
          <img  alt="English"  border="0" src="<s:url value='/images/english_flag.png'/>">
        </s:a>
      </li>
    </ul>
  </div>

</div>