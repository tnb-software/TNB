FROM alpine:latest
LABEL maintainer="Salvatore Mongiardo <smongiar@redhat.com>"
RUN apk add --no-cache openssh openssh-keygen \
    && echo 'PasswordAuthentication yes' >> /etc/ssh/sshd_config \
    && echo 'PubkeyAuthentication yes' >> /etc/ssh/sshd_config \
    && adduser -h /home/redhat -s /bin/sh -D redhat

COPY start.sh /
ENTRYPOINT ["/start.sh"]
