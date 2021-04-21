# TNB - System X

System-X provides a way how to deploy any external service that is used in testing.

Each System X service consists of 3 things:

- `account` - a Java object that holds any info needed to connect to the service
- `client` - a Java client for that service
- `validation` - a Java object that wraps around the `client` and `account` and provides convenient methods for interacting with the service

If the 3rd party service is publicly available, no deployment is needed. Otherwise, based on the system properties specified, the service is
deployed `locally` using [TestContainers](https://github.com/testcontainers/testcontainers-java)
or to OpenShift using docker images.

### Parsing accounts from credentials file

You can specify a credentials yaml file using `test.credentials.file` property with a following structure:

```yaml
services:
    aws:
        credentials:
            access_key: xxxx
            secret_key: xxxx
            region: xxxx
            account_id: xxxx
    jira:
        credentials:
            username: xxxx
            password: xxxx
    ....
```

When creating a new account for a system-x service, you can automatically populate its attributes from the credentials file. To do that, you need to
implement `Account#id(String id)` method in your account, where the id matches the credentials id from the yaml file (in the example above, `aws`,
or `jira`)
and your account fields must have the same name as the fields in the yaml file.

To obtain a new instance of the account with populated attributes, use `Accounts.get(YourAccount.class)`
