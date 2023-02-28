FROM registry.access.redhat.com/ubi8/openjdk-11

USER root

RUN microdnf update -y && microdnf install unzip -y && microdnf clean all

USER jboss

ADD --chown=185:0 https://downloads.apache.org/mina/ftpserver/1.1.4/apache-ftpserver-1.1.4-bin.zip /tmp/apache-ftpserver.zip

ENV FTP_SERVER_DIR=/opt/apache-ftpserver-1.1.4


RUN cd /opt && \
    unzip /tmp/apache-ftpserver.zip && \
    chmod -R g+w $FTP_SERVER_DIR && \
    chown --recursive :0 $FTP_SERVER_DIR && \
    rm $FTP_SERVER_DIR/res/ftpserver.jks && \
    keytool -noprompt -storepass fuseqe -keystore $FTP_SERVER_DIR/res/ftpserver.jks -genkeypair -alias fuseqe-ftpserver -keyalg RSA -validity 3650 -dname 'CN=Unknown, OU=Unknown, O=Red Hat, L=Brno, ST=Unknown, C=CZ'

COPY ftpd-typical.xml $FTP_SERVER_DIR/res/conf/ftpd-typical.xml
COPY entrypoint.sh /opt/entrypoint.sh

WORKDIR $FTP_SERVER_DIR

EXPOSE 2121-2130

ENV USERS "admin|admin"

STOPSIGNAL SIGKILL

USER 10000

ENTRYPOINT [ "/opt/entrypoint.sh" ]
