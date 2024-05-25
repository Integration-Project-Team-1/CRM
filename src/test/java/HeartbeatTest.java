/*import crm.Heartbeat;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class HeartbeatTest {

    @Test
    public void testCreateXML() {
        try {
            Heartbeat heartbeat = new Heartbeat();
            String xml = heartbeat.createXML();
            assertNotNull(xml); // Zorg ervoor dat XML niet null is
            assertTrue(xml.contains("<heartbeat>")); // Controleer of het XML-element aanwezig is
            assertTrue(xml.contains("<service>crm</service>")); // Controleer of servicenaam correct is
            assertTrue(xml.contains("<status>")); // Controleer of status aanwezig is
            assertTrue(xml.contains("<error>")); // Controleer of error aanwezig is
        } catch (Exception e) {
            fail("Exception thrown: " + e.getMessage()); // Mislukken als er een uitzondering wordt gegooid
        }
    }

    @Test
    public void testSendHeartbeat() {
        try {
            Heartbeat heartbeat = new Heartbeat();
            heartbeat.sendHeartbeat();
            // Als er geen uitzondering wordt gegooid, wordt de test als geslaagd beschouwd
        } catch (Exception e) {
            fail("Exception thrown: " + e.getMessage()); // Mislukken als er een uitzondering wordt gegooid
        }
    }

    @Test
    public void testIsSalesforceAvailable() {
        try {
            assertTrue(Heartbeat.isSalesforceAvailable()); // Controleer of Salesforce beschikbaar is
        } catch (Exception e) {
            fail("Exception thrown: " + e.getMessage()); // Mislukken als er een uitzondering wordt gegooid
        }
    }
}
*/