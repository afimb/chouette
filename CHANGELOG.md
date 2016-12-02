# Version 3.4.2 (01/12/2016)
* Correction
  * PullRequest #34 : When querying for jobs, expose job action and status as parameters
  * PullRequest #36 : Truncate all tables when cleaning before import
  * PullRequest #37 : Robustify dao
  * PullRequest #38 : Add switch to skip importing of Lines where last calendar date is in the past
  * PullRequest #40 : Do not fail GTFS export on empty timetables
  * PullRequest #42 : Add GTFS export parameter to keep objectId from Chouette on export

# Version 3.4.1 (28/11/2016)
* Correction
  * Mantis 50126: Validation Neptune : informations manquantes dans le rapport provoquant plantage de l'IHM

# Version 3.4.0 (02/11/2016)
* Nouvelles fonctionnalités :
  * validation à l'export : après exports Neptune ou GTFS, une validation des fichiers produits est ajoutée sur option
  * reprise du rapport de validation pour mieux présenter les résultats
  * imports : ajout de contrôles de compatibilité entre les objets importés et leurs précédente version déjà en base.
  * imports : optimisation de la sauvegarde
  * import GTFS : optimisation de l'import
  * Admin : requêtes REST sur les statistiques d'utilisation
  * Modèle : horaires après minuit, ajout d'un champ 'dayOffset' 
  * Validation niveau 3 : ajout de tests sur les tracés et les courses en fréquence

# Version 3.3.3 (24/05/2016)
* Correction 
  * Mantis 44559: Export GTFS Shape.txt : rupture de numérotation dans la table journey_pattern_sections

# Version 3.3.2 (10/05/2016)
* Correction 
  * Mantis 43811: Export GTFS Shape.txt : inversion latitude et longitude
  * Mantis 43813: Export GTFS: colonne shape_dist_travelled obligatoire dans shape.txt
  * Mantis 43815: Export GTFS Shape.txt : tracés réels non pris en comptes dans certains cas

# Version 3.3.1 (07/04/2016)
* Nouvelles fonctionnalités :
  * export SIG : nouvel export combinant les format KML et GeoJson

# Version 3.3.0 (25/03/2016) 
* Nouvelles fonctionnalités :
  * actions de conversion
  * modularisation pour créer le backoffice de conversion/validation : cvdtc_cv
  * import GTFS : prise en compte des modes de transport étendus
* Correction 
  * Mantis 43096 : Import GTFS, pas de rapport sur les fichiers non traités dans le cas d'une erreur bloquante sur un fichier 
  * Mantis 43093 : Import GTFS, les fichiers obligatoires absents n'apparaissent pas en erreur dans le rapport d'import

# Version 3.2.1 (26/02/2016) 
* Corrections :
  * issue #23 : wrong shape_pt_sequence on GTFS export
  * issue #24 : memory leak and timeout on transaction on exports

# Version 3.2.0 (05/02/2016) 
* Nouvelles fonctionnalités
  * Gestion des courses en fréquences
  * Gestion des tracés de missions

# Version 3.1.1 (13/11/2015) 
* Corrections
  * Validation GTFS n'accepte pas les lang en majuscule (Mantis 40461) 
  * Validation GTFS erreur sur trip.txt en l'absence de calendar.txt (Mantis 40551)
  * Export Neptune : erreur si la séquence des arrêts n'est pas continue (position)  (Mantis 40552)
  * API Rest : blocage temporaire des api sur upload de gros volumes  (Mantis 40553)

# Version 3.1.0 (28/10/15)
* Nouvelles fonctionnalités
  * Validation GTFS lors de l'import
* Améliorations
  * mise en adéquation de Chouette sur Neptune (hors courses à fréquences)
  * export GTFS des informations pickup_type et drop_off_type

# Version 3.0.1
* Correction 
  * Mantis 38577 : fusion des logs Velocity aux logs IEV


# Version 3.0.0 (20/08/15)
* Nouveaux composants
  * Implémentation d'une interface webservice REST asynchrone des traitements d'import, export et validation chouette.ear exécutable sous Wildly en remplacement du jar programme chouette-gui-command.
  * Ajout d'un composant jar autonome pour réaliser des opérations de conversion de format et de validation en ligne de commande

# Version 2.5.3 (non déployée)
* Nouvelles fonctionnalités
  * Contrôle de cohérence des communes des arrêts (Mantis 31896)
  * Ajout de champs dans les échanges Neptune en structurant le champ commentaire (Mantis 31897)

