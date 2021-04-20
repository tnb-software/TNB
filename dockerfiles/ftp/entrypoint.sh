#!/bin/sh

sed -i -e "s/%FTP_USERNAME%/$FTP_USERNAME/" -e "s/%FTP_PASSWORD%/$FTP_PASSWORD/" /app/ftpserver.json

exec /bin/ftpserver
