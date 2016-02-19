# update postgres and wildfly from previous version

## from chouette 3.0.x or 3.1.x

Postgres : 
install postgis 2.1+ with: `sudo apt-get install postgresql-9.3-postgis-2.1`

Rails (chouette2) launch the command below:

````
bundle exec rake db:gis:setup
bundle exec rake db:migrate
```

Wildfly :
install and activate postgis and hibernate spatial modules : (commands are launched from wildfly deployment folder)

Step 1 : remove old modules, datasources and chouette_iev ear

```sh
sudo bin/jboss-cli.sh
connect
undeploy chouette.ear
data-source remove --name=chouette
data-source remove --name=iev
/subsystem=datasources/jdbc-driver=postgres:remove
/subsystem=datasources/jdbc-driver=postgresql:remove
module remove --name=org.postgres
exit
```

Step 2 : stop wildfly server

Step 3 : download jars for postgres and hibernate modules in temporary folder (/tmp for following commands)

[postgis version 2.1.7.2](http://mvnrepository.com/artifact/net.postgis/postgis-jdbc/2.1.7.2)

[hibernate-spatial version 4.3](http://www.hibernatespatial.org/repository/org/hibernate/hibernate-spatial/4.3/)

[JTS model version 1.13](http://mvnrepository.com/artifact/com.vividsolutions/jts/1.13)

Step 4 : add hibernate spatial extension to hibernate module
```
sudo cp /tmp/hibernate-spatial-4.3.jar /tmp/jts-1.13.jar modules/system/layers/base/org/hibernate/main/.
sudo mv modules/system/layers/base/org/hibernate/main/module.xml modules/system/layers/base/org/hibernate/main/module.xml.bak
sudo sed -e '/<resources>/a         <resource-root path="hibernate-spatial-4.3.jar"/>
/<resources>/a         <resource-root path="jts-1.13.jar"/>
/<dependencies>/a        <module name="org.postgres"/>' <modules/system/layers/base/org/hibernate/main/module.xml.bak >modules/system/layers/base/org/hibernate/main/module.xml
``` 

Step 5 : reinstall postgres module and datasources

start wildfly and follow commands above
```
sudo bin/jboss-cli.sh
connect
module add --name=org.postgres --resources=/tmp/postgresql-9.3-1103-jdbc41.jar:/tmp/postgis-jdbc-2.1.7.2.jar --dependencies=javax.api,javax.transaction.api
/subsystem=datasources/jdbc-driver=postgresql:add(driver-name="postgresql",driver-module-name="org.postgres",driver-xa-datasource-class-name=org.postgresql.xa.PGXADataSource)
data-source add --jndi-name=java:jboss/datasources/iev --name=iev --connection-url=jdbc:postgresql://localhost:5432/iev  --driver-name=postgresql --user-name=chouette --password=chouette
data-source add --jndi-name=java:jboss/datasources/chouette --name=chouette --connection-url=jdbc:postgresql_postGIS://localhost:5432/chouette2 --driver-class=org.postgis.DriverWrapper --driver-name=postgresql --user-name=chouette --password=chouette --max-pool-size=30
exit
```

