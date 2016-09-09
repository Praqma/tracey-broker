## tracey-broker

The broker module for Tracey. Requires [tracey-core](https://github.com/Praqma/tracey-core)

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

We've added a `TraceyMessageValidator` interface since we want to be able to discard messages that do not fit a certain format. Use this if mesaages must conform to a specific protocol.

```
	package net.praqma.tracey.broker;

	public interface TraceyMessageValidator {
		public void validateSend(String message) throws TraceyValidatorError;
		public void validateReceive(String message) throws TraceyValidatorError;
	}

``` 

The TraceyMessageValidator is attached to the broker, and by default will be called when `send(...)` and `receive(...)` is called in `TraceyBroker.java`

We've added new empty interface `RoutingInfo` since we want to have a generic interface post to TraceySender if we will implement more message brokers. RoutingInfo interface for classes will contain the routing information, such as in the case of RabbitMQ the routing key, message header, delivery mode, etc.     


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

		public TraceyBroker() { }

		//Tracey with preconfigured sender and reciever
		public TraceyBroker(T receiver, S sender) {
			this.sender = sender;
			this.receiver = receiver;
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

### RoutingInfoRabbitMQ

The class contains message attributes such as routing key, header, exchange name, delivery mode, etc.

```
	public class RoutingInfoRabbitMQ implements RoutingInfo {
	    private Map<String, Object> headers;
	    private int deliveryMode;
	    private String routingKey;
	    private String destination; // Exchange name
	    private String exchangeType;

	    public RoutingInfoRabbitMQ(Map<String, Object> headers, String destination, int deliveryMode, String routingKey) {
	        this.headers = headers;
	        this.destination = destination;
	        this.deliveryMode = deliveryMode;
	        this.routingKey = routingKey;
	    }

	    public RoutingInfoRabbitMQ(Map<String, Object> headers, int deliveryMode, String routingKey, String destination, String exchangeType) {
	        this.headers = headers;
	        this.deliveryMode = deliveryMode;
	        this.routingKey = routingKey;
	        this.destination = destination;
	        this.exchangeType = exchangeType;
	    }

	    public Map<String, Object> getHeaders() {
	        return headers;
	    }

	    public int getDeliveryMode() {
	        return deliveryMode;
	    }

	    public String getRoutingKey() {
	        return routingKey;
	    }

	    public String getDestination() {
	        return destination;
	    }

	    public String getExchangeType() {
	        return exchangeType;
	    }

	}

```  

### Building tracey-broker

 - Make sure you've installed the [tracey core module](https://github.com/Praqma/tracey-core) locally first using `publishToMaven` task
 - `gradle build` 

### Publishing a local artifact

If you want to publish a local artifact to maven, you can do so using the `publishToMaven` task: 

`gradle publishToMavenLocal`

### TODO

- Proper artifact management. We want to publish this somewhere. Currently we're building dependencies locally.
- Threading, `.basicConsume(...)` blocks.





 
