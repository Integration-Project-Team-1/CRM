//import crm.Consumer;
//import crm.Salesforce;
//import org.junit.jupiter.api.Test;
//
//import javax.xml.bind.JAXBException;
//import java.io.IOException;
//
//import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
//import static org.mockito.Mockito.*;
//
//public class ConsumerTest {
//
//    @Test
//    public void testHandleDelivery_ParticipantCreation() throws IOException, JAXBException {
//        // Mocks aanmaken
//        Salesforce mockSalesforce = mock(Salesforce.class);
//        Consumer consumer = new Consumer();
//        consumer.salesforce = mockSalesforce;
//
//        // Simuleer een bericht voor het maken van een deelnemer
//        String participantMessage = "<participant><method>create</method><uuid>123</uuid><name>Test Participant</name></participant>";
//
//        // Simuleer het aanroepen van handleDelivery
//        assertDoesNotThrow(() -> consumer.handleDelivery("tag", null, null, participantMessage.getBytes()));
//
//        // Controleer of de juiste methoden van Salesforce zijn aangeroepen
//        verify(mockSalesforce).createDeelnemer(any());
//    }
//
//    @Test
//    public void testHandleDelivery_ParticipantUpdate() throws IOException, JAXBException {
//        // Mocks aanmaken
//        Salesforce mockSalesforce = mock(Salesforce.class);
//        Consumer consumer = new Consumer();
//        consumer.salesforce = mockSalesforce;
//
//        // Simuleer een bericht voor het bijwerken van een deelnemer
//        String participantMessage = "<participant><method>update</method><uuid>123</uuid><name>Updated Participant</name></participant>";
//
//        // Simuleer het aanroepen van handleDelivery
//        assertDoesNotThrow(() -> consumer.handleDelivery("tag", null, null, participantMessage.getBytes()));
//
//        // Controleer of de juiste methoden van Salesforce zijn aangeroepen
//        verify(mockSalesforce).updateDeelnemer(any(), any());
//    }
//
//    @Test
//    public void testHandleDelivery_ParticipantDeletion() throws IOException, JAXBException {
//        // Mocks aanmaken
//        Salesforce mockSalesforce = mock(Salesforce.class);
//        Consumer consumer = new Consumer();
//        consumer.salesforce = mockSalesforce;
//
//        // Simuleer een bericht voor het verwijderen van een deelnemer
//        String participantMessage = "<participant><method>delete</method><uuid>123</uuid></participant>";
//
//        // Simuleer het aanroepen van handleDelivery
//        assertDoesNotThrow(() -> consumer.handleDelivery("tag", null, null, participantMessage.getBytes()));
//
//        // Controleer of de juiste methoden van Salesforce zijn aangeroepen
//        verify(mockSalesforce).deleteDeelnemer(any());
//    }
//}