* Corrections d'anomalies
  * NullPointerException dans la méthode isActiveOn du modèle java Timetable sur un calendrier dans jours d'application (Mantis 33783)

# Version 2.5.2 (21/01/15)
* Nouvelles fonctionnalités
  * Tests de validation avant export (Hub)
  * Metadata à l'export

* Amélioration
  * Optimisation de jaxb
  
* Correction
  * Détection d'absence de fin de ligne en fin de fichier gtfs (Mantis 30988)

# Version 2.5.1 (09/12/14)
* Nouvelles fonctionnalités
  * refonte des échanges GTFS

* Amélioration
  * Complétude du javadoc pour chouette-model

# Version 2.5.0 (23/10/14)
* Nouvelles fonctionnalités
  * Import d'arrêts en CSV/Excel (Mantis 26832)
  * Attributs vacances et jours fériés : comme étiquette (Mantis 26835)
  * Gestion de dates exclues dans les calendriers (Mantis 26837)
  * Import CSV d'horaires pour une séquence d'arrêts (Mantis 26843)
  * Attributs TAD et PMR sur les lignes et les courses (Mantis 26846
  * Séparataion des classes du modèle dans un module maven isolé
  * ajout d'un test de niveau 3 sur l'unicité des numéros de course (Mantis 29182)
  * ajout d'un test de niveau 3 sur les modes autorisés pour les courses (Mantis 29183)
  * ajout d'un test de niveau 3 sur les arrêts sans commune (Mantis 29184)

# Version 2.4.1 (30/06/2014)
* Corrections d'anomalies
  * L'export GTFS exige que l'indice de ligne soit renseigné (Mantis 26726)
  * L'import GTFS ne tient pas compte des courses commençant après minuit (Mantis 25824)
  * L'import GTFS plante sur une course qui dessert plus de 2 fois le même arrêt (Mantis 26755)
  * L'export NeTEx produit un fichier invalide si le champ VersionDate du réseau est vide (Mantis 26434)
  * La validation plante si un arrêt n'est pas géolocalisé (Mantis 26931) 
  * L'import plante si l'encodage du catalogue d'un ZIP n'est pas compatible UTF-8 (Mantis 27011) 
  
# Version 2.4.0 (27/05/14)
* Corrections d'anomalies
  * L'Import GTFS n'importe pas les fichiers avec marqueurs BOM (Mantis 21260)
  * L'Import GTFS n'accepte pas des routes sans colonne agencyId (Mantis 22665)
  * L'Export GTFS sort en erreur sur objet à exporter incomplêt (Mantis 24484)
  * L'Export CSV sort un message inexploitable sur informations incomplètes (Mantis 24485)
  * L'import NeTEx n'importe pas les ITL (Mantis 20889)
  * La validation plante sur un arrêt non géolocalisé (Mantis 26099)
  * L'export n'accepte pas une liste de réseaux (Mantis 26438)
  * Import Neptune : erreur de sauvegarde si le mode de transport est manquant (Mantis 26702)
  * Import Neptune : comparaison des objets partagés trop stricte (Mantis 26912)
  * Export Neptune : export de séquences d'arrêt sans sens de défini (Mantis 26929)
  
# Version 2.3.0 (17/04/14)
* Migration technique
  * passage sous spring 4
  * passage sous hibernate 4
  * utilisation des annotations JPA

# Version 2.2.0 (14/02/14)
* Refonte de la validation
  * suppression de la validation de fichiers
  * mise en place de la validation durant l'import
  * ajout de la validation sur les objets en base
  * redéfinition des tests

# Version 2.1.0 (15/07/13)
* Suppression des coordonnées projetées en base
  * calcul à la volée pour les exports
  * conversion à la volée pour les imports
* Amélioration de l'import GTFS
  * consolidation de l'import
  * clarification du rapport
* Amélioration de l'export Neptune
  * ajout filtre sur une période calendaire
* Amélioration de l'export GTFS
  * correction des courses(trip) en retour d'un trajet(route)
  * correction de la construction des calendriers (calendar et calendar_date)
  * export des correspondances (transfer)

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
  *  création de nouveaux horaires
  *  validation des formulaires
  *  export Neptune ne contient pas les références waybackRouteId dans les Route
		
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

