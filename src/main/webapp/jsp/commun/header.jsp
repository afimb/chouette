<%@ taglib uri="/struts-tags" prefix="s"%>
<div id="header">
  <div class="title">
    <a href="http://www.ecologie.gouv.fr/developpement-durable" target="_blank">
      <img alt="Visiter le site du Ministère de l'Ecologie, du Développement et de l'Aménagement Durable" class="logoMedad" >
    </a>
    <ul class="certu">
      <li>
        <a href="http://www.certu.fr" target="_blank">
          <img alt="Visiter le site du Centre d'Etudes sur les Réseaux, les Transports, l'Urbanisme et les constructions publiques" class="logoCertu">
        </a>
      </li>
      <li>
        <a href="http://www.predim.org" target="_blank">
          <img border="0" alt="Visiter le site de la Plate-forme de Recherche et d'Expérimentation pour le Développement de l'information Multimodale" class="logoPredim">
        </a>
      </li>
    </ul>
    <ul class="chouette">
      <li class="title">
        Chouette
      </li>
      <li class="definition">(Création d'horaires avec un outil d'échange de données TC selon le format Trident Européen)</li>
    </ul>
  </div>

  <div class="tools">
    <ul>
      <s:url action="AProposDe" id="aproposde"/>
      <li><s:a href="%{aproposde}"><s:text name="app.aproposde.title"/></s:a></li>
    </ul>
    <ul>
      <li><s:property value="principalProxy.remoteUser"/></li> |
      <s:url action="deconnexion" id="deconnexion" includeParams="none"/>
      <li><s:a href="%{deconnexion}" >D&eacute;connexion</s:a></li>
    </ul>
  </div>

</div>