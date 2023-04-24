# TNB CLI

If you want to use System-X services from your terminal without writing a test case you can use the `tnb` script.

## Installation
You'll need `jbang` installed and TNB build locally. Use command
`jbang app install jbang/tnb.java` to register the `tnb` command globally.

## Usage
The `tnb` CLI uses groovy shell, so any groovy language features should be valid.

You can see it in action in this demo: 

[![demo](demo.gif)](https://asciinema.org/a/585241)

### Deploying System-X services
To deploy a System-X service you can use the `deploy <System-X Classname>` command. 
By default the services are deployed locally, you can use the `--openshift` flag to deploy on OCP cluster you are currently logged in.

Or use commands `setOpenshift` and `setLocal` to use OCP or local installation always.

### Using deployed services
After deploying the service you'll be prompted with the service name to use in shell.
You can then use this name to use it as a normal System-X service. `<name>.validation().[TAB]` will help you.

Services are undeployed by default after you close the `tnb` session, or use the `undeploy` command.

### Configuration
The CLI uses `~/.tnb` directory for its configuration. `~/.tnb/init.groovy` file is executed on startup. You can use this to setup any properties, for example:

```groovy
System.setProperty('test.credentials.file', '~/credentials.yaml')
System.setProperty('openshift.kubeconfig', '~/kubeconfig.yaml')
```

#### Credentials
In a case where a service needs credentials that can't be found then you'll be asked to provide the credentials.
These credentials are then stored in `~/.tnb/credentials/<id>.properties` and will be used for later use.
