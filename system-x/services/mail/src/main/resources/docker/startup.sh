#!/bin/bash

#if [[ ! -e /root/conf/private.key ]]; then
#    keytool -genkey -alias james -keyalg RSA -keystore keystore
#    cp keystore /root/conf/
#    openssl req -new -newkey rsa:4096 -days 365 -nodes -x509 -subj "/C=US/ST=Apache/L=Fundation/O=/CN=james.apache.org" -keyout /root/conf/private.key -out /root/conf/private.csr
#fi

wait-for-it.sh --host=localhost --port=9999 --strict --timeout=0 -- ./initialdata.sh &

java -Djdk.tls.ephemeralDHKeySize=2048 \
     -classpath '/root/resources:/root/classes:/root/libs/*' \
     -Dlogback.configurationFile=/root/conf/logback.xml \
      -Dworking.directory=/root/ org.apache.james.MemoryJamesServerMain