import crm.Heartbeat;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class HeartbeatTest {

    @Test
    public void testCreateXML() {
        try {
            Heartbeat heartbeat = new Heartbeat();
            String xml = heartbeat.createXML();
            assertNotNull(xml, "XML should not be null");
            assertTrue(xml.contains("<heartbeat>"), "XML should contain <heartbeat> element");
            assertTrue(xml.contains("<service>crm</service>"), "XML should contain correct service name");
            assertTrue(xml.contains("<status>"), "XML should contain status");
            assertTrue(xml.contains("<error>"), "XML should contain error");
        } catch (Exception e) {
            fail("Exception thrown: " + e.getMessage());
        }
    }

    @Test
    public void testSendHeartbeat() {
        try {
            Heartbeat heartbeat = new Heartbeat();
            heartbeat.sendHeartbeat();
        } catch (Exception e) {
            fail("Exception thrown: " + e.getMessage());
        }
    }

    @Test
    public void testIsSalesforceAvailable() {
        try {
            assertTrue(Heartbeat.isSalesforceAvailable(), "Salesforce should be available");
        } catch (Exception e) {
            fail("Exception thrown: " + e.getMessage());
        }
    }
}
