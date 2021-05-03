FROM fclairamb/ftpserver:latest


EXPOSE 2121-2130

ENV FTP_USERNAME=user \
    FTP_PASSWORD=user


COPY entrypoint.sh /app/entrypoint.sh
COPY ftpserver.json /app/ftpserver.json

RUN chmod g+w /app

USER 10000

ENTRYPOINT [ "/app/entrypoint.sh" ]
