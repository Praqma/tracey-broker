package net.praqma.tracey.broker.impl.rabbitmq;

import net.praqma.tracey.broker.api.TraceyIOError;

/**
 * <h2>Tracey RabbitMQ network error</h2>
 * <p>
 * Throw this error whenever RabbitMQ throws an exception.
 * </p>
 *
 * @author Mads
 */
public class TraceyRabbitMQError extends TraceyIOError {

    public TraceyRabbitMQError(String message, Throwable innerCause) {
        super(message, innerCause);
    }

}
