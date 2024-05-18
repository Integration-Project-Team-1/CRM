import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.xml.sax.SAXException;
import crm.Business;
import crm.Consumer;
import crm.Participant;
import crm.Consumption;
import java.io.IOException;

public class ConsumerTest {

    @Test
    public void testUnmarshalParticipant() throws IOException {
        String xml = "<participant xmlns=\"http://ehb.local\"><uuid>123</uuid><method>create</method></participant>";
        Consumer consumer = new Consumer();
        try {
            Participant participant = consumer.unmarshalParticipant(xml);
            assertNotNull(participant);
            assertEquals("123", participant.getUuid());
            assertEquals("create", participant.getMethod());
        } catch (Exception e) {
            fail("Exception should not be thrown: " + e.getMessage());
        }
    }

    @Test
    public void testUnmarshalConsumption() throws IOException {
        String xml = "<consumption><id>456</id></consumption>";
        Consumer consumer = new Consumer();
        try {
            Consumption consumption = consumer.unmarshalConsumption(xml);
            assertNotNull(consumption);
            assertEquals("456", consumption.getId());
        } catch (Exception e) {
            fail("Exception should not be thrown: " + e.getMessage());
        }
    }

    @Test
    public void testUnmarshalBusiness() throws IOException {
        String xml = "<business xmlns=\"http://ehb.local\"><uuid>789</uuid><method>update</method></business>";
        Consumer consumer = new Consumer();
        try {
            Business business = consumer.unmarshalBusiness(xml);
            assertNotNull(business);
            assertEquals("789", business.getUuid());
            assertEquals("update", business.getMethod());
        } catch (Exception e) {
            fail("Exception should not be thrown: " + e.getMessage());
        }
    }

    @Test
    public void testValidateXML() {
        String validXml = "<participant xmlns=\"http://ehb.local\"><uuid>123</uuid><method>create</method></participant>";
        String invalidXml = "<participant><uuid>123</uuid>"; // missing closing tag
        String xsdPath = "src/main/resources/include.template.xsd";
        assertTrue(Consumer.validateXML(validXml, xsdPath));
        assertFalse(Consumer.validateXML(invalidXml, xsdPath));
    }
}
