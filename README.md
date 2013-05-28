# Chouette [![Build Status](https://travis-ci.org/dryade/chouette.png)](http://travis-ci.org/dryade/chouette?branch=master)

Chouette is a java open source project on transport offer. It's divided in differents module : 
* chouette-castor-neptune : 
* chouette-command : Command line tool to export and import data
* chouette-core : Core functionnalities
* chouette-exchange-csv : Import/Export in csv format
* chouette-exchange-gtfs : Import/Export in gtfs format
* chouette-exchange-neptune : Import/Export in neptune format
* chouette-exchange-netex : Import/Export in netex format
* chouette-export-geoportail : Export in geoportail format
* chouette-gui-command : 
* chouette-hibernate-dao : Access to the database via hibernate 
* chouette-jdbc-dao : Access to the database via jdbc
* chouette-neptune : Old Web site to edit and view transport offer
* chouette-services : 
* chouette-validation : Validate the data 

Feel free to test and access to the demonstration web site at [http://www.chouette.mobi](http://www.chouette.mobi/chouette2/users/sign_in). Two types of access are granted : 
* A demo organisation with a set of data
  * login : demo@chouette.mobi
  * password : chouette
* Create your own organisation : Must follow the link "Sign up" ("S'inscrire")

Requirements
------------
 
This code has been run and tested on [Travis](http://travis-ci.org/dryade/chouette?branch=master) with : 
* oraclejdk7
* openjdk7
* openjdk6

External Deps
-------------
On Debian/Ubuntu/Kubuntu OS : 
```sh
sudo apt-get install postgresql 
sudo apt-get install pgadmin3 
sudo apt-get install openjdk-7-jdk 
sudo apt-get install git 
```

Installation
------------
 
Install [Postgres](https://github.com/dryade/chouette/blob/master/doc/install/postgresql.md)
Install [Maven]((https://github.com/dryade/chouette/blob/master/doc/install/maven.md))

Get git repository
```sh
cd workspace
git clone -b V2_0_3 git://github.com/dryade/chouette
cd chouette
```

Test
----

```sh
mvn test
```

More Information
----------------
 
More information can be found on the [project website on GitHub](http://github.com/dryade/chouette). 
There is extensive usage documentation available [on the wiki](https://github.com/dryade/chouette/wiki).

Example Usage 
-------------

Install 
```sh
mvn -Dmaven.test.skip=true install
```

License
-------
 
This project is licensed under the CeCILL-B license, a copy of which can be found in the [LICENSE](https://github.com/dryade/chouette/blob/master/LICENSE.md) file.

Release Notes
-------------

The release notes can be found in [CHANGELOG](https://github.com/dryade/chouette/blob/master/CHANGELOG.md) file 
 
Support
-------
 
Users looking for support should file an issue on the GitHub issue tracking page (https://github.com/dryade/chouette/issues), or file a pull request (https://github.com/dryade/chouette/pulls) if you have a fix available.
