FROM jboss/wildfly:8.2.1.Final

USER root
RUN yum -y update && yum -y install wget && yum clean all
USER jboss

RUN mkdir /opt/jboss/wildfly/customization/

# Added depenedencies in docker/lib folder due to authentication issues with jforg


COPY docker/lib/postgresql-9.3-1103.jdbc41.jar /opt/jboss/wildfly/customization/postgresql-9.3-1103.jdbc41.jar
COPY docker/lib/postgis-jdbc-2.1.7.2.jar       /opt/jboss/wildfly/customization/postgis-jdbc-2.1.7.2.jar
COPY docker/lib/hibernate-spatial-4.3.jar      /opt/jboss/wildfly/modules/system/layers/base/org/hibernate/main/hibernate-spatial-4.3.jar
COPY docker/lib/jts-1.13.jar                   /opt/jboss/wildfly/modules/system/layers/base/org/hibernate/main/jts-1.13.jar

# File where sed expression has been performed:
COPY docker/files/wildfly/module.xml /opt/jboss/wildfly/modules/system/layers/base/org/hibernate/main/module.xml

# Updated JAXB implementation to work with Netex jaxb classes
COPY docker/lib/jaxb-impl-2.2.11.jar         /opt/jboss/wildfly/modules/system/layers/base/com/sun/xml/bind/main/jaxb-impl-2.2.11.jar
COPY docker/lib/jaxb-core-2.2.11.jar         /opt/jboss/wildfly/modules/system/layers/base/com/sun/xml/bind/main/jaxb-core-2.2.11.jar
COPY docker/lib/jaxb-xjc-2.2.11.jar          /opt/jboss/wildfly/modules/system/layers/base/com/sun/xml/bind/main/jaxb-xjc-2.2.11.jar
COPY docker/files/wildfly/jaxb_module.xml    /opt/jboss/wildfly/modules/system/layers/base/com/sun/xml/bind/main/module.xml

COPY docker/lib/xercesImpl-2.11.0.SP6-RB.jar /opt/jboss/wildfly/modules/system/layers/base/org/apache/xerces/main/xercesImpl-2.11.0.SP6-RB.jar
#COPY files/wildfly/xerces_module.xml /opt/jboss/wildfly/modules/system/layers/base/org/apache/xerces/main/module.xml

#Copy iev.properties
COPY docker/files/wildfly/iev.properties /etc/chouette/iev/

RUN touch /opt/jboss/wildfly/build.log
RUN chmod a+w /opt/jboss/wildfly/build.log

# Wildfly container configurations, copy and execute
COPY docker/files/wildfly/*.cli /opt/jboss/wildfly/customization/
COPY docker/files/wildfly/execute.sh /opt/jboss/wildfly/customization/
RUN /opt/jboss/wildfly/customization/execute.sh

# Deploying by copying to deployment directory
COPY chouette_iev/target/chouette.ear /opt/jboss/wildfly/standalone/deployments/

# Copy standalone customizations
COPY docker/files/wildfly/standalone.conf /opt/jboss/wildfly/bin
# From http://stackoverflow.com/questions/20965737/docker-jboss7-war-commit-server-boot-failed-in-an-unrecoverable-manner
RUN rm -rf /opt/jboss/wildfly/standalone/configuration/standalone_xml_history \
  && mkdir -p /opt/jboss/data \
  && chown jboss:jboss /opt/jboss/data

# Configuration of Prometheus agent
RUN  mkdir -p /opt/jboss/wildfly/prometheus && chown jboss:jboss /opt/jboss/wildfly/prometheus
COPY docker/lib/jmx_prometheus_javaagent-0.12.0.jar /opt/jboss/wildfly/prometheus/jmx_prometheus_javaagent.jar
COPY docker/files/jmx_exporter_config.yml /opt/jboss/wildfly/prometheus

EXPOSE 8778 9779

# Running as root, in order to get mounted volume writable:
USER root

COPY docker/files/disk_usage_notifier.sh /disk_usage_notifier.sh
RUN chmod a+x /disk_usage_notifier.sh

COPY docker/files/disk_usage_notifier.sh /disk_usage_notifier.sh






# This argument comes from https://github.com/jboss-dockerfiles/wildfly
# It enables the admin interface.

CMD ["/opt/jboss/wildfly/bin/standalone.sh", "-b", "0.0.0.0", "-bmanagement", "0.0.0.0", "--read-only-server-config=standalone.xml"]
