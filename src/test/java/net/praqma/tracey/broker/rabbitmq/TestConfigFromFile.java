/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.praqma.tracey.broker.rabbitmq;

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

@PrepareForTest({System.class, TraceyRabbitMQReceiverBuilder.class})
public class TestConfigFromFile {

    @Rule
    public PowerMockRule r = new PowerMockRule();

    static final Map<String,String> ENV = new HashMap<String, String>();

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
    public void parseFileConfigureReceiver () throws Exception {
        URI path = TestConfigFromFile.class.getResource("broker.config").toURI();
        File f = new File(path);
        TraceyRabbitMQBrokerImpl impl = new TraceyRabbitMQBrokerImpl(f);

        TraceyRabbitMQReceiverImpl receiver = impl.getReceiver();
        assertEquals("some.host.name", receiver.getHost());
        assertEquals("s0m3p4ss", receiver.getPassword());
        assertEquals("fanout", receiver.getType().toString());
        assertEquals("stacie", receiver.getExchange());
        assertEquals("myuser", receiver.getUsername());

        //Factory
        assertEquals("myuser", impl.getReceiver().getFactory().getUsername());
        assertEquals("s0m3p4ss", impl.getReceiver().getFactory().getPassword());
        assertEquals("myuser", impl.getSender().getFactory().getUsername());
        assertEquals("s0m3p4ss", impl.getSender().getFactory().getPassword());

    }

    @Test
    public void parseEmptyConfigFile() throws Exception {
        URI path = TestConfigFromFile.class.getResource("broker_empty.config").toURI();
        File f = new File(path);
        TraceyRabbitMQBrokerImpl impl = new TraceyRabbitMQBrokerImpl(f);

        TraceyRabbitMQReceiverImpl receiver = impl.getReceiver();
        assertEquals("localhost", receiver.getHost());
        assertEquals("guest", receiver.getPassword());
        assertEquals("guest", receiver.getUsername());
        assertEquals("fanout", receiver.getType().toString());
        assertEquals("tracey", receiver.getExchange());

        //Factory defaults
        assertEquals("guest", impl.getReceiver().getFactory().getUsername());
        assertEquals("guest", impl.getReceiver().getFactory().getPassword());
        assertEquals("guest", impl.getSender().getFactory().getUsername());
        assertEquals("guest", impl.getSender().getFactory().getPassword());
    }

    @Test
    public void parseVariableExpansion() throws Exception {
        PowerMockito.mockStatic(System.class);
        Mockito.when(System.getenv()).thenReturn(ENV);
        Mockito.when(System.getenv(Mockito.eq("RABBITMQ_USER"))).thenReturn("rabbituser");
        Mockito.when(System.getenv(Mockito.eq("RABBITMQ_PW"))).thenReturn("rabbitpw");

        URI path = TestConfigFromFile.class.getResource("broker_expansion.config").toURI();
        File f = new File(path);
        TraceyRabbitMQBrokerImpl impl = new TraceyRabbitMQBrokerImpl(f);


        TraceyRabbitMQReceiverImpl receiver = impl.getReceiver();
        assertEquals("localhost", receiver.getHost());
        assertEquals(System.getenv("RABBITMQ_PW"), receiver.getPassword());
        assertEquals("guest", receiver.getUsername());
        assertEquals("fanout", receiver.getType().toString());
        assertEquals("tracey", receiver.getExchange());

        //Factory defaults
        assertEquals("guest", impl.getReceiver().getFactory().getUsername());
        assertEquals(System.getenv("RABBITMQ_PW"), impl.getReceiver().getFactory().getPassword());
        assertEquals("guest", impl.getSender().getFactory().getUsername());
        assertEquals(System.getenv("RABBITMQ_PW"), impl.getSender().getFactory().getPassword());
    }

    @Test
    public void testVariableExpansion() throws Exception {

        PowerMockito.mockStatic(System.class);
        Mockito.when(System.getenv()).thenReturn(ENV);
        Mockito.when(System.getenv(Mockito.eq("RABBITMQ_USER"))).thenReturn("rabbituser");
        Mockito.when(System.getenv(Mockito.eq("RABBITMQ_PW"))).thenReturn("rabbitpw");

        String windowsEnvVar = "%RABBITMQ_PW%";
        String expected = System.getenv("RABBITMQ_PW");

        String unixStyled = "$RABBITMQ_PW";
        String unixStyled2 = "${RABBITMQ_PW}";

        String expanded = TraceyRabbitMQReceiverBuilder.expand(windowsEnvVar);
        assertEquals(expected, expanded);
        assertEquals(expected, TraceyRabbitMQReceiverBuilder.expand(unixStyled));
        assertEquals(expected, TraceyRabbitMQReceiverBuilder.expand(unixStyled2));

    }
}
