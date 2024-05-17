

import crm.Consumer;
import crm.Participant;
import crm.Business;
import crm.Consumption;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import jakarta.xml.bind.JAXBException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mockito.Mockito;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ConsumerTest {

    private Consumer consumer;
    private Channel channelMock;

    @BeforeEach
    void setUp() throws IOException, TimeoutException {
        // Mocking environment variables
        System.setProperty("CONSUMING_QUEUE", "testQueue");
        System.setProperty("EXCHANGE", "testExchange");
        System.setProperty("ROUTINGKEY_USER", "testUser");
        System.setProperty("DEV_HOST", "localhost");
        System.setProperty("RABBITMQ_USERNAME", "guest");
        System.setProperty("RABBITMQ_PASSWORD", "guest");
        System.setProperty("RABBITMQ_PORT", "5672");

        // Mocking RabbitMQ channel and connection
        channelMock = mock(Channel.class);
        Connection connectionMock = mock(Connection.class);
        when(connectionMock.createChannel()).thenReturn(channelMock);
        ConnectionFactory factoryMock = mock(ConnectionFactory.class);
        when(factoryMock.newConnection()).thenReturn(connectionMock);

        consumer = Mockito.spy(new Consumer());
        doReturn(factoryMock).when(consumer).getConnectionFactory();
    }

    @Test
    void testStartConsuming() throws IOException {
        // Verify if the channel and queue are properly set up
        consumer.startConsuming();
        verify(channelMock).queueDeclare("testQueue", false, false, false, null);
        verify(channelMock).queueBind("testQueue", "testExchange", "testUser");
        verify(channelMock).basicConsume(anyString(), eq(true), any());
    }

    @Test
    void testUnmarshalParticipant() throws JAXBException {
        String xml = "<participant><method>create</method><uuid>12345</uuid></participant>";
        Participant participant = consumer.unmarshalParticipant(xml);
        assertNotNull(participant);
        assertEquals("create", participant.getMethod());
        assertEquals("12345", participant.getUuid());
    }

    @Test
    void testUnmarshalConsumption() throws JAXBException {
        String xml = "<consumption><id>123</id></consumption>";
        Consumption consumption = consumer.unmarshalConsumption(xml);
        assertNotNull(consumption);
        assertEquals("123", consumption.getUuid());
    }

    @Test
    void testUnmarshalBusiness() throws JAXBException {
        String xml = "<business><access_code>ABC123</access_code></business>";
        Business business = consumer.unmarshalBusiness(xml);
        assertNotNull(business);
        assertEquals("ABC123", business.getAccessCode());
    }

    @Test
    void testValidateXML() throws IOException,SAXException  {
        String xml = "<participant><method>create</method><uuid>12345</uuid></participant>";
        String xsdPath = "src/test/resources/participant.xsd"; // Ensure you have this file for the test

        boolean isValid = Consumer.validateXML(xml, xsdPath);
        assertTrue(isValid);
    }

    @Test
    void testValidateXML_Invalid() {
        String xml = "<participant><method>create</method><uuid>12345</uuid>"; // Malformed XML
        String xsdPath = "src/test/resources/participant.xsd"; // Ensure you have this file for the test

        boolean isValid = Consumer.validateXML(xml, xsdPath);
        assertFalse(isValid);
    }
}
