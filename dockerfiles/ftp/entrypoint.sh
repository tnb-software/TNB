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

exec sh ./bin/ftpd.sh res/conf/ftpd-typical.xml
