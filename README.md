# TNB - The New Beginning framework

TNB is a collection of JUnit 5 extensions designed for testing with external services referred to as [System-X services](./system-x/services).

For testing Camel based applications see [fuse-products](./fuse-products/README.md) README file.

For using System-X services from your terminal see [jbang integration](./jbang/README.md) README file.

---

There are two categories of System-X services: `Remote` and `Self-hosted`.

Remote services are internet-facing services that can be accessed publicly. Some examples of such services include `Twitter`, `Salesforce`, and
various
`cloud providers` like AWS, Google, and Azure.

On the other hand, self-hosted services are typically internal services hosted on-premises or in private cloud environments. These services may
include messaging systems like `Kafka`, file transfer protocol (`FTP`) servers, and various types of `databases` such as Cassandra, Postgres, MySQL,
and others.

Each System-X service comprises three parts:

- `account` - a Java object that contains all the information required to connect to the service
- `client` - a Java client used to access the service
- `validation` - a Java object that wraps around the `client` and `account` and offers convenient methods to interact with the service.

Self-hosted services can be deployed:
- locally using [TestContainers](https://github.com/testcontainers/testcontainers-java)
  - using `test.use.openshift=false` property
- as deployments on [OpenShift](https://www.redhat.com/en/technologies/cloud-computing/openshift)
    - using `test.use.openshift=true` property
- externally and use System-X service to connect to the external service (only available for a subset of services)
    - using `tnb.<serviceName>.host` property

---

## Example usage

```java
public class KafkaTest {
    @RegisterExtension
    public static Kafka kafka = ServiceFactory.create(Kafka.class);

    @Test
    public void testWithKafka() {
        final String topic = "myTopic";
        final String message = "Hello kafka!";
        kafka.validation().produce(topic, message);

        final List<ConsumerRecord<String, String>> records = kafka.validation().consume(topic);
        Assertions.assertEquals(1, records.size());
        Assertions.assertEquals(message, records.get(0).value());
    }
}
```

In this case, a `Kafka` System-X service instance is created using
[ServiceFactory](./system-x/common/src/main/java/software/tnb/common/service/ServiceFactory.java) class. The default environment to run the test
is a local machine, so in this case the Kafka docker container is started automatically before running the
tests (using JUnit's `@BeforeAll` method) and after the test method is executed, Kafka is automatically undeployed (again,
using JUnit's `@AfterAll` method).

## Example service

Each service extends an abstract [Service](./system-x/common/src/main/java/software/tnb/common/service/Service.java) class that provides
the `account`,
`client` and `validation` fields and methods.

### Remote service

As stated earlier, in the case of a remote service, there is no requirement to deploy the service.
Consequently, this service only establishes a connection to the remote service.

```java

@AutoService(MyService.class)
public class MyService implements Service {
    private MyServiceAccount account;
    private MyServiceClient client;
    private MyServiceValidation validation;

    public MyServiceAccount account() {
        if (account == null) {
            account = AccountFactory.create(MyServiceAccount.class);
        }
        return account;
    }

    protected MyServiceClient client() {
        client = new MyServiceClient("https://myservice.com");
        return client;
    }

    public MyServiceValidation validation() {
        return validation;
    }

    @Override
    public void afterAll(ExtensionContext extensionContext) throws Exception {
        if (client != null) {
            client.close();
        }
    }

    @Override
    public void beforeAll(ExtensionContext extensionContext) throws Exception {
        validation = new MyServiceValidation(client(), account());
    }
}
```

### Self-hosted service

Every self-hosted service must be able to operate in both deployment environments, local and OpenShift. As demonstrated in the example,
a `ServiceFactory` is utilized to construct a service instance. The correct implementation (local or OpenShift) is selected and deployed based on the
`test.use.openshift` system property and the class name.

A sample self-hosted System-X service might appear as follows:

```java
public abstract class MyService implements Service, WithDockerImage {
    // Account, Client, Validation with methods as in external service
    public abstract String hostname();

    public String defaultImage() {
        return "quay.io/myorganization/myimage:1.0";
    }
}
```

```java

@AutoService(MyService.class)
public class LocalMyService extends MyService implements Deployable {
    // TestContainers container that runs the given docker image
    private MyServiceContainer container;

    @Override
    public void deploy() {
        // start the container
    }

    @Override
    public void undeploy() {
        // stop the container
    }

    @Override
    public void openResources() {
        // connect the client to the service
    }

    @Override
    public void closeResources() {
        // close the client
    }

    @Override
    public String hostname() {
        // example method that is different for each deployment
    }
}
```

```java

@AutoService(MyService.class)
// WithExternalHostname is the hostname where the client should connect
public class OpenshiftMyService extends MyService implements OpenshiftDeployable, WithExternalHostname {
    @Override
    public void create() {
        // deploy the service
    }

    @Override
    public void undeploy() {
        // undeploy the service
    }

    @Override
    public void openResources() {
        // connect the client to the service
    }

    @Override
    public boolean isReady() {
        // a condition when the service deployment is ready
    }

    @Override
    public boolean isDeployed() {
        // a condition when the service is already deployed in the namespace and shouldn't be deployed again
    }

    @Override
    public String externalHostname() {
        // for example a path to the route in openshift
    }

    @Override
    public void closeResources() {
        // close the client
    }
}
```

### Overriding the default image

As observed, the Docker image used is hardcoded within the System-X service. To substitute the image without altering the source code, you can utilize
the system property that is derived from the System-X service name. For example, to override the docker image for the `MongoDB` service, you would use
the `tnb.mongodb.image` property.

### Configuring services

Some services may have additional configurations. In such cases, the service would have
a [ServiceConfiguration](./system-x/common/src/main/java/software/tnb/common/service/configuration/ServiceConfiguration.java)  class and would extend
the [ConfigurableService](./system-x/common/src/main/java/software/tnb/common/service/ConfigurableService.java) class. You can refer to
the [Splunk](./system-x/services/splunk/src/main/java/software/tnb/splunk/service/Splunk.java)
System-X service and its [Configuration](./system-x/services/splunk/src/main/java/software/tnb/splunk/service/configuration/SplunkConfiguration.java)
class for an example.

Afterward, you can configure the service using the `ServiceFactory` class

```java
ServiceFactory.create(Splunk.class,config->config.protocol(SplunkProtocol.HTTP));
```

### Accounts

As mentioned earlier, each System-X service has an associated [Account](./system-x/common/src/main/java/software/tnb/common/account/Account.java)
class. The values in the account classes are hardcoded for self-hosted services, however, for external services, exposing the secrets in the test
repository is not recommended.

Currently, TNB enables loading credentials from either a [HashiCorp vault](https://www.vaultproject.io/) or from YAML file (or YAML string).
You can use the `AccountFactory#create(YourAccount.class)` method in the
dedicated [AccountFactory](./system-x/common/src/main/java/software/tnb/common/account/AccountFactory.java) class to obtain the account instance with
filled attributes.

#### Parsing accounts from HashiCorp Vault

The credentials format must conform to the same structure as the YAML file. You must store a secret named credentials under the `ID` used
in the account configuration.

You must provide the following properties in this scenario:

```bash
test.credentials.use.vault=true
test.credentials.vault.address=https://<vault.address>
# Use either token or roleId + secretId configuration
test.credentials.vault.token=<token>
test.credentials.vault.role.id=<roleId>
test.credentials.vault.secret.id=<secretId>
# Pattern passed to String.format() where %s is the id of the credentials for the account
test.credentials.vault.path.pattern=/path/to/services/%s/credentials
```

#### Loading accounts from the credentials file

You can set a credentials YAML file by using the `test.credentials.file` property with the following structure:

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

To load the credentials from the YAML file, you need to implement `Account#id(String id)` method in your account, where the `id` matches the
credentials id from the yaml file (in the example above, `aws`, or `jira`). Your account fields must have the same names as the fields in the YAML
file.

#### Loading accounts from the system property

Instead of creating a separate file for the credentials, you can also load the credentials directly from a YAML string. The format of the string
is exactly the same as the credentials file. In this case, you need to set the content to the the `test.credentials` property

#### Overriding account IDs

Each loaded account must have the `id` defined. If you need to override the default id, you can do so by setting a system property derived from the
account class name. For example, to override the default `id` for `AWSAccount` class, you can set the `tnb.awsaccount.id` property.

#### Composite account

An account instance can also be created by parsing multiple entries from a credentials file, which allows for a credential extension mechanism of some
sort. To enable this functionality, when creating a new instance, all parent classes are automatically checked for the presence
of [WithId](./system-x/common/src/main/java/software/tnb/common/account/WithId.java)
interface - in that case the parent ids are also used to populate the account instance.

Let's look at the following classes and credentials file as an example:

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

If you instantiate the `ChildAccount` using `AccountFactory.create(ChildAccount.class)`, the resulting account is created by first creating an
instance
from the `parent` credentials, and then the credentials with id `child` are merged into the existing object. This results in the account having both
sets
of values populated.

However, in the case of composite accounts, it is necessary that at least one of the given credentials ids exist.

Let's take a look at the account classes introduced earlier and the following credentials file:

```yaml
services:
    child:
        credentials:
            parentKey: parentValue
            childKey: childValue
```

In this scenario, the credentials file does not have an entry with the parent id, but it contains all the necessary values for the child account.

### Validation

Each System-X service is accompanied by a validation class which offers methods for interacting with the service through the client, making it easier
to use when the client API is not straightforward. The validation class should be continuously updated as the usage of the System-X service evolves.

### Contributing

To minimize the amount of changes done in PR, please use code style configuration from this repository.

The code style is in the [EditorConfig](https://editorconfig.org/) [file](.editorconfig).

#### IntelliJ IDEA setup

##### EditorConfig support

- Enable `EditorConfig` plugin if it is not already enabled
- Go to `Settings` -> `Editor` -> `Code Style`
    - check `Enable EditorConfig support` if not already enabled
    - in `Formatter Control` check `Enable formatter markers in comments` if not already enabled
- `Optional`: you can also use the [Save Actions](https://plugins.jetbrains.com/plugin/7642-save-actions) plugin to automatically reformat the code
  and imports on each save

##### CheckStyle

- Install `CheckStyle-IDEA` plugin
- Go to `Settings` -> `Tools` -> `Checkstyle`
    - Checkstyle version: any version from `8.24` to `8.43` (latest at the time of writing) should work
      (if you select some version and then add the config file and the file is loaded successfully, you should be good to go)
    - Scan scope `Only Java sources (including tests)`
    - Set `Treat Checkstyle errors as warnings` by your personal preference
    - Add a configuration file - click on `+` in the `Configuration File` section
    - Use a local checkstyle file from the `checkstyle` directory and set some description (for example `TNB configuration`)
    - Mark the newly added configuration as `active`

---

For more information see the readme files in the modules.

[common](common/README.md)

[fuse-products](fuse-products/README.md)
