# Generating Java classes from CRD

In order to generate java client classes from a CRD, follow the [kubernetes java client readme](https://github.com/kubernetes-client/java/blob/master/docs/generate-model-from-third-party-resources.md), with a
couple of caveats:

* Download the CRD locally since you may need to tweak it, see below
* The command is

```
docker run \
  --rm \
  -v /var/run/docker.sock:/var/run/docker.sock \
  -v "$(pwd)":"$(pwd)" \
  -ti \
  --network host \
  docker.pkg.github.com/kubernetes-client/java/crd-model-gen:v1.0.3 \
  /generate.sh \
  -u "$(pwd)/crd.yaml" \
  -n com.example\
  -p com.example \
  -o "$(pwd)"
```

make sure to replace `crd.yaml` with your file. The argument to `-n` _must_ be the value from the CRD's `.spec.group`, the argument to `-p` can be a
package name of you choosing.

* If you get `failed publishing openapi schema because it's attached non-structral-schema condition.`, add `type: object` into all occurences
  of `openAPIV3Schema` in the CRDs.
* If you get `failed publishing openapi schema because it explicitly disabled unknown fields pruning`, change all occurences
  of `preserveUnknownFields: true` in the CRDs to `false` (you might need to add this if it's not present)
* If you need to generate multiple CRDs, you can put them all in a single yaml file (separated with `---`), provided they have the same `.spec.group`


The docker command will create a new `kind` cluster, import the CRD, export it again as `OpenAPI` schema, then use that to generate the java classes. 
It would probably be possible to skip the cluster creation and just export the schema from an existing cluster.
