# TNB - System X

System-X provides a way how to deploy any external service that is used in testing.

Each System X service consists of 3 things:

- `account` - a Java object that holds any info needed to connect to the service
- `client` - a Java client for that service
- `validation` - a Java object that wraps around the `client` and `account` and provides convenient methods for interacting with the service

If the 3rd party service is publicly available, no deployment is needed. Otherwise, based on the system properties specified, the service is
deployed `locally` using [TestContainers](https://github.com/testcontainers/testcontainers-java)
or to OpenShift using docker images.
