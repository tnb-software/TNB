FROM --platform=linux/amd64 maven:3.9-ibm-semeru-21-jammy AS builder

WORKDIR /build
COPY pom.xml .
COPY src/ src/
RUN mvn package -DskipTests -q

FROM ibm-semeru-runtimes:open-21-jre-jammy

RUN useradd -m -d /home/test -s /bin/sh test \
    && mkdir -p /opt/mina-sshd \
    && chown -R test:test /opt/mina-sshd

COPY --from=builder /build/target/mina-sshd-server.jar /opt/mina-sshd/mina-sshd-server.jar
COPY start.sh /opt/start.sh
RUN chmod +x /opt/start.sh

EXPOSE 22

ENTRYPOINT ["/opt/start.sh"]
