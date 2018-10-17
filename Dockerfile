FROM jboss/wildfly:8.2.1.Final

ARG NEXUS_USER
ARG NEXUS_PASS

USER root
RUN yum -y update && yum -y install wget && yum clean all
USER jboss

RUN mkdir /opt/jboss/wildfly/customization/
RUN echo "Downloading with user ${NEXUS_USER}"

RUN wget --http-user=${NEXUS_USER} --http-password=${NEXUS_PASS} https://nexus.rutebanken.org/content/groups/public/org/hibernate/hibernate-spatial/4.3/hibernate-spatial-4.3.jar   --directory-prefix /opt/jboss/wildfly/modules/system/layers/base/org/hibernate/main/
RUN wget --http-user=${NEXUS_USER} --http-password=${NEXUS_PASS} https://nexus.rutebanken.org/content/groups/public/com/vividsolutions/jts/1.13/jts-1.13.jar                        --directory-prefix /opt/jboss/wildfly/modules/system/layers/base/org/hibernate/main/
RUN wget --http-user=${NEXUS_USER} --http-password=${NEXUS_PASS} https://nexus.rutebanken.org/content/groups/public/com/sun/xml/bind/jaxb-impl/2.2.11/jaxb-impl-2.2.11.jar          --directory-prefix /opt/jboss/wildfly/modules/system/layers/base/com/sun/xml/bind/main/
RUN wget --http-user=${NEXUS_USER} --http-password=${NEXUS_PASS} https://nexus.rutebanken.org/content/groups/public/com/sun/xml/bind/jaxb-core/2.2.11/jaxb-core-2.2.11.jar          --directory-prefix /opt/jboss/wildfly/modules/system/layers/base/com/sun/xml/bind/main/
RUN wget --http-user=${NEXUS_USER} --http-password=${NEXUS_PASS} https://nexus.rutebanken.org/content/groups/public/com/sun/xml/bind/jaxb-xjc/2.2.11/jaxb-xjc-2.2.11.jar            --directory-prefix /opt/jboss/wildfly/modules/system/layers/base/com/sun/xml/bind/main/
RUN wget --http-user=${NEXUS_USER} --http-password=${NEXUS_PASS} https://nexus.rutebanken.org/content/groups/public/xerces/xercesImpl/2.11.0.SP6-RB/xercesImpl-2.11.0.SP6-RB.jar    --directory-prefix /opt/jboss/wildfly/modules/system/layers/base/org/apache/xerces/main/
RUN wget --http-user=${NEXUS_USER} --http-password=${NEXUS_PASS} https://nexus.rutebanken.org/content/groups/public/net/postgis/postgis-jdbc/2.1.7.2/postgis-jdbc-2.1.7.2.jar       --directory-prefix /opt/jboss/wildfly/customization/
RUN wget http://jump.rutebanken.org/postgresql-9.3-1103.jdbc41.jar --directory-prefix /opt/jboss/wildfly/customization/


# COPY postgresql-9.3-1103.jdbc41.jar /opt/jboss/wildfly/customization/postgresql-9.3-1103.jdbc41.jar
# COPY postgis-jdbc-2.1.7.2.jar /opt/jboss/wildfly/customization/postgis-jdbc-2.1.7.2.jar
# COPY hibernate-spatial-4.3.jar /opt/jboss/wildfly/modules/system/layers/base/org/hibernate/main/hibernate-spatial-4.3.jar
# COPY jts-1.13.jar /opt/jboss/wildfly/modules/system/layers/base/org/hibernate/main/jts-1.13.jar

# File where sed expression has been performed:
COPY docker/files/wildfly/module.xml /opt/jboss/wildfly/modules/system/layers/base/org/hibernate/main/module.xml

# Updated JAXB implementation to work with Netex jaxb classes
#COPY jaxb-impl-2.2.11.jar /opt/jboss/wildfly/modules/system/layers/base/com/sun/xml/bind/main/jaxb-impl-2.2.11.jar
#COPY jaxb-core-2.2.11.jar /opt/jboss/wildfly/modules/system/layers/base/com/sun/xml/bind/main/jaxb-core-2.2.11.jar
#COPY jaxb-xjc-2.2.11.jar /opt/jboss/wildfly/modules/system/layers/base/com/sun/xml/bind/main/jaxb-xjc-2.2.11.jar
COPY docker/files/wildfly/jaxb_module.xml /opt/jboss/wildfly/modules/system/layers/base/com/sun/xml/bind/main/module.xml

#COPY xercesImpl-2.11.0.SP6-RB.jar /opt/jboss/wildfly/modules/system/layers/base/org/apache/xerces/main/xercesImpl-2.11.0.SP6-RB.jar
#COPY files/wildfly/xerces_module.xml /opt/jboss/wildfly/modules/system/layers/base/org/apache/xerces/main/module.xml

#Copy iev.properties
COPY docker/files/wildfly/iev.properties /etc/chouette/iev/

RUN touch /opt/jboss/wildfly/build.log
RUN chmod a+w /opt/jboss/wildfly/build.log

# Wildfly container configurations, copy and execute
COPY docker/files/wildfly/*.cli /opt/jboss/wildfly/customization/
COPY docker/files/wildfly/execute.sh /opt/jboss/wildfly/customization/
RUN /opt/jboss/wildfly/customization/execute.sh

# Overriding previously installed java version:
RUN curl -L http://jump.rutebanken.org/jdk-8u144-linux-x64.tar.gz > jdk.tgz
RUN tar xzf jdk.tgz
ENV JAVA_HOME /opt/jboss/jdk1.8.0_144/

# Deploying by copying to deployment directory
COPY chouette_iev/target/chouette.ear /opt/jboss/wildfly/standalone/deployments/

# Copy standalone customizations
COPY docker/files/wildfly/standalone.conf /opt/jboss/wildfly/bin
# From http://stackoverflow.com/questions/20965737/docker-jboss7-war-commit-server-boot-failed-in-an-unrecoverable-manner
RUN rm -rf /opt/jboss/wildfly/standalone/configuration/standalone_xml_history \
  && mkdir -p /opt/jboss/data \
  && chown jboss:jboss /opt/jboss/data

# Agent-bond setup largely copied from:
# https://hub.docker.com/r/fabric8/java-jboss-openjdk8-jdk/~/dockerfile/
RUN mkdir -p /opt/jboss/wildfly/agent-bond \
 && curl http://central.maven.org/maven2/io/fabric8/agent-bond-agent/1.0.2/agent-bond-agent-1.0.2.jar \
          -o /opt/jboss/wildfly/agent-bond/agent-bond.jar \
 && chmod 444 /opt/jboss/wildfly/agent-bond/agent-bond.jar
ADD docker/files/jmx_exporter_config.yml /opt/jboss/wildfly/agent-bond/
EXPOSE 8778 9779

# Running as root, in order to get mounted volume writable:
USER root

COPY docker/files/disk_usage_notifier.sh /disk_usage_notifier.sh
RUN chmod a+x /disk_usage_notifier.sh

COPY docker/files/disk_usage_notifier.sh /disk_usage_notifier.sh






# This argument comes from https://github.com/jboss-dockerfiles/wildfly
# It enables the admin interface.

CMD ["/opt/jboss/wildfly/bin/standalone.sh", "-b", "0.0.0.0", "-bmanagement", "0.0.0.0", "--read-only-server-config=standalone.xml"]
