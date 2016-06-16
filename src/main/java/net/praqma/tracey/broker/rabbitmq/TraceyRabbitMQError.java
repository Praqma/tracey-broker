package net.praqma.tracey.broker.rabbitmq;

import net.praqma.tracey.broker.TraceyIOError;

public class TraceyRabbitMQError extends TraceyIOError {

    public TraceyRabbitMQError(String message, Throwable innerCause) {
        super(message, innerCause);
    }


}
