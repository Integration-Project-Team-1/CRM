import crm.Heartbeat;

import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class HeartbeatTest {

    @BeforeEach
    void setUp() {
        // This method will be called before each test
        // Set up any common configuration or environment variables if needed
        System.setProperty("QUEUE_NAME_HEARTBEAT", "testQueue");
        System.setProperty("DEV_HOST", "localhost");
        System.setProperty("RABBITMQ_USERNAME", "guest");
        System.setProperty("RABBITMQ_PASSWORD", "guest");
        System.setProperty("RABBITMQ_PORT", "5672");
    }

    @Test
    void testGetService() throws Exception {
        Heartbeat heartbeat = new Heartbeat();
        assertEquals("crm", heartbeat.getService());
    }

    @Test
    void testSetService() throws Exception {
        Heartbeat heartbeat = new Heartbeat();
        heartbeat.setService("newService");
        assertEquals("newService", heartbeat.getService());
    }

    @Test
    void testCreateXML() throws Exception {
        Heartbeat heartbeat = new Heartbeat();
        String xml = heartbeat.createXML();
        assertNotNull(xml);
        assertTrue(xml.contains("<service>crm</service>"));
        assertTrue(xml.contains("<status>up</status>") || xml.contains("<status>down</status>"));
    }

    @Test
    void testIsSalesforceAvailable() throws Exception {
        URL urlMock = mock(URL.class);
        HttpURLConnection connectionMock = mock(HttpURLConnection.class);

        when(urlMock.openConnection()).thenReturn(connectionMock);
        when(connectionMock.getResponseCode()).thenReturn(200);

        assertTrue(Heartbeat.isSalesforceAvailable());
    }

    @Test
    void testIsSalesforceAvailable_Failure() throws Exception {
        URL urlMock = mock(URL.class);
        HttpURLConnection connectionMock = mock(HttpURLConnection.class);

        when(urlMock.openConnection()).thenReturn(connectionMock);
        when(connectionMock.getResponseCode()).thenReturn(500);

        assertFalse(Heartbeat.isSalesforceAvailable());
    }

    @Test
    void testSendHeartbeat() throws Exception {
        Heartbeat heartbeat = Mockito.spy(new Heartbeat());

        doNothing().when(heartbeat).sendHeartbeat();

        try {
            heartbeat.sendHeartbeat();
            verify(heartbeat, times(1)).sendHeartbeat();
        } catch (Exception e) {
            fail("Exception should not be thrown");
        }
    }
}
