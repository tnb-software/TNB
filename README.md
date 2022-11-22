# TNB - The New Beginning framework
```
   _     _      _     _      _     _     
  (c).-.(c)    (c).-.(c)    (c).-.(c)    
   / ._. \      / ._. \      / ._. \     
 __\( Y )/__  __\( Y )/__  __\( Y )/__   
(_.-/'-'\-._)(_.-/'-'\-._)(_.-/'-'\-._)  
   || T ||      || N ||      || B ||     
 _.' `-' '._  _.' `-' '._  _.' `-' '._   
(.-./`-'\.-.)(.-./`-'\.-.)(.-./`-'\.-.)  
 `-'     `-'  `-'     `-'  `-'     `-'   
```
The New Beginning (TNB) is the Fuse QE Team developed framework used for Camel-related products testing as well as for testing external services on local OCP. The main benefit of the TNB is agnostic and generic configuration of TNB-based tests which make a story of adding new products and new services into the testing scope efficient, smooth and quick to perform.

---

For more information see the readme files in the modules.

[common](common/README.md)

[fuse-products](fuse-products/README.md)

[system-x](system-x/README.md)

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
    - Use a local checkstyle file from the `checkstyle` directory  and set some description (for example `TNB configuration`)
    - Mark the newly added configuration as `active`


### General tips

#### Generating CRD classes

See [here](docs/generating-crd-classes.md)


