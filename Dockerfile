FROM jboss/wildfly:8.2.1.Final

USER root
RUN yum -y update && yum -y install wget && yum clean all
USER jboss


# Copy iev.properties
COPY docker/files/wildfly/iev.properties /etc/chouette/iev/

RUN touch /opt/jboss/wildfly/build.log
RUN chmod a+w /opt/jboss/wildfly/build.log

# Copy EAR
COPY chouette_iev/target/chouette.ear /opt/jboss/wildfly/standalone/deployments/
# Copy customized Wildfly modules and Prometheus agent
COPY target/docker/wildfly /opt/jboss/wildfly/
# Copy customized Wildfly configuration file
COPY docker/files/wildfly/standalone.conf /opt/jboss/wildfly/bin
# Copy Prometheus agent configuration file
COPY docker/files/jmx_exporter_config.yml /opt/jboss/wildfly/prometheus

# From http://stackoverflow.com/questions/20965737/docker-jboss7-war-commit-server-boot-failed-in-an-unrecoverable-manner
RUN rm -rf /opt/jboss/wildfly/standalone/configuration/standalone_xml_history \
  && mkdir -p /opt/jboss/data \
  && chown jboss:jboss /opt/jboss/data

# This argument comes from https://github.com/jboss-dockerfiles/wildfly
# It enables the admin interface.

CMD ["/opt/jboss/wildfly/bin/standalone.sh", "-b", "0.0.0.0", "-bmanagement", "0.0.0.0", "--read-only-server-config=standalone.xml"]
