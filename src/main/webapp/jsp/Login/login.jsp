<html>
  <head>
    <title>Chouette - Connexion</title>
    <META HTTP-EQUIV="Expires" CONTENT="0">
    <META HTTP-EQUIV="Pragma" CONTENT="no-cache">
    <META HTTP-EQUIV="Cache-Control" CONTENT="no-cache">
    <link rel="stylesheet" type="text/css" media="all" href="../../css/chouette_ninoxe.css" />
  </head>
  <body>
    <div id="global">
      <div id="header">
        <div id="title">
          <table cellpadding="0" cellspacing="0" border="0"><tr>
              <td height="108" style="vertical-align:top;">
                <a href="http://www.ecologie.gouv.fr/developpement-durable" target="_blank">
                  <img  alt="Visiter le site du Ministère de l'Ecologie, du Développement et de l'Amènagement Durable"  border="0" src="../../images/logoMedad.gif">
                </a>
              </td>
              <td style="vertical-align:top;">
                <table cellpadding="0" cellspacing="0" border="0"><tr><td style="vertical-align:top;">
                      <a href="http://www.certu.fr" target="_blank">
                        <img alt="Visiter le site du Centre d'Etudes sur les Réseaux, les Transports, l'Urbanisme et les constructions publiques"  border="0" src="../../images/logoCertuSmall.gif">
                      </a>
                    </td></tr>
                  <tr><td>
                      <a href="http://www.predim.org" target="_blank">
                        <img border="0" alt="Visiter le site de la Plate-forme de Recherche et d'Expérimentation pour le Développement de l'information Multimodale"  border="0" src="../../images/logoPredimSmall.gif">
                      </a>
                    </td>
                  </tr>
                </table>
              </td>
              <td height="108" style="vertical-align:top;">
                <table id="logo" height="108">
                  <tr>
                    <td id="chouette">Chouette</td>
                  </tr>
                  <tr>
                    <td id="definition">(Création d'horaires avec un outil d''échange de données TC selon le format Trident Européen)</td>
                  </tr>
                </table>
              </td>
            </tr>
          </table>
        </div>
      </div>
      <div id="main">
        <div id="content">
          <%
			Object busySessionParam = request.getParameter("busySession");
			if (busySessionParam != null)
			{
				Boolean busySession = Boolean.valueOf(busySessionParam.toString()).booleanValue();
				if(busySession) 
				{
					String html = "<p>La session est actuellement occup&eacute;e, veuillez r&eacute;essayer ult&eacute;rieurement</p>";
					out.print(html);
				}
			}
		  %>
          <form action="j_security_check" method="post">
            <table align="center">
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
