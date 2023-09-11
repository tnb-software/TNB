# Generating Java classes from CRD

The TNB framework depends on the fabric8io.kubernetes-client of version 5.12 (at the time of writing this DOC) but the *latest* CRD generator requires dependencies from kubernetes-client version 6.x.

The _io.fabric8:java-generator-cli:6.0.0_ seems to be free from those dependencies and it is working with the current TNB framework setup.

* Download the CRD locally since you may need to tweak it

### To generate Java model classes just execute:

```
jbang io.fabric8:java-generator-cli:6.0.0 -s ${CRD_SOURCE_FOLDER} -t  ${OUTPUT_FOLDER} 
```
