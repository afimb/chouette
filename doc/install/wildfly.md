# Install and configure Wildfly


Download
--------
download Wildfly 8.2.0.Final from [http://wildfly.org/downloads/](http://wildfly.org/downloads/)

uncompress wildfly in installation directory (/opt for exemple)

download in temporary folder (/tmp for following commands) : 

[Postgresql jdbc driver version 9.3-1103](http://mvnrepository.com/artifact/org.postgresql/postgresql/9.3-1103-jdbc41)

[postgis version 2.1.7.2](http://mvnrepository.com/artifact/net.postgis/postgis-jdbc/2.1.7.2)

[hibernate-spatial version 4.3](http://www.hibernatespatial.org/repository/org/hibernate/hibernate-spatial/4.3/)

[JTS model version 1.13](http://mvnrepository.com/artifact/com.vividsolutions/jts/1.13)


Setup
-----

in installation directory (/opt/wildfly-8.2.0.Final)

update hibernate module :
```
sudo cp /tmp/hibernate-spatial-4.3.jar /tmp/jts-1.13.jar modules/system/layers/base/org/hibernate/main/.
sudo mv modules/system/layers/base/org/hibernate/main/module.xml modules/system/layers/base/org/hibernate/main/module.xml.bak
sudo sed -e '/<resources>/a         <resource-root path="hibernate-spatial-4.3.jar"/>
/<resources>/a         <resource-root path="jts-1.13.jar"/>
/<dependencies>/a        <module name="org.postgres"/>' <modules/system/layers/base/org/hibernate/main/module.xml.bak >modules/system/layers/base/org/hibernate/main/module.xml
```

start server : 
on default ports (8080 and 9990 for administration)
```sh
bin/standalone.sh -c standalone-full.xml
```
if port 8080 is used (8180 and 10090 for adminstration)
```sh
bin/standalone.sh -c standalone-full.xml -Djboss.socket.binding.port-offset=100
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

module add --name=org.postgres --resources=/tmp/postgresql-9.3-1103-jdbc41.jar:/tmp/postgis-jdbc-2.1.7.2.jar --dependencies=javax.api,javax.transaction.api
/subsystem=datasources/jdbc-driver=postgresql:add(driver-name="postgresql",driver-module-name="org.postgres",driver-xa-datasource-class-name=org.postgresql.xa.PGXADataSource)
data-source add --jndi-name=java:jboss/datasources/iev --name=iev --connection-url=jdbc:postgresql://localhost:5432/iev  --driver-name=postgresql --user-name=chouette --password=chouette
data-source add --jndi-name=java:jboss/datasources/chouette --name=chouette --connection-url=jdbc:postgresql_postGIS://localhost:5432/chouette2 --driver-class=org.postgis.DriverWrapper --driver-name=postgresql --user-name=chouette --password=chouette --max-pool-size=30

/subsystem=ee/managed-executor-service=default/ :write-attribute(name=max-threads,value=15)
/subsystem=ee/managed-executor-service=default/ :write-attribute(name=queue-length,value=15)

exit
```
Notes : 
max-threads and queue-length should be sized according to iev.started.jobs.max and iev.copy.by.import.max defined in iev.properties (default are 5)
```sh
max-threads >= 2 * iev.started.jobs.max
max-threads + queue-length >= iev.started.jobs.max * (iev.copy.by.import.max + 1)
```

change uploaded file size: 

```sh
bin/jboss-cli.sh
connect
/subsystem=undertow/server=default-server/http-listener=default/ :write-attribute(name=max-post-size, value=80000000)
exit
```
where 80000000 is the file size in bytes (defauls is 10 Mb)

change JVM heap size :
for huge data, JVM heap size should be increased to 1024kb (defaults is 512kb)

* stop wildfly
* edit bin/standalone.conf
* in JAVA_OPTS : change Xmx value
* save and restart wildfly

Install as service
------------------

On github sukharevd gives a shell to download and install as a linux service :

[wildfly-install.sh](https://gist.github.com/sukharevd/6087988)

after using it : 
* stop wildfly
* process step "update hibernate module" 
* start wildly
* process all steps from "add a managment user for web administration console"
