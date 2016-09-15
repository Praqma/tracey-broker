package net.praqma.tracey.broker.impl.rabbitmq;

import java.util.Collections;
import java.util.Map;

public class RabbitMQDefaults {
    final public static String HOST = "localhost";
    final public static int PORT = 5672;
    final public static String USERNAME = "guest";
    final public static String PASSWORD = "guest";
    final public static boolean AUTOMATIC_RECOVERY = true;
    final public static String EXCHANGE_TYPE = "direct";
    final public static String EXCHANGE_NAME = "tracey";
    final public static int    DELEIVERY_MODE = 1;
    final public static String ROUTING_KEY = "";
    final public static Map<String, Object> HEADERS= Collections.EMPTY_MAP;
}