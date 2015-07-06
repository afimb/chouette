# Chouette Command

Chouette command is a java project that provide validation and conversion functions on Public Transport Data exchange formats

* Validate transport data files on Neptune format and (comming soon) GTFS format
* Convert transport data files from Neptune, NeTEx or GTFS format to another of these formats

Formats documentation are available for:
* Neptune
 * [www.normes-donnees-tc.org](http://www.normes-donnees-tc.org/format-dechange/donnees-theoriques/neptune/)
* NeTEx
 * [www.normes-donnees-tc.org](http://www.normes-donnees-tc.org/format-dechange/donnees-theoriques/netex/)
* GTFS
 * [General Transit Feed Specification Reference](https://developers.google.com/transit/gtfs/reference)
 * [www.normes-donnees-tc.org](http://www.normes-donnees-tc.org/format-dechange/donnees-theoriques/gtfs-correspondance-avec-neptune-et-autres-normes/)

This java project is splited in different modules :

* mobi.chouette.command : Command mode standalone program (Import, Export and Validation actions)
* mobi.chouette.common : common classes and interfaces
* mobi.chouette.exchange : Common classes, interfaces and commands for data exchange 
* mobi.chouette.exchange.gtfs : Specific commands for GTFS data exchange and validation 
* mobi.chouette.exchange.hub : Specific commands for HUB data exchange
* mobi.chouette.exchange.kml : Specific commands for KML data exchange 
* mobi.chouette.exchange.neptune : Specific commands for Neptune data exchange and validation 
* mobi.chouette.exchange.netex : Specific commands for NeTEx data exchange 
* mobi.chouette.exchange.validator : Specific commands for common data validation 
* mobi.chouette.model : JPA entities modelisation for public transport model

For more information see [Architecture Documentation](http://www.chouette.mobi/developpeurs/) 

## Requirements
 
This code has been run and tested on [Travis](http://travis-ci.org/afimb/chouette?branch=master) with : 
* oraclejdk7
* oraclejdk8
* openjdk7
* openjdk8

## External Deps

On Debian/Ubuntu/Kubuntu OS : 
```sh
sudo apt-get install openjdk-7-jdk 
```

## Installation from binary
download mobi.chouette.command.x.y.z.zip from [maven repository](http://maven.chouette.mobi/mobi/chouette/mobi.chouette.command)

unzip in a specific folder

## Run programm

to get syntax and options : 

On Linux systems : 
```sh
./chouette.sh --help
```

On Windows systems :
```sh
chouette.bat --help
```
