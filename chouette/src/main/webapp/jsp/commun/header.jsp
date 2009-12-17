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
            <tr><td id="chouette">Chouette</td></tr>
            <tr><td id="definition">
						(Création d'horaires avec un outil d'échange de données TC selon le format Trident Européen)
              </td></tr>
          </table>
        </td>
      </tr></table>

  </div>

  <div id="tools">
    <s:url id="language_fr" value="">
      <s:param name="request_locale" value="%{'fr'}" />
    </s:url>
    <s:url id="language_en" value="">
      <s:param name="request_locale" value="%{'en'}" />
    </s:url>
    <s:form>
      <ul>
        <s:url action="AProposDe" id="aproposde"/>
        <li><s:a href="%{aproposde}"><s:text name="app.aproposde.title"/></s:a></li>
      </ul>
      <ul>
        <li><s:property value="principalProxy.remoteUser"/></li> |
        <s:url action="deconnexion" id="deconnexion" includeParams="none"/>
        <li><s:a href="%{deconnexion}" >D&eacute;connexion</s:a></li>
      </ul>
      <ul>
        <li>
          <s:a href="%{language_fr}" includeParams="none">
            <img  alt="French"  border="0" src="<s:url value='/images/french_flag.jpg'/>">
          </s:a>
          <s:a  href="%{language_en}" includeParams="none">
            <img  alt="English"  border="0" src="<s:url value='/images/english_flag.jpg'/>">
          </s:a>
        </li>
      </ul>
    </s:form>
  </div>

</div>