# mill-giter8

## Build
```mill mill-giter8.publishLocal```


## Precondition
Create the file ~/predef.sc
```
if (!ammonite.ops.exists(ammonite.ops.pwd/"build.sc"))
  ammonite.ops.write(ammonite.ops.pwd/"build.sc", "")

// This import should not be needed
import $ivy.`org.foundweekends.giter8:giter8-lib_2.13:0.13.1`

// Import the mill ExternalModule that is using giter8
import $ivy.`org.matru::mill-giter8:0.0.1`
```

## Usage
Start mill:
```
./mill -i -p ~/predef.sc
```

On the prompt, call the command:
```
plugin.Giter8.instantiate("https://github.com/mattisr/giter8-example-1.git")()
```

Use tab completion on plugin.Giter8.instantiate to list it's arguments, 
and check the sourcecode for default values.
