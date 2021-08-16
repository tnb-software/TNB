FROM mendhak/http-https-echo

RUN chgrp -R 0 /app && \
    chmod -R g=u /app

RUN chown -R 1001:0 /app
USER 1001

ENV HTTP_PORT=8080 HTTPS_PORT=8443
EXPOSE $HTTP_PORT $HTTPS_PORT

CMD ["node", "./index.js"]
