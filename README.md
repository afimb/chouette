# Chouette [![Build Status](https://travis-ci.org/afimb/chouette.png)](http://travis-ci.org/afimb/chouette?branch=master)[![Coverity Scan](https://img.shields.io/coverity/scan/5816.svg)](https://scan.coverity.com/projects/5816)

Chouette is a java project for handling public transport (PT) data in different formats: Neptune, NeTEx, GTFS and provides 3 main services:

* Import PT data files from Neptune, NeTEx and GTFS formats
* Export PT data to Neptune, NeTEx, GTFS,  KML and GeoJson formats
* Validate PT data

The Import, Export and Validation services are provided as Web Services and performed asynchronously.

The Chouette Web Service API is specified here :
* [Interface Specification Document](./doc/interface/Chouette-API serveur IEV-1.0.pdf)
* inputs and outputs data structures [Jobs](./doc/interface/Jobs.xsd), [Reports](./doc/interface/Reports.xsd)

Chouette is used by : 
* [Chouette2 ](https://github.com/afimb/chouette2), a standard-based PT planned data management web application.
* [CVDTC ](https://gitub.com/afimb/cvdtc), a web platform for PT planned data conversion and validation 

Format documentations are available for:
* Neptune
 * [www.normes-donnees-tc.org](http://www.normes-donnees-tc.org/format-dechange/donnees-theoriques/neptune/)
* NeTEx
 * [www.normes-donnees-tc.org](http://www.normes-donnees-tc.org/format-dechange/donnees-theoriques/netex/)
* GTFS
 * [General Transit Feed Specification Reference](https://developers.google.com/transit/gtfs/reference)
 * [www.normes-donnees-tc.org](http://www.normes-donnees-tc.org/format-dechange/donnees-theoriques/gtfs-correspondance-avec-neptune-et-autres-normes/)

The Chouette java project is split into modules :

* chouette-iev : **REST server (ear)**
* mobi.chouette.command : **Command mode standalone program** (Conversion and Validation actions)
* mobi.chouette.common : common classes and interfaces
* mobi.chouette.dao : Dao implementation for model persistence (EJB)
* mobi.chouette.dao.iev : Dao implementation for iev persistence (EJB)
* mobi.chouette.exchange : Common classes, interfaces and commands for data exchange 
* mobi.chouette.exchange.converter : Specific commands for conversion
* mobi.chouette.exchange.geojson : Specific commands for GeoJson data export 
* mobi.chouette.exchange.gtfs : Specific commands for GTFS data exchange and validation 
* mobi.chouette.exchange.hub : Specific commands for HUB data exchange 
* mobi.chouette.exchange.kml : Specific commands for KML data export 
* mobi.chouette.exchange.neptune : Specific commands for Neptune data exchange and validation 
* mobi.chouette.exchange.netex : Specific commands for NeTEx data exchange (experimental local agreement)
* mobi.chouette.exchange.sig : Specific commands for GeoJson and kml data export at same time
* mobi.chouette.exchange.validator : Specific commands for common data validation 
* mobi.chouette.model : JPA entities modelisation for public transport model
* mobi.chouette.model.iev : JPA entities modelisation for iev jobs
* mobi.chouette.persistence.hibernate : Hibernate specific tools
* mobi.chouette.service : Job and tasks managment
* mobi.chouette.schema.checker : Access control implementation for Chouette GUI
* mobi.chouette.ws : REST API implementation

For more information see the [Architecture Documentation](http://www.chouette.mobi/developpeurs/) , in French.

Installation instructions for the **command line standalone program** are available [here](./mobi.chouette.command/README.md) 

## Release Notes

The Chouette release notes (in French) can be found in [CHANGELOG](./CHANGELOG.md) file 

## Requirements
 
This code has been run and tested on [Travis](http://travis-ci.org/afimb/chouette?branch=master) with : 
* oraclejdk7
* oraclejdk8
* openjdk7
* openjdk8
* postgres 9.3 + postgis 2.1
* wildfly 8.2.0

## Chouette External Dependencies

Chouette V3.x requires Postgresql V9.3 or above

On Debian/Ubuntu/Kubuntu OS : 
```sh
sudo apt-get install postgresql-9.3
sudo apt-get install postgresql-9.3-postgis-2.1
sudo apt-get install openjdk-7-jdk 
```

For installation from sources : 
```sh
sudo apt-get install git
sudo add-apt-repository ppa:natecarlson/maven3
sudo apt-get update 
sudo apt-get install maven3
sudo ln -s /usr/share/maven3/bin/mvn /usr/bin/mvn
```
if ```apt-get update``` fails, modify file :
/etc/apt/sources.list.d/natecarlson-maven3-trusty.list
and replace ```trusty``` by ```precise``` 

## Chouette Installation

On Debian, **Chouette can also be installed as a package** : see [debian packages](http://packages.chouette.cityway.fr/debian/chouette)

### Prerequisite
 
[Create Postgres user and databases ](./doc/install/postgresql.md) 


### Installation from sources

Get git repository :
```sh
git clone -b V3_3 git://github.com/afimb/chouette
cd chouette
```

Test :

```sh
mvn test -DskipWildfly
```

Deployment :

change the data storage directory (USER_HOME by default) :
copy properties file [iev.properties](./doc/iev.properties) in /etc/chouette/iev/ directory
change property ```iev.directory``` value to desired directory
change property ```iev.started.jobs.max``` value to limit parallel jobs processing (default = 5)
change property ```iev.copy.by.import.max``` value to limit parallel single line import by import job (default = 5)

[Install and configure Wildfly](./doc/install/wildfly.md) 

[For existing chouette_iev deployment : update postgres / wildfly configuration](./doc/install/update.md) 

deploy ear (wildfly must be running)
```sh
mvn -DskipTests install
```

### Installation from the binaries
download chouette_iev.x.y.z.zip from [maven repository](http://maven.chouette.mobi/mobi/chouette/chouette_iev)

change the data storage directory (USER_HOME by default)
copy the properties file [iev.properties](./doc/iev.properties) into /etc/chouette/iev/ directory
change the ```iev.directory``` value to the desired directory
change the ```iev.started.jobs.max``` value in order to limit the max number of parallel jobs (default = 5)

[Install and configure Wildfly](./doc/install/wildfly.md) 

.
[For an existing chouette_iev deployment prior to 3.2 : update the postgres / wildfly configuration](./doc/install/update.md) 

in the wildfly installation repository :
```sh
bin/jboss-cli.sh --connect --command="deploy (path to ...)/chouette.ear"
```

## More Information
 
An exhaustive technical documentation in French is avalailable [here](http://www.chouette.mobi/developpeurs/)


## License
 
Chouette is licensed under the CeCILL-B license, a copy of which can be found in the [LICENSE](./LICENSE.md) file.

 
## Support
 
Users looking for support should file an issue on the GitHub [issue tracking page](../../issues), or file a [pull request](../../pulls) if you have a fix available.
