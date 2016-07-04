/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.praqma.tracey.broker.rabbitmq;

import java.io.File;
import java.net.URI;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import org.junit.Test;

public class TestConfigFromFile {

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
        assertNull(receiver.getPassword());
        assertNull(receiver.getUsername());
        assertEquals("fanout", receiver.getType().toString());
        assertEquals("tracey", receiver.getExchange());

        //Factory defaults
        assertEquals("guest", impl.getReceiver().getFactory().getUsername());
        assertEquals("guest", impl.getReceiver().getFactory().getPassword());
        assertEquals("guest", impl.getSender().getFactory().getUsername());
        assertEquals("guest", impl.getSender().getFactory().getPassword());
    }

    @Test
    public void testVariableExpansion() throws Exception {
        String windowsEnvVar = "%JAVA_HOME%";
        String expected = System.getenv("JAVA_HOME");

        String unixStyled = "$JAVA_HOME";
        String unixStyled2 = "${JAVA_HOME}";

        String expanded = TraceyRabbitMQReceiverBuilder.expand(windowsEnvVar);
        assertEquals(expected, expanded);
        assertEquals(expected, TraceyRabbitMQReceiverBuilder.expand(unixStyled));
        assertEquals(expected, TraceyRabbitMQReceiverBuilder.expand(unixStyled2));

    }
}
