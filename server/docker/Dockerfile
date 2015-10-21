# Provides OpenIdeate (from master branch) running on top of Ubuntu 14.04.2 with
# Oracle JDK 8.
FROM ubuntu:14.04.2
MAINTAINER Thane Thomson <thane.thomson@dstvdm.com>

# Update and upgrade Ubuntu packages
RUN apt-get update && apt-get -y upgrade && apt-get -y --fix-missing install unzip git

# Set up the JDK
COPY files/jdk-8u45-linux-x64.tar.gz /tmp/
RUN cd /tmp && tar -xf /tmp/jdk-8u45-linux-x64.tar.gz && mkdir /usr/lib/jvm && mv /tmp/jdk1.8.0_45 /usr/lib/jvm/oraclejdk8
ENV LANG=C.UTF-8
ENV J2SDKDIR=/usr/lib/jvm/oraclejdk8
ENV J2REDIR=/usr/lib/jvm/oraclejdk8/jre
ENV JAVA_HOME=/usr/lib/jvm/oraclejdk8
ENV DERBY_HOME=/usr/lib/jvm/oraclejdk8/db
ENV PATH=$PATH:/usr/lib/jvm/oraclejdk8/bin:/usr/lib/jvm/oraclejdk8/db/bin:/usr/lib/jvm/oraclejdk8/jre/bin

# Install Typesafe Activator
COPY files/typesafe-activator-1.3.6.zip /opt/
RUN cd /opt && unzip typesafe-activator-1.3.6.zip -d . && rm typesafe-activator-1.3.6.zip && mv activator-dist-1.3.6 activator && chmod +x ./activator/activator
ENV PATH=$PATH:/opt/activator

# Get the OpenIdeate repo on the master branch
RUN cd /opt && git clone https://github.com/thanethomson/openideate.git && mkdir /opt/openideate/server/openideate/logs
# Copy the application's production config across
COPY files/application_production.conf /opt/openideate/server/openideate/conf/
# Build it so it can pull in the dependencies prior to starting the container
RUN cd /opt/openideate/server/openideate && activator compile
# By default the application will expose itself on port 9000
EXPOSE 9000
# Keep our logs
VOLUME /opt/openideate/server/openideate/logs
# Make sure we have the right config file configured for production
ENV JAVA_OPTS="$JAVA_OPTS -Dconfig.resource=application_production.conf"
WORKDIR /opt/openideate/server/openideate

# Our primary command
CMD ["activator", "run"]
