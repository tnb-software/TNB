FROM python:3.9.16-slim-buster

ADD *.py /app/
RUN python3 -m pip install telethon && \
    chgrp -R 0 /app && \
    chmod -R g=u /app

USER 1001

ENTRYPOINT ["tail", "-f", "/dev/null"]
