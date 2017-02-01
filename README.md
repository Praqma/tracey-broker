---
maintainer: andrey9kin
---
# tracey-broker

The broker module for Tracey project.
This module contains abstractions and concrete implementations for the type of middleware we wish to support for messaging.
The Broker class holds references to both sender and reciever, two seperate interfaces

![Design Diagram](/docs/images/tracey2.png)

### TraceySender.java

```
	package net.praqma.tracey.broker;
	
	public interface TraceySender <T extends RoutingInfo>{
    	public String send(String payload, T data) throws TraceyIOError;
	}

```

### TraceyReciver.java

```
	package net.praqma.tracey.broker;

	public interface TraceyReceiver <T extends RoutingInfo> {
 	   public String receive(T data) throws TraceyIOError;
	}
```

### RoutingInfo.java

```
public interface RoutingInfo {
}
```

`RoutingInfo` is an empty interface to have a generic way of providing routing info about connection to send and receive methods.
In the case of RabbitMQ RoutingInfo implementation contains the routing key, message header, delivery mode, exchange name and exchange type.


### TraceyBroker.java

TracyBroker is an abstract class that contains an implementation of a sender and a receiver. That means for any given broker middleware, we can subclass and add specific implementations.

```
	public abstract class TraceyBroker<T extends TraceyReceiver, S extends TraceySender> {

		protected T receiver;
		protected S sender;
		
		public String send(String payload, RoutingInfo data) throws TraceyValidatorError, TraceyIOError {
			return sender.send(payload, data);
		}

		public String receive(RoutingInfo data) throws TraceyValidatorError, TraceyIOError {
			return receiver.receive(data);
		}
		....
	}
```

## RabbitMQ Implementation. Examples

### Create from config file

First create configuration file. See example below
```
broker {
    rabbitmq {
    	connection {
        	host = 'some.host.name'
        	port = 4444
        	userName = 'myuser'
        	password = 's0m3p4ss'
        	automaticRecovery = true
        }
        routingInfo {
        	exchangeName = 'stacie'
        	exchangeType = 'fanout'
        	routingKey = ''
        	deliveryMode = 1
        	headers {
        		someKey = 'someValue'
        		someKey1 = 0
        	}
        }
    }
}
```

Then create broker

```
final File configFile = new File("path to configuration file");
final TraceyRabbitMQBrokerImpl broker = new TraceyRabbitMQBrokerImpl(configFile);
final RabbitMQRoutingInfo info = RabbitMQRoutingInfo.buildFromConfigFile(configFile);
```

Note that filters for receiver is not covered by the configuration file.
You will need to set them separately

```
broker.getReceiver().setFilters(...)
```

And then send

```
broker.getSender().send("Hello!", info);
```

or receive

```
broker.getReceiver().receive(info);
```

Please note that you can use environment variable names for user name and password to avoid storing them
in plain text. Use the following format - %SOMETEXT%, ${SOMETEXT}, $SOMETEXT, $SOMETEXT$. See example below

```
broker {
    rabbitmq {
    	connection {
        	host = 'some.host.name'
        	port = 4444
        	userName = '${USERNAME}'
        	password = '${PASSWORD}'
        	automaticRecovery = true
        }
    }
}
```

Also, you don't have to specify all fields in the configuration file - if some piece is not provided then
default value will be used. Check RabbitMQDefaults to see them.
See example below

```
broker {
    rabbitmq {
    	connection {
        	host = 'my.host'
        	userName = '${USERNAME}'
        	password = '${PASSWORD}'
        }
    }
}
```

### Using constructors

TBD

## Build, test, contribute

### Building tracey-broker

Simply run `gradlew build`

### Get the latest builds

Tracey broker built using [JitPack](https://jitpack.io). See [instructions](https://jitpack.io/#Praqma/tracey-broker) how to add it as dependency

### TODO

- Threading, `.basicConsume(...)` blocks.





 
