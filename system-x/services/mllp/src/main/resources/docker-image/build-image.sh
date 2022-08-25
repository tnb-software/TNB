#!/bin/bash

mvn clean package && docker build -t mllp-test-server:latest .
