/*import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import crm.Heartbeat;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;


import static org.junit.jupiter.api.Assertions.*;

public class HeartbeatTest {

    @Test
    void testCreateXML()  {
        Heartbeat heartbeat = null;
        try {
            heartbeat = new Heartbeat();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        heartbeat.setService("crm");
        heartbeat.setStatus("up");
        heartbeat.setError("0");


        try {
            String xml = heartbeat.createXML();
            assertNotNull(xml);
            assertTrue(xml.contains("<heartbeat>"));
            assertTrue(xml.contains("<service>crm</service>"));

            assertTrue(xml.contains("<status>up</status>"));
            assertTrue(xml.contains("<error>0</error>"));
        } catch (Exception e) {
            fail("Exception occurred: " + e.getMessage());
        }
    }




    @Test
    void testSendHeartbeat() throws Exception {
        // Mocking the connection, channel, and connection factory
        ConnectionFactory factory = Mockito.mock(ConnectionFactory.class);
        Connection connection = Mockito.mock(Connection.class);
        com.rabbitmq.client.Channel channel = Mockito.mock(com.rabbitmq.client.Channel.class); // Gebruik de juiste Channel-klasse

        Mockito.when(factory.newConnection()).thenReturn(connection);
        Mockito.when(connection.createChannel()).thenReturn(channel); // Gebruik de juiste Channel-mock

        // Creating an instance of Heartbeat
        Heartbeat heartbeat = new Heartbeat();
        heartbeat.setService("crm");
        heartbeat.setStatus("up");
        heartbeat.setError("0");

        // Capturing System.out.println() output
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream printStream = new PrintStream(outputStream);
        System.setOut(printStream);

        // Calling the method
        heartbeat.sendHeartbeat();

        // Verifying the expected output
        String expectedOutput = "heartbeat was not sent due to error";
        assertTrue(outputStream.toString().contains(expectedOutput), "Expected output not found");
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

    // You can add more test cases for other methods if needed
}
*/