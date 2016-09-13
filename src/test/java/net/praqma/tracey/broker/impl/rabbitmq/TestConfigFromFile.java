package net.praqma.tracey.broker.impl.rabbitmq;

import java.io.File;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import static org.junit.Assert.assertEquals;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.rule.PowerMockRule;

@PrepareForTest({System.class, RabbitMQConnection.class})
public class TestConfigFromFile {

    @Rule
    public PowerMockRule r = new PowerMockRule();

    static final Map<String,String> ENV = new HashMap<>();

    static {
        ENV.put("RABBITMQ_USER", "rabbituser");
        ENV.put("RABBITMQ_PW", "rabbitpw");
    }

    @Test
    public void parseSenderConfigFile () throws Exception {
        URI path = TestConfigFromFile.class.getResource("broker.config").toURI();
        File f = new File(path);
        TraceyRabbitMQBrokerImpl impl = new TraceyRabbitMQBrokerImpl(f);

        TraceyRabbitMQReceiverImpl receiver = impl.getReceiver();
        assertEquals("some.host.name", receiver.getConnection().getHost());
        assertEquals("myuser", receiver.getConnection().getUserName());
        assertEquals("s0m3p4ss", receiver.getConnection().getPassword());
        assertEquals(true, receiver.getConnection().isAutomaticRecoveryEnabled());
    }

    @Test
    public void parseReceiverConfigFile () throws Exception {
        URI path = TestConfigFromFile.class.getResource("broker.config").toURI();
        File f = new File(path);
        TraceyRabbitMQBrokerImpl impl = new TraceyRabbitMQBrokerImpl(f);

        TraceyRabbitMQSenderImpl sender = impl.getSender();
        assertEquals("some.host.name", sender.getConnection().getHost());
        assertEquals("myuser", sender.getConnection().getUserName());
        assertEquals("s0m3p4ss", sender.getConnection().getPassword());
        assertEquals(true, sender.getConnection().isAutomaticRecoveryEnabled());
    }

    @Test
    public void parseRoutingInfoConfigFile () throws Exception {
        URI path = TestConfigFromFile.class.getResource("broker.config").toURI();
        File f = new File(path);
        RabbitMQRoutingInfo info = RabbitMQRoutingInfo.buildFromConfigFile(f);

        assertEquals("stacie", info.getExchangeName());
        assertEquals("fanout", info.getExchangeType());
        assertEquals(1, info.getDeliveryMode());
        assertEquals("", info.getRoutingKey());
        assertEquals(2, info.getHeaders().size());
        assertEquals("someValue", info.getHeaders().get("someKey"));
        assertEquals(0, info.getHeaders().get("someKey1"));
    }

    @Test
    public void parseEmptyConfigFile() throws Exception {
        URI path = TestConfigFromFile.class.getResource("broker_empty.config").toURI();
        File f = new File(path);
        TraceyRabbitMQBrokerImpl impl = new TraceyRabbitMQBrokerImpl(f);

        TraceyRabbitMQReceiverImpl receiver = impl.getReceiver();
        assertEquals(RabbitMQDefaults.HOST, receiver.getConnection().getHost());
        assertEquals(RabbitMQDefaults.PORT, receiver.getConnection().getPort());
        assertEquals(RabbitMQDefaults.USERNAME, receiver.getConnection().getUserName());
        assertEquals(RabbitMQDefaults.PASSWORD, receiver.getConnection().getPassword());
        assertEquals(RabbitMQDefaults.AUTOMATIC_RECOVERY, receiver.getConnection().isAutomaticRecoveryEnabled());

        TraceyRabbitMQSenderImpl sender = impl.getSender();
        assertEquals(RabbitMQDefaults.HOST, sender.getConnection().getHost());
        assertEquals(RabbitMQDefaults.PORT, sender.getConnection().getPort());
        assertEquals(RabbitMQDefaults.USERNAME, sender.getConnection().getUserName());
        assertEquals(RabbitMQDefaults.PASSWORD, sender.getConnection().getPassword());
        assertEquals(RabbitMQDefaults.AUTOMATIC_RECOVERY, sender.getConnection().isAutomaticRecoveryEnabled());

        RabbitMQRoutingInfo info = RabbitMQRoutingInfo.buildFromConfigFile(f);
        assertEquals(RabbitMQDefaults.EXCHANGE_NAME, info.getExchangeName());
        assertEquals(RabbitMQDefaults.EXCHANGE_TYPE, info.getExchangeType());
        assertEquals(RabbitMQDefaults.DELEIVERY_MODE, info.getDeliveryMode());
        assertEquals(RabbitMQDefaults.ROUTING_KEY, info.getRoutingKey());
        assertEquals(RabbitMQDefaults.HEADERS.size(), info.getHeaders().size());
    }

    @Test
    public void parseVariableExpansion() throws Exception {
        PowerMockito.mockStatic(System.class);
        Mockito.when(System.getenv()).thenReturn(ENV);

        URI path = TestConfigFromFile.class.getResource("broker_expansion.config").toURI();
        File f = new File(path);
        TraceyRabbitMQBrokerImpl impl = new TraceyRabbitMQBrokerImpl(f);

        TraceyRabbitMQReceiverImpl receiver = impl.getReceiver();
        assertEquals(RabbitMQDefaults.HOST, receiver.getConnection().getHost());
        assertEquals(RabbitMQDefaults.PORT, receiver.getConnection().getPort());
        assertEquals("rabbitpw", receiver.getConnection().getPassword());
        assertEquals("rabbituser", receiver.getConnection().getUserName());
        assertEquals(RabbitMQDefaults.AUTOMATIC_RECOVERY, receiver.getConnection().isAutomaticRecoveryEnabled());

        TraceyRabbitMQSenderImpl sender = impl.getSender();
        assertEquals(RabbitMQDefaults.HOST, sender.getConnection().getHost());
        assertEquals(RabbitMQDefaults.PORT, sender.getConnection().getPort());
        assertEquals("rabbitpw", sender.getConnection().getPassword());
        assertEquals("rabbituser", sender.getConnection().getUserName());
        assertEquals(RabbitMQDefaults.AUTOMATIC_RECOVERY, sender.getConnection().isAutomaticRecoveryEnabled());

    }
}
