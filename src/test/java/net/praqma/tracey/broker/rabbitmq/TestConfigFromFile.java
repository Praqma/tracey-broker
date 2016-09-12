package net.praqma.tracey.broker.rabbitmq;

import java.io.File;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import static org.junit.Assert.assertEquals;
import org.junit.Rule;
import org.junit.Test;
//import org.mockito.Mockito;
//import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.rule.PowerMockRule;

@PrepareForTest({System.class})
public class TestConfigFromFile {

    @Rule
    public PowerMockRule r = new PowerMockRule();

    static final Map<String,String> ENV = new HashMap<>();

    static {
        ENV.put("RABBITMQ_USER", "rabbituser");
        ENV.put("RABBITMQ_PW", "rabbitpw");
    }

    /**
     * Example config file. Test for basic stuff. We'll fail gloriously if the file is not there
     *
     * broker {
            rabbitmq {
                host = 'some.host.name'
                password = 's0m3p4ss'
                exchange = 'tracey'
            }
        }
     * @throws Exception
     */
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
        // TODO: fix headers
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
    }

    // TODO: fix me
    /*@Test
    public void parseVariableExpansion() throws Exception {
        PowerMockito.mockStatic(System.class);
        Mockito.when(System.getenv()).thenReturn(ENV);

        URI path = TestConfigFromFile.class.getResource("broker_expansion.config").toURI();
        File f = new File(path);
        TraceyRabbitMQBrokerImpl impl = new TraceyRabbitMQBrokerImpl(f);

        TraceyRabbitMQReceiverImpl receiver = impl.getReceiver();
        assertEquals("localhost", receiver.getConnection().getHost());
        assertEquals("rabbitpw", receiver.getConnection().getPassword());
        assertEquals("guest", receiver.getConnection().getUserName());

        //Factory defaults
        assertEquals("guest", impl.getReceiver().getConnection().getUserName());
        assertEquals("rabbitpw", impl.getReceiver().getConnection().getPassword());
        assertEquals("guest", impl.getSender().getConnection().getUserName());
        assertEquals("rabbitpw", impl.getSender().getConnection().getPassword());
    }*/

    // TODO: Fix me
    /*@Test
    public void testVariableExpansion() throws Exception {

        PowerMockito.mockStatic(System.class);
        Mockito.when(System.getenv()).thenReturn(ENV);

        String windowsEnvVar = "%RABBITMQ_PW%";

        String unixStyled = "$RABBITMQ_PW";
        String unixStyled2 = "${RABBITMQ_PW}";

        String expanded = TraceyRabbitMQReceiverBuilder.expand(windowsEnvVar);
        assertEquals("rabbitpw", expanded);
        assertEquals("rabbitpw", TraceyRabbitMQReceiverBuilder.expand(unixStyled));
        assertEquals("rabbitpw", TraceyRabbitMQReceiverBuilder.expand(unixStyled2));

    }*/
}
