## tracey-broker

The broker module for Tracey. Requires [tracey-core](https://github.com/Praqma/tracey-core)

This module contains abstractions and concrete implementations for the type of middleware we wish to support for messaging.

The Broker class holds references to both sender and reciever, two seperate interfaces

![Design Diagram](/docs/images/tracey2.png)

### TraceySender.java

```
	package net.praqma.tracey.broker;

	public interface TraceySender {
		public String send(String payload, String destination) throws TraceyValidatorError, TraceyIOError;
	}
```

### TraceyReciver.java

```
	package net.praqma.tracey.broker;

	public interface TraceyReceiver {
		public String receive(String source);
	}
```

We've added a `TraceyMessageValidator` interface since we want to be able to discard messages that do not fit a certain format. Use this if mesaages must conform to a specific protocol.

```
	package net.praqma.tracey.broker;

	public interface TraceyMessageValidator {
		public void validateSend(String message) throws TraceyValidatorError;
		public void validateReceive(String message) throws TraceyValidatorError;
	}

``` 

The TraceyMessageValidator is attached to the broker, and by default will be called when `send(...)` and `receive(...)` is called in `TraceyBroker.java`

### TraceyBroker.java

TracyBroker is an abstract class that contains an implementation of a sender and a receiver. That means for any given broker middleware, we can subclass and add specific implementations.

```
	public abstract class TraceyBroker<T extends TraceyReceiver, S extends TraceySender> {

		protected T receiver;
		protected S sender;
		protected TraceyMessageValidator validator;

		public String send(String payload, String destination) throws TraceyValidatorError, TraceyIOError {
			if(validator != null) {
				validator.validateSend(payload);
			}
			return sender.send(payload, destination);
		}

		public String receive(String destination) throws TraceyValidatorError, TraceyIOError {
			if(validator != null) {
				validator.validateReceive(destination);
			}
			return receiver.receive(destination);
		}

		public TraceyBroker() { }

		//Tracey with preconfigured sender and reciever
		public TraceyBroker(T receiver, S sender) {
			this.sender = sender;
			this.receiver = receiver;
		}

		/**
		 * @return the validator
		 */
		public TraceyMessageValidator getValidator() {
			return validator;
		}

		/**
		 * @param validator the validator to set
		 */
		public void setValidator(TraceyMessageValidator validator) {
			this.validator = validator;
		}

		/**
		 * @return the receiver
		 */
		public T getReceiver() {
			return receiver;
		}

		/**
		 * @param receiver the receiver to set
		 */
		public void setReceiver(T receiver) {
			this.receiver = receiver;
		}

		/**
		 * @return the sender
		 */
		public S getSender() {
			return sender;
		}

		/**
		 * @param sender the sender to set
		 */
		public void setSender(S sender) {
			this.sender = sender;
		}
	}
```

## RabbitMQ Implementation

The first version of the broker contains one concrete imlpementation that uses `RabbitMQ` as the message broker.

This implmentation is used in our experimental Jenkins plugin. When a broker is created, you get 1 randomly generated queue, that attaches itself to a shared exchange. If you want to 
override how the receiver should handle messages, you can override the MessageHandler for the Receiver, like so: 

```
	public class TraceyBuildStarter implements TraceyRabbitMQMessageHandler {

		private AbstractProject<?,?> project;

		public TraceyBuildStarter(final AbstractProject<?,?> project) {
			this.project = project;
		}

		@Override
		public void handleDelivery(String string, Envelope envlp, AMQP.BasicProperties bp, byte[] bytes) throws IOException {
			project.scheduleBuild2(3, new Cause.UserIdCause(), new TraceyAction(new String(bytes, "UTF-8")));
		}
	}
```

And then attaching it to the Broker: 

```
	TraceyRabbitMQBrokerImpl p = new TraceyRabbitMQBrokerImpl();
	p.getReceiver().setHandler(new TraceyBuildStarter(project));
	p.receive(exchange);
```


### Building tracey-broker

`gradle build` 

### Publishing a local artifact

If youwa want to publish a local artifact to maven, you can do so using the `publishToMaven` task: 

`gradle publishToMavenLocal`

### TODO

- Proper artifact management. We want to publish this somewhere. Currently we're building dependencies locally.
- Threading, `.basicConsume(...)` blocks.





 
