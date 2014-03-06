# Chouette [![Build Status](https://travis-ci.org/afimb/chouette.png)](http://travis-ci.org/afimb/chouette?branch=master)

Chouette is a java open source project on transport offer. It's divided in differents module : 
* chouette-command : Lite command line tool to manipulate data
* chouette-core : Core functionnalities (Internal model and managers)
* chouette-exchange-csv : Import/Export in a chouette specific csv format
* chouette-exchange-gtfs : Import/Export in gtfs format
* chouette-exchange-neptune : Import/Export in neptune format
* chouette-exchange-netex : Import/Export in netex format
* chouette-export-geoportail : Export in geoportail format (deprecated)
* chouette-gui-command : Command Line tool used by Ruby Chouette2 GUI for Import/Export/Validation purpose
* chouette-hibernate-dao : Access to the database via hibernate 
* chouette-jdbc-dao : Access to the database via jdbc (mass upload only)
* chouette-jaxb-neptune : JAXB object model upon Neptune xsd 
* chouette-validation : Validate the data from import or database

For more information see [Architecture Documentation](http://www.chouette.mobi/IMG/pdf/DARC_CHOUETTE_2-0.pdf) 

Feel free to test and access to the demonstration web site at [http://www.chouette.mobi](http://www.chouette.mobi/chouette2/users/sign_in). Two types of access are granted : 
* A demo organisation with a set of data
  * login : demo@chouette.mobi
  * password : chouette
* Create your own organisation : Must follow the link "Sign up" ("S'inscrire")

Requirements
------------
 
This code has been run and tested on [Travis](http://travis-ci.org/afimb/chouette?branch=master) with : 
* oraclejdk7
* openjdk7


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
 
Install [Postgres](./doc/install/postgresql.md) 

Install [Maven](./doc/install/maven.md)

Create test and development databases : 
```sh
createdb -E UTF-8 -T template1 chouette_dev
createdb -E UTF-8 -T template1 chouette_test
```

Get git repository
```sh
cd workspace
git clone -b V2_3 git://github.com/afimb/chouette
cd chouette
```

Test
----

```sh
mvn test
```

More Information
----------------
 
More information can be found on the [project website on GitHub](.). 
There is extensive usage documentation available [on the wiki](../../wiki).

Example Usage 
-------------

Install 
```sh
mvn -Dmaven.test.skip=true install
```

License
-------
 
This project is licensed under the CeCILL-B license, a copy of which can be found in the [LICENSE](./LICENSE.md) file.

Release Notes
-------------

The release notes can be found in [CHANGELOG](./CHANGELOG.md) file 
 
Support
-------
 
Users looking for support should file an issue on the GitHub [issue tracking page](../../issues), or file a [pull request](../../pulls) if you have a fix available.
