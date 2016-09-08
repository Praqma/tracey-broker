package net.praqma.tracey.broker.rabbitmq;

import java.util.HashMap;
import java.util.Map;

public class RabbitMQDefaults {
    final public static String HOST = "localhost";
    final public static int PORT = 5672;
    final public static String USERNAME = "guest";
    final public static String PASSWORD = "guest";
    final public static boolean AUTOMATIC_RECOVERY = true;
    final public static RabbitMQExchangeType EXCHANGE_TYPE = RabbitMQExchangeType.DIRECT;
    final public static String EXCHANGE_NAME = "tracey";
    final public static int    DELEIVERY_MODE = 0;
    final public static String ROUTING_KEY = "";
    final public static Map<String, String> HEADERS= new HashMap<>();
}