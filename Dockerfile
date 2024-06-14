# Build stage
FROM maven:3.9.7-eclipse-temurin-21 as build
COPY src /home/app/src
COPY pom.xml /home/app
COPY settings.xml /root/.m2/settings.xml
COPY docker /home/app/docker
ARG SERVER_USERNAME
ARG SERVER_PASSWORD
RUN mvn -f /home/app/pom.xml clean package

# Package stage
FROM eeclipse-temurin:21-jre-alpine

MAINTAINER CZERTAINLY <support@czertainly.com>

# add non root user czertainly
RUN addgroup --system --gid 10001 czertainly && adduser --system --home /opt/czertainly --uid 10001 --ingroup czertainly czertainly

COPY --from=build /home/app/docker /
COPY --from=build /home/app/target/*.jar /opt/czertainly/app.jar

WORKDIR /opt/czertainly

ENV JDBC_URL=
ENV JDBC_USERNAME=
ENV JDBC_PASSWORD=
ENV DB_SCHEMA=scheduler
ENV PORT=8080
ENV JAVA_OPTS=
ENV RABBITMQ_HOST=
ENV RABBITMQ_PORT=5672
ENV RABBITMQ_USERNAME=
ENV RABBITMQ_PASSWORD=
ENV RABBITMQ_VHOST=czertainly

USER 10001

ENTRYPOINT ["/opt/czertainly/entry.sh"]