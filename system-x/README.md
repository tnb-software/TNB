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

To obtain a new instance of the account with populated attributes, use `AccountFactory.create(YourAccount.class)`

#### Composite account

You can also create an account instance by parsing multiple entries from credentials file that allows for having credential extension mechanism
of some sort. To enable this functionality, when creating a new instance, all parent classes are automatically checked for the presence of `WithId`
interface - in that case the parent ids are also used to populate the account instance.

Consider following classes and credentials file:

```java
public class ParentAccount implements Account, WithId {
    private String parentKey;

    @Override
    public String credentialsId() {
        return "parent";
    }
}

public class ChildAccount extends ParentAccount {
    private String childKey;

    @Override
    public String credentialsId() {
        return "child";
    }
}
```

```yaml
services:
    parent:
        credentials:
            parentKey: parentValue
    child:
        credentials:
            childKey: childValue
```

When instantiating the `ChildAccount` with `AccountFactory.create(ChildAccount.class)` the account is created by creating the instance from the
`parent` credentials and then the credentials with id `child` are merged into the existing object resulting in the account having both values
populated.

In case of composite accounts at least one of the given credentials id must always exist.

Consider previous set of account classes and a following credentials file:

```yaml
services:
    child:
        credentials:
            parentKey: parentValue
            childKey: childValue
```

In this case there is no credentials entry with `parent` id, but the `child` entry contains all necessary values for the account.

### Parsing accounts from HashiCorp Vault

Instead of using credentials yaml file you can also use a [HashiCorp Vault](https://www.vaultproject.io/) instance to holds your credentials.
The format of the credentials need to follow the same structure as the yaml file - you need to to store a secret named `credentials` under the id used
in the account configuration

In this case you need to specify the following properties:

```bash
test.credentials.use.vault=true
test.credentials.vault.address=https://<vault.address>
# Use either token or role configuration
test.credentials.vault.token=<token>
test.credentials.vault.role=<role>
# Pattern passed to String.format() where %s is the "credentialsId" value for the account
test.credentials.vault.path.pattern=/path/to/services/%s/credentials
```
