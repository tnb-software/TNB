#!/bin/sh

echo "USERS: $USERS ::"

rm res/conf/users.properties

for USER in $USERS ; do
    echo "processing user $USER"
    NAME=$(echo $USER | cut -d'|' -f1)
    PASS=$(echo $USER | cut -d'|' -f2)

    mkdir -p /tmp/$NAME

    cat <<EOF >> res/conf/users.properties
ftpserver.user.$NAME.userpassword=$PASS
ftpserver.user.$NAME.homedirectory=/tmp/$NAME
ftpserver.user.$NAME.enableflag=true
ftpserver.user.$NAME.writepermission=true
EOF

done

echo "setting idle timeout $FTP_IDLE_TIMEOUT"

sed -i "s/__IDLE_TIMEOUT__/${FTP_IDLE_TIMEOUT:-300}/g" res/conf/ftpd-typical.xml

sed -i 's/^log4j.rootLogger=.*/log4j.rootLogger=DEBUG, R, stdout/' $FTP_SERVER_DIR/common/classes/log4j.properties
cat <<'LOGEOF' >> $FTP_SERVER_DIR/common/classes/log4j.properties
log4j.appender.stdout=org.apache.log4j.ConsoleAppender
log4j.appender.stdout.layout=org.apache.log4j.PatternLayout
log4j.appender.stdout.layout.ConversionPattern=[%5p] %d [%X{userName}] [%X{remoteIp}] %m%n
LOGEOF

exec sh ./bin/ftpd.sh res/conf/ftpd-typical.xml
