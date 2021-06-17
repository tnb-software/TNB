#!/bin/bash

gunicorn -b 0.0.0.0:8443 --certfile /httpbin/server.cert --keyfile /httpbin/server.key httpbin:app &
gunicorn -b 0.0.0.0:8000 httpbin:app
