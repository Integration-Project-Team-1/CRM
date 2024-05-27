import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import crm.*;
import java.io.IOException;
import com.rabbitmq.client.Channel;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class ConsumerTest {

    @Mock
    private Salesforce salesforce;

    @Mock
    private Channel channel;

    private Consumer consumer;

    @BeforeEach
    public void setUp() throws IOException {
        MockitoAnnotations.openMocks(this);
        consumer = new Consumer();
        consumer.setSalesforce(salesforce); // assuming setter method for salesforce
        consumer.setChannel(channel); // assuming setter method for channel
    }

    @Test
    public void testUnmarshalParticipant() throws Exception {
        // Arrange
        String participantXml = "<participant><method>create</method><uuid>123</uuid><name>John Doe</name></participant>";

        // Act
        Participant participant = consumer.unmarshalParticipant(participantXml);

        // Assert
        assertNotNull(participant);
        assertEquals("123", participant.getUuid());
        assertEquals("John Doe", participant.getLastname());
        assertEquals("create", participant.getMethod());
    }

    @Test
    public void testUnmarshalConsumption() throws Exception {
        // Arrange
        String consumptionXml = "<consumption><method>create</method><uuid>789</uuid><amount>10.99</amount></consumption>";

        // Act
        Consumption consumption = consumer.unmarshalConsumption(consumptionXml);

        // Assert
        assertNotNull(consumption);
        assertEquals("789", consumption.getUuid());
        assertEquals(10.99, consumption.getProducts());
        assertEquals("create", consumption.getId());
    }

    @Test
    public void testUnmarshalBusiness() throws Exception {
        // Arrange
        String businessXml = "<business><method>create</method><uuid>456</uuid><name>Acme Inc.</name></business>";

        // Act
        Business business = consumer.unmarshalBusiness(businessXml);

        // Assert
        assertNotNull(business);
        assertEquals("456", business.getUuid());
        assertEquals("Acme Inc.", business.getName());
        assertEquals("create", business.getMethod());
    }
}
