## tracey-broker

The broker module for Tracey. Requires [tracey-core](https://github.com/Praqma/tracey-core)

This module contains abstractions and concrete implementations for the type of middleware we wish to support for messaging.

Currently we have one concrete implementation. That implements the interface

```
public interface TraceyBroker {
    public String send(String payload, String destination) throws TraceyValidatorError, TraceyIOError;
    public String recieve(String source);
    public void configure(File f);
}
```

We've added a `TraceyMessageValidator` interface since we want to be able to discard messages that do not fit a certain format. Use this if mesaages must conform to a specific protocol.

```
package net.praqma.tracey.broker;

public interface TraceyMessageValidator {
    public void validate(String message) throws TraceyValidatorError;
}
``` 

### Building tracey-broker

`gradle build` 

### Publishing a local artifact

If youwa want to publish a local artifact to maven, you can do so using the `publishToMaven` task: 

`gradle publishToMavenLocal`

### TODO

- Proper artifact management. We want to publish this somewhere. Currently we're building dependencies locally.





 
