FROM python:3.9.16-slim-bullseye

ADD *.py /app/
RUN python3 -m pip install telethon flask && \
    chgrp -R 0 /app && \
    chmod -R g=u /app

USER 1001

EXPOSE 8080

# Run HTTP server instead of tail -f /dev/null with execInContainer calls
# Reason: execInContainer has buffering/broken pipe issues on Podman
# HTTP provides reliable, consistent communication across Docker/Podman/Kubernetes
ENTRYPOINT ["python3", "/app/main.py"]
