# Install Wildfly

Download
--------
download Wildfly 8.2.0.Final from [http://wildfly.org/downloads/](http://wildfly.org/downloads/)

uncompress wildfly in installation directory (/opt for exemple)

download Postgresql jdbc driver 9.3-1103 from [https://jdbc.postgresql.org/download.html](https://jdbc.postgresql.org/download.html)

Setup
-----

in installation directory (/opt/wildfly-8.2.0.Final)
start server : 
on default ports (8080 and 9990 for administration)
```sh
bin/standalone.sh -b 0.0.0.0 -c standalone-full.xml
```
if port 8080 is used (8180 and 10090 for adminsitration)
```sh
bin/standalone.sh -b 0.0.0.0 -c standalone-full.xml -Djboss.socket.binding.port-offset=100
```
add a managment user for web administration console
```sh
bin/add-user.sh
type: management user (a)
login : admin
password : admin
```

create database access :
```sh
bin/jboss-cli.sh
connect

module add --name=org.postgres --resources=(path to driver)/postgresql-9.3-1101.jdbc41.jar --dependencies=javax.api,javax.transaction.api
/subsystem=datasources/jdbc-driver=postgres:add(driver-name="postgres",driver-module-name="org.postgres",driver-class-name=org.postgresql.Driver)
data-source add --jndi-name=java:jboss/datasources/chouette --name=chouette --connection-url=jdbc:postgresql://localhost:5432/chouette2 --driver-name=postgres --user-name=chouette --password=chouette

data-source add --jndi-name=java:jboss/datasources/iev --name=iev --connection-url=jdbc:h2:(path to iev data directory)/database/jobs --driver-name=h2 --user-name=chouette --password=chouette
```
Note : (path to ...) must be replaced by absolute paths without parenthesis





