import crm.Heartbeat;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class HeartbeatTest {

    @Test
    void testCreateXML() throws Exception {
        Heartbeat heartbeat = new Heartbeat();
        heartbeat.setService("crm");
        heartbeat.setStatus("up");
        heartbeat.setError("0");
        heartbeat.setTimestamp(123456789);

        try {
            String xml = heartbeat.createXML();
            assertNotNull(xml);
            assertTrue(xml.contains("<heartbeat>"));
            assertTrue(xml.contains("<service>crm</service>"));
            assertTrue(xml.contains("<timestamp>123456789</timestamp>"));
            assertTrue(xml.contains("<status>up</status>"));
            assertTrue(xml.contains("<error>0</error>"));
        } catch (Exception e) {
            fail("Exception occurred: " + e.getMessage());
        }
    }


    @Test
    void testIsSalesforceAvailable() {
        try {
            boolean result = Heartbeat.isSalesforceAvailable();
            assertTrue(result);
        } catch (Exception e) {
            fail("Exception occurred: " + e.getMessage());
        }
    }


}
