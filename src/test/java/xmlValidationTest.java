

import crm.xmlValidation;
import org.junit.Test;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

public class xmlValidationTest {

    // Sample XML strings for testing
    private static final String VALID_XML = "<?xml version=\"1.0\"?>"
            + "<heartbeat xmlns=\"http://ehb.local\">"
            + "<service>crm</service>"
            + "<timestamp>1625247600</timestamp>"
            + "<status>Active</status>"
            + "<error></error>"
            + "</heartbeat>";

    private static final String INVALID_XML = "<?xml version=\"1.0\"?>"
            + "<heartbeat xmlns=\"http://ehb.local\">"
            + "<service>crm</service>"
            + "<timestamp>InvalidTimestamp</timestamp>"  // Invalid timestamp format
            + "<status>Active</status>"
            + "<error></error>"
            + "</heartbeat>";

    @Test
    public void testValidXML() {
        // Test with valid XML
        boolean result = xmlValidation.validateXML(VALID_XML);
        System.out.println("Validation result for valid XML: " + result); // Debug
        assertTrue("The XML should be valid", result);
    }

    @Test
    public void testInvalidXML() {
        // Test with invalid XML
        boolean result = xmlValidation.validateXML(INVALID_XML);
        System.out.println("Validation result for invalid XML: " + result);  // Debug
        assertFalse("The XML should be invalid", result);
    }
}
