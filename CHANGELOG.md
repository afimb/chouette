# Version 2.0.3 (27/05/13)
* Import/export NeTex 
* Fonctionnement sous Windows
* Meilleur détection du format des fichiers XML en import
* Suppression de la mise à jour du modèle par Hibernate
* Amélioration de l'import GTFS

# Version 2.0.2 (28/01/13)
* Import GTFS 
* Import CSV : ajout des nouveaux champs V2
* Mise en cohérence des modèles DB java et Rails

# Version 2.0.1 (17/12/12)
* Import Neptune : accepter les principaux types d'encodage
* Import/Export Neptune : quelques corrections sur les imports/exports des groupes de lignes et des accès

# Version 2.0.0 (10/09/12)
* reprise du modèle de la base de données conformément aux "best practices" de la majorité des ORM
* ajout d'un mode commande dédié à l'IHM RAILS
* import/export CSV : quelques corrections de bugs et de désynchronisation entre les 2 sens
* import GTFS : optimisation des traitements et ajout d'un mode incrémental
* import/export CSV : quelques corrections de bugs et de désynchronisation entre les 2 sens

# Version 1.7.3 (12/03/12)
* ajout des liens sur les définitions des tests de validation sur la page de lancement des tests
* correction import/export Neptune : durée de correspondances
* procédure de migration : renumérotation des périodes et dates des calendriers
* ajout des correspondances en export GTFS
* correction de l'affichage GEOPORTAIL
* ajout de la clé primaire sur la table journeypattern_stoppoint
* ajout du champs sourcetype dans la table ptnetwork
* ajout dans l'import Neptune de l'option optimizeMemory permettant d'importer des lignes volumineuses
* correction de l'affichage sous IE
  
# Version 1.7.2 (06/01/12)

* correction export GTFS : latitude = longitude et autres coquilles
* correction import CSV : nom du réseau pour créer l'objectId (remplacé par le code réseau)
* correction formats des latitudes/longitudes dans creation de la base
* upgrade de la version d'Hibernate = 3.6.8.Final
* upgrade de la version de spring = 3.0.5.RELEASE

# Version 1.7.1 (29/11/11)

* optimisation des imports
* pour la migration des bases v1.6 et en deça : ajout d'une commande à chouette-command qui analyse les identifiants dans la base et produit un fichier SQL pour corriger ceux qui ne respectent pas le format TRIDENT/NEPTUNE; ce fichier est ensuite à corriger à la main et à passer par psql pour appliquer les corrections
* suppression des doublons dans la table TimetableVehicleJourney dans la procédure de migration
* correction des bugs de la version 1.7.0 :
**  création de nouveaux horaires
**  validation des formulaires
**  export Neptune ne contient pas les références waybackRouteId dans les Route
		
# Version 1.7.0 (07/11/11)

* Adaptation du schéma de la base aux objets Neptune  (le manuel d'installation précise les incompatibilités possible entre les schémas)
* Suppression de l'ancienne architecture et des développements spécifiques non généralisables (HASTUS, Pégase)
* Redéfinition du modèle et des écrans des ITL pour mise en conformité à la XSD Neptune


# Version 1.6.3 (3/10/11)

* Correction de bugs typos
* Fusion des boutons d'ajout d'arrêts aux séquences d'arrêts
* Lors de la conversion de coordonnées Lambert en WGS84, bien spécifier la catégorie "Lambert"
* Mise à jour documentaire

# Version 1.6.2

  publiée en aout 2011, cette version de Chouette ajoute les fonctionnalités suivantes :
* Validation Neptune : Implémentation des tests BATERI (Tests de validation des données aux format XML Neptune).
* Export Géopportail : Export des données associées aux arrêts en vue de leurs publications sur le Géoprtail.
* Chouette Commande : Un moyen d'exécuter les différentes fonctionnalités de Chouette en ligne de commande (sans utilisation d'interface WEB).

# Version 1.5

  Publiée en septembre 2010, cette version de Chouette ajoute les fonctionnalités suivantes :
