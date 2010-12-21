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
        <%
            Object busySessionParam = request.getParameter("busySession");
            if (busySessionParam != null)
            {
              Boolean busySession = Boolean.valueOf(busySessionParam.toString()).booleanValue();
              if (busySession)
              {
                String html = "<p class=\"errorMessage\">La session est actuellement occup&eacute;e, veuillez r&eacute;essayer ult&eacute;rieurement</p>";
                out.print(html);
              }
            }
        %>
        <div id="login">
          <form action="j_security_check" method="post">
            <table>
              <tbody>
                <tr>
                  <td><b>Identifiant : </b></td>
                  <td><input type="text" name="j_username" /></td>
                </tr>
                <tr>
                  <td><b>Mot de passe : </b></td>
                  <td><input type="password" name="j_password" /></td>
                </tr>
                <tr>
                  <td></td>
                  <td><input type="submit" value="Connexion" style="float:right;"/></td>
                </tr>
              </tbody>
            </table>
          </form>
        </div>
      </div>
    </div>
  </body>
</html>
