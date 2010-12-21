<html>
  <head>
    <title>Chouette - Connexion</title>
    <META HTTP-EQUIV="Expires" CONTENT="0">
    <META HTTP-EQUIV="Pragma" CONTENT="no-cache">
    <META HTTP-EQUIV="Cache-Control" CONTENT="no-cache">
    <link rel="stylesheet" type="text/css" media="all" href="<%=request.getContextPath()%>/css/chouette_ninoxe.css" />
  </head>
  <body>
    <div id="global">
      <div id="header">
        <table class="layout">
          <tr>
            <td class="ministere">
              <a href="http://www.ecologie.gouv.fr/developpement-durable" target="_blank" class="medad">
                <img alt="Visiter le site du Ministère de l'Ecologie, du Développement et de l'Aménagement Durable" src="<%=request.getContextPath()%>/images/logoMedad.gif">
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
              <div class="chouette">Chouette</div>
              <!--div class="definition">(Cr&eacute;ation d'horaires avec un outil d'&eacute;change de donn&eacute;es TC selon le format Trident Europ&eacute;en)</div-->
              <div class="definition">(logiciel libre de r&eacute;f&eacute;rence pour l'&eacute;CHange normalis&eacute; de donn&eacute;es d'Offre de TransporT collEctif)</div>
            </td>
            <td class="languages">
            </td>
          </tr>
        </table>
      </div>
      <div id="main">
        <div id="content">
          <p style="text-align: center; color: red; font-weight: bold;">Erreur : l'identifiant ou le mot de passe est incorrect</p>
          <a rel="" href="<%=request.getContextPath()%>"><p style="text-align: center; font-weight: bold;">retour &agrave; la page d'authentification</p></a>
        </div>
      </div>
    </div>
  </body>
</html>
