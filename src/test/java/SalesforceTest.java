import static org.mockito.Mockito.*;

import com.force.api.ApiSession;
import com.force.api.ForceApi;
import crm.*;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

public class SalesforceTest {

    @Test
    public void testConnectToSalesforce() {
        // Mock Salesforce object
        Salesforce salesforce = mock(Salesforce.class);

        // Call method to be tested
        assertDoesNotThrow(() -> {
            salesforce.connectToSalesforce();
        }, "Failed to connect to Salesforce");

        // Verify that the method was called
        verify(salesforce).connectToSalesforce();
    }
    @Test
    public void testCreateDeelnemer() {
        // Mock Participant object
        Participant participant = mock(Participant.class);
        // Mock Salesforce object
        Salesforce salesforce = mock(Salesforce.class);

        // Configure behavior of participant mock
        when(participant.getFirstname()).thenReturn("John");
        when(participant.getLastname()).thenReturn("Doe");
        when(participant.getEmail()).thenReturn("john.doe@example.com");
        when(participant.getPhone()).thenReturn("123456789");
        when(participant.getBusiness()).thenReturn("ABC Company");

        when(participant.getUuid()).thenReturn("ca5378c7-c079-4d62-b4e1-de8cb4004eee");
        when(participant.getFromBusiness()).thenReturn(String.valueOf(true));

        // Call method to be tested
        assertDoesNotThrow(() -> {
            salesforce.createDeelnemer(participant);
        }, "Failed to create deelnemer");

        // Verify that the method was called with the correct arguments
        verify(salesforce).createDeelnemer(participant);
    }

    @Test
    public void testUpdateDeelnemer() {
        // Mock Participant object
        Participant updatedParticipant = mock(Participant.class);
        // Mock Salesforce object
        Salesforce salesforce = mock(Salesforce.class);

        // Configure behavior of updatedParticipant mock
        when(updatedParticipant.getFirstname()).thenReturn("John");
        when(updatedParticipant.getLastname()).thenReturn("Doe");
        when(updatedParticipant.getEmail()).thenReturn("john.doe@example.com");
        when(updatedParticipant.getPhone()).thenReturn("123456789");
        when(updatedParticipant.getBusiness()).thenReturn("ABC Company");

        when(updatedParticipant.getUuid()).thenReturn("ca5378c7-c079-4d62-b4e1-de8cb4004eee");
        when(updatedParticipant.getFromBusiness()).thenReturn(String.valueOf(true));

        // Call method to be tested
        assertDoesNotThrow(() -> {
            salesforce.updateDeelnemer("ca5378c7-c079-4d62-b4e1-de8cb4004eee", updatedParticipant);
        }, "Failed to update deelnemer");

        // Verify that the method was called with the correct arguments
        verify(salesforce).updateDeelnemer("ca5378c7-c079-4d62-b4e1-de8cb4004eee", updatedParticipant);
    }

    @Test
    public void testCreateBusiness() {
        // Mock Business object
        Business business = mock(Business.class);
        // Mock Salesforce object
        Salesforce salesforce = mock(Salesforce.class);

        // Configure behavior of business mock
        when(business.getName()).thenReturn("ABC Company");
        when(business.getVat()).thenReturn("123456789");
        when(business.getEmail()).thenReturn("info@abc.com");
        when(business.getAccessCode()).thenReturn("abc123");
        when(business.getAddress()).thenReturn("123 Main St");
        when(business.getUuid()).thenReturn("12345678-abcd-1234-efgh-1234567890ab");

        // Call method to be tested
        assertDoesNotThrow(() -> {
            salesforce.createBusiness(business);
        }, "Failed to create business");

        // Verify that the method was called with the correct arguments
        verify(salesforce).createBusiness(business);
    }

    @Test
    public void testCreateConsumption() {
        // Mock Consumption object
        Consumption consumption = mock(Consumption.class);
        // Mock Salesforce object
        Salesforce salesforce = mock(Salesforce.class);

        // Configure behavior of consumption mock
        when(consumption.getProducts()).thenReturn(Collections.singletonList("food"));
        when(consumption.getUuid()).thenReturn("12345678-abcd-1234-efgh-1234567890ab");

        // Call method to be tested
        assertDoesNotThrow(() -> {
            salesforce.createConsumption(consumption);
        }, "Failed to create consumption");

        // Verify that the method was called with the correct arguments
        verify(salesforce).createConsumption(consumption);
    }


}
