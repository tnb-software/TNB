FROM registry.access.redhat.com/ubi8/openjdk-17

USER jboss
ARG PROJECT_SOURCE=/projects
ARG MAVEN_MIRROR_URL=
ARG SUB_FOLDER=.
ARG JAVA_OPTS_APPEND=-Dcamel.springboot.main-run-controller=true
ARG MAVEN_ARGS_APPEND=

COPY . /projects

USER root
RUN source /opt/jboss/container/maven/default/maven.sh && maven_init && mvn -s /home/jboss/.m2/settings.xml package -f ${PROJECT_SOURCE}/${SUB_FOLDER}/pom.xml -Dmaven.repo.remote=${MAVEN_MIRROR_URL} \
    && cp ${PROJECT_SOURCE}/${SUB_FOLDER}/target/*.jar /deployments \
    && if [ -n "$(ls $PROJECT_SOURCE/devfile-resources || :)" ]; then cp $PROJECT_SOURCE/devfile-resources/* /deployments/data; fi;

RUN chown jboss:jboss -R ${PROJECT_SOURCE}
RUN chmod -R 777 ${PROJECT_SOURCE}
RUN chown jboss:jboss -R /home/jboss/.m2
RUN chmod -R 777 /home/jboss/.m2
USER jboss