* Quatres cartes géographiques dans les pages d'Edit de :
**  Lignes
**  Arrêts
**  Zones d'arrêts
**  Correspondances
* L'export GTFS depuis la base Chouette à travers l'IHM pages réseaux et en lignes de commandes depuis des fichiers XML Neptune avec ou sans enregistrement dans la base et avec ou sans validation
* Machine virtuelle Chouette : La posibilité de déployer une machine virtuelle Chouette à partir d'une image ISO dans un outil de virtualisation tel que VirtualBox ou VMWare

# Version 1.4.0.2
    
  Publiée en avril 2010, cette version de Chouette ajoute les fonctionnalités suivantes :
* Montée de version des Frameworks : Struts 2.1.8.1, Hibernate 3.2.0.ga, castor 1.3.1, spring 2.5.6
* Internationalisation : Français / Anglais
* Intégration de Neptune dans les Imports / Exports
* Migration de la base de données sur un schéma spécifique
* Gestion des accès concurrent
<<<<<<< HEAD:CHANGELOG.md
=======

h2. Licence


Ce logiciel est un programme informatique servant à créer et à échanger des données de modélisation d'offres de transport en commun en respectant les préconisations de la norme NEPTUNE [ref AFNOR en cour] . 

Ce logiciel est régi par la licence CeCILL-B soumise au droit français et respectant les principes de diffusion des logiciels libres. Vous pouvez utiliser, modifier et/ou redistribuer ce programme sous les conditions de la licence CeCILL-B telle que diffusée par le CEA, le CNRS et l'INRIA sur le site "http://www.cecill.info":http://www.cecill.info .

En contrepartie de l'accessibilité au code source et des droits de copie, de modification et de redistribution accordés par cette licence, il n'est offert aux utilisateurs qu'une garantie limitée. Pour les mêmes raisons, seule une responsabilité restreinte pèse sur l'auteur du programme, le titulaire des droits patrimoniaux et les concédants successifs.

A cet égard  l'attention de l'utilisateur est attirée sur les risques associés au chargement,  à l'utilisation,  à la modification et/ou au développement et à la reproduction du logiciel par l'utilisateur étant donné sa spécificité de logiciel libre, qui peut le rendre complexe à manipuler et qui le réserve donc à des développeurs et des professionnels avertis possédant  des  connaissances  informatiques approfondies.  Les utilisateurs sont donc invités à charger  et  tester  l'adéquation  du logiciel à leurs besoins dans des conditions permettant d'assurer la sécurité de leurs systèmes et ou de leurs données et, plus généralement, à l'utiliser et l'exploiter dans les mêmes conditions de sécurité. 

Le fait que vous puissiez accéder à cet en-tête signifie que vous avez pris connaissance de la licence CeCILL-B, et que vous en avez accepté les termes.

--------------------------------------------------------------

This software is a computer program whose purpose is to to manage and disseminate public transfort offers according to the french NEPTUNE standard [AFNOR reference in progress].

This software is governed by the CeCILL-B license under French law and abiding by the rules of distribution of free software.  You can  use,  modify and/ or redistribute the software under the terms of the CeCILL-B license as circulated by CEA, CNRS and INRIA at the following URL "http://www.cecill.info":http://www.cecill.info . 

As a counterpart to the access to the source code and rights to copy, modify and redistribute granted by the license, users are provided only with a limited warranty  and the software's author, the holder of the economic rights, and the successive licensors have only limited liability. 

In this respect, the user's attention is drawn to the risks associated with loading, using, modifying and/or developing or reproducing the software by the user in light of its specific status of free software, that may mean that it is complicated to manipulate,and that also therefore means that it is reserved for developers and experienced professionals having in-depth computer knowledge. Users are therefore encouraged to load and test the software's suitability as regards their requirements in conditions enabling the security of their systems and/or data to be ensured and, more generally, to use and operate it in the same conditions as regards security. 

The fact that you are presently reading this means that you have had knowledge of the CeCILL-B license and that you accept its terms.


>>>>>>> 778c885f25667b51a4c628ec9d984716c96feb01:README.textile
