package net.praqma.tracey.broker.impl.rabbitmq;

import java.util.Collections;
import java.util.Map;

public class RabbitMQDefaults {
    public static final String HOST = "localhost";
    public static final int PORT = 5672;
    public static final String USERNAME = "guest";
    public static final String PASSWORD = "guest";
    public static final boolean AUTOMATIC_RECOVERY = true;
    public static final String EXCHANGE_TYPE = "direct";
    public static final String EXCHANGE_NAME = "tracey";
    public static final int    DELEIVERY_MODE = 1;
    public static final String ROUTING_KEY = "";
    public static final Map<String, Object> HEADERS= Collections.EMPTY_MAP;
}