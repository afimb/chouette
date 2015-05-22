# Chouette [![Build Status](https://travis-ci.org/afimb/chouette.png)](http://travis-ci.org/afimb/chouette?branch=master)

Chouette is a java project that provides following services on transport data in different formats: Neptune, NeTEx, GTFS:

* Import transport data files
* Export transport data in such file formats including also KML
* Validate transport data
* Import, Export and Validation service are provided as Web Service and performed asynchronously.

The description of the Web Service API is decribed in :

* [Interface Documentation](./doc/interface/Chouette-API serveur IEV-1.0.pdf)
* [inputs XSD file](./doc/interface/Jobs.xsd)
* [outputs XSD file](./doc/interface/Reports.xsd)

Formats documentation are available for:
* Neptune
** [www.normes-donnees-tc.org](http://www.normes-donnees-tc.org/format-dechange/donnees-theoriques/neptune/)
* NeTEx
** [www.normes-donnees-tc.org](http://www.normes-donnees-tc.org/format-dechange/donnees-theoriques/netex/)
* GTFS
** [General Transit Feed Specification Reference](https://developers.google.com/transit/gtfs/reference)
** [www.normes-donnees-tc.org](http://www.normes-donnees-tc.org/format-dechange/donnees-theoriques/gtfs-correspondance-avec-neptune-et-autres-normes/)

This java project is splitted in differents modules :

* chouette-iev : REST server (ear)
* mobi.chouette.command : Command mode standalone program (Import, Export and Validation actions)
* mobi.chouette.common : common classes and interfaces
* mobi.chouette.dao : Dao implementation for model persistence (EJB)
* mobi.chouette.exchange : Common classes, interfaces and commands for data exchange 
* mobi.chouette.exchange.gtfs : Specific commands for GTFS data exchange and validation 
* mobi.chouette.exchange.hub : Specific commands for HUB data exchange
* mobi.chouette.exchange.kml : Specific commands for KML data exchange 
* mobi.chouette.exchange.neptune : Specific commands for Neptune data exchange and validation 
* mobi.chouette.exchange.netex : Specific commands for NeTEx data exchange 
* mobi.chouette.exchange.validator : Specific commands for common data validation 
* mobi.chouette.model : JPA entities modelisation
* mobi.chouette.persistence.hibernate : Hibernate specific tools
* mobi.chouette.service : Job and tasks managment
* mobi.chouette.ws : REST API implementation

For more information see [Architecture Documentation](http://www.chouette.mobi/developpeurs/) 

Feel free to test and access to the demonstration web site at [http://appli.chouette.mobi/chouette2](http://appli.chouette.mobi/chouette2). Two types of accesses are granted : 
* A demo organisation with a set of data
  * login : 'demo@chouette.mobi'
  * password : 'chouette'
* Create your own organisation : follow the "Sign up" ("S'inscrire") link

## Release Notes

The release notes can be found in [CHANGELOG](./CHANGELOG.md) file 

## Requirements
 
This code has been run and tested on [Travis](http://travis-ci.org/afimb/chouette?branch=master) with : 
* oraclejdk7
* oraclejdk8
* openjdk7


## External Deps
On Debian/Ubuntu/Kubuntu OS : 
```sh
sudo apt-get install postgresql 
sudo apt-get install pgadmin3 
sudo apt-get install openjdk-7-jdk 
sudo apt-get install git
```

## Installation

Rerequisite : [chouette2](https://github.com/afimb/chouette2) must be installed before chouette_iev
 
On debian, chouette can also be installed as a package : see [debian packages](http://packages.chouette.cityway.fr/debian/chouette)

Install [Postgres](./doc/install/postgresql.md) 

Install [Maven](./doc/install/maven.md)

Install [Wildfly](./doc/install/wildfly.md) 

### from sources
Create test and development databases : 
```sh
createdb -E UTF-8 -T template1 chouette_test
```

Get git repository :
```sh
git clone -b V3_0 git://github.com/afimb/chouette
cd chouette
```

Test :

```sh
mvn test -DskipWildfly
```

Deployment :

change data storage directory (USER_HOME by default)
copy properties file [iev.properties](./doc/iev.properties) in /etc/chouette/iev/ directory
change property ```iev.directory``` value to desired directory

deploy ear (wildfly must be running)
```sh
mvn -DskipTests install
```

## from binary
download chouette.ear from [maven repository](http://maven.chouette.mobi/mobi/chouette/chouette_iev)

change data storage directory (USER_HOME by default)
copy properties file [iev.properties](./doc/iev.properties) in /etc/chouette/iev/ directory
change property ```iev.directory``` value to desired directory

in wildfly installation repository : 
```sh
bin/jboss-cli.sh connect, deploy --force  (path to ...)/chouette.ear
```

## More Information
 
An exhaustive technical documentation in French is avalailable [here](http://www.chouette.mobi/developpeurs/)


## License
 
This project is licensed under the CeCILL-B license, a copy of which can be found in the [LICENSE](./LICENSE.md) file.

## Release Notes

The release notes can be found in [CHANGELOG](./CHANGELOG.md) file 
 
## Support
 
Users looking for support should file an issue on the GitHub [issue tracking page](../../issues), or file a [pull request](../../pulls) if you have a fix available.
