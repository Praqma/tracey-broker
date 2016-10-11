package net.praqma.tracey.broker.impl.rabbitmq;

import java.io.File;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.rule.PowerMockRule;

@PrepareForTest({System.class, RabbitMQConnection.class})
public class TestConfigFromFile {

    static final Map<String,String> ENV = new HashMap<>();
    static {
        ENV.put("RABBITMQ_USER", "rabbituser");
        ENV.put("RABBITMQ_PW", "rabbitpw");
    }

    @Rule
    public final PowerMockRule r = new PowerMockRule();

    @Test
    public void parseSenderConfigFile () throws Exception {
        final URI path = TestConfigFromFile.class.getResource("broker.config").toURI();
        final File f = new File(path);
        final TraceyRabbitMQBrokerImpl impl = new TraceyRabbitMQBrokerImpl(f);

        final TraceyRabbitMQReceiverImpl receiver = impl.getReceiver();
        Assert.assertEquals("some.host.name", receiver.getConnection().getHost());
        Assert.assertEquals("myuser", receiver.getConnection().getUserName());
        Assert.assertEquals("s0m3p4ss", receiver.getConnection().getPassword());
        Assert.assertEquals(true, receiver.getConnection().isAutomaticRecoveryEnabled());
    }

    @Test
    public void parseReceiverConfigFile () throws Exception {
        final URI path = TestConfigFromFile.class.getResource("broker.config").toURI();
        final File f = new File(path);
        final TraceyRabbitMQBrokerImpl impl = new TraceyRabbitMQBrokerImpl(f);

        final TraceyRabbitMQSenderImpl sender = impl.getSender();
        Assert.assertEquals("some.host.name", sender.getConnection().getHost());
        Assert.assertEquals("myuser", sender.getConnection().getUserName());
        Assert.assertEquals("s0m3p4ss", sender.getConnection().getPassword());
        Assert.assertEquals(true, sender.getConnection().isAutomaticRecoveryEnabled());
    }

    @Test
    public void parseRoutingInfoConfigFile () throws Exception {
        final URI path = TestConfigFromFile.class.getResource("broker.config").toURI();
        final File f = new File(path);
        final RabbitMQRoutingInfo info = RabbitMQRoutingInfo.buildFromConfigFile(f);

        Assert.assertEquals("stacie", info.getExchangeName());
        Assert.assertEquals("fanout", info.getExchangeType());
        Assert.assertEquals(1, info.getDeliveryMode());
        Assert.assertEquals("", info.getRoutingKey());
        Assert.assertEquals(2, info.getHeaders().size());
        Assert.assertEquals("someValue", info.getHeaders().get("someKey"));
        Assert.assertEquals(0, info.getHeaders().get("someKey1"));
    }

    @Test
    public void parseEmptyConfigFile() throws Exception {
        final URI path = TestConfigFromFile.class.getResource("broker_empty.config").toURI();
        final File f = new File(path);
        final TraceyRabbitMQBrokerImpl impl = new TraceyRabbitMQBrokerImpl(f);

        final TraceyRabbitMQReceiverImpl receiver = impl.getReceiver();
        Assert.assertEquals(RabbitMQDefaults.HOST, receiver.getConnection().getHost());
        Assert.assertEquals(RabbitMQDefaults.PORT, receiver.getConnection().getPort());
        Assert.assertEquals(RabbitMQDefaults.USERNAME, receiver.getConnection().getUserName());
        Assert.assertEquals(RabbitMQDefaults.PASSWORD, receiver.getConnection().getPassword());
        Assert.assertEquals(RabbitMQDefaults.AUTOMATIC_RECOVERY, receiver.getConnection().isAutomaticRecoveryEnabled());

        final TraceyRabbitMQSenderImpl sender = impl.getSender();
        Assert.assertEquals(RabbitMQDefaults.HOST, sender.getConnection().getHost());
        Assert.assertEquals(RabbitMQDefaults.PORT, sender.getConnection().getPort());
        Assert.assertEquals(RabbitMQDefaults.USERNAME, sender.getConnection().getUserName());
        Assert.assertEquals(RabbitMQDefaults.PASSWORD, sender.getConnection().getPassword());
        Assert.assertEquals(RabbitMQDefaults.AUTOMATIC_RECOVERY, sender.getConnection().isAutomaticRecoveryEnabled());

        final RabbitMQRoutingInfo info = RabbitMQRoutingInfo.buildFromConfigFile(f);
        Assert.assertEquals(RabbitMQDefaults.EXCHANGE_NAME, info.getExchangeName());
        Assert.assertEquals(RabbitMQDefaults.EXCHANGE_TYPE, info.getExchangeType());
        Assert.assertEquals(RabbitMQDefaults.DELEIVERY_MODE, info.getDeliveryMode());
        Assert.assertEquals(RabbitMQDefaults.ROUTING_KEY, info.getRoutingKey());
        Assert.assertEquals(RabbitMQDefaults.HEADERS.size(), info.getHeaders().size());
    }

    @Test
    public void parseVariableExpansion() throws Exception {
        PowerMockito.mockStatic(System.class);
        Mockito.when(System.getenv()).thenReturn(ENV);

        final URI path = TestConfigFromFile.class.getResource("broker_expansion.config").toURI();
        final File f = new File(path);
        final TraceyRabbitMQBrokerImpl impl = new TraceyRabbitMQBrokerImpl(f);

        final TraceyRabbitMQReceiverImpl receiver = impl.getReceiver();
        Assert.assertEquals(RabbitMQDefaults.HOST, receiver.getConnection().getHost());
        Assert.assertEquals(RabbitMQDefaults.PORT, receiver.getConnection().getPort());
        Assert.assertEquals("rabbitpw", receiver.getConnection().getPassword());
        Assert.assertEquals("rabbituser", receiver.getConnection().getUserName());
        Assert.assertEquals(RabbitMQDefaults.AUTOMATIC_RECOVERY, receiver.getConnection().isAutomaticRecoveryEnabled());

        final TraceyRabbitMQSenderImpl sender = impl.getSender();
        Assert.assertEquals(RabbitMQDefaults.HOST, sender.getConnection().getHost());
        Assert.assertEquals(RabbitMQDefaults.PORT, sender.getConnection().getPort());
        Assert.assertEquals("rabbitpw", sender.getConnection().getPassword());
        Assert.assertEquals("rabbituser", sender.getConnection().getUserName());
        Assert.assertEquals(RabbitMQDefaults.AUTOMATIC_RECOVERY, sender.getConnection().isAutomaticRecoveryEnabled());

    }
}
