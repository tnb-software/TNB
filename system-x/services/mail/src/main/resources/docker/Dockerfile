# Run James
#
# VERSION 1.0

FROM apache/james:memory-3.7.0

# Install git
RUN apt-get update
RUN apt-get install -y git openssl

WORKDIR /root

RUN git clone https://github.com/vishnubob/wait-for-it.git wait-for-it
RUN cp /root/wait-for-it/wait-for-it.sh /usr/bin/wait-for-it.sh

COPY startup.sh /root
COPY initialdata.sh /root
COPY keystore /root/conf/
COPY imapserver.xml /root/conf

RUN chmod +x /root/startup.sh
RUN chmod +x /root/initialdata.sh

ENTRYPOINT ["./startup.sh"]
