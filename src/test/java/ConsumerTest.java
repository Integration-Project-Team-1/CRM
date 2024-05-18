import crm.Business;
import crm.Consumer;
import crm.Consumption;
import crm.Participant;
import org.junit.jupiter.api.Test;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class ConsumerTest {

    @Test
    void testConsumerCreation() {
        // Test of de Consumer zonder fouten kan worden geïnstantieerd
        assertDoesNotThrow(() -> new Consumer());
    }

    @Test
    void testUnmarshalParticipant() throws IOException {
        // Test het unmarshalling van een Participant-object
        Consumer consumer = new Consumer();
        String xml = "<participant><uuid>123</uuid><method>create</method></participant>";

        assertDoesNotThrow(() -> {
            Participant participant = consumer.unmarshalParticipant(xml);
            assertEquals("123", participant.getUuid());
            assertEquals("create", participant.getMethod());
        });
    }


    @Test
    void testUnmarshalConsumption() throws IOException {
        // Test het unmarshalling van een Consumption-object
        Consumer consumer = new Consumer();
        String xml = "<consumption><id>456</id><amount>100</amount></consumption>";

        assertDoesNotThrow(() -> {
            Consumption consumption = consumer.unmarshalConsumption(xml);
            assertEquals(456, consumption.getId());
            assertEquals(100, consumption.getAmount());
        });
    }

    @Test
    void testUnmarshalBusiness() throws IOException {
        // Test het unmarshalling van een Business-object
        Consumer consumer = new Consumer();
        String xml = "<business><name>XYZ Corp</name></business>";

        assertDoesNotThrow(() -> {
            Business business = consumer.unmarshalBusiness(xml);
            assertEquals("XYZ Corp", business.getName());
        });
    }

    @Test
    void testValidateXML() {
        // Test XML-validatie
        String xml = "<participant><uuid>123</uuid><method>create</method></participant>";
        String xsdPath = "src/main/resources/include.template.xsd";

        assertTrue(Consumer.validateXML(xml, xsdPath));
    }

    // Voeg meer tests toe naargelang je functionaliteit uitbreidt of verfijnt

}
