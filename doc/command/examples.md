# Some example on how to use chouette in command mode

Download
--------

download chouette_command from [maven repository : choose the last zip version](http://maven.chouette.mobi/mobi/chouette/mobi.chouette.command) 
uncompress zip file in a directory

chouette command is ready to use with java 7 or higher.

Setup
-----

no setup is required

Usage
-----

get help
Linux : 
```sh
./chouette.sh --help
```
for Windows, use chouette.bat command with same parameters

usage: \[./chouette.sh|chouette.bat\] \[options\] inputFile
 -d,--dir <arg>        working directory (default = ./work)
 -f,--file <arg>       output file
 -h,--help             show help
 -i,--input <arg>      input options json file
 -o,--output <arg>     output options json file
 -v,--validate <arg>   validation options json file

json file examples are provided in params directory

Neptune to GTFS sample command : 
```sh
./chouette.sh -i params/importNeptune.json -e params/exportGtfs.json -f gtfsOutputFile.zip neptuneInputFile.zip
```

Validation sample : (gtfs input)
```sh
./chouette.sh -i params/importGtfs.json -v params/validation.json gtfsInputFile.zip
```
